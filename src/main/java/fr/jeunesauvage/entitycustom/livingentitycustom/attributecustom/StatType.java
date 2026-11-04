package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom;

public sealed interface StatType extends AttributeType permits StatPrimary, StatSecondary {
	static StatType fromString(String name) {
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