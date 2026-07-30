package fr.jeunesauvage.entity.playercustom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceManager;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.team.Team;
import fr.jeunesauvage.entity.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.sound.QuoteType;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class PlayerCustomManager implements Listener {
	private static PlayerCustomManager		INSTANCE = null;
	private final ItemCustomManager			itemCustomManager;
	private final Map<UUID, PlayerCustom>	players = new HashMap<>();

	public PlayerCustomManager(JavaPlugin plugin, ItemCustomManager itemCustomManager, EntityManager entityManager) {
		INSTANCE = this;
		this.itemCustomManager = itemCustomManager;
        PlayerCustomCommand   playerCustomCommand = new PlayerCustomCommand(entityManager);
        plugin.getCommand("menu").setExecutor(playerCustomCommand);
        plugin.getCommand("addstat").setExecutor(playerCustomCommand);
        plugin.getCommand("removestat").setExecutor(playerCustomCommand);
        plugin.getCommand("addskill").setExecutor(playerCustomCommand);
        plugin.getCommand("removeskill").setExecutor(playerCustomCommand);
        plugin.getCommand("printmodifiers").setExecutor(playerCustomCommand);
        plugin.getCommand("print").setExecutor(playerCustomCommand);
        plugin.getCommand("printresources").setExecutor(playerCustomCommand);
        plugin.getCommand("printstats").setExecutor(playerCustomCommand);
        plugin.getCommand("printskills").setExecutor(playerCustomCommand);
        plugin.getCommand("team").setExecutor(playerCustomCommand);
        plugin.getCommand("race").setExecutor(playerCustomCommand);
        plugin.getCommand("form").setExecutor(playerCustomCommand);
        plugin.getCommand("class").setExecutor(playerCustomCommand);
	}

	// player join
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player	player = e.getPlayer();
		// remove all modifiers
		initPlayer(player);
		// create player custom
		PlayerCustom	playerCustom = new PlayerCustom(player, itemCustomManager);
		players.put(player.getUniqueId(), playerCustom);
		playerCustom.cleanDeath();
		// set team player
		Team.add(player, TeamType.PLAYER);
		// set scale
		EntityManager.setScale(playerCustom);
		// refresh stats
		playerCustom.refreshEquipement();
		// print keys
		printKeys(player);
	}

	// player quit
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player	player = e.getPlayer();
		PlayerCustom	playerCustom = players.remove(player.getUniqueId());
		playerCustom.cleanQuit();
	}

	// player death
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player	player = e.getPlayer();
		if (player.hasMetadata("NPC")) return;
		e.getDrops().clear();
		e.setKeepInventory(true);
	    e.setKeepLevel(true);
	    e.setDroppedExp(0);
		PlayerCustom	playerCustom = players.get(player.getUniqueId());
		SoundManager.playQuote(playerCustom, QuoteType.DEATH);
		playerCustom.cleanDeath();
	}

	// player respawn
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player	player = e.getPlayer();
		if (player.hasMetadata("NPC")) return;
		PlayerCustom	playerCustom = players.get(player.getUniqueId());
		playerCustom.refreshEquipement();
		playerCustom.refreshCooldown();
		playerCustom.getScoreboardCustom().refreshAll();
	}

	// player pickup experience
	@EventHandler
	public void onPickupExperience(PlayerPickupExperienceEvent e) {
		if (e.getPlayer().hasMetadata("NPC"))
			e.setCancelled(true);
	}

    // player change exp
    @EventHandler
	public void onExpChange(PlayerExpChangeEvent e) {
		Player			player = e.getPlayer();
		PlayerCustom	playerCustom = players.get(player.getUniqueId());
		ClassType		classType = playerCustom.getClassType();
		int				amount = e.getAmount();
		if (amount < 0)
			e.setAmount(0);
		else if (classType == ClassType.BEGGAR)
			e.setAmount(0);
		else {
			if (amount > 0 && player.getLevel() >= ResourceManager.LEVEL_MAX)
				e.setAmount(0);
		}
	}

	// player change level
	@EventHandler
	public void onLevel(PlayerLevelChangeEvent e) {
		Player		player = e.getPlayer();
		int			newLevel = e.getNewLevel();
		int			oldLevel = e.getOldLevel();
		if (newLevel > ResourceManager.LEVEL_MAX) {
			player.setLevel(oldLevel);
			return;
		}
		PlayerCustom	playerCustom = players.get(player.getUniqueId());
		ClassType		classType = playerCustom.getClassType();
		if (classType == ClassType.BEGGAR) {
			player.setExp(0);
			player.setLevel(0);
		}
		else {
			playerCustom.updateLevel(newLevel);
			if (newLevel == ResourceManager.LEVEL_MAX) {
				Component component = Component.text(player.getName())
					.append(Message.levelMax())
					.append(Component.text(ResourceManager.LEVEL_MAX));
				Bukkit.getServer().broadcast(component);
			}
		}
	}

	// player damage
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player player)) return;
		if (player.hasMetadata("NPC")) return;
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		playerCustom.getScoreboardCustom().refreshHealth();
	}

	// player heal
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onHeal(EntityRegainHealthEvent e) {
		if (!(e.getEntity() instanceof Player player)) return;
		if (player.hasMetadata("NPC")) return;
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		playerCustom.getScoreboardCustom().refreshHealth();
	}

	private void initPlayer(Player player) {
		// remove attribute vanilla
		for (Attribute attribute: Attribute.values()) {
			AttributeInstance				attributeInstance = player.getAttribute(attribute);
			if (attributeInstance == null) continue;
			Collection<AttributeModifier>	attributeModifiers = attributeInstance.getModifiers();
			for (AttributeModifier attributeModifier: Set.copyOf(attributeModifiers)) {
				RpgCraft.debug("modifier vanilla: " + attributeModifier.getKey().getKey());
				if (attributeModifier.getKey().getNamespace().equals(RpgCraft.name()))
					attributeInstance.removeModifier(attributeModifier);
			}
		}
		// set invisibility
        for (Player p: Bukkit.getOnlinePlayers()) {
            if (p == player) continue;
            if (p.isInvisible()) player.hidePlayer(RpgCraft.instance(), p);
            else player.showPlayer(RpgCraft.instance(), p);
        }
	}

	private void printKeys(Player player) {
		int	count = 1;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		RpgCraft.debug("keys of " + player.getName() + ":");
		for (NamespacedKey key: Set.copyOf(pdc.getKeys())) {				
			RpgCraft.debug("key " + count++ + ": " + key.getKey());
		}
	}

	/*
	** getter + setter
	*/

	public static PlayerCustom getPlayerCustom(Player player) {
		if (INSTANCE == null) return null;
		return INSTANCE.players.get(player.getUniqueId());
	}
}
