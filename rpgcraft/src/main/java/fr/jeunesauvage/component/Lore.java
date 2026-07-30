package fr.jeunesauvage.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import fr.jeunesauvage.itemcustom.Rarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Lore {
	public static Component nameEquipable(String name, Rarity rarity) {
		return Component.translatable("item.rpgcraft." + name).color(rarity.getColor()).decorate(TextDecoration.BOLD);
	}

	public static Component namePotion(String name, Rarity rarity) {
		name = name.replaceAll("_\\d+$", "");
		return Component.translatable("potion.rpgcraft." + name).color(rarity.getColor()).decorate(TextDecoration.BOLD);
	}

	public static Component nameSpell(String name, Rarity rarity) {
		name = name.replaceAll("_\\d+$", "");
		return Component.translatable("spell.rpgcraft." + name).color(rarity.getColor()).decorate(TextDecoration.BOLD);
	}

	public static Component type(ItemCustomType itemCustomType) {
		TextColor	colorName = NamedTextColor.GRAY;
		TextColor	colorValue = NamedTextColor.WHITE;
		return Component.translatable("type.rpgcraft").color(colorName).decorate(TextDecoration.BOLD)
			.append(Component.text(" ")).append(itemCustomType.toComponent().color(colorValue));
	}

	public static Component rarity(Rarity rarity) {
		TextColor	colorName = NamedTextColor.GRAY;
		return Component.translatable("rarity.rpgcraft").color(colorName).decorate(TextDecoration.BOLD)
			.append(Component.text(" ")).append(rarity.toComponent().color(rarity.getColor()));
	}

	public static Component level(int level) {
		TextColor	colorName = NamedTextColor.GRAY;
		TextColor	colorValue = NamedTextColor.WHITE;
		return Component.translatable("level.rpgcraft").color(colorName).decorate(TextDecoration.BOLD)
			.append(Component.text(" " + level).color(colorValue));
	}

	public static Component stat(StatType type, int value) {
		TextColor	colorValue = value < 0 ? NamedTextColor.RED : NamedTextColor.WHITE;
		return type.toComponent().color(type.getColor()).append(Component.text(" " + value).color(colorValue));
	}

	public static Component skill(SkillType type, int value) {
		TextColor	colorValue = value < 0 ? NamedTextColor.RED : NamedTextColor.WHITE;
		return type.toComponent().color(type.getColor()).append(Component.text(" " + value).color(colorValue));
	}

	public static Component classType(Set<ClassType> classTypes) {
		TextColor	colorName = NamedTextColor.GRAY;
		Component	component = Component.translatable("class.rpgcraft").color(colorName).decorate(TextDecoration.BOLD);
		for (ClassType classType: classTypes) {
			component = component.append(Component.text(" "));
			component = component.append(classType.toComponent().color(classType.getColor()));
		}
		return component;
	}

	public static List<Component> classType(ClassType classType) {
		List<Component>	lore = new ArrayList<>();
		TextColor		colorName = NamedTextColor.GRAY;
		Component		component = Component.translatable("class.rpgcraft").color(colorName).decorate(TextDecoration.BOLD);
		component = component.append(Component.text(" ")).append(classType.toComponent().color(classType.getColor()));
		lore.add(component);
		return lore;
	}

	public static Component raceType(Set<RaceType> raceTypes) {
		TextColor	colorName = NamedTextColor.GRAY;
		Component	component = Component.translatable("race.rpgcraft").color(colorName).decorate(TextDecoration.BOLD);
		for (RaceType raceType: raceTypes) {
			component = component.append(Component.text(" "));
			component = component.append(raceType.toComponent().color(raceType.getColor()));
		}
		return component;
	}

	public static List<Component> raceType(RaceType raceType) {
		List<Component>	lore = new ArrayList<>();
		TextColor		colorName = NamedTextColor.GRAY;
		Component		component = Component.translatable("race.rpgcraft").color(colorName).decorate(TextDecoration.BOLD);
		component = component.append(Component.text(" ")).append(raceType.toComponent().color(raceType.getColor()));
		lore.add(component);
		return lore;
	}

	public static Component description(String name) {
		TextColor	colorName = NamedTextColor.GRAY;
		TextColor	colorValue = NamedTextColor.WHITE;
		name = name.replaceAll("_\\d+$", "");
		return Component.translatable("description.rpgcraft").color(colorName).decorate(TextDecoration.BOLD)
			.append(Component.text(" ")).append(Component.translatable("description.rpgcraft." + name).color(colorValue));
	}
}
