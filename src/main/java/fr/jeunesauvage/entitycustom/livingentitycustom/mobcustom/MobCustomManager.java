package fr.jeunesauvage.entitycustom.livingentitycustom.mobcustom;

import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.MobCustom;

public class MobCustomManager implements Listener {
    @EventHandler
    public void onMobCustomSpawn(CreatureSpawnEvent e) {
        if (e.getEntity().hasMetadata("NPC") || !(e.getEntity() instanceof Mob mob)) return;
        MobCustom   mobCustom = RpgCraft.getEntityCustomRegistry().createMobCustom(mob);
        mobCustom.onSpawn();
    }

    @EventHandler
    public void onMobCustomDeath(EntityDeathEvent e) {
        if (e.getEntity().hasMetadata("NPC") || !(e.getEntity() instanceof Mob mob)) return;
        MobCustom   mobCustom = RpgCraft.getEntityCustomRegistry().createMobCustom(mob);
        if (mobCustom == null) return;
        mobCustom.onDeath();
        RpgCraft.getEntityCustomRegistry().deleteEntityCustom(mobCustom);
    }
}
