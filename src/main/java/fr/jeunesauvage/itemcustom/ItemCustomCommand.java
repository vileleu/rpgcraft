package fr.jeunesauvage.itemcustom;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.food.Food;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.spell.Spell;

public class ItemCustomCommand implements CommandExecutor {
    // handle npc builder commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "giveequipable":
                return handleGiveEquipable(sender, args);
            case "givespell":
                return handleGiveSpell(sender, args);
            case "givepotion":
                return handleGivePotion(sender, args);
            case "givefood":
                return handleGiveFood(sender, args);
		}
		return false;
    }

	private boolean handleGiveEquipable(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Message.m("<red>Usage: /giveequipable <player name> <item id>"));
            return true;
        }
		String	playername = args[0];
		Player	p = Bukkit.getPlayer(playername);
		if (p == null) {
			sender.sendMessage(Message.m("<red>player: <yellow>" + playername + "<red> is invalid"));
            return true;
		}
        PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>launcher do not exist (CRITICAL ERROR)"));
            return true;
        }
		String  		itemName = args[1];
		Equipable<?>	equipable = RpgCraft.getItemCustomRegistry().getEquipable(itemName);
		if (equipable == null) {
			sender.sendMessage(Message.m("<red>identifier: <yellow>" + itemName + "<red> is invalid"));
            return true;
		}
		launcher.addItem(equipable.getItemClone());
		return true;
	}

	private boolean handleGiveSpell(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Message.m("<red>Usage: /givespell <player name> <item id>"));
            return true;
        }
		String	playername = args[0];
		Player	p = Bukkit.getPlayer(playername);
		if (p == null) {
			sender.sendMessage(Message.m("<red>player: <yellow>" + playername + "<red> is invalid"));
            return true;
		}
        PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>launcher do not exist (CRITICAL ERROR)"));
            return true;
        }
		String	itemName = args[1];
		Spell	spell = RpgCraft.getItemCustomRegistry().getSpell(itemName);
		if (spell == null) {
			sender.sendMessage(Message.m("<red>identifier: <yellow>" + itemName + "<red> is invalid"));
            return true;
		}
		launcher.addItem(spell.getItemClone());
		return true;
	}

	private boolean handleGivePotion(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Message.m("<red>Usage: /givepotion <player name> <item id>"));
            return true;
        }
		String	playername = args[0];
		Player	p = Bukkit.getPlayer(playername);
		if (p == null) {
			sender.sendMessage(Message.m("<red>player: <yellow>" + playername + "<red> is invalid"));
            return true;
		}
        PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>launcher do not exist (CRITICAL ERROR)"));
            return true;
        }
		String	itemName = args[1];
		Potion	potion = RpgCraft.getItemCustomRegistry().getPotion(itemName);
		if (potion == null) {
			sender.sendMessage(Message.m("<red>identifier: <yellow>" + itemName + "<red> is invalid"));
            return true;
		}
		launcher.addItem(potion.getItemClone());
		return true;
	}

	private boolean handleGiveFood(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Message.m("<red>Usage: /givefood <player name> <item id>"));
            return true;
        }
		String	playername = args[0];
		Player	p = Bukkit.getPlayer(playername);
		if (p == null) {
			sender.sendMessage(Message.m("<red>player: <yellow>" + playername + "<red> is invalid"));
            return true;
		}
        PlayerCustom	launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>launcher do not exist (CRITICAL ERROR)"));
            return true;
        }
		String	itemName = args[1];
		Food	food = RpgCraft.getItemCustomRegistry().getFood(itemName);
		if (food == null) {
			sender.sendMessage(Message.m("<red>identifier: <yellow>" + itemName + "<red> is invalid"));
            return true;
		}
		launcher.addItem(food.getItemClone());
		return true;
	}
}
