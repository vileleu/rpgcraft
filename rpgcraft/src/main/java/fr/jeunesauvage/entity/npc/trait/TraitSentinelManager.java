package fr.jeunesauvage.entity.npc.trait;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.npc.npcspell.NPCSpellManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;

public class TraitSentinelManager implements Listener {
	private final ItemCustomManager		itemCustomManager;
	private final NPCSpellManager		npcSpellManager;
	private BukkitTask					task = null;
	private final Map<Integer, Long>	respawnTimers = new HashMap<>();

	public TraitSentinelManager(ItemCustomManager itemCustomManager, NPCSpellManager npcSpellManager) {
		this.itemCustomManager = itemCustomManager;
		this.npcSpellManager = npcSpellManager;
	}

	@EventHandler
	public void onSpawn(NPCSpawnEvent e) {
		NPC				npc = e.getNPC();
		TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
		traitSentinel.init(itemCustomManager, npcSpellManager);
	}

	@EventHandler
	public void onDeath(NPCDeathEvent e) {
		NPC				npc = e.getNPC();
		TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
		int				respawnTime = traitSentinel.getRespawnTime();
		if (respawnTime == 0) return;
		else if (respawnTime == -1) {
			Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> npc.destroy(), 20);
			return;
		}
		long			respawnEnd = System.currentTimeMillis() + (respawnTime * 1000l);
		respawnTimers.put(npc.getId(), respawnEnd);
		if (task == null)
			task = starTask();
	}

	private BukkitTask starTask() {
		return Bukkit.getScheduler().runTaskTimer(RpgCraft.instance(), () -> {
		    long		now = System.currentTimeMillis();
			Iterator<Entry<Integer, Long>>	it = respawnTimers.entrySet().iterator();
		    while (it.hasNext()) {
				Entry<Integer, Long>	entry = it.next();
		        NPC	npc = CitizensAPI.getNPCRegistry().getById(entry.getKey());
		        if (npc == null) {
					it.remove();
					continue;
				}
		        if (now >= entry.getValue()) {
					Location	respawn = npc.getOrAddTrait(TraitSentinel.class).getRespawn();
					if (respawn != null)
		            	npc.spawn(respawn);
					else
						npc.spawn(npc.getStoredLocation());
		            it.remove();
		        }
		    }
			if (respawnTimers.isEmpty()) {
				task.cancel();
				task = null;
			}
		}, 20L, 20L);
	}
}
