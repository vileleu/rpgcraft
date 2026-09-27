package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menusell;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.Gold;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.MenuHolder;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuSell extends MenuHolder {
    private static final int        SELL_SLOT = 8;
    private final Gold              gold = new Gold();
    private final Gold              lastGold = new Gold();

    public MenuSell(PlayerCustom launcher) {
        super(launcher);
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Potion"));
        open();
    }

    @Override
    public void open() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        inventory.setItem(SELL_SLOT, createSellSlot(Material.GOLD_INGOT, "Sell", "sell"));
        launcher.openInventory(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        ItemStack   current = e.getCurrentItem();
        ItemStack   cursor = e.getCursor();
        String      action = null;
        if ((current == null || current.getType() == Material.AIR) && (cursor == null || cursor.getType() == Material.AIR)) return;
        if (current != null && current.getType() != Material.AIR) action = getAction(current);
        Player          p = (Player)e.getWhoClicked();
        PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (playerCustom == null) return;
        switch (action) {
            case null -> refreshSell();
            case "sell" -> {
                e.setCancelled(true);
                sell();
            }
            default -> {
                e.setCancelled(true);
                close();
            }
        }
    }

    @Override
    public void onClose() {
        giveBackItems();
        RpgCraft.getEntityCustomRegistry().deleteMenu(this);
    }

    public void sell() {
        if (gold.isEmpty()) return;
        deleteItems();
        while (gold.haveIngots()) {
            ItemStack   ingotItem = new ItemStack(Material.GOLD_INGOT);
            ingotItem.setAmount(Math.min(64, gold.getIngots()));
            launcher.addItem(ingotItem);
            gold.decreaseIngots(64);
        }
        while (gold.haveNuggets()) {
            ItemStack   ingotItem = new ItemStack(Material.GOLD_NUGGET);
            ingotItem.setAmount(Math.min(64, gold.getNuggets()));
            launcher.addItem(ingotItem);
            gold.decreaseNuggets(64);
        }
        SoundManager.playSound(launcher, "sell");
        refreshSell();
    }

    public void refreshSell() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            gold.reset();
            ItemCustomRegistry  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
            for (int i = 0; i < INVENTORY_SIZE; i++) {
                if (i == BACK_SLOT || i == SELL_SLOT) continue;
                ItemStack   item = inventory.getItem(i);
                if (item == null || item.getType() == Material.AIR) continue;
                ItemCustom<?>   itemCustom = itemCustomRegistry.getItemCustom(item);
                if (itemCustom == null) continue;
                if (itemCustom instanceof Equipable) {
                    switch (itemCustom.getRarity()) {
                        case POOR -> gold.increaseNuggets(2);
                        case COMMON -> gold.increaseNuggets(5);
                        case UNCOMMON -> gold.increaseIngots(1);
                        case RARE -> gold.increaseIngots(4);
                        case EPIC -> gold.increaseIngots(10);
                        case LEGENDARY -> gold.increaseIngots(50);
                    }
                }
                else {
                    switch (itemCustom.getRarity()) {
                        case POOR -> gold.increaseNuggets(1);
                        case COMMON -> gold.increaseNuggets(2);
                        case UNCOMMON -> gold.increaseNuggets(4);
                        case RARE -> gold.increaseIngots(1);
                        case EPIC -> gold.increaseIngots(2);
                        case LEGENDARY -> gold.increaseIngots(5);
                    }
                }
            }
            if (!gold.equals(lastGold)) {
                refreshSellSlot(inventory.getItem(SELL_SLOT));
                SoundManager.playSound(launcher, "put");
                lastGold.copy(gold);
            }
        });
    }

    private ItemStack createSellSlot(Material mat, String name, String action) {
        ItemStack				item = new ItemStack(mat);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Message.c(Component.text(name)));
        Data.setString(pdc, KEY_MENU, action);
        meta.lore(Lore.gold(gold.getIngots(), gold.getNuggets()));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack refreshSellSlot(ItemStack item) {
        ItemMeta				meta = item.getItemMeta();
        meta.lore(Lore.gold(gold.getIngots(), gold.getNuggets()));
        item.setItemMeta(meta);
        return item;
    }

    private void deleteItems() {
        ItemCustomRegistry  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            if (i == BACK_SLOT || i == SELL_SLOT) continue;
            ItemStack   item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            ItemCustom<?>   itemCustom = itemCustomRegistry.getItemCustom(item);
            if (itemCustom == null) continue;
            inventory.setItem(i, null);
        }
    }
}
