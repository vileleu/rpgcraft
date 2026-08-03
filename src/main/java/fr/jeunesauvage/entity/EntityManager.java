package fr.jeunesauvage.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.FluidCollisionMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.bossbar.BossBarData;
import fr.jeunesauvage.entity.bossbar.TargetData;
import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.modifier.EntityModifierManager;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.race.RaceType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class EntityManager implements Listener {
	private static final double				RANGEBOSSBAR_DEFAULT = 60;
	private static final long				TIMEBOSSBAR_DEFAULT = 40; // 60 seconds (4 == 1 second)
	private final EntityModifierManager 	entityModifierManager;
	private final Map<UUID, TargetData>		targets = new HashMap<>();

	public EntityManager(JavaPlugin plugin) {
		// modifier of entity
        this.entityModifierManager = new EntityModifierManager(plugin);
        plugin.getServer().getPluginManager().registerEvents(entityModifierManager, plugin);
	}

	// print infos of living entity
	@EventHandler(ignoreCancelled = false)
	public void onClick(PlayerInteractEvent e) {
	    Player	player = e.getPlayer();
		if (!player.isSneaking()) return;
		Action	action = e.getAction();
	    if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) return;
	    LivingEntity	livingTarget = getTargetEntity(player, RANGEBOSSBAR_DEFAULT);
	    if (livingTarget == null) return;
		// is NPC
		if (livingTarget.hasMetadata("NPC")) {
			NPC	npcTarget = CitizensAPI.getNPCRegistry().getNPC(livingTarget);
	    	printInfosEntity(player, livingTarget, npcTarget);
		}
		// is player or vanilla entity
		else {
			printInfosEntity(player, livingTarget);
		}
	}

	// get target with range
	private LivingEntity getTargetEntity(Player player, double range) {
		RayTraceResult	blockResult = player.getWorld().rayTraceBlocks(
		        player.getEyeLocation(),
		        player.getLocation().getDirection(),
		        range,
		        FluidCollisionMode.NEVER,
		        true
		);
	    double			maxDistance = range;
		// if we hit block ajust range
	    if (blockResult != null && blockResult.getHitPosition() != null)
	        maxDistance = blockResult.getHitPosition().distance(player.getEyeLocation().toVector());
	    RayTraceResult	entityResult = player.getWorld().rayTraceEntities(
	            player.getEyeLocation(),
	            player.getLocation().getDirection(),
	            maxDistance,
	            entity -> entity != player
	    );
	    if (entityResult == null) return null;
	    Entity	entity = entityResult.getHitEntity();
	    if (!(entity instanceof LivingEntity livingEntity)) return null;
	    return livingEntity;
	}

	private void printInfosEntity(Player player, LivingEntity livingEntity, NPC npc) {
		cleanBossBar(player);
		double		health = livingEntity.getHealth();
		double		maxHealth = 1;
		AttributeInstance	instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (instance != null)
			maxHealth = instance.getValue();
		float		progress = (float)(health / maxHealth);
		Component	component = Component.translatable("level.rpgcraft.short").decorate(TextDecoration.BOLD)
			.append(Component.text(npc.getOrAddTrait(TraitSentinel.class).getLevel() + " "))
			.append(Component.text(npc.getName() + " "));
		BossBar	bar = BossBar.bossBar(
		        component,
		        progress,
		        BossBar.Color.RED,
		        BossBar.Overlay.PROGRESS
		);
		player.showBossBar(bar);
		BossBarData	bossBarData = new BossBarData(bar, new BukkitRunnable() {
		    int	ticks = 0;
		    @Override
		    public void run() {
				double		health = livingEntity.getHealth();
				double		maxHealth = 1;
				AttributeInstance	instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				if (instance != null)
					maxHealth = instance.getValue();
				float		progress = (float)(health / maxHealth);
				Component	component = Component.translatable("level.rpgcraft.short").decorate(TextDecoration.BOLD)
					.append(Component.text(npc.getOrAddTrait(TraitSentinel.class).getLevel() + " "))
					.append(Component.text(npc.getName() + " "));
				bar.name(component);
				bar.progress(progress);
				ticks++;
				if (ticks == TIMEBOSSBAR_DEFAULT) {
					if (player.getLocation().distanceSquared(livingEntity.getLocation()) > RANGEBOSSBAR_DEFAULT * RANGEBOSSBAR_DEFAULT)
						cleanBossBar(player);
					else
						ticks = 0;
				}
				else if (player.isDead() || !player.isOnline() || livingEntity.isDead() || !livingEntity.isValid())
					cleanBossBar(player);
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 5L));
		targets.put(player.getUniqueId(), new TargetData(bossBarData, livingEntity));
	}

	private void printInfosEntity(Player player, LivingEntity livingEntity) {
		cleanBossBar(player);
		double		health = livingEntity.getHealth();
		double		maxHealth = 1;
		AttributeInstance	instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (instance != null)
			maxHealth = instance.getValue();
		float		progress = (float)(health / maxHealth);
		Component	component = Component.translatable("level.rpgcraft.short").decorate(TextDecoration.BOLD)
			.append(Component.text("??? "))
			.append(Component.text(livingEntity.getName() + " "));
		BossBar	bar = BossBar.bossBar(
		        component,
		        progress,
		        BossBar.Color.RED,
		        BossBar.Overlay.PROGRESS
		);
		player.showBossBar(bar);
		BossBarData	bossBarData = new BossBarData(bar, new BukkitRunnable() {
		    int	ticks = 0;
		    @Override
		    public void run() {
				double		health = livingEntity.getHealth();
				double		maxHealth = 1;
				AttributeInstance	instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
				if (instance != null)
					maxHealth = instance.getValue();
				float		progress = (float)(health / maxHealth);
				Component	component = Component.translatable("level.rpgcraft.short").decorate(TextDecoration.BOLD)
					.append(Component.text("??? "))
					.append(Component.text(livingEntity.getName() + " "));
				bar.name(component);
				bar.progress(progress);
				ticks++;
				if (ticks == TIMEBOSSBAR_DEFAULT) {
					if (player.getLocation().distanceSquared(livingEntity.getLocation()) > RANGEBOSSBAR_DEFAULT * RANGEBOSSBAR_DEFAULT)
						cleanBossBar(player);
					else
						ticks = 0;
				}
				else if (player.isDead() || !player.isOnline() || livingEntity.isDead() || !livingEntity.isValid())
					cleanBossBar(player);
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 5L));
		targets.put(player.getUniqueId(), new TargetData(bossBarData, livingEntity));
	}

	private void cleanBossBar(Player player) {
		UUID		uuid = player.getUniqueId();
		TargetData	targetData = targets.get(uuid);
		if (targetData == null) return;
		BossBarData	bossBarData = targetData.getBossBarData();
		if (bossBarData == null) return;
		player.hideBossBar(bossBarData.getBossBar());
		bossBarData.getBukkitTask().cancel();
		targets.remove(uuid);
	}

	public LivingEntity getTarget(PlayerCustom playerCustom) {
		UUID		uuid = playerCustom.getPlayer().getUniqueId();
		TargetData	targetData = targets.get(uuid);
		if (targetData == null) return null;
		return targetData.getTarget();
	}

	public EntityModifierManager getEntityModifierManager() {
		return entityModifierManager;
	}

	// set scale of player
	public static void setScale(PlayerCustom playerCustom) {
		setScaleFromLivingEntity(playerCustom.getPlayer(), playerCustom.getRaceType(), playerCustom.getFormType());
	}

	// set scale of npc
	public static void setScale(NPC npc) {
		if (!(npc.getEntity() instanceof LivingEntity livingEntity)) return;
		TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
		setScaleFromLivingEntity(livingEntity, traitSentinel.getRaceType(), traitSentinel.getFormType());
	}

	// set scale of living entity
	private static void setScaleFromLivingEntity(LivingEntity livingEntity, RaceType raceType, FormType formType) {
		AttributeInstance	instance = livingEntity.getAttribute(Attribute.GENERIC_SCALE);
		if (instance != null)
			instance.setBaseValue(formType.getScale());
	}
}
