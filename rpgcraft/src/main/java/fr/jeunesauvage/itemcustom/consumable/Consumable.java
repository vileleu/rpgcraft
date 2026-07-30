package fr.jeunesauvage.itemcustom.consumable;

import org.bukkit.Material;

import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;

public interface Consumable {
	void 				consume(ConsumableManager consumableManager, PlayerCustom playerCustom);
	boolean				canConsume(PlayerCustom playerCustom);
	ItemCustomCategory	getCategory();
	Material			getMaterial();
}
