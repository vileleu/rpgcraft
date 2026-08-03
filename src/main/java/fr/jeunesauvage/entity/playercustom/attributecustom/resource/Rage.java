package fr.jeunesauvage.entity.playercustom.attributecustom.resource;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import fr.jeunesauvage.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Rage implements Resource {
	private final @NonNull Player	player;
	private final ResourceType		type;
	private double					value;
	private double					valueMax;

	public Rage(@NonNull Player p, double v, double vMax) {
		this.player = p;
		this.type = ResourceType.RAGE;
		this.value = v;
		this.valueMax = vMax;
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

	@Override
	public void increaseMax(double v) {
		setValueMax(valueMax + v);
	}

	@Override
	public void decreaseMax(double v) {
		setValueMax(valueMax - v);
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
		value = Math.max(Math.min(v, valueMax), 0);
		Data.setDouble(player.getPersistentDataContainer(), ResourceType.RAGE.getKey(), value);
	}

	@Override
	public void setValueMax(double vMax) {
		valueMax = Math.max(vMax, 1);
		if (value > valueMax)
			setValue(valueMax);
	}

	@Override
	public Component toComponent() {
		return type.toComponent();
	}

	@Override
	public TextColor getColor() {
		return type.getColor();
	}
}
