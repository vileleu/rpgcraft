package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill;

import org.bukkit.entity.Player;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeCustom;

public class Skill implements AttributeCustom {
	private final Player	player;
	private final SkillType	type;
	private int				value;
	private int				valueModifier;
	private int				valueMax;

	public Skill(SkillType type, int value) {
		this.player = null;
		this.type = type;
		this.value = value;
		this.valueModifier = 0;
		this.valueMax = value;
	}

	public Skill(Player player, SkillType type) {
		this.player = player;
		this.type = type;
		this.value = Data.getInteger(player.getPersistentDataContainer(), type.getKey());
		this.valueModifier = 0;
		this.valueMax = player.getLevel() * 5;
	}

    public SkillType getType() {
		return type;
	}

	public boolean isMaxed() {
		return value == valueMax;
	}

	public int getDifference() {
		return valueMax - value;
	}

	@Override
    public int getValue() {
		return value;
	}

	@Override
    public void setValue(int value) {
		if (player == null) this.value = Math.min(value, valueMax);
		else {
			valueMax = player.getLevel() * 5;
			this.value = Math.min(value, valueMax);
			Data.setInteger(player.getPersistentDataContainer(), type.getKey(), value);
		}
	}

	@Override
    public void increase(int amount) {
		if (player == null) value = Math.min(value + amount, valueMax);
		else {
			valueMax = player.getLevel() * 5;
			this.value = Math.min(value + amount, valueMax);
			Data.setInteger(player.getPersistentDataContainer(), type.getKey(), value);
		}
	}

	@Override
    public void decrease(int amount) {
		if (player == null) value = Math.min(value - amount, valueMax);
		else {
			valueMax = player.getLevel() * 5;
			this.value = Math.min(value - amount, valueMax);
			Data.setInteger(player.getPersistentDataContainer(), type.getKey(), value);
		}
	}

	@Override
	public int getValueModifier() {
		return valueModifier;
	}

	@Override
    public void increaseModifier(int amount) {
		valueModifier += amount;
	}

	@Override
    public void decreaseModifier(int amount) {
		valueModifier -= amount;
	}
}
