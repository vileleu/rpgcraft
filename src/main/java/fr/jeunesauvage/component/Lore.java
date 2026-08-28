package fr.jeunesauvage.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import fr.jeunesauvage.itemcustom.Rarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Lore {
	public static Component nameEquipable(String name, Rarity rarity) {
		return Message.c(Component.translatable("item.rpgcraft." + name), rarity.getColor());
	}

	public static Component namePotion(String name, Rarity rarity) {
		name = name.replaceAll("_\\d+$", "");
		return Message.c(Component.translatable("potion.rpgcraft." + name), rarity.getColor());
	}

	public static Component nameSpell(String name, Rarity rarity) {
		name = name.replaceAll("_\\d+$", "");
		return Message.c(Component.translatable("spell.rpgcraft." + name), rarity.getColor());
	}

	public static Component type(ItemCustomType itemCustomType) {
		TextColor	colorName = NamedTextColor.GRAY;
		TextColor	colorValue = NamedTextColor.WHITE;
		return Message.c(Component.translatable("type.rpgcraft").color(colorName).append(Component.text(" ")).append(itemCustomType.toComponent().color(colorValue)));
	}

	public static Component rarity(Rarity rarity) {
		TextColor	colorName = NamedTextColor.GRAY;
		return Message.c(Component.translatable("rarity.rpgcraft").color(colorName).append(Component.text(" ")).append(rarity.toComponent()));
	}

	public static Component level(int level) {
		TextColor	colorName = NamedTextColor.GRAY;
		TextColor	colorValue = NamedTextColor.WHITE;
		return Message.c(Component.translatable("level.rpgcraft").color(colorName).append(Component.text(" " + level).color(colorValue)));
	}

	public static Component cost(int cost) {
		TextColor	colorName = NamedTextColor.GRAY;
		TextColor	colorValue = NamedTextColor.WHITE;
		return Message.c(Component.translatable("cost.rpgcraft").color(colorName).append(Component.text(" " + cost).color(colorValue)));
	}

	public static Component stat(StatType type, int value) {
		TextColor	colorValue = value < 0 ? NamedTextColor.RED : NamedTextColor.WHITE;
		return Message.c(type.toComponent().append(Component.text(" " + value).color(colorValue)));
	}

	public static Component skill(SkillType type, int value) {
		TextColor	colorValue = value < 0 ? NamedTextColor.RED : NamedTextColor.WHITE;
		return Message.c(type.toComponent().append(Component.text(" " + value).color(colorValue)));
	}

	public static Component classType(Set<ClassType> classTypes) {
		TextColor	colorName = NamedTextColor.GRAY;
		Component	component = Component.translatable("class.rpgcraft").color(colorName);
		for (ClassType classType: classTypes) {
			component = component.append(Component.text(" "));
			component = component.append(classType.toComponent());
		}
		return Message.c(component);
	}

	public static List<Component> classType(ClassType classType) {
		List<Component>	lore = new ArrayList<>();
		TextColor		colorName = NamedTextColor.GRAY;
		Component		component = Component.translatable("class.rpgcraft").color(colorName).append(Component.text(" ")).append(classType.toComponent());
		lore.add(Message.c(component));
		return lore;
	}

	public static Component raceType(Set<RaceType> raceTypes) {
		TextColor	colorName = NamedTextColor.GRAY;
		Component	component = Component.translatable("race.rpgcraft").color(colorName);
		for (RaceType raceType: raceTypes) {
			component = component.append(Component.text(" "));
			component = component.append(raceType.toComponent());
		}
		return Message.c(component);
	}

	public static List<Component> raceType(RaceType raceType) {
		List<Component>	lore = new ArrayList<>();
		TextColor		colorName = NamedTextColor.GRAY;
		Component		component = Component.translatable("race.rpgcraft").color(colorName).append(Component.text(" ")).append(raceType.toComponent());
		lore.add(Message.c(component));
		return lore;
	}

	public static List<Component> team(Set<TeamType> teams) {
		List<Component>	lore = new ArrayList<>();
		teams.forEach(t -> lore.add(Message.c(t.toComponent())));
		return lore;
	}

	public static Component description(String name) {
		TextColor	colorName = NamedTextColor.GRAY;
		TextColor	colorValue = NamedTextColor.WHITE;
		name = name.replaceAll("_\\d+$", "");
		return Message.c(Component.translatable("description.rpgcraft").color(colorName).append(Component.text(" ")).append(Component.translatable("description.rpgcraft." + name).color(colorValue)));
	}
}
