package fr.jeunesauvage.entitycustom.livingentitycustom.team;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import net.kyori.adventure.text.Component;

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
		this.key = new NamespacedKey(RpgCraft.name(), "team/" + name);
	}

	public String getName() {
		return name;
	}

	public NamespacedKey getKey() {
		return key;
	}

	public Component toComponent() {
		return Component.text(name);
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
