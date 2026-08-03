package fr.jeunesauvage.itemcustom.equipable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.ArrayListMultimap;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import fr.jeunesauvage.itemcustom.itembuilder.EquipableStat;
import net.kyori.adventure.text.Component;

public abstract class Equipable<T extends ItemCustomType> extends ItemCustom<T> {
	protected Map<StatPrimary, Integer>	statsPrimary = new HashMap<>();
	protected Map<StatSecondary, Integer>	statsSecondary = new HashMap<>();

	protected Equipable(String name, EquipableStat equipableStat, T type, int customModelData) {
		super(type, name, equipableStat.getRarity(), equipableStat.getLevel());
		buildItem(equipableStat, customModelData);
	}

	private void buildItem(EquipableStat equipableStat, int customModelData) {
		ItemMeta	meta = item.getItemMeta();
		meta.displayName(Lore.nameEquipable(name, rarity));
        Data.setString(meta.getPersistentDataContainer(), KEY_IDENTIFIER, name);
		// remove attribute vanilla
		meta.setAttributeModifiers(ArrayListMultimap.create());
		AttributeModifier	tmp = new AttributeModifier(new NamespacedKey(RpgCraft.name(), "armor"), 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
		meta.addAttributeModifier(Attribute.GENERIC_ARMOR, tmp);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		// write lore
		List<Component>	lore = new ArrayList<>();
		lore.add(Lore.type(type));
		lore.add(Lore.rarity(rarity));
		lore.add(Lore.level(level));
		Set<StatPrimary>	primary = equipableStat.getStatsPrimary();
		if (primary != null && !primary.isEmpty()) {
			int	value = level / primary.size();
			for (StatPrimary slot: primary) {
				int	valuePrimary = value;
				if (this.statsPrimary.isEmpty() && value % 2 != 0)
					valuePrimary++;
				this.statsPrimary.put(slot, valuePrimary);
				lore.add(Lore.stat(slot, valuePrimary));
			}
		}
		Set<StatSecondary>	secondary = equipableStat.getStatsSecondary();
		if (secondary != null && !secondary.isEmpty()) {
			int	value = level / secondary.size();
			for (StatSecondary slot: secondary) {
				int	valueSecondary = value;
				if (this.statsSecondary.isEmpty() && value % 2 != 0)
					valueSecondary++;
				this.statsSecondary.put(slot, valueSecondary);
				lore.add(Lore.stat(slot, valueSecondary));
			}
		}
		meta.lore(lore);
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
	}

	public Map<StatPrimary, Integer> getStatsPrimary() {
		return statsPrimary;
	}

	public Map<StatSecondary, Integer> getStatsSecondary() {
		return statsSecondary;
	}

	@Override
	public Component toComponent() {
        return Component.translatable("item.rpgcraft." + name);
	}
}
