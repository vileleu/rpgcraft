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
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.WeaponMaterial;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.sound.SoundManager;
import net.kyori.adventure.text.Component;

public class MenuBuyWeapon extends MenuHolderEquipable {
    public MenuBuyWeapon(PlayerCustom launcher, int level, Rarity rarity) {
        super(launcher, level, rarity);
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Buy Weapon"));
        getEquipables();
        open();
    }

    @Override 
    protected  void getEquipables() {
        clearEquipables();
        Collection<Weapon>          weapons = RpgCraft.getItemCustomRegistry().getWeaponsDroppable().values();
        List<Pair<Weapon, Gold>>    tmp = new ArrayList<>();
        for (Weapon weapon: weapons) {
            if (isInRangeLevel(weapon.getLevel()) && isSameOrLowerRarity(weapon.getRarity()))
                tmp.add(new Pair<Weapon,Gold>(weapon, getPrice(weapon)));
        }
        tmp.sort(Comparator.comparing((Pair<Weapon, Gold> pair) -> pair.getFirst().getRarity())
            .thenComparingInt(pair -> pair.getFirst().getLevel()));
        for (Pair<Weapon, Gold> pair: tmp) {
            Weapon   weapon = pair.getFirst();
            equipables.computeIfAbsent(weapon.getType().getEquipableMaterial(), type -> new LinkedHashMap<>()).put(weapon, pair.getSecond());
        }
    }

    protected void open(WeaponMaterial m) {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        Map<Equipable<?>, Gold> map = equipables.get(m);
        int                     i = 9;
        for (Entry<Equipable<?>, Gold> e: map.entrySet()) {
            Weapon   weapon = (Weapon)e.getKey();
            if (i >= inventory.getSize()) break;
            inventory.setItem(i++, createEquipableSlot(weapon, e.getValue(), "buy"));
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
            case "open_claw" -> open(WeaponMaterial.CLAW);
            case "open_sword" -> open(WeaponMaterial.SWORD);
            case "open_axe" -> open(WeaponMaterial.AXE);
            case "open_pickaxe" -> open(WeaponMaterial.PICKAXE);
            case "open_hoe" -> open(WeaponMaterial.HOE);
            case "open_shovel" -> open(WeaponMaterial.SHOVEL);
            case "open_trident" -> open(WeaponMaterial.TRIDENT);
            case "open_mace" -> open(WeaponMaterial.MACE);
            case "open_bow" -> open(WeaponMaterial.BOW);
            case "open_crossbow" -> open(WeaponMaterial.CROSSBOW);
            case "open_staff" -> open(WeaponMaterial.STAFF);
            case "open_spellbook" -> open(WeaponMaterial.SPELLBOOK);
            case "open_shield" -> open(WeaponMaterial.SHIELD);
            case "buy" -> {
                Weapon weapon = RpgCraft.getItemCustomRegistry().getWeapon(clicked);
                buy(weapon);
            }
            default -> {
                close();
            }
        }
    }

    public void buy(Weapon weapon) {
        Gold    price = equipables.get(weapon.getType().getEquipableMaterial()).get(weapon);
        if (!launcher.takeGoldInInventory(price)) {
            SoundManager.playSound(launcher, "error");
            return;
        }
        launcher.addItem(weapon.getItemClone());
        SoundManager.playSound(launcher, "buy");
    }

    private Gold getPrice(Weapon weapon) {
        int nuggets = switch (weapon.getType()) {
            case HAND -> 0;
            case CLAW -> 0;
            case SWORD -> 7;
            case AXE -> 9;
	        case PICKAXE -> 8;
	        case HOE, SHOVEL -> 6;
	        case TRIDENT -> 8;
            case MACE -> 10;
	        case BOW -> 8;
	        case CROSSBOW -> 9;
            case STAFF -> 10;
	        case SPELLBOOK -> 20;
            case SHIELD -> 10;
            case UNKNOWN -> 0;
        };
        Rarity  rarity = weapon.getRarity();
        nuggets *= Math.pow(rarity.getNumber(), Math.max(1, rarity.getNumber() - 2));
        Gold    price = new Gold();
        price.fromNuggetsToGold(nuggets);
        return price;
    }
}