package fr.jeunesauvage.entity.playercustom.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomManager;

public class MenuManager implements Listener {
    private final ItemCustomManager itemCustomManager;
    private final Map<UUID, Menu>   menuMap = new HashMap<>();

    public MenuManager(ItemCustomManager itemCustomManager) {
        this.itemCustomManager = itemCustomManager;
    }

    public void addMenu(PlayerCustom sender, Menu menu) {
        menuMap.put(sender.getPlayer().getUniqueId(), menu);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof MenuHolder) {
            e.setCancelled(true);
            ItemStack   clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            String  action = Menu.getAction(clicked);
            if (action == null) return;
            Player          p = (Player)e.getWhoClicked();
            Menu            menu = menuMap.get(p.getUniqueId());
            if (menu == null || menu.isOffline()) {
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
                case "back_items" -> menu.openItemsMenu(itemCustomManager);
                case "get_equipable" -> p.getInventory().addItem(itemCustomManager.getEquipableClone(ItemCustom.getIdentifier(clicked)));
                case "get_spell" -> p.getInventory().addItem(itemCustomManager.getSpellClone(ItemCustom.getIdentifier(clicked)));
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
                case "open_items" -> menu.openItemsMenu(itemCustomManager);
                case "change_race" -> menu.openRaceChangeMenu();
                case "change_class" -> menu.openClassChangeMenu();
                case "print_spell" -> menu.openSpellMenu();
                case "print_pyromancer" -> menu.openSpellPyromancerMenu(itemCustomManager);
                case "print_warrior" -> menu.openSpellWarriorMenu(itemCustomManager);
                case "print_rogue" -> menu.openSpellRogueMenu(itemCustomManager);
                case "print_priest" -> menu.openSpellPriestMenu(itemCustomManager);
                case "print_dracthyr" -> menu.openSpellDracthyrMenu(itemCustomManager);
                case "print_hunter" -> menu.openSpellHunterMenu(itemCustomManager);
                // items
                case "print_claw" -> menu.openClawsMenu(itemCustomManager, parseAction.getStart());
                case "print_sword" -> menu.openSwordsMenu(itemCustomManager, parseAction.getStart());
                case "print_axe" -> menu.openAxesMenu(itemCustomManager, parseAction.getStart());
                case "print_pickaxe" -> menu.openPickaxesMenu(itemCustomManager, parseAction.getStart());
                case "print_hoe" -> menu.openHoesMenu(itemCustomManager, parseAction.getStart());
                case "print_shovel" -> menu.openShovelsMenu(itemCustomManager, parseAction.getStart());
                case "print_mace" -> menu.openMacesMenu(itemCustomManager, parseAction.getStart());
                case "print_bow" -> menu.openBowsMenu(itemCustomManager, parseAction.getStart());
                case "print_crossbow" -> menu.openCrossbowsMenu(itemCustomManager, parseAction.getStart());
                case "print_staff" -> menu.openStaffsMenu(itemCustomManager, parseAction.getStart());
                case "print_spellbook" -> menu.openSpellbooksMenu(itemCustomManager, parseAction.getStart());
                case "print_shield" -> menu.openShieldsMenu(itemCustomManager, parseAction.getStart());
                case "print_head" -> menu.openHelmetsMenu(itemCustomManager, parseAction.getStart());
                case "print_chest" -> menu.openChestplatesMenu(itemCustomManager, parseAction.getStart());
                case "print_legs" -> menu.openLeggingsMenu(itemCustomManager, parseAction.getStart());
                case "print_feet" -> menu.openBootsMenu(itemCustomManager, parseAction.getStart());
                case "print_elytra" -> menu.openElytrasMenu(itemCustomManager, parseAction.getStart());
            }
        }
    }
}