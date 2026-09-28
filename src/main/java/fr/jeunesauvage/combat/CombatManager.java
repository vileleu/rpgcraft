package fr.jeunesauvage.combat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public class CombatManager implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void entityDamageByEntity(EntityDamageByEntityEvent e) {
		Combat	combat = Combat.buildCombat(e);
		if (combat == null || combat.getDamager().isGrouped(combat.getTarget())) {
			RpgCraft.debug("isGrouped");
			e.setCancelled(true);
			return;
		}
		// mark
		if (combat.getDamager() instanceof PlayerCustom p && p.isFriend(combat.getTarget())) p.addMark();
		else if (combat.getDamager().isPet() && combat.getDamager().getOwner() instanceof PlayerCustom p && p.isFriend(combat.getTarget())) p.addMark();
		else if (combat.getDamager().isOwner() && combat.getDamager().getPet() instanceof PlayerCustom p && p.isFriend(combat.getTarget())) p.addMark();
		CombatResult	result = new CombatResult(e.getDamage(), combat);
		// damage is unmodifiable
		if (combat.getTarget().damageIsUnmodifiable() != true) {
			result = combat.applyBonusTarget(result);
			result = combat.applyBonusDamager(result);
			RpgCraft.debug("");
			RpgCraft.debug("combatType: " + combat.getCombatType().getName());
			RpgCraft.debug("weapontype: " + combat.getWeaponType().getName());
			RpgCraft.debug("combatDamage: " + combat.getCombatDamage().getName());
			RpgCraft.debug("armor: " + result.getArmor());
			RpgCraft.debug("before amount: " + result.getAmount());
			result.calculate();
			result = combat.applySpell(result);
			RpgCraft.debug("after amount: " + result.getAmount());
		}
		// aggro npc
		if (combat.getTarget() instanceof NPCCustom npcCustom) npcCustom.addAggro(combat.getDamager(), result.getAmount() + 5);
		if (combat.getTarget().isOwner()) {
			NPCCustom	pet = RpgCraft.getEntityCustomRegistry().getNPCCustom(combat.getTarget().getPetUUID());
			if (pet != null) pet.addAggro(combat.getDamager(), result.getAmount() + 5);
		}
		e.setDamage(result.getAmount());
		combat.printDamage(result);
		if (result.isCancelled()) e.setCancelled(true);
	}
}
