package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.npcbuilder;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public class NPCBuilderManager implements Listener {
	public NPCBuilderManager() {
		RpgCraft	rpgCraft = RpgCraft.instance();
		// command
        NPCBuilderCommand   npcBuilderCommand = new NPCBuilderCommand();
        rpgCraft.getCommand("placemynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("createmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("levelmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("patrolmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("aggromynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("chasemynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("bossmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("equipmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("teammynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("dropmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("templatemynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("spawnmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("despawnmynpc").setExecutor(npcBuilderCommand);
        rpgCraft.getCommand("deletemynpc").setExecutor(npcBuilderCommand);
	}

	// put a placer
	@EventHandler(ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent e) {
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(e.getPlayer().getUniqueId());
		if (launcher == null) return;
	    if (e.getBlock().getType() != Material.PLAYER_HEAD) return;
	    ItemStack	item = e.getItemInHand();
	    if (!(item.getItemMeta() instanceof SkullMeta meta)) return;
	    if (!Data.hasBoolean(meta.getPersistentDataContainer(), NPCBuilderRegistry.KEY_PLACER)) return;
		RpgCraft.getNPCBuilderRegistry().addNPCPlacer(launcher, e.getBlock().getLocation());
	}

	// break a placer
	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e) {
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(e.getPlayer().getUniqueId());
		if (launcher == null) return;
		if (e.getBlock().getType() != Material.PLAYER_HEAD) return;
    	RpgCraft.getNPCBuilderRegistry().deleteNPCPlacer(launcher, e.getBlock().getLocation());
	}
}
