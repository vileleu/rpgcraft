package fr.jeunesauvage.itemcustom.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.bossbar.BossBarData;
import fr.jeunesauvage.entity.group.Group;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Health;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Resource;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.usable.Usable;
import fr.jeunesauvage.sound.SoundManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class Spell extends ItemCustom<SpellType> implements Usable {
	private final Map<UUID, BossBarData>	bossBars = new HashMap<>();

	public Spell(SpellType type, String name, Rarity rarity, int level) {
		super(type, name, rarity, level);
		buildSpell();
	}

	private void buildSpell() {
		ItemMeta	meta = item.getItemMeta();
		meta.displayName(Lore.nameSpell(name, rarity));
        Data.setString(meta.getPersistentDataContainer(), KEY_IDENTIFIER, name);
		// write lore
		List<Component>	lore = new ArrayList<>();
		lore.add(Lore.type(type));
		lore.add(Lore.rarity(rarity));
		lore.add(Lore.level(level));
		if (type.getRaceTypes() != null)
			lore.add(Lore.raceType(type.getRaceTypes()));
		if (type.getClassTypes() != null)
			lore.add(Lore.classType(type.getClassTypes()));
		lore.add(Lore.cost(type.getCost(rarity)));
		lore.add(Lore.description(name));
		meta.lore(lore);
        item.setItemMeta(meta);
	}

	// override

	@Override
	public ItemCustomCategory getCategory() {
		return type.getCategory();
	}

	@Override
	public Material	getMaterial() {
		return type.getMaterial();
	}

	@Override
	public Component toComponent() {
        return Component.translatable("spell.rpgcraft." + name);
	}

	@Override
	public boolean canUse(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot) {
		if (type == SpellType.STEALTH && itemCustomManager.getSpellManager().hasStealth(playerCustom.getPlayer().getUniqueId())) return true;
		else if (type == SpellType.PET && itemCustomManager.getSpellManager().hasPet(playerCustom.getPlayer().getUniqueId())) return true;
		// check race
		RaceType		playerRace = playerCustom.getRaceType();
		Set<RaceType>	raceTypes = type.getRaceTypes();
		if (raceTypes != null && !raceTypes.contains(playerRace)) {
			playerCustom.getPlayer().sendActionBar(Message.cantUse());
			return false;
		}
		// check class
		ClassType		playerClass = playerCustom.getClassType();
		if (playerClass == ClassType.GOD) {
			if (type.isCast()) {
				startCast(itemCustomManager, playerCustom, slot);
				return false;
			}
			return true;
		}
		Set<ClassType>	classTypes = type.getClassTypes();
		if (classTypes != null && !classTypes.contains(playerClass)) {
			playerCustom.getPlayer().sendActionBar(Message.cantUse());
			return false;
		}
		// silence
		int	duration = playerCustom.isSilence();
		if (duration > 0) {
			playerCustom.getPlayer().sendActionBar(Message.silence(duration));
			return false;
		}
		// cooldown
		duration = playerCustom.hasCooldown(type.getMaterial());
		if (duration > 0) {
			playerCustom.getPlayer().sendActionBar(Message.cooldown(duration));
			return false;
		}
		// check power
		Resource	power = playerCustom.getPower();
		if (power == null) {
			playerCustom.getPlayer().sendActionBar(Message.cantUse());
			return false;
		}
		int	cost = type.getCost(rarity);
		if (power.getValue() < cost) {
			playerCustom.getPlayer().sendActionBar(Message.notEnough(power.getType()));
			return false;
		}
		if (type.isCast()) {
			startCast(itemCustomManager, playerCustom, slot);
			return false;
		}
		// can use
		playerCustom.addCooldown(type.getMaterial(), type.getCooldown(rarity));
		power.decrease(cost);
		playerCustom.getScoreboardCustom().refreshPower();
		return true;
	}

	@Override
	public void use(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot) {
		switch (type) {
			// warrior
			case SpellType.KNEE_BREAKER -> kneeBreaker(itemCustomManager, playerCustom);
			case SpellType.WHIRLWIND -> whirlwind(itemCustomManager, playerCustom);
			case SpellType.LEAP -> leap(itemCustomManager, playerCustom);
			case SpellType.DEADLY_MAGNET -> deadlyMagnet(itemCustomManager, playerCustom);
			// pyromancer
			case SpellType.FIREBALL -> fireBall(itemCustomManager, playerCustom);
			case SpellType.TELEPORT -> teleport(itemCustomManager, playerCustom);
			case SpellType.MANA_THIRST -> manaThirst(itemCustomManager, playerCustom);
			case SpellType.FLAME_NOVA -> flameNova(itemCustomManager, playerCustom);
			// rogue
			case SpellType.STEALTH -> stealth(itemCustomManager, playerCustom);
			case SpellType.ESCAPE -> escape(itemCustomManager, playerCustom);
			case SpellType.SPRINT -> sprint(itemCustomManager, playerCustom);
			case SpellType.COLDBLOOD -> coldBlood(itemCustomManager, playerCustom);
			// priest
			case SpellType.HOLY_BOMB -> holyBomb(itemCustomManager, playerCustom);
			case SpellType.HOLY_LAND -> holyLand(itemCustomManager, playerCustom);
			case SpellType.HOLY_SHIELD -> holyShield(itemCustomManager, playerCustom);
			case SpellType.SHADOW_WORD -> shadowWord(itemCustomManager, playerCustom);
			// dracthyr
			case SpellType.DRAGON_BREATH -> dragonBreath(itemCustomManager, playerCustom);
			case SpellType.DRAGON_SKIN -> dragonSkin(itemCustomManager, playerCustom);
			case SpellType.METAMORPH -> metamorph(itemCustomManager, playerCustom);
			case SpellType.STRIKE_BACK -> strikeBack(itemCustomManager, playerCustom);
			// hunter
			case SpellType.EXPLOSIVE_SHOT -> explosiveShot(itemCustomManager, playerCustom);
			case SpellType.PET -> pet(itemCustomManager, playerCustom);
			case SpellType.HUNT -> hunt(itemCustomManager, playerCustom);
			case SpellType.ICE_TRAP -> iceTrap(itemCustomManager, playerCustom);
			default -> {}
		}
	}

	private void startCast(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot) {
		Player		player = playerCustom.getPlayer();
		cleanBossBar(player);
		Component	component = Component.translatable("level.rpgcraft.casting").decorate(TextDecoration.BOLD);
		BossBar		bar = BossBar.bossBar(
		        component,
		        0f,
		        BossBar.Color.BLUE,
		        BossBar.Overlay.PROGRESS
		);
		player.showBossBar(bar);
		BossBarData	bossBarData = new BossBarData(bar, new BukkitRunnable() {
			Location	now = null;
			Location	last = player.getLocation();
			float		max = type.getCastTime() - (type.getCastTime() * (float)StatSecondary.getAmount(playerCustom, StatSecondary.CAST_SPEED));
		    int			ticks = 0;
		    @Override
		    public void run() {
				if (max <= 0) {
					cleanBossBar(player);
					if (!castCanUse(playerCustom)) return;
					use(itemCustomManager, playerCustom, slot);
					return;
				}
				now = player.getLocation();
				if (ticks >= 5 && (now.getX() != last.getX() || now.getY() != last.getY() || now.getZ() != last.getZ())) {
					cleanBossBar(player);
					return;
				}
				float	start = ticks / 10f;
				float	progress = Math.min(1f, start / max);
				bar.progress(progress);
				last = now;
				ticks++;
				if (progress >= 1) {
					cleanBossBar(player);
					if (!castCanUse(playerCustom)) return;
					use(itemCustomManager, playerCustom, slot);
				}
		    }
		
		}.runTaskTimer(RpgCraft.instance(), 0L, 2L));
		bossBars.put(player.getUniqueId(), bossBarData);
	}

	private boolean castCanUse(PlayerCustom playerCustom) {
		// check race
		RaceType		playerRace = playerCustom.getRaceType();
		Set<RaceType>	raceTypes = type.getRaceTypes();
		if (raceTypes != null && !raceTypes.contains(playerRace)) {
			playerCustom.getPlayer().sendActionBar(Message.cantUse());
			return false;
		}
		// check class
		ClassType		playerClass = playerCustom.getClassType();
		if (playerClass == ClassType.GOD) return true;
		Set<ClassType>	classTypes = type.getClassTypes();
		if (classTypes != null && !classTypes.contains(playerClass)) {
			playerCustom.getPlayer().sendActionBar(Message.cantUse());
			return false;
		}
		// cooldown
		int	duration = playerCustom.hasCooldown(type.getMaterial());
		if (duration > 0) {
			playerCustom.getPlayer().sendActionBar(Message.cooldown(duration));
			return false;
		}
		// check power
		Resource	power = playerCustom.getPower();
		if (power == null) {
			playerCustom.getPlayer().sendActionBar(Message.cantUse());
			return false;
		}
		int	cost = type.getCost(rarity);
		if (power.getValue() < cost) {
			playerCustom.getPlayer().sendActionBar(Message.notEnough(power.getType()));
			return false;
		}
		// can use
		playerCustom.addCooldown(type.getMaterial(), type.getCooldown(rarity));
		power.decrease(cost);
		playerCustom.getScoreboardCustom().refreshPower();
		return true;
	}

	private void cleanBossBar(Player player) {
		UUID		uuid = player.getUniqueId();
		BossBarData	bossBarData = bossBars.get(uuid);
		if (bossBarData == null) return;
		player.hideBossBar(bossBarData.getBossBar());
		bossBarData.getBukkitTask().cancel();
		bossBars.remove(uuid);
	}

	/////////////////////
	/////* warrior */////
	/////////////////////

	private void kneeBreaker(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().addKneeBreaker(damager.getUniqueId(), rarity.getNumber());
		particleWhirlwind(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_kneebreaker");
	}

	private void particleWhirlwind(Location loc) {
		loc.getWorld().spawnParticle(Particle.CRIT, loc, 30, 0.3, 0.3, 0.3, 0.05);
	}

	private void whirlwind(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player			damager = playerCustom.getPlayer();
		World			world = damager.getWorld();
		Location		center = damager.getLocation();
        double          radius = 4;
		// damage = physical damage * (50% for rarity 1, 60% for rarity 2, ...)
		double			damage = StatSecondary.getAmount(playerCustom, StatSecondary.PHYSICAL_DAMAGE);
		damage *= (rarity.getNumber() + 4) / 10d;
        for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
			if (Group.isInSameGroup(livingTarget, damager)) continue;
			livingTarget.damage(damage, CombatDamage.getDamageSource(damager, CombatDamage.MAGIC.getType()));
		}
		particleWhirlwind(radius, center);
		SoundManager.playSound(playerCustom, "spell_whirlwind");
	}

	private void particleWhirlwind(double radius, Location loc) {
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

	private void leap(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		Location	center = damager.getLocation();
    	Vector		dir = center.getDirection().normalize();
    	dir.setY(0.5);
    	damager.setVelocity(dir.multiply(1.5));
		itemCustomManager.getSpellManager().addLeap(damager, rarity.getNumber());
		particleLeap(center);
    	SoundManager.playSound(playerCustom, "spell_leap");
	}

	private void particleLeap(Location loc) {
    	loc.getWorld().spawnParticle(Particle.CLOUD, loc, 30, 0.3, 0.3, 0.3, 0.05);
	}

	private void deadlyMagnet(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		World		world = damager.getWorld();
		Location	center = damager.getLocation();
        double      radius = 9 + rarity.getNumber();
		double		forceMax = 3;
		double		aggro = rarity.getNumber() * 2;
		new BukkitRunnable() {
		    int			ticks = 0;
		    final int	maxTicks = SpellManager.TIME_DEADLYMAGNET * 4; // 4 ticks = 1 seconds
		    @Override
		    public void run() {
		        for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
		            if (livingTarget.equals(damager)) continue;
					if (Group.isInSameGroup(livingTarget, damager)) continue;
					if (TraitSentinel.isBoss(livingTarget)) continue;
		            Vector	toPlayer = center.toVector().subtract(livingTarget.getLocation().toVector());
		            double	distance = toPlayer.length();
		            if (distance < 0.5) continue;
		            double	force = Math.pow(distance / radius, 2) * forceMax;
		            Vector	velocity = toPlayer.normalize().multiply(force);
		            livingTarget.setVelocity(velocity);
					NPC	npcTarget = CitizensAPI.getNPCRegistry().getNPC(livingTarget);
					// set aggro
					if (npcTarget != null)
						npcTarget.getOrAddTrait(TraitSentinel.class).addAggro(damager, aggro);
					else if (livingTarget instanceof Mob mob)
						mob.setTarget(damager);
					particleEntityDeadlyMagnet(livingTarget.getLocation());
		        }
				particleDeadlyMagnet(center, radius, ticks, maxTicks);
		        ticks++;
		        if (ticks >= maxTicks)
		            cancel();
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 5L);
		SoundManager.playSound(playerCustom, "spell_deadlymagnet");
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
	////* pyromancer *///
	/////////////////////

	private void fireBall(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
	    Vector	direction = damager.getLocation().getDirection();
    	direction.normalize();
		Fireball 				fireball = damager.launchProjectile(Fireball.class);
		PersistentDataContainer	pdc = fireball.getPersistentDataContainer();
		Data.setInteger(pdc, itemCustomManager.getSpellManager().getKeyFireBall(), rarity.getNumber());
		fireball.setYield(0);
		fireball.setVelocity(direction.multiply(1.5));
		particleFireBall(fireball);
		SoundManager.playSound(playerCustom, "spell_fireball");
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

	private void teleport(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		World		world = damager.getWorld();
    	Location	startLoc = damager.getLocation();
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
		particleTeleport(startLoc);
    	SoundManager.playSound(playerCustom, "spell_teleportation");
		damager.setVelocity(new Vector(0,0,0));
		damager.setFallDistance(0);
    	damager.teleport(nextLoc);
		particleTeleport(nextLoc);
		SoundManager.playSound(playerCustom, "spell_teleportation");
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

	private int getHighestSolidBlockY(World world, int x, int z, int maxY) {
	    maxY = Math.min(maxY, world.getMaxHeight() - 1);
	    for (int y = maxY; y >= world.getMinHeight(); y--) {
	        Block block = world.getBlockAt(x, y, z);
	        if (block.getType().isSolid())
	            return y;
	    }
	    return world.getMinHeight();
	}

	private void manaThirst(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		Health	health = playerCustom.getHealth();
		double	healthPercent = (38 - rarity.getNumber() * 3) / 100d;
		double	healthAmount = health.getValueMax() * healthPercent;
		health.setValue(Math.max(1, health.getValue() - healthAmount));
		Resource	power = playerCustom.getPower();
		if (power == null) return;
		double	powerPercent = (30 + rarity.getNumber() * 4) / 100d;
		double	powerAmount = power.getValueMax() * powerPercent;
		power.increase(Math.max(0, powerAmount));
		playerCustom.getScoreboardCustom().refreshHealth();
		playerCustom.getScoreboardCustom().refreshPower();
		particleManaThirst(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_manathirst");
	}

	private void particleManaThirst(Location loc) {
		loc.getWorld().spawnParticle(Particle.FLASH, loc, 30, 1.5, 1.5, 1.5, 0.2);
	}

	private void flameNova(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		Location	center = damager.getLocation();
		World		world = damager.getWorld();
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
        for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
            if (livingTarget.equals(damager)) continue;
			if (Group.isInSameGroup(livingTarget, damager)) continue;
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
			livingTarget.damage(damage, CombatDamage.getDamageSource(damager, CombatDamage.MAGIC.getType()));
		}
		particleFlameNova(radius, center);
		SoundManager.playSound(playerCustom, "spell_flamenova");
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
	//////* rogue *//////
	/////////////////////
	
	private void stealth(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		UUID		uuid = damager.getUniqueId();
		if (itemCustomManager.getSpellManager().hasStealth(uuid)) {
			int id = itemCustomManager.getSpellManager().removeStealth(uuid);
			playerCustom.removeStatModifier(id);
			// damager.setInvisible(false);
			SpellManager.removeInvisibility(damager);
			SoundManager.playSound(playerCustom, "spell_stealth");
			return;
		}
		int		value = (-50 + rarity.getNumber() * 5);
		int		id = playerCustom.addStatModifier(StatSecondary.SPEED, value, 0);
		itemCustomManager.getSpellManager().addStealth(uuid, id);
		// damager.setInvisible(true);
		SpellManager.applyInvisibility(damager);
		particleStealth(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_stealth");
    }

	private void particleStealth(Location loc) {
		loc.getWorld().spawnParticle(Particle.SMOKE, loc, 30, 0.5, 0.5, 0.5, 0.1);
	}

	private void escape(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		int		dodge = (rarity.getNumber() * 50); // 50 = 10% dodge
		int		attackSpeed = 100;
		int		duration = SpellManager.TIME_ESCAPE;
		playerCustom.addStatModifier(StatSecondary.DODGE, dodge, duration);
		playerCustom.addStatModifier(StatSecondary.ATTACK_SPEED, attackSpeed, duration);
		particleEscape(playerCustom.getPlayer().getLocation());
		SoundManager.playSound(playerCustom, "spell_escape");
	}

	private void particleEscape(Location loc) {
		loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 30, 0.3, 0.5, 0.3, 0.02);
	}

	private void sprint(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		int			speed = (rarity.getNumber() * 5 + 10);
		int			duration = 8;
		Location	loc = damager.getLocation();
		double		force = 0.8 + rarity.getNumber() * 0.2;
		Vector		direction = loc.getDirection().setY(0).normalize();
		damager.setVelocity(direction.multiply(force).setY(0.1));
		playerCustom.addStatModifier(StatSecondary.SPEED, speed, duration);
		particleSprint(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_sprint");
	}

	private void particleSprint(Location loc) {
		loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 30, 0.3, 0.5, 0.3, 0.02);
	}

	private void coldBlood(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().addColdBlood(damager.getUniqueId(), rarity.getNumber());
		particleColdBlood(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_coldblood");
	}

	private void particleColdBlood(Location loc) {
		loc.getWorld().spawnParticle(Particle.FIREWORK, loc, 30, 0.3, 1, 0.3, 0.05);
	}

	/////////////////////
	//////* priest */////
	/////////////////////

	private void holyBomb(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().addHolyBomb(damager.getUniqueId(), rarity.getNumber());
		SoundManager.playSound(playerCustom, "spell_holybomb");
	}

	private void holyLand(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		Location	center = damager.getLocation();
		double		radius = (rarity.getNumber() + 4);
		double		heal = (rarity.getNumber() * 0.5);
		double		mana = (rarity.getNumber() * 0.5);
		new BukkitRunnable() {
			int	seconds = 0;
		    @Override
			public void run() {
				for (LivingEntity livingTarget : damager.getWorld().getNearbyLivingEntities(center, radius)) {
					PlayerCustom	playerCustomTarget = null;
					if (livingTarget.equals(damager))
						playerCustomTarget = playerCustom;
					else if (Group.isInSameGroup(livingTarget, damager)) {
						if (livingTarget instanceof Player player)
							playerCustomTarget = PlayerCustomManager.getPlayerCustom(player);
					}
					if (playerCustomTarget != null) {
						CombatDamage.heal(playerCustomTarget, heal);
						Resource		power = playerCustomTarget.getPower();
						if (power == null || power.getType() != ResourceType.MANA) continue;
						power.increase(mana);
						playerCustomTarget.getScoreboardCustom().refreshPower();
					}
					else
						CombatDamage.heal(livingTarget, heal);
				}
				particleHolyLand(radius, center);
				seconds++;
				if (seconds >= SpellManager.TIME_HOLYLAND) cancel();
			}
		}.runTaskTimer(RpgCraft.instance(), 0L, 20L);
		SoundManager.playSound(playerCustom, "spell_holyland");
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

	private void holyShield(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().addHolyShield(damager.getUniqueId(), rarity.getNumber());
		particleHolyShield(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_holyshield");
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

	private void shadowWord(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
    	Vector	direction = damager.getLocation().getDirection();
    	direction.normalize();
		WitherSkull				witherSkull = damager.launchProjectile(WitherSkull.class);
		PersistentDataContainer	pdc = witherSkull.getPersistentDataContainer();
		Data.setInteger(pdc, itemCustomManager.getSpellManager().getKeyShadowWord(), rarity.getNumber());
		witherSkull.setYield(0);
		witherSkull.setCharged(false);
		witherSkull.setVelocity(direction.multiply(1));
		particleShadowWord(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_shadowword");
	}

	private void particleShadowWord(Location loc) {
		loc.getWorld().spawnParticle(Particle.TRIAL_OMEN, loc, 40, 0.5, 0.5, 0.5, 0.05);
	}

	/////////////////////
	/////* dracthyr *////
	/////////////////////

	private void dragonBreath(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		World		world = damager.getWorld();
    	double		radius = 8 + rarity.getNumber();
    	double		maxAngle = Math.toRadians(25); // 50°
    	double		maxForce = 3;
    	Vector		direction = damager.getLocation().getDirection().normalize();
    	Location	center = damager.getLocation();
		double		damage = (3 + rarity.getNumber() * 3);
		for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
            if (livingTarget.equals(damager)) continue;
			if (Group.isInSameGroup(livingTarget, damager)) continue;
    		Vector 			toEntity = livingTarget.getLocation().toVector().subtract(center.toVector());
    		double 			distance = toEntity.length();
    		if (distance <= 0 || distance > radius) continue;
    		toEntity.normalize();
    		double dot = direction.dot(toEntity);
    		if (dot < Math.cos(maxAngle)) continue;
			if (!TraitSentinel.isBoss(livingTarget)) {
        		double	strength = (1 - (distance / radius)) * maxForce;
        		Vector	velocity = direction.clone().multiply(strength);
        		velocity.setY(velocity.getY() + 0.3);
        		livingTarget.setVelocity(velocity);
			}
			livingTarget.damage(damage, CombatDamage.getDamageSource(damager, CombatDamage.MAGIC.getType()));
		}
		particleDragonBreath(radius, maxAngle, direction, center);
		SoundManager.playSound(playerCustom, "spell_dragonbreath");
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

	private void dragonSkin(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		SpellManager	spellManager = itemCustomManager.getSpellManager();
		Player			damager = playerCustom.getPlayer();
		World			world = damager.getWorld();
		Location		center = damager.getLocation();
		double			radius = (rarity.getNumber() * 0.5 + 3);
		new BukkitRunnable() {
			Map<UUID, PlayerCustom>	currentPlayers = new HashMap<>();
			Map<UUID, PlayerCustom>	activePlayers = new HashMap<>();
			Map<UUID, LivingEntity>	currentEntities = new HashMap<>();
			Map<UUID, LivingEntity>	activeEntities = new HashMap<>();
			float		seconds = 0f;
		    @Override
			public void run() {
				currentPlayers.clear();
				currentEntities.clear();
				for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
					UUID	uuid = livingTarget.getUniqueId();
					if (activePlayers.containsKey(uuid)) {
						PlayerCustom	playerCustomTarget = PlayerCustomManager.getPlayerCustom((Player)livingTarget);
						currentPlayers.put(uuid, playerCustomTarget);
						continue;
					}
					else if (activeEntities.containsKey(uuid)) {
						currentEntities.put(uuid, livingTarget);
						continue;
					}
					if (livingTarget.equals(damager)) {
						activePlayers.put(uuid, playerCustom);
						currentPlayers.put(uuid, playerCustom);
						spellManager.addDragonSkin(playerCustom, rarity.getNumber());
					}
					else if (Group.isInSameGroup(livingTarget, damager)) {
						if (livingTarget instanceof Player player && !player.hasMetadata("NPC")) {
							PlayerCustom	playerCustomTarget = PlayerCustomManager.getPlayerCustom(player);
							activePlayers.put(uuid, playerCustom);
							currentPlayers.put(uuid, playerCustom);
							spellManager.addDragonSkin(playerCustomTarget, rarity.getNumber());
						}
						else {
							activeEntities.put(uuid, livingTarget);
							currentEntities.put(uuid, livingTarget);
							spellManager.addDragonSkin(livingTarget, rarity.getNumber());
						}
					}
				}
				Iterator<Entry<UUID, PlayerCustom>>	itPlayers = activePlayers.entrySet().iterator();
				while (itPlayers.hasNext()) {
					Entry<UUID, PlayerCustom>	entry = itPlayers.next();
					if (!currentPlayers.containsKey(entry.getKey())) {
						spellManager.removeDragonSkin(entry.getValue());
						itPlayers.remove();
					}
				}
				Iterator<Entry<UUID, LivingEntity>>	itEntities = activeEntities.entrySet().iterator();
				while (itEntities.hasNext()) {
					Entry<UUID, LivingEntity>	entry = itEntities.next();
					if (!currentPlayers.containsKey(entry.getKey())) {
						spellManager.removeDragonSkin(entry.getValue());
						itEntities.remove();
					}
				}
				particleDragonSkin(radius, center);
				seconds++;
				if (seconds >= SpellManager.TIME_DRAGONSKIN) {
					for(PlayerCustom playerCustom: activePlayers.values())
						spellManager.removeDragonSkin(playerCustom);
					for(LivingEntity livingEntity: activeEntities.values())
						spellManager.removeDragonSkin(livingEntity);
					activePlayers.clear();
					activeEntities.clear();
					cancel();
				}
			}
		}.runTaskTimer(RpgCraft.instance(), 0L, 20L);
		SoundManager.playSound(playerCustom, "spell_dragonskin");
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

	private void metamorph(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().addMetamorph(itemCustomManager, playerCustom, rarity.getNumber());
		particleMetamorph(damager.getLocation());
	}

	private void particleMetamorph(Location loc) {}

	private void strikeBack(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().addStrikeBack(damager.getUniqueId(), rarity.getNumber());
		particleStrikeBack(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_strikeback");
	}

	private void particleStrikeBack(Location loc) {}

	public void explosiveShot(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().addExplosiveShot(damager.getUniqueId(), rarity.getNumber());
		particleExplosiveShot(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_explosiveshot");
	}

	private void particleExplosiveShot(Location loc) {
		loc.getWorld().spawnParticle(Particle.FIREWORK, loc, 40, 0.5, 0.5, 0.5, 0.05);
	}

	public void pet(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player	damager = playerCustom.getPlayer();
		itemCustomManager.getSpellManager().pet(playerCustom, rarity.getNumber());
		particlePet(damager.getLocation());
		SoundManager.playSound(playerCustom, "spell_pet");
	}

	private void particlePet(Location loc) {
		loc.getWorld().spawnParticle(Particle.SPIT, loc, 40, 0.5, 0.5, 0.5, 0.05);
	}

	public void hunt(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player			damager = playerCustom.getPlayer();
		EntityManager	entityManager = itemCustomManager.getSpellManager().getEntityManager();
		LivingEntity	target = entityManager.getTarget(playerCustom);
		if (target == null || target.isDead() || !target.isValid()) return;
		if (damager.equals(target) || Group.isInSameGroup(playerCustom.getPlayer(), target)) return;
		int	value = -50 * rarity.getNumber();
		if (!target.hasMetadata("NPC") && target instanceof Player playerTarget) {
			PlayerCustom	playerCustomTarget = PlayerCustomManager.getPlayerCustom(playerTarget);
			playerCustomTarget.addStatModifier(StatSecondary.PHYSICAL_ARMOR, value, SpellManager.TIME_HUNT);
		}
		else
			entityManager.getEntityModifierManager().addModifier(target, StatSecondary.PHYSICAL_ARMOR, value, SpellManager.TIME_HUNT);
		target.setGlowing(true);
		new BukkitRunnable() {
			int		seconds = 0;
			double	angle = 0;
			@Override
			public void run() {
				if (target.isDead() || !target.isValid()) {
					target.setGlowing(false);
					cancel();
					return;
				}
				particleHunt(angle, target.getLocation().add(0, target.getHeight() + 2, 0));
				seconds++;
				angle += 0.2;
				if (seconds >= SpellManager.TIME_HUNT * 2) {
					target.setGlowing(false);
					cancel();
				}
			}
		}.runTaskTimer(RpgCraft.instance(), 0, 10);
		SoundManager.playSound(playerCustom, "spell_hunt");
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

	public void iceTrap(ItemCustomManager itemCustomManager, PlayerCustom playerCustom) {
		Player		damager = playerCustom.getPlayer();
		World		world = damager.getWorld();
		Location	center = damager.getLocation();
		double		radius = 3;
		new BukkitRunnable() {
			int		seconds = 0;
			double	angle = 0;
			@Override
			public void run() {
        		for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
					if (livingTarget.equals(damager)) continue;
					if (Group.isInSameGroup(livingTarget, damager)) continue;
					double	explosionRadius = 8;
					double	damage = rarity.getNumber() * 4;
					int		freezeTicks = 60 + rarity.getNumber() * 10;
					itemCustomManager.getSpellManager().iceExplosion(damager, center, explosionRadius, damage, freezeTicks);
					cancel();
					return;
				}
				particleIceTrap(angle, radius, center.clone().add(0, 1, 0));
				seconds++;
				angle += 0.2;
				if (seconds >= SpellManager.TIME_ICETRAP * 4)
					cancel();
			}
		}.runTaskTimer(RpgCraft.instance(), 0, 5);
		SoundManager.playSound(playerCustom, "spell_icetrap");
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
}
