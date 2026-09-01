package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.RayTraceResult;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;

public class PlayerCustomManager implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().createPlayerCustom(e.getPlayer());
        playerCustom.onJoin();
		printKeys(e.getPlayer());
	}

	private void printKeys(Player player) {
		int	count = 1;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		RpgCraft.debug("keys of " + player.getName() + ":");
		for (NamespacedKey key: Set.copyOf(pdc.getKeys())) {				
			RpgCraft.debug("key " + count++ + ": " + key.getKey());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(e.getPlayer().getUniqueId());
		if (playerCustom == null) return;
        playerCustom.onQuit();
        RpgCraft.getEntityCustomRegistry().deleteEntityCustom(playerCustom);
	}

	@EventHandler
	public void onPlayerSpawn(PlayerRespawnEvent e) {
		PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(e.getPlayer().getUniqueId());
		if (playerCustom == null) return;
        playerCustom.onSpawn();
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		e.getDrops().clear();
		e.setKeepInventory(true);
	    e.setKeepLevel(true);
	    e.setDroppedExp(0);
		PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(e.getPlayer().getUniqueId());
		if (playerCustom == null) return;
        playerCustom.onDeath();
	}

    // player change exp
    @EventHandler
	public void onExpChange(PlayerExpChangeEvent e) {
		Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		ClassType		classType = playerCustom.getClassType();
		int				amount = e.getAmount();
		if (amount < 0 || classType == ClassType.BEGGAR || (amount > 0 && p.getLevel() >= LivingEntityCustom.LEVEL_MAX)) e.setAmount(0);
	}

	// player change level
	@EventHandler
	public void onLevel(PlayerLevelChangeEvent e) {
		Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		int			newLevel = e.getNewLevel();
		if (newLevel < 1) {
			p.setLevel(1);
			return;
		}
		else if (newLevel > LivingEntityCustom.LEVEL_MAX) {
			p.setLevel(LivingEntityCustom.LEVEL_MAX);
			return;
		}
		ClassType		classType = playerCustom.getClassType();
		if (classType == ClassType.BEGGAR) {
			p.setExp(0);
			p.setLevel(1);
		}
		else if (newLevel == LivingEntityCustom.LEVEL_MAX) Bukkit.getServer().broadcast(Message.levelMax(playerCustom));
	}

	// player damage
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		playerCustom.getScoreboardCustom().refreshHealth(playerCustom);
	}

	// player heal
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHeal(EntityRegainHealthEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		playerCustom.getScoreboardCustom().refreshHealth(playerCustom);
	}

	// NPC player cancel pickup experience
	@EventHandler
	public void onPickupExperience(PlayerPickupExperienceEvent e) {
		if (e.getPlayer().hasMetadata("NPC")) e.setCancelled(true);
	}

	// catch target
	@EventHandler(ignoreCancelled = false)
	public void onClick(PlayerInteractEvent e) {
	    Player			p = e.getPlayer();
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (launcher == null) return;
		if (!launcher.isSneaking()) return;
		Action	action = e.getAction();
	    if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR && action != Action.PHYSICAL) return;
	    LivingEntityCustom	target = getTargetCustom(launcher, PlayerCustom.RANGETARGET_DEFAULT);
	    if (target == null) return;
		launcher.setTarget(target);
	}

	// get target with range
	private LivingEntityCustom getTargetCustom(PlayerCustom launcher, double range) {
		World	world = launcher.getWorld();
		if (world == null) return null;
		RayTraceResult	blockResult = world.rayTraceBlocks(
		        launcher.getEyeLocation(),
		        launcher.getLocation().getDirection(),
		        range,
		        FluidCollisionMode.NEVER,
		        true
		);
	    double			maxDistance = range;
		// if we hit block ajust range
	    if (blockResult != null && blockResult.getHitPosition() != null)
	        maxDistance = blockResult.getHitPosition().distance(launcher.getEyeLocation().toVector());
	    RayTraceResult	entityResult = world.rayTraceEntities(
	            launcher.getEyeLocation(),
	            launcher.getLocation().getDirection(),
	            maxDistance,
	            entity -> entity != launcher.getPlayer()
	    );
	    if (entityResult == null) return null;
	    Entity				e = entityResult.getHitEntity();
		LivingEntityCustom	target = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(e.getUniqueId());
		if (target == null) return null;
	    return target;
	}
}
