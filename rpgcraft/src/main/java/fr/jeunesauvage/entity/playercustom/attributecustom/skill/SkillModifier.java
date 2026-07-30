package fr.jeunesauvage.entity.playercustom.attributecustom.skill;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NonNull;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;

public class SkillModifier {
	private final @NonNull NamespacedKey	keyValue;
	private final @NonNull NamespacedKey	keyTimer;
	private final SkillType					type;
	private final int						id;
	private final long 						endTime;
	private final int						value;
	private final int						duration;

	public SkillModifier(@NonNull Player player, SkillType type, int id, int value, int duration) {
		this.keyValue = new NamespacedKey(RpgCraft.name(), "skillmodifier-" + type.getName() + "-" + id);
		this.keyTimer = new NamespacedKey(RpgCraft.name(), "skilltimer-" + type.getName() + "-" + id);
		this.type = type;
		this.id = id;
		this.endTime = System.currentTimeMillis() + 1000l * duration;
		this.value = value;
		this.duration = duration;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		Data.setInteger(pdc, keyValue, value);
		if (duration > 0)
			Data.setLong(pdc, keyTimer, endTime);
	}

	public NamespacedKey getKeyValue() {
		return this.keyValue;
	}

	public NamespacedKey getKeyTimer() {
		return this.keyTimer;
	}

	public long getTimeLeft() {
		if (duration == 0) return 0;
		return (this.endTime - System.currentTimeMillis()) / 1000l + 1;
	}

	public SkillType getType() {
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
