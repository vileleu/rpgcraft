package fr.jeunesauvage.entity.team;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;

public class Team {
	public static void add(LivingEntity livingEntity, TeamType type) {
		if (type == null) return;
		Data.setBoolean(livingEntity.getPersistentDataContainer(), type.getKey());
	}

	public static void remove(LivingEntity livingEntity, TeamType type) {
		if (type == null) return;
		Data.remove(livingEntity.getPersistentDataContainer(), type.getKey());
	}

	public static boolean has(LivingEntity livingEntity, TeamType type) {
		if (type == null) return false;
		return Data.hasBoolean(livingEntity.getPersistentDataContainer(), type.getKey());
	}

	public static boolean isFriend(LivingEntity e1, LivingEntity e2) {
		PersistentDataContainer	pdc1 = e1.getPersistentDataContainer();
		PersistentDataContainer	pdc2 = e2.getPersistentDataContainer();
		for (TeamType type: TeamType.values()) {
			if (Data.hasBoolean(pdc1, type.getKey()) && Data.hasBoolean(pdc2, type.getKey()))
				return true;
		}
		return false;
	}

	public static Set<TeamType> get(LivingEntity livingEntity) {
		Set<TeamType>			teams = new HashSet<>();
		PersistentDataContainer	pdc = livingEntity.getPersistentDataContainer();
		for (TeamType type: TeamType.values()) {
			if (Data.hasBoolean(pdc, type.getKey()))
				teams.add(type);
		}
		return teams;
	}
}
