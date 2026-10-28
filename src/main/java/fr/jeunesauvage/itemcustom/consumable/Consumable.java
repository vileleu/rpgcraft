package fr.jeunesauvage.itemcustom.consumable;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public interface Consumable {
	void 				consume(PlayerCustom playerCustom);
	boolean				canConsume(PlayerCustom playerCustom);
	Material			getMaterial();
}
