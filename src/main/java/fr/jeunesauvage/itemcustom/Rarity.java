package fr.jeunesauvage.itemcustom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum Rarity {
    POOR(1, "poor", NamedTextColor.GRAY),
    COMMON(2, "common", NamedTextColor.WHITE),
    UNCOMMON(3, "uncommon", NamedTextColor.GREEN),
    RARE(4, "rare", NamedTextColor.DARK_BLUE),
    EPIC(5, "epic", NamedTextColor.DARK_PURPLE),
    LEGENDARY(6, "legendary", NamedTextColor.GOLD);

    private final int       number;
    private final String    name;
    private final TextColor color;
    public static final int LEVEL_MAX = 6;

    Rarity(int number, String name, TextColor color) {
        this.number = number;
        this.name  = name;
        this.color = color;
    }

	public int getNumber() {
		return number;
	}

    public String getName() {
        return name;
    }

    public TextColor getColor() {
        return color;
    }

    public Component toComponent() {
        return Component.translatable("rarity.rpgcraft." + name).color(color);
    }

    public int getLevel() {
        return number * 10;
    }

    public static Rarity fromInt(int number) {
		for (Rarity type: Rarity.values()) {
			if (type.getNumber() == number)
        		return type;
		}
		return POOR;
    }

    public static Rarity fromLevel(int level) {
        level /= 10;
		for (Rarity type: Rarity.values()) {
			if (type.getNumber() == level)
        		return type;
		}
		return POOR;
    }
}
