package fr.jeunesauvage.itemcustom;

import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;

public sealed interface EquipableType extends ItemCustomType permits WeaponType, ArmorType {
    EquipableMaterial   getEquipableMaterial();
}
