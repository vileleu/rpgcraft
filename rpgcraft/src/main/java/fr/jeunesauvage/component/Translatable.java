package fr.jeunesauvage.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class Translatable {
	public static Component name() {
		return Component.translatable("name.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component type() {
		return Component.translatable("type.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component rarity() {
		return Component.translatable("rarity.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component level() {
		return Component.translatable("level.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component resource() {
		return Component.translatable("resource.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component stat() {
		return Component.translatable("stat.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component skill() {
		return Component.translatable("stat.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component classType() {
		return Component.translatable("class.rpgcraft").decorate(TextDecoration.BOLD);
	}

	public static Component raceType() {
		return Component.translatable("race.rpgcraft").decorate(TextDecoration.BOLD);
	}
}
