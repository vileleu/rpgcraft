package fr.jeunesauvage.entity.form;

import fr.jeunesauvage.entity.race.RaceType;

public enum FormType {
    UNKNOWN("unknown", 1),
	// playable
    TAUREN("tauren", 1.1),
    ORC("orc", 0.9),
    DWARF("dwarf", 0.8),
    HUMAN("human", 0.9),
	//
    ANIMAL("animal", 1),
    DWARFIRON("dwarfiron", 0.8),
    DWARFIRON_GUARD("dwarfiron_guard", 1.4),
    ORC_FEMALE("orc_female", 0.9),
    ELFNIGHT("elfnight", 1),
    ELFBLOOD("elfblood", 1),
	// murloc
    MURLOC("murloc", 0.7),
    MURLOC_ELITE("murloc_elite", 0.8),
    MURLOC_BOSS("murloc_boss", 1.6),
    HUMAN_EVIL("human_evil", 0.9),
    TAUREN_EVIL("tauren_evil", 1.1),
	// necromancer
    NECROMANCER("necromancer", 0.9),
    NECROMANCER_SKELETON("necromancer_skeleton", 0.9),
	// elemental
    ELEMENTAL_FIRE("elemental_fire", 0.9),
    ELEMENTAL_WIND("elemental_wind", 0.9),
    GOLEM_REDSTONE("golem_redstone", 2),
    GOLEM_MAGMA("golem_magma", 1.2),
	// spider
    SPIDER_CHILD("spider_child", 0.6),
    SPIDER_NORMAL("spider_normal", 0.9),
    SPIDER_BIG("spider_big", 2),
    SPIDER_BOSS("spider_boss", 4),
	// other
	DRACTHYR("dracthyr", 1.4),
	PERFECT("perfect", 1);

	private final String		name;
	private final double		scale;

	FormType(String name, double scale) {
		this.name = name;
		this.scale = scale;
	}

	public String getName() {
		return name;
	}

	public double getScale() {
		return scale;
	}

	public static FormType fromRaceType(RaceType raceType) {
		if (raceType == null)
			return UNKNOWN;
		for (FormType type: FormType.values()) {
			if (type.getName().equals(raceType.getName()))
				return type;
		}
		return null;
	}

	public static FormType fromString(String formString) {
		if (formString == null)
			return UNKNOWN;
		for (FormType type: FormType.values()) {
			if (type.getName().equals(formString))
				return type;
		}
		return null;
	}
}
