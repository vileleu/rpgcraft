package fr.jeunesauvage.entity.playercustom.attributecustom.resource;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum ResourceType {
    HEALTH("health", NamedTextColor.RED),
    LEVEL("level", NamedTextColor.GOLD),
    MANA("mana", NamedTextColor.BLUE),
    RAGE("rage", NamedTextColor.RED),
    ENERGY("energy", NamedTextColor.YELLOW);

    private final String    name;
    private final TextColor color;
    private NamespacedKey   key = null;
    private NamespacedKey   keyMax = null;

    private ResourceType(String name, TextColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public NamespacedKey getKey() {
        if (this.key == null)
            this.key = new NamespacedKey(RpgCraft.name(), "resource_" + name);
        return this.key;
    }

    public NamespacedKey getKeyMax() {
        if (this.keyMax == null)
            this.keyMax = new NamespacedKey(RpgCraft.name(), "resourcemax_" + name);
        return this.keyMax;
    }

	public Component toComponent() {
		return Component.translatable("resource.rpgcraft." + name).decorate(TextDecoration.BOLD);
	}

	public TextColor getColor() {
		return this.color;
	}
}
