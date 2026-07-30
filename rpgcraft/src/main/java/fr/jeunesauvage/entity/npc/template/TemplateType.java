package fr.jeunesauvage.entity.npc.template;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.entity.EntityType;

import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.race.RaceType;

public enum TemplateType {
	//                             name                                  hidename                                   entitytype             skin                      racetype         formtype                     stats                                        teams                    respawntime       healthByLevel damageByLevel attackrate spellrate speed                        speedcombat                        isBoss?
	// tauren
	TAUREN(                        "tauren",                        "Tauren",                        EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_VENDOR_POTION(          "tauren_vendor_potion",          "Vendor Potion Tauren",          EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_VENDOR_FOOD(            "tauren_vendor_food",            "Vendor Food Tauren",            EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_BLACKSMITH(             "tauren_blacksmith",             "BlackSmith Tauren",             EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_WARRIOR(    "tauren_classmaster_warrior",    "ClassMaster Warrior Tauren",    EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_ROGUE(      "tauren_classmaster_rogue",      "ClassMaster Rogue Tauren",      EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_HUNTER(     "tauren_classmaster_hunter",     "ClassMaster Hunter Tauren",     EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_DRACTHYR(   "tauren_classmaster_dracthyr",   "ClassMaster Dracthyr Tauren",   EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_PRIEST(     "tauren_classmaster_priest",     "ClassMaster Priest Tauren",     EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_CLASSMASTER_PYROMANCER( "tauren_classmaster_pyromancer", "ClassMaster Pyromancer Tauren", EntityType.PLAYER,      "tauren",            RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_GUARD(                  "tauren_guard",                  "Guard Tauren",                  EntityType.PLAYER,      "tauren_guard",      RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_SHAMAN_BLUE(            "tauren_shaman_blue",            "Shaman Tauren Blue",            EntityType.PLAYER,      "tauren_shamanblue", RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_SHAMAN_RED(             "tauren_shaman_red",             "Shaman Tauren Red",             EntityType.PLAYER,      "tauren_shamanred",  RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	TAUREN_KING(                   "tauren_king",                   "King Tauren",                   EntityType.PLAYER,      "tauren_king",       RaceType.TAUREN,    FormType.TAUREN,         null,                                  Set.of("horde"),     300, 30,       2,            1.5f,      10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	// dwar 
	DWARF(                         "dwarf",                         "Dwarf",                         EntityType.PLAYER,      "dwarf",             RaceType.DWARF,     FormType.DWARF,          null,                                  Set.of("alliance"),  180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	DWARF_VENDOR_FOOD(             "dwarf_vender_food",             "Vendor Food Dwarf",             EntityType.PLAYER,      "dwarf-trader",      RaceType.DWARF,     FormType.DWARF,          null,                                  Set.of("alliance"),  180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	DWARF_BLACKSMITH(              "dwarf_blacksmith",              "BlackSmith Dwarf",              EntityType.PLAYER,      "dwarf_trader",      RaceType.DWARF,     FormType.DWARF,          null,                                  Set.of("alliance"),  180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	DWARF_GUARD(                   "dwarf_guard",                   "Guard Dwarf",                   EntityType.PLAYER,      "dwarf_guard",       RaceType.DWARF,     FormType.DWARF,          null,                                  Set.of("alliance"),  180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	DWARF_KING(                    "dwarf_king",                    "King Dwarf",                    EntityType.PLAYER,      "dwarf_king",        RaceType.DWARF,     FormType.DWARF,          null,                                  Set.of("alliance"),  300, 30,       2,            1.5f,      10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	// murloc
	MURLOC(                        "murloc",                        "Swamp Murloc",                  EntityType.PLAYER,      "murloc",            RaceType.MURLOC,    FormType.MURLOC,         null,                                  Set.of("murloc"),    180, 10,       0.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	MURLOC_ELITE(                  "murloc_elite",                  "Elite Murloc",                  EntityType.PLAYER,      "murloc_elite",      RaceType.MURLOC,    FormType.MURLOC_ELITE,   null,                                  Set.of("murloc"),    180, 15,       1,            2f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	MURLOC_MRGL(                   "murloc_mrgl",                   "Mrgl The Oracle",               EntityType.PLAYER,      "murloc_boss",       RaceType.MURLOC,    FormType.MURLOC_BOSS,    null,                                  Set.of("murloc"),    300, 30,       2,            2f,        10f,      TraitSentinel.SPEED_DEFAULT, 1.3f,                              true),
	// tauren black
	TAUREN_BLACK(                  "tauren_black",                  "Black Tauren",                  EntityType.PLAYER,      "tauren_black",      RaceType.TAUREN,    FormType.TAUREN_EVIL,    null,                                  Set.of("black"),     180, 10,       0.5,          1.5f,      10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	PALPOUTINE(                    "palpoutine",                    "Palpoutine",                    EntityType.PLAYER,      "palpoutine",        RaceType.HUMAN,     FormType.HUMAN_EVIL,     null,                                  Set.of("black"),     300, 20,       1.5,          1f,        10f,      TraitSentinel.SPEED_DEFAULT, 1.3f,                              true),
	// elemental
	GOLEM_REDSTONE(                "golem_redstone",                "Redstone Golem",                EntityType.IRON_GOLEM,  null,                RaceType.ELEMENTAL, FormType.GOLEM_REDSTONE, Map.of(StatSecondary.PHYSICAL_ARMOR, 10), Set.of("elemental"), 300, 50,    2,            2f,        15f,      TraitSentinel.SPEED_DEFAULT, 1.2f,                              false),
	GOLEM_MAGMA(                   "golem_magma",                   "Sheared Admin",                 EntityType.IRON_GOLEM,  null,                RaceType.ELEMENTAL, FormType.GOLEM_MAGMA,    null,                                  Set.of("elemental"), 180, 30,       1.5,          2f,        10f,      TraitSentinel.SPEED_DEFAULT, 1.2f,                              false),
	// spider
	SPIDER_CHILD(                  "spider_child",                  "Wolf 3",                        EntityType.WOLF,        null,                RaceType.SPIDER,    FormType.SPIDER_CHILD,   null,                                  Set.of("spider"),    180, 3,        1,            1f,        10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	SPIDER_NORMAL(                 "spider_normal",                 "Spider 1",                      EntityType.SPIDER,      null,                RaceType.SPIDER,    FormType.SPIDER_NORMAL,  null,                                  Set.of("spider"),    180, 30,       1.5,          1.5f,      10f,      TraitSentinel.SPEED_DEFAULT, TraitSentinel.SPEEDCOMBAT_DEFAULT, false),
	SPIDER_BIG(                    "spider_big",                    "Spider 6",                      EntityType.SPIDER,      null,                RaceType.SPIDER,    FormType.SPIDER_BIG,     Map.of(StatSecondary.SPELL_ARMOR, 2),     Set.of("spider"),    300, 25,    1.5,          2f,        5f,       0.4f,                        1f,                                false),
	SPIDER_BOSS(                   "spider_boss",                   "Spider 5",                      EntityType.SPIDER,      null,                RaceType.SPIDER,    FormType.SPIDER_BOSS,    Map.of(StatSecondary.SPELL_ARMOR, 4),     Set.of("spider"),    600, 50,    3,            2f,        10f,      0.4f,                        1f,                                true),
	// pet
	PET_WOLF(                      "pet_wolf",                      "Kingbdogz",                     EntityType.WOLF,        null,                RaceType.ANIMAL,    FormType.ANIMAL,         null,                                  null,             0,   5,           0.2,          1.5f,      10f,      1.2f,                        1.6f,                              false);

	private final String				name;
	private final String				hideName;
	private final EntityType			entityType;
	private final String				skin;
	private final FormType				formType;
	private final RaceType				raceType;
	private final Map<String, Integer>	stats;
	private final Set<String>			teams;
	private final int					respawnTime;
	private final double				healthByLevel;
	private final double				damageByLevel;
	private final float					attackRate;
	private final float					spellRate;
	private final float					speed;
	private final float					speedCombat;
	private final boolean				isBoss;

	TemplateType(String name, String hideName, EntityType entityType, String skin, RaceType raceType, FormType formType, Map<StatSecondary, Integer> stats, Set<String> teams, int respawnTime, double healthByLevel, double damageByLevel, float attackRate, float spellRate, float speed, float speedCombat, boolean isBoss) {
		this.name = name;
		this.hideName = hideName;
		this.entityType = entityType;
		this.skin = skin;
		this.raceType = raceType;
		this.formType = formType;
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

	public String getSkin() {
		return skin;
	}

	public RaceType getRaceType() {
		return raceType;
	}

	public FormType getFormType() {
		return formType;
	}

	public Map<String, Integer> getStats(int level) {
		if (stats == null) return null;
		Map<String, Integer>	result = new HashMap<>(stats);
		for (Entry<String, Integer> e: result.entrySet()) {
			e.setValue(e.getValue() * level);
		}
		return result;
	}

	public Set<String> getTeams() {
		return teams;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public double getHealth(int level) {
		return level * healthByLevel;
	}

	public double getDamage(int level) {
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
		if (name == null) return null;
		for (TemplateType type: TemplateType.values()) {
			if (name.equals(type.getName()))
				return type;
		}
		return null;
	}
}
