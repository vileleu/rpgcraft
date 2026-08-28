package fr.jeunesauvage.entitycustom.livingentitycustom.classcustom;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public enum ClassType {
    BEGGAR("beggar", TextColor.fromHexString("#a48a8a"), PowerType.MANA),
    PYROMANCER("pyromancer", TextColor.fromHexString("#88329b"), PowerType.MANA),
    PRIEST("priest", TextColor.fromHexString("#ffffff"), PowerType.MANA),
    ROGUE("rogue", TextColor.fromHexString("#e5ee3ae3"), PowerType.ENERGY),
    HUNTER("hunter", TextColor.fromHexString("#1b8642"), PowerType.MANA),
    DRACTHYR("dracthyr", TextColor.fromHexString("#e43d40"), PowerType.MANA),
    WARRIOR("warrior", TextColor.fromHexString("#5B8DD9"), PowerType.RAGE),
    GOD("god", TextColor.fromHexString("#caa00a"), PowerType.MANA);

    static public final NamespacedKey   KEY = new NamespacedKey(RpgCraft.name(), "class");
    private final String                name;
    private final TextColor             color;
    private final PowerType             powerType;

    private ClassType(String name, TextColor color, PowerType powerType) {
        this.name = name;
        this.color = color;
        this.powerType = powerType;
    }

    public String getName() {
        return this.name;
    }

	public Component toComponent() {
		return Component.translatable("class.rpgcraft." + name).color(color);
	}

    public PowerCustom buildPower() {
        return new PowerCustom(powerType);
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
