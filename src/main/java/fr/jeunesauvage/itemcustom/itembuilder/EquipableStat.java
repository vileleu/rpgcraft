package fr.jeunesauvage.itemcustom.itembuilder;

import java.util.Set;

import com.google.gson.annotations.SerializedName;

import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;

public class EquipableStat {
    private Set<StatPrimary>    statsPrimary;
    private Set<StatSecondary>  statsSecondary;
    private Rarity              rarity;
    private int                 level;
    @SerializedName("type")
    private EquipableMaterial   equipableMaterial;

    public Set<StatPrimary> getStatsPrimary() {
        return statsPrimary;
    }

    public Set<StatSecondary> getStatsSecondary() {
        return statsSecondary;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getLevel() {
        return level;
    }

    public EquipableMaterial getEquipableMaterial() {
        return equipableMaterial;
    }
}
