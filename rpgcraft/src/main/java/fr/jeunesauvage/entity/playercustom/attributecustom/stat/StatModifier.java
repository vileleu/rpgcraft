package fr.jeunesauvage.entity.playercustom.attributecustom.stat;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NonNull;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;

public class StatModifier {
	private final @NonNull NamespacedKey	keyValue;
	private final @NonNull NamespacedKey	keyTimer;
	private final StatType					type;
	private final int						id;
	private final long 						end;
	private final int						value;
	private final int						duration;	

	public StatModifier(@NonNull Player player, StatType type, int id, int value, int duration) {
		this.keyValue = new NamespacedKey(RpgCraft.name(), "statmodifier-" + type.getName() + "-" + id);
		this.keyTimer = new NamespacedKey(RpgCraft.name(), "stattimer-" + type.getName() + "-" + id);
		this.type = type;
		this.id = id;
		this.end = System.currentTimeMillis() + 1000l * duration;
		this.value = value;
		this.duration = duration;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		Data.setInteger(pdc, keyValue, value);
		Data.setLong(pdc, keyTimer, end);
	}

	public NamespacedKey getKeyValue() {
		return this.keyValue;
	}

	public NamespacedKey getKeyTimer() {
		return this.keyTimer;
	}

	public long getTimeLeft() {
		if (duration == 0) return 0;
		return ((this.end - System.currentTimeMillis()) / 1000l) + 1;
	}

	public StatType getType() {
		return this.type;
	}

	public String getName() {
		return this.type.getName();
	}

	public int getId() {
		return this.id;
	}

	public int getValue() {
		return this.value;
	}

	public int getDuration() {
		return duration;
	}
}
