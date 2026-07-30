package fr.jeunesauvage;

import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import fr.jeunesauvage.combat.CombatManager;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.group.GroupManager;
import fr.jeunesauvage.entity.npc.NPCManager;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.sound.SoundManager;
import fr.jeunesauvage.sound.SoundPacket;
import fr.jeunesauvage.world.WorldManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;

public class RpgCraft extends JavaPlugin {
    private static final String     NAME = "rpgcraft";
    private static JavaPlugin       INSTANCE = null;
    private static SkinsRestorer    INSTANCE_SKINSRESTORER = null;
    private ProtocolManager         protocolManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        INSTANCE_SKINSRESTORER = SkinsRestorerProvider.get();
		// load traitsentinel in citizens
    	CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TraitSentinel.class).withName("traitsentinel"));
        // protocolLib
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.protocolManager.addPacketListener(new SoundPacket(this));
        // world manager
        WorldManager worldManager = new WorldManager(this);
        getServer().getPluginManager().registerEvents(worldManager, this);
        // entity data
        EntityManager entityManager = new EntityManager(this);
        getServer().getPluginManager().registerEvents(entityManager, this);
        // item data
        ItemCustomManager itemCustomManager = new ItemCustomManager(this, entityManager);
        getServer().getPluginManager().registerEvents(itemCustomManager, this);
		// npc manager
        NPCManager	npcManager = new NPCManager(this, entityManager, itemCustomManager);
        getServer().getPluginManager().registerEvents(npcManager, this);
        // player data
        PlayerCustomManager playerCustomManager = new PlayerCustomManager(this, itemCustomManager, entityManager);
        getServer().getPluginManager().registerEvents(playerCustomManager, this);
        // combat
        CombatManager combatManager = new CombatManager(itemCustomManager, entityManager);
        getServer().getPluginManager().registerEvents(combatManager, this);
        // sound
        new SoundManager(this);
        // group
        new GroupManager(this);
        // plugin ON
        getLogger().info("RpgCraft ON");
    }

    @Override
    public void onDisable() {
        getLogger().info("RpgCraft OFF");
    }

    public static String name() {
        return NAME;
    }

    public static JavaPlugin instance() {
        return INSTANCE;
    }

    public static SkinsRestorer instanceSkinsRestorer() {
        return INSTANCE_SKINSRESTORER;
    }

    public static void debug(String message) {
        if (INSTANCE == null) return;
        INSTANCE.getLogger().info(message);
    }
}
