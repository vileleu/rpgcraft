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
    DWARFIRON("dwarfiron", TextColor.fromHexString("#867f83")),
    DWARFIRON_GUARD("dwarfiron_guard", TextColor.fromHexString("#867f83")),
    ELFNIGHT("elfnight", TextColor.fromHexString("#7e4bbc")),
    ELFBLOOD("elfblood", TextColor.fromHexString("#dde411")),
    MURLOC("murloc", TextColor.fromHexString("#4b8445")),
    NECROMANCER("necromancer", TextColor.fromHexString("#4ad81f")),
    NECROMANCER_SKELETON("necromancer_skeleton", TextColor.fromHexString("#4ad81f")),
    ELEMENTAL("elemental", TextColor.fromHexString("#ffffff")),
    SPIDER("spider", TextColor.fromHexString("#292727")),
    SCORPION("scorpion", TextColor.fromHexString("#c6ef97")),
    DEMON("demon", TextColor.fromHexString("#931313"));

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
