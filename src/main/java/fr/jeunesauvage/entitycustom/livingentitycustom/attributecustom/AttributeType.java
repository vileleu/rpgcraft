package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom;

import net.kyori.adventure.text.Component;

public sealed interface AttributeType permits StatType, SkillType {
	AttributeCategory	getCategory();
	String				getName();
	Component			toComponent();

	static AttributeType fromString(String name) {
		AttributeType	attributeType = StatType.fromString(name);
		if (attributeType == null) attributeType = SkillType.fromString(name);
		return attributeType;
	}
}
