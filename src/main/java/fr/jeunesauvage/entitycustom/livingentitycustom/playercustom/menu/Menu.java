package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;

public interface Menu {
	static public final NamespacedKey   KEY_MENU = new NamespacedKey(RpgCraft.name(), "menuid");
	static public final int             BACK_SLOT = 0;
	static public final int             SMALL_SLOT = 27;
	static public final int             BIG_SLOT = 54;

    void open();
    void close();

    static public String getAction(ItemStack item) {
        if (item == null) return null;
        ItemMeta                meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String  action = Data.getString(pdc, KEY_MENU);
        Data.remove(pdc, KEY_MENU);
        return action;
    }
}
