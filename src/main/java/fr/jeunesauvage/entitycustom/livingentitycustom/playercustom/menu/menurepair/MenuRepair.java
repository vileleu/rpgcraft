package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menurepair;

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
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuRepair extends MenuHolder {
    private static final int        REPAIR_SLOT = 8;
    private final Gold              price = new Gold();
    private final Gold              lastPrice = new Gold();

    public MenuRepair(PlayerCustom launcher) {
        super(launcher);
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Potion"));
        open();
    }

    @Override
    public void open() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        inventory.setItem(REPAIR_SLOT, createRepairSlot(Material.ANVIL, "Repair", "repair"));
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

    public void repair() {
        if (price.isEmpty()) return;
        if (!launcher.takeGoldInInventory(price)) {
            SoundManager.playSound(launcher, "error");
            return;
        }
        ItemCustomRegistry  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
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
            for (int i = 0; i < INVENTORY_SIZE; i++) {
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
}
