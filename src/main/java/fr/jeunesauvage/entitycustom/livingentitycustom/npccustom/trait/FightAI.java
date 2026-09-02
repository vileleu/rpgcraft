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
import org.bukkit.entity.EntityType;
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
		Waypoints			waypoints = npc.getOrAddTrait(Waypoints.class);
		WaypointProvider	provider = waypoints.getCurrentProvider();
		if (provider instanceof LinearWaypointProvider linear) {
		    for (Waypoint waypoint : linear.waypoints()) {
		        allWaypoints.add(waypoint.getLocation());
		    }
		}
    }

    // find better target (aggro or not in team) (for pet)
	public void findTargetPet(NPCCustom npcCustom) {
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
				if (entity.isGrouped(npcCustom)) {
					it.remove();
					continue;
				}
				// target is dead
    		    if (!entity.isPresent() || entity.isInvulnerable() || entity.isInvisible() || entity.isCreative()) {
					it.remove();
					continue;
				}
				// target too far from npc
				if (score > bestScore) {
                    bestScore = score;
        			if (!npcCustom.hasLineOfSight(entity)) {
					    if (entity == null || !entity.equals(entity)) continue;
						target = null;
						targetHide = entity;
				    }
                    else
    		            target = entity;
    		    }
    		}
		}
		if (target == null && targetHide == null) {
			LivingEntityCustom	owner = data.getOwner();
			if (owner != null)
				npc.getNavigator().setTarget(owner.getLivingEntity(), false);
			else
				npc.getNavigator().setTarget(null, false);
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
				RpgCraft.debug("newScore = " + newScore);
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
		// no target
		if (target == null && targetHide == null) {
			if (quote) quote = false;
			// heal
			npcCustom.heal(data.getHealth() / 10);
			// first no chase
			if (!inChase) return 0;
			inChase = false;
			npc.getOrAddTrait(LookClose.class).lookClose(true);
			if (parameters.speedModifier() != data.getSpeed())
				parameters.speedModifier(data.getSpeed());
			navigator.cancelNavigation();
			lastTarget = null;
			lastTargetLocation = null;
			return 0;
		}
		// npc too far from waypoints (back to waypoints and full life)
		Location	closestWaypoint = findClosestWaypoint(npcCustom);
		if (closestWaypoint != null && npcCustom.getLocation().distanceSquared(closestWaypoint) > data.getChaseRangeSquared()) {
			EntityType	entityType = npcCustom.getType();
			parameters.speedModifier(entityType == EntityType.EVOKER ? 1 : 2);
			navigator.setTarget(closestWaypoint);
			inChase = false;
			target = null;
			lastTarget = null;
			lastTargetLocation = null;
			return 120; // 6 secondes
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
			double		width = npcCustom.getWidth() / 2d;
			double		range;
			ItemStack	item = findItem(npcCustom);
			WeaponType	weaponType = findWeaponType(item);
			range = data.getAttackRangeRanged();
			// spell ranged
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= (range * range + width * width)) {
				if (data.getSpellRate() > 0 && now >= nextSpellRanged) {
					launchSpellRanged(npcCustom);
					nextSpellRanged = now + (int)(data.getSpellRate() * 20f);
				}
			}
			range += range / 2;
			// spell ranged boss
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= (range * range + width * width)) {
				if (data.getSpellRate() > 0 && now >= nextSpellRangedBoss) {
					launchSpellRangedBoss(npcCustom);
					nextSpellRangedBoss = now + (int)(data.getSpellRate() * 30f);
				}
			}
			// get physical range
			switch (weaponType) {
				case BOW, CROSSBOW, STAFF, SPELLBOOK -> range = data.getAttackRangeRanged();
				default -> {
					if (data.getDamage() == 0) range = data.getAttackRangeRanged();
					else range = data.getAttackRangeClose();
				}
			};
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= (data.getAttackRangeClose() * data.getAttackRangeClose() + width * width)) {
				// spell close
				if (data.getSpellRate() > 0 && now >= nextSpellClose) {
					launchSpellClose(npcCustom);
					nextSpellClose = now + (int)(data.getSpellRate() * 20f);
				}
			}
			// if target close: attack
			if (npcCustom.getLocation().distanceSquared(target.getLocation()) <= (range * range + width * width)) {
				npc.faceLocation(target.getLocation());
				// no damage == no walking to target
				if (data.getDamage() == 0) flee(npcCustom, navigator);
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
				navigator.setTarget(target.getLivingEntity(), false);
				lastTarget = target;
				lastTargetLocation = target.getLocation();
			}
		}
		// target is not visible
		else {
			if (quote) quote = false;
			// can't find target ()
			double	width = npcCustom.getWidth() / 2d;
			if (npcCustom.getLocation().distanceSquared(lastTargetLocation) < 2 * 2 + width * width) {
				navigator.cancelNavigation();
				lastTarget = null;
				lastTargetLocation = null;
				return 0;
			}
			// go to last position of target
			navigator.setTarget(lastTargetLocation);
		}
		return 0;
	}

    public void flee(NPCCustom npcCustom, Navigator navigator) {
        LivingEntity npcEntity = npcCustom.getLivingEntity();
        if (npcEntity == null || target == null) return;
        Vector		awayDirection = npcEntity.getLocation().subtract(target.getLocation()).toVector().normalize();
        Location	fleeTarget = npcEntity.getLocation().add(awayDirection.multiply(8));
        navigator.setTarget(fleeTarget);
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
		if (npcCustom.getType() == EntityType.PLAYER)
			npcCustom.swingMainHand();
		else if (npcCustom.getType() == EntityType.IRON_GOLEM)
			npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
		else if (npcCustom.getType() == EntityType.RAVAGER)
			npcCustom.playEffect(EntityEffect.RAVAGER_ATTACK);
		// damage
		target.damage(data.getDamage(), CombatDamage.PHYSICAL, npcCustom);
		// knockback
		Vector	knock = target.getEyeLocation().subtract(npcCustom.getEyeLocation()).toVector().normalize().multiply(0.3);
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
			case GOLEM_REDSTONE -> {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().strikeBack(npcCustom, data.getRarity());
			}
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
			case ELEMENTAL_WIND -> RpgCraft.getSpellRegistry().launchWind(npcCustom, target);
			case ELEMENTAL_FIRE, PET_BRAISED -> RpgCraft.getSpellRegistry().launchFire(npcCustom, target, data.getRarity());
			case SPIDER_BIG -> RpgCraft.getSpellRegistry().launchSpiderEgg(npcCustom, target, data.getRarity(), false);
			case SPIDER_BOSS -> RpgCraft.getSpellRegistry().launchSpiderEgg(npcCustom, target, data.getRarity(), true);
			case GOLEM_REDSTONE -> {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().deadlyMagnet(npcCustom, data.getRarity());
			}
			case WHISPERER -> {
				Spellcaster	caster = (Spellcaster)npcCustom.getLivingEntity();
				caster.setSpell(Spellcaster.Spell.FANGS);
				RpgCraft.getSpellRegistry().teleportWhisperer(npcCustom);
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
			case ELEMENTAL_WIND, ELEMENTAL_FIRE, PET_BRAISED -> RpgCraft.getSpellRegistry().teleportElemental(npcCustom, target);
			case PALPOUTINE -> RpgCraft.getSpellRegistry().lightning(npcCustom, target, data.getRarity());
			case MURLOC_MRGL -> RpgCraft.getSpellRegistry().spawnTrident(npcCustom, target, data.getRarity());
			case SPIDER_BOSS -> RpgCraft.getSpellRegistry().launchCobweb(npcCustom, target, data.getRarity());
			case GOLEM_REDSTONE -> {
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
