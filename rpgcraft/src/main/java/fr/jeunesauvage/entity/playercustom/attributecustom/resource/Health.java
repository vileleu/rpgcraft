package fr.jeunesauvage.entity.playercustom.attributecustom.resource;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class Health implements Resource {
	private final @NonNull Player	player;
	private final ResourceType		type;
	private double					value;
	private double					valueMax;

	public Health(@NonNull Player p, double v, double vMax) {
		this.player = p;
		this.type = ResourceType.HEALTH;
		this.value = v;
		this.valueMax = vMax;
	}

	@Override
	public ResourceType getType() {
		return type;
	}

	@Override
	public void increase(double v) {
		setValue(value + v);
	}

	@Override
	public void decrease(double v) {
		setValue(value - v);
	}

	@Override
	public void increaseMax(double v) {
		setValueMax(valueMax + v);
	}

	@Override
	public void decreaseMax(double v) {
		setValueMax(valueMax - v);
	}

	private void refresh() {
		value = player.getHealth();
		valueMax = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
	}

	/*
	** getter + setter
	*/

	@Override
	public double getValue() {
		refresh();
		return value;
	}

	@Override
	public double getValueMax() {
		refresh();
		return valueMax;
	}

	@Override
	public void setValue(double v) {
		refresh();
		value = Math.max(Math.min(v, valueMax), 0);
		player.setHealth(Math.min(value, valueMax));
	}

	@Override
	public void setValueMax(double vMax) {
		refresh();
		valueMax = Math.max(vMax, 1);
		double				tmp = valueMax - ResourceManager.HEALTH_DEFAULT;
		AttributeInstance	attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		AttributeModifier	attributeModifier = attributeInstance.getModifier(ResourceType.HEALTH.getKeyMax());
		if (attributeModifier != null) {
			if (attributeModifier.getAmount() == tmp) {
				if (value > valueMax) {
					value = valueMax;
					player.setHealth(value);
				}
				return;
			}
			attributeInstance.removeModifier(attributeModifier);
		}
		if (tmp != 0) 
			attributeInstance.addModifier(new AttributeModifier(ResourceType.HEALTH.getKeyMax(), tmp, AttributeModifier.Operation.ADD_NUMBER));
		if (value > valueMax) {
			value = valueMax;
			player.setHealth(value);
		}
	}

	@Override
	public Component toComponent() {
		return type.toComponent();
	}

	@Override
	public TextColor getColor() {
		return type.getColor();
	}
}
