package fr.jeunesauvage.entity.group;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.component.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class GroupCommand implements CommandExecutor {
    private final GroupManager 		groupManager;
	private final Map<UUID, UUID>	asks = new HashMap<>();


    public GroupCommand(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    // handle npc builder commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "group":
                return handleGroup(sender, args);
			case "groupaccept":
				return handleGroupAccept(sender, args);
			case "groupdecline":
				return handleGroupDecline(sender, args);
		}
		return false;
    }

	private boolean handleGroup(CommandSender sender, String[] args) {
		if (!(sender instanceof Player playerSender)) return true;
        if (args.length != 0 && args.length != 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /group <player name>"));
            return true;
        }
		if (args.length == 1) {
			Player	player = Bukkit.getPlayer(args[0]);
			if (player == null || player.equals(playerSender)) {
				sender.sendMessage(Msg.msg("<red>player: <yellow>" + args[0] + "<red> is invalid"));
        	    return true;
			}
			askGroup(playerSender, player);
		}
		else {
			if (Group.hasGroup(playerSender)) {
				Player	ally = groupManager.getAlly(playerSender);
				playerSender.sendMessage(Message.groupPrint().append(Component.text(" " + ally.getName()).color(NamedTextColor.DARK_GREEN)));
			}
			else {
				playerSender.sendMessage(Message.noGroup());
			}
		}
		return true;
	}

	private void askGroup(Player sender, Player target) {
		if (Group.hasGroup(target) || Group.hasGroup(sender)) {
			sender.sendMessage(Message.groupAlreadyIn());
			return;
		}
		UUID	uuidTarget = target.getUniqueId();
		UUID	uuidSender = sender.getUniqueId();
		if (asks.containsKey(uuidTarget) || asks.containsValue(uuidTarget) || asks.containsKey(uuidSender) || asks.containsValue(uuidSender)) {
			sender.sendMessage(Message.groupCantAsk());
			return;
		}
		asks.put(uuidTarget, uuidSender);
		sender.sendMessage(Message.groupAskSent());
		target.sendMessage(Message.groupAsk().append(Component.text(" " + sender.getName()).color(NamedTextColor.DARK_GREEN)));
        target.sendMessage(Message.groupAccept().clickEvent(ClickEvent.runCommand("/groupaccept")).decorate(TextDecoration.UNDERLINED));
        target.sendMessage(Message.groupDecline().clickEvent(ClickEvent.runCommand("/groupdecline")).decorate(TextDecoration.UNDERLINED));
	}

	private boolean handleGroupAccept(CommandSender sender, String[] args) {
		if (!(sender instanceof Player playerSender)) return true;
        if (args.length != 0) {
            sender.sendMessage(Msg.msg("<red>Usage: /groupaccept"));
            return true;
        }
		acceptGroup(playerSender);
		return true;
	}

	private void acceptGroup(Player target) {
		UUID	uuidTarget = target.getUniqueId();
		UUID	uuidSender = asks.get(uuidTarget);
		if (uuidSender == null) {
			target.sendMessage(Message.groupCantAccept());
			return;
		}
		Player	sender = Bukkit.getPlayer(uuidSender);
		if (sender == null) {
			target.sendMessage(Message.groupCantAccept());
			return;
		}
		groupManager.createGroup(target, sender);
        target.sendMessage(Message.groupAccepted());
        sender.sendMessage(Message.groupAccepted());
	}

	private boolean handleGroupDecline(CommandSender sender, String[] args) {
		if (!(sender instanceof Player playerSender)) return true;
        if (args.length != 0) {
            sender.sendMessage(Msg.msg("<red>Usage: /groupdecline"));
            return true;
        }
		declineGroup(playerSender);
		return true;
	}

	private void declineGroup(Player target) {
		UUID	uuidTarget = target.getUniqueId();
		UUID	uuidSender = asks.remove(uuidTarget);
        target.sendMessage(Message.groupDeclined());
		if (uuidSender != null) {
			Player	sender = Bukkit.getPlayer(uuidSender);
			if (sender != null)
        		sender.sendMessage(Message.groupDeclined());
		}
	}
}
