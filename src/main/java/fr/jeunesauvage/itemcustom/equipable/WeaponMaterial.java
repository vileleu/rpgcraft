package fr.jeunesauvage.itemcustom.equipable;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import net.kyori.adventure.text.Component;

public enum WeaponMaterial implements EquipableMaterial {
    HAND("hand", Material.AIR, Set.of(ClassType.BEGGAR)),
    CLAW("claw", Material.NETHERITE_SWORD, Set.of(ClassType.DRACTHYR)),
    SWORD("sword", Material.NETHERITE_SWORD, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
    AXE("axe", Material.NETHERITE_AXE, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
	PICKAXE("pickaxe", Material.NETHERITE_PICKAXE, Set.of(ClassType.BEGGAR)),
	HOE("hoe", Material.NETHERITE_HOE, Set.of(ClassType.BEGGAR)),
	SHOVEL("shovel", Material.NETHERITE_SHOVEL, Set.of(ClassType.BEGGAR)),
	TRIDENT("trident", Material.TRIDENT, Set.of(ClassType.BEGGAR)),
    MACE("mace", Material.MACE, Set.of(ClassType.ROGUE, ClassType.WARRIOR)),
	BOW("bow", Material.BOW, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
	CROSSBOW("crossbow", Material.CROSSBOW, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
    STAFF("staff", Material.BOW, Set.of(ClassType.PRIEST, ClassType.PYROMANCER)),
    SPELLBOOK("spellbook", Material.CROSSBOW, Set.of(ClassType.PRIEST, ClassType.PYROMANCER)),
    SHIELD("shield", Material.SHIELD, Set.of(ClassType.DRACTHYR, ClassType.WARRIOR)),
	UNKNOWN("unknown", Material.AIR, Set.of(ClassType.BEGGAR));

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
