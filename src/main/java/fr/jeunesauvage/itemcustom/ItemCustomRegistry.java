package fr.jeunesauvage.itemcustom;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.itemcustom.consumable.Consumable;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorType;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.itembuilder.ItemBuilder;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.spell.Spell;
import fr.jeunesauvage.itemcustom.usable.Usable;

public class ItemCustomRegistry implements Iterable<ItemCustom<?>> {
	private final Map<String, ItemCustom<?>>	itemCustoms;
	private final Map<String, Equipable<?>>		equipables;
	private final Map<String, Armor>			armors;
	private final Map<String, Weapon>			weapons;
	private final Map<String, Potion>			potions;
	private final Map<String, Spell>			spells;
	private final Map<String, Usable>			usables;
	private final Map<String, Consumable>		consumables;

    public ItemCustomRegistry() {
		ItemBuilder itemBuilder = new ItemBuilder();
		this.itemCustoms = itemBuilder.getItems();
		this.equipables = itemBuilder.getEquipable();
		this.armors = itemBuilder.getArmor();
		this.weapons = itemBuilder.getWeapon();
		this.potions = itemBuilder.getPotion();
		this.spells = itemBuilder.getSpell();
		this.usables = itemBuilder.getUsable();
		this.consumables = itemBuilder.getConsumable();
		for (Equipable<?>	equipable: equipables.values()) {
			if (equipable.getType() == ArmorType.ELYTRA)
				RpgCraft.debug(equipable.getIdentifier());
		}
    }

    @Override
    public Iterator<ItemCustom<?>> iterator() {
        return itemCustoms.values().iterator();
    }

	public ItemStack getClone(ItemStack item) {
		ItemCustom<?>	itemCustom = getItemCustom(item);
		if (itemCustom == null) return null;
		return itemCustom.getItemClone();
	}

    // get Map

	public Map<String, Equipable<?>> getEquipables() {
		return equipables;
	}

    public Map<String, Armor> getArmors() {
        return armors;
    }

    public Map<String, Weapon> getWeapons() {
        return weapons;
    }

    public Map<String, Potion> getPotions() {
        return potions;
    }

	public Map<String, Spell> getSpells() {
		return spells;
	}

    public Map<String, Usable> getUsables() {
        return usables;
    }

    public Map<String, Consumable> getConsumables() {
        return consumables;
    }

	// get ItemCustom from Identifier

	public ItemCustom<?> getItemCustom(String identifier) {
		return itemCustoms.get(identifier);
	}

	public Equipable<?> getEquipable(String identifier) {
		return equipables.get(identifier);
	}

	public Armor getArmor(String identifier) {
		return armors.get(identifier);
	}

	public Weapon getWeapon(String identifier) {
		return weapons.get(identifier);
	}

	public Potion getPotion(String identifier) {
		return potions.get(identifier);
	}

	public Spell getSpell(String identifier) {
		return spells.get(identifier);
	}

	public Usable getUsable(String identifier) {
		return usables.get(identifier);
	}

	public Consumable getConsumable(String identifier) {
		return consumables.get(identifier);
	}

	// get ItemCustom from Itemstack

	public ItemCustom<?> getItemCustom(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return itemCustoms.get(identifier);
	}

	public Equipable<?> getEquipable(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return equipables.get(identifier);
	}

	public Armor getArmor(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return armors.get(identifier);
	}

	public Weapon getWeapon(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return weapons.get(identifier);
	}

	public Potion getPotion(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return potions.get(identifier);
	}

	public Spell getSpell(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return spells.get(identifier);
	}

	public Usable getUsable(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return usables.get(identifier);
	}

	public Consumable getConsumable(ItemStack item) {
        String  identifier = ItemCustom.getIdentifier(item);
        if (identifier == null) return null;
		return consumables.get(identifier);
	}
}
