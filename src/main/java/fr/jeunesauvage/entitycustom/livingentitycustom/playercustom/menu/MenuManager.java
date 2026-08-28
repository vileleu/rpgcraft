package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public class MenuManager implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof MenuHolder) {
            e.setCancelled(true);
            ItemStack   clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            String  action = Menu.getAction(clicked);
            if (action == null) return;
            Player          p = (Player)e.getWhoClicked();
            PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
            if (playerCustom == null) return;
            Menu            menu = RpgCraft.getEntityCustomRegistry().getMenu(playerCustom);
            if (menu == null || !menu.isPresent()) {
                p.closeInventory();
                return;
            }
            ParseAction parseAction = new ParseAction(action);
            parseAction.parse();
            RpgCraft.debug("parse: result = " + parseAction.getResult() + ", start = " + parseAction.getStart());
            switch (parseAction.getResult()) {
                case "back_main" -> menu.openMainMenu();
                case "back_stats" -> menu.openStatsMenu();
                case "back_skills" -> menu.openSkillsMenu();
                case "back_class" -> menu.openClassMenu();
                case "back_spell" -> menu.openSpellMenu();
                case "back_items" -> menu.openItemsMenu();
                case "get_equipable" -> p.getInventory().addItem(RpgCraft.getItemCustomRegistry().getClone(clicked));
                case "get_spell" -> p.getInventory().addItem(RpgCraft.getItemCustomRegistry().getClone(clicked));
                // stats + skills
                case "open_stats" -> menu.openStatsMenu();
                case "open_skills" -> menu.openSkillsMenu();
                case "add_stat" -> menu.openStatsAddMenu();
                case "remove_stat" -> menu.openStatsRemoveMenu();
                case "add_skill" -> menu.openSkillsAddMenu();
                case "remove_skill" -> menu.openSkillsRemoveMenu();
                // race + class
                case "open_race" -> menu.openRaceMenu();
                case "open_class" -> menu.openClassMenu();
                case "change_race" -> menu.openRaceChangeMenu();
                case "change_class" -> menu.openClassChangeMenu();
                case "open_spell" -> menu.openSpellMenu();
                case "open_pyromancer" -> menu.openSpellPyromancerMenu();
                case "open_warrior" -> menu.openSpellWarriorMenu();
                case "open_rogue" -> menu.openSpellRogueMenu();
                case "open_priest" -> menu.openSpellPriestMenu();
                case "open_dracthyr" -> menu.openSpellDracthyrMenu();
                case "open_hunter" -> menu.openSpellHunterMenu();
                case "open_team" -> menu.openTeamMenu();
                case "add_team" -> menu.openTeamAddMenu();
                case "delete_team" -> menu.openTeamDeleteMenu();
                // npc
                case "get_placer_npc" -> RpgCraft.getNPCBuilderRegistry().createMyNPCPlacer(playerCustom);
                case "open_npc" -> menu.openNPCMenu();
                case "create_npc" -> menu.openCreateNPCMenu();
                case "patrol_npc" -> menu.openPatrolNPCMenu();
                case "aggro_npc" -> menu.openAggroNPCMenu();
                case "level_npc" -> menu.openLevelNPCMenu();
                case "chase_npc" -> menu.openChaseNPCMenu();
                case "boss_npc" -> menu.openBossNPCMenu();
                case "equip_npc" -> menu.openEquipNPCMenu();
                case "team_npc" -> menu.openTeamNPCMenu();
                case "drop_npc" -> menu.openDropNPCMenu();
                case "template_npc" -> menu.openTemplateNPCMenu();
                case "spawn_npc" -> menu.openSpawnNPCMenu();
                case "despawn_npc" -> menu.openDespawnNPCMenu();
                case "delete_npc" -> menu.openDeleteNPCMenu();
                // items
                case "open_items" -> menu.openItemsMenu();
                case "open_claw" -> menu.openClawsMenu(parseAction.getStart());
                case "open_sword" -> menu.openSwordsMenu(parseAction.getStart());
                case "open_axe" -> menu.openAxesMenu(parseAction.getStart());
                case "open_pickaxe" -> menu.openPickaxesMenu(parseAction.getStart());
                case "open_hoe" -> menu.openHoesMenu(parseAction.getStart());
                case "open_shovel" -> menu.openShovelsMenu(parseAction.getStart());
                case "open_mace" -> menu.openMacesMenu(parseAction.getStart());
                case "open_bow" -> menu.openBowsMenu(parseAction.getStart());
                case "open_crossbow" -> menu.openCrossbowsMenu(parseAction.getStart());
                case "open_staff" -> menu.openStaffsMenu(parseAction.getStart());
                case "open_spellbook" -> menu.openSpellbooksMenu(parseAction.getStart());
                case "open_shield" -> menu.openShieldsMenu(parseAction.getStart());
                case "open_head" -> menu.openHelmetsMenu(parseAction.getStart());
                case "open_chest" -> menu.openChestplatesMenu(parseAction.getStart());
                case "open_legs" -> menu.openLeggingsMenu(parseAction.getStart());
                case "open_feet" -> menu.openBootsMenu(parseAction.getStart());
                case "open_elytra" -> menu.openElytrasMenu(parseAction.getStart());
            }
        }
    }
}