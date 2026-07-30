package fr.jeunesauvage.itemcustom.spell;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatType;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.sound.SoundManager;

public class MetamorphManager implements Listener {
	private static final NamespacedKey						KEY_DRACTHYR = new NamespacedKey(RpgCraft.name(), "dracthyr");
	private static final NamespacedKey						KEY_DRACTHYR_CHEST = new NamespacedKey(RpgCraft.name(), "dracthyrchest");
	private static final NamespacedKey						KEY_DRACTHYR_HAND = new NamespacedKey(RpgCraft.name(), "dracthyrhand");
	private static final NamespacedKey						KEY_DRACTHYR_OFFHAND = new NamespacedKey(RpgCraft.name(), "dracthyroffhand");
	private final Map<UUID, Map<StatType, Integer>>			statsDracthyr = new HashMap<>();
	private final Map<UUID, BukkitTask>						tasks = new HashMap<>();

	// metamorph hotbar
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onWeaponHotbar(PlayerItemHeldEvent e) {
	    Player		player = e.getPlayer();
		if (!Data.hasBoolean(player.getPersistentDataContainer(), KEY_DRACTHYR)) return;
		e.setCancelled(true);
	}

	// metamorph swap
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onWeaponSwap(PlayerSwapHandItemsEvent e) {
	    Player		player = e.getPlayer();
		if (!Data.hasBoolean(player.getPersistentDataContainer(), KEY_DRACTHYR)) return;
		e.setCancelled(true);
	}

	// metamorph open inventory
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onInventoryOpen(InventoryOpenEvent e) {
		if (!(e.getPlayer() instanceof Player player)) return;
		if (!Data.hasBoolean(player.getPersistentDataContainer(), KEY_DRACTHYR)) return;
		e.setCancelled(true);
	}

	// metamorph click inventory
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent e) {
	    if (!(e.getWhoClicked() instanceof Player player)) return;
	    if (!Data.hasBoolean(player.getPersistentDataContainer(), KEY_DRACTHYR)) return;
	    e.setCancelled(true);
	}

	public void addDracthyr(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, int level) {
		Player					player = playerCustom.getPlayer();
		UUID					uuid = player.getUniqueId();
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		// skin + form + scale
		PlayerCustom.setSkin(player, "dragon_black");
		playerCustom.setFormType(FormType.DRACTHYR);
		EntityManager.setScale(playerCustom);
		// set key
		Data.setBoolean(pdc, KEY_DRACTHYR);
		// inventory
		PlayerInventory			inv = player.getInventory();
		Armor					wings = itemCustomManager.getArmor("ender_dragon_wings");
		Weapon					claw = itemCustomManager.getWeapon("claw_lightning");
		ItemStack				chest = inv.getChestplate();
		ItemStack				hand = inv.getItemInMainHand();
		ItemStack				offhand = inv.getItemInOffHand();
		if (chest != null)
			Data.setString(pdc, KEY_DRACTHYR_CHEST, Data.toBase64(chest));
		if (hand != null)
			Data.setString(pdc, KEY_DRACTHYR_HAND, Data.toBase64(hand));
		if (offhand != null)
			Data.setString(pdc, KEY_DRACTHYR_OFFHAND, Data.toBase64(offhand));
		inv.setChestplate(wings.getItemClone());
		inv.setItemInMainHand(claw.getItemClone());
		inv.setItemInOffHand(claw.getItemClone());
		// stats
		Map<StatType, Integer>	map = statsDracthyr.computeIfAbsent(uuid, id -> new HashMap<>());
		int						agility = level * 10;
		int						strength = level * 10;
		map.put(StatPrimary.AGILITY, playerCustom.addStatModifier(StatPrimary.AGILITY, agility, 0));
		map.put(StatPrimary.STRENGTH, playerCustom.addStatModifier(StatPrimary.STRENGTH, strength, 0));
		// explosion
		Location	center = player.getLocation();
		double		radius = 6;
		double 		damage = 6 + level * 3;
		double		force = 2;
		itemCustomManager.getSpellManager().explosion(player, center, radius, damage, force, 0);
		// refresh equipment + refresh cooldown
		playerCustom.refreshEquipement();
		playerCustom.refreshCooldown();
		// sound
		SoundManager.playSound(playerCustom, "spell_metamorph");
		// task
		tasks.put(uuid, Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
			if (!player.isOnline() || player.isDead() || playerCustom == null) return;
			new BukkitRunnable() {
		    	@Override
		    	public void run() {
					if (!player.isOnline() || player.isDead() || playerCustom == null) {
						cancel();
						return;
					}
					else if (isLanding(player)) {
						cancel();
						removeDracthyr(playerCustom);
					}
				};
			}.runTaskTimer(RpgCraft.instance(), 0L, 10L);
		}, SpellManager.TIME_METAMORPH * 20L));
	}

	public void removeDracthyr(PlayerCustom playerCustom) {
		Player					player = playerCustom.getPlayer();
		UUID					uuid = player.getUniqueId();
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		// skin + form + scale
		PlayerCustom.setSkin(player, null);
		playerCustom.setFormType(FormType.fromRaceType(playerCustom.getRaceType()));
		EntityManager.setScale(playerCustom);
		// stats
		Map<StatType, Integer>	map = statsDracthyr.get(uuid);
		if (map != null) {
			for (Integer id: map.values()) {
				if (id == null) continue;
				playerCustom.removeStatModifier(id);
			}
		}
		// inventory
		PlayerInventory	inv = player.getInventory();
		String			base64Chest = Data.getString(pdc, KEY_DRACTHYR_CHEST);
		ItemStack		item = null;
		if (base64Chest != null) {
			item = Data.fromBase64(base64Chest);
			Data.remove(pdc, KEY_DRACTHYR_CHEST);
		}
		inv.setChestplate(item);
		String	base64Hand = Data.getString(pdc, KEY_DRACTHYR_HAND);
		item = null;
		if (base64Hand != null) {
			item = Data.fromBase64(base64Hand);
			Data.remove(pdc, KEY_DRACTHYR_HAND);
		}
		inv.setItemInMainHand(item);
		String	base64Offhand = Data.getString(pdc, KEY_DRACTHYR_OFFHAND);
		item = null;
		if (base64Offhand != null) {
			item = Data.fromBase64(base64Offhand);
			Data.remove(pdc, KEY_DRACTHYR_OFFHAND);
		}
		inv.setItemInOffHand(item);
		// remove key
		Data.remove(pdc, KEY_DRACTHYR);
		// refresh equipment + refresh cooldown
		playerCustom.refreshEquipement();
		playerCustom.refreshCooldown();
		// sound
		SoundManager.playSound(playerCustom, "spell_metamorph_end");
	}

	public boolean isDracthyr(Player player) {
		return Data.hasBoolean(player.getPersistentDataContainer(), KEY_DRACTHYR);
	}

	public void clean(PlayerCustom playerCustom) {
		Player	player = playerCustom.getPlayer();
		if (isDracthyr(player)) {
			removeDracthyr(playerCustom);
			UUID	uuid = player.getUniqueId();
			BukkitTask	task = tasks.remove(uuid);
			if (task != null)
				task.cancel();
		}
	}

	private boolean isLanding(Player player) {
    	BoundingBox	box = player.getBoundingBox();
    	World		world = player.getWorld();
    	double		y = box.getMinY() - 0.01;
    	for (double x = box.getMinX(); x <= box.getMaxX(); x += 0.3) {
    	    for (double z = box.getMinZ(); z <= box.getMaxZ(); z += 0.3) {
    	        Block block = world.getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    	        if (block.getType().isSolid())
    	            return true;
    	    }
    	}
    	return false;
	}

	public static NamespacedKey getKeyMetamorph() {
		return KEY_DRACTHYR;
	}

	public static NamespacedKey getKeyMetamorphChest() {
		return KEY_DRACTHYR_CHEST;
	}

	public static NamespacedKey getKeyMetamorphHand() {
		return KEY_DRACTHYR_HAND;
	}

	public static NamespacedKey getKeyMetamorphOffhand() {
		return KEY_DRACTHYR_OFFHAND;
	}
}
