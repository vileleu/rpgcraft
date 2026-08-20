package fr.jeunesauvage.itemcustom.equipable.weapon.launcher;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.sound.SoundManager;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;

public class LauncherManager implements Listener {
	private final ItemCustomManager		itemCustomManager;
	private static final float			FORCENEED_DEFAULT = 1f;
	private static final NamespacedKey	KEY_SAVEITEM = new NamespacedKey(RpgCraft.name(), "saveitem");
	private static final int			SLOT_SAVEITEM = 35;
	private static final NamespacedKey	KEY_BOW = new NamespacedKey(RpgCraft.name(), "bow");
	private static final NamespacedKey	KEY_CROSSBOW = new NamespacedKey(RpgCraft.name(), "crossbow");
	private static final NamespacedKey	KEY_STAFF = new NamespacedKey(RpgCraft.name(), "staff");
	private static final NamespacedKey	KEY_SPELLBOOK = new NamespacedKey(RpgCraft.name(), "spellbook");

	public LauncherManager(ItemCustomManager itemCustomManager) {
		this.itemCustomManager = itemCustomManager;
	}

	// use staff or spellbook without arrows
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onRightClickStaff(PlayerInteractEvent e) {
	    if (!e.getAction().isRightClick()) return;
	    Player		player = e.getPlayer();
	    ItemStack	item = e.getItem();
	    if (item == null) return;
		Weapon		weapon = itemCustomManager.getWeapon(item);
		if (weapon == null || (weapon.getType() != WeaponType.STAFF && weapon.getType() != WeaponType.SPELLBOOK)) return;
		// launch spellbook
		if (weapon.getType() == WeaponType.SPELLBOOK) {
			CrossbowMeta	meta = (CrossbowMeta)item.getItemMeta();
			if (meta.hasChargedProjectiles()) {
				launchSpellBook(player, weapon);
				meta.setChargedProjectiles(Collections.emptyList());
				item.setItemMeta(meta);
				damageLauncher(player, item);
				e.setCancelled(true);
				return;
			}
		}
		// charge + shoot for launchers
		allowVanillaCharge(player);
		charge(player, item);
	}

	// allow charge vanilla from bow and crossbow
	private void allowVanillaCharge(Player player) {
		PlayerInventory	inv = player.getInventory();
		if (hasArrow(inv))
			replaceArrow(player);
		else {
			int	slot = findSlot(inv);
			if (slot == -1) {
				String	base64 = Data.toBase64(inv.getItem(SLOT_SAVEITEM));
				Data.setString(player.getPersistentDataContainer(), KEY_SAVEITEM, base64);
				player.getInventory().setItem(SLOT_SAVEITEM, new ItemStack(Material.ARROW));
			}
			else
				player.getInventory().setItem(slot, new ItemStack(Material.ARROW));
			Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
				if (slot == -1) {
					ItemStack	itemSaved = null;
					String		base64 = Data.getString(player.getPersistentDataContainer(), KEY_SAVEITEM);
					if (base64 != null) {
						Data.remove(player.getPersistentDataContainer(), KEY_SAVEITEM);
						itemSaved = Data.fromBase64(base64);
					}
					player.getInventory().setItem(SLOT_SAVEITEM, itemSaved);
				}
				else
					player.getInventory().setItem(slot, null);
			});
		}
	}

	// reset item saved
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player	player = e.getPlayer();
		giveBackArrow(player);
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		String					base64 = Data.getString(pdc, KEY_SAVEITEM);
		if (base64 == null) return;
		ItemStack	item = Data.fromBase64(base64);
		Data.remove(pdc, KEY_SAVEITEM);
		player.getInventory().setItem(SLOT_SAVEITEM, item);
	}

	// create custom charge
	private void charge(Player player, ItemStack weapon) {
		new BukkitRunnable() {
		    float	ticks = 0f;
			boolean	sound = false;
			float	forceNeed = FORCENEED_DEFAULT;
		    @Override
		    public void run() {
	    		float	force = ticks / 20;
				if (!sound && force >= forceNeed) {
					sound = true;
					player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
				}
		    	if (player.isDead() || !player.isOnline()) {
					giveBackArrow(player);
		    	    cancel();
					return;
		    	}
		    	if (player.isHandRaised()) {
					ticks++;
					return;
				}
				// launch
				if (force >= forceNeed) {
					if (weapon.getType() == Material.BOW) {
						launchStaff(player);
						damageLauncher(player, weapon);
					}
					else if (weapon.getType() == Material.CROSSBOW) {
						CrossbowMeta	metaSpellBook = (CrossbowMeta)weapon.getItemMeta();
						ItemStack		projectile = new ItemStack(Material.ARROW);
						ItemMeta		metaProjectile = projectile.getItemMeta();
						metaProjectile.displayName(Component.translatable("item.rpgcraft.spellbook_projectile"));
						projectile.setItemMeta(metaProjectile);
						metaSpellBook.addChargedProjectile(projectile);
						weapon.setItemMeta(metaSpellBook);
					}
				}
				giveBackArrow(player);
		        cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 1L);
	}

	// replace arrow in inventory
	private void replaceArrow(Player player) {
		Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
			PlayerInventory	inv = player.getInventory();
			ItemStack[]		contents = inv.getContents();
    		for (int i = 0; i < contents.length; i++) {
				ItemStack	item = contents[i];
    		    if (!isArrow(item)) continue;
				String		base64 = Data.toBase64(item);
				ItemStack	barrier = new ItemStack(Material.BARRIER);
				ItemMeta	meta = barrier.getItemMeta();
				Data.setString(meta.getPersistentDataContainer(), KEY_SAVEITEM, base64);
				barrier.setItemMeta(meta);
				player.getInventory().setItem(i, barrier);
			}
		});
	}

	// give back arrow in inventory
	private void giveBackArrow(Player player) {
		PlayerInventory	inv = player.getInventory();
		ItemStack[]		contents = inv.getContents();
    	for (int i = 0; i < contents.length; i++) {
			ItemStack	barrier = contents[i];
    	    if (!isBarrier(barrier)) continue;
			ItemMeta				meta = barrier.getItemMeta();
			PersistentDataContainer	pdc = meta.getPersistentDataContainer();
			String					base64 = Data.getString(pdc, KEY_SAVEITEM);
			if (base64 != null) {
				ItemStack	arrow = Data.fromBase64(base64);
				player.getInventory().setItem(i, arrow);
			}
			else
				player.getInventory().setItem(i, null);
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
	    if (item == null) return false;
	    Material	material = item.getType();
	    return material == Material.BARRIER;
	}

	private int findSlot(PlayerInventory inventory) {
		ItemStack[]	contents = inventory.getStorageContents();
    	for (int i = 9; i < contents.length; i++) {
    	    if (contents[i] == null || contents[i].getType().isAir())
				return i;
    	}
		return -1;
	}

	// launch staff
	public void launchStaff(Player shooter) {
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(shooter);
		switch (playerCustom.getClassType()) {
			case PYROMANCER, GOD -> {
	    		SmallFireball	smallFireball = shooter.launchProjectile(SmallFireball.class);
	    		smallFireball.setGravity(false);
	    		smallFireball.setVelocity(shooter.getEyeLocation().getDirection());
				SoundManager.playSound(shooter, "staff_shoot");
				Data.setBoolean(smallFireball.getPersistentDataContainer(), KEY_STAFF);
			}
			case PRIEST -> {
	    		DragonFireball	dragonFireball = shooter.launchProjectile(DragonFireball.class);
	    		dragonFireball.setGravity(false);
	    		dragonFireball.setVelocity(shooter.getEyeLocation().getDirection());
				SoundManager.playSound(shooter, "staff_shoot");
				Data.setBoolean(dragonFireball.getPersistentDataContainer(), KEY_STAFF);
			}
			default -> {
				AttributeInstance	attributeInstance = shooter.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				double				damage = 0;
				if (attributeInstance != null)
					damage = shooter.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.9;
				itemCustomManager.getSpellManager().explosionFriendlyFire(shooter.getEyeLocation(), 6, damage, 2, 0);
			}
		}
	}

	// launch staff
	public void launchStaff(LivingEntity shooter, LivingEntity target) {
		switch (shooter.getName()) {
			case "ClassMaster Priest Tauren" -> {
	    		DragonFireball	dragonFireball = shooter.launchProjectile(DragonFireball.class);
	    		dragonFireball.setGravity(false);
	    		dragonFireball.setVelocity(target.getEyeLocation().subtract(shooter.getEyeLocation()).toVector());
				SoundManager.playSound(shooter, "staff_shoot");
				Data.setBoolean(dragonFireball.getPersistentDataContainer(), KEY_STAFF);
			}
			default -> {
	    		SmallFireball	smallFireball = shooter.launchProjectile(SmallFireball.class);
	    		smallFireball.setGravity(false);
	    		smallFireball.setVelocity(target.getEyeLocation().subtract(shooter.getEyeLocation()).toVector());
				SoundManager.playSound(shooter, "staff_shoot");
				Data.setBoolean(smallFireball.getPersistentDataContainer(), KEY_STAFF);
			}
		}
	}

	// launch spellbook
	public void launchSpellBook(Player shooter, Weapon spellBook) {
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(shooter);
		switch (playerCustom.getClassType()) {
			case PYROMANCER, PRIEST, GOD -> {
	    		switch (spellBook.getIdentifier()) {
					case "spellbook_blades_of_war" -> bladesOfWar(shooter);
					case "spellbook_hellow" -> {}
					case "spellbook_braised" -> {}
					case "spellbook_majestica" -> {}
					default -> {}
				}
			}
			default -> {
				AttributeInstance	attributeInstance = shooter.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				double				damage = 0;
				if (attributeInstance != null)
					damage = shooter.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.9;
				itemCustomManager.getSpellManager().explosionFriendlyFire(shooter.getEyeLocation(), 6, damage, 2, 0);
			}
		}
	}

	// launch spellbook
	public void launchSpellBook(LivingEntity shooter, LivingEntity target, Weapon spellBook) {
		switch (spellBook.getIdentifier()) {
			case "spellbook_blades_of_war" -> bladesOfWar(shooter);
			case "spellbook_hellow" -> {}
			case "spellbook_braised" -> {}
			case "spellbook_majestica" -> {}
			default -> {}
		}
	}

	// spellbook blades of war
    public void bladesOfWar(LivingEntity shooter) {
        Location	eyeLoc = shooter.getEyeLocation();
        Vector		forward = eyeLoc.getDirection().normalize();
        Location	center = eyeLoc.clone().add(forward.clone().multiply(6));
        int			amount = 30;
        double		range = 5.0;
		double		rangeSquared = range * range;
        double		height = 6.0;
		int			delay = 1;
        for (int i = 0; i < amount; i++) {
            double xOffset = ThreadLocalRandom.current().nextDouble(-range, range);
            double zOffset = ThreadLocalRandom.current().nextDouble(-range, range);
            if (xOffset * xOffset + zOffset * zOffset > rangeSquared) continue;
			Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
            	Location	spawnLoc = center.clone().add(xOffset, height, zOffset);
            	Arrow		arrow = shooter.getWorld().spawn(spawnLoc, Arrow.class);
            	Vector		velocity = forward.clone().multiply(1);
            	velocity.setY(-0.3);
            	arrow.setShooter(shooter);
            	arrow.setGravity(false);
            	arrow.setVelocity(velocity);
				arrow.setDamage(1);
				arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
				PersistentDataContainer	pdc = arrow.getPersistentDataContainer();
				Data.setBoolean(pdc, KEY_BOW);
				Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
					if (arrow.isValid() && !arrow.isDead())
						arrow.remove();
				}, 60);
			}, delay++);
        }
		SoundManager.playSound(shooter, "staff_shoot");
    }

	// dragonfireball hit
	@EventHandler
	public void onDragonFireballHit(EnderDragonFireballHitEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) return;
		e.getAreaEffectCloud().setDuration(20);
	}

	// dragonfireball area hit
	@EventHandler(priority = EventPriority.LOW)
	public void onDragonFireballArea(AreaEffectCloudApplyEvent e) {
		if (!(e.getEntity().getSource() instanceof Player player)) return;
		e.setCancelled(true);
		for (LivingEntity victim: e.getAffectedEntities()) {
			if (victim.equals(player)) continue;
			victim.damage(0.5, CombatDamage.getDamageSource(player, CombatDamage.MAGIC.getType()));
		}
	}

	// set key for projectiles from ranged weapons (npc can't trigger this event)
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if (!(e.getEntity() instanceof LivingEntity livingEntity)) return;
		ItemStack	item = e.getBow();
		if (item == null || item.getType().isAir()) return;
		Weapon	weapon = itemCustomManager.getWeapon(item);
		if (weapon == null) return;
		Entity					projectile = e.getProjectile();
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		// non player
		if (livingEntity instanceof Mob mob) {
			switch (weapon.getType()) {
				case BOW -> Data.setBoolean(pdc, KEY_BOW);
				case CROSSBOW -> Data.setBoolean(pdc, KEY_CROSSBOW);
				case STAFF -> {
					e.setCancelled(true);
					launchStaff(livingEntity, mob.getTarget());
				}
				case SPELLBOOK -> {
					e.setCancelled(true);
	    			launchSpellBook(livingEntity, mob.getTarget(), weapon);
				}
				default -> {}
			}
			return;
		}
		switch (weapon.getType()) {
			case BOW -> Data.setBoolean(pdc, KEY_BOW);
			case CROSSBOW -> Data.setBoolean(pdc, KEY_CROSSBOW);
			case STAFF -> e.setCancelled(true);
			case SPELLBOOK -> e.setCancelled(true);
			default -> {}
		}
	}

	// custom damage for launcher
	private void damageLauncher(Player player, ItemStack launcher) {
		Equipable<?>	equipable = itemCustomManager.getEquipable(launcher);
		if (equipable == null) return;
		ItemMeta		meta = launcher.getItemMeta();
		if (!(meta instanceof Damageable damageable)) return;
		int	damage = itemCustomManager.getEquipableManager().getNewDurability(3, equipable);
		damageable.setDamage(damageable.getDamage() + damage);
		if (damageable.getDamage() >= launcher.getType().getMaxDurability()) {
			int	slot = getSlot(player, launcher);
			if (slot == -1) return;
		    player.getInventory().setItem(slot, null);
			player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
			return;
		}
		launcher.setItemMeta(meta);
	}

	private int getSlot(Player player, ItemStack item) {
		NamespacedKey			key = new NamespacedKey(RpgCraft.name(), "findslot");
		ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
		Data.setBoolean(pdc, key);
		PlayerInventory				inv = player.getInventory();
		ItemStack					hand = inv.getItemInMainHand();
		ItemStack					offhand = inv.getItemInOffHand();
		PersistentDataContainerView	pdcHand = hand.getPersistentDataContainer();
		PersistentDataContainerView	pdcOffhand = offhand.getPersistentDataContainer();
		int							slot = -1;
		if (Data.hasBoolean(pdcHand, key))
			slot =  inv.getHeldItemSlot();
		else if (Data.hasBoolean(pdcOffhand, key))
			slot = 40;
		Data.remove(pdc, key);
		return slot;
	}

	public static NamespacedKey getBowKey() {
		return KEY_BOW;
	}

	public static NamespacedKey getCrossbowKey() {
		return KEY_CROSSBOW;
	}

	public static NamespacedKey getStaffKey() {
		return KEY_STAFF;
	}

	public static NamespacedKey getSpellbookKey() {
		return KEY_SPELLBOOK;
	}
}
