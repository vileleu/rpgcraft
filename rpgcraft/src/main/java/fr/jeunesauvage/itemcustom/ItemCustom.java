package fr.jeunesauvage.itemcustom;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import net.kyori.adventure.text.Component;

public abstract class ItemCustom<T extends ItemCustomType> {
	public static final NamespacedKey	KEY_IDENTIFIER = new NamespacedKey(RpgCraft.name(), "identifier");
	protected final ItemStack			item;
    protected final T					type;
	protected final String				name;
    protected final Rarity				rarity;
    protected final int					level;

    protected ItemCustom(T type, String name, Rarity rarity, int level) {
		this.item = new ItemStack(type.getMaterial());
        this.type = type;
		this.name = name;
		this.rarity = rarity;
		this.level = level;
    }

	public ItemStack getItem() {
		return item;
	}

	public ItemStack getItemClone() {
		return item.clone();
	}

    public T getType() {
		return type;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public int getLevel() {
		return level;
	}

    public abstract Component toComponent();

	public static String getIdentifier(ItemStack item) {
		if (item == null) return null;
		ItemMeta	meta = item.getItemMeta();
		if (meta == null) return null;
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
		return Data.getString(pdc, KEY_IDENTIFIER);
	}
}
