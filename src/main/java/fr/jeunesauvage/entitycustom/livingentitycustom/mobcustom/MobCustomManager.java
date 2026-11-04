package fr.jeunesauvage.entitycustom.livingentitycustom.mobcustom;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.MobCustom;
import net.citizensnpcs.api.CitizensAPI;

public class MobCustomManager implements Listener {
    @EventHandler
    public void onMobCustomSpawn(CreatureSpawnEvent e) {
        if (CitizensAPI.getNPCRegistry().isNPC(e.getEntity()) || !(e.getEntity() instanceof Mob mob)) return;
        if (mob instanceof Animals || (mob.getType() != EntityType.ZOMBIE && mob.getType() != EntityType.SKELETON)) e.setCancelled(true);
        MobCustom   mobCustom = RpgCraft.getEntityCustomRegistry().createMobCustom(mob);
        mobCustom.onSpawn();
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent e) {
        for (Entity entity : e.getEntities()) {
            if (CitizensAPI.getNPCRegistry().isNPC(entity) || !(entity instanceof Mob mob)) continue;
            MobCustom   mobCustom = RpgCraft.getEntityCustomRegistry().getMobCustom(mob.getUniqueId());
            if (mobCustom == null) mobCustom = RpgCraft.getEntityCustomRegistry().createMobCustom(mob);
            mobCustom.onSpawn();
        }
    }

    @EventHandler
    public void onMobCustomDeath(EntityDeathEvent e) {
        if (CitizensAPI.getNPCRegistry().isNPC(e.getEntity()) || !(e.getEntity() instanceof Mob mob)) return;
        e.getDrops().clear();
        e.setDroppedExp(0);
        MobCustom   mobCustom = RpgCraft.getEntityCustomRegistry().getMobCustom(mob.getUniqueId());
        if (mobCustom == null) return;
        mobCustom.onDeath();
        RpgCraft.getEntityCustomRegistry().deleteEntityCustom(mobCustom);
    }

    // cancel grief by mob
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        e.setCancelled(true);
    }
}
