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
		if (combat == null) return;
		if (combat.getDamager().isGrouped(combat.getTarget())) {
			e.setCancelled(true);
			return;
		}
		CombatResult	result = new CombatResult(e.getDamage(), combat);
		// damage is unmodifiable
		if (combat.getTarget().damageIsUnmodifiable() != true) {
			result = combat.applyBonusTarget(result);
			result = combat.applyBonusDamager(result);
			result.calculate();
			result = combat.applySpell(result);
		}
		RpgCraft.debug("");
		RpgCraft.debug("combat infos:");
		RpgCraft.debug("level target " + combat.getTarget().getLevel());
		RpgCraft.debug("level damager " + combat.getDamager().getLevel());
		RpgCraft.debug("critical chance " + result.getCriticalChance());
		RpgCraft.debug("dodge chance " + result.getDodgeChance());
		RpgCraft.debug("skill target " + result.getSkillTarget());
		RpgCraft.debug("skill damager " + result.getSkillDamager());
		RpgCraft.debug("combat type " + combat.getCombatType().getName());
		RpgCraft.debug("weapon type " + combat.getWeaponType().getName());
		RpgCraft.debug("combat damage " + combat.getCombatDamage().getName());
		RpgCraft.debug("");
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
