package fr.jeunesauvage.itemcustom.equipable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorManager;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponManager;

public class EquipableManager implements Listener {
	private final ItemCustomManager	itemCustomManager;
	private final ArmorManager		armorManager;
	private final WeaponManager		weaponManager;

	public EquipableManager(JavaPlugin plugin, ItemCustomManager itemCustomManager) {
		this.itemCustomManager = itemCustomManager;
		this.armorManager = new ArmorManager(this);
		plugin.getServer().getPluginManager().registerEvents(armorManager, plugin);
		this.weaponManager = new WeaponManager(plugin, itemCustomManager, this);
		plugin.getServer().getPluginManager().registerEvents(weaponManager, plugin);
	}

    // cancel craft of weapon/tool/armor
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
		CraftingInventory	inv = e.getInventory();
        ItemStack			result = inv.getResult();
        if (result == null) return;
        Material    material = result.getType();
        if (WeaponManager.isWeapon(material) || ArmorManager.isArmor(material))
			return;
		for (ItemStack ingredient : inv.getMatrix()) {
			if (ingredient == null || !ingredient.hasItemMeta()) continue;
			ItemCustom<?>	itemCustom = itemCustomManager.getItemCustom(ingredient);
			if (itemCustom == null) continue;
	        e.getInventory().setResult(null);
			return;
		}
    }

	// handle durability of Equipable
	@EventHandler
	public void onEquipableDamage(PlayerItemDamageEvent e) {
		Player	player = e.getPlayer();
		if (player.hasMetadata("NPC")) {
			e.setCancelled(true);
			return;
		}
		ItemStack		item = e.getItem();
		Equipable<?>	equipable = itemCustomManager.getEquipable(item);
		if (equipable == null) return;
		e.setDamage(getNewDurability(e.getDamage(), equipable));
	}

	// get durability of Equipable
	public int getNewDurability(int damage, Equipable<?> equipable) {
		int		level = equipable.getRarity().getNumber();
		int		levelMax = Rarity.LEVEL_MAX + 1;
		int		durabilityMax = equipable.getItem().getType().getMaxDurability();
		double	durabilityPercent = (levelMax - level) / 1000d;
		int		newDamage = Math.max(1, (int)(durabilityPercent * durabilityMax));
		/*
		level 1 = 0.6%
		level 2 = 0.5%
		level 3 = 0.4%
		level 4 = 0.3%
		level 5 = 0.2%
		level 6 = 0.1%
		*/
		return newDamage;
	}

	// drop Equipable
	@EventHandler
	public void onEquipableDrop(PlayerDropItemEvent e) {
	    Player			player = e.getPlayer();
	    Item			itemEntity = e.getItemDrop();
	    ItemStack 		item = itemEntity.getItemStack();
		Equipable<?>	equipable = itemCustomManager.getEquipable(item);
	    if (equipable == null) return;
		refreshEquipement(player);
	}

	// refresh Equipable
	public void refreshEquipement(Player player) {
		Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
			PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
			if (playerCustom == null) return;
			playerCustom.refreshEquipement();
		});
	}

	/*
	** getter + setter
	*/

	public ArmorManager getArmorManager() {
		return armorManager;
	}

	public WeaponManager getWeaponManager() {
		return weaponManager;
	}
}
