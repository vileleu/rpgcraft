package fr.jeunesauvage.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.combatant.CombatantType;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.group.Group;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import net.citizensnpcs.api.npc.NPC;

public class CombatManager implements Listener {
	private final ItemCustomManager	itemCustomManager;
	private final EntityManager		entityManager;

	public CombatManager(ItemCustomManager itemCustomManager, EntityManager entityManager) {
		this.itemCustomManager = itemCustomManager;
		this.entityManager = entityManager;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void entityDamageByEntity(EntityDamageByEntityEvent e) {
		Combat	combat = Combat.buildCombat(entityManager.getEntityModifierManager(), e.getEntity(), e.getDamager(), e.getCause());
		if (combat == null) return;
		RpgCraft.debug("isInSameGroup = " + Group.isInSameGroup(combat.getTarget().getLivingEntity(), combat.getDamager().getLivingEntity()));
		if (Group.isInSameGroup(combat.getTarget().getLivingEntity(), combat.getDamager().getLivingEntity())) {
			e.setCancelled(true);
			return;
		}
		RpgCraft.debug("//////////////////////////");
		RpgCraft.debug("combatType = " + combat.getCombatType().getName());
		RpgCraft.debug("combatDamage = " + combat.getCombatDamage().getName());
		RpgCraft.debug("weaponType = " + combat.getWeaponType().getName());
		RpgCraft.debug("//////////////////////////");
		CombatResult	result = new CombatResult(e.getDamage(), combat);
		result = combat.applyBonusDamager(result);
		result = combat.applyBonusTarget(result);
		result.calculate();
		result = combat.applySpell(itemCustomManager.getSpellManager(), result);
		// aggro npc
		if (combat.getTarget().getType() == CombatantType.NPC) {
			NPC				npcTarget = result.getNpcTarget();
			if (npcTarget != null) {
				TraitSentinel	traitSentinel = npcTarget.getOrAddTrait(TraitSentinel.class);
				traitSentinel.addAggro(combat.getDamager().getLivingEntity(), result.getAmount() + 5);
			}
		}
		// aggro npc
		if (combat.getTarget().getType() == CombatantType.PLAYER) {
			Player	playerTarget = (Player)combat.getTarget().getLivingEntity();
			if (TraitSentinel.isOwner(playerTarget)) {
				NPC	pet = TraitSentinel.getPet(playerTarget);
				if (pet != null) {
					TraitSentinel	traitSentinel = pet.getOrAddTrait(TraitSentinel.class);
					traitSentinel.addAggro(combat.getDamager().getLivingEntity(), result.getAmount() + 5);
				}
			}
		}
		e.setDamage(result.getAmount());
		combat.printDamage(result);
		if (result.isCancelled()) e.setCancelled(true);
	}
}
