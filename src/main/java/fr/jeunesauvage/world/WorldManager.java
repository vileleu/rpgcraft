package fr.jeunesauvage.world;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class WorldManager implements Listener {
	private static final Set<Material> INFINIBURN = EnumSet.of(
	    Material.NETHERRACK,
	    Material.MAGMA_BLOCK,
	    Material.SOUL_SAND,
	    Material.SOUL_SOIL
	);

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

	// cancel fire ignite
	@EventHandler
	public void onIgnite(BlockIgniteEvent e) {
    	Block	block = e.getBlock();
    	for (int x = -1; x <= 1; x++) {
    	    for (int y = -1; y <= 1; y++) {
    	        for (int z = -1; z <= 1; z++) {
    	            if (x == 0 && y == 0 && z == 0) continue;
    	            Block		relative = block.getRelative(x, y, z);
					Material	material = relative.getType();
    	            if (material.isBurnable() || INFINIBURN.contains(material)) {
    	                e.setCancelled(true);
    	                return;
    	            }
    	        }
    	    }
    	}
	}

	// remove projectilesz + display
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
    	for (Entity entity : e.getChunk().getEntities()) {
        	if (entity instanceof Projectile)
        	    entity.remove();
			else if (entity instanceof Display display)
				display.remove();
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
            if (traitSentinel.getRespawnTime() == -1 || traitSentinel.isPet())
                npc.destroy();
        }
    }
}
