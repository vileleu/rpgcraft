package fr.jeunesauvage.itemcustom.equipable.weapon;

import java.util.Set;

import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;
import net.kyori.adventure.text.Component;

public enum WeaponMaterial implements EquipableMaterial {
    HAND("hand", Set.of(ClassType.BEGGAR)),
    CLAW("claw", Set.of(ClassType.DRACTHYR)),
    SWORD("sword", Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
    AXE("axe", Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
	PICKAXE("pickaxe", Set.of(ClassType.BEGGAR)),
	HOE("hoe", Set.of(ClassType.BEGGAR)),
	SHOVEL("shovel", Set.of(ClassType.BEGGAR)),
	TRIDENT("trident", Set.of(ClassType.BEGGAR)),
    MACE("mace", Set.of(ClassType.ROGUE, ClassType.WARRIOR)),
	BOW("bow", Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
	CROSSBOW("crossbow", Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
    STAFF("staff", Set.of(ClassType.PRIEST, ClassType.PYROMANCER)),
    SPELLBOOK("spellbook", Set.of(ClassType.PRIEST, ClassType.PYROMANCER)),
    SHIELD("shield", Set.of(ClassType.DRACTHYR, ClassType.WARRIOR)),
	UNKNOWN("unknown", Set.of(ClassType.BEGGAR));

    private final String			name;
    private final Set<ClassType>	classTypes;

    WeaponMaterial(String name, Set<ClassType> classTypes) {
        this.name = name;
		this.classTypes = classTypes;
    }

	@Override
	public String getName() {
		return name;
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
