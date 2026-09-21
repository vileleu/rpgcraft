package fr.jeunesauvage.entitycustom;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public class EntityCustomCommand implements CommandExecutor {
    // handle playercustom commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return switch (cmd.getName().toLowerCase()) {
            case "menu" -> handleMenuCommand(sender, args);
            case "menusell" -> handleMenuSell(sender, args);
            default -> false;
		};
    }

    // open menu command
    private boolean handleMenuCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length > 1) {
            sender.sendMessage(Message.m("<red>Usage: /menu <player name/UUID>"));
            return true;
        }
        PlayerCustom        launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>launcher do not exist (CRITICAL ERROR)"));
            return true;
        }
        LivingEntityCustom  target = null;
        if (args.length == 1) {
            Player        tmpPlayer = Bukkit.getPlayer(args[0]);
            if (tmpPlayer == null) {
                if (!(Bukkit.getEntity(UUID.fromString(args[0])) instanceof LivingEntity tmpEntity)) {
                    sender.sendMessage(Message.m("<red>entity: <yellow>" + args[0] + "<red> is unknown"));
                    return true;
                }
                target = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(tmpEntity.getUniqueId());
            }
            else
                target = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(tmpPlayer.getUniqueId());
        }
        else
            target = launcher;
        RpgCraft.getEntityCustomRegistry().addMenuCommand(launcher, target);
        return true;
    }

    // open menu sell
    private boolean handleMenuSell(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Message.m("<red>Usage: /menusell <player name>"));
            return true;
        }
        Player  p = Bukkit.getPlayer(args[0]);
        if (p == null) {
            sender.sendMessage(Message.m("<red>entity: <yellow>" + args[0] + "<red> is unknown"));
            return true;
        }
        PlayerCustom        launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>launcher do not exist (CRITICAL ERROR)"));
            return true;
        }
        RpgCraft.getEntityCustomRegistry().addMenuSell(launcher);
        return true;
    }
}