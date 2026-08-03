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
            switch (action) {
                case "back_main" -> menu.openMainMenu();
                case "back_stats" -> menu.openStatsMenu();
                case "back_skills" -> menu.openSkillsMenu();
                case "back_class" -> menu.openClassMenu();
                case "back_spell" -> menu.openSpellMenu();
                case "back_items" -> menu.openItemsMenu();
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
                case "open_items" -> menu.openItemsMenu();
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
                case "print_claws" -> menu.openClawsMenu(itemCustomManager);
                case "print_swords" -> menu.openSwordsMenu(itemCustomManager);
                case "print_swords2" -> menu.openSwords2Menu(itemCustomManager);
                case "print_axes" -> menu.openAxesMenu(itemCustomManager);
                case "print_pickaxes" -> menu.openPickaxesMenu(itemCustomManager);
                case "print_hoes" -> menu.openHoesMenu(itemCustomManager);
                case "print_shovels" -> menu.openShovelsMenu(itemCustomManager);
                case "print_maces" -> menu.openMacesMenu(itemCustomManager);
                case "print_bows" -> menu.openBowsMenu(itemCustomManager);
                case "print_crossbows" -> menu.openCrossbowsMenu(itemCustomManager);
                case "print_staffs" -> menu.openStaffsMenu(itemCustomManager);
                case "print_spellbooks" -> menu.openSpellbooksMenu(itemCustomManager);
                case "print_shields" -> menu.openShieldsMenu(itemCustomManager);
                case "print_helmets" -> menu.openHelmetsMenu(itemCustomManager);
                case "print_helmets2" -> menu.openHelmets2Menu(itemCustomManager);
                case "print_helmets3" -> menu.openHelmets3Menu(itemCustomManager);
                case "print_helmets4" -> menu.openHelmets4Menu(itemCustomManager);
                case "print_chestplates" -> menu.openChestplatesMenu(itemCustomManager);
                case "print_chestplates2" -> menu.openChestplates2Menu(itemCustomManager);
                case "print_chestplates3" -> menu.openChestplates3Menu(itemCustomManager);
                case "print_leggings" -> menu.openLeggingsMenu(itemCustomManager);
                case "print_leggings2" -> menu.openLeggings2Menu(itemCustomManager);
                case "print_boots" -> menu.openBootsMenu(itemCustomManager);
                case "print_boots2" -> menu.openBoots2Menu(itemCustomManager);
                case "print_elytras" -> menu.openElytrasMenu(itemCustomManager);
            }
        }
    }
}