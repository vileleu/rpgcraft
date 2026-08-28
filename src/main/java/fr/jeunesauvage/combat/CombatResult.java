package fr.jeunesauvage.combat;

import java.util.concurrent.ThreadLocalRandom;

import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;

public class CombatResult {
    private double  amount;
    private double  dodgeChance;
    private boolean isDodge;
	private double	skillTarget;
	private double	skillDamager;
    private boolean isMiss;
    private double  criticalChance;
    private boolean isCritical;
	private boolean isBlocking;
	private boolean isCancelled;
    private double  armor;

    public CombatResult(double amount, Combat combat) {
        this.amount = amount;
		this.skillTarget = 0;
		this.skillDamager = 0;
		this.criticalChance = 0;
		this.isCritical = false;
		this.isMiss = false;
		this.dodgeChance = 0;
		this.isDodge = false;
		this.armor = 0;
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
		double	max = 50;
		double	diff = Math.max(-max, Math.min(max, skillDamager - skillTarget));
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
