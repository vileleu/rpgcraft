package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menucommand.MenuCommand;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menucommand.MenuCommandHolder;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menusell.MenuSell;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menusell.MenuSellHolder;

public class MenuManager implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory   inventory = e.getInventory();
        if (inventory == null) return;
        InventoryHolder inventoryHolder = inventory.getHolder();
        if (inventoryHolder instanceof MenuSellHolder) {
            Player          p = (Player)e.getPlayer();
            PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
            if (playerCustom == null) return;
            if (!(RpgCraft.getEntityCustomRegistry().getMenu(playerCustom) instanceof MenuSell menu)) return;
            menu.giveBack();
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory   inventory = e.getInventory();
        if (inventory == null) return;
        InventoryHolder inventoryHolder = inventory.getHolder();
        // menu sell
        if (inventoryHolder instanceof MenuSellHolder) {
            ItemStack   current = e.getCurrentItem();
            ItemStack   cursor = e.getCursor();
            String      action = null;
            if ((current == null || current.getType() == Material.AIR) && (cursor == null || cursor.getType() == Material.AIR)) return;
            if (current != null && current.getType() != Material.AIR) action = Menu.getAction(current);
            Player          p = (Player)e.getWhoClicked();
            PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
            if (playerCustom == null) return;
            if (!(RpgCraft.getEntityCustomRegistry().getMenu(playerCustom) instanceof MenuSell menu)) {
                p.closeInventory();
                return;
            }
            switch (action) {
                case null -> menu.refreshSell();
                case "sell" -> {
                    e.setCancelled(true);
                    menu.sell();
                }
                default -> {
                    e.setCancelled(true);
                    menu.close();
                }
            }
        }
        // menu command
        else if (inventoryHolder instanceof MenuCommandHolder) {
            e.setCancelled(true);
            ItemStack   clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            String  action = Menu.getAction(clicked);
            if (action == null) return;
            Player          p = (Player)e.getWhoClicked();
            PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
            if (playerCustom == null) return;
            if (!(RpgCraft.getEntityCustomRegistry().getMenu(playerCustom) instanceof MenuCommand menu) || !menu.isPresent()) {
                p.closeInventory();
                return;
            }
            ParseAction parseAction = new ParseAction(action);
            parseAction.parse();
            switch (parseAction.getResult()) {
                case "close" -> menu.close();
                case "back_main" -> menu.open();
                case "back_stats" -> menu.openStats();
                case "back_skills" -> menu.openSkills();
                case "back_class" -> menu.openClass();
                case "back_spell" -> menu.openSpell();
                case "back_items" -> menu.openItems();
                case "back_potions" -> menu.openPotions();
                case "get" -> playerCustom.addItem(RpgCraft.getItemCustomRegistry().getClone(clicked));
                // stats + skills
                case "open_stats" -> menu.openStats();
                case "open_skills" -> menu.openSkills();
                case "add_stat" -> menu.openStatsAdd();
                case "remove_stat" -> menu.openStatsRemove();
                case "add_skill" -> menu.openSkillsAdd();
                case "remove_skill" -> menu.openSkillsRemove();
                // race + class
                case "open_race" -> menu.openRace();
                case "open_class" -> menu.openClass();
                case "change_race" -> menu.openRaceChange();
                case "change_class" -> menu.openClassChange();
                case "open_spell" -> menu.openSpell();
                case "open_pyromancer" -> menu.openSpellPyromancer();
                case "open_warrior" -> menu.openSpellWarrior();
                case "open_rogue" -> menu.openSpellRogue();
                case "open_priest" -> menu.openSpellPriest();
                case "open_dracthyr" -> menu.openSpellDracthyr();
                case "open_hunter" -> menu.openSpellHunter();
                case "open_team" -> menu.openTeam();
                case "add_team" -> menu.openTeamAdd();
                case "delete_team" -> menu.openTeamDelete();
                // npc
                case "get_placer_npc" -> RpgCraft.getNPCBuilderRegistry().createMyNPCPlacer(playerCustom);
                case "open_npc" -> menu.openNPC();
                case "create_npc" -> menu.openCreateNPC();
                case "patrol_npc" -> menu.openPatrolNPC();
                case "aggro_npc" -> menu.openAggroNPC();
                case "level_npc" -> menu.openLevelNPC();
                case "chase_npc" -> menu.openChaseNPC();
                case "boss_npc" -> menu.openBossNPC();
                case "equip_npc" -> menu.openEquipNPC();
                case "team_npc" -> menu.openTeamNPC();
                case "drop_npc" -> menu.openDropNPC();
                case "template_npc" -> menu.openTemplateNPC();
                case "spawn_npc" -> menu.openSpawnNPC();
                case "despawn_npc" -> menu.openDespawnNPC();
                case "delete_npc" -> menu.openDeleteNPC();
                // items
                case "open_items" -> menu.openItems();
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
                // potions
                case "open_potions" -> menu.openPotions();
                case "open_potion_health" -> menu.openPotionsHealth();
                case "open_potion_mana" -> menu.openPotionsMana();
                case "open_potion_rage" -> menu.openPotionsRage();
                case "open_potion_energy" -> menu.openPotionsEnergy();
                // foods
                case "open_foods" -> menu.openFoods();
                default -> menu.close();
            }
        }
    }
}