package fr.jeunesauvage.entity.group;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupManager {
	private final Set<Group>		groups = new HashSet<>();
	private int						id = 1;

	public GroupManager(JavaPlugin plugin) {
		// commands
        GroupCommand	groupCommand = new GroupCommand(this);
        plugin.getCommand("group").setExecutor(groupCommand);
        plugin.getCommand("groupaccept").setExecutor(groupCommand);
        plugin.getCommand("groupdecline").setExecutor(groupCommand);
	}
	
	public void createGroup(Player player1, Player player2) {
		if (Group.hasGroup(player1) || Group.hasGroup(player2)) return;
		Group	group = new Group(id++);
		group.addInGroup(player1);
		group.addInGroup(player2);
		groups.add(group);
	}

	public void removeGroup(Player player) {
		if (!Group.hasGroup(player)) return;
		for (Group group: groups) {
			if (!group.isInGroup(player)) continue;
			int	size = group.removeFromGroup(player);
			if (size <= 1) {
				group.clean();
				groups.remove(group);
			}
		}
	}

	public Player getAlly(Player player) {
		if (!Group.hasGroup(player)) return null;
		for (Group group: groups) {
			if (!group.isInGroup(player)) continue;
			Player	ally = group.getAlly(player);
			if (ally == null) {
				group.clean();
				groups.remove(group);
				return null;
			}
			return ally;
		}
		return null;
	}
}
