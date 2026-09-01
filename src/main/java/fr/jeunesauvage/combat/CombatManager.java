package fr.jeunesauvage.combat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;

public class CombatManager implements Listener {
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void entityDamageByEntity(EntityDamageByEntityEvent e) {
		Combat	combat = Combat.buildCombat(e);
		if (combat == null || combat.getDamager().isGrouped(combat.getTarget())) {
			e.setCancelled(true);
			return;
		}
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
		LivingEntityCustom	target = combat.getTarget();
		if (target instanceof NPCCustom npcCustom) {
			npcCustom.addAggro(combat.getDamager(), result.getAmount() + 5);
		}
		if (target.isOwner()) {
			NPCCustom	pet = RpgCraft.getEntityCustomRegistry().getNPCCustom(target.getPet());
			if (pet != null) pet.addAggro(combat.getDamager(), result.getAmount() + 5);
		}
		e.setDamage(result.getAmount());
		combat.printDamage(result);
		if (result.isCancelled()) e.setCancelled(true);
	}
}
