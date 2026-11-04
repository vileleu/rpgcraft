package fr.jeunesauvage.itemcustom.usable;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.spell.SpellRegistry;

public class UsableManager implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onRightClick(PlayerInteractEvent e) {
		if (!e.getAction().isRightClick()) return;
		Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		ItemStack	item = e.getItem();
		if (item == null) return;
		if (item.getType() == Material.CARVED_PUMPKIN) {
			PlayerInventory	inv = playerCustom.getInventory();
			EquipmentSlot	slot = e.getHand();
			ItemStack		head = inv.getHelmet();
			if (head != null && head.getType() != Material.AIR) {
				int	amount = item.getAmount();
				if (amount != 1) return;
				ItemStack	newHead = item.clone();
				inv.setItem(slot, head);
				inv.setHelmet(newHead);
			}
			else {
				ItemStack	newHead = item.clone();
				newHead.setAmount(1);
				int	amount = item.getAmount() - 1;
				item.setAmount(amount);
				if (amount <= 0) inv.setItem(slot, null);
				inv.setHelmet(newHead);
			}
		}
		Usable		usable = RpgCraft.getItemCustomRegistry().getUsable(item);
		if (usable == null) return;
		if (!usable.canUse(playerCustom, e.getHand())) return;
		if (usable.use(playerCustom, e.getHand())) e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		ItemStack	item = playerCustom.getEquipment().getItemInMainHand();
		if (item == null) return;
		Usable	usable = RpgCraft.getItemCustomRegistry().getUsable(item);
		if (usable == null) return;
		e.setCancelled(true);
	}

	// cancel damage fall on leap + cancel stealth for all damage
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof LivingEntity l)) return;
		LivingEntityCustom	livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
		if (livingEntityCustom == null) return;
		SpellRegistry		spellRegistry = RpgCraft.getSpellRegistry();
		// cancel stealth
		if (e.getDamage() > 0 && spellRegistry.hasStealth(livingEntityCustom.getUUID())) {
			spellRegistry.removeStealth(livingEntityCustom);
		}
		// cancel damage on leap
	    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (spellRegistry.hasLeap(livingEntityCustom.getUUID())) e.setCancelled(true);
			else if (livingEntityCustom instanceof PlayerCustom p && p.getPlayer().isRiptiding()) e.setCancelled(true);
		}
	}
}
