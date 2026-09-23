package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menurepair;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.Gold;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.MenuHolder;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuRepair implements MenuHolder {
    private static final int        REPAIR_SLOT = 8;
    private final PlayerCustom      launcher;
    private Inventory               inventory = null;
    private final Gold              price = new Gold();
    private final Gold              lastPrice = new Gold();

    public MenuRepair(PlayerCustom launcher) {
        this.launcher = launcher;
        open();
    }

    @Override
    public void open() {
        inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Repair"));
        inventory.setItem(BACK_SLOT, createBack("close"));
        inventory.setItem(REPAIR_SLOT, createRepairSlot(Material.ANVIL, "Repair", "repair"));
        launcher.openInventory(inventory);
    }

    @Override
    public void close() {
        launcher.closeInventory();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        ItemStack   current = e.getCurrentItem();
        ItemStack   cursor = e.getCursor();
        String      action = null;
        if ((current == null || current.getType() == Material.AIR) && (cursor == null || cursor.getType() == Material.AIR)) return;
        if (current != null && current.getType() != Material.AIR) action = MenuHolder.getAction(current);
        Player          p = (Player)e.getWhoClicked();
        PlayerCustom    playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (playerCustom == null) return;
        switch (action) {
            case null -> refreshRepair();
            case "repair" -> {
                e.setCancelled(true);
                repair();
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

    @Override 
    public void giveBackItems() {
        for (int i = 0; i < BIG_SLOT; i++) {
            if (i == BACK_SLOT || i == REPAIR_SLOT) continue;
            ItemStack   item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            launcher.addItem(item);
        }
    }

    public void repair() {
        if (price.isEmpty()) return;
        if (!takeGoldInInventory()) {
            SoundManager.playSound(launcher, "error");
            return;
        }
        ItemCustomRegistry  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
        for (int i = 0; i < BIG_SLOT; i++) {
            if (i == BACK_SLOT || i == REPAIR_SLOT) continue;
            ItemStack   item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            Equipable<?>   equipable = itemCustomRegistry.getEquipable(item);
            if (equipable == null || Equipable.getDamagePercent(item) == 0) continue;
            Equipable.repair(item);
        }
        SoundManager.playSound(launcher, "repair");
        refreshRepair();
    }

    public void refreshRepair() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            price.reset();
            ItemCustomRegistry  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
            for (int i = 0; i < BIG_SLOT; i++) {
                if (i == BACK_SLOT || i == REPAIR_SLOT) continue;
                ItemStack   item = inventory.getItem(i);
                if (item == null || item.getType() == Material.AIR) continue;
                Equipable<?>   equipable = itemCustomRegistry.getEquipable(item);
                if (equipable == null) continue;
                double  damagePercent = Equipable.getDamagePercent(item);
                if (damagePercent == 0) continue;
                switch (equipable.getRarity()) {
                    case POOR -> price.increaseNuggets((int)Math.ceil(3 * damagePercent));
                    case COMMON -> price.increaseNuggets((int)Math.ceil(6 * damagePercent));
                    case UNCOMMON -> price.increaseNuggets((int)Math.ceil(9 * damagePercent));
                    case RARE -> price.increaseIngots((int)Math.ceil(3 * damagePercent));
                    case EPIC -> price.increaseIngots((int)Math.ceil(8 * damagePercent));
                    case LEGENDARY -> price.increaseIngots((int)Math.ceil(20 * damagePercent));
                }
            }
            if (!price.equals(lastPrice)) {
                refreshRepairSlot(inventory.getItem(REPAIR_SLOT));
                SoundManager.playSound(launcher, "put");
                lastPrice.copy(price);
            }
        });
    }

    private boolean takeGoldInInventory() {
        Gold                        goldInInventory = new Gold();
        Set<Integer>                setIngots = new HashSet<>();
        Set<Integer>                setNuggets = new HashSet<>();
        PlayerInventory             inv = launcher.getInventory();
        ItemStack[]                 contents = inv.getContents();
        ItemStack                   item = null;
        // all inventory
        for (int i = 0; i < contents.length; i++) {
            item = contents[i];
            if (item == null) continue;
            else if (item.getType() == Material.GOLD_INGOT) {
                int amount = item.getAmount();
                setIngots.add(i);
                goldInInventory.increaseIngots(amount);
            }
            else if (item.getType() == Material.GOLD_NUGGET) {
                int amount = item.getAmount();
                setNuggets.add(i);
                goldInInventory.increaseNuggets(amount);
            }
        }
        // offhand
        item = inv.getItemInOffHand();
        if (item != null) {
            if (item.getType() == Material.GOLD_INGOT) {
                int amount = item.getAmount();
                setIngots.add(40);
                goldInInventory.increaseIngots(amount);
            }
            else if (item.getType() == Material.GOLD_NUGGET) {
                int amount = item.getAmount();
                setNuggets.add(40);
                goldInInventory.increaseNuggets(amount);
            }
        }
        if (!goldInInventory.isHigherOrEqual(price)) return false;
        int saveIngots = 0;
        int saveNuggets = 0;
        for (Integer i: setNuggets) {
            if (price.isEmpty()) break;
            item = inv.getItem(i);
            if (item == null || item.getType() != Material.GOLD_NUGGET) continue;
            if (price.haveNuggets()) {
                int decrease = Math.min(price.getNuggets(), item.getAmount());
                price.decreaseNuggets(decrease);
                goldInInventory.decreaseNuggets(decrease);
                int newAmount = item.getAmount() - decrease;
                if (newAmount > 0) item.setAmount(newAmount);
                else inv.setItem(i, item);
            }
            if (price.isEmpty()) break;
            item = inv.getItem(i);
            if (item == null || item.getType() != Material.GOLD_NUGGET) continue;
            if (price.haveIngots()) {
                int decrease = Math.min(price.getIngots() * 9 - saveNuggets, item.getAmount());
                saveNuggets += decrease;
                if (saveNuggets == price.getIngots() * 9)
                    price.decreaseIngots(saveNuggets / 9);
                goldInInventory.decreaseNuggets(decrease);
                int newAmount = item.getAmount() - decrease;
                if (newAmount > 0) item.setAmount(newAmount);
                else inv.setItem(i, item);
            }
        }
        for (Integer i: setIngots) {
            if (price.isEmpty()) break;
            item = inv.getItem(i);
            if (item == null || item.getType() != Material.GOLD_INGOT) continue;
            if (price.haveIngots()) {
                int decrease = Math.min(price.getIngots(), item.getAmount());
                price.decreaseIngots(decrease);
                goldInInventory.decreaseIngots(decrease);
                int newAmount = item.getAmount() - decrease;
                if (newAmount > 0) item.setAmount(newAmount);
                else inv.setItem(i, item);
            }
            if (price.isEmpty()) break;
            item = inv.getItem(i);
            if (item == null || item.getType() != Material.GOLD_INGOT) continue;
            if (price.haveNuggets()) {
                int decrease = Math.min((int)Math.ceil(price.getNuggets() / 9) - saveIngots, item.getAmount());
                saveIngots += decrease;
                int left = (price.getNuggets() % 9) != 0 ? 9 - price.getNuggets() % 9 : 0;
                if (saveIngots * 9 - left == price.getNuggets())
                    price.decreaseNuggets(saveIngots / 9);
                goldInInventory.decreaseIngots(decrease);
                int newAmount = item.getAmount() - decrease;
                if (newAmount > 0) item.setAmount(newAmount);
                else inv.setItem(i, item);
            }
        }
        return true;
    }

    private ItemStack createRepairSlot(Material mat, String name, String action) {
        ItemStack				item = new ItemStack(mat);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Message.c(Component.text(name)));
        Data.setString(pdc, KEY_MENU, action);
        meta.lore(Lore.gold(price.getIngots(), price.getNuggets()));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack refreshRepairSlot(ItemStack item) {
        ItemMeta    meta = item.getItemMeta();
        meta.lore(Lore.gold(price.getIngots(), price.getNuggets()));
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
}
