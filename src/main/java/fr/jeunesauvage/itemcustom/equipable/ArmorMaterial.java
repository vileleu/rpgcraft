package fr.jeunesauvage.itemcustom.equipable;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import net.kyori.adventure.text.Component;

public enum ArmorMaterial implements EquipableMaterial {
    CLOTH("cloth", Material.STRING, Set.of(ClassType.BEGGAR)),
    LEATHER("leather", Material.LEATHER, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.ROGUE, ClassType.WARRIOR)),
	MAIL("mail", Material.CHAIN, Set.of(ClassType.DRACTHYR, ClassType.HUNTER, ClassType.WARRIOR)),
    PLATE("plate", Material.ANVIL, Set.of(ClassType.DRACTHYR, ClassType.WARRIOR)),
    ELYTRA("elytra", Material.ELYTRA, Set.of(ClassType.DRACTHYR)),
    UNKNOWN("unknown", Material.AIR, Set.of(ClassType.BEGGAR));

    private final String			name;
    private final Material			material;
    private final Set<ClassType>	classTypes;

    ArmorMaterial(String name, Material material, Set<ClassType> classTypes) {
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
