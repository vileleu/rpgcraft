package fr.jeunesauvage.world;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class WorldManager implements Listener {
	public WorldManager(JavaPlugin plugin) {
        WorldCommand   worldCommand = new WorldCommand(this);
        plugin.getCommand("cleanentities").setExecutor(worldCommand);
	}

	// cancel vanilla boss bar
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		LivingEntity	livingEntity = e.getEntity();
		if (livingEntity instanceof Wither wither)
	    	wither.getBossBar().setVisible(false);
	}

	// cancel break block
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onBlockBreak(BlockBreakEvent e) {
		Player	player = e.getPlayer();
		if (player.getInventory().getItemInOffHand().getType() != Material.ANCIENT_DEBRIS)
	    	e.setCancelled(true);
	}

	// cancel place block
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player	player = e.getPlayer();
		if (player.getInventory().getItemInOffHand().getType() != Material.ANCIENT_DEBRIS)
	    	e.setCancelled(true);
	}

	// cancel fire spread
	@EventHandler
	public void onSpread(BlockSpreadEvent e) {
		Material	material = e.getSource().getType();
	    if (material == Material.FIRE || material == Material.SOUL_FIRE) e.setCancelled(true);
	}

	// cancel fire burn
	@EventHandler
	public void onBurn(BlockBurnEvent e) {
	    e.setCancelled(true);
	}

	// remove projectiles + tmp npc
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
    	for (Entity entity : e.getChunk().getEntities()) {
        	if (entity instanceof Projectile) {
        	    entity.remove();
        	}
        	else if (entity instanceof LivingEntity livingEntity) {
        	    NPC	npc = CitizensAPI.getNPCRegistry().getNPC(livingEntity);
				if (npc != null) {
					TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
					if (traitSentinel.getRespawnTime() == -1 ||  traitSentinel.isPet())
						npc.destroy();
				}
        	}
			else if (entity instanceof Display display) {
				display.remove();
    		}
    	}
	}

    public void cleanEntities() {
        for (World world: Bukkit.getWorlds()) {
            for (Entity entity: world.getEntities()) {
                if (entity instanceof Projectile)
                    entity.remove();
				else if (entity instanceof Display display) {
					display.remove();
    			}
            }
        }
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
            if (traitSentinel.getRespawnTime() == -1 ||  traitSentinel.isPet())
                npc.destroy();
        }
    }
}
