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
    private float               cmd;
    private String              set;
    @SerializedName("type")
    private EquipableMaterial   equipableMaterial;

    EquipableStat(EquipableMaterial equipableMaterial) {
        this.statsPrimary = null;
        this.statsSecondary = null;
        this.rarity = Rarity.COMMON;
        this.level = 1;
        this.cmd = 0;
        this.set = null;
        this.equipableMaterial = equipableMaterial;
    }
    
    EquipableStat(EquipableStat equipableStat) {
        this.statsPrimary = equipableStat.statsPrimary;
        this.statsSecondary = equipableStat.statsSecondary;
        this.rarity = equipableStat.rarity;
        this.level = equipableStat.level;
        this.cmd = equipableStat.cmd;
        this.set = equipableStat.set;
        this.equipableMaterial = equipableStat.equipableMaterial;
    }

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

    public float getCmd() {
        return cmd;
    }

    public String getSet() {
        return set;
    }

    public EquipableMaterial getEquipableMaterial() {
        return equipableMaterial;
    }

    public void setCmd(float cmd) {
        this.cmd = cmd;
    }

    public void setSet(String set) {
        this.set = set;
    }
}
