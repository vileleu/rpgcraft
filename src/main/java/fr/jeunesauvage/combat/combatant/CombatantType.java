package fr.jeunesauvage.combat.combatant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public enum CombatantType {
	LIVING_ENTITY,
	NPC,
	PLAYER;

	public static CombatantType fromLivingEntity(LivingEntity livingEntity) {
		if (livingEntity == null) return null;
		if (livingEntity.hasMetadata("NPC")) return NPC;
		if (livingEntity instanceof Player) return PLAYER;
		return LIVING_ENTITY;
	}
}
