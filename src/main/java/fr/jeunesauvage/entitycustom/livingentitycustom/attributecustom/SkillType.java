package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom;

import org.bukkit.NamespacedKey;

public sealed interface SkillType extends AttributeType permits SkillPrimary, SkillSecondary {
	NamespacedKey	getKey();

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