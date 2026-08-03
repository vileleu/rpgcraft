package fr.jeunesauvage.entity.playercustom.equipementcustom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.Stat;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.equipable.Equipable;

public class EquipementCustom {
	private final Player						player;
	private final AttributeManager				attributeManager;
	private final ItemCustomManager				itemCustomManager;
	private final Map<StatPrimary, Integer>		valuePrimary = new HashMap<>();
	private final Map<StatSecondary, Integer>	valueSecondary = new HashMap<>();

	public EquipementCustom(Player player, AttributeManager attributeManager, ItemCustomManager itemCustomManager) {
		this.player = player;
		this.attributeManager = attributeManager;
		this.itemCustomManager = itemCustomManager;
	}

	public void refresh() {
		mergeStats();
		applyStats();
		refreshStats();
		clear();
	}

	private void mergeStats() {
		for (Equipable<?> equipable: getEquipement(player, itemCustomManager)) {
			if (equipable == null) continue;
			for (StatPrimary type: StatPrimary.values()) {
				Integer	value = equipable.getStatsPrimary().get(type);
				if (value == null || value == 0) continue;
				valuePrimary.merge(type, value, (a, b) -> a + b);
			}
			for (StatSecondary type: StatSecondary.values()) {
				Integer	value = equipable.getStatsSecondary().get(type);
				if (value == null || value == 0) continue;
				valueSecondary.merge(type, value, (a, b) -> a + b);
			}
		}
	}

	private void applyStats() {
		for (StatPrimary type: StatPrimary.values()) {
			Stat	stat = attributeManager.getStat(type);
			int		value = stat.getValueBonus();
			Integer	tmp = valuePrimary.get(type);
			if (tmp != null)
				value += tmp;
			stat.setValue(value);
		}
		for (StatSecondary type: StatSecondary.values()) {
			Stat	stat = attributeManager.getStat(type);
			int		value = stat.getValueBonus();
			Integer	tmp = valueSecondary.get(type);
			if (tmp != null)
				value += tmp;
			stat.setValue(value);
		}
	}

	private void refreshStats() {
		attributeManager.refreshStats();
	}

	private void clear() {
		valuePrimary.clear();
		valueSecondary.clear();
	}

	private List<Equipable<?>> getEquipement(Player player, ItemCustomManager itemCustomManager) {
		PlayerInventory		inv = player.getInventory();
		List<Equipable<?>>	equipments = new ArrayList<>();
		equipments.add(itemCustomManager.getEquipable(getHead(inv.getHelmet())));
		equipments.add(itemCustomManager.getEquipable(getChest(inv.getChestplate())));
		equipments.add(itemCustomManager.getEquipable(getLegs(inv.getLeggings())));
		equipments.add(itemCustomManager.getEquipable(getFeet(inv.getBoots())));
		equipments.add(itemCustomManager.getEquipable(getHand(inv.getItemInMainHand())));
		equipments.add(itemCustomManager.getEquipable(getOffhand(inv.getItemInOffHand())));
		return equipments;
	}

	private ItemStack getHead(ItemStack item) {
		if (item == null) return null;
		if (!item.getType().getEquipmentSlot().equals(EquipmentSlot.HEAD)) return null;
		return item;
	}

	private ItemStack getChest(ItemStack item) {
		if (item == null) return null;
		if (!item.getType().getEquipmentSlot().equals(EquipmentSlot.CHEST)) return null;
		return item;
	}

	private ItemStack getLegs(ItemStack item) {
		if (item == null) return null;
		if (!item.getType().getEquipmentSlot().equals(EquipmentSlot.LEGS)) return null;
		return item;
	}

	private ItemStack getFeet(ItemStack item) {
		if (item == null) return null;
		if (!item.getType().getEquipmentSlot().equals(EquipmentSlot.FEET)) return null;
		return item;
	}

	private ItemStack getHand(ItemStack item) {
		if (item == null) return null;
		if (!item.getType().getEquipmentSlot().equals(EquipmentSlot.HAND) && !item.getType().getEquipmentSlot().equals(EquipmentSlot.OFF_HAND)) return null;
		return item;
	}
	
	private ItemStack getOffhand(ItemStack item) {
		if (item == null) return null;
		if (!item.getType().getEquipmentSlot().equals(EquipmentSlot.OFF_HAND) && !item.getType().getEquipmentSlot().equals(EquipmentSlot.HAND)) return null;
		return item;
	}
}
