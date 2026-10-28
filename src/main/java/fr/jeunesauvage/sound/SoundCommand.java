package fr.jeunesauvage.sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;

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
            player.sendMessage(Message.m("<red>Usage: /quote"));
            return true;
        }
        player.sendMessage(Message.u(Message.greeting(), "/quotegreeting"));
        player.sendMessage(Message.u(Message.farewell(), "/quotefarewell"));
        player.sendMessage(Message.u(Message.attack(), "/quoteattack"));
        player.sendMessage(Message.u(Message.death(), "/quotedeath"));
        return true;
    }

	private boolean handleGreeting(CommandSender sender, String[] args) {
        if (!(sender instanceof LivingEntity l)) return true;
        if (args.length != 0) {
            l.sendMessage(Message.m("<red>Usage: /quotegreeting"));
            return true;
        }
		LivingEntityCustom  livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
        if (livingEntityCustom == null) return true;
		SoundManager.playQuote(livingEntityCustom, QuoteType.GREETING);
        return true;
    }

	private boolean handleFarewell(CommandSender sender, String[] args) {
        if (!(sender instanceof LivingEntity l)) return true;
        if (args.length != 0) {
            l.sendMessage(Message.m("<red>Usage: /quotefarewell"));
            return true;
        }
		LivingEntityCustom  livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
        if (livingEntityCustom == null) return true;
		SoundManager.playQuote(livingEntityCustom, QuoteType.FAREWELL);
        return true;
    }

	private boolean handleAttack(CommandSender sender, String[] args) {
        if (!(sender instanceof LivingEntity l)) return true;
        if (args.length != 0) {
            l.sendMessage(Message.m("<red>Usage: /quoteattack"));
            return true;
        }
		LivingEntityCustom  livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
        if (livingEntityCustom == null) return true;
		SoundManager.playQuote(livingEntityCustom, QuoteType.ATTACK);
        return true;
    }

	private boolean handleDeath(CommandSender sender, String[] args) {
        if (!(sender instanceof LivingEntity l)) return true;
        if (args.length != 0) {
            l.sendMessage(Message.m("<red>Usage: /quotedeath"));
            return true;
        }
		LivingEntityCustom  livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
        if (livingEntityCustom == null) return true;
		SoundManager.playQuote(livingEntityCustom, QuoteType.DEATH);
        return true;
    }
}
