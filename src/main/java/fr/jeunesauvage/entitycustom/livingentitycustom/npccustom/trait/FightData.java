package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait;

import java.util.UUID;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.itemcustom.Rarity;

public class FightData {
	private TemplateType		templateType;
	private int					level;
	private Rarity				rarity;
	private long				silence;
    private double				health;
    private double				damage;
	private double				aggroRange;
	private double				aggroRangeSquared;
	private double				chaseRange;
	private double				chaseRangeSquared;
	private double				attackRangeClose;
	private double				attackRangeRanged;
	private float				attackRate;
	private float				spellRate;
    private float				speed;
    private float				speedCombat;
	private UUID				ownerUUID;
	private UUID				petUUID;

	FightData(FightTrait fightTrait) {
		this.templateType = fightTrait.getTemplateType();
		this.level = fightTrait.getLevel();
		this.rarity = Rarity.fromLevel(level);
		this.silence = fightTrait.getSilence();
	    this.health = fightTrait.getHealth();
	    this.damage = fightTrait.getDamage();
	    this.aggroRange = fightTrait.getAggroRange();
		this.aggroRangeSquared = aggroRange * aggroRange;
	    this.chaseRange = fightTrait.getChaseRange();
	    this.chaseRangeSquared = chaseRange * chaseRange;
	    this.attackRangeClose = fightTrait.getAttackRangeClose();
	    this.attackRangeRanged = fightTrait.getAttackRangeRanged();
	    this.attackRate = fightTrait.getAttackRate();
	    this.spellRate = fightTrait.getSpellRate();
	    this.speed = fightTrait.getSpeed();
	    this.speedCombat = fightTrait.getSpeedCombat();
		this.ownerUUID = fightTrait.getOwnerUUID();
	}

	public TemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(TemplateType templateType) {
		this.templateType = templateType;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		this.rarity = Rarity.fromLevel(level);
	}

	public Rarity getRarity() {
		return rarity;
	}

	public long getSilence() {
		return silence;
	}

	public void setSilence(long silence) {
		this.silence = silence;
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
		chaseRangeSquared = chaseRange * chaseRange;
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

	public LivingEntityCustom getOwner() {
		if (ownerUUID == null) return null;
		return RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(ownerUUID);
	}

	public UUID getOwnerUUID() {
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	public LivingEntityCustom getPet() {
		if (petUUID == null) return null;
		return RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(petUUID);
	}

	public UUID getPetUUID() {
		return petUUID;
	}

	public void setPetUUID(UUID petUUID) {
		this.petUUID = petUUID;
	}
}