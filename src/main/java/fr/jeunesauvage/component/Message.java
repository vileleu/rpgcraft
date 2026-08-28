package fr.jeunesauvage.component;

import org.bukkit.Location;

import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerType;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Message {
	public static final MiniMessage MESSAGE = MiniMessage.miniMessage();

    // message

	public static Component m(String message, TagResolver... resolvers) {
		return MESSAGE.deserialize(message, resolvers);
	}

	public static Component m(String message) {
		return MESSAGE.deserialize(message);
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

	// scoreboard

	public static Component health(double health, double healthMax) {
		return c(Component.translatable("health.rpgcraft").append(Component.text(" " + (int)health + "/" + (int)healthMax).color(NamedTextColor.RED)));
	}

	public static Component power(PowerCustom power) {
		PowerType	powerType = power.getType();
		return c(Component.translatable("power.rpgcraft." + powerType.getName()).append(Component.text(" " + (int)power.getValue() + "/" + (int)power.getValueMax()).color(powerType.getColor())));
	}

	public static Component raceType(RaceType raceType) {
		return c(Component.translatable("race.rpgcraft").append(Component.text(" ").append(raceType.toComponent())));
	}

	public static Component classType(ClassType classType) {
		return c(Component.translatable("class.rpgcraft").append(Component.text(" ").append(classType.toComponent())));
	}

	public static Component ally(LivingEntityCustom ally) {
		String	name = (ally != null ? ally.getName() : "...");
		if (name.length() > 10) name = name.substring(0, 6) + "...";
		return c(Component.translatable("group.rpgcraft.ally").append(Component.text(" " + name).color(NamedTextColor.GRAY)));
	}

	public static Component allyLocation(LivingEntityCustom ally) {
		if (ally == null) return c(Component.text("  --,--,--"), NamedTextColor.GRAY);
		Location	location = ally.getLocation();
		return c(Component.text(location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ()), NamedTextColor.GRAY);
	}

	// warning

	public static Component levelMax(PlayerCustom playerCustom) {
		String	name = (playerCustom != null ? playerCustom.getName() : "...");
		return c(
            Component.text(name + " ").append(Component.translatable("message.rpgcraft.levelmax")).append(Component.text(LivingEntityCustom.LEVEL_MAX)),
            NamedTextColor.GREEN
        );
	}

	public static Component cantUse() {
		return c(
            Component.translatable("message.rpgcraft.cantuse"),
            NamedTextColor.RED
        );
	}

	public static Component notEnough(PowerType type) {
		return switch (type) {
			case MANA -> c(
                Component.translatable("message.rpgcraft.notenoughmana"),
                NamedTextColor.RED
            );
			case RAGE -> c(
                Component.translatable("message.rpgcraft.notenoughrage"),
                NamedTextColor.RED
            );
			case ENERGY -> c(
                Component.translatable("message.rpgcraft.notenoughenergy"),
                NamedTextColor.RED
            );
		};
	}

	public static Component dodge() {
		return c(Component.translatable("message.rpgcraft.dodge"));
	}

	public static Component miss() {
		return c(Component.translatable("message.rpgcraft.miss"));
	}

	public static Component immune() {
		return c(Component.translatable("message.rpgcraft.immune"));
	}

	public static Component greeting() {
		return c(Component.translatable("message.rpgcraft.greeting"));
	}

	public static Component farewell() {
		return c(Component.translatable("message.rpgcraft.farewell"));
	}

	public static Component attack() {
		return c(Component.translatable("message.rpgcraft.attack"));
	}

	public static Component death() {
		return c(Component.translatable("message.rpgcraft.death"));
	}

	public static Component increaseSkill(Skill skill) {
		if (skill == null) return null;
		SkillType	skillType = skill.getType();
		return c(Component.translatable("message.rpgcraft.increaseskill").append(Component.text(" " + skillType.getName() + " " + skill.getValue())), NamedTextColor.GREEN);
	}

	public static Component silence(long timeLeft) {
        return c(
		    Component.translatable("message.rpgcraft.silence")
			    .append(Component.text(": "))
			    .append(Component.text(timeLeft).color(NamedTextColor.YELLOW))
			    .append(Component.text("s")),
            NamedTextColor.RED
        );
	}

	public static Component cooldown(long timeLeft) {
		return c(
            Component.translatable("message.rpgcraft.cooldown")
			    .append(Component.text(": "))
			    .append(Component.text(timeLeft).color(NamedTextColor.YELLOW))
			    .append(Component.text("s")),
            NamedTextColor.RED
        );
	}

	// npc

	public static Component npcCombat() {
		return c(Component.translatable("npc.rpgcraft.combat"));
	}

	public static Component npcOccuped() {
		return c(Component.translatable("npc.rpgcraft.occuped"));
	}

	public static Component npcCantSee() {
		return c(Component.translatable("npc.rpgcraft.cantsee"));
	}

    // group

	public static Component groupAlreadyIn() {
		return c(
            Component.translatable("group.rpgcraft.alreadyin"),
            NamedTextColor.YELLOW
        );
	}

	public static Component groupCantAsk() {
		return c(
            Component.translatable("group.rpgcraft.cantask"),
            NamedTextColor.YELLOW
        );
	}

	public static Component groupAsk() {
		return c(
            Component.translatable("group.rpgcraft.ask"),
            NamedTextColor.GREEN
        );
	}

	public static Component groupAskSent() {
		return c(
            Component.translatable("group.rpgcraft.asksent"),
            NamedTextColor.GREEN
        );
	}

	public static Component groupAccept() {
		return c(
            Component.translatable("group.rpgcraft.accept"),
            NamedTextColor.GREEN
        );
	}

	public static Component groupCantAccept() {
		return c(
            Component.translatable("group.rpgcraft.cantaccept"),
            NamedTextColor.YELLOW
        );
	}

	public static Component groupDecline() {
		return c(
            Component.translatable("group.rpgcraft.decline"),
            NamedTextColor.YELLOW
        );
	}

	public static Component groupAccepted() {
		return c(
            Component.translatable("group.rpgcraft.accepted"),
            NamedTextColor.GREEN
        );
	}

	public static Component groupDeclined() {
		return c(
            Component.translatable("group.rpgcraft.declined"),
            NamedTextColor.YELLOW
        );
	}

	public static Component groupPrint() {
		return c(
            Component.translatable("group.rpgcraft.print"),
            NamedTextColor.GREEN
        );
	}

	public static Component noGroup() {
		return c(
            Component.translatable("group.rpgcraft.nogroup"),
            NamedTextColor.YELLOW
        );
	}

    // utils

	public static Component c(Component component, TextColor color) {
		return component.color(color).decorate(TextDecoration.BOLD);
	}

	public static Component c(Component component) {
		return component.decorate(TextDecoration.BOLD);
	}

	public static Component c(String text, TextColor color) {
		return Component.text(text).color(color).decorate(TextDecoration.BOLD);
	}

	public static Component c(String text) {
		return Component.text(text).decorate(TextDecoration.BOLD);
	}

	public static Component u(Component component, String command) {
		return component.clickEvent(ClickEvent.runCommand(command)).decorate(TextDecoration.UNDERLINED);
	}

	public static Component u(Component component, String command, TextColor color) {
		return component.clickEvent(ClickEvent.runCommand(command)).decorate(TextDecoration.UNDERLINED).color(color);
	}
}
