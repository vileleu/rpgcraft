package fr.jeunesauvage.entity.playercustom.attributecustom.resource;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public interface Resource {
	ResourceType	getType();
	void			increase(double v);
	void			decrease(double v);
	void			increaseMax(double v);
	void			decreaseMax(double v);
	double			getValue();
	double			getValueMax();
	void			setValue(double v);
	void			setValueMax(double v);
	Component		toComponent();
	TextColor		getColor();
}
