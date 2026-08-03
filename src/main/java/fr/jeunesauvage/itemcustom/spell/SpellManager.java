package fr.jeunesauvage.itemcustom.spell;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.group.Group;
import fr.jeunesauvage.entity.npc.template.TemplateType;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.equipable.weapon.launcher.LauncherManager;
import fr.jeunesauvage.sound.SoundManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class SpellManager implements Listener {
	private static final NamespacedKey			KEY_FIREBALL = new NamespacedKey(RpgCraft.name(), "fireball");
	private static final NamespacedKey			KEY_SHADOWWORD = new NamespacedKey(RpgCraft.name(), "shadowword");
	public static final int						TIME_DEADLYMAGNET = 4;
	public static final int						TIME_ESCAPE = 8;
	public static final int						TIME_HOLYLAND = 10;
	public static final int						TIME_HOLYSHIELD = 8;
	public static final int						TIME_DRAGONSKIN = 20;
	public static final int						TIME_METAMORPH = 40;
	public static final int						TIME_STRIKEBACK = 10;
	public static final int						TIME_HUNT = 15;
	public static final int						TIME_ICETRAP = 30;
	private final EntityManager					entityManager;
	private final MetamorphManager				metamorphManager;	
	private final Map<UUID, Integer>			kneeBreaker = new HashMap<>();
	private final Map<UUID, BukkitTask>			leap = new HashMap<>();
	private final Map<UUID, Integer>			stealth = new HashMap<>();
	private final Map<UUID, Integer>			coldBlood = new HashMap<>();
	private final Map<UUID, Integer>			holyBomb = new HashMap<>();
	private final Map<UUID, DataTask<Integer>>	holyShield = new HashMap<>();
	private final Map<UUID, Integer>			dragonSkinPlayer = new HashMap<>();
	private final Map<UUID, Integer>			dragonSkinEntity = new HashMap<>();
	private final Map<UUID, DataTask<Integer>>	strikeBack = new HashMap<>();
	private final Map<UUID, Boolean>			canUseStrikeback = new HashMap<>();
	private final Map<UUID, Integer>			explosiveShot = new HashMap<>();
	private final Map<UUID, NPC>				pets = new HashMap<>();

	public SpellManager(JavaPlugin plugin, EntityManager entityManager) {
		this.entityManager = entityManager;
		this.metamorphManager = new MetamorphManager();
		plugin.getServer().getPluginManager().registerEvents(metamorphManager, plugin);
	}

	public void addKneeBreaker(UUID uuid, int level) {
		kneeBreaker.put(uuid, level);
	}

	public int removeKneeBreaker(UUID uuid) {
		Integer	level = kneeBreaker.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasKneeBreaker(UUID uuid) {
		return kneeBreaker.containsKey(uuid);
	}

	public void addLeap(Player player, int level) {
		UUID	uuid = player.getUniqueId();
		if (hasLeap(uuid)) return;
		leap.put(uuid, new BukkitRunnable() {
			boolean	cancel = false;
		    @Override
		    public void run() {
				if (cancel) {
					removeLeap(uuid);
					return;
				}
				if (!isLanding(player)) return;
	    		Location	loc = player.getLocation();
				double		radius = 6;
				double 		damage = level * 4 + 2;
				explosion(player, loc, radius, damage, 0.5, 0);
				cancel = true;
		    }
		}.runTaskTimer(RpgCraft.instance(), 5L, 2L));
	}

	public void removeLeap(UUID uuid) {
		BukkitTask	task = leap.remove(uuid);
		if (task != null)
			task.cancel();
	}

	public boolean hasLeap(UUID uuid) {
		return leap.containsKey(uuid);
	}

	private boolean isLanding(Player player) {
    	BoundingBox	box = player.getBoundingBox();
    	World		world = player.getWorld();
    	double		y = box.getMinY() - 0.01;
    	for (double x = box.getMinX(); x <= box.getMaxX(); x += 0.3) {
    	    for (double z = box.getMinZ(); z <= box.getMaxZ(); z += 0.3) {
    	        Block block = world.getBlockAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    	        if (block.getType().isSolid())
    	            return true;
    	    }
    	}
    	return false;
	}

	public NamespacedKey getKeyFireBall() {
		return KEY_FIREBALL;
	}

	// basic explosion
	public void explosion(LivingEntity launcher, Location center, double radius, double damage, double force, int fireticks) {
	    World	world = center.getWorld();
	    for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
            if (livingTarget.equals(launcher)) continue;
			if (Group.isInSameGroup(livingTarget, launcher)) continue;
			if (force > 0 && !TraitSentinel.isBoss(livingTarget)) {
				Vector	direction = livingTarget.getLocation().toVector().subtract(center.toVector()).normalize();
				double	resistance = 0;
				AttributeInstance	instance = livingTarget.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
				if (instance != null)
					resistance = instance.getValue();
	        	direction.multiply(force - (force * resistance));
	        	direction.setY(direction.getY() + 0.3);
				livingTarget.setVelocity(direction);
			}
			livingTarget.damage(damage, CombatDamage.getDamageSource(launcher, CombatDamage.MAGIC.getType()));
			if (fireticks > 0)
				livingTarget.setFireTicks(fireticks);
	    }
	    world.spawnParticle(Particle.EXPLOSION, center, 3);
		world.spawnParticle(Particle.CLOUD, center, 40, 1.5, 1.5, 1.5, 0.1);
	    world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1f);
	}

	public void addStealth(UUID uuid, int id) {
		stealth.put(uuid, id);
	}

	public int removeStealth(UUID uuid) {
		Integer	id = stealth.remove(uuid);
		return (id == null ? -1 : id);
	}

	public boolean hasStealth(UUID uuid) {
		return stealth.containsKey(uuid);
	}

	public static void applyInvisibility(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 7199980, 0, false, false, false));
	}

	public static void removeInvisibility(Player player) {
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

	public void addColdBlood(UUID uuid, int level) {
		coldBlood.put(uuid, level);
	}

	public int removeColdBlood(UUID uuid) {
		Integer	level = coldBlood.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasColdBlood(UUID uuid) {
		return coldBlood.containsKey(uuid);
	}

	public void addHolyBomb(UUID uuid, int level) {
		holyBomb.put(uuid, level);
	}

	public int removeHolyBomb(UUID uuid) {
		Integer	level = holyBomb.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasHolyBomb(UUID uuid) {
		return holyBomb.containsKey(uuid);
	}

	private void holyBombExplosion(Player damager, Location center, int level) {
	    World	world = center.getWorld();
		double	radius = 5;
		double	damage = level * 3;
		double	heal = level * 2;
		double	force = 1;
		for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
			if (Group.isInSameGroup(livingTarget, damager) || livingTarget.equals(damager)) {
				CombatDamage.heal(livingTarget, heal);
				continue;
			}
			if (!TraitSentinel.isBoss(livingTarget)) {
				Vector	direction = livingTarget.getLocation().toVector().subtract(center.toVector()).normalize();
				double	resistance = 0;
				AttributeInstance	instance = livingTarget.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
				if (instance != null)
					resistance = instance.getValue();
	        	direction.multiply(force - (force * resistance));
	        	direction.setY(direction.getY() + 0.3);
				livingTarget.setVelocity(direction);
			}
			livingTarget.damage(damage, CombatDamage.getDamageSource(damager, CombatDamage.MAGIC.getType()));
		}
		particleHolyBombExplosion(radius, center);
		SoundManager.playSound(center, "spell_holybomb_hit");
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

	public void addHolyShield(UUID uuid, int level) {
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

	public void holyShield(Player damager, int level, double damage) {
		World		world = damager.getWorld();
		Location	center = damager.getLocation();
		double		radius = (level * 0.5d + 4.5);
		damage *= (4 + level) / 10d;
		for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
			if (livingTarget.equals(damager)) continue;
			if (Group.isInSameGroup(livingTarget, damager)) continue;
			livingTarget.damage(damage, CombatDamage.getDamageSource(damager, CombatDamage.MAGIC.getType()));
			particleHolyShield(center);
			break;
		}
	}

	private void particleHolyShield(Location center) {
		center.getWorld().spawnParticle(Particle.CRIT, center, 30, 0.5, 0.5, 0.5, 0.2);
	}

	public NamespacedKey getKeyShadowWord() {
		return KEY_SHADOWWORD;
	}

	public void shadowWordExplosion(Player launcher, Location center, int level) {
	    World	world = center.getWorld();
		double	radius = 4;
		double 	damage = level * 3 + 3;
		int		silence = (int)(level * 0.5 + 3);
		for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
    	    if (livingTarget.equals(launcher)) continue;
			if (Group.isInSameGroup(livingTarget, launcher)) continue;
			if (livingTarget instanceof Player playerTarget && !playerTarget.hasMetadata("NPC")) {
				PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(playerTarget);
				if (playerCustom != null)
					playerCustom.addSilence(silence);
			}
			livingTarget.damage(damage, CombatDamage.getDamageSource(launcher, CombatDamage.MAGIC.getType()));
		}
		particleShadowWordExplosion(radius, center);
		SoundManager.playSound(center, "spell_shadowword_hit");
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

	public void addDragonSkin(PlayerCustom playerCustom, int level) {
		UUID	uuid = playerCustom.getPlayer().getUniqueId();
		int		value = level * 100;
		dragonSkinPlayer.put(uuid, playerCustom.addStatModifier(StatSecondary.SPELL_ARMOR, value, 0));
	}

	public void addDragonSkin(LivingEntity livingEntity, int level) {
		UUID	uuid = livingEntity.getUniqueId();
		int		value = level * 100;
		dragonSkinEntity.put(uuid, entityManager.getEntityModifierManager().addModifier(livingEntity, StatSecondary.SPELL_ARMOR, value, 0));
	}

	public void removeDragonSkin(PlayerCustom playerCustom) {
		UUID	uuid = playerCustom.getPlayer().getUniqueId();
		Integer	id = dragonSkinPlayer.get(uuid);
		if (id == null) return;
		playerCustom.removeStatModifier(id);
	}

	public void removeDragonSkin(LivingEntity livingEntity) {
		UUID	uuid = livingEntity.getUniqueId();
		Integer	id = dragonSkinEntity.get(uuid);
		if (id == null) return;
		entityManager.getEntityModifierManager().removeModifier(livingEntity, id);
	}

	public void addMetamorph(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, int level) {
		metamorphManager.addDracthyr(itemCustomManager, playerCustom, level);
	}

	public void addStrikeBack(UUID uuid, int level) {
		DataTask<Integer>	dataTask;
		if (strikeBack.containsKey(uuid)) {
			dataTask = strikeBack.get(uuid);
			dataTask.cancel();
		}
		else
			dataTask = new DataTask<Integer>(1, null);
		dataTask.setData(level);
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

	public void strikeBack(LivingEntity damager, int level) {
	    World		world = damager.getWorld();
		Location	center = damager.getLocation();
		double		radius = 6;
		double 		damage = level * 4;
		for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
    	    if (livingTarget.equals(damager)) continue;
			if (Group.isInSameGroup(livingTarget, damager)) continue;
			livingTarget.damage(damage, CombatDamage.getDamageSource(damager, CombatDamage.MAGIC.getType()));
		}
		particleStrikeBack(radius, center);
		SoundManager.playSound(center, "spell_strikeback_hit");
	}

	private void particleStrikeBack(double radius, Location center) {
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

	public void addExplosiveShot(UUID uuid, int level) {
		explosiveShot.put(uuid, level);
	}

	public int removeExplosiveShot(UUID uuid) {
		Integer	level = explosiveShot.remove(uuid);
		return (level == null ? 1 : level);
	}

	public boolean hasExplosiveShot(UUID uuid) {
		return explosiveShot.containsKey(uuid);
	}

	public void pet(PlayerCustom playerCustom, int level) {
		Player	player = playerCustom.getPlayer();
		UUID	uuid = player.getUniqueId();
		NPC		pet = pets.get(uuid);
		if (player.isSneaking()) {
			if (pet != null) {
				pet.destroy();
				pets.remove(uuid);
			}
		}
		else if (pet == null)
			createPet(playerCustom);
		else if (!(pet.getEntity() instanceof LivingEntity petEntity))
			teleportPet(player, pet);
		else if (petEntity.isDead() || !petEntity.isValid())
			teleportPet(player, pet);
		else if (petEntity.getLocation().distanceSquared(player.getLocation()) > 40 * 40)
			teleportPet(player, pet);
		else
			attackPet(playerCustom, pet);
	}

	private void createPet(PlayerCustom playerCustom) {
		Player			player = playerCustom.getPlayer();
		UUID			uuid = player.getUniqueId();
		TemplateType	templateType = TemplateType.PET_WOLF;
		NPC				pet = CitizensAPI.getNPCRegistry().createNPC(templateType.getEntityType(), templateType.getHideName(), player.getLocation());
		TraitSentinel	traitSentinel = pet.getOrAddTrait(TraitSentinel.class);
		traitSentinel.setLevel((int)playerCustom.getLevel().getValue());
		traitSentinel.setTemplate(templateType);
		traitSentinel.setOwner(player);
		pet.setProtected(false);
		pet.getNavigator().setTarget(player, false);
		pets.put(uuid, pet);
	}

	private void teleportPet(Player player, NPC pet) {
		pet.despawn();
		pet.spawn(player.getLocation());
		pet.getOrAddTrait(TraitSentinel.class).getTargetHelper().cleanAggro();
	}

	private void removePet(PlayerCustom playerCustom) {
		Player			player = playerCustom.getPlayer();
		UUID			uuid = player.getUniqueId();
		NPC				pet = pets.get(uuid);
		if (pet != null) {
			pet.destroy();
			pets.remove(uuid, pet);
		}
	}

	private void attackPet(PlayerCustom playerCustom, NPC pet) {
		LivingEntity	target = entityManager.getTarget(playerCustom);
		if (target == null || target.isDead() || !target.isValid()) return;
		TraitSentinel	traitSentinel = pet.getOrAddTrait(TraitSentinel.class);
		LivingEntity	actuelTarget = traitSentinel.getTargetHelper().getTarget();
		if (actuelTarget != null)
			traitSentinel.getTargetHelper().cleanAggro();
		else
			traitSentinel.addAggro(target, 1000);
	}

	public void iceExplosion(LivingEntity launcher, Location center, double radius, double damage, int freezeTicks) {
	    World	world = center.getWorld();
	    for (LivingEntity livingTarget : world.getNearbyLivingEntities(center, radius)) {
            if (livingTarget.equals(launcher)) continue;
			if (Group.isInSameGroup(livingTarget, launcher)) continue;
			if (!TraitSentinel.isBoss(livingTarget)) {
				if (!livingTarget.hasMetadata("NPC") && livingTarget instanceof Player playerTarget) {
					PlayerCustom	playerCustomTarget = PlayerCustomManager.getPlayerCustom(playerTarget);
					playerCustomTarget.addStatModifier(StatSecondary.SPEED, -60, freezeTicks / 20);
				}
				else
					entityManager.getEntityModifierManager().addModifier(livingTarget, StatSecondary.SPEED, -60, freezeTicks / 20);
				if (freezeTicks > 0)
					livingTarget.setFreezeTicks(freezeTicks);
			}
			livingTarget.damage(damage, CombatDamage.getDamageSource(launcher, CombatDamage.MAGIC.getType()));
	    }
	    particleIceExplosion(radius, center);
	    SoundManager.playSound(center, "spell_icetrap_hit");
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

	////////////////
	/// listener ///
	////////////////

	// projectile
	@EventHandler
	public void onProjectileExplosion(ProjectileHitEvent e) {
	    Projectile	projectile = e.getEntity();
		if (!(projectile.getShooter() instanceof Player player)) return;
		UUID					uuid = player.getUniqueId();
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
		// fireball
		if (Data.hasInteger(pdc, KEY_FIREBALL)) {
			int	level = Data.getInteger(pdc, KEY_FIREBALL);
			if (level == 0) return;
			Location	location = projectile.getLocation();
			double		radius = (level * 0.5d) + 3.5;
			double 		damage = level * 4;
			int			fireTicks = (level + 2) * 20;
			explosion(player, location, radius, damage, 1, fireTicks);
		}
		// holybomb
		else if (hasHolyBomb(uuid) && Data.hasBoolean(pdc, LauncherManager.getStaffKey())) {
			int		level = removeHolyBomb(uuid);
			if (projectile instanceof SmallFireball smallFireball)
				smallFireball.setIsIncendiary(false);
			holyBombExplosion(player, projectile.getLocation(), level);
		}
		// shadowword
		else if (Data.hasInteger(pdc, KEY_SHADOWWORD)) {
			int	level = Data.getInteger(pdc, KEY_SHADOWWORD);
			if (level == 0) return;
			Location	location = projectile.getLocation();
			shadowWordExplosion(player, location, level);
		}
		// explosive shot
		else if (hasExplosiveShot(uuid) && (Data.hasBoolean(pdc, LauncherManager.getBowKey()) || Data.hasBoolean(pdc, LauncherManager.getCrossbowKey()))) {
			int	level = removeExplosiveShot(uuid);
			if (level == 0) return;
			Location	location = projectile.getLocation();
			double		radius = 4;
			double 		damage = level * 3;
			explosion(player, location, radius, damage, 1, 0);
		}
		else
			return;
		projectile.remove();
		e.setCancelled(true);
	}

	// cancel destruction on explosion
	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		e.blockList().clear();
		if (!(e.getEntity() instanceof Projectile projectile)) return;
		PersistentDataContainer	pdc = projectile.getPersistentDataContainer();
	    if (Data.hasInteger(pdc, KEY_FIREBALL) || Data.hasInteger(pdc, KEY_SHADOWWORD))
    		e.setCancelled(true);
	}

    // apply real invisibility
    @EventHandler
    public void onInvisibility(EntityPotionEffectEvent e) {
		PotionEffect	potionEffect = e.getNewEffect();
        if (potionEffect == null || potionEffect.getType() != PotionEffectType.INVISIBILITY) return;
        if (e.getEntity() instanceof Player player && !player.hasMetadata("NPC")) {
			Action	action = e.getAction();
			if (action == Action.ADDED) {
        		for (Player p: Bukkit.getOnlinePlayers()) {
        		    if (p.equals(player)) continue;
        		    p.hidePlayer(RpgCraft.instance(), player);
        		}
			}
			else if (action == Action.REMOVED || action == Action.CLEARED) {
        		for (Player p: Bukkit.getOnlinePlayers()) {
        		    if (p.equals(player)) continue;
        		    p.showPlayer(RpgCraft.instance(), player);
        		}
			}
		}
    }

	// cancel damage fall on leap and cancel stealth for all damage
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player player)) return;
		if (player.hasMetadata("NPC")) return;
		UUID	uuid = player.getUniqueId();
		// cancel stealth
		if (hasStealth(uuid)) {
			PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
			int				id = removeStealth(uuid);
			playerCustom.removeStatModifier(id);
			// player.setInvisible(false);
			SpellManager.removeInvisibility(player);
			SoundManager.playSound(playerCustom, "spell_stealth");
		}
		// cancel damage on leap
	    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (!leap.containsKey(player.getUniqueId())) return;
	    	e.setCancelled(true);
		}
	}

	public void clean(PlayerCustom playerCustom) {
		UUID	uuid = playerCustom.getPlayer().getUniqueId();
		removeKneeBreaker(uuid);
		removeLeap(uuid);
		removeStealth(uuid);
		removeColdBlood(uuid);
		removeHolyBomb(uuid);
		removeHolyShield(uuid);
		removeDragonSkin(playerCustom);
		removePet(playerCustom);
		metamorphManager.clean(playerCustom);
	}

	public MetamorphManager getMetamorphManager() {
		return metamorphManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}
}
