package fr.jeunesauvage.itemcustom.equipable.armor;

import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.itembuilder.EquipableStat;

public class Armor extends Equipable<ArmorType> {
	public Armor(String name, ArmorType type, EquipableStat equipableStat, int customModelData) {
		super(name, equipableStat, type, customModelData);
	}
}
