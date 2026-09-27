package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menubuy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.Pair;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.Gold;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.MenuHolder;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuBuyPotion extends MenuHolder {
    private final Map<Potion, Gold> toSell = new LinkedHashMap<>();

    public MenuBuyPotion(PlayerCustom launcher) {
        super(launcher);
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Potion"));
        open();
    }

    private void getPotionsToSell() {
        toSell.clear();
        Collection<Potion>          potions = RpgCraft.getItemCustomRegistry().getPotions().values();
        List<Pair<Potion, Gold>>    tmp = new ArrayList<>();
        for (Potion potion: potions) {
            if (potion.getRarity() == Rarity.LEGENDARY) continue;
            tmp.add(new Pair<Potion,Gold>(potion, getPrice(potion)));
        }
        tmp.sort(Comparator.comparing((Pair<Potion, Gold> pair) -> pair.getFirst().getRarity())
            .thenComparingInt(pair -> pair.getFirst().getLevel()));
        for (Pair<Potion, Gold> pair: tmp) {
            toSell.put(pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    public void open() {
        getPotionsToSell();
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        int     i = 0;
        Potion  last = null;
        for (Entry<Potion, Gold> e: toSell.entrySet()) {
            if (i >= inventory.getSize()) break;
            Potion  potion = e.getKey();
            if (last != null && potion.getType() != last.getType()) i++;
            inventory.setItem(i++, createPotionSlot(potion, e.getValue(), "buy"));
            last = potion;
        }
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
            case "buy" -> {
                Potion potion = RpgCraft.getItemCustomRegistry().getPotion(clicked);
                buy(potion);
            }
            default -> {
                close();
            }
        }
    }

    @Override
    public void onClose() {
        giveBackItems();
        RpgCraft.getEntityCustomRegistry().deleteMenu(this);
    }

    public void buy(Potion potion) {
        if (!launcher.takeGoldInInventory(toSell.get(potion))) {
            SoundManager.playSound(launcher, "error");
            return;
        }
        launcher.addItem(potion.getItemClone());
        SoundManager.playSound(launcher, "buy");
    }

    private Gold getPrice(Potion potion) {
        int nuggets = switch (potion.getType()) {
            case POTION_HEALTH -> 5;
            case POTION_MANA -> 4;
            case POTION_RAGE -> 4;
	        case POTION_ENERGY -> 4;
        };
        Rarity  rarity = potion.getRarity();
        nuggets *= rarity.getNumber();
        Gold    price = new Gold();
        price.fromNuggetsToGold(nuggets);
        return price;
    }

    private ItemStack createPotionSlot(Potion potion, Gold price, String action) {
        ItemStack				item = potion.getItemClone();
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, action);
        List<Component> lore = meta.lore();
        lore .addAll(Lore.gold(price.getIngots(), price.getNuggets()));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
