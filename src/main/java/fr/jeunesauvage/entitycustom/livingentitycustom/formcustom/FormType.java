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
    ORC_DEMON("orc_demon", 0.9, FormTypeSkin.ORC_DEMON),
    ORC_FEMALE("orc_female", 0.9, FormTypeSkin.ORC_FEMALE),
	ORC_TRADER("orc_trader", 0.9, FormTypeSkin.ORC_TRADER),
	ORC_GUARD("orc_guard", 0.9, FormTypeSkin.ORC_GUARD),
	ORC_FOREST("orc_forest", 0.9, FormTypeSkin.ORC_FOREST),
    ORC_FATHER("orc_father", 0.9, FormTypeSkin.ORC_FATHER),
    ORC_MOTHER("orc_mother", 0.9, FormTypeSkin.ORC_MOTHER),
    ORC_CHILD("orc_child", 0.6, FormTypeSkin.ORC_CHILD),
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
    DWARFIRON_PYROMANCER("dwarfiron_pyromancer", 0.7, FormTypeSkin.DWARFIRON_PYROMANCER),
	// elfnight
    ELFNIGHT("elfnight", 1, FormTypeSkin.ELFNIGHT),
    ILLIDAN("elfnight", 1, FormTypeSkin.ILLIDAN),
    MALFURION("elfnight", 1, FormTypeSkin.MALFURION),
	// elfblood
    ELFBLOOD("elfblood", 1, FormTypeSkin.ELFBLOOD),
	// murloc
    MURLOC("murloc", 0.7, FormTypeSkin.MURLOC),
    MURLOC_ELITE("murloc_elite", 0.8, FormTypeSkin.MURLOC_ELITE),
    MURLOC_BOSS("murloc_boss", 1.6, FormTypeSkin.MURLOC_BOSS),
    MURLOC_NICE("murloc_nice", 0.7, FormTypeSkin.MURLOC_NICE),
	// necromancer
    NECROMANCER("necromancer", 0.9, FormTypeSkin.NECROMANCER),
    NECROMANCER_FACELESS("necromancer_faceless", 1.2, FormTypeSkin.NECROMANCER_FACELESS),
    NECROMANCER_SKELETAL("necromancer_skeletal", 0.9, FormTypeSkin.NECROMANCER_SKELETAL),
    NECROMANCER_SKELETAL_GREY("necromancer_skeletal_grey", 0.9, FormTypeSkin.NECROMANCER_SKELETAL_GREY),
    NECROMANCER_SKELETON("necromancer_skeleton", 0.9, FormTypeSkin.NECROMANCER_SKELETON),
    ZOMBIE("zombie", 0.9, FormTypeSkin.ZOMBIE),
	// elemental
    ELEMENTAL_FIRE("elemental_fire", 0.9, FormTypeSkin.ELEMENTAL_FIRE),
    ELEMENTAL_WIND("elemental_wind", 0.9, FormTypeSkin.ELEMENTAL_WIND),
    REDSTONE_GOLEM("redstone_golem", 2, FormTypeSkin.REDSTONE_GOLEM),
	// spider
    SMALL_SPIDER("small_spider", 0.6, FormTypeSkin.SMALL_SPIDER),
    SPIDER("spider", 0.9, FormTypeSkin.SPIDER),
    BIG_SPIDER("big_spider", 2, FormTypeSkin.BIG_SPIDER),
    TARENTULA("spider_boss", 4, FormTypeSkin.TARENTULA),
	// scorpion
    SCORPION("scorpion", 1.2, FormTypeSkin.SCORPION),
	// forest
	LEAPER("leaper", 1.2, FormTypeSkin.LEAPER),
	WHISPERER("whisperer", 1, FormTypeSkin.WHISPERER),
	FROZER("frozer", 2, FormTypeSkin.FROZER),
	// demon
	DEMON("demon", 1.2, FormTypeSkin.DEMON),
    MAGMA_GOLEM("magla_golem", 1.2, FormTypeSkin.MAGMA_GOLEM),
	ELEMENTAL_VOID("elemental_void", 1.5, FormTypeSkin.ELEMENTAL_VOID),
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
