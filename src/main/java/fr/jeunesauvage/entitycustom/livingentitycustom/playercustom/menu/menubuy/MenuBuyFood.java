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
import fr.jeunesauvage.itemcustom.food.Food;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuBuyFood extends MenuHolder {
    private final Map<Food, Gold> toSell = new LinkedHashMap<>();

    public MenuBuyFood(PlayerCustom launcher) {
        super(launcher);
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Potion"));
        open();
    }

    private void getFoodsToSell() {
        toSell.clear();
        Collection<Food>          foods = RpgCraft.getItemCustomRegistry().getFoods().values();
        List<Pair<Food, Gold>>    tmp = new ArrayList<>();
        for (Food food: foods) {
            tmp.add(new Pair<Food,Gold>(food, getPrice(food)));
        }
        tmp.sort(Comparator.comparing(pair -> pair.getFirst().getType()));
        for (Pair<Food, Gold> pair: tmp) {
            toSell.put(pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    public void open() {
        getFoodsToSell();
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        int     i = 9;
        for (Entry<Food, Gold> e: toSell.entrySet()) {
            if (i >= inventory.getSize()) break;
            Food  food = e.getKey();
            inventory.setItem(i++, createFoodSlot(food, e.getValue(), "buy"));
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
                Food food = RpgCraft.getItemCustomRegistry().getFood(clicked);
                buy(food);
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

    public void buy(Food food) {
        if (!launcher.takeGoldInInventory(toSell.get(food))) {
            SoundManager.playSound(launcher, "error");
            return;
        }
        launcher.addItem(food.getItemClone());
        SoundManager.playSound(launcher, "buy");
    }

    private Gold getPrice(Food food) {
        int nuggets = switch (food.getType()) {
            case GOLDEN_CARROT -> 18;
            case RABBIT_STEW -> 16;
            case COOKED_BEEF -> 12;
            case COOKED_SALMON -> 12;
            case COOKED_CHICKEN -> 12;
            case MUSHROOM_STEW -> 8;
            case COOKED_RABBIT -> 8;
            case BAKED_POTATO -> 6;
            case CARROT -> 5;
            case APPLE -> 4;
            case POTATO -> 3;
            case BREAD -> 3;
            case SALMON -> 3;
            case COOKIE -> 2;
        };
        Gold    price = new Gold();
        price.fromNuggetsToGold(nuggets);
        return price;
    }

    private ItemStack createFoodSlot(Food food, Gold price, String action) {
        ItemStack				item = food.getItemClone();
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
