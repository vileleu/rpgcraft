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

import fr.jeunesauvage.Pair;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.Gold;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.MenuHolderEquipable;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.ArmorMaterial;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuBuyArmor extends MenuHolderEquipable {

    public MenuBuyArmor(PlayerCustom launcher, int level, Rarity rarity) {
        super(launcher, level, rarity);
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Buy Armor"));
        getEquipables();
        open();
    }

    @Override 
    protected  void getEquipables() {
        clearEquipables();
        Collection<Armor>          armors = RpgCraft.getItemCustomRegistry().getArmorsDroppable().values();
        List<Pair<Armor, Gold>>    tmp = new ArrayList<>();
        for (Armor armor: armors) {
            if (isInRangeLevel(armor.getLevel()) && isSameOrLowerRarity(armor.getRarity()))
                tmp.add(new Pair<Armor,Gold>(armor, getPrice(armor)));
        }
        tmp.sort(Comparator.comparing((Pair<Armor, Gold> pair) -> pair.getFirst().getRarity())
            .thenComparingInt(pair -> pair.getFirst().getLevel()));
        for (Pair<Armor, Gold> pair: tmp) {
            Armor   armor = pair.getFirst();
            equipables.computeIfAbsent(armor.getType().getEquipableMaterial(), type -> new LinkedHashMap<>()).put(armor, pair.getSecond());
        }
    }

    protected void open(ArmorMaterial m) {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        Map<Equipable<?>, Gold> map = equipables.get(m);
        Armor                   last = null;
        int                     i = 9;
        for (Entry<Equipable<?>, Gold> e: map.entrySet()) {
            Armor   armor = (Armor)e.getKey();
            if (last != null && armor.getLevel() != last.getLevel()) i += (i % 9 != 0 ? 9 - i % 9 : 0);
            if (i >= inventory.getSize()) break;
            inventory.setItem(i++, createEquipableSlot(armor, e.getValue(), "buy"));
            last = armor;
        }
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        ItemStack   clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        String  action = getAction(clicked);
        if (action == null) return;
        switch (action) {
            case "open" -> open();
            case "open_cloth" -> open(ArmorMaterial.CLOTH);
            case "open_leather" -> open(ArmorMaterial.LEATHER);
            case "open_mail" -> open(ArmorMaterial.MAIL);
            case "open_plate" -> open(ArmorMaterial.PLATE);
            case "buy" -> {
                Armor armor = RpgCraft.getItemCustomRegistry().getArmor(clicked);
                buy(armor);
            }
            default -> {
                close();
            }
        }
    }

    public void buy(Armor armor) {
        Gold    price = equipables.get(armor.getType().getEquipableMaterial()).get(armor);
        if (!launcher.takeGoldInInventory(price)) {
            SoundManager.playSound(launcher, "error");
            return;
        }
        launcher.addItem(armor.getItemClone());
        SoundManager.playSound(launcher, "buy");
    }

    private Gold getPrice(Armor armor) {
        int nuggets = switch (armor.getType()) {
            case CLOTH_HEAD -> 5;
            case CLOTH_CHEST -> 8;
            case CLOTH_LEGS -> 6;
            case CLOTH_FEET -> 5;
            case LEATHER_HEAD -> 7;
            case LEATHER_CHEST -> 11;
            case LEATHER_LEGS -> 9;
            case LEATHER_FEET -> 7;
            case MAIL_HEAD -> 11;
            case MAIL_CHEST -> 16;
            case MAIL_LEGS -> 13;
            case MAIL_FEET -> 10;
            case PLATE_HEAD -> 16;
            case PLATE_CHEST -> 22;
            case PLATE_LEGS -> 20;
            case PLATE_FEET -> 15;
            case ELYTRA -> 0;
            case UNKNOWN -> 0;
        };
        Rarity  rarity = armor.getRarity();
        nuggets *= Math.pow(rarity.getNumber(), Math.max(1, rarity.getNumber() - 2));
        Gold    price = new Gold();
        price.fromNuggetsToGold(nuggets);
        return price;
    }
}
