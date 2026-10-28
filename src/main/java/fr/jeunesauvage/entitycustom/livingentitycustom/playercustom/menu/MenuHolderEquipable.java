package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.Rarity;
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
        item = new ItemStack(equipableMaterial.getMaterial());
        meta = item.getItemMeta();
        if (equipableMaterial == WeaponMaterial.CLAW) meta.setItemModel(ItemCustomRegistry.DEFAULT_CLAW);
        else if (equipableMaterial == WeaponMaterial.STAFF) meta.setItemModel(ItemCustomRegistry.DEFAULT_STAFF);
        else if (equipableMaterial == WeaponMaterial.SPELLBOOK) meta.setItemModel(ItemCustomRegistry.DEFAULT_SPELLBOOK);
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
