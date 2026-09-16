package fr.jeunesauvage.entitycustom.livingentitycustom.metamorph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;
import fr.jeunesauvage.entitycustom.livingentitycustom.saveEquipment.SaveEquipment;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.spell.SpellRegistry;
import fr.jeunesauvage.sound.SoundManager;

public class MetamorphRegistry {
	private final Map<UUID, Map<StatType, Integer>>			statsDracthyr = new HashMap<>();
	private final Map<UUID, BukkitTask>						tasks = new HashMap<>();

	public void addDracthyr(LivingEntityCustom launcher, Rarity rarity) {
		if (launcher.getType() != EntityType.PLAYER) return;
		if (isDracthyr(launcher)) return;
		UUID	uuid = launcher.getUUID();
		launcher.setMetamorph(FormType.DRACTHYR_BLACK);
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
		if (launcher instanceof PlayerCustom playerCustom) {
			equipDracthyr(launcher);
			playerCustom.refreshCooldown();
		}
		launcher.refreshStat();
		// sound
		SoundManager.playSound(launcher, "spell_metamorph");
		// task
		tasks.put(uuid, new BukkitRunnable() {
		    	@Override
		    	public void run() {
					if (!launcher.isPresent() || RpgCraft.getSpellRegistry().isLanding(launcher)) {
						removeDracthyr(launcher);
						cancel();
					}
				};
			}.runTaskTimer(RpgCraft.instance(), Data.d(SpellRegistry.TIME_METAMORPH), 10L)
		);
	}

	public void equipDracthyr(LivingEntityCustom launcher) {
		ItemCustomRegistry	itemCustomRegistry = RpgCraft.getItemCustomRegistry();
		Armor				wings = itemCustomRegistry.getArmor("ender_dragon_wings");
		Weapon				claw = itemCustomRegistry.getWeapon("claw_lightning");
		EntityEquipment		equipment = launcher.getEquipment();
		ItemStack			chest = equipment.getChestplate();
		ItemStack			hand = equipment.getItemInMainHand();
		ItemStack			offhand = equipment.getItemInOffHand();
		launcher.saveEquipment(SaveEquipment.CHEST, chest);
		launcher.saveEquipment(SaveEquipment.HAND, hand);
		launcher.saveEquipment(SaveEquipment.OFFHAND, offhand);
		Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
			equipment.setChestplate(wings.getItemClone());
			equipment.setItemInMainHand(claw.getItemClone());
			equipment.setItemInOffHand(claw.getItemClone());
		}, 5L);
	}

	public void unequipDracthyr(LivingEntityCustom launcher) {
		if (!launcher.isPresent()) return;
		Map<SaveEquipment, ItemStack>	savedEquipment = launcher.getSavedEquipment();
		if (savedEquipment.isEmpty()) return;
		ItemStack						chest = savedEquipment.get(SaveEquipment.CHEST);
		ItemStack						hand = savedEquipment.get(SaveEquipment.HAND);
		ItemStack						offhand = savedEquipment.get(SaveEquipment.OFFHAND);
		launcher.deleteSavedEquipment();
		EntityEquipment	equipment = launcher.getEquipment();
		equipment.setChestplate(chest);
		equipment.setItemInMainHand(hand);
		equipment.setItemInOffHand(offhand);
	}

	public void removeDracthyr(LivingEntityCustom launcher) {
		if (launcher.getType() != EntityType.PLAYER) return;
		if (launcher.getMetamorph() != FormType.DRACTHYR_BLACK) return;
		UUID					uuid = launcher.getUUID();
		// stats
		Map<StatType, Integer>	map = statsDracthyr.get(uuid);
		if (map != null) {
			for (Integer id: map.values()) {
				if (id == null) continue;
				launcher.deleteModifier(id);
			}
		}
		// inventory
		unequipDracthyr(launcher);
		// refresh
		if (launcher instanceof PlayerCustom playerCustom) playerCustom.refreshCooldown();
		launcher.refreshStat();
		// form
		launcher.setMetamorph(FormType.UNKNOWN);
		// sound
		SoundManager.playSound(launcher, "spell_metamorph_end");
	}

	public boolean isDracthyr(LivingEntityCustom launcher) {
		return launcher.getMetamorph() == FormType.DRACTHYR_BLACK;
	}

	public void clean(LivingEntityCustom launcher) {
		if (isDracthyr(launcher)) {
			removeDracthyr(launcher);
			UUID	uuid = launcher.getUUID();
			BukkitTask	task = tasks.remove(uuid);
			if (task != null)
				task.cancel();
		}
		else
        	unequipDracthyr(launcher);
	}
}
