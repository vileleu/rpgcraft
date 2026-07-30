package fr.jeunesauvage.itemcustom.equipable.armor;

import java.util.Set;

import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;
import net.kyori.adventure.text.Component;

public enum ArmorMaterial implements EquipableMaterial {
    CLOTH("cloth", Set.of(ClassType.BEGGAR)),
    LEATHER("leather", Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
	MAIL("mail", Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.WARRIOR)),
    PLATE("plate", Set.of(ClassType.DRACTHYR, ClassType.WARRIOR)),
    UNKNOWN("unknown", Set.of(ClassType.BEGGAR));

    private final String			name;
    private final Set<ClassType>	classTypes;

    ArmorMaterial(String name, Set<ClassType> classTypes) {
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
