package fr.jeunesauvage.entitycustom.livingentitycustom.metamorph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.spell.SpellRegistry;
import fr.jeunesauvage.sound.SoundManager;

public class MetamorphRegistry {
	private static final NamespacedKey						KEY_DRACTHYR = new NamespacedKey(RpgCraft.name(), "dracthyr");
	private static final NamespacedKey						KEY_CHEST = new NamespacedKey(RpgCraft.name(), "dracthyrchest");
	private static final NamespacedKey						KEY_HAND = new NamespacedKey(RpgCraft.name(), "dracthyrhand");
	private static final NamespacedKey						KEY_OFFHAND = new NamespacedKey(RpgCraft.name(), "dracthyroffhand");
	private final Map<UUID, Map<StatType, Integer>>			statsDracthyr = new HashMap<>();
	private final Map<UUID, BukkitTask>						tasks = new HashMap<>();

	public void addDracthyr(LivingEntityCustom launcher, Rarity rarity) {
		if (launcher.getEntityType() != EntityType.PLAYER) return;
		LivingEntity			l = launcher.getLivingEntity();
		if (l == null) return;
		PersistentDataContainer	pdc = l.getPersistentDataContainer();
		if (Data.hasString(pdc, KEY_DRACTHYR)) return;
		UUID	uuid = launcher.getUUID();
		Data.setString(pdc, KEY_DRACTHYR, launcher.getFormType().getName());
		launcher.setFormType(FormType.DRACTHYR_BLACK);
		// inventory
		EntityEquipment		equipment = launcher.getEquipment();
		ItemCustomRegistry	itemCustomRegistry = RpgCraft.getItemCustomRegistry();
		Armor				wings = itemCustomRegistry.getArmor("ender_dragon_wings");
		Weapon				claw = itemCustomRegistry.getWeapon("claw_lightning");
		ItemStack			chest = equipment.getChestplate();
		ItemStack			hand = equipment.getItemInMainHand();
		ItemStack			offhand = equipment.getItemInOffHand();
		if (chest != null)
			Data.setString(pdc, KEY_CHEST, Data.toBase64(chest));
		if (hand != null)
			Data.setString(pdc, KEY_HAND, Data.toBase64(hand));
		if (offhand != null)
			Data.setString(pdc, KEY_OFFHAND, Data.toBase64(offhand));
		equipment.setChestplate(wings.getItemClone());
		equipment.setItemInMainHand(claw.getItemClone());
		equipment.setItemInOffHand(claw.getItemClone());
		// stats
		Map<StatType, Integer>	map = statsDracthyr.computeIfAbsent(uuid, id -> new HashMap<>());
		int						agility = rarity.getNumber() * 10;
		int						strength = rarity.getNumber() * 10;
		map.put(StatPrimary.AGILITY, launcher.addStatModifier(StatPrimary.AGILITY, agility, 0));
		map.put(StatPrimary.STRENGTH, launcher.addStatModifier(StatPrimary.STRENGTH, strength, 0));
		// explosion
		Location	center = launcher.getLocation();
		double		radius = 6;
		double 		damage = 6 + rarity.getNumber() * 3;
		double		force = 2;
		RpgCraft.getSpellRegistry().explosion(launcher, center, radius, damage, force, 0);
		// refresh
		if (launcher instanceof PlayerCustom playerCustom) playerCustom.refreshCooldown();
		launcher.refreshStat();
		// sound
		SoundManager.playSound(launcher, "spell_metamorph");
		// task
		tasks.put(uuid, Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
			if (!launcher.isPresent()) return;
			new BukkitRunnable() {
		    	@Override
		    	public void run() {
					if (!launcher.isPresent()) {
						cancel();
						return;
					}
					else if (RpgCraft.getSpellRegistry().isLanding(launcher)) {
						cancel();
						removeDracthyr(launcher);
					}
				};
			}.runTaskTimer(RpgCraft.instance(), 0L, 10L);
		}, Data.d(SpellRegistry.TIME_METAMORPH)));
	}

	public void removeDracthyr(LivingEntityCustom launcher) {
		if (launcher.getEntityType() != EntityType.PLAYER) return;
		UUID					uuid = launcher.getUUID();
		LivingEntity			l = launcher.getLivingEntity();
		if (l == null) return;
		PersistentDataContainer	pdc = l.getPersistentDataContainer();
		String	formName = Data.getString(pdc, KEY_DRACTHYR);
		if (formName == null) return;
		launcher.setFormType(FormType.fromString(formName));
		// stats
		Map<StatType, Integer>	map = statsDracthyr.get(uuid);
		if (map != null) {
			for (Integer id: map.values()) {
				if (id == null) continue;
				launcher.deleteModifier(id);
			}
		}
		// inventory
		EntityEquipment	equipment = launcher.getEquipment();
		String			base64Chest = Data.getString(pdc, KEY_CHEST);
		ItemStack		item = null;
		if (base64Chest != null) {
			item = Data.fromBase64(base64Chest);
			Data.remove(pdc, KEY_CHEST);
		}
		equipment.setChestplate(item);
		String	base64Hand = Data.getString(pdc, KEY_HAND);
		item = null;
		if (base64Hand != null) {
			item = Data.fromBase64(base64Hand);
			Data.remove(pdc, KEY_HAND);
		}
		equipment.setItemInMainHand(item);
		String	base64Offhand = Data.getString(pdc, KEY_OFFHAND);
		item = null;
		if (base64Offhand != null) {
			item = Data.fromBase64(base64Offhand);
			Data.remove(pdc, KEY_OFFHAND);
		}
		equipment.setItemInOffHand(item);
		// remove key
		Data.remove(pdc, KEY_DRACTHYR);
		// refresh
		if (launcher instanceof PlayerCustom playerCustom) playerCustom.refreshCooldown();
		launcher.refreshStat();
		// sound
		SoundManager.playSound(launcher, "spell_metamorph_end");
	}

	public boolean isDracthyr(LivingEntityCustom launcher) {
		LivingEntity	l = launcher.getLivingEntity();
		if (l == null) return false;
		return Data.hasString(l.getPersistentDataContainer(), KEY_DRACTHYR);
	}

	public void clear(LivingEntityCustom launcher) {
		if (isDracthyr(launcher)) {
			removeDracthyr(launcher);
			UUID	uuid = launcher.getUUID();
			BukkitTask	task = tasks.remove(uuid);
			if (task != null)
				task.cancel();
		}
	}
}
