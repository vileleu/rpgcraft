package fr.jeunesauvage;

import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import fr.jeunesauvage.combat.CombatManager;
import fr.jeunesauvage.entitycustom.EntityCustomManager;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.metamorph.MetamorphRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.npcbuilder.NPCBuilderRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait.FightTrait;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.spell.EquipmentHidden;
import fr.jeunesauvage.itemcustom.spell.SpellRegistry;
import fr.jeunesauvage.sound.SoundManager;
import fr.jeunesauvage.sound.SoundPacket;
import fr.jeunesauvage.world.WorldManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;

public class RpgCraft extends JavaPlugin {
    private static final String     NAME = "rpgcraft";
    private static RpgCraft         INSTANCE = null;
    private static SkinsRestorer    INSTANCE_SKINSRESTORER = null;
    private static ProtocolManager  INSTANCE_PROTOCOLLIB = null;
    private static EquipmentHidden  INSTANCE_EQUIPMENTHIDDEN = null;
    private EntityCustomRegistry    entityCustomRegistry;
    private ItemCustomRegistry      itemCustomRegistry;
    private SpellRegistry           spellRegistry;
    private MetamorphRegistry       metamorphRegistry;
    private NPCBuilderRegistry      npcBuilderRegistry;

    @Override
    public void onEnable() {
        INSTANCE = this;
        INSTANCE_SKINSRESTORER = SkinsRestorerProvider.get();
        INSTANCE_PROTOCOLLIB = ProtocolLibrary.getProtocolManager();
		// load traitsentinel in citizens
    	CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(FightTrait.class).withName("fighttrait"));
        // protocolLib
        INSTANCE_EQUIPMENTHIDDEN = new EquipmentHidden();
        INSTANCE_PROTOCOLLIB.addPacketListener(new SoundPacket());
        INSTANCE_PROTOCOLLIB.addPacketListener(INSTANCE_EQUIPMENTHIDDEN);
        // entitycustom registry
        this.entityCustomRegistry = new EntityCustomRegistry();
        // itemcustom registry
        this.itemCustomRegistry = new ItemCustomRegistry();
        // spell registry
        this.spellRegistry = new SpellRegistry();
        // metamorph registry
        this.metamorphRegistry = new MetamorphRegistry();
        // npc builder registry
        this.npcBuilderRegistry = new NPCBuilderRegistry();
        // entity custom manager
        EntityCustomManager entityCustomManager = new EntityCustomManager();
        getServer().getPluginManager().registerEvents(entityCustomManager, this);
        // item custom manager
        ItemCustomManager   itemCustomManager = new ItemCustomManager();
        getServer().getPluginManager().registerEvents(itemCustomManager, this);
        // combat manager
        CombatManager       combatManager = new CombatManager();
        getServer().getPluginManager().registerEvents(combatManager, this);
        // world manager
        WorldManager worldManager = new WorldManager();
        getServer().getPluginManager().registerEvents(worldManager, this);
        // sound
        new SoundManager();
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

    // instance

    public static RpgCraft instance() {
        return INSTANCE;
    }

    public static SkinsRestorer instanceSkinsRestorer() {
        return INSTANCE_SKINSRESTORER;
    }

    public static ProtocolManager instanceProtocolLib() {
        return INSTANCE_PROTOCOLLIB;
    }

    // protocollib listener

    public static EquipmentHidden getInstancEquipmentHidden() {
        return INSTANCE_EQUIPMENTHIDDEN;
    }

    // registries

    public static EntityCustomRegistry getEntityCustomRegistry() {
        return INSTANCE.entityCustomRegistry;
    }

    public static ItemCustomRegistry getItemCustomRegistry() {
        return INSTANCE.itemCustomRegistry;
    }

    public static SpellRegistry getSpellRegistry() {
        return INSTANCE.spellRegistry;
    }

    public static MetamorphRegistry getMetamorphRegistry() {
        return INSTANCE.metamorphRegistry;
    }

    public static NPCBuilderRegistry getNPCBuilderRegistry() {
        return INSTANCE.npcBuilderRegistry;
    }

    public static void debug(String message) {
        if (INSTANCE == null) return;
        INSTANCE.getLogger().info(message);
    }
}
