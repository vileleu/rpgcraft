package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat;

import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum StatSecondary implements StatType {
    // combat
    PHYSICAL_DAMAGE("physical_damage", 0.2),            // + 0.2  increase physical damage
    SPELL_DAMAGE("spell_damage", 0.2),                  // + 0.2  increase spell damage
    PHYSICAL_ARMOR("physical_armor", 0.05),             // + 0.05 reduce physical damage taken
    SPELL_ARMOR("spell_armor", 0.05),                   // + 0.05 reduce spell damage taken
    CRITICAL_CHANCE("critical_chance", 0.002),          // + 0.2% chance to hit critical
    DEFENSE("defense", 0.001),                          // + 0.1% chance to cancel critical taken
    DODGE("dodge", 0.002),                              // + 0.2% chance to dodge
    CAST_SPEED("cast_speed", 0.01),                     // + 1%   speed of casting
    // resources
    MAXIMUM_HEALTH("maximum_health", 1),                // + 1   increase health maximum
    MAXIMUM_MANA("maximum_mana", 1),                    // + 1   increase mana maxmimum
    REGENERATION_HEALTH("regeneration_health", 0.5),    // + 0.5 increase health each 2 seconds
    REGENERATION_MANA("regeneration_mana", 0.5),        // + 0.5 increase mana each 2 seconds
    // attributes vanilla
    JUMP_STRENGTH("jump_strength", 0.001),              // 0.1% increase strength on jump
    SPEED("speed", 0.01),                               // 1%   increase speed movement
    ATTACK_SPEED("attack_speed", 0.01),                 // 1%   increase attack speed
    PHYSICAL_RANGE("physical_range", 0.01),             // 1%   increase range of close damage
    KNOCKBACK("knockback", 0.01),                       // 1%   increase knockback of close damage
    KNOCKBACK_RESISTANCE("knockback_resistance", 0.01), // 1%   decrease knockback of close damage + explosion damage
    FALL_DAMAGE("fall_damage", 0.01),                   // 1%   decrease fall damage
    GRAVITY("gravity", 0.01);                           // 1%   decrease gravity

    private final String    name;
    private final double    amplifier;

    private StatSecondary(String name, double amplifier) {
        this.name = name;
        this.amplifier = amplifier;
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

    public double getAmplifier() {
        return this.amplifier;
    }

    public double getAmount(LivingEntityCustom livingEntityCustom) {
        Stat    stat = livingEntityCustom.getStat(this);
        if (stat == null) return 0;
        return (stat.getValue() + stat.getValueModifier()) * getAmplifier();
    }
}