package fr.jeunesauvage.entity.playercustom.classcustom;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum ClassType {
    BEGGAR("beggar", TextColor.fromHexString("#a48a8a")),
    PYROMANCER("pyromancer", TextColor.fromHexString("#88329b")),
    PRIEST("priest", TextColor.fromHexString("#ffffff")),
    ROGUE("rogue", TextColor.fromHexString("#e5ee3ae3")),
    HUNTER("hunter", TextColor.fromHexString("#1b8642")),
    DRACTHYR("dracthyr", TextColor.fromHexString("#e43d40")),
    WARRIOR("warrior", TextColor.fromHexString("#5B8DD9")),
    GOD("god", TextColor.fromHexString("#caa00a"));

    private final String    name;
    private final TextColor color;

    private ClassType(String name, TextColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

	public Component toComponent() {
		return Component.translatable("class.rpgcraft." + name).decorate(TextDecoration.BOLD);
	}

    public TextColor getColor() {
        return color;
    }

    public static ClassType fromString(String name) {
        if (name == null)
            return BEGGAR;
        for (ClassType type: ClassType.values()) {
            if (type.getName().equals(name))
                return type;
        }
        return BEGGAR;
    }
}
