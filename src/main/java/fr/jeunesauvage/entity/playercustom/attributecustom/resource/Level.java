package fr.jeunesauvage.entity.playercustom.attributecustom.resource;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Level implements Resource {
	private final @NonNull Player	player;
	private final ResourceType		type;
	private int						value;
	private final int				valueMax;

	public Level(@NonNull Player player, int valueMax) {
		this.player = player;
		this.type = ResourceType.LEVEL;
		this.value = player.getLevel();
		this.valueMax = valueMax;
	}

	@Override
	public ResourceType getType() {
		return type;
	}

	@Override
	public void increase(double v) {
		setValue(value + v);
	}

	@Override
	public void decrease(double v) {
		setValue(value - v);
	}

	public void increase(int v) {
		setValue(value + v);
	}

	public void decrease(int v) {
		setValue(value - v);
	}

	@Override
	public void increaseMax(double v) {
	}

	@Override
	public void decreaseMax(double v) {
	}

	/*
	** getter + setter
	*/

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public double getValueMax() {
		return valueMax;
	}

	@Override
	public void setValue(double v) {
		value = Math.max(Math.min((int)v, valueMax), 0);
		player.setLevel(value);
	}

	public void setValue(int v) {
		value = Math.max(Math.min(v, valueMax), 0);
		player.setLevel(value);
	}

	@Override
	public void setValueMax(double vMax) {}

	@Override
	public Component toComponent() {
		return type.toComponent();
	}

	@Override
	public TextColor getColor() {
		return type.getColor();
	}
}
