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
	//                                name                                     hidename                                      entitytype                    racetype                       formtype                             classtype                 stats                                             teams                                      respawntime                     healthByLevel              damageByLevel       attackrate        spellrate                      speed                     speedcombat                     isBoss?
	DEFAULT(                          "default",                          "Default",                          EntityType.SHULKER_BULLET,    RaceType.UNKNOWN,              FormType.UNKNOWN,                    ClassType.BEGGAR,         null,                                      null,                                0,                       5,             0,                  0f,               0f,                            0.5f,                     1.4f,                           false),
	// tauren
	TAUREN(                           "tauren",                           "Tauren",                           EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_VENDOR_POTION(             "tauren_vendor_potion",             "Vendor Potion Tauren",             EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_VENDOR_FOOD(               "tauren_vendor_food",               "Vendor Food Tauren",               EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_BLACKSMITH(                "tauren_blacksmith",                "BlackSmith Tauren",                EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_DRACTHYR(      "tauren_classmaster_dracthyr",      "ClassMaster Dracthyr Tauren",      EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                     ClassType.DRACTHYR,       null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_PRIEST(        "tauren_classmaster_priest",        "ClassMaster Priest Tauren",        EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                     ClassType.PRIEST,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_PYROMANCER(    "tauren_classmaster_pyromancer",    "ClassMaster Pyromancer Tauren",    EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN,                     ClassType.PYROMANCER,     null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_GUARD(                     "tauren_guard",                     "Guard Tauren",                     EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_GUARD,               ClassType.WARRIOR,        null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_KING(                      "tauren_king",                      "King Tauren",                      EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_KING,                ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     300,               30,            2,                  1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// orc
	ORC(                              "orc",                              "Orc",                              EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC,                        ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_CLASSMASTER_WARRIOR(          "orc_classmaster_warrior",          "ClassMaster Warrior Orc",          EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC,                        ClassType.WARRIOR,        null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_CLASSMASTER_ROGUE(            "orc_classmaster_rogue",            "ClassMaster Rogue Orc",            EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC,                        ClassType.ROGUE,          null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_CLASSMASTER_HUNTER(           "orc_classmaster_hunter",           "ClassMaster Hunter Orc",           EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC,                        ClassType.HUNTER,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_DEMON(                        "orc_demon",                        "Demon Orc",                        EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_DEMON,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               15,            0.8,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_FEMALE(                       "orc_female",                       "Orc",                              EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_FEMALE,                 ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_VENDOR_FOOD(                  "orc_vender_food",                  "Vendor Food Orc",                  EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_TRADER,                 ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_GUARD(                        "orc_guard",                        "Guard Orc",                        EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_GUARD,                  ClassType.WARRIOR,        null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_FOREST(                       "orc_forest",                       "Forest Orc",                       EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_FOREST,                 ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_FATHER(                       "orc_father",                       "Magrash",                          EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_FATHER,                 ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_MOTHER(                       "orc_mother",                       "Khalash",                          EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_MOTHER,                 ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ORC_CHILD(                        "orc_child",                        "Go'el",                            EntityType.PLAYER,            RaceType.ORC,                  FormType.ORC_CHILD,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               5,             0.1,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// dwarf
	DWARF(                            "dwarf",                            "Dwarf",                            EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF,                      ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_CLASSMASTER_WARRIOR(        "dwarf_classmaster_warrior",        "ClassMaster Warrior Dwarf",        EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF,                      ClassType.WARRIOR,        null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_CLASSMASTER_HUNTER(         "dwarf_classmaster_hunter",         "ClassMaster Hunter Dwarf",         EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF,                      ClassType.HUNTER,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_ARCHER(                     "dwarf_archer",                     "Archer Dwarf",                     EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF,                      ClassType.HUNTER,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_VENDOR_FOOD(                "dwarf_vender_food",                "Vendor Food Dwarf",                EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_TRADER,               ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_BLACKSMITH(                 "dwarf_blacksmith",                 "BlackSmith Dwarf",                 EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_TRADER,               ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_GUARD(                      "dwarf_guard",                      "Guard Dwarf",                      EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_GUARD,                ClassType.WARRIOR,        null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARF_KING(                       "dwarf_king",                       "King Dwarf",                       EntityType.PLAYER,            RaceType.DWARF,                FormType.DWARF_KING,                 ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  300,               30,            2,                  1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// human
	HUMAN(                            "human",                            "Human",                            EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN,                      ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// dwarfiron
	DWARFIRON(                        "dwarfiron",                        "DwarfIron",                        EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_CLASSMASTER_PYROMANCER( "dwarfiron_classmaster_pyromancer", "ClassMaster Pyromancer DwarfIron", EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON,                  ClassType.PYROMANCER,     null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_VENDOR_FOOD(            "dwarfiron_vender_food",            "Vendor Food DwarfIron",            EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON_TRADER,           ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_BLACKSMITH(             "dwarfiron_blacksmith",             "BlackSmith DwarfIron",             EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON_BLACKSMITH,       ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_GUARD(                  "dwarfiron_guard",                  "Guard DwarfIron",                  EntityType.PLAYER,            RaceType.DWARFIRON_GUARD,      FormType.DWARFIRON_GUARD,            ClassType.WARRIOR,        null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_KING(                   "dwarfiron_king",                   "King DwarfIron",                   EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON_KING,             ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  300,               30,            2,                  1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	DWARFIRON_PYROMANCER(             "dwarfiron_pyromancer",             "Pyromancer DwarfIron",             EntityType.PLAYER,            RaceType.DWARFIRON,            FormType.DWARFIRON_PYROMANCER,       ClassType.PYROMANCER,     null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                1.5f,             10f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// murloc
	MURLOC(                           "murloc",                           "Swamp Murloc",                     EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC),                    180,               10,            0.3,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	MURLOC_ARCHER(                    "murloc_archer",                    "Archer Murloc",                    EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC),                    180,               10,            0.3,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	MURLOC_ELITE(                     "murloc_elite",                     "Elite Murloc",                     EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC_ELITE,               ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC),                    180,               15,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	MURLOC_MRGL(                      "murloc_mrgl",                      "Mrgl The Oracle",                  EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC_BOSS,                ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC),                    300,               30,            2,                  2f,               15f,                          FightTrait.SPEED_DEFAULT, 1.3f,                           true),
	MURLOC_NICE(                      "murloc_nice",                      "Nice Murloc",                      EntityType.PLAYER,            RaceType.MURLOC,               FormType.MURLOC_NICE,                ClassType.BEGGAR,         null,                                      Set.of(TeamType.MURLOC, TeamType.PLAYER),   180,               10,            0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// tauren blacK
	TAUREN_BLACK(                     "tauren_black",                     "Black Tauren",                     EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_BLACK,               ClassType.WARRIOR,        null,                                      Set.of(TeamType.BLACK),                     180,               10,            0.5,            	   1.5f,             15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	PALPOUTINE(                       "palpoutine",                       "Palpoutine",                       EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN_PALPOUTINE,           ClassType.PYROMANCER,     null,                                      Set.of(TeamType.BLACK),                     300,               30,            1.5,          	   1f,               15f,                          FightTrait.SPEED_DEFAULT, 1.3f,                           true),
	PALPOUTINE_CLONE(                 "palpoutine_clone",                 "Palpoutine Clone",                 EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN_PALPOUTINE,           ClassType.PYROMANCER,     null,                                      Set.of(TeamType.BLACK),                     0,                 10,            1.5,          	   1f,               15f,                          FightTrait.SPEED_DEFAULT, 1.3f,                           true),
	// desert
	DESERT_ROGUE(                     "desert_rogue",                     "Desert Rogue",                     EntityType.PLAYER,            RaceType.HUMAN,                FormType.HUMAN_ROGUE,                ClassType.ROGUE,          null,                                      Set.of(TeamType.DESERT),                    180,               10,            0.5,                2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_DESERT(                    "tauren_desert",                    "Desert Tauren",                    EntityType.PLAYER,            RaceType.TAUREN,               FormType.TAUREN_DESERT,              ClassType.WARRIOR,        null,                                      Set.of(TeamType.DESERT),                    180,               10,            0.5,                1.5f,             15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	SCORPION(                         "scorpion",                         "Scorpion",                         EntityType.SPIDER,            RaceType.SCORPION,             FormType.SCORPION,                   ClassType.BEGGAR,         null,                                      Set.of(TeamType.DESERT),                    180,               12,            0.5,                1.5f,             10f,                          0.5f,                     1.1f,                           false),
	// elfnight
	ELFNIGHT(                         "elfnight",                         "Elf Night",                        EntityType.PLAYER,            RaceType.ELFNIGHT,             FormType.ELFNIGHT,                   ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ILLIDAN(                          "illidan",                          "Illidan",                          EntityType.PLAYER,            RaceType.ELFNIGHT,             FormType.ILLIDAN,                    ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	MALFURION(                        "malfurion",                        "Malfurion",                        EntityType.PLAYER,            RaceType.ELFNIGHT,             FormType.MALFURION,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.ALLIANCE),                  180,               10,            0.5,                1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// elfblood
	ELFBLOOD(                         "elfblood",                         "Elf Blood",                        EntityType.PLAYER,            RaceType.ELFBLOOD,             FormType.ELFBLOOD,                   ClassType.BEGGAR,         null,                                      Set.of(TeamType.HORDE),                     180,               10,            0.5,                1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// elemental
	REDSTONE_GOLEM(                   "redstone_golem",                   "Redstone Golem",                   EntityType.IRON_GOLEM,        RaceType.ELEMENTAL,            FormType.REDSTONE_GOLEM,             ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 10),     Set.of(TeamType.ELEMENTAL),                 300,             50,           2,                  2f,               20f,                          FightTrait.SPEED_DEFAULT, 1.2f,                           true),
	ELEMENTAL_FIRE(                   "elemental_fire",                   "Elemental Fire",                   EntityType.BLAZE,             RaceType.ELEMENTAL,            FormType.ELEMENTAL_FIRE,             ClassType.BEGGAR,         null,                                      Set.of(TeamType.ELEMENTAL),                 180,               5,             0.2,                1.5f,              15f,                          0.7f,                     1f,                            false),
	ELEMENTAL_WIND(                   "elemental_wind",                   "Elemental Wind",                   EntityType.BREEZE,            RaceType.ELEMENTAL,            FormType.ELEMENTAL_WIND,             ClassType.BEGGAR,         Map.of(StatSecondary.DODGE, 4),               Set.of(TeamType.ELEMENTAL),                 180,             5,           0.2,                 1.5f,             5f,                           0.9f,                      1.1f,                          false),
	// spider
	SMALL_SPIDER(                     "small_spider",                     "Small Spider",                     EntityType.WOLF,              RaceType.SPIDER,               FormType.SMALL_SPIDER,               ClassType.BEGGAR,         null,                                      Set.of(TeamType.SPIDER),                    180,               3,             1,                 1f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	SPIDER(                           "spider",                           "Spider",                           EntityType.SPIDER,            RaceType.SPIDER,               FormType.SPIDER,                     ClassType.BEGGAR,         Map.of(StatSecondary.SPELL_ARMOR, 4),         Set.of(TeamType.SPIDER),                    180,             30,           1.5,               1.5f,             FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	BIG_SPIDER(                       "big_spider",                       "Big Spider",                       EntityType.SPIDER,            RaceType.SPIDER,               FormType.BIG_SPIDER,                 ClassType.BEGGAR,         Map.of(StatSecondary.SPELL_ARMOR, 6),         Set.of(TeamType.SPIDER),                    300,             25,           1.5,               2f,               10f,                          0.4f,                     1f,                             false),
	TARENTULA(                        "tarentula",                        "Tarentula",                        EntityType.SPIDER,            RaceType.SPIDER,               FormType.TARENTULA,                  ClassType.BEGGAR,         Map.of(StatSecondary.SPELL_ARMOR, 10),         Set.of(TeamType.SPIDER),                   600,             50,           2,                 2f,               15f,                          0.4f,                     1.2f,                           true),
	// necro
	NECROMANCER(                      "necromancer",                      "Necromancer",                      EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER,                ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                     180,             20,           1,                 1f,               15f,                          FightTrait.SPEED_DEFAULT, 1.2f,                           false),
	NECROMANCER_FACELESS(             "necromancer_faceless",             "Faceless Necromancer",             EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER_FACELESS,       ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                     180,             20,           1,                 1.5f,             15f,                          FightTrait.SPEED_DEFAULT, 1.2f,                           false),
	NECROMANCER_SKELETAL(             "necromancer_skeletal",             "Skeletal Necromancer",             EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER_SKELETAL,       ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                     180,             20,           1,                 2f,               15f,                          FightTrait.SPEED_DEFAULT, 1.2f,                           false),
	NECROMANCER_SKELETAL_GREY(        "necromancer_skeletal_grey",        "Skeletal Necromancer Grey",        EntityType.PLAYER,            RaceType.NECROMANCER,          FormType.NECROMANCER_SKELETAL_GREY,  ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO, TeamType.PLAYER),    180,             20,           1,                 2f,               15f,                          FightTrait.SPEED_DEFAULT, 1.2f,                           false),
	NECROMANCER_SKELETON(             "necromancer_skeleton",             "Skeleton Necromancer",             EntityType.PLAYER,            RaceType.NECROMANCER_SKELETON, FormType.NECROMANCER_SKELETON,       ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 100),    Set.of(TeamType.NECRO),                     180,             10,           0.6,               2f,               15f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	ZOMBIE(                           "zombie",                           "Zombie",                           EntityType.ZOMBIE,            RaceType.ZOMBIE,               FormType.ZOMBIE,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.NECRO),                     180,             10,             0.5,                2f,               FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, 1.1f,                             false),
	// forest
	LEAPER(                           "leaper",                           "Leaper",                           EntityType.RAVAGER,           RaceType.ELEMENTAL,            FormType.LEAPER,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.FOREST),                    180,               30,            1,                  2f,               20f,                         FightTrait.SPEED_DEFAULT, 1.5f,                           false),
	WHISPERER(                        "whisperer",                        "Whisperer",                        EntityType.EVOKER,            RaceType.ELEMENTAL,            FormType.WHISPERER,                  ClassType.BEGGAR,         null,                                      Set.of(TeamType.FOREST),                    180,               20,            0,                  0,                6f,                          0.5f,                     0.7f,                           false),
	FROZER(                           "frozer",                           "Frozer",                           EntityType.EVOKER,            RaceType.ELEMENTAL,            FormType.FROZER,                     ClassType.BEGGAR,         null,                                      Set.of(TeamType.FOREST),                    300,               30,            0,                  0,                10f,                         0.5f,                     0.7f,                           true),
	// demon
	DEMON(                            "demon",                            "Demon",                            EntityType.WITHER_SKELETON,   RaceType.DEMON,                FormType.DEMON,                      ClassType.BEGGAR,         null,                                      Set.of(TeamType.DEMON),                     180,               10,            2,                  1.5f,             5f,                          FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	MAGMA_GOLEM(                      "magma_golem",                      "Magma Golem",                      EntityType.IRON_GOLEM,        RaceType.ELEMENTAL,            FormType.MAGMA_GOLEM,                ClassType.BEGGAR,         Map.of(StatSecondary.PHYSICAL_ARMOR, 5),      Set.of(TeamType.ELEMENTAL, TeamType.DEMON), 180,             30,           1.5,                2f,              FightTrait.SPELLRATE_DEFAULT, FightTrait.SPEED_DEFAULT, 1.2f,                           false),
	ELEMENTAL_VOID(                   "elemental_void",                   "Elemental Void",                   EntityType.WITHER,            RaceType.ELEMENTAL,            FormType.ELEMENTAL_VOID,             ClassType.BEGGAR,         null,                                      Set.of(TeamType.ELEMENTAL, TeamType.DEMON), 180,               10,            2,                  1.5f,             5f,                          0.7f,                      1.2f,                           false),
	// dracthyr
	HUMAN_DRACTHYR_BLACK(             "human_dracthyr_black",             "Dracthyr Black Human",             EntityType.PLAYER,            RaceType.HUMAN,                FormType.DRACTHYR_BLACK,             ClassType.DRACTHYR,       null,                                      Set.of(TeamType.ALLIANCE),                  180,               20,            1,                  1.5f,             15f,                         FightTrait.SPEED_DEFAULT, FightTrait.SPEEDCOMBAT_DEFAULT, false),
	// pet
	PET_WOLF(                         "pet_wolf",                         "Wolf",                             EntityType.WOLF,              RaceType.ANIMAL,               FormType.ANIMAL,                     ClassType.BEGGAR,         null,                                      null,                                 0,                       10,            0.6,               1.5f,             FightTrait.SPELLRATE_DEFAULT, 1.2f,                     1.6f,                           false),
	PET_BRAISED(                      "pet_braised",                      "Elemental Fire",                   EntityType.BLAZE,             RaceType.ELEMENTAL,            FormType.ELEMENTAL_FIRE,             ClassType.BEGGAR,         null,                                      null,                                 0,                       10,            0.5,               1.5f,             15f,                          1.2f,                     1.2f,                           false);

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
