package fr.jeunesauvage.combat;

import java.util.UUID;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.spell.SpellRegistry;


public class Combat {
	private final LivingEntityCustom	target;
	private final LivingEntityCustom	damager;
	private final CombatType			combatType;
	private final WeaponType			weaponType;
	private final CombatDamage			combatDamage;

	/*
	** init
	*/

	private Combat(LivingEntityCustom target, LivingEntityCustom damager, DamageSource source) {
		this.target = target;
		this.damager = damager;
		this.combatType = initCombatType(source);
		this.weaponType = initWeaponType(source);
		this.combatDamage = initCombatDamage(source);
	}

	private CombatType initCombatType(DamageSource source) {
		DamageType	d = source.getDamageType();
		if (d == DamageType.MOB_ATTACK || d == DamageType.MOB_ATTACK_NO_AGGRO || d == DamageType.PLAYER_ATTACK)
	        return CombatType.CLOSE;
	    else if (source.getDirectEntity() instanceof Projectile)
	        return CombatType.RANGE;
	    else
			return CombatType.UNKNOWN;
	}

	private WeaponType initWeaponType(DamageSource source) {
	    if (combatType == CombatType.CLOSE) {
			ItemStack	item = damager.getEquipment().getItemInMainHand();
			if (item == null) return WeaponType.HAND;
			Weapon		weapon = RpgCraft.getItemCustomRegistry().getWeapon(item);
			if (weapon == null) return WeaponType.HAND;
			return weapon.getType();
		}
	    else if (combatType == CombatType.RANGE) {
			SpellRegistry	spellRegistry = RpgCraft.getSpellRegistry();
			if (spellRegistry.isBow(source.getDirectEntity()))
				return WeaponType.BOW;
			else if (spellRegistry.isCrossBow(source.getDirectEntity()))
				return WeaponType.CROSSBOW;
			else if (spellRegistry.isStaff(source.getDirectEntity()))
				return WeaponType.STAFF;
			else if (spellRegistry.isSpellBook(source.getDirectEntity()))
				return WeaponType.SPELLBOOK;
		}
		return WeaponType.UNKNOWN;
	}

	private CombatDamage initCombatDamage(DamageSource source) {
	    return switch (weaponType) {
	        case AXE, BOW, CLAW, CROSSBOW, HAND, HOE, MACE, PICKAXE, SHOVEL, SWORD, TRIDENT -> CombatDamage.PHYSICAL;
			default -> {
				yield switch (source.getDirectEntity()) {
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

	// apply resistance from target
	public CombatResult applyBonusTarget(CombatResult result) {
		result.setBlocking(target.isBlocking());
		result = combatDamage.applyStatTarget(target, result);
		if (weaponType != WeaponType.UNKNOWN) result = combatDamage.applySkillTarget(target, result);
		return result;
	}

	// apply resistance from damager
	public CombatResult applyBonusDamager(CombatResult result) {
		result = combatDamage.applyStatDamager(damager, result);
		if (weaponType != WeaponType.UNKNOWN) result = combatDamage.applySkillDamager(damager, this, result);
		return result;
	}

	// apply spell
	public CombatResult applySpell(CombatResult result) {
		SpellRegistry	spellRegistry = RpgCraft.getSpellRegistry();
		UUID	uuidTarget = target.getUUID();
		// holyshield (immune to physical damage + reflect damage to random ennemy)
		// duration = 8 seconds
		if (spellRegistry.hasHolyShield(uuidTarget) && combatDamage == CombatDamage.PHYSICAL) {
			int	level = spellRegistry.getHolyShield(uuidTarget);
			spellRegistry.holyShieldHit(target, Rarity.fromInt(level), result.getAmount());
			result.setAmount(0);
		}
		// strikeback (create damage area at each hit, 1 time by second)
		if (result.getAmount() > 0 && spellRegistry.canUseStrikeBack(uuidTarget)) {
			int	level = spellRegistry.getStrikeBack(uuidTarget);
			spellRegistry.strikeBackHit(target, Rarity.fromInt(level));
		}
		if (result.getAmount() <= 0) {
			result.setAmount(0);
			result.setCancelled(true);
		}
		UUID	uuidDamager = damager.getUUID();
		// kneebreaker (-30% speed)
		// duration = 2 seconds + rarity number
		if (combatType == CombatType.CLOSE && spellRegistry.hasKneeBreaker(uuidDamager)) {
			int	duration = 4 + spellRegistry.removeKneeBreaker(uuidDamager);
			// need damage and target != boss
			if (result.getAmount() > 0 && !target.isBoss()) target.addStatModifier(StatSecondary.SPEED, -40, duration);
		}
		// stealth (remove stealth if attack)
		if (spellRegistry.hasStealth(damager)) {
			if (result.getAmount() > 0) spellRegistry.removeStealth(damager);
		}
		// coldblood (100% chance to critical + poison wither)
		// duration = 4 seconds + rarity number
		if (combatType == CombatType.CLOSE && spellRegistry.hasColdBlood(uuidDamager)) {
			int	duration = 4 + spellRegistry.removeColdBlood(uuidDamager);
			// need damage
			if (result.getAmount() > 0) {
				if (!result.isCritical()) {
					result.setCritical(true);
					result.increaseAmount(result.getAmount());
				}
				LivingEntity	l = target.getLivingEntity();
				if (l != null) l.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration * 20, 2, false, false, false));
			}
		}
		return result;
	}

	// print damage above target
	public void printDamage(CombatResult result) {
		combatDamage.printDamage(target, result);
	}

	/*
	** getter + setter
	*/

	public LivingEntityCustom getTarget() {
		return target;
	}

	public LivingEntityCustom getDamager() {
		return damager;
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

	public static Combat buildCombat(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof LivingEntity target)) return null;
    	DamageSource	source = e.getDamageSource();
    	Entity 			damager = source.getCausingEntity();
    	if (damager == null) return null;
		LivingEntityCustom	combatantTarget = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(target.getUniqueId());
		LivingEntityCustom	combatantDamager = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(damager.getUniqueId());
		if (combatantTarget == null || combatantDamager == null) return null;
		return new Combat(combatantTarget, combatantDamager, source);
	}
}
