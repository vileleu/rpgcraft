package fr.jeunesauvage.entitycustom.livingentitycustom.racecustom;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum RaceType {
    // race playable
    UNKNOWN("unknown", TextColor.fromHexString("#a48a8a"), false),
    TAUREN("tauren", TextColor.fromHexString("#825838"), true),
    ORC("orc", TextColor.fromHexString("#4A7C3F"), true),
    DWARF("dwarf", TextColor.fromHexString("#af873d"), true),
    HUMAN("human", TextColor.fromHexString("#5B8DD9"), true),
    // others rac, falsees
    DWARFIRON("dwarfiron", TextColor.fromHexString("#867f83"), true),
    IRON_GUARD("iron_guard", TextColor.fromHexString("#867f83"), true),
    ELFNIGHT("elfnight", TextColor.fromHexString("#7e4bbc"), true),
    ELFBLOOD("elfblood", TextColor.fromHexString("#dde411"), true),
    MURLOC("murloc", TextColor.fromHexString("#4b8445"), true),
    NECROMANCER("necromancer", TextColor.fromHexString("#4ad81f"), true),
    NECROMANCER_SKELETON("necromancer_skeleton", TextColor.fromHexString("#4ad81f"), false),
    ZOMBIE("zombie", TextColor.fromHexString("#2d3d28"), false),
    SKELETON("skeleton", TextColor.fromHexString("#2d3d28"), false),
    ELEMENTAL("elemental", TextColor.fromHexString("#ffffff"), false),
    SPIDER("spider", TextColor.fromHexString("#292727"), false),
    SCORPION("scorpion", TextColor.fromHexString("#c6ef97"), false),
    DEMON("demon", TextColor.fromHexString("#931313"), false),
    KODO("kodo", TextColor.fromHexString("#704322"), false),
    ANIMAL("animal", TextColor.fromHexString("#ffffff"), false);

    static public final NamespacedKey   KEY = new NamespacedKey(RpgCraft.name(), "race");
    private final String                name;
    private final TextColor             color;
    private final boolean               playable;

    private RaceType(String name, TextColor color, boolean playable) {
        this.name = name;
        this.color = color;
        this.playable = playable;
    }

    public String getName() {
        return this.name;
    }

    public boolean isPlayable() {
        return playable;
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
