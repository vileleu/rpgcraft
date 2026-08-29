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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.sound.SoundPacket;
import fr.jeunesauvage.sound.SoundType;
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
    		    if (!entity.isPresent() || entity.isInvulnerable() || entity.isInvisible()) {
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
				if (entity.isGrouped(npcCustom)) {
					it.remove();
					continue;
				}
				// target is dead
    		    if (!entity.isPresent() || entity.isInvulnerable() || entity.isInvisible()) {
					it.remove();
					continue;
				}
				// check team (no aggro on friendly target)
				if (entity.isFriend(npcCustom) && !(entity instanceof PlayerCustom)) {
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
				if (entity == null || entity.equals(npcCustom) || entity.isFriend(npcCustom)) continue;
				// target choice
    		    if (!entity.isPresent() || entity.isInvisible() || entity.isInvulnerable()) continue;
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
			parameters.speedModifier(2);
			navigator.setTarget(closestWaypoint);
			inChase = false;
			target = null;
			lastTarget = null;
			lastTargetLocation = null;
			return 160; // 8 secondes
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
				if (data.getDamage() == 0) navigator.setTarget(null, false);
				// physical attack
				else if (data.getAttackRate() > 0 && now >= nextAttack) {
					switch (weaponType) {
						case BOW -> {
							attackBow(npcCustom);
							navigator.setTarget(null, false);
						}
						case CROSSBOW -> {
							attackCrossBow(npcCustom);
							navigator.setTarget(null, false);
						}
						case STAFF -> {
							attackStaff(npcCustom, item);
							navigator.setTarget(null, false);
						}
						case SPELLBOOK -> {
							attackSpellBook(npcCustom, item);
							navigator.setTarget(null, false);
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
			// can't find target ()
			double	width = npcCustom.getWidth() / 2d;
			if (npcCustom.getLocation().distanceSquared(lastTargetLocation) < 3 * 3 + width * width) {
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
		RpgCraft.getSpellRegistry().launchSpellBook(npcCustom, item);
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
		// damage
		target.damage(data.getDamage(), CombatDamage.PHYSICAL, npcCustom);
		// knockback
		Vector	knock = target.getEyeLocation().subtract(npcCustom.getEyeLocation()).toVector().normalize().multiply(0.3);
		target.setVelocity(knock);
		world.playSound(npcCustom.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1.0f, 1.0f);
		SoundPacket.playSound(npcCustom, SoundType.ATTACK);
	}

	// spell close
	private void launchSpellClose(NPCCustom npcCustom) {
		EntityType	entityType = npcCustom.getType();
		String		name = npcCustom.getName();
		if (name == null) return;
		if (entityType == EntityType.PLAYER) {
			if (name.equals("Mrgl The Oracle"))
				RpgCraft.getSpellRegistry().expulse(npcCustom, 5);
		}
		else if (entityType == EntityType.SPIDER) {
			return;
		}
		else if (entityType == EntityType.IRON_GOLEM) {
			if (name.equals("Redstone Golem")) {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().strikeBack(npcCustom, Rarity.fromInt(data.getLevel() / 10));
			}
		}
	}

	// spell ranged
	private void launchSpellRanged(NPCCustom npcCustom) {
		EntityType	entityType = npcCustom.getType();
		String		name = npcCustom.getName();
		if (name == null) return;
		if (entityType == EntityType.PLAYER) {
			if (name.equals("Mrgl The Oracle"))
				RpgCraft.getSpellRegistry().launchWater(npcCustom, target, data.getLevel());
		}
		if (entityType == EntityType.BLAZE) {
			RpgCraft.getSpellRegistry().launchFire(npcCustom, target, data.getLevel());
		}
		if (entityType == EntityType.SPIDER) {
			if (name.equals("Spider 6"))
				RpgCraft.getSpellRegistry().launchSpiderEgg(npcCustom.getLocation(), target, data.getLevel(), false);
			else if (name.equals("Spider 5"))
				RpgCraft.getSpellRegistry().launchSpiderEgg(npcCustom.getLocation(), target, data.getLevel(), true);
		}
		else if (entityType == EntityType.IRON_GOLEM) {
			if (name.equals("Redstone Golem")) {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().deadlyMagnet(npcCustom, Rarity.fromInt(data.getLevel() / 10));
			}
		}
	}

	private void launchSpellRangedBoss(NPCCustom npcCustom) {
		EntityType	entityType = npcCustom.getType();
		String		name = npcCustom.getName();
		if (name == null) return;
		if (entityType == EntityType.PLAYER) {
			if (name.equals("Mrgl The Oracle"))
				RpgCraft.getSpellRegistry().spawnTrident(npcCustom, target, data.getLevel());
		}
		if (entityType == EntityType.SPIDER) {
			if (name.equals("Spider 5"))
				RpgCraft.getSpellRegistry().launchCobweb(npcCustom.getLocation(), target);
		}
		else if (entityType == EntityType.IRON_GOLEM) {
			if (name.equals("Redstone Golem")) {
				npcCustom.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
				RpgCraft.getSpellRegistry().launchRedstoneBlock(npcCustom, target, data.getLevel());
			}
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
