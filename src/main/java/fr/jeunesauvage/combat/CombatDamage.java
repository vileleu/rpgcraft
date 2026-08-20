package fr.jeunesauvage.combat;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.combatant.Combatant;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.modifier.EntityModifierManager;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Health;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillSecondary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;


public enum CombatDamage {
    PHYSICAL("physical", DamageType.PLAYER_ATTACK) {
		// player damager
        @Override
        public CombatResult applyStatDamager(PlayerCustom playerCustom, CombatResult result) {
			// stats + modifiers
			// recharge physique
			if (result.getAttackCooldown() == false)
            	result.increaseAmount(StatSecondary.getAmount(playerCustom, StatSecondary.PHYSICAL_DAMAGE));
			result.increaseCriticalChance(StatSecondary.getAmount(playerCustom, StatSecondary.CRITICAL_CHANCE));
			return result;
        }
		// npc damager
        @Override
        public CombatResult applyStatDamager(EntityModifierManager entityModifierManager, LivingEntity livingEntity, NPC npc, CombatResult result) {
			// stats
            result.increaseAmount(StatSecondary.getAmount(npc, StatSecondary.PHYSICAL_DAMAGE));
			result.increaseCriticalChance(StatSecondary.getAmount(npc, StatSecondary.CRITICAL_CHANCE));
			// modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.PHYSICAL_DAMAGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.CRITICAL_CHANCE));
			return result;
        }
		// living entity damager
        @Override
        public CombatResult applyStatDamager(EntityModifierManager entityModifierManager, LivingEntity livingEntity, CombatResult result) {
			// modifiers
            result.increaseAmount(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.PHYSICAL_DAMAGE));
			result.increaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.CRITICAL_CHANCE));
			return result;
        }
		// player target
        @Override
        public CombatResult applyStatTarget(PlayerCustom playerCustom, CombatResult result) {
			// stats + modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(playerCustom, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(playerCustom, StatSecondary.DEFENSE));
            result.increaseArmor(StatSecondary.getAmount(playerCustom, StatSecondary.PHYSICAL_ARMOR));
			return result;
        }
		// npc target
        @Override
        public CombatResult applyStatTarget(EntityModifierManager entityModifierManager, LivingEntity livingEntity, NPC npc, CombatResult result) {
			// stats
			result.increaseDodgeChance(StatSecondary.getAmount(npc, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(npc, StatSecondary.DEFENSE));
            result.increaseArmor(StatSecondary.getAmount(npc, StatSecondary.PHYSICAL_ARMOR));
			// modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DEFENSE));
            result.increaseArmor(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.PHYSICAL_ARMOR));
			return result;
        }
		// living entity target
        @Override
        public CombatResult applyStatTarget(EntityModifierManager entityModifierManager, LivingEntity livingEntity, CombatResult result) {
			// modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DEFENSE));
            result.increaseArmor(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.PHYSICAL_ARMOR));
			return result;
        }
        @Override
		public void printDamage(Combatant<?> combatant, CombatResult result) {
			createDisplay(combatant, result, NamedTextColor.GRAY);
		}
    },
    MAGIC("magic", DamageType.MAGIC) {
		// player damager
        @Override
        public CombatResult applyStatDamager(PlayerCustom playerCustom, CombatResult result) {
			// stats + modifiers
            result.increaseAmount(StatSecondary.getAmount(playerCustom, StatSecondary.SPELL_DAMAGE));
			result.increaseCriticalChance(StatSecondary.getAmount(playerCustom, StatSecondary.CRITICAL_CHANCE));
			return result;
        }
		// npc damager
        @Override
        public CombatResult applyStatDamager(EntityModifierManager entityModifierManager, LivingEntity livingEntity, NPC npc, CombatResult result) {
			// stats
            result.increaseAmount(StatSecondary.getAmount(npc, StatSecondary.SPELL_DAMAGE));
			result.increaseCriticalChance(StatSecondary.getAmount(npc, StatSecondary.CRITICAL_CHANCE));
			// modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.SPELL_DAMAGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.CRITICAL_CHANCE));
			return result;
        }
		// living entity damager
        @Override
        public CombatResult applyStatDamager(EntityModifierManager entityModifierManager, LivingEntity livingEntity, CombatResult result) {
            result.increaseAmount(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.SPELL_DAMAGE));
			result.increaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.CRITICAL_CHANCE));
			return result;
        }
		// player target
        @Override
        public CombatResult applyStatTarget(PlayerCustom playerCustom, CombatResult result) {
			// stats + modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(playerCustom, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(playerCustom, StatSecondary.DEFENSE));
            result.increaseArmor(StatSecondary.getAmount(playerCustom, StatSecondary.SPELL_ARMOR));
			return result;
        }
		// npc target
        @Override
        public CombatResult applyStatTarget(EntityModifierManager entityModifierManager, LivingEntity livingEntity, NPC npc, CombatResult result) {
			// stats
			result.increaseDodgeChance(StatSecondary.getAmount(npc, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(npc, StatSecondary.DEFENSE));
            result.increaseArmor(StatSecondary.getAmount(npc, StatSecondary.SPELL_ARMOR));
			// modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DEFENSE));
			result.decreaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.SPELL_ARMOR));
			return result;
        }
		// living entity target
        @Override
        public CombatResult applyStatTarget(EntityModifierManager entityModifierManager, LivingEntity livingEntity, CombatResult result) {
			// modifiers
			result.increaseDodgeChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DODGE));
			result.decreaseCriticalChance(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.DEFENSE));
            result.increaseArmor(StatSecondary.getAmount(entityModifierManager, livingEntity, StatSecondary.SPELL_ARMOR));
			return result;
        }
        @Override
		public void printDamage(Combatant<?> combatant, CombatResult result) {
			createDisplay(combatant, result, NamedTextColor.LIGHT_PURPLE);
		}
    };

	private final String		name;
	private final DamageType	damageType;

	CombatDamage(String name, DamageType damageType) {
		this.name = name;
		this.damageType = damageType;
	}

    public abstract CombatResult applyStatDamager(PlayerCustom playerCustom, CombatResult result);
    public abstract CombatResult applyStatDamager(EntityModifierManager entityModifierManager, LivingEntity livingEntity, NPC npc, CombatResult result);
	public abstract CombatResult applyStatDamager(EntityModifierManager entityModifierManager, LivingEntity livingEntity, CombatResult result);

    public abstract CombatResult applyStatTarget(PlayerCustom playerCustom, CombatResult result);
    public abstract CombatResult applyStatTarget(EntityModifierManager entityModifierManager, LivingEntity livingEntity, NPC npc, CombatResult result);
	public abstract CombatResult applyStatTarget(EntityModifierManager entityModifierManager, LivingEntity livingEntity, CombatResult result);

	public abstract void printDamage(Combatant<?> combatant, CombatResult result);

	// apply skill

    public CombatResult applySkillDamager(PlayerCustom playerCustom, WeaponType weaponType, CombatType combatType, CombatResult result) {
		for (SkillSecondary skill: SkillSecondary.values()) {
			if (skill.getWeaponType() == weaponType) {
				int	skillAmount = SkillSecondary.getAmount(playerCustom, skill);
				result.setSkillDamager(skillAmount);
				if (result.getAttackCooldown() == false)
					playerCustom.incrementeSkill(skill);
			}
		}
		return result;
    }

	public CombatResult applySkillDamager(NPC npc, CombatResult result) {
		// skill = (level * 5)
		int	npcSkill = result.getLevelDamager() * 5;
		result.setSkillDamager(npcSkill);
		return result;
    }

    public CombatResult applySkillTarget(PlayerCustom playerCustom, CombatResult result) {
		SkillSecondary	skill = SkillSecondary.TEMPERING;
		int	skillAmount = SkillSecondary.getAmount(playerCustom, skill);
		result.setSkillTarget(skillAmount);
		if (result.getAttackCooldown() == false)
			playerCustom.incrementeSkill(skill);
		return result;
    }

    public CombatResult applySkillTarget(NPC npc, CombatResult result) {
		// skill = (level * 5)
		int	npcSkill = result.getLevelTarget() * 5;
		result.setSkillTarget(npcSkill);
		return result;
    }

	// utils

	public DamageSource getDamageSource(Combatant<?> combatant) {
		if (combatant == null) return DamageSource.builder(damageType).build();
		LivingEntity	livingEntity = combatant.getLivingEntity();
		if (livingEntity == null) return DamageSource.builder(damageType).build();
		return DamageSource.builder(damageType).withCausingEntity(livingEntity).withDirectEntity(livingEntity).build();
	}

	protected void createDisplay(Combatant<?> combatant, CombatResult result, NamedTextColor color) {
		LivingEntity	livingEntity = combatant.getLivingEntity();
		Location		loc = livingEntity.getLocation().add(0, livingEntity.getHeight() + 0.5, 0);
		World			world = loc.getWorld();
		TextDisplay 	display = (TextDisplay)world.spawnEntity(loc, EntityType.TEXT_DISPLAY);
		if (result.isMiss())
			display.text(Message.miss().color(color));
		else if (result.isDodge())
		    display.text(Message.dodge().color(color));
		else {
			if (result.getAmount() == 0)
				display.text(Message.immune().color(color));
			else {
				display.text(Component.text(String.format("%.1f", result.getAmount())).color(color).decorate(TextDecoration.BOLD));
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

	public static DamageSource getDamageSource(LivingEntity livingEntity, DamageType damageType) {
		if (livingEntity == null) return DamageSource.builder(damageType).build();
		return DamageSource.builder(damageType).withCausingEntity(livingEntity).withDirectEntity(livingEntity).build();
	}

	public static void heal(LivingEntity livingEntity, double amount) {
		if (livingEntity == null) return;
		double		healthMax = 1;
		AttributeInstance	instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (instance != null)
			healthMax = instance.getValue();
		double	healthActual = livingEntity.getHealth();
		double	amountNext = Math.min(amount, healthMax - healthActual);
		if (amountNext <= 0) return;
		livingEntity.setHealth(healthActual + amountNext);
		livingEntity.getWorld().spawnParticle(Particle.HEART, livingEntity.getEyeLocation(), 3, 0.3, 0.5, 0.3, 0);
		createDisplayHeal(livingEntity, amountNext);
		if (livingEntity instanceof Player player && !player.hasMetadata("NPC")) {
			PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
			if (playerCustom == null) return;
			playerCustom.getScoreboardCustom().refreshHealth();
		}
	}

	public static void heal(PlayerCustom playerCustom, double amount) {
		if (playerCustom == null) return;
		Player	player = playerCustom.getPlayer();
		Health	health = playerCustom.getHealth();
		double	healthMax = health.getValueMax();
		double	healthActual = health.getValue();
		double	amountNext = Math.min(amount, healthMax - healthActual);
		if (amountNext <= 0) return;
		health.increase(amountNext);
		player.getWorld().spawnParticle(Particle.HEART, player.getEyeLocation(), 3, 0.3, 0.5, 0.3, 0);
		createDisplayHeal(player, amountNext);
		playerCustom.getScoreboardCustom().refreshHealth();
	}

	private static void createDisplayHeal(LivingEntity livingEntity, double amount) {
		if (amount == 0) return;
		Location		loc = livingEntity.getLocation().add(0, livingEntity.getHeight() + 0.5, 0);
		World			world = loc.getWorld();
		TextDisplay 	display = (TextDisplay)world.spawnEntity(loc, EntityType.TEXT_DISPLAY);
		TextColor		color = NamedTextColor.GREEN;
		display.text(Component.text(String.format("%.1f", amount)).color(color).decorate(TextDecoration.BOLD));
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
