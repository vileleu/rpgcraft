package fr.jeunesauvage.component;

import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Message {
	// message
	public static Component levelMax() {
		return Component.translatable("message.rpgcraft.levelmax").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD);
	}

	public static Component cantUse() {
		return Component.translatable("message.rpgcraft.cantuse").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
	}

	public static Component notEnough(ResourceType type) {
		return switch (type) {
			case ResourceType.MANA -> Component.translatable("message.rpgcraft.notenoughmana").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
			case ResourceType.RAGE -> Component.translatable("message.rpgcraft.notenoughrage").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
			case ResourceType.ENERGY -> Component.translatable("message.rpgcraft.notenoughenergy").color(NamedTextColor.RED).decorate(TextDecoration.BOLD);
			default -> null;
		};
	}

	public static Component dodge() {
		return Component.translatable("message.rpgcraft.dodge").decorate(TextDecoration.BOLD);
	}

	public static Component miss() {
		return Component.translatable("message.rpgcraft.miss").decorate(TextDecoration.BOLD);
	}

	public static Component immune() {
		return Component.translatable("message.rpgcraft.immune").decorate(TextDecoration.BOLD);
	}

	public static Component greeting() {
		return Component.translatable("message.rpgcraft.greeting").decorate(TextDecoration.BOLD);
	}

	public static Component farewell() {
		return Component.translatable("message.rpgcraft.farewell").decorate(TextDecoration.BOLD);
	}

	public static Component attack() {
		return Component.translatable("message.rpgcraft.attack").decorate(TextDecoration.BOLD);
	}

	public static Component death() {
		return Component.translatable("message.rpgcraft.death").decorate(TextDecoration.BOLD);
	}

	public static Component increaseSkill() {
		return Component.translatable("message.rpgcraft.increaseskill").decorate(TextDecoration.BOLD);
	}

	public static Component silence(long timeLeft) {
		return Component.translatable("message.rpgcraft.silence").decorate(TextDecoration.BOLD).color(NamedTextColor.RED)
			.append(Component.text(": "))
			.append(Component.text(timeLeft).color(NamedTextColor.YELLOW))
			.append(Component.text("s"));
	}

	public static Component cooldown(long timeLeft) {
		return Component.translatable("message.rpgcraft.cooldown").decorate(TextDecoration.BOLD).color(NamedTextColor.RED)
			.append(Component.text(": "))
			.append(Component.text(timeLeft).color(NamedTextColor.YELLOW))
			.append(Component.text("s"));
	}

	// npc

	public static Component npcCombat() {
		return Component.translatable("npc.rpgcraft.combat").decorate(TextDecoration.BOLD);
	}

	public static Component npcOccuped() {
		return Component.translatable("npc.rpgcraft.occuped").decorate(TextDecoration.BOLD);
	}

	public static Component npcCantSee() {
		return Component.translatable("npc.rpgcraft.cantsee").decorate(TextDecoration.BOLD);
	}

	public static Component groupAlreadyIn() {
		return Component.translatable("group.rpgcraft.alreadyin").decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW);
	}

	public static Component groupCantAsk() {
		return Component.translatable("group.rpgcraft.cantask").decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW);
	}

	public static Component groupAsk() {
		return Component.translatable("group.rpgcraft.ask").decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN);
	}

	public static Component groupAskSent() {
		return Component.translatable("group.rpgcraft.asksent").decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN);
	}

	public static Component groupAccept() {
		return Component.translatable("group.rpgcraft.accept").decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN);
	}

	public static Component groupCantAccept() {
		return Component.translatable("group.rpgcraft.cantaccept").decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW);
	}

	public static Component groupDecline() {
		return Component.translatable("group.rpgcraft.decline").decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW);
	}

	public static Component groupAccepted() {
		return Component.translatable("group.rpgcraft.accepted").decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN);
	}

	public static Component groupDeclined() {
		return Component.translatable("group.rpgcraft.declined").decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW);
	}

	public static Component groupPrint() {
		return Component.translatable("group.rpgcraft.print").decorate(TextDecoration.BOLD).color(NamedTextColor.GREEN);
	}

	public static Component noGroup() {
		return Component.translatable("group.rpgcraft.nogroup").decorate(TextDecoration.BOLD).color(NamedTextColor.YELLOW);
	}
}
