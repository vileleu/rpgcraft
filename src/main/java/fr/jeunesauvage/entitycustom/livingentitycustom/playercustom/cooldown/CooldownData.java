package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.cooldown;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;

public class CooldownData {
	private final long			end;
	private final NamespacedKey	key;

	CooldownData(Material material, int duration) {
		this.end = System.currentTimeMillis() + (duration * 1000);
		this.key = new NamespacedKey(RpgCraft.name(), "cooldown/" + material.name().toLowerCase());
	}

	public int getDuration() {
		float	decimal = ((end - System.currentTimeMillis()) / 1000f);
		int		duration = (int)(decimal > 0f ? decimal + 1f : decimal);
		if (duration <= 0) return 0;
		return duration;
	}

	public long getEnd() {
		return end;
	}

	public NamespacedKey getKey() {
		return key;
	}
}
