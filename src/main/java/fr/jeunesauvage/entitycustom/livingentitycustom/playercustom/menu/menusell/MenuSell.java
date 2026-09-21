package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menusell;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.Menu;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuSell implements Menu {
    private static final int        SELL_SLOT = 8;
    private final MenuSellHolder    holder;
    private final PlayerCustom      launcher;
    int                             ingots = 0;
    int                             nuggets = 0;
    int                             lastIngots = 0;
    int                             lastNuggets = 0;

    public MenuSell(PlayerCustom launcher) {
        this.launcher = launcher;
        this.holder = new MenuSellHolder();
        open();
    }

    @Override 
    public void open() {
        Inventory       inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Sell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("close"));
        inv.setItem(SELL_SLOT, createSellSlot(Material.GOLD_INGOT, "Sell", "sell"));
        launcher.openInventory(inv);
    }

    @Override
    public void close() {
        launcher.closeInventory();
    }

    public void sell() {
        if (ingots == 0 && nuggets == 0) return;
        deleteItems();
        while (ingots > 0) {
            ItemStack   ingotItem = new ItemStack(Material.GOLD_INGOT);
            ingotItem.setAmount(Math.min(64, ingots));
            launcher.addItem(ingotItem);
            ingots -= 64;
        }
        while (nuggets > 0) {
            ItemStack   ingotItem = new ItemStack(Material.GOLD_NUGGET);
            ingotItem.setAmount(Math.min(64, nuggets));
            launcher.addItem(ingotItem);
            nuggets -= 64;
        }
        SoundManager.playSound(launcher, "sell");
        refreshSell();
    }

    public void refreshSell() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ingots = 0;
            nuggets = 0;
            Inventory           inv = holder.getInventory();
            ItemCustomRegistry  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
            for (int i = 0; i < BIG_SLOT; i++) {
                if (i == BACK_SLOT || i == SELL_SLOT) continue;
                ItemStack   item = inv.getItem(i);
                if (item == null || item.getType() == Material.AIR) continue;
                ItemCustom<?>   itemCustom = itemCustomRegistry.getItemCustom(item);
                if (itemCustom == null) continue;
                switch (itemCustom.getRarity()) {
                    case POOR -> nuggets += 3;
                    case COMMON -> nuggets += 6;
                    case UNCOMMON -> ingots += 1;
                    case RARE -> ingots += 4;
                    case EPIC -> ingots += 10;
                    case LEGENDARY -> ingots += 50;
                }
            }
            if (ingots != lastIngots || nuggets != lastNuggets) {
                refreshSellSlot(inv.getItem(SELL_SLOT));
                SoundManager.playSound(launcher, "put");
                lastIngots = ingots;
                lastNuggets = nuggets;
            }
        });
    }

    private ItemStack createSellSlot(Material mat, String name, String action) {
        ItemStack				item = new ItemStack(mat);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Message.c(Component.text(name)));
        Data.setString(pdc, KEY_MENU, action);
        meta.lore(Lore.sell(ingots, nuggets));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack refreshSellSlot(ItemStack item) {
        ItemMeta				meta = item.getItemMeta();
        meta.lore(Lore.sell(ingots, nuggets));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBack(String action) {
        ItemStack				item = new ItemStack(Material.ARROW);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Message.c(Component.text("Back")));
        Data.setString(pdc, KEY_MENU, action);
        item.setItemMeta(meta);
        return item;
    }

    private void deleteItems() {
        Inventory           inv = holder.getInventory();
        ItemCustomRegistry  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
        for (int i = 0; i < BIG_SLOT; i++) {
            if (i == BACK_SLOT || i == SELL_SLOT) continue;
            ItemStack   item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            ItemCustom<?>   itemCustom = itemCustomRegistry.getItemCustom(item);
            if (itemCustom == null) continue;
            inv.setItem(i, null);
        }
    }

    public void giveBack() {
        Inventory           inv = holder.getInventory();
        for (int i = 0; i < BIG_SLOT; i++) {
            if (i == BACK_SLOT || i == SELL_SLOT) continue;
            ItemStack   item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            launcher.addItem(item);
        }
    }
}
