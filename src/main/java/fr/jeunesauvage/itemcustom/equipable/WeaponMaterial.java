package fr.jeunesauvage.itemcustom.equipable;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import net.kyori.adventure.text.Component;

public enum WeaponMaterial implements EquipableMaterial {
    HAND("hand", Material.AIR, Set.of()),
    CLAW("claw", Material.NETHERITE_SWORD, Set.of()),
    SWORD("sword", Material.NETHERITE_SWORD, Set.of()),
    SPEAR("spear", Material.NETHERITE_SPEAR, Set.of()),
    AXE("axe", Material.NETHERITE_AXE, Set.of()),
	PICKAXE("pickaxe", Material.NETHERITE_PICKAXE, Set.of()),
	HOE("hoe", Material.NETHERITE_HOE, Set.of()),
	SHOVEL("shovel", Material.NETHERITE_SHOVEL, Set.of()),
	SHEARS("shears", Material.SHEARS, Set.of()),
	TRIDENT("trident", Material.TRIDENT, Set.of()),
    MACE("mace", Material.MACE, Set.of()),
	BOW("bow", Material.BOW, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
	CROSSBOW("crossbow", Material.CROSSBOW, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
    STAFF("staff", Material.BOW, Set.of()),
    SPELLBOOK("spellbook", Material.CROSSBOW, Set.of()),
    SHIELD("shield", Material.SHIELD, Set.of(ClassType.DRACTHYR, ClassType.WARRIOR)),
	UNKNOWN("unknown", Material.AIR, Set.of());

    private final String			name;
    private final Material			material;
    private final Set<ClassType>	classTypes;

    WeaponMaterial(String name, Material material, Set<ClassType> classTypes) {
        this.name = name;
		this.material = material;
		this.classTypes = classTypes;
    }

	@Override
	public String getName() {
		return name;
	}

	@Override
    public Material getMaterial() {
		return material;
	}

	@Override
	public Set<ClassType> getClassTypes() {
		return classTypes;
	}

	@Override
    public Component toComponent() {
        return Component.translatable("type.rpgcraft." + name);
    }
}
