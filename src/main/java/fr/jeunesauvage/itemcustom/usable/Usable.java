package fr.jeunesauvage.itemcustom.usable;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public interface Usable {
	void				use(PlayerCustom playerCustom, EquipmentSlot slot);
	boolean				canUse(PlayerCustom playerCustom, EquipmentSlot slot);
	Material			getMaterial();
}
