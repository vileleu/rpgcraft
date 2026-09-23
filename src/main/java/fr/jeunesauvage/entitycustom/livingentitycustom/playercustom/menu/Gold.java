package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import java.util.Objects;

public class Gold {
    private int ingots = 0;
    private int nuggets = 0;

    public void reset() {
        ingots = 0;
        nuggets = 0;
    }

    public boolean haveIngots() {
        return ingots > 0;
    }

    public boolean haveNuggets() {
        return nuggets > 0;
    }

    public boolean isEmpty() {
        return (ingots == 0 && nuggets == 0);
    }

    public void increaseIngots(int amount) {
        ingots += amount;
    }

    public void decreaseIngots(int amount) {
        ingots -= Math.min(ingots, amount);
    }

    public void increaseNuggets(int amount) {
        nuggets += amount;
    }

    public void decreaseNuggets(int amount) {
        nuggets -= Math.min(nuggets, amount);
    }

    public int getIngots() {
        return ingots;
    }

    public int getNuggets() {
        return nuggets;
    }

    public void copy(Gold gold) {
        ingots = gold.ingots;
        nuggets = gold.nuggets;
    }

    public boolean isHigherOrEqual(Gold gold) {
        int total1 = ingots * 9 + nuggets;
        int total2 = gold.ingots * 9 + gold.nuggets;
        return (total1 >= total2);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Gold gold)) return false;
        return ingots == gold.ingots && nuggets == gold.nuggets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingots, nuggets);
    }
}
