package fr.jeunesauvage.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Msg {
	public static final MiniMessage MESSAGE = MiniMessage.miniMessage();

	public static Component msg(String message, TagResolver... resolvers) {
		return MESSAGE.deserialize(message, resolvers);
	}

	public static TagResolver text(String name, Object value) {
		return TagResolver.resolver(name, Tag.inserting(Component.text(String.valueOf(value))));
	}

	public static TagResolver tran(String name, String value) {
		return TagResolver.resolver(name, Tag.inserting(Component.translatable(value)));
	}

	public static TagResolver comp(String name, Component value) {
		return TagResolver.resolver(name, Tag.inserting(value));
	}
}
