package fr.jeunesauvage.entity.playercustom.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.print.Print;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class Menu {
	static public NamespacedKey	KEY_MENU = new NamespacedKey(RpgCraft.name(), "menuid");

    static public Inventory openMainMenu(PlayerCustom playerCustom) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu"));
        holder.setInventory(inv);
        inv.setItem(12, createSlot(playerCustom, Material.FLETCHING_TABLE, "Stats", "open_stats"));
        inv.setItem(13, createSlot(playerCustom, Material.CRAFTING_TABLE, "Skills", "open_skills"));
        inv.setItem(14, createSlot(playerCustom, Material.PLAYER_HEAD, "Race", "open_race"));
        inv.setItem(15, createSlot(playerCustom, Material.BLAZE_ROD, "Class", "open_class"));
        inv.setItem(16, createSlot(playerCustom, Material.IRON_SWORD, "Items", "open_items"));
        return inv;
    }

    static public Inventory openStatsMenu(PlayerCustom playerCustom) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Stats"));
        holder.setInventory(inv);
        inv.setItem(0, createBack());
        inv.setItem(12, createSlot(playerCustom, Material.GLOW_INK_SAC, "Print Primary", "print_stats_primary"));
        inv.setItem(13, createSlot(playerCustom, Material.INK_SAC, "Print Secondary", "print_stats_secondary"));
        inv.setItem(14, createSlot(playerCustom, Material.GREEN_DYE, "Add", "add_stat"));
        inv.setItem(15, createSlot(playerCustom, Material.RED_DYE, "Remove", "remove_stat"));
        return inv;
    }

    static public Inventory openSkillsMenu(PlayerCustom playerCustom) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Stats"));
        holder.setInventory(inv);
        inv.setItem(0, createBack());
        inv.setItem(12, createSlot(playerCustom, Material.GLOW_INK_SAC, "Print Primary", "print_skills_primary"));
        inv.setItem(13, createSlot(playerCustom, Material.INK_SAC, "Print Secondary", "print_skills_secondary"));
        inv.setItem(14, createSlot(playerCustom, Material.GREEN_DYE, "Add", "add_skill"));
        inv.setItem(15, createSlot(playerCustom, Material.RED_DYE, "Remove", "remove_skill"));
        return inv;
    }

    static public Inventory openRaceMenu(PlayerCustom playerCustom) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Race"));
        holder.setInventory(inv);
        inv.setItem(0, createBack());
        inv.setItem(12, createSlot(playerCustom, Material.LEATHER, "Print", "print_race"));
        return inv;
    }

    static public Inventory openClassMenu(PlayerCustom playerCustom) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Class"));
        holder.setInventory(inv);
        inv.setItem(0, createBack());
        inv.setItem(12, createSlot(playerCustom, Material.FLETCHING_TABLE, "Print", "print_class"));
        return inv;
    }

    static public Inventory openItemsMenu(PlayerCustom playerCustom) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Stats"));
        holder.setInventory(inv);
        inv.setItem(0, createBack());
        inv.setItem(10, createSlot(playerCustom, Material.IRON_SWORD, "Claw", "print_claws"));
        inv.setItem(11, createSlot(playerCustom, Material.IRON_SWORD, "Sword", "print_swords"));
        inv.setItem(12, createSlot(playerCustom, Material.IRON_AXE, "Axe", "print_axes"));
        inv.setItem(13, createSlot(playerCustom, Material.IRON_PICKAXE, "Pickaxe", "print_pickaxes"));
        inv.setItem(14, createSlot(playerCustom, Material.IRON_HOE, "Hoe", "print_hoes"));
        inv.setItem(15, createSlot(playerCustom, Material.IRON_SHOVEL, "Shovel", "print_shovels"));
        inv.setItem(16, createSlot(playerCustom, Material.MACE, "Mace", "print_maces"));
        inv.setItem(17, createSlot(playerCustom, Material.BOW, "Bow", "print_bows"));
        inv.setItem(18, createSlot(playerCustom, Material.CROSSBOW, "Crossbow", "print_crossbows"));
        inv.setItem(19, createSlot(playerCustom, Material.BOW, "Staff", "print_staffs"));
        inv.setItem(20, createSlot(playerCustom, Material.CROSSBOW, "Spellbook", "print_spellbooks"));
        inv.setItem(21, createSlot(playerCustom, Material.SHIELD, "Shield", "print_shields"));
        inv.setItem(22, createSlot(playerCustom, Material.IRON_HELMET, "Helmet", "print_helmets"));
        inv.setItem(23, createSlot(playerCustom, Material.IRON_CHESTPLATE, "Chestplate", "print_chestplates"));
        inv.setItem(24, createSlot(playerCustom, Material.IRON_LEGGINGS, "Leggings", "print_leggings"));
        inv.setItem(25, createSlot(playerCustom, Material.IRON_BOOTS, "Boots", "print_boots"));
        inv.setItem(26, createSlot(playerCustom, Material.ELYTRA, "Elytra", "print_elytra"));
        return inv;
    }

    static private ItemStack createSlot(PlayerCustom playerCustom, Material mat, String name, String action) {
        ItemStack				item = new ItemStack(mat);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Component.text(name).decoration(TextDecoration.BOLD, false));
        Data.setString(pdc, KEY_MENU, action);
        if (action.equals("print_stats_primary")) {
            Print   print = new Print(playerCustom.getPlayer());
            meta.lore(print.printStatPrimary());
        }
        else if (action.equals("print_stats_secondary")) {
            Print   print = new Print(playerCustom.getPlayer());
            meta.lore(print.printStatSecondary());
        }
        else if (action.equals("print_skills_primary")) {
            Print   print = new Print(playerCustom.getPlayer());
            meta.lore(print.printSkillPrimary());
        }
        else if (action.equals("print_skills_secondary")) {
            Print   print = new Print(playerCustom.getPlayer());
            meta.lore(print.printSkillSecondary());
        }
        else if (action.equals("print_claws"))
            meta.setCustomModelData(1);
        else if (name.equals("print_staffs"))
            meta.setCustomModelData(1);
        else if (name.equals("print_spellbooks"))
            meta.setCustomModelData(1);
        else if (name.equals("print_race"))
            meta.lore(Lore.raceType(playerCustom.getRaceType()));
        else if (name.equals("print_class"))
            meta.lore(Lore.classType(playerCustom.getClassType()));
        item.setItemMeta(meta);
        return item;
    }

    static private ItemStack createBack() {
        ItemStack				item = new ItemStack(Material.ARROW);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Component.text("Back").decoration(TextDecoration.BOLD, false));
        Data.setString(pdc, KEY_MENU, "back");
        item.setItemMeta(meta);
        return item;
    }

    static public String getAction(ItemStack item) {
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        return Data.getString(pdc, Menu.getKeyMenu());
    }

    static public NamespacedKey getKeyMenu() {
        return KEY_MENU;
    }
}
