package fr.jeunesauvage.entitycustom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.MobCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.Menu;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class EntityCustomRegistry implements Iterable<EntityCustom> {
	private final Map<UUID, EntityCustom>       entitiesCustom = new HashMap<>();
	private final Map<UUID, LivingEntityCustom> livingEntitiesCustom = new HashMap<>();
	private final Map<UUID, PlayerCustom>       playersCustom = new HashMap<>();
	private final Map<UUID, NPCCustom>			npcsCustom = new HashMap<>();
	private final Map<UUID, MobCustom>          mobsCustom = new HashMap<>();
    private final Map<UUID, Menu>   menuMap = new HashMap<>();

    @Override
    public Iterator<EntityCustom> iterator() {
        return entitiesCustom.values().iterator();
    }

    // get Set

    public Set<PlayerCustom> getPlayerCustoms() {
        return new HashSet<>(playersCustom.values());
    }

    // get

    public EntityCustom getEntityCustom(UUID uuid) {
        return entitiesCustom.get(uuid);
    }

    public LivingEntityCustom getLivingEntityCustom(UUID uuid) {
        LivingEntityCustom  livingEntityCustom = livingEntitiesCustom.get(uuid);
        if (livingEntityCustom == null) {
            Entity  entity = Bukkit.getEntity(uuid);
            if (entity == null) return null;
            NPC     npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            if (npc == null) return null;
            livingEntityCustom = livingEntitiesCustom.get(npc.getUniqueId());
        }
        return livingEntityCustom;
    }

    public PlayerCustom getPlayerCustom(UUID uuid) {
        return playersCustom.get(uuid);
    }

    public NPCCustom getNPCCustom(UUID uuid) {
        return npcsCustom.get(uuid);
    }

    public MobCustom getMobCustom(UUID uuid) {
        return mobsCustom.get(uuid);
    }

    // create

    public PlayerCustom createPlayerCustom(Player player) {
        if (player == null) return null;
        PlayerCustom    playerCustom = new PlayerCustom(player);
        register(playerCustom);
        return playerCustom;
    }

    public NPCCustom createNPCCustom(NPC npc) {
        if (npc == null) return null;
        NPCCustom   npcCustom = new NPCCustom(npc);
        register(npcCustom);
        return npcCustom;
    }

    public MobCustom createMobCustom(Mob mob) {
        if (mob == null) return null;
        MobCustom   mobCustom = new MobCustom(mob);
        register(mobCustom);
        return mobCustom;
    }

    // delete

    public void deleteEntityCustom(UUID uuid) {
        if (uuid == null) return;
        unregister(uuid);
    }

    public void deleteEntityCustom(EntityCustom entityCustom) {
        if (entityCustom == null) return;
        unregister(entityCustom);
    }

    // register/unregister

    private void register(EntityCustom entityCustom) {
        entitiesCustom.put(entityCustom.getUUID(), entityCustom);
        if (entityCustom instanceof LivingEntityCustom l) livingEntitiesCustom.put(entityCustom.getUUID(), l);
        if (entityCustom instanceof PlayerCustom p) playersCustom.put(entityCustom.getUUID(), p);
        if (entityCustom instanceof NPCCustom n) npcsCustom.put(entityCustom.getUUID(), n);
        if (entityCustom instanceof MobCustom m) mobsCustom.put(entityCustom.getUUID(), m);
    }

    private void unregister(EntityCustom entityCustom) {
        entitiesCustom.remove(entityCustom.getUUID(), entityCustom);
        if (entityCustom instanceof LivingEntityCustom l) livingEntitiesCustom.remove(entityCustom.getUUID(), l);
        if (entityCustom instanceof PlayerCustom p) playersCustom.remove(entityCustom.getUUID(), p);
        if (entityCustom instanceof NPCCustom n) npcsCustom.remove(entityCustom.getUUID(), n);
        if (entityCustom instanceof MobCustom m) mobsCustom.remove(entityCustom.getUUID(), m);
    }

    private void unregister(UUID uuid) {
        EntityCustom    entityCustom = entitiesCustom.get(uuid);
        if (entityCustom == null) return;
        unregister(entityCustom);
    }

    // menu

    public void addMenu(PlayerCustom launcher, LivingEntityCustom target) {
        if (launcher == null || target == null) return;
        Menu    menu = new Menu(launcher, target);
        menuMap.put(launcher.getPlayer().getUniqueId(), menu);
    }

    public Menu getMenu(PlayerCustom launcher) {
        if (launcher == null) return null;
        return menuMap.get(launcher.getUUID());
    }
}
