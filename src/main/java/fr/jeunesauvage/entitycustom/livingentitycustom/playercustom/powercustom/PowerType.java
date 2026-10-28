package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom;

import net.kyori.adventure.text.format.TextColor;

public enum PowerType {
    MANA("mana", TextColor.fromHexString("#511bda")),
    RAGE("rage", TextColor.fromHexString("#d32020")),
    ENERGY("energy", TextColor.fromHexString("#dcd04b"));

    private final String    name;
    private final TextColor color;

    PowerType(String name, TextColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

	public TextColor getColor() {
        return color;
    }
}
