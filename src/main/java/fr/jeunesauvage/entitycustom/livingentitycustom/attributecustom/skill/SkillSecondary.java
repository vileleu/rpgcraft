package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum SkillSecondary implements SkillType {
    ENCHANT("enchant"),
    SMELT("smelt");

    private final String    name;
    private final NamespacedKey key;

    private SkillSecondary(String name) {
        this.name = name;
        this.key = new NamespacedKey(RpgCraft.name(), "skill/" + name);
    }

    @Override
    public AttributeCategory getCategory() {
        return AttributeCategory.SECONDARY;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Component toComponent() {
        return Component.text(name).color(NamedTextColor.AQUA);
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}
