package fr.jeunesauvage.entity.playercustom.attributecustom.stat;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.modifier.EntityModifierManager;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum StatSecondary implements StatType {
    // combat
    PHYSICAL_DAMAGE("physical_damage", 1),
    SPELL_DAMAGE("spell_damage", 1),
    PHYSICAL_ARMOR("physical_armor", 0.1),
    SPELL_ARMOR("spell_armor", 0.1),
    CRITICAL_CHANCE("critical_chance", 0.005), // 0.5%
    DEFENSE("defense", 0.003),                 // 0.3%
    DODGE("dodge", 0.002),                     // 0.2%
    CAST_SPEED("cast_speed", 0.01),            // 1%
    // resources
    MAXIMUM_HEALTH("maximum_health", 1),
    MAXIMUM_MANA("maximum_mana", 1),
    REGENERATION_HEALTH("regeneration_health", 0.5),
    REGENERATION_MANA("regeneration_mana", 0.5),
    // attributes vanilla
    JUMP_STRENGTH("jump_strength", 0.01),
    SPEED("speed", 0.01),
    ATTACK_SPEED("attack_speed", 0.01),
    PHYSICAL_RANGE("physical_range", 0.01),
    KNOCKBACK("knockback", 0.01),
    KNOCKBACK_RESISTANCE("knockback_resistance", 0.01),
    FALL_DAMAGE("fall_damage", 0.01),
    GRAVITY("gravity", 0.01);

    private final String        name;
    private final double        amplifier;
    private final NamespacedKey key;

    private StatSecondary(String name, double amplifier) {
        this.name = name;
        this.amplifier = amplifier;
        this.key = new NamespacedKey(RpgCraft.name(), "statsecondary_" + name);
    }

    @Override
    public AttributeCategory getCategory() {
        return AttributeCategory.SECONDARY;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public double getAmplifier() {
        return this.amplifier;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
	public Component toComponent() {
		return Component.translatable("stat.rpgcraft." + name).decorate(TextDecoration.BOLD);
	}

    @Override
	public TextColor getColor() {
		return NamedTextColor.AQUA;
	}

    public static double getAmount(PlayerCustom playerCustom, StatSecondary statType) {
        return playerCustom.getStat(statType).getValue() * statType.getAmplifier();
    }

    public static double getAmount(NPC npc, StatSecondary statType) {
        return npc.getOrAddTrait(TraitSentinel.class).getStat(statType) * statType.getAmplifier();
    }

    public static double getAmount(EntityModifierManager entityModifierManager, LivingEntity livingEntity, StatSecondary statType) {
        return entityModifierManager.getModifierValue(livingEntity, statType) * statType.getAmplifier();
    }

	public static StatSecondary fromString(String name) {
        if (name == null)
            return null;
		for (StatSecondary type: StatSecondary.values()) {
			if (type.getName().equals(name))
        		return type;
		}
        return null;
	}
}
