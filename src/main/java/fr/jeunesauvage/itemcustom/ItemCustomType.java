package fr.jeunesauvage.itemcustom;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import net.kyori.adventure.text.Component;

public sealed interface ItemCustomType permits EquipableType, SpellType, PotionType, FoodType {
	String				getName();
	Set<ClassType>		getClassTypes();
	Material			getMaterial();
	ItemCustomCategory	getCategory();
	Component			toComponent();
}
