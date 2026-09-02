package fr.jeunesauvage.entitycustom.livingentitycustom.group;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.DataTask;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GroupCommand implements CommandExecutor {
	private final int						TIME_GROUPASK = 10;
	private final Map<UUID, DataTask<UUID>>	asks = new HashMap<>();

	public GroupCommand() {
		new BukkitRunnable() {
		    @Override
		    public void run() {
				for (PlayerCustom playerCustom: RpgCraft.getEntityCustomRegistry().getPlayerCustoms()) {
					if (playerCustom.hasGroup()) playerCustom.getScoreboardCustom().refreshAlly(playerCustom);
				}
			}
		}.runTaskTimer(RpgCraft.instance(), 0L, 60L);
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
			case "groupquit":
				return handleGroupQuit(sender, args);
		}
		return false;
    }

	private boolean handleGroup(CommandSender sender, String[] args) {
		if (!(sender instanceof Player p1)) return true;
        if (args.length != 0 && args.length != 1) {
            sender.sendMessage(Message.m("<red>Usage: /group <player name>"));
            return true;
        }
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p1.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player do not exist (CRITICAL ERROR)"));
            return true;
        }
		if (!launcher.hasGroup() && args.length == 1) {
			Player	p2 = Bukkit.getPlayer(args[0]);
			if (p2 == null || p2.equals(p1)) {
				sender.sendMessage(Message.m("<red>player: <yellow>" + args[0] + "<red> is invalid"));
        	    return true;
			}
			PlayerCustom	target = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p2.getUniqueId());
        	if (target == null) {
        	    sender.sendMessage(Message.m("<red>target do not exist (CRITICAL ERROR)"));
        	    return true;
        	}
			askGroup(launcher, target);
		}
		else {
			if (launcher.hasGroup()) {
				LivingEntityCustom	ally = launcher.getAlly();
				launcher.sendMessage(Message.groupPrint().append(Component.text(" " + ally.getName()).color(NamedTextColor.DARK_GREEN)));
			}
			else {
				launcher.sendMessage(Message.noGroup());
			}
		}
		return true;
	}

	private boolean hasInvitation(PlayerCustom launcher) {
		for(DataTask<UUID> dataTask: asks.values()) {
			if (dataTask.getData().equals(launcher.getUUID())) return true;
		}
		return false;
	}

	// launch task who check group demand (10 seconds)
	private void askGroup(PlayerCustom launcher, PlayerCustom target) {
		if (launcher.hasGroup() || target.hasGroup()) {
			launcher.sendMessage(Message.groupAlreadyIn());
			return;
		}
		final UUID	uuidTarget = target.getUUID();
		final UUID	uuidLauncher = launcher.getUUID();
		if (asks.containsKey(uuidTarget) || hasInvitation(launcher)) {
			launcher.sendMessage(Message.groupCantAsk());
			return;
		}
		DataTask<UUID>	dataTask = new DataTask<UUID>(uuidLauncher, Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
			PlayerCustom	t = RpgCraft.getEntityCustomRegistry().getPlayerCustom(uuidTarget);
			if (t != null) t.sendMessage(Message.groupDeclined());
			PlayerCustom	l = RpgCraft.getEntityCustomRegistry().getPlayerCustom(uuidLauncher);
			if (l != null) l.sendMessage(Message.groupDeclined());
			asks.remove(uuidTarget);
		}, Data.d(TIME_GROUPASK)));
		asks.put(uuidTarget, dataTask);
		launcher.sendMessage(Message.groupAskSent());
		target.sendMessage(Message.groupAsk().append(Component.text(" " + launcher.getName()).color(NamedTextColor.DARK_GREEN)));
        target.sendMessage(Message.u(Message.groupAccept(), "/groupaccept"));
        target.sendMessage(Message.u(Message.groupDecline(), "/groupdecline"));
	}

	private boolean handleGroupAccept(CommandSender sender, String[] args) {
		if (!(sender instanceof Player p)) return true;
        if (args.length != 0) {
            sender.sendMessage(Message.m("<red>Usage: /groupaccept"));
            return true;
        }
		PlayerCustom	target = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (target == null) {
            sender.sendMessage(Message.m("<red>player do not exist (CRITICAL ERROR)"));
            return true;
        }
		acceptGroup(target);
		return true;
	}

	private void deleteAsk(PlayerCustom target) {
		asks.computeIfPresent(target.getUUID(), (u, d) -> {
			d.cancel();
			return null;
		});
	}

	private void acceptGroup(PlayerCustom target) {
		UUID			uuidTarget = target.getUUID();
		DataTask<UUID>	dataTask = asks.get(uuidTarget);
		if (dataTask == null) {
			target.sendMessage(Message.groupCantAccept());
			return;
		}
		UUID			uuidLauncher = dataTask.getData();
		if (uuidLauncher == null) {
			deleteAsk(target);
			target.sendMessage(Message.groupCantAccept());
			return;
		}
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(uuidLauncher);
		if (launcher == null) {
			deleteAsk(target);
			target.sendMessage(Message.groupCantAccept());
			return;
		}
		if (launcher.hasGroup()) {
			deleteAsk(target);
			target.sendMessage(Message.groupAlreadyIn());
			return;
		}
		deleteAsk(target);
		launcher.createGroup(target);
        target.sendMessage(Message.groupAccepted());
        launcher.sendMessage(Message.groupAccepted());
	}

	private boolean handleGroupDecline(CommandSender sender, String[] args) {
		if (!(sender instanceof Player p)) return true;
        if (args.length != 0) {
            sender.sendMessage(Message.m("<red>Usage: /groupdecline"));
            return true;
        }
		PlayerCustom	target = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (target == null) {
            sender.sendMessage(Message.m("<red>player do not exist (CRITICAL ERROR)"));
            return true;
        }
		declineGroup(target);
		return true;
	}

	private void declineGroup(PlayerCustom target) {
		UUID			uuidTarget = target.getUUID();
		DataTask<UUID>	dataTask = asks.get(uuidTarget);
		if (dataTask == null) return;
		target.sendMessage(Message.groupDeclined());
		UUID			uuidLauncher = dataTask.getData();
		if (uuidLauncher == null) {
			deleteAsk(target);
			return;
		}
		PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(uuidLauncher);
		if (launcher == null) {
			deleteAsk(target);
			return;
		}
		if (launcher.hasGroup()) {
			deleteAsk(target);
			return;
		}
		deleteAsk(target);
        launcher.sendMessage(Message.groupDeclined());
	}

	private boolean handleGroupQuit(CommandSender sender, String[] args) {
		if (!(sender instanceof Player p)) return true;
        if (args.length != 0) {
            sender.sendMessage(Message.m("<red>Usage: /groupquit"));
            return true;
        }
		PlayerCustom	target = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (target == null) {
            sender.sendMessage(Message.m("<red>player do not exist (CRITICAL ERROR)"));
            return true;
        }
		quitGroup(target);
		return true;
	}

	private void quitGroup(PlayerCustom target) {
		if (!target.hasGroup()) {
            target.sendMessage(Message.noGroup());
            return;
		}
		target.deleteGroup();
	}
}
