package fr.jeunesauvage.entity.playercustom.classcustom;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;

public class ClassCustom {
	private static final NamespacedKey	KEY_CLASS = new NamespacedKey(RpgCraft.name(), "class");
	private final Player				player;
	private ClassType					classType;

	public ClassCustom(Player player) {
		this.player = player;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		this.classType = ClassType.fromString(Data.getString(pdc, KEY_CLASS));
	}

	/*
	** getter + setter
	*/

	public ClassType getClassType() {
		return classType;
	}

	public String getString() {
		return classType.getName();
	}

	public void setClassCustom(String classString) {
		setClassCustom(ClassType.fromString(classString));
	}

	public void setClassCustom(ClassType type) {
		if (type == null)
			type = ClassType.BEGGAR;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		Data.setString(pdc, KEY_CLASS, type.getName());
		this.classType = type;
	}
}