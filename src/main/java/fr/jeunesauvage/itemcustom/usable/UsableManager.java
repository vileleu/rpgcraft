package fr.jeunesauvage.itemcustom.usable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomManager;

public class UsableManager implements Listener {
	private final ItemCustomManager	itemCustomManager;

	public UsableManager(ItemCustomManager itemCustomManager) {
		this.itemCustomManager = itemCustomManager;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onRightClick(PlayerInteractEvent e) {
		if (!e.getAction().isRightClick()) return;
		Player		player = e.getPlayer();
		ItemStack	item = e.getItem();
		if (item == null) return;
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		Usable			usable = itemCustomManager.getUsable(item);
		if (usable == null) return;
		if (usable.getCategory() != ItemCustomCategory.WEAPON)
			e.setCancelled(true);
		if (!usable.canUse(itemCustomManager, playerCustom, e.getHand())) return;
		usable.use(itemCustomManager, playerCustom, e.getHand());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		Player		player = e.getPlayer();
		ItemStack	item = player.getInventory().getItem(e.getHand());
		if (item == null) return;
		Usable		usable = itemCustomManager.getUsable(item);
		if (usable == null) return;
		if (usable.getCategory() != ItemCustomCategory.WEAPON)
			e.setCancelled(true);
	}
}
