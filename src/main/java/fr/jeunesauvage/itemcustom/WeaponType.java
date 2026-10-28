package fr.jeunesauvage.itemcustom;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;
import fr.jeunesauvage.itemcustom.equipable.WeaponMaterial;
import net.kyori.adventure.text.Component;

public enum WeaponType implements EquipableType {
    HAND("hand", WeaponMaterial.HAND),
    CLAW("claw", WeaponMaterial.CLAW),
    SWORD("sword", WeaponMaterial.SWORD),
    SPEAR("spear", WeaponMaterial.SPEAR),
    AXE("axe", WeaponMaterial.AXE),
	PICKAXE("pickaxe", WeaponMaterial.PICKAXE),
	HOE("hoe", WeaponMaterial.HOE),
	SHOVEL("shovel", WeaponMaterial.SHOVEL),
	SHEARS("shears", WeaponMaterial.SHEARS),
	TRIDENT("trident", WeaponMaterial.TRIDENT),
    MACE("mace", WeaponMaterial.MACE),
	BOW("bow", WeaponMaterial.BOW),
	CROSSBOW("crossbow", WeaponMaterial.CROSSBOW),
    STAFF("staff", WeaponMaterial.STAFF),
	SPELLBOOK("spellbook", WeaponMaterial.SPELLBOOK),
    SHIELD("shield", WeaponMaterial.SHIELD),
    UNKNOWN("unknown", WeaponMaterial.UNKNOWN);

    private final String            name;
    private final WeaponMaterial    weaponMaterial;

    WeaponType(String name, WeaponMaterial weaponMaterial) {
        this.name = name;
        this.weaponMaterial = weaponMaterial;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Material getMaterial() {
        return weaponMaterial.getMaterial();
    }

    @Override 
    public EquipableMaterial getEquipableMaterial() {
        return weaponMaterial;
    }

    @Override
    public Set<ClassType> getClassTypes() {
        return weaponMaterial.getClassTypes();
    }

    @Override
    public Component toComponent() {
        return weaponMaterial.toComponent();
    }

    public static WeaponType fromString(String name) {
        if (name == null)
            return UNKNOWN;
		for (WeaponType type: WeaponType.values()) {
			if (type.getName().equals(name))
        		return type;
		}
		return UNKNOWN;
    }

    public static WeaponType fromMaterial(EquipableMaterial equipableMaterial) {
        if (equipableMaterial == null)
            return UNKNOWN;
		for (WeaponType type: WeaponType.values()) {
			if (type.getName().equals(equipableMaterial.getName()))
        		return type;
		}
		return UNKNOWN;
    }
}
