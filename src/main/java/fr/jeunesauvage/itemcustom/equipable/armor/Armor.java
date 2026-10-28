package fr.jeunesauvage.itemcustom.equipable.armor;

import fr.jeunesauvage.itemcustom.ArmorType;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.itembuilder.EquipableStat;

public class Armor extends Equipable<ArmorType> {
	private final String	set;
	public Armor(String name, ArmorType type, EquipableStat equipableStat) {
		super(name, equipableStat, type);
		this.set = equipableStat.getSet();
	}

	public String getSet() {
		return set;
	}
}
