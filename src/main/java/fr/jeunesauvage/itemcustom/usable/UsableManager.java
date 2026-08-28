package fr.jeunesauvage.itemcustom.usable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.spell.SpellRegistry;

public class UsableManager implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onRightClick(PlayerInteractEvent e) {
		if (!e.getAction().isRightClick()) return;
		Player		p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		ItemStack	item = e.getItem();
		if (item == null) return;
		Usable			usable = RpgCraft.getItemCustomRegistry().getUsable(item);
		if (usable == null) return;
		if (usable.getCategory() != ItemCustomCategory.WEAPON)
			e.setCancelled(true);
		if (!usable.canUse(playerCustom, e.getHand())) return;
		usable.use(playerCustom, e.getHand());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		ItemStack	item = playerCustom.getEquipment().getItemInMainHand();
		if (item != null) {
			Usable	usable = RpgCraft.getItemCustomRegistry().getUsable(item);
			if (usable == null) return;
			if (usable.getCategory() != ItemCustomCategory.WEAPON)
				e.setCancelled(true);
		}
		Entity	entity = e.getRightClicked();
		if (entity != null) {
			LivingEntityCustom	target = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(entity.getUniqueId());
			if (target != null) {
				if (!target.isFriend(playerCustom))
					e.setCancelled(true);
			}
		}
	}

	// spell

	/*
    // apply real invisibility
    @EventHandler
    public void onInvisibility(EntityPotionEffectEvent e) {
		PotionEffect	potionEffect = e.getNewEffect();
        if (potionEffect == null || potionEffect.getType() != PotionEffectType.INVISIBILITY) return;
        if (e.getEntity() instanceof Player player && !player.hasMetadata("NPC")) {
			Action	action = e.getAction();
			if (action == Action.ADDED) {
        		for (Player p: Bukkit.getOnlinePlayers()) {
        		    if (p.equals(player)) continue;
        		    p.hidePlayer(RpgCraft.instance(), player);
        		}
			}
			else if (action == Action.REMOVED || action == Action.CLEARED) {
        		for (Player p: Bukkit.getOnlinePlayers()) {
        		    if (p.equals(player)) continue;
        		    p.showPlayer(RpgCraft.instance(), player);
        		}
			}
		}
    }

    // apply real invisibility when player join
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
		Player	player = e.getPlayer();
		boolean	isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
	    for (Player p: Bukkit.getOnlinePlayers()) {
            if (p.equals(player)) continue;
			if (isInvisible)
            	p.hidePlayer(RpgCraft.instance(), player);
			if (p.hasPotionEffect(PotionEffectType.INVISIBILITY))
				player.hidePlayer(RpgCraft.instance(), p);
        }
	}
	*/

	// cancel damage fall on leap + cancel stealth for all damage
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof LivingEntity l)) return;
		LivingEntityCustom	livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
		if (livingEntityCustom == null) return;
		SpellRegistry		spellRegistry = RpgCraft.getSpellRegistry();
		// cancel stealth
		if (e.getDamage() > 0 && spellRegistry.hasStealth(livingEntityCustom)) {
			spellRegistry.removeStealth(livingEntityCustom);
		}
		// cancel damage on leap
	    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (!spellRegistry.hasLeap(livingEntityCustom.getUUID())) return;
	    	e.setCancelled(true);
		}
	}
}
