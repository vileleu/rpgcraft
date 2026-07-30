package fr.jeunesauvage.entity.playercustom.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;

public class MenuManager implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof MenuHolder)) return;
        e.setCancelled(true);
        ItemStack   clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        String  action = Menu.getAction(clicked);
        if (action == null) return;
        Player          player = (Player)e.getWhoClicked();
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        switch (action) {
            case "back" -> player.openInventory(Menu.openMainMenu(playerCustom));
            case "open_stats" -> player.openInventory(Menu.openStatsMenu(playerCustom));
            case "open_skills" -> player.openInventory(Menu.openSkillsMenu(playerCustom));
            case "open_race" -> player.openInventory(Menu.openRaceMenu(playerCustom));
            case "open_class" -> player.openInventory(Menu.openClassMenu(playerCustom));
            case "open_items" -> player.openInventory(Menu.openItemsMenu(playerCustom));
            /*
            case "add_stat" -> 
            case "remove_stat" -> 
            case "add_skill" -> 
            case "remove_skill" -> 
            */
        }
    }

}