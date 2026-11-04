package fr.jeunesauvage.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.citizensnpcs.api.CitizensAPI;

public class CombatManager implements Listener {
	private final Map<UUID, Float>	attackCooldowns = new HashMap<>();

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onPreAttack(PrePlayerAttackEntityEvent e) {
		Player	p = e.getPlayer();
		if (CitizensAPI.getNPCRegistry().isNPC(p)) return;
	    attackCooldowns.put(p.getUniqueId(), p.getAttackCooldown());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void entityDamageByEntity(EntityDamageByEntityEvent e) {
		Combat	combat = Combat.buildCombat(e);
		// cancel damage
		if (combat == null || combat.getDamager().isGrouped(combat.getTarget())) {
			RpgCraft.debug("isGrouped");
			e.setCancelled(true);
			return;
		}
		LivingEntityCustom	damager = combat.getDamager();
		LivingEntityCustom	target = combat.getTarget();
		// captured
		if (combat.getDamager().getTeams().contains(TeamType.CAPTURED)) combat.getDamager().deleteTeam(TeamType.CAPTURED);
		// mark
		if (damager instanceof PlayerCustom p && p.isFriend(target)) p.addMark();
		else if (damager.isPet() && damager.getOwner() instanceof PlayerCustom p && p.isFriend(target)) p.addMark();
		else if (damager.isOwner() && damager.getPet() instanceof PlayerCustom p && p.isFriend(target)) p.addMark();
		// attack cooldown
		if (damager instanceof PlayerCustom p) {
			Float	value = attackCooldowns.remove(p.getUUID());
			if (value != null) {
				boolean	attackIsInCooldown = (value < 1.0f ? true : false);
				p.setAttackIsInCooldown(attackIsInCooldown);
			}
		}
		CombatResult	result = new CombatResult(e.getDamage(), combat);
		// damage is unmodifiable
		if (target.damageIsUnmodifiable() != true) {
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
		if (target instanceof NPCCustom npcCustom) npcCustom.addAggro(damager, result.getAmount() + 5);
		if (target.isOwner()) {
			NPCCustom	pet = RpgCraft.getEntityCustomRegistry().getNPCCustom(target.getPetUUID());
			if (pet != null) pet.addAggro(damager, result.getAmount() + 5);
		}
		e.setDamage(result.getAmount());
		combat.printDamage(result);
		if (result.isCancelled()) e.setCancelled(true);
	}
}
