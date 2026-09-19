package fr.jeunesauvage.itemcustom.food;

import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import net.kyori.adventure.text.Component;

public enum FoodType implements ItemCustomType {
	// potion
    RABBIT_STEW("rabbit_stew", Material.RABBIT_STEW, Color.WHITE, null, 10),
    GOLDEN_CARROT("golden_carrot", Material.GOLDEN_CARROT, Color.WHITE, null, 10),
    COOKED_BEEF("cooked_beef", Material.COOKED_BEEF, Color.WHITE, null, 10),
    COOKED_SALMON("cooked_salmon", Material.COOKED_SALMON, Color.WHITE, null, 10),
    COOKED_CHICKEN("cooked_chicken", Material.COOKED_CHICKEN, Color.WHITE, null, 10),
    MUSHROOM_STEW("mushroom_stew", Material.MUSHROOM_STEW, Color.WHITE, null, 10),
    COOKED_RABBIT("cooked_rabbit", Material.COOKED_RABBIT, Color.WHITE, null, 10),
    BAKED_POTATO("baked_potato", Material.BAKED_POTATO, Color.WHITE, null, 10),
    CARROT("carrot", Material.CARROT, Color.WHITE, null, 10),
    APPLE("apple", Material.APPLE, Color.WHITE, null, 10),
    POTATO("potato", Material.POTATO, Color.WHITE, null, 10),
    BREAD("bread", Material.BREAD, Color.WHITE, null, 10),
    SALMON("salmon", Material.SALMON, Color.WHITE, null, 10),
    COOKIE("cookie", Material.COOKIE, Color.WHITE, null, 10);

	private final String			name;
	private final Material			material;
	private final Color				color;
    private final Set<ClassType>    classTypes;
	private final int				cooldown;

	FoodType(String name, Material material, Color color, Set<ClassType> classTypes, int cooldown) {
		this.name = name;
		this.material = material;
		this.color = color;
        this.classTypes = classTypes;
		this.cooldown = cooldown;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Material getMaterial() {
		return material;
	}

	public Color getColor() {
		return color;
	}

    @Override
    public ItemCustomCategory getCategory() {
        return ItemCustomCategory.FOOD;
    }

    @Override
    public Set<ClassType> getClassTypes() {
        return classTypes;
    }

	@Override
	public Component toComponent() {
		return Component.text("type.rpgcraft.food");
	}

	public int getCooldown() {
		return cooldown;
	}

	public static FoodType fromString(String name) {
		if (name == null) return null;
		for (FoodType foodType: FoodType.values()) {
			if (foodType.getName().equals(name)) return foodType;
		}
		return null;
	}
}
