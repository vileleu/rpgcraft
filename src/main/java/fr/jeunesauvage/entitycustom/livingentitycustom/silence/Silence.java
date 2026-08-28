package fr.jeunesauvage.entitycustom.livingentitycustom.silence;

import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;

public class Silence {
	private static final NamespacedKey	KEY_SILENCE = new NamespacedKey(RpgCraft.name(), "silence");
	private final LivingEntity			livingEntity;

	public Silence(LivingEntity livingEntity) {
		this.livingEntity = livingEntity;
		load(livingEntity);
	}

	public void add(int duration) {
		long	end = System.currentTimeMillis() + (duration * 1000);
		Data.setLong(livingEntity.getPersistentDataContainer(), KEY_SILENCE, end);
	}

	public int is() {
		long	end = Data.getLong(livingEntity.getPersistentDataContainer(), KEY_SILENCE);
		long	now = System.currentTimeMillis();
		if (end <= now) {
			Data.remove(livingEntity.getPersistentDataContainer(), KEY_SILENCE);
			return 0;
		}
		return (int)((end - now) / 1000 + 1);
	}

	private void load(LivingEntity livingEntity) {
		long					now = System.currentTimeMillis();
		PersistentDataContainer	pdc = livingEntity.getPersistentDataContainer();
		for (NamespacedKey key: Set.copyOf(pdc.getKeys())) {
			if (!key.equals(KEY_SILENCE)) continue;
			long	end = Data.getLong(pdc, key);
			if (now >= end)
				Data.remove(pdc, key);
		}
	}
}
