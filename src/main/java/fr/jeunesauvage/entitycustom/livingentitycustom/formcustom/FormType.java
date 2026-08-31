package fr.jeunesauvage.entitycustom.livingentitycustom.formcustom;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;

public enum FormType {
    UNKNOWN("unknown", 1, FormTypeSkin.UNKNOWN),
	// tauren
    TAUREN("tauren", 1.1, FormTypeSkin.TAUREN),
    TAUREN_GUARD("tauren_guard", 1.1, FormTypeSkin.TAUREN_GUARD),
    TAUREN_SHAMAN_BLUE("tauren_shaman_blue", 1.1, FormTypeSkin.TAUREN_SHAMAN_BLUE),
    TAUREN_SHAMAN_RED("tauren_shaman_red", 1.1, FormTypeSkin.TAUREN_SHAMAN_RED),
    TAUREN_KING("tauren_king", 1.1, FormTypeSkin.TAUREN_KING),
	TAUREN_DESERT("tauren_desert", 1.1, FormTypeSkin.TAUREN_DESERT),
	TAUREN_BLACK("tauren_black", 1.1, FormTypeSkin.TAUREN_BLACK),
	// dwarf
    DWARF("dwarf", 0.7, FormTypeSkin.DWARF),
    DWARF_GUARD("dwarf_guard", 0.7, FormTypeSkin.DWARF_GUARD),
    DWARF_TRADER("dwarf_trader", 0.7, FormTypeSkin.DWARF_TRADER),
    DWARF_REDHEAD("dwarf_redhead", 0.7, FormTypeSkin.DWARF_REDHEAD),
    DWARF_HUNTER("dwarf_hunter", 0.7, FormTypeSkin.DWARF_HUNTER),
    DWARF_KING("dwarf_king", 0.7, FormTypeSkin.DWARF_KING),
	// orc
    ORC("orc", 0.9, FormTypeSkin.ORC),
    ORC_FEMALE("orc_female", 0.9, FormTypeSkin.ORC_FEMALE),
	// human
    HUMAN("human", 0.9, FormTypeSkin.HUMAN),
    HUMAN_ROGUE("human_rogue", 0.9, FormTypeSkin.HUMAN_ROGUE),
    HUMAN_PALPOUTINE("human_palpoutine", 0.9, FormTypeSkin.HUMAN_PALPOUTINE),
	// dwarfiron
    DWARFIRON("dwarfiron", 0.7, FormTypeSkin.DWARFIRON),
    DWARFIRON_BLACKSMITH("dwarfiron_blacksmith", 0.7, FormTypeSkin.DWARFIRON_BLACKSMITH),
    DWARFIRON_TRADER("dwarfiron_trader", 0.7, FormTypeSkin.DWARFIRON_TRADER),
    DWARFIRON_REDHEADBEARD("dwarfiron_redheadbeard", 0.7, FormTypeSkin.DWARFIRON_REDHEADBEARD),
    DWARFIRON_GUY("dwarfiron_guy", 0.7, FormTypeSkin.DWARFIRON_GUY),
    DWARFIRON_KING("dwarfiron_king", 0.7, FormTypeSkin.DWARFIRON_KING),
    DWARFIRON_GUARD("dwarfiron_guard", 1.4, FormTypeSkin.DWARFIRON_GUARD),
	// elfnight
    ELFNIGHT("elfnight", 1, FormTypeSkin.ELFNIGHT),
	// elfblood
    ELFBLOOD("elfblood", 1, FormTypeSkin.ELFBLOOD),
	// murloc
    MURLOC("murloc", 0.7, FormTypeSkin.MURLOC),
    MURLOC_ELITE("murloc_elite", 0.8, FormTypeSkin.MURLOC_ELITE),
    MURLOC_BOSS("murloc_boss", 1.6, FormTypeSkin.MURLOC_BOSS),
    MURLOC_NICE("murloc_nice", 1.6, FormTypeSkin.MURLOC_NICE),
	// necromancer
    NECROMANCER("necromancer", 0.9, FormTypeSkin.NECROMANCER),
    NECROMANCER_NOFACE("necromancer_noface", 0.9, FormTypeSkin.NECROMANCER_NOFACE),
    NECROMANCER_BONE("necromancer_bone", 0.9, FormTypeSkin.NECROMANCER_BONE),
    NECROMANCER_BONEGREY("necromancer_bonegrey", 0.9, FormTypeSkin.NECROMANCER_BONEGREY),
    NECROMANCER_SKELETON("necromancer_skeleton", 0.9, FormTypeSkin.NECROMANCER_SKELETON),
	// elemental
    ELEMENTAL_FIRE("elemental_fire", 0.9, FormTypeSkin.ELEMENTAL_FIRE),
    ELEMENTAL_WIND("elemental_wind", 0.9, FormTypeSkin.ELEMENTAL_WIND),
    GOLEM_MAGMA("golem_magma", 1.2, FormTypeSkin.GOLEM_MAGMA),
    GOLEM_REDSTONE("golem_redstone", 2, FormTypeSkin.GOLEM_REDSTONE),
	// spider
    SPIDER_CHILD("spider_child", 0.6, FormTypeSkin.SPIDER_CHILD),
    SPIDER_NORMAL("spider_normal", 0.9, FormTypeSkin.SPIDER_NORMAL),
    SPIDER_BIG("spider_big", 2, FormTypeSkin.SPIDER_BIG),
    SPIDER_BOSS("spider_boss", 4, FormTypeSkin.SPIDER_BOSS),
	// scorpion
    SCORPION("scorpion", 1.2, FormTypeSkin.SCORPION),
	// forest
	LEAPER("leaper", 1.5, FormTypeSkin.LEAPER),
	WHISPERER("whisperer", 1, FormTypeSkin.WHISPERER),
	// others
	ANIMAL("animal", 1, FormTypeSkin.ANIMAL),
	DRACTHYR_BLACK("dracthyr_black", 1.4, FormTypeSkin.DRACTHYR_BLACK),
	DRACTHYR_RED("dracthyr_red", 1.4, FormTypeSkin.DRACTHYR_RED);

    static public final NamespacedKey   KEY = new NamespacedKey(RpgCraft.name(), "form");
	private final String				name;
	private final double				scale;
	private final FormTypeSkin			formTypeSkin;

	FormType(String name, double scale, FormTypeSkin formTypeSkin) {
		this.name = name;
		this.scale = scale;
		this.formTypeSkin = formTypeSkin;
	}

	public String getName() {
		return name;
	}

	public double getScale() {
		return scale;
	}

	public FormTypeSkin getFormTypeSkin() {
		return formTypeSkin;
	}

	public static FormType fromRaceType(RaceType raceType) {
		if (raceType == null)
			return UNKNOWN;
		for (FormType type: FormType.values()) {
			if (type.getName().equals(raceType.getName()))
				return type;
		}
		return UNKNOWN;
	}

	public static FormType fromString(String formString) {
		if (formString == null)
			return UNKNOWN;
		for (FormType type: FormType.values()) {
			if (type.getName().equals(formString))
				return type;
		}
		return UNKNOWN;
	}
}
