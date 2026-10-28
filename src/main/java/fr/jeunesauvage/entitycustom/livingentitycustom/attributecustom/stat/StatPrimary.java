package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat;

import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum StatPrimary implements StatType {
    AGILITY("agility"),
    INTELLECT("intellect"),
    SPIRIT("spirit"),
    STAMINA("stamina"),
    STRENGTH("strength");

    private final String    name;

    private StatPrimary(String name) {
        this.name = name;
    }

    @Override
    public AttributeCategory getCategory() {
        return AttributeCategory.PRIMARY;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Component toComponent() {
        return Component.text(name).color(NamedTextColor.YELLOW);
    }
}
