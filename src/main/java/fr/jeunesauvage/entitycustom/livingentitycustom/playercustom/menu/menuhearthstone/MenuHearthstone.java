package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menuhearthstone;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.MenuHolder;
import fr.jeunesauvage.itemcustom.spell.Spell;
import net.kyori.adventure.text.Component;

public class MenuHearthstone extends MenuHolder {
    private final Spell         spell;
    private final EquipmentSlot slot;

    public MenuHearthstone(PlayerCustom launcher, Spell spell, EquipmentSlot slot) {
        super(launcher);
        this.spell = spell;
        this.slot = slot;
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Command"));
        open();
    }

    @Override
    public void open() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        inventory.setItem(0, createHearthstoneSlot(Material.OAK_LOG, "Thunderbluff", "thunderbluff"));
        if (launcher.getAlly() != null)
            inventory.setItem(9, createHearthstoneSlot(Material.PLAYER_HEAD, "Ally", "ally"));
        launcher.openInventory(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        ItemStack   clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        String  action = getAction(clicked);
        if (action == null) return;
        switch (action) {
            case "thunderbluff", "ally" -> {
                RpgCraft.getSpellRegistry().choseHearthstone(launcher, action);
                spell.canUse(launcher, slot);
                close();
            }
            default -> close();
        }
    }

    @Override
    public void onClose() {
        RpgCraft.getEntityCustomRegistry().deleteMenu(this);
    }

    private ItemStack createHearthstoneSlot(Material mat, String name, String action) {
        ItemStack				item = new ItemStack(mat);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Message.c(Component.text(name)));
        Data.setString(pdc, KEY_MENU, action);
        item.setItemMeta(meta);
        return item;
    }
}
