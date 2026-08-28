package fr.jeunesauvage.world;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jeunesauvage.component.Message;

public class WorldCommand implements CommandExecutor {
    private final WorldManager  worldManager;

    public WorldCommand(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    // handle npc builder commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "cleanentities":
                return handleCleanEntities(sender, args);
		}
		return false;
    }

    private boolean handleCleanEntities(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length != 0) {
            sender.sendMessage(Message.m("<red>Usage: /cleanentities"));
            return true;
        }
        worldManager.cleanEntities();
        sender.sendMessage(Message.m("<green>entities cleaned"));
        return true;
    }
}
