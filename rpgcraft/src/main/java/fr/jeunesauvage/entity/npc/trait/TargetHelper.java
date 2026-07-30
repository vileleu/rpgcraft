package fr.jeunesauvage.entity.npc.trait;

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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.npc.npcspell.NPCSpellManager;
import fr.jeunesauvage.entity.team.Team;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.equipable.weapon.launcher.LauncherManager;
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

public class TargetHelper {
	private final ItemCustomManager	itemCustomManager;
	private final NPCSpellManager	npcSpellManager;
	private final NPC				npc;
	private final AttributeHelper	a;
	private final Map<UUID, Double>	aggro;
	private final Set<Location>		allWaypoints;
    private LivingEntity			target;
    private LivingEntity			lastTarget;
    private Location				lastTargetLocation;
    private LivingEntity			targetHide;
    private boolean					inChase;
    private int						nextAttack;
    private int						nextSpellClose;
    private int						nextSpellRanged;
    private int						nextSpellRangedBoss;

    TargetHelper(ItemCustomManager itemCustomManager, NPCSpellManager npcSpellManager, NPC npc, AttributeHelper attributeHelper) {
		this.itemCustomManager = itemCustomManager;
		this.npcSpellManager = npcSpellManager;
		this.npc = npc;
		this.a = attributeHelper;
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
	public void findTargetPet(LivingEntity livingNPC) {
		target = null;
		targetHide = null;
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
    		    if (!(Bukkit.getEntity(entry.getKey()) instanceof LivingEntity livingTarget)) {
					it.remove();
					continue;
				}
				// target is damager ? 
				if (livingTarget.equals(livingNPC)) {
					it.remove();
					continue;
				}
				// target is dead
    		    if (livingTarget.isDead() || !livingTarget.isValid() || livingTarget.isInvulnerable()) {
					it.remove();
					continue;
				}
				// target too far from npc
				if (score > bestScore) {
                    bestScore = score;
        			if (!livingNPC.hasLineOfSight(livingTarget)) {
					    if (lastTarget == null || !livingTarget.equals(lastTarget)) continue;
						target = null;
						targetHide = livingTarget;
				    }
                    else
    		            target = livingTarget;
    		    }
    		}
		}
		if (target == null && targetHide == null)
			npc.getNavigator().setTarget(a.getOwner(), false);
	}

    // find better target (aggro or not in team) (for non pet)
	public void findTarget(LivingEntity livingNPC) {
		target = null;
		targetHide = null;
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
    		    if (!(Bukkit.getEntity(entry.getKey()) instanceof LivingEntity livingTarget)) {
					it.remove();
					continue;
				}
				// target is damager ? 
				if (livingTarget.equals(livingNPC)) {
					it.remove();
					continue;
				}
				// target is dead
    		    if (livingTarget.isDead() || !livingTarget.isValid() || livingTarget.isInvulnerable()) {
					it.remove();
					continue;
				}
				// check team (no aggro on boss)
				if (Team.isFriend(livingTarget, livingNPC) && TraitSentinel.isBoss(livingTarget)) {
					it.remove();
					continue;
				}
				// target too far from npc
    		    if (livingNPC.getLocation().distanceSquared(livingTarget.getLocation()) > a.getAggroRangeSquared() || livingTarget.isInvisible()) continue;
				if (score > bestScore) {
                    bestScore = score;
        			if (!livingNPC.hasLineOfSight(livingTarget)) {
					    if (lastTarget == null || !livingTarget.equals(lastTarget)) continue;
						target = null;
						targetHide = livingTarget;
				    }
                    else
    		            target = livingTarget;
    		    }
    		}
		}
		if (target == null && targetHide == null) {
			World	world = livingNPC.getWorld();
			for (LivingEntity livingTarget: world.getNearbyLivingEntities(livingNPC.getLocation(), a.getAggroRange(), a.getAggroRange(), a.getAggroRange())) {
				if (livingTarget.equals(livingNPC)) continue;
				// target choice
    		    if (livingTarget.isDead() || !livingTarget.isValid() || livingTarget.isInvisible() || livingTarget.isInvulnerable()) continue;
				// check team
				if (Team.isFriend(livingTarget, livingNPC)) continue;
        		if (!livingNPC.hasLineOfSight(livingTarget)) {
				    if (lastTarget == null || !livingTarget.equals(lastTarget)) continue;
                    target = null;
					targetHide = livingTarget;
				}
                else
    				target = livingTarget;
				return;
			}
		}
	}

	// find closest waypoint
	public Location findClosestWaypoint(LivingEntity livingNPC) {
		Location	locNPC = livingNPC.getLocation();
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

    // find weapon
	public WeaponType findWeaponType(LivingEntity livingNPC) {
		EntityEquipment	equipment = livingNPC.getEquipment();
		ItemStack	item = equipment.getItemInMainHand();
		if (item == null) return WeaponType.HAND;
		Weapon	weapon = itemCustomManager.getWeapon(item);
		if (weapon != null) return weapon.getType();
		for (WeaponType weaponType: WeaponType.values()) {
			if (weaponType.getMaterial() == item.getType())
				return weaponType;
		}
		return WeaponType.HAND;
	}

    // attack/walk/search target
	public int attackTarget(LivingEntity livingNPC) {
		Navigator			navigator = npc.getNavigator();
		NavigatorParameters	parameters = navigator.getDefaultParameters();
		// no target
		if (target == null && targetHide == null) {
			livingNPC.setHealth(a.getHealth());
			// first no chase
			if (!inChase) return 0;
			inChase = false;
			npc.getOrAddTrait(LookClose.class).lookClose(true);
			if (parameters.speedModifier() != a.getSpeed())
				parameters.speedModifier(a.getSpeed());
			navigator.cancelNavigation();
			lastTarget = null;
			lastTargetLocation = null;
			return 0;
		}
		// npc too far from waypoints (back to waypoints and full life)
		Location	closestWaypoint = findClosestWaypoint(livingNPC);
		if (closestWaypoint != null && livingNPC.getLocation().distanceSquared(closestWaypoint) > a.getChaseRangeSquared()) {
			livingNPC.setHealth(a.getHealth());
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
			if (parameters.speedModifier() != a.getSpeedCombat())
				parameters.speedModifier(a.getSpeedCombat());
		}
		// target is visible
		if (target != null) {
			int			now = Bukkit.getCurrentTick();
			double		width = livingNPC.getWidth() / 2d;
			double		range;
			WeaponType	weaponType;
			// if npc is player
			if (livingNPC.getType() == EntityType.PLAYER)
				weaponType = findWeaponType(livingNPC);
			// if npc is entity
			else
				weaponType = WeaponType.UNKNOWN;
			range = a.getAttackRangeRanged();
			// spell ranged
			if (livingNPC.getLocation().distanceSquared(target.getLocation()) <= (range * range + width * width)) {
				if (a.getSpellRate() > 0 && now >= nextSpellRanged) {
					launchSpellRanged(livingNPC);
					nextSpellRanged = now + (int)(a.getSpellRate() * 20f);
				}
			}
			range += range / 2;
			// spell ranged boss
			if (livingNPC.getLocation().distanceSquared(target.getLocation()) <= (range * range + width * width)) {
				if (a.getSpellRate() > 0 && now >= nextSpellRangedBoss) {
					launchSpellRangedBoss(livingNPC);
					nextSpellRangedBoss = now + (int)(a.getSpellRate() * 30f);
				}
			}
			// get physical range
			switch (weaponType) {
				case BOW, CROSSBOW, STAFF, SPELLBOOK -> range = a.getAttackRangeRanged();
				default -> range = a.getAttackRangeClose();
			};
			// if target close: attack
			if (livingNPC.getLocation().distanceSquared(target.getLocation()) <= (range * range + width * width)) {
				npc.faceLocation(target.getLocation());
				// spell close
				if (a.getSpellRate() > 0 && now >= nextSpellClose) {
					launchSpellClose(livingNPC);
					nextSpellClose = now + (int)(a.getSpellRate() * 20f);
				}
				// physical attack
				if (a.getAttackRate() > 0 && now >= nextAttack) {
					switch (weaponType) {
						case BOW -> attackBow(livingNPC);
						case CROSSBOW -> attackCrossBow(livingNPC);
						case STAFF -> attackStaff(livingNPC);
						case SPELLBOOK -> attackSpellBook(livingNPC);
						default -> attackSimple(livingNPC);
					}
					nextAttack = now + (int)(a.getAttackRate() * 20f);
				}
			}
			else {
				if (parameters.speedModifier() != a.getSpeedCombat())
					parameters.speedModifier(a.getSpeedCombat());
				navigator.setTarget(target, false);
				lastTarget = target;
				lastTargetLocation = target.getLocation();
			}
		}
		// target is not visible
		else {
			// can't find target ()
			double	width = livingNPC.getWidth() / 2d;
			if (livingNPC.getLocation().distanceSquared(lastTargetLocation) < 3 * 3 + width * width) {
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
	private void attackBow(LivingEntity livingNPC) {
		// animation
		livingNPC.swingMainHand();
		Vector	direction = target.getEyeLocation().subtract(livingNPC.getEyeLocation()).toVector();
		double	distance = direction.length();
		double	speed = 0.8 + Math.min(distance * 0.05, 1.8);
		direction = direction.normalize().multiply(speed);
		double	gravityCompensation = distance * 0.01;
		direction.setY(direction.getY() + gravityCompensation);
		Arrow	arrow = livingNPC.launchProjectile(Arrow.class);
		arrow.setVelocity(direction);
		arrow.setGravity(true);
		arrow.setDamage(a.getDamage());
		arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		Data.setBoolean(arrow.getPersistentDataContainer(), LauncherManager.getBowKey());
		livingNPC.getWorld().playSound(livingNPC.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
	}

	// attack crossbow
	private void attackCrossBow(LivingEntity livingNPC) {
		// animation
		livingNPC.swingMainHand();
		Vector	direction = target.getEyeLocation().subtract(livingNPC.getEyeLocation()).toVector();
		double	distance = direction.length();
		double	speed = 0.8 + Math.min(distance * 0.05, 1.8);
		direction = direction.normalize().multiply(speed);
		double	gravityCompensation = distance * 0.01;
		direction.setY(direction.getY() + gravityCompensation);
		Arrow	arrow = livingNPC.launchProjectile(Arrow.class);
		arrow.setVelocity(direction);
		arrow.setGravity(true);
		arrow.setDamage(a.getDamage());
		arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
		Data.setBoolean(arrow.getPersistentDataContainer(), LauncherManager.getCrossbowKey());
		livingNPC.getWorld().playSound(livingNPC.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f);
	}

	// attack staff
	private void attackStaff(LivingEntity livingNPC) {
		LauncherManager	launcherManager = itemCustomManager.getWeaponManager().getLauncherManager();
		// animation
		livingNPC.swingMainHand();
		launcherManager.launchStaff(livingNPC, target);
	}

	// attack spellbook
	private void attackSpellBook(LivingEntity livingNPC) {
		LauncherManager	launcherManager = itemCustomManager.getWeaponManager().getLauncherManager();
		// animation
		livingNPC.swingMainHand();
		launcherManager.launchSpellBook(livingNPC, target);
	}

	// attack simple
	private void attackSimple(LivingEntity livingNPC) {
		// animation
		if (livingNPC.getType() == EntityType.PLAYER)
			livingNPC.swingMainHand();
		else if (livingNPC.getType() == EntityType.IRON_GOLEM)
			livingNPC.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
		// damage
		target.damage(a.getDamage(), livingNPC);
		// knockback
		Vector	knock = target.getEyeLocation().subtract(livingNPC.getEyeLocation()).toVector().normalize().multiply(0.3);
		target.setVelocity(knock);
		livingNPC.getWorld().playSound(livingNPC.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1.0f, 1.0f);
		SoundPacket.playSound(npc, SoundType.ATTACK);
	}

	// spell close
	private void launchSpellClose(LivingEntity livingNPC) {
		EntityType	entityType = livingNPC.getType();
		String		name = livingNPC.getName();
		if (name == null) return;
		if (entityType == EntityType.PLAYER) {
			if (name.equals("Mrgl The Oracle"))
				npcSpellManager.expulse(livingNPC, 4);
		}
		else if (entityType == EntityType.SPIDER) {
			return;
		}
		else if (entityType == EntityType.IRON_GOLEM) {
			if (name.equals("Redstone Golem"))
				npcSpellManager.strikeBack(livingNPC, a.getLevel());
		}
	}

	// spell ranged
	private void launchSpellRanged(LivingEntity livingNPC) {
		EntityType	entityType = livingNPC.getType();
		String		name = livingNPC.getName();
		if (name == null) return;
		if (entityType == EntityType.PLAYER) {
			if (name.equals("Mrgl The Oracle"))
				npcSpellManager.launchWater(livingNPC, target, a.getLevel());
		}
		if (entityType == EntityType.SPIDER) {
			if (name.equals("Spider 6"))
				npcSpellManager.launchSpiderEgg(livingNPC.getLocation(), target, a.getLevel(), false);
			else if (name.equals("Spider 5"))
				npcSpellManager.launchSpiderEgg(livingNPC.getLocation(), target, a.getLevel(), true);
		}
		else if (entityType == EntityType.IRON_GOLEM) {
			if (name.equals("Redstone Golem"))
				npcSpellManager.redstoneMagnet(livingNPC, target, a.getAttackRangeRanged());
		}
	}

	private void launchSpellRangedBoss(LivingEntity livingNPC) {
		EntityType	entityType = livingNPC.getType();
		String		name = livingNPC.getName();
		if (name == null) return;
		if (entityType == EntityType.PLAYER) {
			if (name.equals("Mrgl The Oracle"))
				npcSpellManager.spawnTrident(livingNPC, a.getLevel());
		}
		if (entityType == EntityType.SPIDER) {
			if (name.equals("Spider 5"))
				npcSpellManager.launchCobweb(livingNPC.getLocation(), target);
		}
		else if (entityType == EntityType.IRON_GOLEM) {
			if (name.equals("Redstone Golem"))
				npcSpellManager.launchRedstoneBlock(livingNPC, target, a.getLevel());
		}
	}

	public void addAggro(LivingEntity livingTarget, double damage) {
		aggro.merge(livingTarget.getUniqueId(), damage, (a, b) -> a + b);
	}

	public void cleanAggro() {
		aggro.clear();
	}

    // getter + setter

    public LivingEntity getTarget() {
        return target;
    }

    public LivingEntity getLastTarget() {
        return lastTarget;
    }

    public Location getLastTargetLocation() {
        return lastTargetLocation;
    }

	public LivingEntity getTargetHide() {
		return targetHide;
	}

    public boolean inChase() {
        return inChase;
    }
}
