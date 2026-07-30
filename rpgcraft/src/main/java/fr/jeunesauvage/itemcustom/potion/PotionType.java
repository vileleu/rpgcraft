package fr.jeunesauvage.itemcustom.potion;

import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Material;

import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import fr.jeunesauvage.itemcustom.Rarity;
import net.kyori.adventure.text.Component;

public enum PotionType implements ItemCustomType {
	// potion
    POTION_HEALTH("potion_health", Material.POTION, Color.RED, null, 0, 10, 30),
    POTION_MANA("potion_mana", Material.POTION, Color.BLUE, null, 0, 10, 30),
    POTION_RAGE("potion_rage", Material.POTION, Color.RED, null, 0, 10, 30),
	POTION_ENERGY("potion_energy", Material.POTION, Color.YELLOW, null, 0, 10, 30);

	private final String			name;
	private final Set<ClassType>	classTypes;
	private final Material			material;
	private final Color				color;
	private final int				level;
	private final int				changeLevel;
	private final int				cooldown;

	PotionType(String name, Material material, Color color, Set<ClassType> classTypes, int level, int CLevel, int cooldown) {
		this.name = name;
		this.material = material;
		this.color = color;
		this.classTypes = classTypes;
		this.level = level;
		this.changeLevel = CLevel;
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
	public Set<ClassType> getClassTypes() {
		return classTypes;
	}

    @Override
    public ItemCustomCategory getCategory() {
        return ItemCustomCategory.POTION;
    }

	@Override
	public Component toComponent() {
		return Component.text("type.rpgcraft.potion");
	}

	public int getLevel(Rarity rarity) {
		return level + changeLevel * (rarity.getNumber() - 1);
	}

	public int getCooldown() {
		return cooldown;
	}
}