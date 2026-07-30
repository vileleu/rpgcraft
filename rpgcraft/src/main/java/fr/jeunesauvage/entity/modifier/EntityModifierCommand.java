package fr.jeunesauvage.entity.modifier;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;

import fr.jeunesauvage.component.Msg;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.print.Print;
import net.kyori.adventure.text.Component;

public class EntityModifierCommand implements CommandExecutor {
	private final EntityModifierManager entityModifierManager;

	EntityModifierCommand(EntityModifierManager entityModifierManager) {
        this.entityModifierManager = entityModifierManager;
	}

    // handle playercustom commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "addstatentity":
                return handleAddStat(sender, args);
            case "removestatentity":
                return handleRemoveStat(sender, args);
            case "printstatsentity":
                return handlePrintStats(sender, args);
		}
		return false;
    }

    // add stat modifier
    private boolean handleAddStat(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(Msg.msg("<red>Usage: /addentitystatmodifier <uuid> <stat name> <value> <duration>"));
            return true;
        }
		UUID	uuid = UUID.fromString(args[0]);
        if (uuid == null) {
            sender.sendMessage(Msg.msg("<red>uuid: <yellow><uuid><red> is unknown", Msg.text("uuid", args[0])));
            return true;
        }
        if (!(Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity)) {
            sender.sendMessage(Msg.msg("<red>entity: <yellow><entity><red> is unknown", Msg.text("entity", args[0])));
            return true;
        }
        StatSecondary    type = StatSecondary.fromString(args[1]);
        if (type == null) {
            sender.sendMessage(Msg.msg("<red>stat: <yellow><stat><red> is invalid", Msg.text("stat", args[1])));
            return true;   
        }
        int value;
        try {
            value = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(Msg.msg("<red>value: <yellow><value><red> is invalid", Msg.text("value", args[2])));
            return true;
        }
        int duration;
        try {
            duration = Integer.parseInt(args[3]);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(Msg.msg("<red>duration: <yellow><duration><red> is invalid", Msg.text("duration", args[3])));
            return true;
        }
        entityModifierManager.addModifier(livingEntity, type, value, duration);
        return true;
    }

    // remove stat modifier
    private boolean handleRemoveStat(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /removestatmodifier <uuid> <id/stat name>"));
            return true;
        }
		UUID	uuid = UUID.fromString(args[0]);
        if (uuid == null) {
            sender.sendMessage(Msg.msg("<red>uuid: <yellow><uuid><red> is unknown", Msg.text("uuid", args[0])));
            return true;
        }
        if (!(Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity)) {
            sender.sendMessage(Msg.msg("<red>entity: <yellow><entity><red> is unknown", Msg.text("entity", args[0])));
            return true;
        }
        StatSecondary    type = StatSecondary.fromString(args[1]);
        if (type != null)
            entityModifierManager.removeModifier(livingEntity, type);
        else {
            int id;
            try {
                id = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", args[1])));
                return true;
            }
            entityModifierManager.removeModifier(livingEntity, id);
        }
        return true;
    }

    // print value modifiers
	private boolean handlePrintStats(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /infosmodifier <uuid>"));
            return true;
        }
		UUID	uuid = UUID.fromString(args[0]);
        if (uuid == null) {
            sender.sendMessage(Msg.msg("<red>uuid: <yellow><uuid><red> is unknown", Msg.text("uuid", args[0])));
            return true;
        }
        if (!(Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity)) {
            sender.sendMessage(Msg.msg("<red>entity: <yellow><entity><red> is unknown", Msg.text("entity", args[0])));
            return true;
        }
        sender.sendMessage(Msg.msg(""));
        Print   print = new Print(livingEntity, entityModifierManager);
        for (Component component: print.printStatSecondary()) {
            sender.sendMessage(component);
        }
        return true;
    }
}
