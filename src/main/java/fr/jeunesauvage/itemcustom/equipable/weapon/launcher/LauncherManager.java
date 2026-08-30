package fr.jeunesauvage.itemcustom.equipable.weapon.launcher;

import java.util.Collections;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.spell.SpellRegistry;
import net.kyori.adventure.text.Component;

public class LauncherManager implements Listener {
	private static final float			FORCENEED_DEFAULT = 2f;
	private static final NamespacedKey	KEY_SAVEITEM = new NamespacedKey(RpgCraft.name(), "saveitem");
	private static final int			SLOT_SAVEITEM = 35;

	// use staff or spellbook
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onRightClickStaff(PlayerInteractEvent e) {
	    if (!e.getAction().isRightClick()) return;
	    Player			p = e.getPlayer();
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (launcher == null) return;
	    ItemStack	item = e.getItem();
	    if (item == null) return;
		Weapon		weapon = RpgCraft.getItemCustomRegistry().getWeapon(item);
		if (weapon == null || (weapon.getType() != WeaponType.STAFF && weapon.getType() != WeaponType.SPELLBOOK)) return;
		// launch spellbook
		if (weapon.getType() == WeaponType.SPELLBOOK) {
			CrossbowMeta	meta = (CrossbowMeta)item.getItemMeta();
			if (meta.hasChargedProjectiles()) {
				RpgCraft.getSpellRegistry().launchSpellBook(launcher, item);
				CrossbowMeta	updatedMeta = (CrossbowMeta)item.getItemMeta();
				updatedMeta.setChargedProjectiles(Collections.emptyList());
				item.setItemMeta(updatedMeta);
				e.setCancelled(true);
				return;
			}
		}
		// charge + shoot for launchers
		allowVanillaCharge(launcher);
		charge(launcher, item);
	}

	// allow charge vanilla from bow and crossbow
	private void allowVanillaCharge(PlayerCustom launcher) {
		PlayerInventory	inv = launcher.getInventory();
		if (hasArrow(inv))
			replaceArrow(launcher);
		else {
			final int	slot = findSlot(inv);
			if (slot == -1) {
				String	base64 = Data.toBase64(inv.getItem(SLOT_SAVEITEM));
				Data.setString(launcher.getLivingEntity().getPersistentDataContainer(), KEY_SAVEITEM, base64);
				inv.setItem(SLOT_SAVEITEM, new ItemStack(Material.ARROW));
			}
			else
				inv.setItem(slot, new ItemStack(Material.ARROW));
			Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
				if (slot == -1) {
					ItemStack	itemSaved = null;
					String		base64 = Data.getString(launcher.getLivingEntity().getPersistentDataContainer(), KEY_SAVEITEM);
					if (base64 != null) {
						Data.remove(launcher.getLivingEntity().getPersistentDataContainer(), KEY_SAVEITEM);
						itemSaved = Data.fromBase64(base64);
					}
					inv.setItem(SLOT_SAVEITEM, itemSaved);
				}
				else
					inv.setItem(slot, null);
			});
		}
	}

	// reset item saved
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player			p = e.getPlayer();
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (launcher == null) return;
		giveBackArrow(launcher);
		PersistentDataContainer	pdc = p.getPersistentDataContainer();
		String					base64 = Data.getString(pdc, KEY_SAVEITEM);
		if (base64 == null) return;
		ItemStack	item = Data.fromBase64(base64);
		Data.remove(pdc, KEY_SAVEITEM);
		launcher.getInventory().setItem(SLOT_SAVEITEM, item);
	}

	// create custom charge
	private void charge(PlayerCustom launcher, ItemStack item) {
		World	world	= launcher.getWorld();
		if (world == null) return;
		new BukkitRunnable() {
		    float	ticks = 0f;
			boolean	sound = false;
			float	forceNeed = Math.max(FORCENEED_DEFAULT - ((float)StatSecondary.CAST_SPEED.getAmount(launcher)), 0.5f);
		    @Override
		    public void run() {
	    		float	force = ticks / 20;
				if (!sound && force >= forceNeed) {
					sound = true;
					world.playSound(launcher.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				}
		    	if (!launcher.isPresent()) {
					giveBackArrow(launcher);
		    	    cancel();
					return;
		    	}
		    	if (launcher.isHandRaised()) {
					ticks++;
					return;
				}
				// launch
				if (force >= forceNeed) {
					// staff
					if (item.getType() == Material.BOW) {
						RpgCraft.getSpellRegistry().launchStaff(launcher, item);
					}
					// spellbook
					else if (item.getType() == Material.CROSSBOW) {
						CrossbowMeta	metaSpellBook = (CrossbowMeta)item.getItemMeta();
						ItemStack		projectile = new ItemStack(Material.ARROW);
						ItemMeta		metaProjectile = projectile.getItemMeta();
						metaProjectile.displayName(Message.c(Component.translatable("item.rpgcraft.spellbook_projectile")));
						projectile.setItemMeta(metaProjectile);
						metaSpellBook.addChargedProjectile(projectile);
						item.setItemMeta(metaSpellBook);
					}
				}
				giveBackArrow(launcher);
		        cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 1L);
	}

	// replace arrow in inventory
	private void replaceArrow(PlayerCustom launcher) {
		Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
			PlayerInventory	inv = launcher.getInventory();
			ItemStack[]		contents = inv.getContents();
    		for (int i = 0; i < contents.length; i++) {
				ItemStack	item = contents[i];
    		    if (!isArrow(item)) continue;
				String		base64 = Data.toBase64(item);
				ItemStack	barrier = new ItemStack(Material.BARRIER);
				ItemMeta	meta = barrier.getItemMeta();
				Data.setString(meta.getPersistentDataContainer(), KEY_SAVEITEM, base64);
				barrier.setItemMeta(meta);
				inv.setItem(i, barrier);
			}
		});
	}

	// give back arrow in inventory
	private void giveBackArrow(PlayerCustom launcher) {
		PlayerInventory	inv = launcher.getInventory();
		ItemStack[]		contents = inv.getContents();
    	for (int i = 0; i < contents.length; i++) {
			ItemStack	barrier = contents[i];
    	    if (!isBarrier(barrier)) continue;
			ItemMeta				meta = barrier.getItemMeta();
			PersistentDataContainer	pdc = meta.getPersistentDataContainer();
			String					base64 = Data.getString(pdc, KEY_SAVEITEM);
			if (base64 != null) {
				ItemStack	arrow = Data.fromBase64(base64);
				inv.setItem(i, arrow);
			}
			else
				inv.setItem(i, null);
		}
	}

	private boolean hasArrow(PlayerInventory inventory) {
    	for (ItemStack item: inventory.getContents()) {
    	    if (isArrow(item)) return true;
    	}
    	return isArrow(inventory.getItemInOffHand());
	}

	private boolean isArrow(ItemStack item) {
	    if (item == null) return false;
	    Material	material = item.getType();
	    return material == Material.ARROW || material == Material.SPECTRAL_ARROW || material == Material.TIPPED_ARROW;
	}

	private boolean isBarrier(ItemStack item) {
	    if (item == null || item.getType() != Material.BARRIER) return false;
		return Data.hasString(item.getPersistentDataContainer(), KEY_SAVEITEM);
	}

	private int findSlot(PlayerInventory inventory) {
		ItemStack[]	contents = inventory.getStorageContents();
    	for (int i = 9; i < contents.length; i++) {
    	    if (contents[i] == null || contents[i].getType().isAir())
				return i;
    	}
		return -1;
	}

	// dragonfireball hit
	@EventHandler
	public void onDragonFireballHit(EnderDragonFireballHitEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) return;
		e.getAreaEffectCloud().setDuration(40);
	}

	// dragonfireball area hit
	@EventHandler(priority = EventPriority.LOW)
	public void onDragonFireballArea(AreaEffectCloudApplyEvent e) {
		if (!(e.getEntity().getSource() instanceof LivingEntity l)) return;
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		LivingEntityCustom		launcher = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		if (launcher == null) return;
		e.setCancelled(true);
		for (LivingEntity le: e.getAffectedEntities()) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(le.getUniqueId());
			if (target == null || target.isGrouped(launcher)) continue;
			target.damage(1, CombatDamage.MAGIC, launcher);
		}
	}

	// use staffs for all livingentitycustom
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		LivingEntity		l = e.getEntity();
		LivingEntityCustom	launcher = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
		if (launcher == null) return;
		ItemStack		item = e.getBow();
		if (item == null) return;
		Weapon	weapon = RpgCraft.getItemCustomRegistry().getWeapon(item);
		if (weapon == null) return;
		Entity	projectile = e.getProjectile();
		switch (weapon.getType()) {
			case BOW -> RpgCraft.getSpellRegistry().setBow(projectile);
			case CROSSBOW -> RpgCraft.getSpellRegistry().setCrossBow(projectile);
			case STAFF -> {
				e.setCancelled(true);
				if (launcher instanceof PlayerCustom playerCustom) RpgCraft.getSpellRegistry().launchStaff(playerCustom, item);
				else RpgCraft.getSpellRegistry().launchStaff(launcher, launcher.getTarget(), item);
			}
			case SPELLBOOK -> {
				e.setCancelled(true);
				RpgCraft.getSpellRegistry().launchSpellBook(launcher, item);
			}
			default -> {}
		}
	}

	// cancel destruction on explosion
	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		e.blockList().clear();
		if (!(e.getEntity() instanceof Projectile projectile)) return;
		SpellRegistry	spellRegistry = RpgCraft.getSpellRegistry();
	    if (spellRegistry.isFireball(projectile) || spellRegistry.isShadowWord(projectile))
    		e.setCancelled(true);
	}

	// projectile
	@EventHandler
	public void onProjectileExplosion(ProjectileHitEvent e) {
	    Projectile	projectile = e.getEntity();
		if (!(projectile.getShooter() instanceof LivingEntity l)) return;
		LivingEntityCustom	launcher = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
		if (launcher == null) return;
		SpellRegistry		spellRegistry = RpgCraft.getSpellRegistry();
		UUID				uuid = (launcher == null ? null : launcher.getUUID());
		// fireball
		if (spellRegistry.isFireball(projectile)) {
			int	level = spellRegistry.getFireballRarity(projectile);
			if (level == 0) return;
			Location	location = projectile.getLocation();
			double		radius = (level * 0.5d) + 3.5;
			double 		damage = level * 4;
			int			fireTicks = (level + 2) * 20;
			spellRegistry.explosion(launcher, location, radius, damage, 1, fireTicks);
		}
		// holybomb
		else if (spellRegistry.hasHolyBomb(uuid) && spellRegistry.isStaff(projectile)) {
			int		level = spellRegistry.removeHolyBomb(uuid);
			if (projectile instanceof SmallFireball smallFireball)
				smallFireball.setIsIncendiary(false);
			spellRegistry.holyBombExplosion(launcher, projectile.getLocation(), Rarity.fromInt(level));
		}
		// shadowword
		else if (spellRegistry.isShadowWord(projectile)) {
			int	level = spellRegistry.getShadowWordRarity(projectile);
			if (level == 0) return;
			Location	location = projectile.getLocation();
			spellRegistry.shadowWordExplosion(launcher, Rarity.fromInt(level), location);
		}
		// explosive shot
		else if (spellRegistry.hasExplosiveShot(uuid) && (spellRegistry.isBow(projectile) || spellRegistry.isCrossBow(projectile))) {
			int	level = spellRegistry.removeExplosiveShot(uuid);
			if (level == 0) return;
			Location	location = projectile.getLocation();
			double		radius = 4;
			double 		damage = level * 3;
			spellRegistry.explosion(launcher, location, radius, damage, 1, 0);
		}
		else
			return;
		projectile.remove();
		e.setCancelled(true);
	}
}
