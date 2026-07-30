package fr.jeunesauvage.sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.component.Msg;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;

public class SoundCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "quote":
                return handleQuote(sender, args);
            case "quotegreeting":
                return handleGreeting(sender, args);
            case "quotefarewell":
                return handleFarewell(sender, args);
            case "quoteattack":
                return handleAttack(sender, args);
            case "quotedeath":
                return handleDeath(sender, args);
		}
		return false;
    }

	private boolean handleQuote(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /quote"));
            return true;
        }
        player.sendMessage(Message.greeting().clickEvent(ClickEvent.runCommand("/quotegreeting")).decorate(TextDecoration.UNDERLINED));
        player.sendMessage(Message.farewell().clickEvent(ClickEvent.runCommand("/quotefarewell")).decorate(TextDecoration.UNDERLINED));
        player.sendMessage(Message.attack().clickEvent(ClickEvent.runCommand("/quoteattack")).decorate(TextDecoration.UNDERLINED));
        player.sendMessage(Message.death().clickEvent(ClickEvent.runCommand("/quotedeath")).decorate(TextDecoration.UNDERLINED));
        return true;
    }

	private boolean handleGreeting(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /quotegreeting"));
            return true;
        }
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		SoundManager.playQuote(playerCustom, QuoteType.GREETING);
        return true;
    }

	private boolean handleFarewell(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /quotefarewell"));
            return true;
        }
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		SoundManager.playQuote(playerCustom, QuoteType.FAREWELL);
        return true;
    }

	private boolean handleAttack(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /quoteattack"));
            return true;
        }
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		SoundManager.playQuote(playerCustom, QuoteType.ATTACK);
        return true;
    }

	private boolean handleDeath(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /quotedeath"));
            return true;
        }
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		SoundManager.playQuote(playerCustom, QuoteType.DEATH);
        return true;
    }
}
