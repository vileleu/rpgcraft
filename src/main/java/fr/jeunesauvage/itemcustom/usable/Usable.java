package fr.jeunesauvage.itemcustom.usable;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;

public interface Usable {
	void				use(PlayerCustom playerCustom, EquipmentSlot slot);
	boolean				canUse(PlayerCustom playerCustom, EquipmentSlot slot);
	ItemCustomCategory	getCategory();
	Material			getMaterial();
}
