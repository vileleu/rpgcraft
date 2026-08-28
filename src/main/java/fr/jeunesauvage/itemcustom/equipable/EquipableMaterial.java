package fr.jeunesauvage.itemcustom.equipable;

import java.util.Set;

import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import net.kyori.adventure.text.Component;

public interface EquipableMaterial {
	String 			getName();
	Set<ClassType>	getClassTypes();
    Component		toComponent();
}
