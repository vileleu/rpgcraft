package fr.jeunesauvage.entity.playercustom.silence;

import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;

public class Silence {
	private static final NamespacedKey	KEY_SILENCE = new NamespacedKey(RpgCraft.name(), "silence");
	private final Player				player;

	public Silence(Player player) {
		this.player = player;
		load(player);
	}

	public void add(int duration) {
		long	end = System.currentTimeMillis() + (duration * 1000);
		Data.setLong(player.getPersistentDataContainer(), KEY_SILENCE, end);
	}

	public int is() {
		long	end = Data.getLong(player.getPersistentDataContainer(), KEY_SILENCE);
		long	now = System.currentTimeMillis();
		if (now >= end) {
			Data.remove(player.getPersistentDataContainer(), KEY_SILENCE);
			return 0;
		}
		return (int)((end - now) / 1000 + 1);
	}

	private void load(Player player) {
		long					now = System.currentTimeMillis();
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		for (NamespacedKey key: Set.copyOf(pdc.getKeys())) {
			if (!key.equals(KEY_SILENCE)) continue;
			long	end = Data.getLong(pdc, key);
			if (now >= end)
				Data.remove(pdc, key);
		}
	}
}
