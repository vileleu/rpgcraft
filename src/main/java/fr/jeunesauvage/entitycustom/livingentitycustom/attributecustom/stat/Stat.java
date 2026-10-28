package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat;

import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeCustom;

public class Stat implements AttributeCustom {
	private StatType	type;
	private int			value;
	private int			valueModifier;

	public Stat(StatType type) {
		this.type = type;
		this.value = 0;
		this.valueModifier = 0;
	}

    public StatType getType() {
		return type;
	}

	@Override
    public int getValue() {
		return value;
	}

	@Override
    public void setValue(int value) {
		this.value = value;
	}

	@Override
    public void increase(int amount) {
		value += amount;
	}

	@Override
    public void decrease(int amount) {
		value -= amount;
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
