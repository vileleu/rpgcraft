package fr.jeunesauvage.itemcustom.equipable.weapon;

import org.bukkit.Material;
import org.bukkit.entity.Item;
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
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.equipable.EquipableManager;
import fr.jeunesauvage.itemcustom.equipable.weapon.launcher.LauncherManager;

public class WeaponManager implements Listener {
	private final EquipableManager	equipableManager;
	private final LauncherManager 	launcherManager;

	public WeaponManager(JavaPlugin plugin, ItemCustomManager itemCustomManager, EquipableManager equipableManager) {
		this.equipableManager = equipableManager;
		this.launcherManager = new LauncherManager(itemCustomManager);
		plugin.getServer().getPluginManager().registerEvents(launcherManager, plugin);
	}

	// weapon hotbar
	@EventHandler
	public void onWeaponHotbar(PlayerItemHeldEvent e) {
	    Player		player = e.getPlayer();
	    int			previousSlot = e.getPreviousSlot();
	    int			newSlot = e.getNewSlot();
		ItemStack	previousItem = player.getInventory().getItem(previousSlot);
		ItemStack	newItem = player.getInventory().getItem(newSlot);
		if (isWeaponCustom(previousItem) || isWeaponCustom(newItem))
			equipableManager.refreshEquipement(player);
	}

	// weapon swap
	@EventHandler
	public void onWeaponSwap(PlayerSwapHandItemsEvent e) {
	    Player		player = e.getPlayer();
	    ItemStack	hand = e.getMainHandItem();
	    ItemStack	offhand = e.getOffHandItem();
		if (isWeaponCustom(hand) || isWeaponCustom(offhand))
			equipableManager.refreshEquipement(player);
	}

	// weapon click
	@EventHandler
	public void onWeaponClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) return;
	    if (!(e.getWhoClicked() instanceof Player player)) return;
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
			// equip/unequip offensive/defensive weapon
			if (current.getType() == Material.SHIELD)
				equipableManager.refreshEquipement(player);
			else if (slotClick == slotHand)
				equipableManager.refreshEquipement(player);
			else if (inv.getItem(slotHand) == null)
				equipableManager.refreshEquipement(player);
		}
		else if (typeClick.isMouseClick()) {
			if (e.getClickedInventory().getType() != InventoryType.PLAYER) return;
			// equip/unequip weapon
			if (slotClick == slotHand || slotClick == slotOffhand)
				equipableManager.refreshEquipement(player);
		}
	}

	// weapon pickup
	@EventHandler
	public void onWeaponPickup(EntityPickupItemEvent e) {
	    if (!(e.getEntity() instanceof Player player)) return;
	    Item		itemEntity = e.getItem();
	    ItemStack 	item = itemEntity.getItemStack();
	    if (isWeaponCustom(item))
			equipableManager.refreshEquipement(player);
	}

	public boolean isWeaponCustom(ItemStack item) {
		if (item == null)
			return false;
		Material	material = item.getType();
		for (WeaponType type: WeaponType.values()) {
			if (type == WeaponType.HAND || type == WeaponType.UNKNOWN) continue;
			if (type.getMaterial() == material)
				return true;
		}
		return false;
	}

	public LauncherManager getLauncherManager() {
		return launcherManager;
	}

	// is weapon
	public static boolean isWeapon(Material m) {
		String	s = m.name();
	    if (s.endsWith("_SWORD") 
		|| s.endsWith("_AXE")
		|| s.endsWith("_PICKAXE")
		|| s.endsWith("_SHOVEL")
		|| s.endsWith("_HOE")
		|| s.equals("SHEARS")
		|| s.equals("TRIDENT")
		|| s.equals("MACE")
		|| s.equals("SHIELD")
		|| s.equals("BOW")
		|| s.equals("CROSSBOW"))
			return true;
		return false;
	}
}
