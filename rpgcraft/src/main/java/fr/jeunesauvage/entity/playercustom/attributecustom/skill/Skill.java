package fr.jeunesauvage.entity.playercustom.attributecustom.skill;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Skill {
	private final Player		player;
	private final NamespacedKey	key;
	private final NamespacedKey	keyBonus;
	private final SkillType		type;
	private int					value;
	private int					valueMax;
	private int					valueBonus;

	public Skill(Player player, SkillType type) {
		this.player = player;
		this.key = new NamespacedKey(RpgCraft.name(), type.getName());
		this.keyBonus = new NamespacedKey(RpgCraft.name(), type.getName() + "-bonus");
		this.type = type;
		this.value = Data.getInteger(player.getPersistentDataContainer(), key);
		this.valueBonus = Data.getInteger(player.getPersistentDataContainer(), keyBonus);
		this.valueMax = player.getLevel() * 5;
	}

	public boolean isMaxed() {
		return value == valueMax || value == ResourceManager.LEVEL_MAX * 5;
	}

	public void increase(int v) {
		setValue(value + v);
	}

	public void decrease(int v) {
		setValue(value - v);
	}

	public void increaseBonus(int v) {
		setValueBonus(valueBonus + v);
	}

	public void decreaseBonus(int v) {
		setValueBonus(valueBonus - v);
	}

	public void increaseMax(int v) {
		setValueMax(valueMax + v);
	}

	public void decreaseMax(int v) {
		setValueMax(valueMax - v);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Skill skill)) return false;
		return java.util.Objects.equals(type, skill.type);
	}

	public void reset() {
		this.value = 0;
		this.valueMax = 0;
		Data.remove(player.getPersistentDataContainer(), key);
	}

	/*
	** getter + setter
	*/

	public SkillType getType() {
		return this.type;
	}

	public String getName() {
		return this.type.getName();
	}

	public int getValue() {
		return this.value;
	}

	public int getValueBonus() {
		return this.valueBonus;
	}

	public void setValue(int v) {
		if (v <= this.valueMax)
			this.value = v;
		else
			this.value = this.valueMax;
		Data.setInteger(player.getPersistentDataContainer(), key, this.value);
	}

	public void setValueBonus(int v) {
		this.valueBonus = v;
		Data.setInteger(player.getPersistentDataContainer(), keyBonus, this.valueBonus);
	}

	public int getValueMax() {
		return this.valueMax;
	}

	public void setValueMax(int v) {
		if (this.value > v)
			this.value = v;
		this.valueMax = v;
	}

	public Component toComponent() {
		return type.toComponent();
	}

	public TextColor getColor() {
		return type.getColor();
	}
}
