package fr.jeunesauvage.entity.playercustom.attributecustom.skill;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public interface SkillType {
	AttributeCategory	getCategory();
    String				getName();
	NamespacedKey		getKey();
	Component			toComponent();
	TextColor			getColor();
	static SkillType	fromString(String name) {
        if (name == null)
            return null;
		for (SkillPrimary type: SkillPrimary.values()) {
			if (type.getName().equals(name))
        		return type;
		}
		for (SkillSecondary type: SkillSecondary.values()) {
			if (type.getName().equals(name))
        		return type;
		}
        return null;
	}
}
