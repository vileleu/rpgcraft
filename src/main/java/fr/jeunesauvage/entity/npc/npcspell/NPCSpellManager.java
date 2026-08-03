package fr.jeunesauvage.entity.npc.npcspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.group.Group;
import fr.jeunesauvage.entity.npc.template.TemplateType;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.team.Team;
import fr.jeunesauvage.entity.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.spell.SpellManager;
import fr.jeunesauvage.sound.SoundManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class NPCSpellManager implements Listener {
	private static final int			TIME_NPCTEMPORARY = 60; // seconds
	private final EntityManager			entityManager;
	private final ItemCustomManager		itemCustomManager;
	private final Map<Integer, Integer>	npcTemporary = new HashMap<>();
	private BukkitTask					task;

	public NPCSpellManager(EntityManager entityManager, ItemCustomManager itemCustomManager) {
		this.entityManager = entityManager;
		this.itemCustomManager = itemCustomManager;
		this.task = null;
	}

	private void addNPCTemporary(NPC npc) {
		if (task == null) {
			task = Bukkit.getScheduler().runTaskTimer(RpgCraft.instance(), () -> {
				if (npcTemporary.isEmpty()) {
					task.cancel();
					task = null;
					return;
				}
				int									now = Bukkit.getCurrentTick();
				Iterator<Entry<Integer, Integer>>	it = npcTemporary.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, Integer>	e = it.next();
					if (now >= e.getValue()) {
						NPC	tmp = CitizensAPI.getNPCRegistry().getById(e.getKey());
						if (tmp != null)
							tmp.destroy();
						it.remove();
					}
				}
			}, 0L, 20L);
		}
		npcTemporary.put(npc.getId(), Bukkit.getCurrentTick() + TIME_NPCTEMPORARY * 20);
	}

	public void launchSpiderEgg(Location center, LivingEntity target, int level, boolean isBig) {
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
		NPC	spider;
		if (isBig) {
			spider = CitizensAPI.getNPCRegistry().createNPC(EntityType.SPIDER, "Spider 5");
			TraitSentinel	traitSentinel = spider.getOrAddTrait(TraitSentinel.class);
			traitSentinel.setLevel(level);
			traitSentinel.setTemplate(TemplateType.SPIDER_BIG);
			traitSentinel.setRespawnTime(-1);
		}
		else {
			spider = CitizensAPI.getNPCRegistry().createNPC(EntityType.WOLF, "Wolf 3");
			TraitSentinel	traitSentinel = spider.getOrAddTrait(TraitSentinel.class);
			traitSentinel.setLevel(level);
			traitSentinel.setTemplate(TemplateType.SPIDER_CHILD);
			traitSentinel.setRespawnTime(-1);	
		}
		spider.setProtected(false);
		spider.spawn(center);
		if (!(spider.getEntity() instanceof LivingEntity livingEntity)) {
			spider.destroy();
			return;
		}
		Location	spawn = findSpawn(center, livingEntity);
		if (spawn == null) {
			spider.destroy();
			return;
		}
		spider.teleport(spawn, TeleportCause.PLUGIN);
		addNPCTemporary(spider);
		SoundManager.playSound(spawn, "spell_spideregg_hit");
	}

	public void launchCobweb(Location center, LivingEntity target) {
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
		for (LivingEntity livingTarget: world.getNearbyLivingEntities(center, radius)) {
			if (Team.has(livingTarget, TeamType.SPIDER)) return;
			if (livingTarget instanceof Player playerTarget && !playerTarget.hasMetadata("NPC")) {
				PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(playerTarget);
				playerCustom.addStatModifier(StatSecondary.SPEED, slow, 6);
			}
			else
				entityManager.getEntityModifierManager().addModifier(livingTarget, StatSecondary.SPEED, slow, 6);
		}
		SoundManager.playSound(center, "spell_cobweb_hit");
	}

	public void launchRedstoneBlock(LivingEntity launcher, LivingEntity target, int level) {
		launcher.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
	    World			world = launcher.getWorld();
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
					itemCustomManager
						.getSpellManager()
						.explosion(launcher, redstone.getLocation(), radius, damage, force, fireTicks);
    	            redstone.remove();
    	            cancel();
    	        }
    	    }
    	}.runTaskTimer(RpgCraft.instance(), 0L, 1L);
		SoundManager.playSound(center, "spell_redstoneblock");
	}

	public void redstoneMagnet(LivingEntity launcher, LivingEntity target, double range) {
		launcher.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
		World		world = launcher.getWorld();
        double      radius = range + 5;
		double		forceMax = 2;
		new BukkitRunnable() {
		    int			ticks = 0;
		    final int	maxTicks = SpellManager.TIME_DEADLYMAGNET * 4; // 4 ticks = 1 seconds
		    @Override
		    public void run() {
				Location	center = launcher.getLocation();
		        for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
		            if (livingTarget.equals(launcher)) continue;
					if (Group.isInSameGroup(livingTarget, launcher)) continue;
					if (TraitSentinel.isBoss(livingTarget)) continue;
		            Vector	toPlayer = center.toVector().subtract(livingTarget.getLocation().toVector());
		            double	distance = toPlayer.length();
		            if (distance < 0.5) continue;
		            double	force = Math.pow(distance / radius, 2) * forceMax;
		            Vector	velocity = toPlayer.normalize().multiply(force);
		            livingTarget.setVelocity(velocity);
					particleEntityRedstoneMagnet(livingTarget.getLocation());
		        }
				particleRedstoneMagnet(center, radius, ticks, maxTicks);
		        ticks++;
		        if (ticks >= maxTicks)
		            cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 5L);
		SoundManager.playSound(launcher, "spell_deadlymagnet");
	}

	private void particleEntityRedstoneMagnet(Location loc) {
		World		world = loc.getWorld();
    	Location	from = loc.add(0, 1, 0);
    	Vector		dir = loc.toVector().subtract(from.toVector()).normalize();
    	for (int i = 0; i < 5; i++) {
    	    Location particleLoc = from.clone().add(dir.clone().multiply(i * 0.5));
    	    world.spawnParticle(Particle.END_ROD, particleLoc, 0, 0, 0, 0, 0);
    	}
	}

	private void particleRedstoneMagnet(Location center, double radius, double tick, double maxTicks) {
    	World	world = center.getWorld();
		int		points = 16;
    	double	progress = tick / maxTicks;
    	for (int i = 0; i < points; i++) {
    	    double	startAngle = (Math.PI * 2 * i) / (double)points;
    	    double	currentAngle = startAngle + tick * 0.25;
    	    double	currentRadius = radius * (1.0 - progress);
    	    double	x = Math.cos(currentAngle) * currentRadius;
    	    double	z = Math.sin(currentAngle) * currentRadius;
    	    world.spawnParticle(Particle.SWEEP_ATTACK, center.clone().add(x, 0.2, z), 1);
    	}
	}

	public void strikeBack(LivingEntity launcher, int level) {
		launcher.playEffect(EntityEffect.IRON_GOLEN_ATTACK);
		itemCustomManager
			.getSpellManager()
			.strikeBack(launcher, level / 3);
	}

	public void launchWater(LivingEntity launcher, LivingEntity target, int level) {
		World		world = launcher.getWorld();
		double		damage = level / 2;
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
				for (LivingEntity livingTarget : world.getNearbyLivingEntities(point, 1)) {
				    if (livingTarget.equals(launcher)) continue;
					livingTarget.damage(damage, CombatDamage.getDamageSource(launcher, CombatDamage.MAGIC.getType()));
				    Vector knockback = direction.clone().multiply(1.2);
				    knockback.setY(0.3);
				    livingTarget.setVelocity(knockback);
				}
    			world.spawnParticle(Particle.SPLASH, point, 10, 0.3, 0.3, 0.3, 0);
				world.spawnParticle(Particle.BUBBLE, point, 3, 0.1, 0.1, 0.1, 0);
		        if (point.getBlock().isSolid()) {
					Block	block = point.getBlock().getRelative(BlockFace.UP);
					if (block.getType() == Material.AIR && canPlaceWater(block, 10)) {
						block.setType(Material.WATER);
						Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> block.setType(Material.AIR), 20);
					}
		            cancel();
		            return;
		        }
		        if (distance >= maxDistance)
		            cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0, 1);
	}

	private boolean canPlaceWater(Block block, int radius) {
		Location	center = block.getLocation();
		World		world = block.getWorld();
		int			y = 0;
		for (int x = -radius; x <= radius; x++) {
		    for (int z = -radius; z <= radius; z++) {
		        Block	check = world.getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
		        if (check.isPassable()) {
		            return false;
		        }
		    }
		}
	    return true;
	}

	public void expulse(LivingEntity launcher, double radius) {
		World		world = launcher.getWorld();
		Location	center = launcher.getLocation();
		double		force = 3;
        for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
			if (Group.isInSameGroup(livingTarget, launcher)) continue;
			if (!TraitSentinel.isBoss(livingTarget)) {
		    	Vector	knockback = livingTarget.getLocation().toVector().subtract(center.toVector()).normalize();
				double	resistance = 0;
				AttributeInstance	instance = livingTarget.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
				if (instance != null)
					resistance = instance.getValue();
	        	knockback.multiply(force - (force * resistance));
		    	knockback.setY(knockback.getY() + 0.3);
				livingTarget.setVelocity(livingTarget.getVelocity().add(knockback));
			}
		}
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

	public void spawnTrident(LivingEntity launcher, int level) {
	    World				world = launcher.getWorld();
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
	            if (ticks >= maxTicks || !launcher.isValid() || tridents.isEmpty()) {
	                tridents.forEach(t -> t.remove());
	                this.cancel();
	                return;
	            }
	            if (ticks > 0 && ticks % 50 == 0) {
					Location	eye = launcher.getEyeLocation();
    				Vector		direction = eye.getDirection();
					Trident		trident = launcher.getWorld().spawn(eye, Trident.class);
    				trident.setShooter(launcher);
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

	/*
	** UTILS
	*/

	private Location findSpawn(Location origin, LivingEntity livingEntity) {
    	double	width = livingEntity.getWidth() + 2;
    	double	height = livingEntity.getHeight() + 2;
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
    	for (int y = 0; y >= -2; y++) {
    	    for (int x = 0; x >= -radius; x++) {
    	        for (int z = 0; z >= -radius; z++) {
    	            Location	loc = origin.clone().add(x, y, z);
    	            if (canFit(loc, width, height))
    	                return loc.add(0.5, 0, 0.5);
    	        }
    	    }
    	}
    	return null;
	}

	private boolean canFit(Location loc, double width, double height) {
	    double	radius = width / 2.0;
	    for (double x = -radius; x <= radius; x += 0.3) {
	        for (double z = -radius; z <= radius; z += 0.3) {
	            for (double y = 0; y <= height; y += 0.5) {
	                Block block = loc.clone().add(x, y, z).getBlock();
	                if (!block.isPassable())
	                    return false;
	            }
	        }
	    }
	    Block	ground = loc.clone().subtract(0, 1, 0).getBlock();
	    return ground.isSolid();
	}
}
