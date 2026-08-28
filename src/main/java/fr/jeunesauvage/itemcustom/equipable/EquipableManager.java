package fr.jeunesauvage.itemcustom.equipable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorManager;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponManager;

public class EquipableManager implements Listener {
	public EquipableManager() {
		RpgCraft	rpgCraft = RpgCraft.instance();
		ArmorManager	armorManager = new ArmorManager();
		rpgCraft.getServer().getPluginManager().registerEvents(armorManager, rpgCraft);
		WeaponManager	weaponManager = new WeaponManager();
		rpgCraft.getServer().getPluginManager().registerEvents(weaponManager, rpgCraft);
	}

    // cancel craft of weapon/tool/armor
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
		CraftingInventory	inv = e.getInventory();
        ItemStack			result = inv.getResult();
		if (result != null) {
			ItemCustom<?>	itemCustom = RpgCraft.getItemCustomRegistry().getItemCustom(result);
			if (itemCustom != null) {
	    		inv.setResult(null);
				return;
			}
		}
		for (ItemStack ingredient : inv.getMatrix()) {
			if (ingredient == null) continue;
			ItemCustom<?>	itemCustom = RpgCraft.getItemCustomRegistry().getItemCustom(ingredient);
			if (itemCustom == null) continue;
	        inv.setResult(null);
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
		Equipable<?>	equipable = RpgCraft.getItemCustomRegistry().getEquipable(item);
		if (equipable == null) return;
		e.setDamage(getNewDurability(equipable));
	}

	// drop Equipable
	@EventHandler
	public void onEquipableDrop(PlayerDropItemEvent e) {
	    Player			player = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(player.getUniqueId());
		if (playerCustom == null) return;
	    ItemStack 		item = e.getItemDrop().getItemStack();
		Equipable<?>	equipable = RpgCraft.getItemCustomRegistry().getEquipable(item);
	    if (equipable == null) return;
		playerCustom.refreshStat();
	}

	// get durability of Equipable
	public static int getNewDurability(Equipable<?> equipable) {
		int		level = equipable.getRarity().getNumber();
		int		levelMax = Rarity.LEVEL_MAX + 4;
		int		durabilityMax = equipable.getItem().getType().getMaxDurability();
		double	durabilityPercent = (levelMax - level) / 1000d;
		int		newDamage = Math.max(1, (int)(durabilityPercent * durabilityMax));
		/*
		level 1 = 0.9%
		level 2 = 0.8%
		level 3 = 0.7%
		level 4 = 0.6%
		level 5 = 0.5%
		level 6 = 0.4%
		*/
		return newDamage;
	}
}
