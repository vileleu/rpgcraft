package fr.jeunesauvage.entity.race;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum RaceType {
    // race playable
    UNKNOWN("unknown", TextColor.fromHexString("#a48a8a"), 1),
    TAUREN("tauren", TextColor.fromHexString("#825838"), 1.1),
    ORC("orc", TextColor.fromHexString("#4A7C3F"), 0.9),
    DWARF("dwarf", TextColor.fromHexString("#af873d"), 0.7),
    HUMAN("human", TextColor.fromHexString("#5B8DD9"), 0.9),
    // others races
    ANIMAL("animal", TextColor.fromHexString("#ffffff"), 0),
    DWARFIRON("dwarfiron", TextColor.fromHexString("#ffffff"), 0.7),
    DWARFIRON_GUARD("dwarfiron_guard", TextColor.fromHexString("#ffffff"), 1.4),
    ELFNIGHT("elfnight", TextColor.fromHexString("#ffffff"), 1),
    ELFBLOOD("elfblood", TextColor.fromHexString("#ffffff"), 1),
    MURLOC("murloc", TextColor.fromHexString("#ffffff"), 0.6),
    NECROMANCER("necromancer", TextColor.fromHexString("#ffffff"), 0.9),
    NECROMANCER_SKELETON("necromancer_skeleton", TextColor.fromHexString("#ffffff"), 0.9),
    ELEMENTAL("elemental", TextColor.fromHexString("#ffffff"), 0.9),
    SPIDER("spider", TextColor.fromHexString("#ffffff"), 0.5);

    private final String    name;
    private final TextColor color;
    private final double    scale;

    private RaceType(String name, TextColor color, double scale) {
        this.name = name;
        this.color = color;
        this.scale = scale;
    }

    public String getName() {
        return this.name;
    }

	public Component toComponent() {
		return Component.translatable("race.rpgcraft." + name).decorate(TextDecoration.BOLD);
	}

    public TextColor getColor() {
        return this.color;
    }

    public double getScale() {
        return scale;
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
