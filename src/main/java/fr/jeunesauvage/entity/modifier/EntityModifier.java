package fr.jeunesauvage.entity.modifier;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;

public class EntityModifier {
	private final LivingEntity	entity;
	private final NamespacedKey	keyTimer;
	private final NamespacedKey	keyValue;
	private final StatSecondary	type;
	private int					value;
	private long				duration;
	private final long			end;
	private final int			id;

	EntityModifier(LivingEntity livingEntity, StatSecondary type, int value, long duration, int id) {
		this.entity = livingEntity;
		this.keyValue = new NamespacedKey(RpgCraft.name(), "entitystattimer-" + type.getName() + "-" + id);
		this.keyTimer = new NamespacedKey(RpgCraft.name(), "entitystatvalue-" + type.getName() + "-" + id);
		this.type = type;
		this.value = value;
		this.duration = duration;
		this.end = System.currentTimeMillis() + 1000l * duration;
		this.id = id;
		PersistentDataContainer	pdc = entity.getPersistentDataContainer();
		Data.setLong(pdc, keyTimer, end);
		Data.setDouble(pdc, keyValue, value);
		refreshEntityStat(type);
	}

	public void remove() {
		PersistentDataContainer	pdc = entity.getPersistentDataContainer();
		Data.remove(pdc, keyTimer);
		Data.remove(pdc, keyValue);
		value *= -1;
		refreshEntityStat(type);
	}

	// getter + setter

	public NamespacedKey getKeyTimer() {
		return keyTimer;
	}

	public NamespacedKey getKeyValue() {
		return keyValue;
	}

	public StatSecondary getType() {
		return type;
	}

	public String getName() {
		return type.getName();
	}

	public int getValue() {
		return value;
	}

	public long getDuration() {
		return duration;
	}
	
	public long getEnd() {
		return end;
	}

	public long getTimeLeft() {
		return ((end - System.currentTimeMillis()) / 1000l) + 1;
	}

	public int getId() {
		return id;
	}

	// refresh

	private void refreshEntityStat(StatSecondary type) {
		switch (type) {
			// combat
			case StatSecondary.PHYSICAL_DAMAGE -> refreshPhysicalDamage();
			case StatSecondary.SPELL_DAMAGE -> refreshSpellDamage();
			case StatSecondary.PHYSICAL_ARMOR -> refreshPhysicalArmor();
			case StatSecondary.SPELL_ARMOR -> refreshSpellArmor();
			case StatSecondary.CRITICAL_CHANCE -> refreshCriticalChance();
			case StatSecondary.DEFENSE -> refreshDefense();
			case StatSecondary.DODGE -> refreshDodge();
			case StatSecondary.CAST_SPEED -> refreshCastSpeed();
			// resources
			case StatSecondary.MAXIMUM_HEALTH -> refreshMaximumHealth();
			case StatSecondary.MAXIMUM_MANA -> refreshMaximumMana();
			case StatSecondary.REGENERATION_HEALTH -> refreshRegenerationHealth();
			case StatSecondary.REGENERATION_MANA -> refreshRegenerationMana();
			// attributes vanilla
			case StatSecondary.JUMP_STRENGTH -> refreshJumpStrength(type);
			case StatSecondary.SPEED -> refreshSpeed(type);
			case StatSecondary.ATTACK_SPEED -> refreshAttackSpeed(type);
			case StatSecondary.PHYSICAL_RANGE -> refreshPhysicalRange(type);
			case StatSecondary.KNOCKBACK -> refreshKnockback(type);
			case StatSecondary.KNOCKBACK_RESISTANCE -> refreshKnockbackResistance(type);
			case StatSecondary.FALL_DAMAGE -> refreshFallDamage(type);
			case StatSecondary.GRAVITY -> refreshGravity(type);
		}
	}

	private void refreshPhysicalDamage() {
	}

	private void refreshSpellDamage() {
	}

	private void refreshPhysicalArmor() {
	}

	private void refreshSpellArmor() {
	}

	private void refreshCriticalChance() {
	}

	private void refreshDefense() {
	}

	private void refreshDodge() {
	}

	private void refreshCastSpeed() {
	}

	// resources
	private void refreshRegenerationHealth() {
	}

	private void refreshRegenerationMana() {
	}

	// attributes vanilla

	private void refreshMaximumHealth() {
	}

	private void refreshMaximumMana() {
	}

	private void refreshJumpStrength(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH);
		addAttributeModifier(instance, StatSecondary.JUMP_STRENGTH.getKey(), amount);
	}

	private void refreshSpeed(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		addAttributeModifier(instance, StatSecondary.SPEED.getKey(), amount);
	}

	private void refreshAttackSpeed(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		addAttributeModifier(instance, StatSecondary.ATTACK_SPEED.getKey(), amount);
	}

	private void refreshPhysicalRange(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE);
		addAttributeModifier(instance, StatSecondary.PHYSICAL_RANGE.getKey(), amount);
	}

	private void refreshKnockback(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
		addAttributeModifier(instance, StatSecondary.KNOCKBACK.getKey(), amount);
	}

	private void refreshKnockbackResistance(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
		addAttributeModifier(instance, StatSecondary.KNOCKBACK_RESISTANCE.getKey(), amount);
		instance = entity.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
		addAttributeModifier(instance, StatSecondary.KNOCKBACK_RESISTANCE.getKey(), amount);
	}

	private void refreshFallDamage(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER);
		addAttributeModifier(instance, StatSecondary.FALL_DAMAGE.getKey(), amount);
	}

	private void refreshGravity(StatSecondary type) {
		double				amount = value * type.getAmplifier();
		AttributeInstance	instance = entity.getAttribute(Attribute.GENERIC_GRAVITY);
		addAttributeModifier(instance, StatSecondary.GRAVITY.getKey(), amount);
	}

	// add/replace/keep/remove modifier
	private void addAttributeModifier(AttributeInstance instance, NamespacedKey key, double amount) {
		if (instance == null) return;
		AttributeModifier	attributeModifier = instance.getModifier(key);
		if (attributeModifier != null) {
			amount += attributeModifier.getAmount();
			instance.removeModifier(attributeModifier);
		}
		if (amount == 0) return;
		instance.addModifier(new AttributeModifier(key, amount, AttributeModifier.Operation.ADD_SCALAR));
	}
}
