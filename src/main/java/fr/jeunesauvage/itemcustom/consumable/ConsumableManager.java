package fr.jeunesauvage.itemcustom.consumable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public class ConsumableManager implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onConsume(PlayerItemConsumeEvent e) {
	    Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
	    ItemStack	item = e.getItem();
		if (item == null) return;
		Consumable		consumable = RpgCraft.getItemCustomRegistry().getConsumable(item);
		if (consumable == null) return;
		if (!consumable.canConsume(playerCustom)) {
			e.setCancelled(true);
			return;
		}
		consumable.consume(playerCustom);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onRightClick(PlayerInteractEvent e) {
		if (!e.getAction().isRightClick()) return;
	    Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
	    ItemStack	item = e.getItem();
		if (item == null) return;
		Consumable		consumable = RpgCraft.getItemCustomRegistry().getConsumable(item);
		if (consumable == null) return;
		int	duration = playerCustom.hasCooldown(consumable.getMaterial());
		if (duration == 0) return;
		e.setCancelled(true);
		playerCustom.sendActionBar(Message.cooldown(duration));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		ItemStack	item = playerCustom.getEquipment().getItemInMainHand();
		if (item == null) return;
		Consumable	consumable = RpgCraft.getItemCustomRegistry().getConsumable(item);
		if (consumable == null) return;
		e.setCancelled(true);
	}
}
