package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spellcaster;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.NavigatorParameters;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.waypoint.LinearWaypointProvider;
import net.citizensnpcs.trait.waypoint.Waypoint;
import net.citizensnpcs.trait.waypoint.WaypointProvider;
import net.citizensnpcs.trait.waypoint.Waypoints;

public class FightAI {
	private final NPC				npc;
	private final FightData			data;
	private final Map<UUID, Double>	aggro;
	private final Set<Location>		allWaypoints;
    private LivingEntityCustom		target;
    private LivingEntityCustom		lastTarget;
    private Location				lastTargetLocation;
    private LivingEntityCustom		targetHide;
    private boolean					inChase;
    private int						nextAttack;
    private int						nextSpellClose;
    private int						nextSpellRanged;
    private int						nextSpellRangedBoss;
	private boolean					quote;
	private int						nextStuckRate;
	private int						nextStuck;
	private Location				lastLocation;

    FightAI(NPC npc, FightData fightData) {
		this.npc = npc;
		this.data = fightData;
		this.aggro = new HashMap<>();
		this.allWaypoints = new HashSet<>();
        this.target = null;
        this.lastTarget = null;
        this.lastTargetLocation = null;
		this.targetHide = null;
        this.inChase = false;
		this.nextAttack = 0;
		this.nextSpellClose = 0;
		this.nextSpellRanged = 0;
		this.nextSpellRangedBoss = 0;
		this.quote = false;
		this.nextStuckRate = 2;
		this.nextStuck = 0;
		this.lastLocation = npc.getStoredLocation();
		this.lastLocation.setY(0);
		Waypoints			waypoints = npc.getOrAddTrait(Waypoints.class);
		WaypointProvider	provider = waypoints.getCurrentProvider();
		if (provider instanceof LinearWaypointProvider linear) {
		    for (Waypoint waypoint : linear.waypoints()) {
		        allWaypoints.add(waypoint.getLocation());
		    }
		}
    }

    // find better target (aggro or not in team) (for non pet)
	public void findTarget(NPCCustom npcCustom) {
		target = null;
		targetHide = null;
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		if (!aggro.isEmpty()) {
			double	bestScore = 0;
			Iterator<Entry<UUID, Double>>	it = aggro.entrySet().iterator();
    		while (it.hasNext()) {
				Entry<UUID, Double>	entry = it.next();
    		    double				score = entry.getValue();
				Double				newScore = score * 0.90;
				entry.setValue(newScore);
				// no more aggro
				if (newScore <= 0.5) {
					it.remove();
					continue;
				}
				// target is not living entity ?
				LivingEntityCustom	entity = entityCustomRegistry.getLivingEntityCustom(entry.getKey());
    		    if (entity == null) {
					it.remove();
					continue;
				}
				// target is grouped ? 
				if (npcCustom.isGrouped(entity)) {
					it.remove();
					continue;
				}
				// target is dead
    		    if (!entity.isPresent() || entity.isInvulnerable() || entity.isInvisible() || entity.isCreative()) {
					it.remove();
					continue;
				}
				// target too far from npc
    		    if (npcCustom.getLocation().distanceSquared(entity.getLocation()) > data.getAggroRangeSquared()) continue;
				if (score > bestScore) {
                    bestScore = score;
        			if (!npcCustom.hasLineOfSight(entity)) {
					    if (lastTarget == null || !entity.equals(lastTarget)) continue;
						target = null;
						targetHide = entity;
				    }
                    else
    		            target = entity;
    		    }
    		}
		}
		if (target == null && targetHide == null) {
			if (npcCustom.isPet()) {
				LivingEntityCustom	owner = data.getOwner();
				npc.getNavigator().setTarget(owner != null ? owner.getLivingEntity() : null, false);
			}
			else {
				World	world = npcCustom.getWorld();
				if (world == null) return;
				for (LivingEntity l: world.getNearbyLivingEntities(npcCustom.getLocation(), data.getAggroRange())) {
					LivingEntityCustom	entity = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
					if (entity == null || npcCustom.isGrouped(entity)) continue;
					// target choice
    			    if (!entity.isPresent() || entity.isInvisible() || entity.isInvulnerable() || entity.isCreative()) continue;
        			if (!npcCustom.hasLineOfSight(entity)) {
					    if (lastTarget == null || !entity.equals(lastTarget)) continue;
            	        target = null;
						targetHide = entity;
					}
            	    else
    					target = entity;
					return;
				}
			}
		}
	}

	// find closest waypoint
	public Location findClosestWaypoint(NPCCustom npcCustom) {
		Location	locNPC = npcCustom.getLocation();
		Location	result = null;
    	double		minimumDistance = Double.MAX_VALUE;
    	for (Location waypoint : allWaypoints) {
    	    if (!waypoint.getWorld().equals(locNPC.getWorld())) continue;
    	    double	distance = waypoint.distanceSquared(locNPC);
    	    if (distance < minimumDistance) {
    	        minimumDistance = distance;
				result = waypoint;
    	    }
    	}
		return result;
	}

    // find item
	public ItemStack findItem(NPCCustom npcCustom) {
		return npcCustom.getEquipment().getItemInMainHand();
	}

    // find weapon
	public WeaponType findWeaponType(ItemStack item) {
		if (item == null) return WeaponType.HAND;
		Weapon	weapon = RpgCraft.getItemCustomRegistry().getWeapon(item);
		if (weapon != null) return weapon.getType();
		for (WeaponType weaponType: WeaponType.values()) {
			if (weaponType.getMaterial() == item.getType())
				return weaponType;
		}
		return WeaponType.HAND;
	}

    // attack/walk/search target
	public int attackTarget(NPCCustom npcCustom) {
		Navigator			navigator = npc.getNavigator();
		NavigatorParameters	parameters = navigator.getDefaultParameters();
		Location			closestWaypoint = findClosestWaypoint(npcCustom);
		// no target
		if (target == null && targetHide == null) {
			if (quote) quote = false;
			// heal
			npcCustom.heal(data.getHealth() / 10);
			if (isFlightType(npcCustom)) {
				setTargetFlight(npcCustom, navigator, closestWaypoint);
				if (!inChase) return 0;
			}
			else {
				if (!inChase) return 0;
				navigator.cancelNavigation();
			}
			// first no chase
			inChase = false;
			npc.getOrAddTrait(LookClose.class).lookClose(true);
			if (parameters.speedModifier() != data.getSpeed())
				parameters.speedModifier(data.getSpeed());
			lastTarget = null;
			lastTargetLocation = null;
			return 0;
		}
		// npc too far from waypoints (back to waypoints and full life)
		if (closestWaypoint != null && npcCustom.getLocation().distanceSquared(closestWaypoint) > data.getChaseRangeSquared()) {
			parameters.speedModifier(data.getSpeedCombat());
			if (isFlightType(npcCustom)) setTargetFlight(npcCustom, navigator, closestWaypoint);
			else setTargetGround(npcCustom, navigator, closestWaypoint);
			inChase = false;
			target = null;
			lastTarget = null;
			lastTargetLocation = null;
			return 12; // 6 secondes
		}
		// first chase
		if (!inChase) {
			inChase = true;
			npc.getOrAddTrait(LookClose.class).lookClose(false);
			if (parameters.speedModifier() != data.getSpeedCombat())
				parameters.speedModifier(data.getSpeedCombat());
		}
		// target is visible
		if (target != null) {
			if (!quote) {
				quote = true;
				npcCustom.attack();
			}
			int			now = Bukkit.getCurrentTick();
			double		width = npcCustom.getWidth();
			double		rangeClose = data.getAttackRangeClose() + width;
			double		rangeRanged = data.getAttackRangeRanged() + width;
			double		rangeBoss = rangeRanged + rangeRanged / 2;
			ItemStack	item = findItem(npcCustom);
			WeaponType	weaponType = findWeaponType(item);
			// spell close
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= rangeClose * rangeClose) {
				// spell close
				if (data.getSpellRate() > 0 && now >= nextSpellClose) {
					launchSpellClose(npcCustom);
					nextSpellClose = now + (int)(data.getSpellRate() * 20f);
				}
			}
			// spell ranged
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= (rangeRanged * rangeRanged)) {
				if (data.getSpellRate() > 0 && now >= nextSpellRanged) {
					launchSpellRanged(npcCustom);
					nextSpellRanged = now + (int)(data.getSpellRate() * 20f);
				}
			}
			// spell ranged boss
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= (rangeBoss * rangeBoss)) {
				if (data.getSpellRate() > 0 && now >= nextSpellRangedBoss) {
					launchSpellRangedBoss(npcCustom);
					nextSpellRangedBoss = now + (int)(data.getSpellRate() * 30f);
				}
			}
			// get physical range
			double	rangePhysical;
			switch (weaponType) {
				case BOW, CROSSBOW, STAFF, SPELLBOOK -> rangePhysical = rangeRanged;
				default -> {
					if (data.getDamage() <= 0 || data.getAttackRate() <= 0) rangePhysical = rangeRanged;
					else rangePhysical = rangeClose;
				}
			};
			// if target close: attack
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= rangePhysical * rangePhysical) {
				npc.faceLocation(target.getLocation());
				// no damage == no walking to target
				if (data.getDamage() <= 0 || data.getAttackRate() <= 0) flee(npcCustom, navigator);
				// physical attack
				else if (data.getAttackRate() > 0 && now >= nextAttack) {
					switch (weaponType) {
						case BOW -> {
							attackBow(npcCustom);
							flee(npcCustom, navigator);
						}
						case CROSSBOW -> {
							attackCrossBow(npcCustom);
							flee(npcCustom, navigator);
						}
						case STAFF -> {
							attackStaff(npcCustom, item);
							flee(npcCustom, navigator);
						}
						case SPELLBOOK -> {
							attackSpellBook(npcCustom, item);
							flee(npcCustom, navigator);
						}
						default -> attackSimple(npcCustom);
					}
					nextAttack = now + (int)(data.getAttackRate() * 20f);
				}
			}
			else {
				if (parameters.speedModifier() != data.getSpeedCombat())
					parameters.speedModifier(data.getSpeedCombat());
				if (isFlightType(npcCustom)) setTargetFlight(npcCustom, navigator, target);
				else setTargetGround(npcCustom, navigator, target);
				lastTarget = target;
				lastTargetLocation = target.getLocation();
			}
			isStuck(npcCustom);
		}
		// target is not visible
		else {
			if (quote) quote = false;
			// can't find target ()
			double	range = 2 + npcCustom.getWidth();
			if (npcCustom.getLocation().distanceSquared(lastTargetLocation) < range * range) {
				if (isFlightType(npcCustom)) setTargetFlight(npcCustom, navigator, closestWaypoint);
				else navigator.cancelNavigation();
				lastTarget = null;
				lastTargetLocation = null;
				return 0;
			}
			// go to last position of target
			if (isFlightType(npcCustom)) setTargetFlight(npcCustom, navigator, lastTargetLocation);
			else setTargetGround(npcCustom, navigator, lastTargetLocation);
			isStuck(npcCustom);
		}
		return 0;
	}

	private boolean isFlightType(NPCCustom npcCustom) {
		return switch (npcCustom.getType()) {
			case BLAZE, BREEZE, WITHER -> true;
			default -> false;
		};
	}

	private void setTargetFlight(NPCCustom npcCustom, Navigator navigator, LivingEntityCustom target) {
		if (target == null) return;
	    Location	npcLoc = npcCustom.getLocation();
	    Location	targetLoc = target.getLocation();
	    Vector direction = targetLoc.toVector().subtract(npcLoc.toVector());
	    double distance = direction.length();
	    if (distance < 3) return;
	    direction.normalize().multiply(5);
		navigator.setTarget(npcLoc.clone().add(direction));
	}

	private void setTargetFlight(NPCCustom npcCustom, Navigator navigator, Location targetLoc) {
		if (targetLoc == null) return;
		Location	npcLoc = npcCustom.getLocation();
	    Vector direction = targetLoc.toVector().subtract(npcLoc.toVector());
	    double distance = direction.length();
	    if (distance < 3) return;
	    direction.normalize().multiply(5);
		navigator.setTarget(npcLoc.clone().add(direction));
	}

	private void setTargetGround(NPCCustom npcCustom, Navigator navigator, LivingEntityCustom target) {
		if (target == null) return;
	    navigator.setTarget(target.getLocation());
	}

	private void setTargetGround(NPCCustom npcCustom, Navigator navigator, Location targetLoc) {
		if (targetLoc == null) return;
		navigator.setTarget(targetLoc);
	}

    public void flee(NPCCustom npcCustom, Navigator navigator) {
        LivingEntity npcEntity = npcCustom.getLivingEntity();
        if (npcEntity == null || target == null) return;
        Vector		awayDirection = npcEntity.getLocation().subtract(target.getLocation()).toVector().normalize();
        Location	fleeTarget = npcEntity.getLocation().add(awayDirection.multiply(8));
        navigator.setTarget(fleeTarget);
    }

	// check if npc is stuck
	public void isStuck(NPCCustom npcCustom) {
		Location	start = npcCustom.getEyeLocation().clone();
		start.setY(0);
		int			now = Bukkit.getCurrentTick();
		if (now >= nextStuck) {
			if (start.distanceSquared(lastLocation) < 2) {
				Location	targetLoc = target != null ? target.getLocation() : lastTargetLocation;
				Vector	direction = targetLoc.toVector().subtract(start.toVector()).normalize();
				npcCustom.setVelocity(npcCustom.getVelocity().add(direction.multiply(1).setY(0.5)));
			}
			lastLocation = start;
			nextStuck = now + nextStuckRate * 20;
		}
	}

	// attack bow
	private void attackBow(NPCCustom npcCustom) {
		World	world = npcCustom.getWorld();
		if (world == null) return;
		// animation
		npcCustom.swingMainHand();
		Vector	direction = target.getEyeLocation().subtract(npcCustom.getEyeLocation()).toVector();
		double	distance = direction.length();
		double	speed = 0.8 + Math.min(distance * 0.05, 1.8);
		direction = direction.normalize().multiply(speed);
		double	gravityCompensation = distance * 0.01;
		direction.setY(direction.getY() + gravityCompensation);
		Arrow	arrow = npcCustom.launchProjectile(Arrow.class);
		arrow.setVelocity(direction);
		arrow.setGravity(true);
		arrow.setDamage(data.getDamage());
		arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		RpgCraft.getSpellRegistry().setBow(arrow);
		world.playSound(npcCustom.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
	}

	// attack crossbow
	private void attackCrossBow(NPCCustom npcCustom) {
		World	world = npcCustom.getWorld();
		if (world == null) return;
		// animation
		npcCustom.swingMainHand();
		Vector	direction = target.getEyeLocation().subtract(npcCustom.getEyeLocation()).toVector();
		double	distance = direction.length();
		double	speed = 0.8 + Math.min(distance * 0.05, 1.8);
		direction = direction.normalize().multiply(speed);
		double	gravityCompensation = distance * 0.01;
		direction.setY(direction.getY() + gravityCompensation);
		Arrow	arrow = npcCustom.launchProjectile(Arrow.class);
		arrow.setVelocity(direction);
		arrow.setGravity(true);
		arrow.setDamage(data.getDamage());
		arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		RpgCraft.getSpellRegistry().setCrossBow(arrow);
		world.playSound(npcCustom.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f);
	}

	// attack staff
	private void attackStaff(NPCCustom npcCustom, ItemStack item) {
		// animation
		npcCustom.swingMainHand();
		RpgCraft.getSpellRegistry().launchStaff(npcCustom, target, item);
	}

	// attack spellbook
	private void attackSpellBook(NPCCustom npcCustom, ItemStack item) {
		// animation
		npcCustom.swingMainHand();
		RpgCraft.getSpellRegistry().launchSpellBook(npcCustom, target, item);
	}

	// attack simple
	private void attackSimple(NPCCustom npcCustom) {
		World	world = npcCustom.getWorld();
		if (world == null) return;
		// animation
		switch (npcCustom.getType()) {
			case PLAYER -> npcCustom.swingMainHand();
			case IRON_GOLEM -> npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
			case RAVAGER -> npcCustom.playEffect(EntityEffect.RAVAGER_ATTACK);
			default -> {}
		}
		// damage
		target.damage(data.getDamage(), CombatDamage.PHYSICAL, npcCustom);
		// knockback
		Vector	knock = target.getEyeLocation().subtract(npcCustom.getEyeLocation()).toVector();
		if (knock.lengthSquared() < 1.0E-6) knock = new Vector(0, 0, 1);
		else knock.normalize().multiply(0.3);
		target.setVelocity(target.getVelocity().add(knock));
		world.playSound(npcCustom.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1.0f, 1.0f);
	}

	// spell close
	private void launchSpellClose(NPCCustom npcCustom) {
		if (npcCustom.isSilence() > 0) return;
		TemplateType	templateType = data.getTemplateType();
		switch (templateType) {
			case MURLOC_MRGL -> RpgCraft.getSpellRegistry().expulse(npcCustom);
			case SCORPION -> RpgCraft.getSpellRegistry().poison(npcCustom, target, data.getRarity());
			case PALPOUTINE -> RpgCraft.getSpellRegistry().force(npcCustom, data.getRarity());
			case PALPOUTINE_CLONE -> RpgCraft.getSpellRegistry().forceClone(npcCustom, data.getRarity());
			case REDSTONE_GOLEM -> {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().strikeBack(npcCustom, data.getRarity());
			}
			case LEAPER -> RpgCraft.getSpellRegistry().impact(npcCustom, data.getRarity());
			default -> {}
		}
	}

	// spell ranged
	private void launchSpellRanged(NPCCustom npcCustom) {
		if (npcCustom.isSilence() > 0) return;
		TemplateType	templateType = data.getTemplateType();
		switch (templateType) {
			case MURLOC_MRGL -> RpgCraft.getSpellRegistry().launchWater(npcCustom, target, data.getRarity());
			case TAUREN_BLACK -> RpgCraft.getSpellRegistry().charge(npcCustom, target, data.getRarity());
			case ELEMENTAL_VOID -> RpgCraft.getSpellRegistry().demonChains(npcCustom, target, data.getRarity());
			case ELEMENTAL_WIND -> RpgCraft.getSpellRegistry().launchWind(npcCustom, target, data.getRarity());
			case ELEMENTAL_FIRE, PET_BRAISED -> RpgCraft.getSpellRegistry().launchFire(npcCustom, target, data.getRarity());
			case BIG_SPIDER -> RpgCraft.getSpellRegistry().launchSpiderEgg(npcCustom, target, data.getRarity(), false);
			case TARENTULA -> RpgCraft.getSpellRegistry().launchSpiderEgg(npcCustom, target, data.getRarity(), true);
			case REDSTONE_GOLEM -> {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().deadlyMagnet(npcCustom, data.getRarity());
			}
			case WHISPERER -> RpgCraft.getSpellRegistry().teleportWhisperer(npcCustom);
			case LEAPER -> RpgCraft.getSpellRegistry().leap(npcCustom, target, data.getRarity());
			case FROZER -> {
				Spellcaster	caster = (Spellcaster)npcCustom.getLivingEntity();
				caster.setSpell(Spellcaster.Spell.WOLOLO);
				RpgCraft.getSpellRegistry().fangsFrozer(npcCustom, data.getRarity());
				Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
				    if (!caster.isDead() && caster.isValid()) caster.setSpell(Spellcaster.Spell.NONE);
				}, 40L);
			}
			default -> {}
		}
	}

	private void launchSpellRangedBoss(NPCCustom npcCustom) {
		if (npcCustom.isSilence() > 0) return;
		TemplateType	templateType = data.getTemplateType();
		switch (templateType) {
			case PALPOUTINE -> RpgCraft.getSpellRegistry().lightning(npcCustom, target, data.getRarity());
			case MURLOC_MRGL -> RpgCraft.getSpellRegistry().spawnTrident(npcCustom, target, data.getRarity());
			case TARENTULA -> RpgCraft.getSpellRegistry().launchCobweb(npcCustom, target, data.getRarity());
			case REDSTONE_GOLEM -> {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().launchRedstone(npcCustom, target, data.getRarity());
			}
			case WHISPERER -> {
				Spellcaster	caster = (Spellcaster)npcCustom.getLivingEntity();
				caster.setSpell(Spellcaster.Spell.WOLOLO);
				RpgCraft.getSpellRegistry().fangs(npcCustom, target, data.getRarity());
				Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
				    if (!caster.isDead() && caster.isValid()) caster.setSpell(Spellcaster.Spell.NONE);
				}, 40L);
			}
			case FROZER -> {
				Spellcaster	caster = (Spellcaster)npcCustom.getLivingEntity();
				caster.setSpell(Spellcaster.Spell.FANGS);
				RpgCraft.getSpellRegistry().cloudFrozer(npcCustom, data.getRarity());
				Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
				    if (!caster.isDead() && caster.isValid()) caster.setSpell(Spellcaster.Spell.NONE);
				}, 40L);
			}
			default -> {}
		}
	}

	public void addAggro(LivingEntityCustom livingEntityCustom, double damage) {
		aggro.merge(livingEntityCustom.getUUID(), damage, (a, b) -> a + b);
	}

	public void cleanAggro() {
		aggro.clear();
	}

    // getter + setter

    public LivingEntityCustom getTarget() {
        return target;
    }

    public LivingEntityCustom getLastTarget() {
        return lastTarget;
    }

    public Location getLastTargetLocation() {
        return lastTargetLocation;
    }

	public LivingEntityCustom getTargetHide() {
		return targetHide;
	}

    public boolean inChase() {
        return inChase;
    }
}
