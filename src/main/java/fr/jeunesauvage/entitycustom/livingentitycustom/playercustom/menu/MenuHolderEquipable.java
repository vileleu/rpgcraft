package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.ArmorMaterial;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;
import fr.jeunesauvage.itemcustom.equipable.WeaponMaterial;
import net.kyori.adventure.text.Component;

public abstract class MenuHolderEquipable extends MenuHolder {
    private static final int                                        RANGE_LEVEL = 5;
    private final int                                               level;
    private final Rarity                                            rarity;
    protected final Map<EquipableMaterial, Map<Equipable<?>, Gold>> equipables = new LinkedHashMap<>();

    protected MenuHolderEquipable(PlayerCustom launcher, int level, Rarity rarity) {
        super(launcher);
        this.level = (level < 1 ? 1 : (level > Rarity.LEVEL_MAX ? Rarity.LEVEL_MAX : level));
        this.rarity = rarity;
    }

    protected abstract void getEquipables();

    protected boolean isInRangeLevel(int l) {
        return (l >= level - RANGE_LEVEL && l <= level + RANGE_LEVEL);
    }

    protected boolean isSameOrLowerRarity(Rarity r) {
        return (r.getNumber() <= rarity.getNumber());
    }

    @Override
    public void open() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        int     i = 9;
        for (EquipableMaterial equipableMaterial: equipables.keySet()) {
            if (i >= inventory.getSize()) break;
            inventory.setItem(i++, createEquipableTypeSlot(equipableMaterial, "open_" + equipableMaterial.getName()));
        }
        launcher.openInventory(inventory);
    }

    @Override
    public void onClose() {
        RpgCraft.getEntityCustomRegistry().deleteMenu(this);
    }

    protected  ItemStack createEquipableTypeSlot(EquipableMaterial equipableMaterial, String action) {
        ItemStack   item;
        ItemMeta	meta;
        switch (equipableMaterial) {
            case ArmorMaterial armorMaterial -> {
                switch (armorMaterial) {
                    case CLOTH -> item = new ItemStack(Material.STRING);
                    case LEATHER -> item = new ItemStack(Material.LEATHER);
	                case MAIL -> item = new ItemStack(Material.CHAIN);
                    case UNKNOWN -> item = new ItemStack(Material.AIR);
                    case ELYTRA -> item = new ItemStack(Material.ELYTRA);
                    case PLATE -> item = new ItemStack(Material.ANVIL);
                    default -> item = new ItemStack(Material.AIR);
                }
                meta = item.getItemMeta();
            }
            case WeaponMaterial weaponMaterial -> {
                item = new ItemStack(weaponMaterial.getMaterial());
                meta = item.getItemMeta();
                switch (weaponMaterial) {
                    case CLAW -> meta.setCustomModelData(164);
                    case STAFF -> meta.setCustomModelData(74);
                    case SPELLBOOK -> meta.setCustomModelData(103);
                    default -> {}
                }
            }
        };
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, action);
        meta.lore(List.of(Lore.equipableMaterial(equipableMaterial)));
        item.setItemMeta(meta);
        return item;
    }

    protected ItemStack createEquipableSlot(Equipable<?> equipable, Gold price, String action) {
        ItemStack				item = equipable.getItemClone();
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, action);
        List<Component> lore = meta.lore();
        lore.addAll(Lore.gold(price.getIngots(), price.getNuggets()));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    protected void clearEquipables() {
        equipables.clear();
    }
}
