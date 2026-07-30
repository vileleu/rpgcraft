package fr.jeunesauvage.itemcustom.equipable.armor;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import net.kyori.adventure.text.Component;

public enum ArmorType implements ItemCustomType {
    CLOTH_HEAD("cloth_head", Material.NETHERITE_HELMET, ArmorMaterial.CLOTH),
    CLOTH_CHEST("cloth_chest", Material.NETHERITE_CHESTPLATE, ArmorMaterial.CLOTH),
    CLOTH_LEGS("cloth_legs", Material.NETHERITE_LEGGINGS, ArmorMaterial.CLOTH),
    CLOTH_FEET("cloth_feet", Material.NETHERITE_BOOTS, ArmorMaterial.CLOTH),
    LEATHER_HEAD("leather_head", Material.NETHERITE_HELMET, ArmorMaterial.LEATHER),
    LEATHER_CHEST("leather_chest", Material.NETHERITE_CHESTPLATE, ArmorMaterial.LEATHER),
    LEATHER_LEGS("leather_legs", Material.NETHERITE_LEGGINGS, ArmorMaterial.LEATHER),
    LEATHER_FEET("leather_feet", Material.NETHERITE_BOOTS, ArmorMaterial.LEATHER),
    MAIL_HEAD("mail_head", Material.NETHERITE_HELMET, ArmorMaterial.MAIL),
    MAIL_CHEST("mail_chest", Material.NETHERITE_CHESTPLATE, ArmorMaterial.MAIL),
    MAIL_LEGS("mail_legs", Material.NETHERITE_LEGGINGS, ArmorMaterial.MAIL),
    MAIL_FEET("mail_feet", Material.NETHERITE_BOOTS, ArmorMaterial.MAIL),
    PLATE_HEAD("plate_head", Material.NETHERITE_HELMET, ArmorMaterial.PLATE),
    PLATE_CHEST("plate_chest", Material.NETHERITE_CHESTPLATE, ArmorMaterial.PLATE),
    PLATE_LEGS("plate_legs", Material.NETHERITE_LEGGINGS, ArmorMaterial.PLATE),
    PLATE_FEET("plate_feet", Material.NETHERITE_BOOTS, ArmorMaterial.PLATE),
    ELYTRA("elytra", Material.ELYTRA, ArmorMaterial.LEATHER),
    UNKNOWN("unknown", Material.AIR, ArmorMaterial.UNKNOWN);

    private final String        name;
    private final Material      material;
    private final ArmorMaterial armorMaterial;

    ArmorType(String name, Material material, ArmorMaterial armorMaterial) {
        this.name = name;
        this.material = material;
        this.armorMaterial = armorMaterial;
    }

    @Override
    public String getName() {
        return name;
    }

    public ArmorMaterial getArmorMaterial() {
        return armorMaterial;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public Set<ClassType> getClassTypes() {
        return armorMaterial.getClassTypes();
    }

    @Override
    public ItemCustomCategory getCategory() {
        return ItemCustomCategory.ARMOR;
    }

    @Override
    public Component toComponent() {
        return armorMaterial.toComponent();
    }

    public static ItemCustomType fromString(String name) {
        if (name == null)
            return null;
		for (ArmorType type: ArmorType.values()) {
			if (type.getName().equals(name))
        		return type;
		}
		return null;
    }
}
