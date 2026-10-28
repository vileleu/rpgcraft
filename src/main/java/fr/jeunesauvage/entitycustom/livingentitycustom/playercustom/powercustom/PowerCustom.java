package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom;

public class PowerCustom {
    private final PowerType powerType;
    private double          value;
    private double          valueMax;

    public PowerCustom(PowerType powerType) {
        this.powerType = powerType;
        if (powerType == PowerType.RAGE || powerType == PowerType.ENERGY) {
            this.value = 0;
            this.valueMax = 100;
        }
        else {
            this.value = 0;
            this.valueMax = 1;
        }
    }

    public PowerType getType() {
        return powerType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double amount) {
        value = Math.max(0, Math.min(valueMax, amount));
    }

    public double getValueMax() {
        return valueMax;
    }

    public void setValueMax(double amount) {
        valueMax = Math.max(1, amount);
        if (value > valueMax) value = valueMax;
    }

    public void increase(double amount) {
        value = Math.max(0, Math.min(valueMax, value + amount));
    }

    public void decrease(double amount) {
        value = Math.max(0, Math.min(valueMax, value - amount));
    }

    public void increaseMax(double amount) {
        valueMax = Math.max(1, valueMax + amount);
        if (value > valueMax) value = valueMax;
    }

    public void decreaseMax(double amount) {
        valueMax = Math.max(1, valueMax - amount);
        if (value > valueMax) value = valueMax;
    }
}