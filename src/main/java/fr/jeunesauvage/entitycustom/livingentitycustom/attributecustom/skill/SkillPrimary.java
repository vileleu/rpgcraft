package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeCategory;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum SkillPrimary implements SkillType {
	TEMPERING("tempering", null),
    HAND("hand", WeaponType.HAND),
    SWORD("sword", WeaponType.SWORD),
    AXE("axe", WeaponType.AXE),
    SHOVEL("shovel", WeaponType.SHOVEL),
    PICKAXE("pickaxe", WeaponType.PICKAXE),
    HOE("hoe", WeaponType.HOE),
    MACE("mace", WeaponType.MACE),
    TRIDENT("trident", WeaponType.TRIDENT),
    BOW("bow", WeaponType.BOW),
    CROSSBOW("crossbow", WeaponType.CROSSBOW),
    STAFF("staff", WeaponType.STAFF),
    SPELLBOOK("spellbook", WeaponType.SPELLBOOK);

    private final String        name;
    private final NamespacedKey key;
    private final WeaponType    weaponType;

    private SkillPrimary(String name, WeaponType weaponType) {
        this.name = name;
        this.key = new NamespacedKey(RpgCraft.name(), "skill/" + name);
        this.weaponType = weaponType;
    }

    @Override
    public AttributeCategory getCategory() {
        return AttributeCategory.PRIMARY;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Component toComponent() {
        return Component.text(name).color(NamedTextColor.YELLOW);
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }
}
