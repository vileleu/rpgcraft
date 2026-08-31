package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.entity.EntityType;

import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait.FightTrait;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;


public enum TemplateType {
	//                             name                                  hidename                                   entitytype                    racetype                       formtype                          classtype                 stats                                             teams                                     respawntime                     healthByLevel              damageByLevel       attackrate        spellrate                      speed                     speedcombat                     isBoss?
	DEFAULT(                       "default",                       "Default",                       EntityType.SHULKER_BULLET,    RaceType.UNKNOWN,              FormType.UNKNOWN,                 ClassType.BEGGAR,         null,                                      null,                               0,                       5,             0,                  0f,               0f,                            0.5f,                       1.4f,                         false),
	// tauren
	TAUREN(                        "tauren",                        "Tauren",                        EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_VENDOR_POTION(          "tauren_vendor_potion",          "Vendor Potion Tauren",          EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_VENDOR_FOOD(            "tauren_vendor_food",            "Vendor Food Tauren",            EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_BLACKSMITH(             "tauren_blacksmith",             "BlackSmith Tauren",             EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_WARRIOR(    "tauren_classmaster_warrior",    "ClassMaster Warrior Tauren",    EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.WARRIOR,        null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_ROGUE(      "tauren_classmaster_rogue",      "ClassMaster Rogue Tauren",      EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.ROGUE,          null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_HUNTER(     "tauren_classmaster_hunter",     "ClassMaster Hunter Tauren",     EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.HUNTER,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_DRACTHYR(   "tauren_classmaster_dracthyr",   "ClassMaster Dracthyr Tauren",   EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.DRACTHYR,       null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_PRIEST(     "tauren_classmaster_priest",     "ClassMaster Priest Tauren",     EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.PRIEST,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_PYROMANCER( "tauren_classmaster_pyromancer", "ClassMaster Pyromancer Tauren", EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.PYROMANCER,     null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_GUARD(                  "tauren_guard",                  "Guard Tauren",                  EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_GUARD,            ClassType.WARRIOR,        null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_SHAMAN_BLUE(            "tauren_shaman_blue",            "Shaman Tauren Blue",            EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.PRIEST,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_SHAMAN_RED(             "tauren_shaman_red",             "Shaman Tauren Red",             EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                  ClassType.PRIEST,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_KING(                   "tauren_king",                   "King Tauren",                   EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_KING,             ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    300,               30,            2,                  1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// orc
	ORC(                          "orc",                            "Orc",                           EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.6,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_FEMALE(                   "orc_female",                     "Orc",                           EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_FEMALE,              ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.6,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// dwarf
	DWARF(                         "dwarf",                         "Dwarf",                         EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF,                   ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_VENDOR_FOOD(             "dwarf_vender_food",             "Vendor Food Dwarf",             EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_TRADER,            ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_BLACKSMITH(              "dwarf_blacksmith",              "BlackSmith Dwarf",              EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_TRADER,            ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_GUARD(                   "dwarf_guard",                   "Guard Dwarf",                   EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_GUARD,             ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_KING(                    "dwarf_king",                    "King Dwarf",                    EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_KING,              ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 300,               30,            2,                  1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// human
	HUMAN(                         "human",                         "Human",                         EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN,                   ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// dwarfiron
	DWARFIRON(                     "dwarfiron",                     "DwarfIron",                     EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON,               ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_VENDOR_FOOD(         "dwarfiron_vender_food",         "Vendor Food DwarfIron",         EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON_TRADER,        ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_BLACKSMITH(          "dwarfiron_blacksmith",          "BlackSmith DwarfIron",          EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON_BLACKSMITH,    ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_GUARD(               "dwarfiron_guard",               "Guard DwarfIron",               EntityType.PLAYER,            RaceType.DWARFIRON_GUARD,      FormType.DWARFIRON_GUARD,         ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_KING(                "dwarfiron_king",                "King DwarfIron",                EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON_KING,          ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 300,               30,            2,                  1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// murloc
	MURLOC(                        "murloc",                        "Swamp Murloc",                  EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC),                   180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	MURLOC_ELITE(                  "murloc_elite",                  "Elite Murloc",                  EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC_ELITE,            ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC),                   180,               15,            1,                  2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	MURLOC_MRGL(                   "murloc_mrgl",                   "Mrgl The Oracle",               EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC_BOSS,             ClassType.PRIEST,         null,                                      Set.of(TeamType.MURLOC),                   300,               30,            2,                  2f,               15f,                          FightTrait.SPEED_DEFAULT, 1.3f,                           true),
	MURLOC_NICE(                   "murloc_nice",                   "Nice Murloc",                   EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC_NICE,             ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC, TeamType.PLAYER),  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// tauren blacK
	TAUREN_BLACK(                  "tauren_black",                  "Black Tauren",                  EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_BLACK,            ClassType.WARRIOR,        null,                                      Set.of(TeamType.BLACK),                    180,               10,            0.5,            	   1.5f,             15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	PALPOUTINE(                    "palpoutine",                    "Palpoutine",                    EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN_PALPOUTINE,        ClassType.PYROMANCER,     null,                                      Set.of(TeamType.BLACK),                    300,               30,            1.5,          	   1f,               15f,                          FightTrait.SPEED_DEFAULT, 1.3f,                           true),
	PALPOUTINE_CLONE(              "palpoutine_clone",              "Palpoutine Clone",              EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN_PALPOUTINE,        ClassType.PYROMANCER,     null,                                      Set.of(TeamType.BLACK),                    0,                 20,            1.5,          	   1f,               15f,                          FightTrait.SPEED_DEFAULT, 1.3f,                           true),
	// desert
	HUMAN_ROGUE(                   "human_rogue",                   "Rogue Human",                   EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN_ROGUE,             ClassType.ROGUE,          null,                                      Set.of(TeamType.DESERT),                   180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_DESERT(                 "tauren_desert",                 "Desert Tauren",                 EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_DESERT,           ClassType.WARRIOR,        null,                                      Set.of(TeamType.DESERT),                   180,               10,            0.5,                1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	SCORPION(                      "scorpion",                      "Spider 7",                      EntityType.SPIDER,            RaceType.SCORPION,             FormType.SCORPION,                ClassType.BEGGAR,         null,                                      Set.of(TeamType.DESERT),                   180,               12,            0.5,                1.5f,             10f,                          0.5f,                     1.1f,                           false),
	// elfnight
	ELFNIGHT(                      "elfnight",                      "Elf Night",                     EntityType.PLAYER,            RaceType.ELFNIGHT,             FormType.ELFNIGHT,                ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                 180,               10,            0.5,                1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// elfblood
	ELFBLOOD(                      "elfblood",                      "Elf Blood",                     EntityType.PLAYER,            RaceType.ELFBLOOD,             FormType.ELFBLOOD,                ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                    180,               10,            0.5,                1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// elemental
	GOLEM_REDSTONE(                "golem_redstone",                "Redstone Golem",                EntityType.IRON_GOLEM,        RaceType.ELEMENTAL,            FormType.GOLEM_REDSTONE,          ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 10),     Set.of(TeamType.ELEMENTAL),                300,             50,           2,                  2f,               20f,                          FightTrait.SPEED_DEFAULT, 1.2f,                           true),
	GOLEM_MAGMA(                   "golem_magma",                   "Sheared Admin",                 EntityType.IRON_GOLEM,        RaceType.ELEMENTAL,            FormType.GOLEM_MAGMA,             ClassType.BEGGAR,         null,                                      Set.of(TeamType.ELEMENTAL),                180,               30,            1.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, 1.2f,                           false),
	ELEMENTAL_FIRE(                "elemental_fire",                "Blaze",                         EntityType.BLAZE,             RaceType.ELEMENTAL,            FormType.ELEMENTAL_FIRE,          ClassType.BEGGAR,         null,                                      Set.of(TeamType.ELEMENTAL),                180,               5,             0.2,                1.5f,             15f,                          1.2f,                     1.2f,                           false),
	ELEMENTAL_WIND(                "elemental_wind",                "Breeze",                        EntityType.BREEZE,            RaceType.ELEMENTAL,            FormType.ELEMENTAL_WIND,          ClassType.BEGGAR,         null,                                      Set.of(TeamType.ELEMENTAL),                180,               5,             0.2,                1.5f,             15f,                          1.2f,                     1.2f,                           false),
	// spider
	SPIDER_CHILD(                  "spider_child",                  "Wolf 3",                        EntityType.WOLF,              RaceType.SPIDER,               FormType.SPIDER_CHILD,            ClassType.BEGGAR,         null,                                      Set.of(TeamType.SPIDER),                   180,               3,             1,                 1f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	SPIDER_NORMAL(                 "spider_normal",                 "Spider 1",                      EntityType.SPIDER,            RaceType.SPIDER,               FormType.SPIDER_NORMAL,           ClassType.BEGGAR,         Map.of(StatSecondary.SPELL_ARMOR, 4),         Set.of(TeamType.SPIDER),                   180,             30,           1.5,               1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	SPIDER_BIG(                    "spider_big",                    "Spider 6",                      EntityType.SPIDER,            RaceType.SPIDER,               FormType.SPIDER_BIG,              ClassType.BEGGAR,         Map.of(StatSecondary.SPELL_ARMOR, 6),         Set.of(TeamType.SPIDER),                   300,             25,           1.5,               2f,               10f,                          0.4f,                     1f,                             false),
	SPIDER_BOSS(                   "spider_boss",                   "Spider 5",                      EntityType.SPIDER,            RaceType.SPIDER,               FormType.SPIDER_BOSS,             ClassType.BEGGAR,         Map.of(StatSecondary.SPELL_ARMOR, 10),         Set.of(TeamType.SPIDER),                  600,             50,           3,                 2f,               15f,                          0.4f,                     1.2f,                           true),
	// necro
	NECROMANCER(                   "necromancer",                   "Necromancer",                   EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER,             ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                    180,             20,           1,                 1f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, 1.1f,                           false),
	NECROMANCER_NOFACE(            "necromancer_noface",            "Faceless Necromancer",          EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER_NOFACE,      ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                    180,             20,           1.5,               1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, 1.1f,                           false),
	NECROMANCER_BONE(              "necromancer_bone",              "Bone Necromancer",              EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER_BONE,        ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                    180,             20,           1.5,               2f,               5f,                           FightTrait.SPEED_DEFAULT, 1.1f,                           false),
	NECROMANCER_BONEGREY(          "necromancer_bonegrey",          "Bone Grey Necromancer",         EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER_BONEGREY,    ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                    180,             20,           3,                 2f,               15f,                          FightTrait.SPEED_DEFAULT, 1.1f,                           false),
	NECROMANCER_SKELETON(          "necromancer_skeleton",          "Skeleton Necromancer",          EntityType.PLAYER,            RaceType.NECROMANCER_SKELETON, FormType.NECROMANCER_SKELETON,    ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                    180,             10,           3,                 2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// forest
	LEAPER(                        "leaper",                        "Ravager 2",                     EntityType.RAVAGER,           RaceType.ELEMENTAL,            FormType.LEAPER,                  ClassType.ROGUE,          null,                                      Set.of(TeamType.FOREST),                   180,               30,            1,                  2f,               20f,                         FightTrait.SPEED_DEFAULT, 1.5f,                           false),
	WHISPERER(                     "whisperer",                     "Whisperer",                     EntityType.EVOKER,            RaceType.ELEMENTAL,            FormType.WHISPERER,               ClassType.WARRIOR,        null,                                      Set.of(TeamType.FOREST),                   180,               20,            0,                  0,                5f,                          0.5f,                     0.8f,                           false),
	// pet
	PET_WOLF(                      "pet_wolf",                      "Kingbdogz",                     EntityType.WOLF,              RaceType.ANIMAL,               FormType.ANIMAL,                  ClassType.BEGGAR,         null,                                      null,                                0,                       5,             0.2,               1.5f,             FightTrait.SPELLRATE_DEFAULT, 1.2f,                     1.6f,                           false),
	PET_BRAISED(                   "pet_braised",                   "Blaze",                         EntityType.BLAZE,             RaceType.ELEMENTAL,            FormType.ELEMENTAL_FIRE,          ClassType.BEGGAR,         null,                                      null,                                0,                       5,             0.2,               1.5f,             15f,                          1.2f,                     1.2f,                           false);

	private final String				name;
	private final String				hideName;
	private final EntityType			entityType;
	private final RaceType				raceType;
	private final FormType				formType;
	private final ClassType				classType;
	private final Map<String, Integer>	stats;
	private final Set<TeamType>			teams;
	private final int					respawnTime;
	private final double				healthByLevel;
	private final double				damageByLevel;
	private final float					attackRate;
	private final float					spellRate;
	private final float					speed;
	private final float					speedCombat;
	private final boolean				isBoss;

	TemplateType(String name, String hideName, EntityType entityType, RaceType raceType, FormType formType, ClassType classType, Map<StatSecondary, Integer> stats, Set<TeamType> teams, int respawnTime, double healthByLevel, double damageByLevel, float attackRate, float spellRate, float speed, float speedCombat, boolean isBoss) {
		this.name = name;
		this.hideName = hideName;
		this.entityType = entityType;
		this.raceType = raceType;
		this.formType = formType;
		this.classType = classType;
		if (stats != null && !stats.isEmpty()) {
			this.stats = new HashMap<>();
			for (Entry<StatSecondary, Integer> entry: stats.entrySet()) {
				this.stats.put(entry.getKey().getName(), entry.getValue());
			}
		}
		else
			this.stats = null;
		this.teams = teams;
		this.respawnTime = respawnTime;
		this.healthByLevel = healthByLevel;
		this.damageByLevel = damageByLevel;
		this.attackRate = attackRate;
		this.spellRate = spellRate;
		this.speed = speed;
		this.speedCombat = speedCombat;
		this.isBoss = isBoss;
	}

	public String getName() {
		return name;
	}

	public String getHideName() {
		return hideName;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public RaceType getRaceType() {
		return raceType;
	}

	public FormType getFormType() {
		return formType;
	}

	public ClassType getClassType() {
		return classType;
	}

	public Map<String, Integer> getStats(int level) {
		if (stats == null) return null;
		Map<String, Integer>	result = new HashMap<>(stats);
		result.values().forEach(v -> v *= level);
		return result;
	}

	public Set<TeamType> getTeams() {
		return teams;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public double getHealth(int level) {
		if (level <= 0) level = 1;
		return level * healthByLevel;
	}

	public double getDamage(int level) {
		if (level <= 0) level = 1;
		return level * damageByLevel;
	}

	public float getAttackRate() {
		return attackRate;
	}

	public float getSpellRate() {
		return spellRate;
	}

	public float getSpeed() {
		return speed;
	}
	
	public float getSpeedCombat() {
		return speedCombat;
	}

	public boolean isBoss() {
		return isBoss;
	}

	public static TemplateType fromString(String name) {
		if (name == null) return TemplateType.DEFAULT;
		for (TemplateType type: TemplateType.values()) {
			if (name.equals(type.getName()))
				return type;
		}
		return TemplateType.DEFAULT;
	}
}
