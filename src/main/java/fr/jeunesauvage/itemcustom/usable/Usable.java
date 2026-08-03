package fr.jeunesauvage.itemcustom.usable;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomManager;

public interface Usable {
	void				use(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot);
	boolean				canUse(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot);
	ItemCustomCategory	getCategory();
	Material			getMaterial();
}
