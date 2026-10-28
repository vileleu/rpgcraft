package fr.jeunesauvage.itemcustom.equipable;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import net.kyori.adventure.text.Component;

public sealed interface EquipableMaterial permits WeaponMaterial, ArmorMaterial {
	String 			getName();
	Material 		getMaterial();
	Set<ClassType>	getClassTypes();
    Component		toComponent();
}
