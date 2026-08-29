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

    public void setValue(double value) {
        if (value < 0) this.value = 0;
        else if (value > valueMax) this.value = valueMax;
        else this.value = value;
    }

    public double getValueMax() {
        return valueMax;
    }

    public void setValueMax(double valueMax) {
        if (valueMax < 0) this.valueMax = 1;
        else this.valueMax = valueMax;
    }

    public void increase(double amount) {
        value = Math.min(valueMax, value + amount);
    }

    public void decrease(double amount) {
        value = Math.max(0, value - amount);
    }

    public void increaseMax(double amount) {
        valueMax = valueMax + amount;
    }

    public void decreaseMax(double amount) {
        valueMax = Math.max(1, valueMax - amount);
    }
}