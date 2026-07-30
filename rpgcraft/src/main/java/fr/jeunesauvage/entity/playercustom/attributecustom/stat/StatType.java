package fr.jeunesauvage.entity.playercustom.attributecustom.stat;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public sealed interface StatType permits StatPrimary, StatSecondary {
	AttributeCategory	getCategory();
	String				getName();
	NamespacedKey		getKey();
	Component			toComponent();
	TextColor			getColor();
	static StatType		fromString(String name) {
        if (name == null)
            return null;
		for (StatPrimary type: StatPrimary.values()) {
			if (type.getName().equals(name))
        		return type;
		}
		for (StatSecondary type: StatSecondary.values()) {
			if (type.getName().equals(name))
        		return type;
		}
        return null;
	}
}
