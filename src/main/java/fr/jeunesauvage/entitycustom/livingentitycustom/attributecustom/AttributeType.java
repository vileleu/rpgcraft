package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom;

import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import net.kyori.adventure.text.Component;

public interface AttributeType {
	AttributeCategory	getCategory();
	String				getName();
	Component			toComponent();

	static AttributeType fromString(String name) {
		AttributeType	attributeType = StatType.fromString(name);
		if (attributeType == null) attributeType = SkillType.fromString(name);
		return attributeType;
	}
}
