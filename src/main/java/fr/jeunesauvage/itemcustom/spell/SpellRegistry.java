package fr.jeunesauvage.itemcustom.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.MobCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.EquipableManager;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.sound.SoundManager;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class SpellRegistry {
	public static final int						TIME_DEADLYMAGNET = 4;
	public static final int						TIME_ESCAPE = 8;
	public static final int						TIME_HOLYLAND = 10;
	public static final int						TIME_HOLYSHIELD = 8;
	public static final int						TIME_DRAGONSKIN = 20;
	public static final int						TIME_METAMORPH = 40;
	public static final int						TIME_STRIKEBACK = 10;
	public static final int						TIME_HUNT = 15;
	public static final int						TIME_ICETRAP = 30;
	private final NamespacedKey					KEY_FIREBALL = new NamespacedKey(RpgCraft.name(), "fireball");
	private final NamespacedKey					KEY_SHADOWWORD = new NamespacedKey(RpgCraft.name(), "shadowword");
	private final NamespacedKey					KEY_BOW = new NamespacedKey(RpgCraft.name(), "bow");
	private final NamespacedKey					KEY_CROSSBOW = new NamespacedKey(RpgCraft.name(), "crossbow");
	private final NamespacedKey					KEY_STAFF = new NamespacedKey(RpgCraft.name(), "staff");
	private final NamespacedKey					KEY_SPELLBOOK = new NamespacedKey(RpgCraft.name(), "spellbook");
	private final Map<UUID, Integer>			kneeBreaker = new HashMap<>();
	private final Map<UUID, BukkitTask>			leap = new HashMap<>();
	private final Map<UUID, Integer>			stealth = new HashMap<>();
	private final Map<UUID, Integer>			coldBlood = new HashMap<>();
	private final Map<UUID, Integer>			holyBomb = new HashMap<>();
	private final Map<UUID, DataTask<Integer>>	holyShield = new HashMap<>();
	private final Map<UUID, Integer>			dragonSkinPlayer = new HashMap<>();
	private final Map<UUID, DataTask<Integer>>	strikeBack = new HashMap<>();
	private final Map<UUID, Boolean>			canUseStrikeback = new HashMap<>();
	private final Map<UUID, Integer>			explosiveShot = new HashMap<>();
	private final Map<UUID, NPCCustom>			pets = new HashMap<>();
	private final Map<UUID, NPCCustom>			braiseds = new HashMap<>();
	private static final int					TIME_TEMPORARYNPC = 60; // seconds
	private final Map<UUID, Integer>			npcTemporary = new HashMap<>();
	private BukkitTask							taskNPCTemporary;

	/////////////////////
	///*   WARRIOR   *///
	/////////////////////

    // knee breaker

	public void kneeBreaker(LivingEntityCustom launcher, Rarity rarity) {
		addKneeBreaker(launcher.getUUID(), rarity.getNumber());
		SoundManager.playSound(launcher, "spell_kneebreaker");
		particleKneeBreaker(launcher.getLocation());
	}

    private void particleKneeBreaker(Location loc) {
        loc.getWorld().spawnParticle(Particle.CRIT, loc, 30, 0.3, 0.3, 0.3, 0.05);
    }

	private void addKneeBreaker(UUID uuid, int level) {
		kneeBreaker.put(uuid, level);
	}

	public int removeKneeBreaker(UUID uuid) {
		Integer	level = kneeBreaker.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasKneeBreaker(UUID uuid) {
		return kneeBreaker.containsKey(uuid);
	}

    // whirlwind

	public void whirlwind(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location	center = launcher.getLocation();
        double      radius = 4;
		// damage = physical damage * (30% for rarity 1, 40% for rarity 2, ...)
		double	    damage = StatSecondary.PHYSICAL_DAMAGE.getAmount(launcher);
		damage *= (rarity.getNumber() + 2) / 10d;
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null || target.isGrouped(launcher)) continue;
			target.damage(damage, CombatDamage.PHYSICAL, launcher, true);
		}
		SoundManager.playSound(launcher, "spell_whirlwind");
		particleWhirlwind(center, radius);
	}

	private void particleWhirlwind(Location loc, double radius) {
		World	world = loc.getWorld();
		int points = 100;
		for (int i = 0; i < points; i++) {
		    double	theta = Math.random() * 2 * Math.PI;
		    double	phi = Math.acos(2 * Math.random() - 1);
		    double	x = radius * Math.sin(phi) * Math.cos(theta);
		    double	y = radius * Math.cos(phi);
		    double	z = radius * Math.sin(phi) * Math.sin(theta);
		    world.spawnParticle(Particle.SWEEP_ATTACK, loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
		}
		points = 20;
		for (int i = 0; i < points; i++) {
		    double	theta = Math.random() * 2 * Math.PI;
		    double	phi = Math.acos(2 * Math.random() - 1);
		    double	x = radius * Math.sin(phi) * Math.cos(theta);
		    double	y = radius * Math.cos(phi);
		    double	z = radius * Math.sin(phi) * Math.sin(theta);
		    world.spawnParticle(Particle.CLOUD, loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
		}
		new BukkitRunnable() {
		    double time = 0;
		    @Override
		    public void run() {
		        time += 0.2;
		        for (int i = 0; i < 40; i++) {
		            double	angle = i * 0.3 + time;
		            double	x = Math.cos(angle) * radius;
		            double	z = Math.sin(angle) * radius;
		            double	y = (i % 10) * 0.2;
		            world.spawnParticle(Particle.GUST, loc.clone().add(x, y, z), 1, 0, 0, 0, 0);
		        }
				if (time >= 1)
					cancel();
		    }
		
		}.runTaskTimer(RpgCraft.instance(), 0L, 2L);
	}

	// leap

	public void leap(LivingEntityCustom launcher, Rarity rarity) {
		Location	center = launcher.getLocation();
    	Vector		dir = center.getDirection().normalize();
    	dir.setY(0.5);
    	launcher.setVelocity(dir.multiply(1.5));
		addLeap(launcher, rarity.getNumber());
    	SoundManager.playSound(launcher, "spell_leap");
		particleLeap(center);
	}

	private void particleLeap(Location loc) {
    	loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.3, 0.3, 0.3, 0.05);
	}

	public boolean isLanding(LivingEntityCustom launcher) {
    	BoundingBox	box = launcher.getBoundingBox();
		if (box == null) return true;
		World	    world = launcher.getWorld();
		if (world == null) return true;
    	double		y = box.getMinY() - 0.01;
    	for (double x = box.getMinX(); x <= box.getMaxX(); x += 0.3) {
    	    for (double z = box.getMinZ(); z <= box.getMaxZ(); z += 0.3) {
    	        Block block = world.getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    	        if (block.getType().isSolid())
    	            return true;
    	    }
    	}
    	return false;
	}

	public void addLeap(LivingEntityCustom launcher, int level) {
		UUID	uuid = launcher.getUUID();
		if (hasLeap(uuid)) return;
		leap.put(uuid, new BukkitRunnable() {
			boolean	cancel = false;
		    @Override
		    public void run() {
				if (cancel) {
					removeLeap(uuid);
					return;
				}
				if (!isLanding(launcher)) return;
	    		Location	loc = launcher.getLocation();
				double		radius = 6;
				double 		damage = level * 4 + 2;
				explosion(launcher, loc, radius, damage, 0.5, 0);
				cancel = true;
		    }
		}.runTaskTimer(RpgCraft.instance(), 5L, 2L));
	}

	private void removeLeap(UUID uuid) {
		BukkitTask	task = leap.remove(uuid);
		if (task != null)
			task.cancel();
	}

	public boolean hasLeap(UUID uuid) {
		return leap.containsKey(uuid);
	}

	// deadly magnet

	public void deadlyMagnet(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location	center = launcher.getLocation();
        double      radius = 9 + rarity.getNumber();
		double		forceMax = 3;
		double		aggro = rarity.getNumber() * 2;
		new BukkitRunnable() {
		    int			ticks = 0;
		    final int	maxTicks = TIME_DEADLYMAGNET * 4; // 4 ticks = 1 seconds
		    @Override
		    public void run() {
				EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		        for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
					LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		            if (target == null || target.isGrouped(launcher)) continue;
					if (target.isBoss()) continue;
		            Vector	toPlayer = center.toVector().subtract(target.getLocation().toVector());
		            double	distance = toPlayer.length();
		            if (distance < 0.5) continue;
		            double	force = Math.pow(distance / radius, 2) * forceMax;
		            Vector	velocity = toPlayer.normalize().multiply(force);
		            target.setVelocity(velocity);
					if (target instanceof NPCCustom npcCustom) npcCustom.addAggro(launcher, aggro);
					else if (target instanceof MobCustom mobCustom) mobCustom.setTarget(launcher);
					particleEntityDeadlyMagnet(target.getLocation());
		        }
				particleDeadlyMagnet(center, radius, ticks, maxTicks);
		        ticks++;
		        if (ticks >= maxTicks)
		            cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 5L);
		SoundManager.playSound(launcher, "spell_deadlymagnet");
	}

	private void particleEntityDeadlyMagnet(Location loc) {
		World		world = loc.getWorld();
    	Location	from = loc.add(0, 1, 0);
    	Vector		dir = loc.toVector().subtract(from.toVector()).normalize();
    	for (int i = 0; i < 5; i++) {
    	    Location particleLoc = from.clone().add(dir.clone().multiply(i * 0.5));
    	    world.spawnParticle(Particle.END_ROD, particleLoc, 1);
    	}
	}

	private void particleDeadlyMagnet(Location center, double radius, double tick, double maxTicks) {
    	World	world = center.getWorld();
		int		points = 16;
    	double	progress = tick / maxTicks;
    	double	angleOffset = tick * 0.3;
    	double	heightProgress = (double)tick / maxTicks;
    	double	currentHeight = heightProgress * 2.0;
    	double	tiltAngle = Math.toRadians(25);
    	for (int i = 0; i < points; i++) {
    	    double angle = angleOffset + (2 * Math.PI * i / points);
    	    Vector	point = new Vector(Math.cos(angle) * 2, 0, Math.sin(angle) * 2);
    	    point.rotateAroundX(tiltAngle);
    	    point.setY(point.getY() + currentHeight);
    	    Location	particleLoc = center.clone().add(point);
    	    world.spawnParticle(Particle.ELECTRIC_SPARK, particleLoc, 1, 0, 0, 0, 0);
    	}
    	for (int i = 0; i < points; i++) {
    	    double	startAngle = (Math.PI * 2 * i) / (double)points;
    	    double	currentAngle = startAngle + tick * 0.25;
    	    double	currentRadius = radius * (1.0 - progress);
    	    double	x = Math.cos(currentAngle) * currentRadius;
    	    double	z = Math.sin(currentAngle) * currentRadius;
    	    world.spawnParticle(Particle.SWEEP_ATTACK, center.clone().add(x, 0.2, z), 1);
    	}
	}

	/////////////////////
	///*  PYROMANCER *///
	/////////////////////

	// fireball

	public void fireBall(LivingEntityCustom launcher, Rarity rarity) {
	    Vector	direction = launcher.getEyeLocation().getDirection();
    	direction.normalize();
		Fireball 				fireball = launcher.launchProjectile(Fireball.class);
		PersistentDataContainer	pdc = fireball.getPersistentDataContainer();
		Data.setInteger(pdc, KEY_FIREBALL, rarity.getNumber());
		fireball.setYield(0);
		fireball.setVelocity(direction.multiply(1.5));
		SoundManager.playSound(launcher, "spell_fireball");
		particleFireBall(fireball);
	}

	private void particleFireBall(Fireball fireball) {
		World	world = fireball.getWorld();
		fireball.getWorld().spawnParticle(Particle.FLAME, fireball.getLocation(), 30, 0.5, 0.5, 0.5);
		new BukkitRunnable() {
		    @Override
		    public void run() {
		        if (fireball.isDead() || !fireball.isValid()) {
		            cancel();
		            return;
		        }
		        Location loc = fireball.getLocation();
		        world.spawnParticle(Particle.FLAME, loc, 5, 0.1, 0.1, 0.1, 0.0);
		        world.spawnParticle(Particle.END_ROD, loc, 5, 0.1, 0.1, 0.1, 0.0);
		    }
		}.runTaskTimer(RpgCraft.instance(), 2L, 1L);
	}

	public boolean isFireball(Projectile projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.hasBoolean(pdc, KEY_FIREBALL);
	}

	public int getFireballRarity(Projectile projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.getInteger(pdc, KEY_FIREBALL);
	}

	// teleport

	public void teleport(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
    	Location	startLoc = launcher.getLocation();
    	Vector		direction = startLoc.getDirection();
		double		maxDistance = 4 + rarity.getNumber() * 3;
    	direction.setY(0);
    	direction.normalize();
    	double		step = 0.5;
    	Location	nextLoc = startLoc.clone();
    	for (double d = 0; d <= maxDistance; d += step) {
    	    Location	checkLoc = startLoc.clone().add(direction.clone().multiply(d));
    	    Block		block = world.getBlockAt(checkLoc);
    	    if (block.getType().isSolid()) {
    	        nextLoc = startLoc.clone().add(direction.clone().multiply(d - 1));
    	        break;
    	    }
    	    nextLoc = checkLoc.clone();
    	}
		int	y = getHighestSolidBlockY(world, nextLoc.getBlockX(), nextLoc.getBlockZ(), nextLoc.getBlockY()) + 1;
		if (y < startLoc.getY())
    		nextLoc.setY(y);
    	SoundManager.playSound(launcher, "spell_teleportation");
		particleTeleport(startLoc);
		launcher.setVelocity(new Vector(0,0,0));
		launcher.setFallDistance(0f);
    	launcher.teleport(nextLoc);
		SoundManager.playSound(launcher, "spell_teleportation");
		particleTeleport(nextLoc);
    }

	private int getHighestSolidBlockY(World world, int x, int z, int maxY) {
	    maxY = Math.min(maxY, world.getMaxHeight() - 1);
	    for (int y = maxY; y >= world.getMinHeight(); y--) {
	        Block block = world.getBlockAt(x, y, z);
	        if (block.getType().isSolid())
	            return y;
	    }
	    return world.getMinHeight();
	}

	private void particleTeleport(Location loc) {
		World	world = loc.getWorld();
		new BukkitRunnable() {
		    double	angle = 0;
		    double	radius = 1.5;
		    @Override
		    public void run() {
		        if (angle >= 4) {
		            cancel();
		            return;
		        }
		        for (int i = 0; i < 6; i++) {
		            double theta = angle + (i * Math.PI / 3);
		            double x = Math.cos(theta) * radius;
		            double z = Math.sin(theta) * radius;
		            Location particleLoc = loc.clone().add(x, 1.0, z);
		            world.spawnParticle(Particle.PORTAL, particleLoc, 1, 0, 0, 0, 0);
		        }
		        angle += 0.4;
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 2L);
	}

	// mana thirst

	public void manaThirst(LivingEntityCustom launcher, Rarity rarity) {
		double	health = launcher.getHealth();
		double	healthPercent = (38 - rarity.getNumber() * 3) / 100d;
		double	healthAmount = launcher.getHealthMax() * healthPercent;
		launcher.setHealth(Math.max(1, health - healthAmount));
		if (launcher instanceof PlayerCustom playerCustom) {
			PowerCustom	power = playerCustom.getPowerCustom();
			if (power == null) return;
			double	powerPercent = (30 + rarity.getNumber() * 4) / 100d;
			double	powerAmount = power.getValueMax() * powerPercent;
			power.increase(Math.max(0, powerAmount));
		}
		SoundManager.playSound(launcher, "spell_manathirst");
		particleManaThirst(launcher.getLocation());
	}

	private void particleManaThirst(Location loc) {
		loc.getWorld().spawnParticle(Particle.FLASH, loc, 30, 1.5, 1.5, 1.5, 0.2);
	}

	// flame nova

	public void flameNova(LivingEntityCustom launcher, Rarity rarity) {
		Location	center = launcher.getLocation();
		World	    world = launcher.getWorld();
		if (world == null) return;
		double		radius = (2.5 + rarity.getNumber() * 0.5);
		double		damage = (5 + rarity.getNumber() * 5);
		double		force = 2;
    	int			points = 72;
    	int			yRange = 5;
    	int			baseY = center.getBlockY();
    	for (int i = 0; i < points; i++) {
		    double	angle = (2 * Math.PI * i) / points;
    	    int		x = center.getBlockX() + (int) Math.round(Math.cos(angle) * radius);
    	    int		z = center.getBlockZ() + (int) Math.round(Math.sin(angle) * radius);
    	    Block	ground = null;
    	    for (int y = baseY + yRange; y >= baseY - yRange; y--) {
    	        Block b = world.getBlockAt(x, y, z);
    	        if (b.getType().isSolid()) {
    	            ground = b;
    	            break;
    	        }
    	    }
    	    if (ground == null || ground.isBurnable()) continue;
    	    Block	fireBlock = ground.getRelative(0, 1, 0);
    	    if (fireBlock.getType() == Material.AIR) {
    	        fireBlock.setType(Material.FIRE);
			}
    	}
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null || target.isGrouped(launcher)) continue;
			if (!target.isBoss()) {
		    	Vector	knockback = target.getLocation().toVector().subtract(center.toVector()).normalize();
				double	resistance = 0;
				AttributeInstance	instance = l.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
				if (instance != null)
					resistance = instance.getValue();
	        	knockback.multiply(force - (force * resistance));
		    	knockback.setY(knockback.getY() + 0.3);
				target.setVelocity(target.getVelocity().add(knockback));
			}
			target.damage(damage, CombatDamage.MAGIC, launcher);
		}
		SoundManager.playSound(launcher, "spell_flamenova");
		particleFlameNova(radius, center);
	}

	private void particleFlameNova(double radius, Location loc) {
		World	world = loc.getWorld();
		int		points = 24;
		for (int i = 0; i < points; i++) {
		    double		angle = 2 * Math.PI * i / points;
		    double		x = Math.cos(angle) * radius;
		    double		z = Math.sin(angle) * radius;
			Location	locParticle = loc.clone().add(x, 0, z);
		    world.spawnParticle(Particle.LAVA, locParticle, 1, 0, 0, 0, 0);
			Vector		diff = locParticle.toVector().subtract(locParticle.toVector()).multiply(0.5);
			locParticle = locParticle.add(diff);
			world.spawnParticle(Particle.LAVA, locParticle, 1, 0, 0, 0, 0);
		}
		new BukkitRunnable() {
		    double	angle = 0;
		    int		ticks = 0;
		    @Override
		    public void run() {
		        for (int i = 0; i < 12; i++) {
		            double		theta = angle + (i * (Math.PI * 2 / 12));
		            double		x = Math.cos(theta) * radius;
		            double		z = Math.sin(theta) * radius;
		            Location	base = loc.clone().add(x, 0.1, z);
		            for (double y = 0; y < 3; y += 0.3) {
		                Location	particleLoc = base.clone().add(0, y, 0);
		                world.spawnParticle(Particle.SMOKE, particleLoc, 1, 0, 0, 0, 0);
		            }
		        }
		        angle += 0.2;
		        ticks++;
		        if (ticks > 8)
		            cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 2L);
	}

	/////////////////////
	///*    ROGUE    *///
	/////////////////////
	
	public void stealth(LivingEntityCustom launcher, Rarity rarity) {
		if (hasStealth(launcher)) {
			removeStealth(launcher);
			return;
		}
		int		value = (-50 + rarity.getNumber() * 5);
		addStealth(launcher, value);
    }

	private void particleStealth(Location loc) {
		loc.getWorld().spawnParticle(Particle.SMOKE, loc, 30, 0.5, 0.5, 0.5, 0.1);
	}

	private void addStealth(LivingEntityCustom launcher, int value) {
		LivingEntity	l = launcher.getLivingEntity();
		if (l == null) return;
		int		id = launcher.addStatModifier(StatSecondary.SPEED, value, 0);
		stealth.put(launcher.getUUID(), id);
		l.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 7199980, 0, false, false, false));
		RpgCraft.getInstancEquipmentHidden().onStealthActivated(launcher);
		SoundManager.playSound(launcher, "spell_stealth");
		particleStealth(launcher.getLocation());
	}

	public void removeStealth(LivingEntityCustom launcher) {
		LivingEntity	l = launcher.getLivingEntity();
		if (l == null) return;
		l.removePotionEffect(PotionEffectType.INVISIBILITY);
		Integer	id = stealth.remove(launcher.getUUID());
		if (id != null) launcher.deleteModifier(id);
		RpgCraft.getInstancEquipmentHidden().onStealthActivated(launcher);
		SoundManager.playSound(launcher, "spell_stealth");
		particleStealth(launcher.getLocation());
	}

	public boolean hasStealth(LivingEntityCustom launcher) {
		return stealth.containsKey(launcher.getUUID());
	}

	// escape

	public void escape(LivingEntityCustom launcher, Rarity rarity) {
		int		dodge = (rarity.getNumber() * 50); // 50 = 10% dodge
		int		attackSpeed = 100;
		int		duration = TIME_ESCAPE;
		launcher.addStatModifier(StatSecondary.DODGE, dodge, duration);
		launcher.addStatModifier(StatSecondary.ATTACK_SPEED, attackSpeed, duration);
		SoundManager.playSound(launcher, "spell_escape");
		particleEscape(launcher.getLocation());
	}

	private void particleEscape(Location loc) {
		loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 30, 0.3, 0.5, 0.3, 0.02);
	}

	// sprint

	public void sprint(LivingEntityCustom launcher, Rarity rarity) {
		int			speed = (rarity.getNumber() * 5 + 10);
		int			duration = 8;
		Location	loc = launcher.getLocation();
		double		force = 0.8 + rarity.getNumber() * 0.2;
		Vector		direction = loc.getDirection().setY(0).normalize();
		launcher.setVelocity(direction.multiply(force).setY(0.1));
		launcher.addStatModifier(StatSecondary.SPEED, speed, duration);
		SoundManager.playSound(launcher, "spell_sprint");
		particleSprint(launcher.getLocation());
	}

	private void particleSprint(Location loc) {
		loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 30, 0.3, 0.5, 0.3, 0.02);
	}

	// cold blood

	public void coldBlood(LivingEntityCustom launcher, Rarity rarity) {
		addColdBlood(launcher.getUUID(), rarity.getNumber());
		SoundManager.playSound(launcher, "spell_coldblood");
		particleColdBlood(launcher.getLocation());
	}

	private void particleColdBlood(Location loc) {
		loc.getWorld().spawnParticle(Particle.FIREWORK, loc, 30, 0.3, 1, 0.3, 0.05);
	}

	private void addColdBlood(UUID uuid, int level) {
		coldBlood.put(uuid, level);
	}

	public int removeColdBlood(UUID uuid) {
		Integer	level = coldBlood.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasColdBlood(UUID uuid) {
		return coldBlood.containsKey(uuid);
	}

	/////////////////////
	///*   PRIEST    *///
	/////////////////////

	// holy bomb

	public void holyBomb(LivingEntityCustom launcher, Rarity rarity) {
		addHolyBomb(launcher.getUUID(), rarity.getNumber());
		SoundManager.playSound(launcher, "spell_holybomb");
	}

	private void addHolyBomb(UUID uuid, int level) {
		holyBomb.put(uuid, level);
	}

	public int removeHolyBomb(UUID uuid) {
		Integer	level = holyBomb.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasHolyBomb(UUID uuid) {
		return holyBomb.containsKey(uuid);
	}

	public void holyBombExplosion(LivingEntityCustom launcher, Location center, Rarity rarity) {
	    World	world = center.getWorld();
		double	radius = 5;
		double	damage = rarity.getNumber() * 3;
		double	heal = rarity.getNumber() * 2;
		double	force = 1;
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null) continue;
			if (target.isGrouped(launcher)) {
				target.heal(heal);
				continue;
			}
			if (!target.isBoss()) {
				Vector	direction = target.getLocation().toVector().subtract(center.toVector()).normalize();
				double	resistance = 0;
				AttributeInstance	instance = l.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
				if (instance != null)
					resistance = instance.getValue();
	        	direction.multiply(force - (force * resistance));
	        	direction.setY(direction.getY() + 0.3);
				target.setVelocity(direction);
			}
			target.damage(damage, CombatDamage.MAGIC, launcher);
		}
		SoundManager.playSound(center, "spell_holybomb_hit");
		particleHolyBombExplosion(radius, center);
	}

	private void particleHolyBombExplosion(double radius, Location center) {
		World	world = center.getWorld();
		double	radiusSquared = radius * radius;
		int		points = 128;
    	for (int i = 0; i < points; i++) {
    	    double phi = Math.acos(1 - 2 * (i / (double) points));
    	    double theta = Math.PI * (1 + Math.sqrt(5)) * i;
    	    double x = radius * Math.cos(theta) * Math.sin(phi);
    	    double y = radius * Math.cos(phi);
    	    double z = radius * Math.sin(theta) * Math.sin(phi);
    	    Location loc = center.clone().add(x, y, z);
    	    world.spawnParticle(Particle.HAPPY_VILLAGER, loc, 1, 0, 0, 0, 0);
    	}
   		ThreadLocalRandom	random = ThreadLocalRandom.current();
		points = 64;
    	for (int i = 0; i < points; i++) {
    	    double x, y, z;
    	    while (true) {
    	        x = random.nextDouble(-radius, radius);
    	        y = random.nextDouble(-radius, radius);
    	        z = random.nextDouble(-radius, radius);
    	        if ((x * x + y * y + z * z) <= radiusSquared)
					break;
    	    }
    	    Location loc = center.clone().add(x, y, z);
    		world.spawnParticle(Particle.ENCHANT, loc, 1, 0, 0, 0, 0);
    	}
	}

	// holy land

	public void holyLand(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location	center = launcher.getLocation();
		double		radius = (rarity.getNumber() + 4);
		double		heal = (rarity.getNumber() * 0.5);
		double		mana = (rarity.getNumber() * 0.5);
		new BukkitRunnable() {
			int	seconds = 0;
		    @Override
			public void run() {
				EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
				for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
					LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
				    if (target == null || !target.isGrouped(launcher)) continue;
					if (target instanceof PlayerCustom playerCustom) {
						PowerCustom	power = playerCustom.getPowerCustom();
						if (power != null && power.getType() == PowerType.MANA) power.increase(mana);
					}
					target.heal(heal);
				}
				particleHolyLand(radius, center);
				seconds++;
				if (seconds >= TIME_HOLYLAND) cancel();
			}
		}.runTaskTimer(RpgCraft.instance(), 0L, 20L);
		SoundManager.playSound(launcher, "spell_holyland");
	}

	private void particleHolyLand(double radius, Location center) {
		World	world = center.getWorld();
		int		points = 48;
		for (int i = 0; i < points; i++) {
		    double		angle = 2 * Math.PI * i / points;
		    double		x = Math.cos(angle) * radius;
		    double		z = Math.sin(angle) * radius;
			Location	loc = center.clone().add(x, 1, z);
		    world.spawnParticle(Particle.HEART, loc, 1, 0, 0, 0, 0);
		}
	}

	// holy shield

	public void holyShield(LivingEntityCustom launcher, Rarity rarity) {
		addHolyShield(launcher.getUUID(), rarity.getNumber());
		SoundManager.playSound(launcher, "spell_holyshield");
		particleHolyShield(launcher.getLocation());
	}

	private void particleHolyShield(Location center) {
		World		world = center.getWorld();
		double		range = 1.5;
		int			points = 48;
    	for (int i = 0; i < points; i++) {
    	    double		phi = Math.acos(1 - 2 * (i / (double) points));
    	    double		theta = Math.PI * (1 + Math.sqrt(5)) * i;
    	    double		x = range * Math.cos(theta) * Math.sin(phi);
    	    double		y = range * Math.cos(phi);
    	    double 		z = range * Math.sin(theta) * Math.sin(phi);
    	    Location	loc = center.clone().add(x, y, z);
    	    world.spawnParticle(Particle.ENCHANT, loc, 1, 0, 0, 0, 0);
    	}
	}

	private void addHolyShield(UUID uuid, int level) {
		DataTask<Integer>	dataTask;
		if (holyShield.containsKey(uuid)) {
			dataTask = holyShield.get(uuid);
			dataTask.cancel();
		}
		else
			dataTask = new DataTask<Integer>(1, null);
		dataTask.setData(level);
		dataTask.setTask(Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> removeHolyShield(uuid), TIME_HOLYSHIELD * 20L));
		holyShield.put(uuid, dataTask);
	}

	public void removeHolyShield(UUID uuid) {
		DataTask<Integer>	dataTask = holyShield.remove(uuid);
		if (dataTask != null)
			dataTask.cancel();
	}

	public int getHolyShield(UUID uuid) {
		Integer	level = holyShield.get(uuid).getData();
		return (level == null ? 1 : level);
	}

	public boolean hasHolyShield(UUID uuid) {
		return holyShield.containsKey(uuid);
	}

	public void holyShieldHit(LivingEntityCustom launcher, Rarity rarity, double damage) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location	center = launcher.getLocation();
		double		radius = (rarity.getNumber() * 0.5d + 4.5);
		damage *= (4 + rarity.getNumber()) / 10d;
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null || target.isGrouped(launcher)) continue;
			target.damage(damage, CombatDamage.MAGIC, launcher);
			SoundManager.playSound(launcher, "spell_holyshield_hit");
			particleHolyShieldHit(center);
			break;
		}
	}

	private void particleHolyShieldHit(Location center) {
		center.getWorld().spawnParticle(Particle.CRIT, center, 30, 0.5, 0.5, 0.5, 0.2);
	}

	// shadow word

	public void shadowWord(LivingEntityCustom launcher, Rarity rarity) {
    	Vector	direction = launcher.getLocation().getDirection();
    	direction.normalize();
		WitherSkull				witherSkull = launcher.launchProjectile(WitherSkull.class);
		PersistentDataContainer	pdc = witherSkull.getPersistentDataContainer();
		Data.setInteger(pdc, KEY_SHADOWWORD, rarity.getNumber());
		witherSkull.setYield(0);
		witherSkull.setCharged(false);
		witherSkull.setVelocity(direction.multiply(1));
		SoundManager.playSound(launcher, "spell_shadowword");
		particleShadowWord(launcher.getLocation());
	}

	private void particleShadowWord(Location loc) {
		loc.getWorld().spawnParticle(Particle.TRIAL_OMEN, loc, 40, 0.5, 0.5, 0.5, 0.05);
	}

	public void shadowWordExplosion(LivingEntityCustom launcher, Rarity rarity, Location center) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		double	radius = 4;
		double 	damage = rarity.getNumber() * 3 + 3;
		int		silence = (int)(rarity.getNumber() * 0.5 + 3);
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null || target.isGrouped(launcher)) continue;
			target.addSilence(silence);
			target.damage(damage, CombatDamage.MAGIC, launcher);
		}
		SoundManager.playSound(center, "spell_shadowword_hit");
		particleShadowWordExplosion(radius, center);
	}

	private void particleShadowWordExplosion(double radius, Location center) {
		World				world = center.getWorld();
		double				radiusSquared = radius * radius;
  		ThreadLocalRandom	random = ThreadLocalRandom.current();
		int					points = 48;
    	for (int i = 0; i < points; i++) {
    	    double x, y, z;
    	    while (true) {
    	        x = random.nextDouble(-radius, radius);
    	        y = random.nextDouble(-radius, radius);
    	        z = random.nextDouble(-radius, radius);
    	        if ((x * x + y * y + z * z) <= radiusSquared)
					break;
    	    }
    	    Location loc = center.clone().add(x, y, z);
    		world.spawnParticle(Particle.SOUL, loc, 1, 0, 0, 0, 0);
    	}
	}

	public boolean isShadowWord(Projectile projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.hasInteger(pdc, KEY_SHADOWWORD);
	}

	public int getShadowWordRarity(Projectile projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.getInteger(pdc, KEY_SHADOWWORD);
	}

	/////////////////////
	///*  DRACTHYR   *///
	/////////////////////

	// dragon breath

	public void dragonBreath(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
    	double		radius = 8 + rarity.getNumber();
    	double		maxAngle = Math.toRadians(25); // 50°
    	double		maxForce = 3;
    	Vector		direction = launcher.getLocation().getDirection().normalize();
    	Location	center = launcher.getLocation();
		double		damage = (3 + rarity.getNumber() * 3);
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null || target.isGrouped(launcher)) continue;
    		Vector 			toEntity = target.getLocation().toVector().subtract(center.toVector());
    		double 			distance = toEntity.length();
    		if (distance <= 0 || distance > radius) continue;
    		toEntity.normalize();
    		double dot = direction.dot(toEntity);
    		if (dot < Math.cos(maxAngle)) continue;
			if (!target.isBoss()) {
        		double	strength = (1 - (distance / radius)) * maxForce;
        		Vector	velocity = direction.clone().multiply(strength);
        		velocity.setY(velocity.getY() + 0.3);
        		target.setVelocity(velocity);
			}
			target.damage(damage, CombatDamage.MAGIC, launcher);
		}
		SoundManager.playSound(launcher, "spell_dragonbreath");
		particleDragonBreath(radius, maxAngle, direction, center);
	}

	private void particleDragonBreath(double radius, double maxAngle, Vector direction, Location loc) {
		for (int d = 0; d <= radius; d++) {
		    double	r = d * Math.tan(maxAngle);
		    Location centerPoint = loc.clone().add(0, 1, 0).add(direction.clone().multiply(d));
		    int points = Math.max(1, (int) (r * 8));
		    for (int i = 0; i < points; i++) {
		        double angle = (2 * Math.PI / points) * i;
		        Vector offset = getPerpendicularOffset(direction, Math.cos(angle) * r, Math.sin(angle) * r);
		        loc.getWorld().spawnParticle(Particle.GUST, centerPoint.clone().add(offset), 1, 0, 0, 0, 0);
		    }
		}
	}

	private Vector getPerpendicularOffset(Vector dir, double x, double y) {
	    Vector up = new Vector(0, 1, 0);
	    if (Math.abs(dir.dot(up)) > 0.9)
	        up = new Vector(1, 0, 0);
	    Vector right = dir.clone().crossProduct(up).normalize();
	    Vector realUp = right.clone().crossProduct(dir).normalize();
	    return right.multiply(x).add(realUp.multiply(y));
	}

	// dragon skin

	public void dragonSkin(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location		center = launcher.getLocation();
		double			radius = (rarity.getNumber() * 0.5 + 3);
		new BukkitRunnable() {
			Map<UUID, LivingEntityCustom>	current = new HashMap<>();
			Map<UUID, LivingEntityCustom>	active = new HashMap<>();
			float		seconds = 0f;
		    @Override
			public void run() {
				current.clear();
				EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
				for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
					LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
				    if (target == null || !target.isGrouped(launcher)) continue;
					UUID	uuid = target.getUUID();
					if (!active.containsKey(uuid)) addDragonSkin(target, rarity);
					current.put(uuid, target);
					active.put(uuid, target);
				}
				Iterator<Entry<UUID, LivingEntityCustom>>	it = active.entrySet().iterator();
				while (it.hasNext()) {
					Entry<UUID, LivingEntityCustom>	e = it.next();
					if (!current.containsKey(e.getKey())) {
						removeDragonSkin(e.getValue());
						it.remove();
					}
				}
				particleDragonSkin(radius, center);
				seconds++;
				if (seconds >= TIME_DRAGONSKIN) {
					current.clear();
					active.values().forEach(l -> removeDragonSkin(l));
					active.clear();
					cancel();
				}
			}
		}.runTaskTimer(RpgCraft.instance(), 0L, 20L);
		SoundManager.playSound(launcher, "spell_dragonskin");
	}

	private void particleDragonSkin(double radius, Location center) {
		World	world = center.getWorld();
		int		points = 256;
    	for (int i = 0; i < points; i++) {
    	    double phi = Math.acos(1 - 2 * (i / (double) points));
    	    double theta = Math.PI * (1 + Math.sqrt(5)) * i;
    	    double x = radius * Math.cos(theta) * Math.sin(phi);
    	    double y = radius * Math.cos(phi);
    	    double z = radius * Math.sin(theta) * Math.sin(phi);
    	    Location loc = center.clone().add(x, y, z);
    	    world.spawnParticle(Particle.DRAGON_BREATH, loc, 1, 0, 0, 0, 0);
    	}
	}

	public void addDragonSkin(LivingEntityCustom launcher, Rarity rarity) {
		UUID	uuid = launcher.getUUID();
		int		value = rarity.getNumber() * 100;
		dragonSkinPlayer.put(uuid, launcher.addStatModifier(StatSecondary.SPELL_ARMOR, value, 0));
	}


	public void removeDragonSkin(LivingEntityCustom launcher) {
		UUID	uuid = launcher.getUUID();
		Integer	id = dragonSkinPlayer.get(uuid);
		if (id == null) return;
		launcher.deleteModifier(id);
	}

	// metamorph

	public void metamorph(LivingEntityCustom launcher, Rarity rarity) {
		RpgCraft.getMetamorphRegistry().addDracthyr(launcher, rarity);
		particleMetamorph(launcher.getLocation());
	}

	private void particleMetamorph(Location loc) {}


	// strike back

	public void strikeBack(LivingEntityCustom launcher, Rarity rarity) {
		addStrikeBack(launcher.getUUID(), rarity);
		SoundManager.playSound(launcher, "spell_strikeback");
		particleStrikeBack(launcher.getLocation());
	}

	private void particleStrikeBack(Location loc) {}

	private void addStrikeBack(UUID uuid, Rarity rarity) {
		DataTask<Integer>	dataTask;
		if (strikeBack.containsKey(uuid)) {
			dataTask = strikeBack.get(uuid);
			dataTask.cancel();
		}
		else
			dataTask = new DataTask<Integer>(1, null);
		dataTask.setData(rarity.getNumber());
		dataTask.setTask(Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> removeStrikeBack(uuid), TIME_STRIKEBACK * 20L));
		strikeBack.put(uuid, dataTask);
		canUseStrikeback.put(uuid, true);
	}

	public void removeStrikeBack(UUID uuid) {
		DataTask<Integer>	dataTask = strikeBack.remove(uuid);
		if (dataTask != null)
			dataTask.cancel();
	}

	public int getStrikeBack(UUID uuid) {
		Integer	level = strikeBack.get(uuid).getData();
		return (level == null ? 1 : level);
	}

	public boolean canUseStrikeBack(UUID uuid) {
		if (!strikeBack.containsKey(uuid)) return false;
		boolean	canUse = canUseStrikeback.get(uuid);
		if (canUse) {
			canUseStrikeback.put(uuid, false);
			Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> canUseStrikeback.put(uuid, true), 20L);
		}
		return canUse;
	}

	public void strikeBackHit(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location	center = launcher.getLocation();
		double		radius = 6;
		double 		damage = rarity.getNumber() * 4;
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null || target.isGrouped(launcher)) continue;
			target.damage(damage, CombatDamage.MAGIC, launcher);
		}
		SoundManager.playSound(center, "spell_strikeback_hit");
		particleStrikeBackHit(radius, center);
	}

	private void particleStrikeBackHit(double radius, Location center) {
		World	world = center.getWorld();
		Random	random = new Random();
		for (int i = 0; i < 256; i++) {
		    double		angle = random.nextDouble() * Math.PI * 2;
		    double		distance = Math.sqrt(random.nextDouble()) * radius;
		    double		x = Math.cos(angle) * distance;
		    double		z = Math.sin(angle) * distance;
		    Location	loc = center.clone().add(x, 0, z);
			loc.add(0, random.nextDouble() * 0.3, 0);
			BlockData data = loc.clone().subtract(0, 1, 0).getBlock().getBlockData();
			world.spawnParticle(Particle.BLOCK, loc, 1, data);
		}
	}

	/////////////////////
	///*   HUNTER    *///
	/////////////////////

	// explosive shot

	public void explosiveShot(LivingEntityCustom launcher, Rarity rarity) {
		addExplosiveShot(launcher.getUUID(), rarity.getNumber());
		SoundManager.playSound(launcher, "spell_explosiveshot");
		particleExplosiveShot(launcher.getLocation());
	}

	private void particleExplosiveShot(Location loc) {
		loc.getWorld().spawnParticle(Particle.FIREWORK, loc, 40, 0.5, 0.5, 0.5, 0.05);
	}

	private void addExplosiveShot(UUID uuid, int level) {
		explosiveShot.put(uuid, level);
	}

	public int removeExplosiveShot(UUID uuid) {
		Integer	level = explosiveShot.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasExplosiveShot(UUID uuid) {
		return explosiveShot.containsKey(uuid);
	}

	// pet

	public void pet(LivingEntityCustom launcher, Rarity rarity) {
		UUID		uuid = launcher.getUUID();
		NPCCustom	pet = pets.get(uuid);
		if (launcher.isSneaking()) {
			if (pet != null) {
				pet.delete();
				pets.remove(uuid);
			}
		}
		else if (pet == null)
			createPet(launcher);
		else if (!pet.isPresent())
			teleportPet(launcher, pet);
		else if (pet.getLocation().distanceSquared(launcher.getLocation()) > 40 * 40)
			teleportPet(launcher, pet);
		else
			attackPet(launcher, pet);
		SoundManager.playSound(launcher, "spell_pet");
		particlePet(launcher.getLocation());
	}

	private void particlePet(Location loc) {
		loc.getWorld().spawnParticle(Particle.SPIT, loc, 40, 0.5, 0.5, 0.5, 0.05);
	}

	public boolean hasPet(LivingEntityCustom launcher) {
		NPCCustom	pet = pets.get(launcher.getUUID());
		return (pet != null && pet.isPresent());
	}

	private void createPet(LivingEntityCustom launcher) {
		UUID			uuid = launcher.getUUID();
		if (hasPet(launcher)) removePet(launcher);
		TemplateType	templateType = TemplateType.PET_WOLF;
		NPC				rawPet = CitizensAPI.getNPCRegistry().createNPC(templateType.getEntityType(), templateType.getHideName(), launcher.getLocation());
		rawPet.setProtected(false);
		NPCCustom	pet = RpgCraft.getEntityCustomRegistry().getNPCCustom(rawPet.getUniqueId());
		if (pet == null) return;
		pet.setLevel(launcher.getLevel());
		pet.setTemplate(templateType);
		pets.put(uuid, pet);
		launcher.setPet(pet);
	}

	private void teleportPet(LivingEntityCustom launcher, NPCCustom pet) {
		pet.despawn();
		pet.spawn(launcher.getLocation());
		pet.cleanAggro();
	}

	public void removePet(LivingEntityCustom launcher) {
		launcher.setPet(null);
		UUID			uuid = launcher.getUUID();
		NPCCustom		pet = pets.get(uuid);
		if (pet != null) {
			pet.delete();
			pets.remove(uuid, pet);
		}
	}

	private void attackPet(LivingEntityCustom launcher, NPCCustom pet) {
		LivingEntityCustom	target = launcher.getTarget();
		if (target == null || !target.isPresent()) return;
		LivingEntityCustom	actuelTarget = pet.getTarget();
		if (actuelTarget != null)
			pet.cleanAggro();
		else
			pet.addAggro(target, 1000);
	}

	// hunt

	public void hunt(LivingEntityCustom launcher, Rarity rarity) {
		LivingEntityCustom	target = launcher.getTarget();
		if (target == null || !target.isPresent()) return;
		if (target.isGrouped(launcher)) return;
		int	value = -50 * rarity.getNumber();
		target.addStatModifier(StatSecondary.PHYSICAL_ARMOR, value, TIME_HUNT);
		target.setGlowing(true);
		new BukkitRunnable() {
			int		seconds = 0;
			double	angle = 0;
			@Override
			public void run() {
				if (!target.isPresent()) {
					target.setGlowing(false);
					cancel();
					return;
				}
				particleHunt(angle, target.getLocation().add(0, target.getHeight() + 2, 0));
				seconds++;
				angle += 0.2;
				if (seconds >= TIME_HUNT * 2) {
					target.setGlowing(false);
					cancel();
				}
			}
		}.runTaskTimer(RpgCraft.instance(), 0, 10L);
		SoundManager.playSound(launcher, "spell_hunt");
	}

	private void particleHunt(double angle, Location center) {
		World	world = center.getWorld();
        double	radius = 0.5;
        for (int i = 0; i < 16; i++) {
            double currentAngle = angle + (Math.PI * 2 * i / 8);
            double x = Math.cos(currentAngle) * radius;
            double z = Math.sin(currentAngle) * radius;
            Location particleLoc = center.clone().add(x, 0, z);
            world.spawnParticle(Particle.GLOW, particleLoc, 1, 0, 0, 0, 0);
        }
	}

	// ice trap

	public void iceTrap(LivingEntityCustom launcher, Rarity rarity) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location	center = launcher.getLocation();
		double		radius = 3;
		new BukkitRunnable() {
			int		seconds = 0;
			double	angle = 0;
			@Override
			public void run() {
				EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
				for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
					LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
				    if (target == null || target.isGrouped(launcher)) continue;
					double	explosionRadius = 8;
					double	damage = rarity.getNumber() * 4;
					int		freezeTicks = 40 + rarity.getNumber() * 20;
					iceExplosion(launcher, center, explosionRadius, damage, freezeTicks);
					cancel();
					return;
				}
				particleIceTrap(angle, radius, center.clone().add(0, 1, 0));
				seconds++;
				angle += 0.2;
				if (seconds >= TIME_ICETRAP * 4)
					cancel();
			}
		}.runTaskTimer(RpgCraft.instance(), 0, 5);
		SoundManager.playSound(launcher, "spell_icetrap");
	}

	private void particleIceTrap(double angle, double radius, Location center) {
		World	world = center.getWorld();
		for (int i = 0; i < 6; i++) {
		    double currentAngle = angle + (Math.PI * 2 * i / 6);
		    double x = Math.cos(currentAngle) * radius;
		    double z = Math.sin(currentAngle) * radius;
		    Location particleLoc = center.clone().add(x, 0.05, z);
		    world.spawnParticle(Particle.SNOWFLAKE, particleLoc, 1, 0, 0, 0, 0);
		}
		world.spawnParticle(Particle.WHITE_ASH, center.clone().add(0, 0.1, 0), 2, 0.3, 0.05, 0.3, 0);
	}

	public void iceExplosion(LivingEntityCustom launcher, Location center, double radius, double damage, int freezeTicks) {
	    World	world = center.getWorld();
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
		    if (target == null || target.isGrouped(launcher)) continue;
			if (!target.isBoss()) {
				target.addStatModifier(StatSecondary.SPEED, -60, freezeTicks / 20);
				target.setFreezeTicks(freezeTicks);
			}
			target.damage(damage, CombatDamage.MAGIC, launcher);
	    }
	    SoundManager.playSound(center, "spell_icetrap_hit");
	    particleIceExplosion(radius, center);
	}

	private void particleIceExplosion(double radius, Location center) {
		World	world = center.getWorld();
   		ThreadLocalRandom	random = ThreadLocalRandom.current();
		int					points = 256;
		double				radiusSquared = radius * radius;
    	for (int i = 0; i < points; i++) {
    	    double x, y, z;
    	    while (true) {
    	        x = random.nextDouble(-radius, radius);
    	        y = random.nextDouble(-radius, radius);
    	        z = random.nextDouble(-radius, radius);
    	        if ((x * x + y * y + z * z) <= radiusSquared)
					break;
    	    }
    	    Location loc = center.clone().add(x, y, z);
    		world.spawnParticle(Particle.SNOWFLAKE, loc, 1, 0, 0, 0, 0);
    	}
		radius /= 2;
		for (int i = 0; i < 16; i++) {
		    double currentAngle = (Math.PI * 2 * i / 16);
		    double x = Math.cos(currentAngle) * radius;
		    double z = Math.sin(currentAngle) * radius;
		    Location particleLoc = center.clone().add(x, 0.05, z);
		    world.spawnParticle(Particle.SWEEP_ATTACK, particleLoc, 1, 0, 0, 0, 0);
		}
	}

	////////////////////////
	///* RANGED WEAPONS *///
	////////////////////////
	
	public boolean isBow(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.hasBoolean(pdc, KEY_BOW);
	}

	public boolean isCrossBow(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.hasBoolean(pdc, KEY_CROSSBOW);
	}

	public boolean isStaff(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.hasBoolean(pdc, KEY_STAFF);
	}

	public boolean isSpellBook(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		return Data.hasBoolean(pdc, KEY_SPELLBOOK);
	}

	public void setBow(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		Data.setBoolean(pdc, KEY_BOW);
	}

	public void setCrossBow(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		Data.setBoolean(pdc, KEY_CROSSBOW);
	}

	public void setStaff(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		Data.setBoolean(pdc, KEY_STAFF);
	}

	public void setSpellBook(Entity projectile) {
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		Data.setBoolean(pdc, KEY_SPELLBOOK);
	}

	// launch staff
	public void launchStaff(LivingEntityCustom launcher, LivingEntityCustom target, ItemStack item) {
		switch (launcher.getClassType()) {
			case PYROMANCER, GOD -> {
	    		SmallFireball	smallFireball = launcher.launchProjectile(SmallFireball.class);
	    		smallFireball.setGravity(false);
	    		smallFireball.setVelocity((target.getEyeLocation().subtract(launcher.getEyeLocation()).toVector().normalize()));
				SoundManager.playSound(launcher, "staff_shoot");
				setStaff(smallFireball);
			}
			case PRIEST -> {
	    		DragonFireball	dragonFireball = launcher.launchProjectile(DragonFireball.class);
	    		dragonFireball.setGravity(false);
	    		dragonFireball.setVelocity((target.getEyeLocation().subtract(launcher.getEyeLocation()).toVector().normalize()));
				SoundManager.playSound(launcher, "staff_shoot");
				setStaff(dragonFireball);
			}
			default -> {
				double	damage = launcher.getHealthMax() * 0.9;
				explosionFriendlyFire(launcher, launcher.getEyeLocation(), 6, damage, 2, 0);
			}
		}
		if (launcher instanceof PlayerCustom playerCustom) damageLauncher(playerCustom, item);
	}

	// launch staff
	public void launchStaff(PlayerCustom launcher, ItemStack item) {
		switch (launcher.getClassType()) {
			case PYROMANCER, GOD -> {
	    		SmallFireball	smallFireball = launcher.launchProjectile(SmallFireball.class);
	    		smallFireball.setGravity(false);
	    		smallFireball.setVelocity(launcher.getEyeLocation().getDirection());
				SoundManager.playSound(launcher, "staff_shoot");
				setStaff(smallFireball);
			}
			case PRIEST -> {
	    		DragonFireball	dragonFireball = launcher.launchProjectile(DragonFireball.class);
	    		dragonFireball.setGravity(false);
	    		dragonFireball.setVelocity(launcher.getEyeLocation().getDirection());
				SoundManager.playSound(launcher, "staff_shoot");
				setStaff(dragonFireball);
			}
			default -> {
				double	damage = launcher.getHealthMax() * 0.9;
				explosionFriendlyFire(launcher, launcher.getEyeLocation(), 6, damage, 2, 0);
			}
		}
		if (launcher instanceof PlayerCustom playerCustom) damageLauncher(playerCustom, item);
	}

	// launch spellbook
	public void launchSpellBook(LivingEntityCustom launcher, ItemStack item) {
		Weapon	spellBook = RpgCraft.getItemCustomRegistry().getWeapon(item);
		if (spellBook == null) return;
		switch (launcher.getClassType()) {
			case PYROMANCER, PRIEST, GOD -> {
	    		switch (spellBook.getIdentifier()) {
					case "spellbook_blades_of_war" -> bladesOfWar(launcher);
					case "spellbook_hellow" -> {}
					case "spellbook_braised" -> braised(launcher);
					case "spellbook_majestica" -> {}
					default -> {}
				}
			}
			default -> {
				double	damage = launcher.getHealthMax() * 0.9;
				explosionFriendlyFire(launcher, launcher.getEyeLocation(), 6, damage, 2, 0);
			}
		}
		if (launcher instanceof PlayerCustom playerCustom) damageLauncher(playerCustom, item);
	}

	// custom damage for launcher
	private void damageLauncher(PlayerCustom launcher, ItemStack item) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Weapon	weapon = RpgCraft.getItemCustomRegistry().getWeapon(item);
		if (weapon == null) return;
		ItemMeta		meta = item.getItemMeta();
		if (!(meta instanceof Damageable damageable)) return;
		int	damage = EquipableManager.getNewDurability(weapon);
		damageable.setDamage(damageable.getDamage() + damage);
		if (damageable.getDamage() >= item.getType().getMaxDurability()) {
			RpgCraft.debug("destroy?");
			int	slot = getSlot(launcher, item);
			if (slot == -1) return;
		    launcher.getInventory().setItem(slot, null);
			world.playSound(launcher.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.5f, 1f);
			return;
		}
		item.setItemMeta(meta);
	}

	private int getSlot(PlayerCustom launcher, ItemStack item) {
		NamespacedKey			key = new NamespacedKey(RpgCraft.name(), "findslot");
		ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
		Data.setBoolean(pdc, key);
		PlayerInventory				inv = launcher.getInventory();
		ItemStack					hand = inv.getItemInMainHand();
		ItemStack					offhand = inv.getItemInOffHand();
		PersistentDataContainerView	pdcHand = hand.getPersistentDataContainer();
		PersistentDataContainerView	pdcOffhand = offhand.getPersistentDataContainer();
		int							slot = -1;
		if (Data.hasBoolean(pdcHand, key))
			slot =  inv.getHeldItemSlot();
		else if (Data.hasBoolean(pdcOffhand, key))
			slot = 40;
		Data.remove(pdc, key);
		return slot;
	}

	// spellbook blades of war
    public void bladesOfWar(LivingEntityCustom launcher) {
		World	    world = launcher.getWorld();
		if (world == null) return;
        Location	eyeLoc = launcher.getEyeLocation();
        Vector		forward = eyeLoc.getDirection().normalize();
        Location	center = eyeLoc.clone().add(forward.clone().multiply(6));
        int			amount = 30;
        double		range = 5.0;
		double		rangeSquared = range * range;
        double		height = 6.0;
		int			delay = 1;
        for (int i = 0; i < amount; i++) {
            double xOffset = ThreadLocalRandom.current().nextDouble(-range, range);
            double zOffset = ThreadLocalRandom.current().nextDouble(-range, range);
            if (xOffset * xOffset + zOffset * zOffset > rangeSquared) continue;
			Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
            	Location	spawnLoc = center.clone().add(xOffset, height, zOffset);
            	Arrow		arrow = world.spawn(spawnLoc, Arrow.class);
            	Vector		velocity = forward.clone().multiply(1);
            	velocity.setY(-0.3);
            	arrow.setShooter(launcher.getLivingEntity());
            	arrow.setGravity(false);
            	arrow.setVelocity(velocity);
				arrow.setDamage(1);
				arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
				setSpellBook(arrow);
				Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
					if (arrow.isValid() && !arrow.isDead())
						arrow.remove();
				}, 60);
			}, delay++);
        }
		SoundManager.playSound(launcher, "staff_shoot");
    }

	// spellbook braised
    public void braised(LivingEntityCustom launcher) {
		UUID		uuid = launcher.getUUID();
		NPCCustom	braised = braiseds.get(uuid);
		if (launcher.isSneaking()) {
			if (braised != null) {
				braised.delete();
				braiseds.remove(uuid);
			}
		}
		else if (braised == null)
			createBraised(launcher);
		else if (!braised.isPresent())
			createBraised(launcher);
		else
			teleportBraised(launcher, braised);
		SoundManager.playSound(launcher, "staff_shoot");
    }

	public boolean hasBraised(UUID uuid) {
		NPCCustom	braised = braiseds.get(uuid);
		return (braised != null && braised.isPresent());
	}

	private void createBraised(LivingEntityCustom launcher) {
		UUID			uuid = launcher.getUUID();
		if (hasBraised(uuid)) removeBraised(launcher);
		TemplateType	templateType = TemplateType.PET_BRAISED;
		NPC				rawBraised = CitizensAPI.getNPCRegistry().createNPC(templateType.getEntityType(), templateType.getHideName(), launcher.getLocation());
		rawBraised.setProtected(false);
		rawBraised.getNavigator().setTarget(launcher.getLivingEntity(), false);
		NPCCustom	braised = RpgCraft.getEntityCustomRegistry().createNPCCustom(rawBraised);
		braised.setLevel(launcher.getLevel());
		braised.setTemplate(templateType);
		braiseds.put(uuid, braised);
		launcher.setPet(braised);
	}

	private void teleportBraised(LivingEntityCustom launcher, NPCCustom braised) {
		braised.despawn();
		braised.spawn(launcher.getLocation());
		braised.cleanAggro();
	}

	public void removeBraised(LivingEntityCustom launcher) {
		launcher.setPet(null);
		UUID			uuid = launcher.getUUID();
		NPCCustom		braised = braiseds.get(uuid);
		if (braised != null) {
			braised.delete();
			braiseds.remove(uuid, braised);
		}
	}

	/////////////////////
	/// *   NPCS     *///
	/////////////////////

	// launch egg

	public void launchSpiderEgg(Location center, LivingEntityCustom target, int level, boolean isBig) {
	    World			world = center.getWorld();
	    BlockDisplay	egg = world.spawn(center, BlockDisplay.class);
	    egg.setBlock(Bukkit.createBlockData(Material.TURTLE_EGG));
	    Transformation	transformation = egg.getTransformation();
		if (isBig)
	    	transformation.getScale().set(6.0F, 6.0F, 6.0F);
		else
	    	transformation.getScale().set(2.0F, 2.0F, 2.0F);
	    egg.setTransformation(transformation);
		egg.setInterpolationDelay(0);
		egg.setInterpolationDuration(1);
	    Random	random = new Random();
		Vector	direction = target.getLocation().toVector().subtract(center.toVector()).normalize();
		double	coneAngle = 70.0;
		double	halfAngle = Math.toRadians(coneAngle / 2.0);
		double	randomAngle = (random.nextDouble() * 2 - 1) * halfAngle;
		double	cos = Math.cos(randomAngle);
		double	sin = Math.sin(randomAngle);
		double	x = direction.getX() * cos - direction.getZ() * sin;
		double	z = direction.getX() * sin + direction.getZ() * cos;
		Vector velocity = new Vector(x, 0, z);
		velocity.normalize().multiply(0.2);
		velocity.setY(0.6);
	    new BukkitRunnable() {
	        Vector	currentVelocity = velocity;
	        @Override
	        public void run() {
	            if (!egg.isValid()) {
	                cancel();
	                return;
	            }
	            Location	next = egg.getLocation().add(currentVelocity);
	            egg.teleport(next);
	            currentVelocity.multiply(0.98);
	            currentVelocity.setY(currentVelocity.getY() - 0.03);
	            if (next.getBlock().isSolid()) {
					spawnSpider(egg.getLocation(), level, isBig);
	                egg.remove();
	                cancel();
	            }
	        }
	    }.runTaskTimer(RpgCraft.instance(), 0L, 1L);
		SoundManager.playSound(center, "spell_spideregg");
	}

	public void spawnSpider(Location center, int level, boolean isBig) {
		center.getWorld().spawnParticle(Particle.WHITE_ASH, center, 40, 0.5, 0.5, 0.5, 0.05);
		NPCCustom	spider;
		if (isBig) {
			NPC	npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.SPIDER, "Spider 5");
			npc.setProtected(false);
			spider = RpgCraft.getEntityCustomRegistry().createNPCCustom(npc);
			spider.setLevel(level);
			spider.setTemplate(TemplateType.SPIDER_BIG);
			spider.setRespawnTime(-1);
		}
		else {
			NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.WOLF, "Wolf 3");
			npc.setProtected(false);
			spider = RpgCraft.getEntityCustomRegistry().createNPCCustom(npc);
			spider.setLevel(level);
			spider.setTemplate(TemplateType.SPIDER_CHILD);
			spider.setRespawnTime(-1);	
		}
		spider.spawn(center);
		if (!spider.isPresent()) {
			spider.delete();
			return;
		}
		Location	spawn = findSpawn(center, spider);
		if (spawn == null) {
			spider.delete();
			return;
		}
		spider.teleport(spawn);
		addNPCTemporary(spider);
		SoundManager.playSound(spawn, "spell_spideregg_hit");
	}

	// launch cobweb

	public void launchCobweb(Location center, LivingEntityCustom target) {
	    World			world = center.getWorld();
	    ItemDisplay		cobweb = world.spawn(center, ItemDisplay.class);
	    cobweb.setItemStack(new ItemStack(Material.COBWEB));
	    Transformation	transformation = cobweb.getTransformation();
	    transformation.getScale().set(3.0F, 3.0F, 3.0F);
	    cobweb.setTransformation(transformation);
		cobweb.setInterpolationDelay(0);
		cobweb.setInterpolationDuration(1);
		Vector	direction = target.getEyeLocation().toVector().subtract(center.toVector());
		double	distance = direction.length();
		double	speed = 0.8;
		direction.normalize();
		direction.setY(direction.getY() + distance * 0.03);
		Vector	velocity = direction.multiply(speed);
    	new BukkitRunnable() {
			Vector	currentVelocity = velocity;
    	    @Override
    	    public void run() {
	            Location	next = cobweb.getLocation().add(currentVelocity);
	            cobweb.teleport(next);
	            currentVelocity.multiply(0.98);
	            currentVelocity.setY(currentVelocity.getY() - 0.04);
    	        if (!cobweb.isValid() || next.getBlock().isSolid()) {
					explosionCobweb(cobweb.getLocation());
    	            cobweb.remove();
    	            cancel();
    	        }
    	    }
    	}.runTaskTimer(RpgCraft.instance(), 0L, 1L);
		SoundManager.playSound(center, "spell_cobweb");
	}

	public void explosionCobweb(Location center) {
		World	world = center.getWorld();
		double	radius = 6;
		int		slow = -60; // -60%
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l: world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
			if (target == null || target.getTeams().contains(TeamType.SPIDER)) continue;
			target.addStatModifier(StatSecondary.SPEED, slow, 6);
		}
		SoundManager.playSound(center, "spell_cobweb_hit");
	}

	// launch redstone block

	public void launchRedstoneBlock(LivingEntityCustom launcher, LivingEntityCustom target, int level) {
		launcher.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location		center = launcher.getLocation();
	    BlockDisplay	redstone = world.spawn(center, BlockDisplay.class);
	    redstone.setBlock(Bukkit.createBlockData(Material.REDSTONE_BLOCK));
	    Transformation	transformation = redstone.getTransformation();
	    transformation.getScale().set(3.0F, 3.0F, 3.0F);
	    redstone.setTransformation(transformation);
		redstone.setInterpolationDelay(0);
		redstone.setInterpolationDuration(1);
		Vector	direction = target.getEyeLocation().toVector().subtract(center.toVector());
		double	distance = direction.length();
		double	speed = 0.9;
		direction.normalize();
		direction.setY(direction.getY() + distance * 0.03);
		Vector	velocity = direction.multiply(speed);
    	new BukkitRunnable() {
			Vector	currentVelocity = velocity;
    	    @Override
    	    public void run() {
	            Location	next = redstone.getLocation().add(currentVelocity);
	            redstone.teleport(next);
	            currentVelocity.multiply(0.98);
	            currentVelocity.setY(currentVelocity.getY() - 0.04);
    	        if (!redstone.isValid() || next.getBlock().isSolid()) {
					double	radius = 6;
					double	damage = level * 2;
					double	force = 2;
					int		fireTicks = 0;
					RpgCraft.getSpellRegistry().explosion(launcher, redstone.getLocation(), radius, damage, force, fireTicks);
    	            redstone.remove();
    	            cancel();
    	        }
    	    }
    	}.runTaskTimer(RpgCraft.instance(), 0L, 1L);
		SoundManager.playSound(center, "spell_redstoneblock");
	}

	// strikeback

	public void strikeBackGolem(LivingEntityCustom launcher, int level) {
		launcher.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
		strikeBack(launcher, Rarity.fromInt(level / 10));
	}

	// launch water

	public void launchWater(LivingEntityCustom launcher, LivingEntityCustom target, int level) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		double		damage = (level / 2 >= 1 ? level / 2 : 1);
		Location	start = launcher.getEyeLocation();
		Vector		direction = target.getEyeLocation().toVector().subtract(start.toVector());
		if (direction.getY() > -0.2)
			direction.setY(-0.2);
		direction.normalize();
		double	maxDistance = 20;
		new BukkitRunnable() {
		    double	distance = 0;
		    @Override
		    public void run() {
		        distance += 0.5;
		        Location	point = start.clone().add(direction.clone().multiply(distance));
				EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
				for (LivingEntity l: world.getNearbyLivingEntities(point, 1)) {
					LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
					if (target == null || target.isGrouped(launcher)) continue;
					target.damage(damage, CombatDamage.MAGIC, launcher);
				    Vector knockback = direction.clone().multiply(1.2);
				    knockback.setY(0.3);
				    target.setVelocity(knockback);
				}
    			world.spawnParticle(Particle.SPLASH, point, 10, 0.3, 0.3, 0.3, 0);
				world.spawnParticle(Particle.BUBBLE, point, 3, 0.1, 0.1, 0.1, 0);
		        if (point.getBlock().isSolid()) {
					Block	block = point.getBlock().getRelative(BlockFace.UP);
					if (block.getType() == Material.AIR && canPlaceWater(block, 10)) {
						block.setType(Material.WATER);
						Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> block.setType(Material.AIR), 40);
					}
		            cancel();
		            return;
		        }
		        if (distance >= maxDistance)
		            cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0, 1);
		SoundManager.playSound(start, "spell_launchwater");
	}

	private boolean canPlaceWater(Block block, int radius) {
	    Location	world_center = block.getLocation();
	    World		world = block.getWorld();
	    int			y = 0;
	    int			lastCheckedChunkX = Integer.MIN_VALUE;
	    int			lastCheckedChunkZ = Integer.MIN_VALUE;
	    for (int x = -radius; x <= radius; x++) {
	        for (int z = -radius; z <= radius; z++) {
	            int	blockX = world_center.getBlockX() + x;
	            int	blockZ = world_center.getBlockZ() + z;
	            int	chunkX = blockX >> 4;
	            int	chunkZ = blockZ >> 4;
	            if (chunkX != lastCheckedChunkX || chunkZ != lastCheckedChunkZ) {
	                if (!world.isChunkLoaded(chunkX, chunkZ)) {
	                    return false;
	                }
	                lastCheckedChunkX = chunkX;
	                lastCheckedChunkZ = chunkZ;
	            }
	            Block	check = world.getBlockAt(blockX, world_center.getBlockY() + y, blockZ);
	            if (check.isPassable())
	                return false;
	        }
	    }
	    return true;
	}

	// expulse

	public void expulse(LivingEntityCustom launcher, double radius) {
		World	    world = launcher.getWorld();
		if (world == null) return;
		Location	center = launcher.getLocation();
		double		force = 3;
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		for (LivingEntity l: world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
			if (target == null || target.isGrouped(launcher)) continue;
			if (!target.isBoss()) continue;
		    Vector	knockback = target.getLocation().toVector().subtract(center.toVector()).normalize();
			double	resistance = 0;
			AttributeInstance	instance = l.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
			if (instance != null)
				resistance = instance.getValue();
	        knockback.multiply(force - (force * resistance));
		    knockback.setY(knockback.getY() + 0.4);
			target.setVelocity(target.getVelocity().add(knockback));
		}
		SoundManager.playSound(center, "spell_expulse");
		particleExpulse(radius, center);
	}

	private void particleExpulse(double radius, Location loc) {
		World	world = loc.getWorld();
		int		points = 24;
		for (int i = 0; i < points; i++) {
		    double		angle = 2 * Math.PI * i / points;
		    double		x = Math.cos(angle) * radius;
		    double		z = Math.sin(angle) * radius;
			Location	locParticle = loc.clone().add(x, 0, z);
		    world.spawnParticle(Particle.LAVA, locParticle, 1, 0, 0, 0, 0);
			Vector		diff = locParticle.toVector().subtract(locParticle.toVector()).multiply(0.5);
			locParticle = locParticle.add(diff);
			world.spawnParticle(Particle.BUBBLE, locParticle, 1, 0, 0, 0, 0);
		}
		new BukkitRunnable() {
		    double	angle = 0;
		    int		ticks = 0;
		    @Override
		    public void run() {
		        for (int i = 0; i < 12; i++) {
		            double		theta = angle + (i * (Math.PI * 2 / 12));
		            double		x = Math.cos(theta) * radius;
		            double		z = Math.sin(theta) * radius;
		            Location	base = loc.clone().add(x, 0.1, z);
		            for (double y = 0; y < 3; y += 0.3) {
		                Location	particleLoc = base.clone().add(0, y, 0);
		                world.spawnParticle(Particle.FISHING, particleLoc, 1, 0, 0, 0, 0);
		            }
		        }
		        angle += 0.2;
		        ticks++;
		        if (ticks > 8)
		            cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 2L);
	}

	// spawn trident

	public void spawnTrident(LivingEntityCustom launcher, LivingEntityCustom target, int level) {
		World	    world = launcher.getWorld();
		if (world == null) return;
	    int					count = 3;
	    double				radius = 1.5;
	    double				heightOffset = launcher.getHeight() / 2;
	    double				rotationSpeedPerTick = Math.toRadians(12);
	    List<ItemDisplay>	tridents = new ArrayList<>();
	    for (int i = 0; i < count; i++) {
	        Location	spawnLoc = launcher.getLocation().clone().add(0, heightOffset, 0);
	        ItemDisplay	trident = world.spawn(spawnLoc, ItemDisplay.class);
			trident.setGravity(false);
	        trident.setItemStack(new ItemStack(Material.TRIDENT));
			trident.setBillboard(Display.Billboard.FIXED);
			trident.setInterpolationDelay(0);
			trident.setInterpolationDuration(2);
	        tridents.add(trident);
	    }
	    new BukkitRunnable() {
	        int			ticks = 0;
	        final int	maxTicks = 300;
	        @Override
	        public void run() {
	            if (ticks >= maxTicks || !launcher.isPresent() || tridents.isEmpty()) {
	                tridents.forEach(t -> t.remove());
	                this.cancel();
	                return;
	            }
	            if (ticks > 0 && ticks % 50 == 0) {
					Location	eye = launcher.getEyeLocation();
    				Vector		direction = eye.getDirection();
					if (target != null && eye.distanceSquared(target.getLocation()) <= 30 * 30)
						direction = target.getEyeLocation().subtract(launcher.getEyeLocation()).toVector();
					direction.normalize();
					Trident		trident = launcher.launchProjectile(Trident.class);
    				trident.setVelocity(direction.multiply(1.5));
    				trident.setPickupStatus(Trident.PickupStatus.DISALLOWED);
					trident.setDamage(level * 1.5);
                	ItemDisplay removed = tridents.remove(tridents.size() - 1);
                	removed.remove();
	            }
	            Location centerLoc = launcher.getLocation().add(0, heightOffset, 0);
	            for (int i = 0; i < tridents.size(); i++) {
					double 			angle = (2 * Math.PI / count) * i + (rotationSpeedPerTick * ticks);
	                double			x = radius * Math.cos(angle);
	                double			z = radius * Math.sin(angle);
	                Location		newLoc = centerLoc.clone().add(x, 0, z);
	                ItemDisplay		trident = tridents.get(i);
	                trident.teleport(newLoc);
                	Transformation transform = trident.getTransformation();
                	Transformation newTransform = new Transformation(
                	    transform.getTranslation(),
                	    new Quaternionf().rotateY((float) angle),
                	    transform.getScale(),
                	    transform.getRightRotation()
                	);
                	trident.setTransformation(newTransform);
	            }
	            ticks += 2;
	        }
	    }.runTaskTimer(RpgCraft.instance(), 0L, 2L);
	}

	// launch fire

	public void launchFire(LivingEntityCustom launcher, LivingEntityCustom target, int level) {
		World	world = launcher.getWorld();
		if (world == null) return;
	    new BukkitRunnable() {
	        int			ticks = 0;
	        final int	maxTicks = 60;
	        @Override
	        public void run() {
				if (launcher == null || !launcher.isPresent() || ticks >= maxTicks) {
					cancel();
					return;
				}
				Location		start = launcher.getEyeLocation();
				Vector			direction = start.getDirection();
				if (target != null && target.isPresent())
					direction = target.getEyeLocation().toVector().subtract(start.toVector());
				direction.normalize();
				SmallFireball	smallFireball = launcher.launchProjectile(SmallFireball.class);
				smallFireball.setVelocity(direction);
				world.playSound(launcher.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5f, 1f);
				ticks += 20;
	        }
	    }.runTaskTimer(RpgCraft.instance(), 0L, 20L);
	}

	// utils

	private void addNPCTemporary(NPCCustom npcCustom) {
		if (taskNPCTemporary == null) {
			taskNPCTemporary = Bukkit.getScheduler().runTaskTimer(RpgCraft.instance(), () -> {
				if (npcTemporary.isEmpty()) {
					taskNPCTemporary.cancel();
					taskNPCTemporary = null;
					return;
				}
				int								now = Bukkit.getCurrentTick();
				Iterator<Entry<UUID, Integer>>	it = npcTemporary.entrySet().iterator();
				while (it.hasNext()) {
					Entry<UUID, Integer>	e = it.next();
					if (now >= e.getValue()) {
						NPCCustom	tmp = RpgCraft.getEntityCustomRegistry().getNPCCustom(e.getKey());
						if (tmp != null)
							tmp.delete();
						it.remove();
					}
				}
			}, 0L, 20L);
		}
		npcTemporary.put(npcCustom.getUUID(), Bukkit.getCurrentTick() + TIME_TEMPORARYNPC * 20);
	}

	private Location findSpawn(Location origin, LivingEntityCustom livingEntityCustom) {
    	double	width = livingEntityCustom.getWidth() + 2;
    	double	height = livingEntityCustom.getHeight() + 2;
    	int		radius = 5;
    	for (int y = 0; y <= 2; y++) {
    	    for (int x = 0; x <= radius; x++) {
    	        for (int z = 0; z <= radius; z++) {
    	            Location	loc = origin.clone().add(x, y, z);
    	            if (canFit(loc, width, height))
    	                return loc.add(0.5, 0, 0.5);
    	        }
    	    }
    	}
    	for (int y = 0; y >= -2; y--) {
    	    for (int x = 0; x >= -radius; x--) {
    	        for (int z = 0; z >= -radius; z--) {
    	            Location	loc = origin.clone().add(x, y, z);
    	            if (canFit(loc, width, height))
    	                return loc.add(0.5, 0, 0.5);
    	        }
    	    }
    	}
    	return null;
	}

	private boolean canFit(Location loc, double width, double height) {
    	World	world = loc.getWorld();
    	double	radius = width / 2.0;
    	for (double x = -radius; x <= radius; x += 0.3) {
    	    for (double z = -radius; z <= radius; z += 0.3) {
    	        for (double y = 0; y <= height; y += 0.5) {
    	            Location	checkLoc = loc.clone().add(x, y, z);
    	            int			chunkX = checkLoc.getBlockX() >> 4;
    	            int			chunkZ = checkLoc.getBlockZ() >> 4;
    	            if (!world.isChunkLoaded(chunkX, chunkZ)) 
    	                return false;
    	            if (!checkLoc.getBlock().isPassable())
    	                return false;
    	        }
    	    }
    	}
    	Block	ground = loc.clone().subtract(0, 1, 0).getBlock();
    	int		groundChunkX = ground.getX() >> 4;
    	int		groundChunkZ = ground.getZ() >> 4;
    	if (!world.isChunkLoaded(groundChunkX, groundChunkZ))
    	    return false;
    	return ground.isSolid();
	}

	/////////////////////
	///*   OTHERS    *///
	/////////////////////

	// basic explosion

	public void explosion(LivingEntityCustom launcher, Location center, double radius, double damage, double force, int fireticks) {
	    World					world = center.getWorld();
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
	    for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
            if (target == null || target.isGrouped(launcher)) continue;
			if (force > 0 && !target.isBoss()) {
				Vector	direction = target.getLocation().toVector().subtract(center.toVector()).normalize();
				double	resistance = 0;
				AttributeInstance	instance = l.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
				if (instance != null)
					resistance = instance.getValue();
	        	direction.multiply(force - (force * resistance));
	        	direction.setY(direction.getY() + 0.3);
				target.setVelocity(direction);
			}
			target.damage(damage, CombatDamage.MAGIC, launcher);
			if (fireticks > 0)
				target.setFireTicks(fireticks);
	    }
	    world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1f);
	    world.spawnParticle(Particle.EXPLOSION, center, 3);
		world.spawnParticle(Particle.CLOUD, center, 40, 1.5, 1.5, 1.5, 0.1);
	}

	public void explosionFriendlyFire(LivingEntityCustom launcher, Location center, double radius, double damage, double force, int fireticks) {
	    World					world = center.getWorld();
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
	    for (LivingEntity l : world.getNearbyLivingEntities(center, radius)) {
			LivingEntityCustom	target = entityCustomRegistry.getLivingEntityCustom(l.getUniqueId());
            if (target == null) continue;
			if (force > 0 && !target.isBoss()) {
				Vector	direction = target.getLocation().toVector().subtract(center.toVector()).normalize();
				double	resistance = 0;
				AttributeInstance	instance = l.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
				if (instance != null)
					resistance = instance.getValue();
	        	direction.multiply(force - (force * resistance));
	        	direction.setY(direction.getY() + 0.3);
				target.setVelocity(direction);
			}
			target.damage(damage, CombatDamage.MAGIC, launcher, true);
			if (fireticks > 0)
				target.setFireTicks(fireticks);
	    }
	    world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1f);
	    world.spawnParticle(Particle.EXPLOSION, center, 3);
		world.spawnParticle(Particle.CLOUD, center, 40, 1.5, 1.5, 1.5, 0.1);
	}
}
