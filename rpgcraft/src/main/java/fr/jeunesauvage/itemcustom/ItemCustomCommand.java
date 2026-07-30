package fr.jeunesauvage.itemcustom;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.jeunesauvage.component.Msg;
import fr.jeunesauvage.component.Translatable;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.spell.Spell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ItemCustomCommand implements CommandExecutor {
    private final ItemCustomManager itemCustomManager;

    public ItemCustomCommand(ItemCustomManager itemCustomManager) {
        this.itemCustomManager = itemCustomManager;
    }

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
            case "infositem":
                return handleInfosItem(sender, args);
		}
		return false;
    }

	private boolean handleGiveEquipable(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /giveequipable <player name> <item id>"));
            return true;
        }
		Player	player = Bukkit.getPlayer(args[0]);
		if (player == null) {
			sender.sendMessage(Msg.msg("<red>player: <yellow>" + args[0] + "<red> is invalid"));
            return true;
		}
		String  	itemName = args[1];
		ItemStack	item = itemCustomManager.getEquipableClone(itemName);
		if (item == null) {
			sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", itemName)));
            return true;
		}
		player.getInventory().addItem(item);
		return true;
	}

	private boolean handleGiveSpell(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /givespell <player name> <item id>"));
            return true;
        }
		Player	player = Bukkit.getPlayer(args[0]);
		if (player == null) {
			sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", args[0])));
            return true;
		}
		String  	itemName = args[1];
		ItemStack	item = itemCustomManager.getSpellClone(itemName);
		if (item == null) {
			sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", itemName)));
            return true;
		}
		player.getInventory().addItem(item);
		return true;
	}

	private boolean handleGivePotion(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /givepotion <player name> <item id>"));
            return true;
        }
		Player	player = Bukkit.getPlayer(args[0]);
		if (player == null) {
			sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", args[0])));
            return true;
		}
		String  	itemName = args[1];
		ItemStack	item = itemCustomManager.getPotionClone(itemName);
		if (item == null) {
			sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", itemName)));
            return true;
		}
		player.getInventory().addItem(item);
		return true;
	}

	private boolean handleInfosItem(CommandSender sender, String[] args) {
		if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /infositem"));
            return true;
        }
		ItemStack 	item = player.getInventory().getItemInMainHand();
		if (item == null || item.getType().isAir()) {
            player.sendMessage(Msg.msg("<red>no item in hand"));
            return true;
		}
		ItemMeta	meta = item.getItemMeta();
		if (meta == null) {
            player.sendMessage(Msg.msg("<red>item has no data"));
            return true;
		}
		String		identifier = ItemCustom.getIdentifier(item);
		if (identifier == null) {
            player.sendMessage(Msg.msg("<red>item has no identifier"));
            return true;
		}
		ItemCustom<?>	itemCustom = itemCustomManager.getItemCustom(identifier);
		if (itemCustom == null) {
            player.sendMessage(Msg.msg("<red>item <yellow><identifier><red> not found", Msg.text("identifier", identifier)));
            return true;
		}
		if (itemCustom instanceof Equipable<?> equipable) {
        	player.sendMessage(Msg.msg(""));
        	player.sendMessage(Msg.msg("<u>INFOS EQUIPABLE:</u>"));
			Component	component = Translatable.name()
				.append(Component.text(": "))
				.append(equipable.toComponent());
			player.sendMessage(component);
			component = Translatable.type()
				.append(Component.text(": "))
				.append(equipable.getType().toComponent());
			player.sendMessage(component);
			component = Translatable.rarity()
				.append(Component.text(": "))
				.append(equipable.getRarity().toComponent());
			player.sendMessage(component);
			component = Translatable.level()
				.append(Component.text(": "))
				.append(Component.text(equipable.getLevel()));
			player.sendMessage(component);
			player.sendMessage(Translatable.stat());
			Map<StatPrimary, Integer>	statsPrimary = equipable.getStatsPrimary();
			for (Entry<StatPrimary, Integer> entry: statsPrimary.entrySet()) {
				StatPrimary	type = entry.getKey();
				int			value = entry.getValue();
        	    TextColor	color = value < 0 ? NamedTextColor.RED : NamedTextColor.WHITE;
				component = Component.text(" - ").decorate(TextDecoration.BOLD)
					.append(type.toComponent().color(type.getColor()))
					.append(Component.text(": "))
					.append(Component.text(value).color(color));
				player.sendMessage(component);
			}
			Map<StatSecondary, Integer>	statsSecondary = equipable.getStatsSecondary();
			for (Entry<StatSecondary, Integer> entry: statsSecondary.entrySet()) {
				StatSecondary	type = entry.getKey();
				int				value = entry.getValue();
        	    TextColor		color = value < 0 ? NamedTextColor.RED : NamedTextColor.WHITE;
				component = Component.text(" - ").decorate(TextDecoration.BOLD)
					.append(type.toComponent().color(type.getColor()))
					.append(Component.text(": "))
					.append(Component.text(value).color(color));
				player.sendMessage(component);
			}
		}
		else if (itemCustom instanceof Spell) {
            player.sendMessage(Msg.msg("<white>item <green><identifier><white> is a spell", Msg.text("identifier", identifier)));
            return true;
		}
		else if (itemCustom instanceof Potion) {
            player.sendMessage(Msg.msg("<white>item <green><identifier><white> is a potion", Msg.text("identifier", identifier)));
            return true;
		}
		else {
            player.sendMessage(Msg.msg("<red>item <yellow><identifier><red> cannot convert", Msg.text("identifier", identifier)));
            return true;
		}
		return true;
	}
}
