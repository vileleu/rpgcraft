package fr.jeunesauvage.entitycustom.livingentitycustom.saveEquipment;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;

import fr.jeunesauvage.RpgCraft;

public enum SaveEquipment {
    HEAD("head", EquipmentSlot.HEAD, new NamespacedKey(RpgCraft.name(), "head")),
    CHEST("chest", EquipmentSlot.HEAD, new NamespacedKey(RpgCraft.name(), "chest")),
    LEGS("legs", EquipmentSlot.HEAD, new NamespacedKey(RpgCraft.name(), "legs")),
    FEET("feet", EquipmentSlot.HEAD, new NamespacedKey(RpgCraft.name(), "feet")),
    HAND("hand", EquipmentSlot.HEAD, new NamespacedKey(RpgCraft.name(), "hand")),
    OFFHAND("offhand", EquipmentSlot.HEAD, new NamespacedKey(RpgCraft.name(), "offhand"));

    private final String        name;
    private final EquipmentSlot slot;
    private final NamespacedKey key;

    SaveEquipment(String name, EquipmentSlot slot, NamespacedKey key) {
        this.name = name;
        this.slot = slot;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public NamespacedKey getKey() {
        return key;
    }
}
