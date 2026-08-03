package fr.jeunesauvage.itemcustom.equipable.weapon;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import net.kyori.adventure.text.Component;

public enum WeaponType implements ItemCustomType {
    HAND("hand", Material.AIR, WeaponMaterial.HAND),
    CLAW("claw", Material.NETHERITE_SWORD, WeaponMaterial.CLAW),
    SWORD("sword", Material.NETHERITE_SWORD, WeaponMaterial.SWORD),
    AXE("axe", Material.NETHERITE_AXE, WeaponMaterial.AXE),
	PICKAXE("pickaxe", Material.NETHERITE_PICKAXE, WeaponMaterial.PICKAXE),
	HOE("hoe", Material.NETHERITE_HOE, WeaponMaterial.HOE),
	SHOVEL("shovel", Material.NETHERITE_SHOVEL, WeaponMaterial.SHOVEL),
	TRIDENT("trident", Material.TRIDENT, WeaponMaterial.TRIDENT),
    MACE("mace", Material.MACE, WeaponMaterial.MACE),
	BOW("bow", Material.BOW, WeaponMaterial.BOW),
	CROSSBOW("crossbow", Material.CROSSBOW, WeaponMaterial.CROSSBOW),
    STAFF("staff", Material.BOW, WeaponMaterial.STAFF),
	SPELLBOOK("spellbook", Material.CROSSBOW, WeaponMaterial.SPELLBOOK),
    SHIELD("shield", Material.SHIELD, WeaponMaterial.SHIELD),
    UNKNOWN("unknown", Material.AIR, WeaponMaterial.UNKNOWN);

    private final String            name;
    private final Material          material;
    private final WeaponMaterial    weaponMaterial;

    WeaponType(String name, Material material, WeaponMaterial weaponMaterial) {
        this.name = name;
        this.material = material;
        this.weaponMaterial = weaponMaterial;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    public WeaponMaterial getWeaponMaterial() {
        return weaponMaterial;
    }

    @Override
    public Set<ClassType> getClassTypes() {
        return weaponMaterial.getClassTypes();
    }

    @Override
    public ItemCustomCategory getCategory() {
        return ItemCustomCategory.WEAPON;
    }

    @Override
    public Component toComponent() {
        return weaponMaterial.toComponent();
    }

    public static ItemCustomType fromString(String name) {
        if (name == null)
            return null;
		for (WeaponType type: WeaponType.values()) {
			if (type.getName().equals(name))
        		return type;
		}
		return null;
    }
}
