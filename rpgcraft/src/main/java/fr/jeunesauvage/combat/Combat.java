package fr.jeunesauvage.combat;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.combat.combatant.Combatant;
import fr.jeunesauvage.combat.combatant.CombatantType;
import fr.jeunesauvage.entity.modifier.EntityModifierManager;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.equipable.weapon.launcher.LauncherManager;
import fr.jeunesauvage.itemcustom.spell.SpellManager;
import fr.jeunesauvage.sound.SoundManager;
import net.citizensnpcs.api.npc.NPC;


public class Combat {
	private final EntityModifierManager	entityModifierManager;
	private final Combatant<?>			combatantTarget;
	private final Combatant<?>			combatantDamager;
	private final CombatType			combatType;
	private final CombatDamage			combatDamage;
	private final WeaponType			weaponType;

	/*
	** init
	*/

	private Combat(EntityModifierManager entityModifierManager, Combatant<?> combatantTarget, Combatant<?> combatantDamager, Entity source, DamageCause damageCause) {
		this.entityModifierManager = entityModifierManager;
		this.combatantTarget = combatantTarget;
		this.combatantDamager = combatantDamager;
		this.combatType = initCombatType(source, damageCause);
		this.weaponType = initWeaponType(source);
		this.combatDamage = initCombatDamage(source);
	}

	private CombatType initCombatType(Entity source, DamageCause damageCause) {
	    if ((damageCause == DamageCause.ENTITY_ATTACK || damageCause == DamageCause.ENTITY_SWEEP_ATTACK) && source instanceof LivingEntity)
	        return CombatType.CLOSE;
	    else if (source instanceof Projectile)
	        return CombatType.RANGE;
	    else
			return CombatType.UNKNOWN;
	}

	private WeaponType initWeaponType(Entity source) {
	    if (combatType == CombatType.CLOSE) {
			ItemStack	weapon = combatantDamager.getLivingEntity().getEquipment().getItemInMainHand();
			Material	material = weapon.getType();
			for (WeaponType type: CombatType.CLOSE.getWeaponTypes()) {
				if (type.getMaterial() == material)
					return type;
			}
			return WeaponType.HAND;
		}
	    else if (combatType == CombatType.RANGE) {
			PersistentDataContainer pdc = source.getPersistentDataContainer();
			if (Data.hasBoolean(pdc, LauncherManager.getBowKey()))
				return WeaponType.BOW;
			else if (Data.hasBoolean(pdc, LauncherManager.getCrossbowKey()))
				return WeaponType.CROSSBOW;
			else if (Data.hasBoolean(pdc, LauncherManager.getStaffKey()))
				return WeaponType.STAFF;
			else if ((Data.hasBoolean(pdc, LauncherManager.getSpellbookKey())))
				return WeaponType.SPELLBOOK;
		}
		return WeaponType.UNKNOWN;
	}

	private CombatDamage initCombatDamage(Entity source) {
	    return switch (weaponType) {
	        case AXE, BOW, CROSSBOW, HAND, HOE, MACE, PICKAXE, SHOVEL, SWORD, TRIDENT -> CombatDamage.PHYSICAL;
			default -> {
				yield switch (source) {
					case AbstractArrow e -> CombatDamage.PHYSICAL;
					case ThrowableProjectile e -> CombatDamage.PHYSICAL;
					case FishHook e -> CombatDamage.PHYSICAL;
					case LlamaSpit e -> CombatDamage.PHYSICAL;
					case ShulkerBullet e -> CombatDamage.PHYSICAL;
					case Firework e -> CombatDamage.PHYSICAL;
					default -> CombatDamage.MAGIC;
				};
			}
		};
	}

	/*
	** combat
	*/

	// apply resistance from damager
	public CombatResult applyBonusDamager(CombatResult result) {
		if (combatantDamager.getType() == CombatantType.PLAYER) {
			PlayerCustom	playerDamager = combatantDamager.getEntityAs(PlayerCustom.class);
			if (playerDamager.getPlayer().getAttackCooldown() != 1.0f)
				result.setAttackCooldown(true);
			// level
			result.setLevelDamager((int)playerDamager.getLevel().getValue());
			// skill
			if (weaponType != WeaponType.UNKNOWN)
				result = combatDamage.applySkillDamager(playerDamager, weaponType, combatType, result);
			// stat
			result = combatDamage.applyStatDamager(playerDamager, result);
		}
		else if (combatantDamager.getType() == CombatantType.NPC) {
			NPC	npcDamager = combatantDamager.getEntityAs(NPC.class);
			// level
			result.setLevelDamager(npcDamager.getOrAddTrait(TraitSentinel.class).getLevel());
			// skill
			if (weaponType != WeaponType.UNKNOWN)
				result = combatDamage.applySkillDamager(npcDamager, result);
			// stat
			result = combatDamage.applyStatDamager(entityModifierManager, combatantTarget.getLivingEntity(), npcDamager, result);
		}
		else {
			// stat
			result = combatDamage.applyStatDamager(entityModifierManager, combatantDamager.getLivingEntity(), result);
		}
		return result;
	}

	// apply resistance from target
	public CombatResult applyBonusTarget(CombatResult result) {
		if (combatantTarget.getType() == CombatantType.PLAYER) {
			PlayerCustom	playerTarget = combatantTarget.getEntityAs(PlayerCustom.class);
			result.setBlocking(playerTarget.getPlayer().isBlocking());
			// level
			result.setLevelTarget((int)playerTarget.getLevel().getValue());
			// skill
			if (weaponType != WeaponType.UNKNOWN)
				result = combatDamage.applySkillTarget(playerTarget, result);
			// stat
			result = combatDamage.applyStatTarget(playerTarget, result);
		}
		else if (combatantTarget.getType() == CombatantType.NPC) {
			NPC	npcTarget = combatantTarget.getEntityAs(NPC.class);
			// level
			result.setLevelTarget(npcTarget.getOrAddTrait(TraitSentinel.class).getLevel());
			// skill
			if (weaponType != WeaponType.UNKNOWN)
				result = combatDamage.applySkillTarget(npcTarget, result);
			// stat
			result = combatDamage.applyStatTarget(entityModifierManager, combatantTarget.getLivingEntity(), npcTarget, result);
			// aggro npc
			result.addNPCTarget(npcTarget);
		}
		else {
			// stat
			result = combatDamage.applyStatTarget(entityModifierManager, combatantTarget.getLivingEntity(), result);
		}
		return result;
	}

	// apply spell
	public CombatResult applySpell(SpellManager spellManager, CombatResult result) {
		// kneebreaker (-30% speed)
		// duration = 2 seconds + rarity number
		LivingEntity	damager = combatantDamager.getLivingEntity();
		if (damager instanceof Player playerDamager) {
			UUID			uuidDamager = playerDamager.getUniqueId();
			if (combatType == CombatType.CLOSE && spellManager.hasKneeBreaker(uuidDamager)) {
				int	duration = 2 + spellManager.removeKneeBreaker(uuidDamager);
				// need damage and target != boss
				if (result.getAmount() > 0 && !TraitSentinel.isBoss(combatantTarget.getLivingEntity())) {
					if (combatantTarget.getType() == CombatantType.PLAYER)
						combatantTarget.getEntityAs(PlayerCustom.class).addStatModifier(StatSecondary.SPEED, -30, duration);
					else
						entityModifierManager.addModifier(combatantTarget.getLivingEntity(), StatSecondary.SPEED, -30, duration);
				}
			}
			// stealth (remove stealth if attack)
			if (spellManager.hasStealth(uuidDamager)) {
				if (result.getAmount() > 0) {
					int	id = spellManager.removeStealth(uuidDamager);
					PlayerCustom	playerCustom = combatantDamager.getEntityAs(PlayerCustom.class);
					playerCustom.removeStatModifier(id);
					// damager.setInvisible(false);
					SpellManager.removeInvisibility(playerDamager);
					SoundManager.playSound(playerDamager, "spell_stealth");
				}
			}
			// coldblood (100% chance to critical + poison wither)
			// duration = 4 seconds + rarity number
			if (combatType == CombatType.CLOSE && spellManager.hasColdBlood(uuidDamager)) {
				int	duration = 4 + spellManager.removeColdBlood(uuidDamager);
				// need damage
				if (result.getAmount() > 0) {
					if (!result.isCritical()) {
						result.setCritical(true);
						result.increaseAmount(result.getAmount());
					}
					combatantTarget.getLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration * 20, 2, false, false, false));
				}
			}
		}
		LivingEntity	target = combatantTarget.getLivingEntity();
		if (target instanceof Player playerTarget) {
			UUID			uuidTarget = playerTarget.getUniqueId();
			// holyshield (immune to physical damage + reflect damage to random ennemy)
			// duration = 8 seconds
			if (spellManager.hasHolyShield(uuidTarget) && combatDamage == CombatDamage.PHYSICAL) {
				int	level = spellManager.getHolyShield(uuidTarget);
				spellManager.holyShield(playerTarget, level, result.getAmount());
				result.setAmount(0);
			}
			// strikeback (create damage area at each hit, 1 time by second)
			if (result.getAmount() > 0 && spellManager.canUseStrikeBack(uuidTarget)) {
				int	level = spellManager.getStrikeBack(uuidTarget);
				spellManager.strikeBack(playerTarget, level);
			}
		}
		if (result.getAmount() <= 0) {
			result.setAmount(0);
			result.setCancelled(true);
		}
		return result;
	}

	// print damage above target
	public void printDamage(CombatResult result) {
		combatDamage.printDamage(combatantTarget, result);
	}

	/*
	** getter + setter
	*/

	public Combatant<?> getTarget() {
		return combatantTarget;
	}

	public Combatant<?> getDamager() {
		return combatantDamager;
	}

	public CombatType getCombatType() {
		return combatType;
	}

	public CombatDamage getCombatDamage() {
		return combatDamage;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	/*
	** static
	*/

	public static Combat buildCombat(EntityModifierManager entityModifierManager, Entity target, Entity damager, DamageCause damageCause) {
		if (!(target instanceof LivingEntity livingTarget)) return null;
		LivingEntity	livingDamager = initDamager(damager);
		if (livingDamager == null) return null;
		Combatant<?>	combatantTarget = Combatant.build(livingTarget);
		Combatant<?>	combatantDamager = Combatant.build(livingDamager);
		if (combatantTarget == null || combatantDamager == null) return null;
		return new Combat(entityModifierManager, combatantTarget, combatantDamager, damager, damageCause);
	}

	private static LivingEntity initDamager(Entity entity) {
	    if (entity instanceof LivingEntity living)
	        return living;
	    // projectile damage
	    else if (entity instanceof Projectile projectile) {
	        ProjectileSource shooter = projectile.getShooter();
	        if (shooter instanceof LivingEntity livingShooter)
	            return livingShooter;
	    }
	    // potion damage
	    else if (entity instanceof AreaEffectCloud cloud) {
	        if (cloud.getSource() instanceof LivingEntity livingSource)
	            return livingSource;
	    }
		// tnt
		else if (entity instanceof TNTPrimed tnt) {
		    if (tnt.getSource() instanceof LivingEntity livingSource)
		        return livingSource;
		}
		return null;
	}
}
