package fr.jeunesauvage.entitycustom.livingentitycustom.metamorph;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public class MetamorphManager implements Listener {
	// metamorph hotbar
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onWeaponHotbar(PlayerItemHeldEvent e) {
	    Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (playerCustom == null) return;
		if (!RpgCraft.getMetamorphRegistry().isDracthyr(playerCustom)) return;
		e.setCancelled(true);
	}

	// metamorph swap
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onWeaponSwap(PlayerSwapHandItemsEvent e) {
	    Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (playerCustom == null) return;
		if (!RpgCraft.getMetamorphRegistry().isDracthyr(playerCustom)) return;
		e.setCancelled(true);
	}

	// metamorph click inventory
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent e) {
	    if (!(e.getWhoClicked() instanceof Player p)) return;
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (playerCustom == null) return;
		if (!RpgCraft.getMetamorphRegistry().isDracthyr(playerCustom)) return;
	    e.setCancelled(true);
	}
}
