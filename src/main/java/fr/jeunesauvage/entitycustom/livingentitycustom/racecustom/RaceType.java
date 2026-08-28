package fr.jeunesauvage.entitycustom.livingentitycustom.racecustom;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum RaceType {
    // race playable
    UNKNOWN("unknown", TextColor.fromHexString("#a48a8a")),
    TAUREN("tauren", TextColor.fromHexString("#825838")),
    ORC("orc", TextColor.fromHexString("#4A7C3F")),
    DWARF("dwarf", TextColor.fromHexString("#af873d")),
    HUMAN("human", TextColor.fromHexString("#5B8DD9")),
    // others races
    ANIMAL("animal", TextColor.fromHexString("#ffffff")),
    DWARFIRON("dwarfiron", TextColor.fromHexString("#ffffff")),
    DWARFIRON_GUARD("dwarfiron_guard", TextColor.fromHexString("#ffffff")),
    ELFNIGHT("elfnight", TextColor.fromHexString("#ffffff")),
    ELFBLOOD("elfblood", TextColor.fromHexString("#ffffff")),
    MURLOC("murloc", TextColor.fromHexString("#ffffff")),
    NECROMANCER("necromancer", TextColor.fromHexString("#ffffff")),
    NECROMANCER_SKELETON("necromancer_skeleton", TextColor.fromHexString("#ffffff")),
    ELEMENTAL("elemental", TextColor.fromHexString("#ffffff")),
    SPIDER("spider", TextColor.fromHexString("#ffffff")),
    SCORPION("scorpion", TextColor.fromHexString("#ffffff"));

    static public final NamespacedKey   KEY = new NamespacedKey(RpgCraft.name(), "race");
    private final String                name;
    private final TextColor             color;

    private RaceType(String name, TextColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

	public Component toComponent() {
		return Component.translatable("race.rpgcraft." + name).color(color);
	}

    public static RaceType fromString(String name) {
        if (name == null)
            return UNKNOWN;
        for (RaceType type: RaceType.values()) {
            if (type.getName().equals(name))
                return type;
        }
        return UNKNOWN;
    }
}
