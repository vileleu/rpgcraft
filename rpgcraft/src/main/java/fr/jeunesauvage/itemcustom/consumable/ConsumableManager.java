package fr.jeunesauvage.itemcustom.consumable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.itemcustom.ItemCustomManager;

public class ConsumableManager implements Listener {
	private final ItemCustomManager	itemCustomManager;

	public ConsumableManager(ItemCustomManager itemCustomManager) {
		this.itemCustomManager = itemCustomManager;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onConsume(PlayerItemConsumeEvent e) {
	    Player		player = e.getPlayer();
	    ItemStack	item = e.getItem();
		if (item == null) return;
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		Consumable		consumable = itemCustomManager.getConsumable(item);
		if (consumable == null) return;
		if (!consumable.canConsume(playerCustom)) {
			e.setCancelled(true);
			return;
		}
		consumable.consume(this, playerCustom);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onRightClick(PlayerInteractEvent e) {
		if (!e.getAction().isRightClick()) return;
		Player		player = e.getPlayer();
		ItemStack	item = e.getItem();
		if (item == null) return;
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		Consumable		consumable = itemCustomManager.getConsumable(item);
		if (consumable == null) return;
		int	duration = playerCustom.hasCooldown(consumable.getMaterial());
		if (duration == 0) return;
		e.setCancelled(true);
		playerCustom.getPlayer().sendActionBar(Message.cooldown(duration));
	}
}
