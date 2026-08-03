package fr.jeunesauvage.entity.group;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;

public class Group {
	private static final NamespacedKey	KEY_GROUP = new NamespacedKey(RpgCraft.name(), "group");
	private final Set<UUID>				uuids = new HashSet<>();
	private final int					id;

	Group(int id) {
		this.id = id;
	}

	public void addInGroup(Player living) {
		uuids.add(living.getUniqueId());
		PersistentDataContainer	pdc = living.getPersistentDataContainer();
		Data.setInteger(pdc, KEY_GROUP, id);
	}

	public int removeFromGroup(Player living) {
		uuids.remove(living.getUniqueId());
		PersistentDataContainer	pdc = living.getPersistentDataContainer();
		Data.remove(pdc, KEY_GROUP);
		return uuids.size();
	}

	public boolean isInGroup(LivingEntity living) {
		return uuids.contains(living.getUniqueId());
	}

	public Player getAlly(Player player) {
		UUID	uuidPlayer = player.getUniqueId();
		for (UUID uuid: uuids) {
			if (uuid.equals(uuidPlayer)) continue;
			Player	ally = Bukkit.getPlayer(uuid);
			if (ally == null) continue;
			return ally;
		}
		return null;
	}

	public void clean() {
		for (UUID uuid: uuids) {
			if (!(Bukkit.getEntity(uuid) instanceof LivingEntity living)) continue;
			PersistentDataContainer	pdc = living.getPersistentDataContainer();
			Data.remove(pdc, KEY_GROUP);
		}
		uuids.clear();
	}

	public static boolean hasGroup(LivingEntity living) {
		PersistentDataContainer	pdc = living.getPersistentDataContainer();
		int						id = Data.getInteger(pdc, KEY_GROUP);
		return id != 0;
	}

	public static boolean isInSameGroup(LivingEntity living1, LivingEntity living2) {
		PersistentDataContainer	pdc1 = living1.getPersistentDataContainer();
		PersistentDataContainer	pdc2 = living2.getPersistentDataContainer();
		int						id1 = Data.getInteger(pdc1, KEY_GROUP);
		int						id2 = Data.getInteger(pdc2, KEY_GROUP);
		if (id1 != 0 && id1 == id2) return true;
		int						idOwner1 = -1;
		int						idOwner2 = -2;
		Player					owner1 = TraitSentinel.getOwner(living1);
		if (owner1 != null) {
			if (owner1.equals(living2)) return true;
			PersistentDataContainer	pdcOwner1 = owner1.getPersistentDataContainer();
			idOwner1 = Data.getInteger(pdcOwner1, KEY_GROUP);
			if (idOwner1 != 0 && idOwner1 == id2) return true;
		}
		Player					owner2 = TraitSentinel.getOwner(living2);
		if (owner2 != null) {
			if (owner2.equals(living1)) return true;
			PersistentDataContainer	pdcOwner2 = owner2.getPersistentDataContainer();
			idOwner2 = Data.getInteger(pdcOwner2, KEY_GROUP);
			if (idOwner2 != 0 && id1 == idOwner2) return true;
		}
		return idOwner1 != 0 && idOwner1 == idOwner2;
	}
}
