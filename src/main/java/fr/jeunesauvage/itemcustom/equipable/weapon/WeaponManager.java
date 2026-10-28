package fr.jeunesauvage.itemcustom.equipable.weapon;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.equipable.weapon.launcher.LauncherManager;

public class WeaponManager implements Listener {
	public WeaponManager() {
		LauncherManager	launcherManager = new LauncherManager();
		RpgCraft.instance().getServer().getPluginManager().registerEvents(launcherManager, RpgCraft.instance());
	}

	// weapon hotbar
	@EventHandler
	public void onWeaponHotbar(PlayerItemHeldEvent e) {
	    Player			player = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(player.getUniqueId());
		if (playerCustom == null) return;
	    int			previousSlot = e.getPreviousSlot();
	    int			newSlot = e.getNewSlot();
		ItemStack	previousItem = player.getInventory().getItem(previousSlot);
		ItemStack	newItem = player.getInventory().getItem(newSlot);
		if (isWeaponCustom(previousItem) || isWeaponCustom(newItem))
			playerCustom.refreshStat();
	}

	// weapon swap
	@EventHandler
	public void onWeaponSwap(PlayerSwapHandItemsEvent e) {
	    Player			player = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(player.getUniqueId());
		if (playerCustom == null) return;
	    ItemStack	hand = e.getMainHandItem();
	    ItemStack	offhand = e.getOffHandItem();
		if (isWeaponCustom(hand) || isWeaponCustom(offhand))
			playerCustom.refreshStat();
	}

	// weapon click
	@EventHandler
	public void onWeaponClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) return;
	    if (!(e.getWhoClicked() instanceof Player player)) return;
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(player.getUniqueId());
		if (playerCustom == null) return;
	    ItemStack 	current = e.getCurrentItem();
	    ItemStack 	cursor = e.getCursor();
		current = (isWeaponCustom(current) ? current : null);
		cursor = (isWeaponCustom(cursor) ? cursor : null);
		if (current == null && cursor == null) return;
		PlayerInventory	inv = player.getInventory();
		ClickType		typeClick = e.getClick();
		int				slotClick = e.getSlot();
		int				slotHand = inv.getHeldItemSlot();
		int				slotOffhand = 40;
		if (typeClick.isShiftClick()) {
			if (current == null) return;
			// equip/unequip weapon
			if (current.getType() == Material.SHIELD)
				playerCustom.refreshStat();
			else if (slotClick == slotHand)
				playerCustom.refreshStat();
			else if (inv.getItem(slotHand) == null)
				playerCustom.refreshStat();
		}
		else if (typeClick.isMouseClick()) {
			if (e.getClickedInventory().getType() != InventoryType.PLAYER) return;
			// equip/unequip weapon
			if (slotClick == slotHand || slotClick == slotOffhand)
				playerCustom.refreshStat();
		}
	}

	// weapon pickup
	@EventHandler
	public void onWeaponPickup(EntityPickupItemEvent e) {
	    if (!(e.getEntity() instanceof Player player)) return;
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(player.getUniqueId());
		if (playerCustom == null) return;
	    if (isWeaponCustom(e.getItem().getItemStack()))
			playerCustom.refreshStat();
	}

	public boolean isWeaponCustom(ItemStack item) {
		if (item == null) return false;
		return RpgCraft.getItemCustomRegistry().getWeapon(item) != null;
	}
}
