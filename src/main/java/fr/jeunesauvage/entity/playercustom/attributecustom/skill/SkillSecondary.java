package fr.jeunesauvage.entity.playercustom.attributecustom.skill;

import org.bukkit.NamespacedKey;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum SkillSecondary implements SkillType {
	TEMPERING("tempering", WeaponType.UNKNOWN),
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
    private final WeaponType    weaponType;
    private final NamespacedKey key;

    private SkillSecondary(String name, WeaponType weaponType) {
        this.name = name;
        this.weaponType = weaponType;
        this.key = new NamespacedKey(RpgCraft.name(), "skillsecondary_" + name);
    }

    @Override
    public AttributeCategory getCategory() {
        return AttributeCategory.SECONDARY;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
	public Component toComponent() {
		return Component.translatable("skill.rpgcraft." + name).decorate(TextDecoration.BOLD);
	}

    @Override
	public TextColor getColor() {
		return NamedTextColor.AQUA;
	}

    public static int getAmount(PlayerCustom playerCustom, SkillSecondary skillType) {
        Skill   skill = playerCustom.getSkill(skillType);
        return skill.getValue() + skill.getValueBonus();
    }
}
