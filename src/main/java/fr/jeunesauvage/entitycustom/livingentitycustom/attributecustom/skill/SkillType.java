package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeType;

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