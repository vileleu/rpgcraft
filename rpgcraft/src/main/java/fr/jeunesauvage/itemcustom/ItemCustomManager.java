package fr.jeunesauvage.itemcustom;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.itemcustom.consumable.Consumable;
import fr.jeunesauvage.itemcustom.consumable.ConsumableManager;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.EquipableManager;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorManager;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponManager;
import fr.jeunesauvage.itemcustom.itembuilder.ItemBuilder;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.potion.PotionManager;
import fr.jeunesauvage.itemcustom.spell.Spell;
import fr.jeunesauvage.itemcustom.spell.SpellManager;
import fr.jeunesauvage.itemcustom.usable.Usable;
import fr.jeunesauvage.itemcustom.usable.UsableManager;

public class ItemCustomManager implements Listener {
	private final EquipableManager				equipableManager;
	private final SpellManager					spellManager;
	private final PotionManager					potionManager;
	private final Map<String, ItemCustom<?>>	items;
	private final Map<String, Equipable<?>>		itemsEquipable;
	private final Map<String, Armor>			itemsArmor;
	private final Map<String, Weapon>			itemsWeapon;
	private final Map<String, Potion>			itemsPotion;
	private final Map<String, Spell>			itemsSpell;
	private final Map<String, Usable>			itemsUsable;
	private final Map<String, Consumable>		itemsConsumable;

	public ItemCustomManager(JavaPlugin plugin, EntityManager entityManager) {
		// command
        ItemCustomCommand   itemCustomCommand = new ItemCustomCommand(this);
        plugin.getCommand("giveequipable").setExecutor(itemCustomCommand);
        plugin.getCommand("givespell").setExecutor(itemCustomCommand);
        plugin.getCommand("givepotion").setExecutor(itemCustomCommand);
        plugin.getCommand("infositem").setExecutor(itemCustomCommand);
		// build all ItemCustom
		ItemBuilder	itemBuilder = new ItemBuilder();
		this.items = itemBuilder.getItems();
		this.itemsEquipable = itemBuilder.getEquipable();
		this.itemsArmor = itemBuilder.getArmor();
		this.itemsWeapon = itemBuilder.getWeapon();
		this.itemsPotion = itemBuilder.getPotion();
		this.itemsSpell = itemBuilder.getSpell();
		this.itemsUsable = itemBuilder.getUsable();
		this.itemsConsumable = itemBuilder.getConsumable();
		// EquipableManager listener
		this.equipableManager = new EquipableManager(plugin, this);
		plugin.getServer().getPluginManager().registerEvents(equipableManager, plugin);
		// SpellManager listener
		this.spellManager = new SpellManager(plugin, entityManager);
		plugin.getServer().getPluginManager().registerEvents(spellManager, plugin);
		// PotionManager listener
		this.potionManager = new PotionManager();
		plugin.getServer().getPluginManager().registerEvents(potionManager, plugin);
		// UsableManager listener
		UsableManager   usablebleManager = new UsableManager(this);
		plugin.getServer().getPluginManager().registerEvents(usablebleManager, plugin);
		// ConsumableManager listener
		ConsumableManager   consumablebleManager = new ConsumableManager(this);
		plugin.getServer().getPluginManager().registerEvents(consumablebleManager, plugin);
		// print all ItemCustom
		int	count = 1;
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			RpgCraft.debug("item " + count++ + ": " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("elytra"))
				RpgCraft.debug("elytra: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("sword"))
				RpgCraft.debug("sword: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("mace"))
				RpgCraft.debug("mace: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("axe"))
				RpgCraft.debug("axe: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("pickaxe"))
				RpgCraft.debug("pickaxe: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("shovel"))
				RpgCraft.debug("shovel: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("hoe"))
				RpgCraft.debug("hoe: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("bow"))
				RpgCraft.debug("bow: " + entry.getKey());
		}
		for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
			if (entry.getValue().getType().getName().equals("crossbow"))
				RpgCraft.debug("crossbow: " + entry.getKey());
		}
	}

	/*
	** getter + setter
	*/

	// get ItemCustom from Identifier

	public ItemCustom<?> getItemCustom(String identifier) {
		return items.get(identifier);
	}

	public Equipable<?> getEquipable(String identifier) {
		return itemsEquipable.get(identifier);
	}

	public Armor getArmor(String identifier) {
		return itemsArmor.get(identifier);
	}

	public Weapon getWeapon(String identifier) {
		return itemsWeapon.get(identifier);
	}

	public Potion getPotion(String identifier) {
		return itemsPotion.get(identifier);
	}

	public Spell getSpell(String identifier) {
		return itemsSpell.get(identifier);
	}

	public Usable getUsable(String identifier) {
		return itemsUsable.get(identifier);
	}

	public Consumable getConsumable(String identifier) {
		return itemsConsumable.get(identifier);
	}

	// get ItemCustom from ItemStack

	public ItemCustom<?> getItemCustom(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getItemCustom(identifier);
	}

	public Equipable<?> getEquipable(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getEquipable(identifier);
	}

	public Armor getArmor(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getArmor(identifier);
	}

	public Weapon getWeapon(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getWeapon(identifier);
	}

	public Potion getPotion(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getPotion(identifier);
	}

	public Spell getSpell(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getSpell(identifier);
	}

	public Usable getUsable(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getUsable(identifier);
	}

	public Consumable getConsumable(ItemStack item) {
		String	identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) return null;
		return getConsumable(identifier);
	}

	// get ItemStack clone from Identifier

	public ItemStack getEquipableClone(String identifier) {
		Equipable<?>	equipable = getEquipable(identifier);
		if (equipable == null) return null;
		return equipable.getItemClone();
	}

	public ItemStack getSpellClone(String identifier) {
		Spell	spell = getSpell(identifier);
		if (spell == null) return null;
		return spell.getItemClone();
	}

	public ItemStack getPotionClone(String identifier) {
		Potion	potion = getPotion(identifier);
		if (potion == null) return null;
		return potion.getItemClone();
	}

	// manager

	public EquipableManager getEquipableManager() {
		return equipableManager;
	}

	public ArmorManager getArmorManager() {
		return equipableManager.getArmorManager();
	}

	public WeaponManager getWeaponManager() {
		return equipableManager.getWeaponManager();
	}

	public SpellManager getSpellManager() {
		return spellManager;
	}

	public PotionManager getPotionManager() {
		return potionManager;
	}
}
