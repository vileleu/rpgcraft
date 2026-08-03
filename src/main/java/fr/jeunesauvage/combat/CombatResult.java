package fr.jeunesauvage.combat;

import java.util.concurrent.ThreadLocalRandom;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import net.citizensnpcs.api.npc.NPC;

public class CombatResult {
    private double  amount;
    private boolean attackCooldown;
    private double  dodgeChance;
    private boolean isDodge;
	private int		levelTarget;
	private int		levelDamager;
	private double	skillTarget;
	private double	skillDamager;
    private boolean isMiss;
    private double  criticalChance;
    private boolean isCritical;
	private boolean isBlocking;
	private boolean isCancelled;
    private double  armor;
	private NPC		npcTarget;

    public CombatResult(double amount, Combat combat) {
        this.amount = amount;
		this.attackCooldown = false;
		this.levelTarget = -1;
		this.levelDamager = -1;
		this.skillTarget = -1;
		this.skillDamager = -1;
		this.criticalChance = 0;
		this.isCritical = false;
		this.isMiss = false;
		this.dodgeChance = 0;
		this.isDodge = false;
		this.armor = 0;
		this.npcTarget = null;
		// reduce damage of mace (vanilla damage too huge)
		if (combat.getCombatType() == CombatType.CLOSE && combat.getWeaponType() == WeaponType.MACE)
			this.amount /= 3;
    }

	public void calculate() {
		if (dodgeChance > 0 && ThreadLocalRandom.current().nextDouble() < dodgeChance) {
			amount = 0;
			isDodge = true;
			isCancelled = true;
			return;
		}
		if (isBlocking) {
			amount = 0;
			return;
		}
		// if (target == entity && damager != entity)
		if (levelTarget == -1 && levelDamager != -1 && skillDamager != -1)
			skillTarget = levelDamager * 5;
		// if (target != entity && damager == entity)
		else if (levelTarget != -1 && levelDamager == -1 && skillTarget != -1)
			skillDamager = levelTarget * 5;
		// RpgCraft.debug("skillTarget = " + skillTarget);
		// RpgCraft.debug("skillDamager = " + skillDamager);
		double	max = 50;
		double	diff = Math.max(-max, Math.min(max, skillDamager - skillTarget));
		RpgCraft.debug("diff skill = " + diff + "%");
		diff /= 100d;
		if (diff > 0)
			amount *= (1 + diff);
		else if (diff < 0 && ThreadLocalRandom.current().nextDouble() < diff * -1) {
			amount = 0;
			isMiss = true;
			isCancelled = true;
			return;
		}
		if (criticalChance > 0 && ThreadLocalRandom.current().nextDouble() < criticalChance) {
			amount *= 2;
			isCritical = true;
		}
		amount -= armor;
		if (amount <= 0) {
			amount = 0;
			isCancelled = true;
		}
	}

	// aggro
	
	public void addNPCTarget(NPC npcTarget) {
		this.npcTarget = npcTarget;
	}

	public NPC getNpcTarget() {
		return npcTarget;
	}

	// amount

    public void increaseAmount(double amount) {
		setAmount(this.amount + amount);
	}	

    public void decreaseAmount(double amount) {
		setAmount(this.amount - amount);
	}

    public double getAmount() {
		return this.amount;
	}

    public void setAmount(double amount) {
		this.amount = amount;
	}

	// cooldown

	public boolean getAttackCooldown() {
		return this.attackCooldown;
	}

	public void setAttackCooldown(boolean attackCooldown) {
		this.attackCooldown = attackCooldown;
	}

	// dodge

    public void increaseDodgeChance(double dodgeChance) {
		setDodgeChance(this.dodgeChance + dodgeChance);
	}	

    public void decreaseDodgeChance(double dodgeChance) {
		setDodgeChance(this.dodgeChance - dodgeChance);
	}

    public double getDodgeChance() {
		return this.dodgeChance;
	}

    public void setDodgeChance(double dodgeChance) {
		this.dodgeChance = dodgeChance;
	}

    public boolean isDodge() {
		return this.isDodge;
	}

	// level

	public void setLevelTarget(int level) {
		this.levelTarget = level;
	}

	public void setLevelDamager(int level) {
		this.levelDamager = level;
	}

	public int getLevelTarget() {
		return levelTarget;
	}

	public int getLevelDamager() {
		return levelDamager;
	}

	// skillTarget

    public void increaseSkillTarget(double skillTarget) {
		setSkillTarget(this.skillTarget + skillTarget);
	}	

    public void decreaseSkillTarget(double skillTarget) {
		setSkillTarget(this.skillTarget - skillTarget);
	}

    public double getSkillTarget() {
		return this.skillTarget;
	}

    public void setSkillTarget(double skillTarget) {
		this.skillTarget = skillTarget;
	}

	// skillDamager

    public void increaseSkillDamager(double skillDamager) {
		setSkillDamager(this.skillDamager + skillDamager);
	}	

    public void decreaseSkillDamager(double skillDamager) {
		setSkillDamager(this.skillDamager - skillDamager);
	}

    public double getSkillDamager() {
		return this.skillDamager;
	}

    public void setSkillDamager(double skillDamager) {
		this.skillDamager = skillDamager;
	}

	// critical chance

    public void increaseCriticalChance(double criticalChance) {
		setCriticalChance(this.criticalChance + criticalChance);
	}	

    public void decreaseCriticalChance(double criticalChance) {
		setCriticalChance(this.criticalChance - criticalChance);
	}

    public double getCriticalChance() {
		return this.criticalChance;
	}

    public void setCriticalChance(double criticalChance) {
		this.criticalChance = criticalChance;
	}

    public boolean isCritical() {
		return this.isCritical;
	}

    public void setCritical(boolean isCritical) {
		this.isCritical = isCritical;
	}

	// miss

    public boolean isMiss() {
		return this.isMiss;
	}

	// armor

    public void increaseArmor(double armor) {
		setArmor(this.armor + armor);
	}	

    public void decreaseArmor(double armor) {
		setArmor(this.armor - armor);
	}

    public double getArmor() {
		return this.armor;
	}

    public void setArmor(double armor) {
		this.armor = armor;
	}

	// blocking

	public boolean isBlocking() {
		return this.isBlocking;
	}

	public void setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}

	// cancelled

	public boolean isCancelled() {
		return this.isCancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
}
