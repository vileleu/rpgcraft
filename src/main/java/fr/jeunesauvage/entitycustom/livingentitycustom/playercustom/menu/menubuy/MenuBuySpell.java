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
import fr.jeunesauvage.itemcustom.spell.Spell;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuBuySpell extends MenuHolder {
    private final Map<Spell, Gold> toSell = new LinkedHashMap<>();

    public MenuBuySpell(PlayerCustom launcher) {
        super(launcher);
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Spell"));
        open();
    }

    private void getSpellsToSell() {
        toSell.clear();
        Collection<Spell>          spells = RpgCraft.getItemCustomRegistry().getSpells().values();
        List<Pair<Spell, Gold>>    tmp = new ArrayList<>();
        for (Spell spell: spells) {
            if (!spell.getType().getClassTypes().contains(launcher.getClassType())) continue;
            tmp.add(new Pair<Spell,Gold>(spell, getPrice(spell)));
        }
        tmp.sort(Comparator.comparing((Pair<Spell, Gold> pair) -> pair.getFirst().getRarity())
            .thenComparingInt(pair -> pair.getFirst().getLevel()));
        for (Pair<Spell, Gold> pair: tmp) {
            toSell.put(pair.getFirst(), pair.getSecond());
        }
    }

    @Override
    public void open() {
        getSpellsToSell();
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        int     i = 0;
        Spell  last = null;
        for (Entry<Spell, Gold> e: toSell.entrySet()) {
            if (i >= inventory.getSize()) break;
            Spell  spell = e.getKey();
            if (last != null && spell.getType() != last.getType()) i++;
            inventory.setItem(i++, createSpellSlot(spell, e.getValue(), "buy"));
            last = spell;
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
                Spell spell = RpgCraft.getItemCustomRegistry().getSpell(clicked);
                buy(spell);
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

    public void buy(Spell spell) {
        if (!launcher.takeGoldInInventory(toSell.get(spell))) {
            SoundManager.playSound(launcher, "error");
            return;
        }
        launcher.addItem(spell.getItemClone());
        SoundManager.playSound(launcher, "buy");
    }

    private Gold getPrice(Spell spell) {
        int halfLevel = spell.getLevel() / 2;
        int nuggets = halfLevel + (halfLevel * spell.getRarity().getNumber());
        Gold    price = new Gold();
        price.fromNuggetsToGold(nuggets);
        return price;
    }

    private ItemStack createSpellSlot(Spell spell, Gold price, String action) {
        ItemStack				item = spell.getItemClone();
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
