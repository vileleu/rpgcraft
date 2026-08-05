package fr.jeunesauvage.entity.race;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

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

    private final String    name;
    private final TextColor color;

    private RaceType(String name, TextColor color) {
        this.name = name;
        this.color = color;
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
