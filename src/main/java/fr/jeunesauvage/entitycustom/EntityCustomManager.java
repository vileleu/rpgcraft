package fr.jeunesauvage.entitycustom;

import org.bukkit.event.Listener;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.group.GroupCommand;
import fr.jeunesauvage.entitycustom.livingentitycustom.metamorph.MetamorphManager;
import fr.jeunesauvage.entitycustom.livingentitycustom.mobcustom.MobCustomManager;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.NPCCustomManager;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.npcbuilder.NPCBuilderManager;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.PlayerCustomManager;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.MenuManager;

public class EntityCustomManager implements Listener {
    public EntityCustomManager() {
        RpgCraft    rpgCraft = RpgCraft.instance();
        // livingentitycustom managers
        PlayerCustomManager playerCustomManager = new PlayerCustomManager();
        rpgCraft.getServer().getPluginManager().registerEvents(playerCustomManager, rpgCraft);
        NPCCustomManager    npcCustomManager = new NPCCustomManager();
        rpgCraft.getServer().getPluginManager().registerEvents(npcCustomManager, rpgCraft);
        NPCBuilderManager    npcBuilderManager = new NPCBuilderManager();
        rpgCraft.getServer().getPluginManager().registerEvents(npcBuilderManager, rpgCraft);
        MobCustomManager    mobCustomManager = new MobCustomManager();
        rpgCraft.getServer().getPluginManager().registerEvents(mobCustomManager, rpgCraft);
        MetamorphManager    metamorphManager = new MetamorphManager();
        rpgCraft.getServer().getPluginManager().registerEvents(metamorphManager, rpgCraft);  
        MenuManager         menuManager = new MenuManager();
        rpgCraft.getServer().getPluginManager().registerEvents(menuManager, rpgCraft);
        // entity commands
        EntityCustomCommand entityCustomCommand = new EntityCustomCommand();
        rpgCraft.getCommand("menu").setExecutor(entityCustomCommand);
        // group commands
        GroupCommand	groupCommand = new GroupCommand();
        rpgCraft.getCommand("group").setExecutor(groupCommand);
        rpgCraft.getCommand("groupaccept").setExecutor(groupCommand);
        rpgCraft.getCommand("groupdecline").setExecutor(groupCommand);
        rpgCraft.getCommand("groupquit").setExecutor(groupCommand);
    }
}
