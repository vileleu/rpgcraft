package fr.jeunesauvage.itemcustom.consumable;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;

public interface Consumable {
	void 				consume(PlayerCustom playerCustom);
	boolean				canConsume(PlayerCustom playerCustom);
	ItemCustomCategory	getCategory();
	Material			getMaterial();
}
