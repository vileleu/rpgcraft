package fr.jeunesauvage.entity.playercustom.attributecustom.resource;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassCustom;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;

public class ResourceManager {
	public final static int	HEALTH_DEFAULT = 100;
	public final static int	LEVEL_MAX = 60;
	public final static int	POWER_DEFAULT = 100;
	private final Player	player;
	private final Health	health;
	private final Level		level;
	private Resource		power;

	public ResourceManager(Player player, ClassCustom classCustom) {
		this.player = player;
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		instance.setBaseValue(HEALTH_DEFAULT);
		this.health = new Health(player, player.getHealth(), instance.getValue());
		this.level = new Level(player, LEVEL_MAX);
		this.power = null;
		loadPower(classCustom.getClassType());
	}

	public void loadPower(ClassType type) {
		removePower();
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		switch (type) {
			case ClassType.BEGGAR -> {}
			case ClassType.WARRIOR -> {
				double	value = Data.getDouble(pdc, ResourceType.RAGE.getKey());
				double	valueMax = POWER_DEFAULT;
				this.power = new Rage(player, value, valueMax);
			}
			case ClassType.ROGUE -> {
				double	value = Data.getDouble(pdc, ResourceType.ENERGY.getKey());
				double	valueMax = POWER_DEFAULT;
				this.power = new Energy(player, value, valueMax);
			}
			default -> {
				double	value = Data.getDouble(pdc, ResourceType.MANA.getKey());
				double	valueMax = POWER_DEFAULT;
				this.power = new Mana(player, value, valueMax);
			}
		}
	}

	private void removePower() {
		if (power == null) return;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		switch (power.getType()) {
			case ResourceType.MANA -> Data.remove(pdc, ResourceType.MANA.getKey());
			case ResourceType.RAGE -> Data.remove(pdc, ResourceType.RAGE.getKey());
			default -> Data.remove(pdc, ResourceType.ENERGY.getKey());
		}
		power = null;
	}

	public void reset() {
		level.setValue(0);
		health.setValueMax(HEALTH_DEFAULT);
		removePower();
	}

	/*
	** getter + setter
	*/

	public Health getHealth() {
		return health;
	}

	public Level getLevel() {
		return level;
	}

	public Resource getPower() {
		return power;
	}
}