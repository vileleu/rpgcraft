package fr.jeunesauvage.entity.playercustom.attributecustom.stat;

import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Stat {
	private StatType	type;
	private int			value;
	private int			lastValue;
	private int			valueBonus;

	public Stat(StatType type, int value) {
		this.type = type;
		this.value = value;
		this.lastValue = 0;
		this.valueBonus = 0;
	}

	public void increase(int v) {
		setValue(value + v);
	}

	public void decrease(int v) {
		setValue(value - v);
	}

	public void increaseBonus(int v) {
		setValue(value + v);
		setValueBonus(valueBonus + v);
	}

	public void decreaseBonus(int v) {
		setValue(value - v);
		setValueBonus(valueBonus - v);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Stat stat)) return false;
		return java.util.Objects.equals(type, stat.type);
	}

	public boolean needRefresh() {
		return switch (this.type.getCategory()) {
			case AttributeCategory.PRIMARY -> true;
			default -> value != lastValue;
		};
	}

	public void reset() {
		this.value = 0;
		this.lastValue = 0;
	}

	/*
	** getter + setter
	*/

	public StatType getType() {
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
		this.value = v;
	}

	public void setValueBonus(int v) {
		this.valueBonus = v;
	}

	public int getLastValue() {
		return this.lastValue;
	}

	public void setLastValue(int v) {
		this.lastValue = v;
	}

	public Component toComponent() {
		return type.toComponent();
	}

	public TextColor getColor() {
		return type.getColor();
	}
}
