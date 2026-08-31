package fr.jeunesauvage.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;


public enum CombatDamage {
    PHYSICAL("physical", DamageType.MOB_ATTACK) {
		// stat player target
        @Override
        public CombatResult applyStatTarget(LivingEntityCustom target, CombatResult result) {
			// stats + modifiers
			result.increaseDodgeChance(StatSecondary.DODGE.getAmount(target));
			result.decreaseCriticalChance(StatSecondary.DEFENSE.getAmount(target));
            result.increaseArmor(StatSecondary.PHYSICAL_ARMOR.getAmount(target));
			return result;
        }
		// stat player damager
        @Override
        public CombatResult applyStatDamager(LivingEntityCustom damager, CombatResult result) {
			// stats + modifiers
			// recharge physique
			if (damager.attackIsInCooldown() == false)
            	result.increaseAmount(StatSecondary.PHYSICAL_DAMAGE.getAmount(damager));
			result.increaseCriticalChance(StatSecondary.CRITICAL_CHANCE.getAmount(damager));
			return result;
        }
		// skill player target
        @Override
    	public CombatResult applySkillTarget(LivingEntityCustom target, CombatResult result) {
			Skill	skill = target.getSkill(SkillPrimary.TEMPERING);
			int		skillAmount = skill.getValue() + skill.getValueModifier();
			result.setSkillTarget(skillAmount);
			if (target.attackIsInCooldown() == false) increaseSkill(target, skill);
			return result;
    	}
		// skill player damager
        @Override
    	public CombatResult applySkillDamager(LivingEntityCustom damager, Combat combat, CombatResult result) {
			for (SkillPrimary skillPrimary: SkillPrimary.values()) {
				if (skillPrimary.getWeaponType() == combat.getWeaponType()) {
					Skill	skill = damager.getSkill(skillPrimary);
					int		skillAmount = skill.getValue() + skill.getValueModifier();
					result.setSkillDamager(skillAmount);
					if (damager.attackIsInCooldown() == false) increaseSkill(damager, skill);
				}
			}
			return result;
    	}
		// print damage
        @Override
		public void printDamage(LivingEntityCustom combatant, CombatResult result) {
			createDisplay(combatant, result, NamedTextColor.GRAY);
		}
    },
    MAGIC("magic", DamageType.MAGIC) {
		// player target
        @Override
        public CombatResult applyStatTarget(LivingEntityCustom target, CombatResult result) {
			// stats + modifiers
			result.increaseDodgeChance(StatSecondary.DODGE.getAmount(target));
			result.decreaseCriticalChance(StatSecondary.DEFENSE.getAmount(target));
            result.increaseArmor(StatSecondary.SPELL_ARMOR.getAmount(target));
			return result;
        }
		// player damager
        @Override
        public CombatResult applyStatDamager(LivingEntityCustom damager, CombatResult result) {
			// stats + modifiers
            result.increaseAmount(StatSecondary.SPELL_DAMAGE.getAmount(damager));
			result.increaseCriticalChance(StatSecondary.CRITICAL_CHANCE.getAmount(damager));
			return result;
        }
		// skill player target
        @Override
    	public CombatResult applySkillTarget(LivingEntityCustom target, CombatResult result) {
			Skill	skill = target.getSkill(SkillPrimary.TEMPERING);
			int		skillAmount = skill.getValue() + skill.getValueModifier();
			result.setSkillTarget(skillAmount);
			if (target.attackIsInCooldown() == false) increaseSkill(target, skill);
			return result;
    	}
		// skill player damager
        @Override
    	public CombatResult applySkillDamager(LivingEntityCustom damager, Combat combat, CombatResult result) {
			for (SkillPrimary skillPrimary: SkillPrimary.values()) {
				if (skillPrimary.getWeaponType() == combat.getWeaponType()) {
					Skill	skill = damager.getSkill(skillPrimary);
					int		skillAmount = skill.getValue() + skill.getValueModifier();
					result.setSkillDamager(skillAmount);
					if (damager.attackIsInCooldown() == false) increaseSkill(damager, skill);
				}
			}
			return result;
    	}
		// print damage
        @Override
		public void printDamage(LivingEntityCustom combatant, CombatResult result) {
			createDisplay(combatant, result, NamedTextColor.LIGHT_PURPLE);
		}
    };

	private final String		name;
	private final DamageType	damageType;

	CombatDamage(String name, DamageType damageType) {
		this.name = name;
		this.damageType = damageType;
	}

    public abstract CombatResult applyStatTarget(LivingEntityCustom target, CombatResult result);
    public abstract CombatResult applyStatDamager(LivingEntityCustom damager, CombatResult result);

	public abstract CombatResult applySkillTarget(LivingEntityCustom target, CombatResult result);
	public abstract CombatResult applySkillDamager(LivingEntityCustom damager, Combat combat, CombatResult result);

	public abstract void printDamage(LivingEntityCustom combatant, CombatResult result);

	// utils

	protected void increaseSkill(LivingEntityCustom livingEntityCustom, Skill skill) {
		if (skill.isMaxed() || !(livingEntityCustom instanceof PlayerCustom playerCustom)) return;
		double	diff = skill.getDifference();
		// 100%
		if (diff >= 100) {
			skill.increase(1);
			playerCustom.sendMessage(Message.increaseSkill(skill));
			return;
		}
		int		random = ThreadLocalRandom.current().nextInt(100);
		// 50%
		if (diff >= 80 && random % 2 == 0) {
			skill.increase(1);
			playerCustom.sendMessage(Message.increaseSkill(skill));
			return;
		}
		// 34%
		else if (diff >= 40 && random % 3 == 0) {
			skill.increase(1);
			playerCustom.sendMessage(Message.increaseSkill(skill));
			return;
		}
		// 25%
		else if (diff >= 20 && random % 4 == 0) {
			skill.increase(1);
			playerCustom.sendMessage(Message.increaseSkill(skill));
			return;
		}
		// 13%
		else if (diff >= 10 && random % 8 == 0) {
			skill.increase(1);
			playerCustom.sendMessage(Message.increaseSkill(skill));
			return;
		}
		// 8%
		else if (diff >= 5 && random % 12 == 0) {
			skill.increase(1);
			playerCustom.sendMessage(Message.increaseSkill(skill));
			return;
		}
	}

	public DamageSource getDamageSource(LivingEntityCustom damager) {
		if (damager == null) return DamageSource.builder(damageType).build();
		LivingEntity	livingEntity = damager.getLivingEntity();
		if (livingEntity == null) return DamageSource.builder(damageType).build();
		return DamageSource.builder(damageType).withCausingEntity(livingEntity).withDirectEntity(livingEntity).build();
	}

	protected void createDisplay(LivingEntityCustom livingEntityCustom, CombatResult result, NamedTextColor color) {
		World			world = livingEntityCustom.getWorld();
		if (world == null) return;
		Location		loc = livingEntityCustom.getLocation().add(0, livingEntityCustom.getHeight() + 0.5, 0);
		TextDisplay 	display = (TextDisplay)world.spawnEntity(loc, EntityType.TEXT_DISPLAY);
		if (result.isMiss())
			display.text(Message.miss().color(color));
		else if (result.isDodge())
		    display.text(Message.dodge().color(color));
		else {
			if (result.getAmount() == 0)
				display.text(Message.immune().color(color));
			else {
				display.text(Message.c(Component.text(String.format("%.1f", result.getAmount())), color));
				float	scaleFactor;
				if (result.isCritical()) {
					scaleFactor = 2.5f;
					loc.getWorld().spawnParticle(Particle.CRIT, loc, 10);
				}
				else
					scaleFactor = 1.0f;
				display.setTransformation(new Transformation(
				    new Vector3f(0, 0, 0),
				    new AxisAngle4f(0, 0, 0, 1),
				    new Vector3f(scaleFactor, scaleFactor, scaleFactor),
				    new AxisAngle4f(0, 0, 0, 1)
				));
			}
		}
        display.setBillboard(Display.Billboard.CENTER);
        display.setSeeThrough(true);
        display.setShadowed(true);
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ > 20) {
                    display.remove();
                    cancel();
                    return;
                }
                display.teleport(display.getLocation().add(0, 0.03, 0));
            }
        }.runTaskTimer(RpgCraft.instance(), 1L, 1L);
	}

	public String getName() {
		return name;
	}

	public DamageType getType() {
		return damageType;
	}

	public static void heal(LivingEntityCustom livingEntityCustom, double amount) {
		if (livingEntityCustom == null) return;
		double	healthMax = livingEntityCustom.getHealthMax();
		double	health = livingEntityCustom.getHealth();
		double	amountNext = Math.min(amount, healthMax - health);
		if (amountNext <= 0) return;
		World	world = livingEntityCustom.getWorld();
		if (world == null) return;
		livingEntityCustom.setHealth(health + amountNext);
		world.spawnParticle(Particle.HEART, livingEntityCustom.getEyeLocation(), 3, 0.3, 0.5, 0.3, 0);
		createDisplayHeal(livingEntityCustom, amountNext);
	}

	private static void createDisplayHeal(LivingEntityCustom livingEntityCustom, double amount) {
		if (amount == 0) return;
		Location		loc = livingEntityCustom.getLocation().add(0, livingEntityCustom.getHeight() + 0.5, 0);
		World			world = loc.getWorld();
		TextDisplay 	display = (TextDisplay)world.spawnEntity(loc, EntityType.TEXT_DISPLAY);
		TextColor		color = NamedTextColor.GREEN;
		display.text(Message.c(Component.text(String.format("%.1f", amount)), color));
		float	scaleFactor = 1.0f;
		display.setTransformation(new Transformation(
		    new Vector3f(0, 0, 0),
		    new AxisAngle4f(0, 0, 0, 1),
		    new Vector3f(scaleFactor, scaleFactor, scaleFactor),
		    new AxisAngle4f(0, 0, 0, 1)
		));
        display.setBillboard(Display.Billboard.CENTER);
        display.setSeeThrough(true);
        display.setShadowed(true);
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks++ > 20) {
                    display.remove();
                    cancel();
                    return;
                }
                display.teleport(display.getLocation().add(0, 0.03, 0));
            }
        }.runTaskTimer(RpgCraft.instance(), 1L, 1L);
	}
}
