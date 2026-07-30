package fr.jeunesauvage.entity.team;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;

public enum TeamType {
	PLAYER("player"),
	HORDE("horde"),
	ALLIANCE("alliance"),
	MURLOC("murloc"),
	DESERT("desert"),
	BLACK("black"),
	FOREST("forest"),
	NECRO("necro"),
	SPIDER("spider"),
	ELEMENTAL("elemental");

	private final String		name;
	private final NamespacedKey	key;

	TeamType(String name) {
		this.name = name;
		this.key = new NamespacedKey(RpgCraft.name(), "team_" + name);
	}

	public String getName() {
		return name;
	}

	public NamespacedKey getKey() {
		return key;
	}

	public static TeamType fromString(String name) {
        if (name == null)
            return null;
		for (TeamType type: TeamType.values()) {
			if (type.getName().equals(name))
        		return type;
		}
        return null;
	}
}
