package fr.jeunesauvage.entity.npc.trait;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class AttributeHelper {
	private int				level;
    private double			health;
    private double			damage;
	private double			aggroRange;
	private double			aggroRangeSquared;
	private double			chaseRange;
	private double			chaseRangeSquared;
	private double			attackRangeClose;
	private double			attackRangeRanged;
	private float			attackRate;
	private float			spellRate;
    private float			speed;
    private float			speedCombat;
	private UUID			uuidOwner;
	private LivingEntity	owner;

	AttributeHelper(TraitSentinel traitSentinel) {
		this.level = traitSentinel.getLevel();
	    this.health = traitSentinel.getHealth();
	    this.damage = traitSentinel.getDamage();
	    this.aggroRange = traitSentinel.getAggroRange();
		this.aggroRangeSquared = aggroRange * aggroRange;
	    this.chaseRange = traitSentinel.getChaseRange();
	    this.chaseRangeSquared = chaseRange * chaseRange;
	    this.attackRangeClose = traitSentinel.getAttackRangeClose();
	    this.attackRangeRanged = traitSentinel.getAttackRangeRanged();
	    this.attackRate = traitSentinel.getAttackRate();
	    this.spellRate = traitSentinel.getSpellRate();
	    this.speed = traitSentinel.getSpeed();
	    this.speedCombat = traitSentinel.getSpeedCombat();
	    this.uuidOwner = traitSentinel.getUUIDOwner();
		this.owner = (uuidOwner != null ? Bukkit.getPlayer(uuidOwner) : null);
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getAggroRange() {
		return aggroRange;
	}

	public void setAggroRange(double aggroRange) {
		this.aggroRange = aggroRange;
		aggroRangeSquared = aggroRange * aggroRange;
	}

	public double getAggroRangeSquared() {
		return aggroRangeSquared;
	}

	public double getChaseRange() {
		return chaseRange;
	}

	public void setChaseRange(double chaseRange) {
		this.chaseRange = chaseRange;
	}

	public double getChaseRangeSquared() {
		return chaseRangeSquared;
	}

	public double getAttackRangeClose() {
		return attackRangeClose;
	}

	public void setAttackRangeClose(double attackRangeClose) {
		this.attackRangeClose = attackRangeClose;
	}

	public double getAttackRangeRanged() {
		return attackRangeRanged;
	}

	public void setAttackRangeRanged(double attackRangeRanged) {
		this.attackRangeRanged = attackRangeRanged;
	}

	public float getAttackRate() {
		return attackRate;
	}

	public void setAttackRate(float attackRate) {
		this.attackRate = attackRate;
	}

	public float getSpellRate() {
		return spellRate;
	}

	public void setSpellRate(float spellRate) {
		this.spellRate = spellRate;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getSpeedCombat() {
		return speedCombat;
	}

	public void setSpeedCombat(float speedCombat) {
		this.speedCombat = speedCombat;
	}

	public UUID getUUIDOwner() {
		return uuidOwner;
	}

	public LivingEntity getOwner() {
		return owner;
	}

	public void setOwner(LivingEntity owner) {
		this.owner = owner;
	}
}