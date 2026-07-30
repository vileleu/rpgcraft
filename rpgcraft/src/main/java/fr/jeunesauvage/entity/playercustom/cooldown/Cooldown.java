package fr.jeunesauvage.entity.playercustom.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;

public class Cooldown {
	private final Player						player;
	private final Map<Material, CooldownData>	cooldowns = new HashMap<>();

	public Cooldown(Player player) {
		this.player = player;
		load(player);
	}

	public void add(Material material, int duration) {
		CooldownData	cooldownData = new CooldownData(material, duration);
		Data.setLong(player.getPersistentDataContainer(), cooldownData.getKey(), cooldownData.getEnd());
		cooldowns.put(material, cooldownData);
		player.setCooldown(material, duration * 20);
	}

	public int has(Material material) {
		CooldownData	cooldownData = cooldowns.get(material);
		if (cooldownData == null) return 0;
		int	duration = cooldownData.getDuration();
		if (duration == 0) {
			Data.remove(player.getPersistentDataContainer(), cooldownData.getKey());
			cooldowns.remove(material);
		}
		return duration;
	}

	public void refresh() {
		cooldowns.entrySet().removeIf(entry -> {
			CooldownData	cooldownData = entry.getValue();
		    int				duration = cooldownData.getDuration();
		    if (duration == 0) {
		        Data.remove(player.getPersistentDataContainer(), cooldownData.getKey());
		        return true;
		    }
		    player.setCooldown(entry.getKey(), duration * 20);
		    return false;
		});
	}

	private void load(Player player) {
		cooldowns.clear();
		long					now = System.currentTimeMillis();
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		for (NamespacedKey key: Set.copyOf(pdc.getKeys())) {
			String	name = key.getKey();
			if (!name.startsWith("cooldown-")) continue;
			long	end = Data.getLong(pdc, key);
			if (end <= now) {
				Data.remove(pdc, key);
				continue;
			}
			Material	material = Material.getMaterial(name.substring(9).toUpperCase());
			if (material == null) {
				Data.remove(pdc, key);
				continue;
			}
			int				duration = (int)((end - now) / 1000 + 1);
			CooldownData	cooldownData = new CooldownData(material, duration);
			cooldowns.put(material, cooldownData);
			player.setCooldown(material, duration * 20);
		}
	}

	public Map<Material, CooldownData> getCooldowns() {
		return cooldowns;
	}
}
