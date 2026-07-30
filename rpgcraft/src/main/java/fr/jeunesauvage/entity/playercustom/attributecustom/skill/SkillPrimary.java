package fr.jeunesauvage.entity.playercustom.attributecustom.skill;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum SkillPrimary implements SkillType {
	ENCHANT("enchant"),
    SMELT("smelt");

    private final String        name;
    private final NamespacedKey key;

    private SkillPrimary(String name) {
        this.name = name;
        this.key = new NamespacedKey(RpgCraft.name(), "skillprimary_" + name);
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
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
	public Component toComponent() {
		return Component.translatable("skill.rpgcraft." + name).decorate(TextDecoration.BOLD);
	}

    @Override
	public TextColor getColor() {
		return NamedTextColor.YELLOW;
	}
}
