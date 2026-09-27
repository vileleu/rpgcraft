package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import net.kyori.adventure.text.Component;

public abstract class MenuHolder implements InventoryHolder {
	static public final NamespacedKey                               KEY_MENU = new NamespacedKey(RpgCraft.name(), "menuid");
	static public final int                                         BACK_SLOT = 53;
	static public final int                                         INVENTORY_SIZE = 54;
    protected final PlayerCustom                                    launcher;
    protected Inventory                                             inventory = null;

    protected MenuHolder(PlayerCustom launcher) {
        this.launcher = launcher;
    }

    protected abstract void open();

    protected void close() {
        launcher.closeInventory();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    protected abstract void onClick(InventoryClickEvent e);
    protected abstract void onClose();

    public void giveBackItems() {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i == BACK_SLOT) continue;
            ItemStack   item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            if (Data.hasString(item.getPersistentDataContainer(), KEY_MENU)) continue;
            launcher.addItem(item);
        }
    }

    protected String getAction(ItemStack item) {
        if (item == null) return null;
        ItemMeta                meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String  action = Data.getString(pdc, KEY_MENU);
        Data.remove(pdc, KEY_MENU);
        return action;
    }

    protected ItemStack createBack(String action) {
        ItemStack				item = new ItemStack(Material.ARROW);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Message.c(Component.text("Back")));
        Data.setString(pdc, KEY_MENU, action);
        item.setItemMeta(meta);
        return item;
    }

    protected void clearInventory() {
        inventory.clear();
    }
}
