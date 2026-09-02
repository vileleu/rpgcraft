package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import net.citizensnpcs.api.event.NPCCreateEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;

public class NPCCustomManager implements Listener {
    @EventHandler
    public void onNPCCustomCreate(NPCCreateEvent e) {
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().createNPCCustom(e.getNPC());
        npcCustom.onJoin();
    }

    @EventHandler
    public void onNPCCustomSpawn(NPCSpawnEvent e) {
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(e.getNPC().getUniqueId());
        if (npcCustom == null) npcCustom = RpgCraft.getEntityCustomRegistry().createNPCCustom(e.getNPC());
        npcCustom.onSpawn();
    }

    @EventHandler
    public void onNPCCustomDespawn(NPCDespawnEvent e) {
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(e.getNPC().getUniqueId());
        if (npcCustom == null) return;
        npcCustom.onDeath();
    }

    @EventHandler
    public void onNPCCustomDeath(NPCDeathEvent e) {
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(e.getNPC().getUniqueId());
        if (npcCustom == null) return;
        npcCustom.onDeath();
    }

    @EventHandler
    public void onNPCCustomDelete(NPCRemoveEvent e) {
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(e.getNPC().getUniqueId());
        if (npcCustom == null) return;
        npcCustom.onQuit();
        RpgCraft.getEntityCustomRegistry().deleteEntityCustom(npcCustom);
    }

    // interactions

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onNPCClick(NPCRightClickEvent e) {
        PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(e.getClicker().getUniqueId());
        if (playerCustom == null) return;
        NPCCustom       npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(e.getNPC().getUniqueId());
        if (npcCustom == null) return;
        if (npcCustom.isFriend(playerCustom)) {
            npcCustom.greeting();
            return;
        }
        e.setCancelled(true);
    }
}
