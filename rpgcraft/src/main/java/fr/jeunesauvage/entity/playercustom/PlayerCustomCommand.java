package fr.jeunesauvage.entity.playercustom;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.jeunesauvage.component.Msg;
import fr.jeunesauvage.component.Translatable;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Health;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Level;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Resource;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.playercustom.menu.MenuHolder;
import fr.jeunesauvage.entity.print.Print;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.entity.team.Team;
import fr.jeunesauvage.entity.team.TeamType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class PlayerCustomCommand implements CommandExecutor {
    private final EntityManager entityManager;

    PlayerCustomCommand(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // handle playercustom commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "menu":
                return handleMenu(sender, args);
            case "addstat":
                return handleAddStat(sender, args);
            case "removestat":
                return handleRemoveStat(sender, args);
            case "addskill":
                return handleAddSkill(sender, args);
            case "removeskill":
                return handleRemoveSkill(sender, args);
            case "printmodifiers":
                return handlePrintModifiers(sender, args);
            case "print":
                return handlePrint(sender, args);
            case "printresources":
                return handlePrintResources(sender, args);
            case "printstats":
                return handlePrintStats(sender, args);
            case "printskills":
                return handlePrintSkills(sender, args);
            case "team":
                return handleTeam(sender, args);
            case "race":
                return handleRace(sender, args);
            case "form":
                return handleForm(sender, args);
            case "class":
                return handleClass(sender, args);
		}
		return false;
    }

    // add stat modifier
    private boolean handleMenu(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(Msg.msg("<red>Usage: /menu <player name>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player: <yellow><player><red> is unknown", Msg.text("player", args[0])));
            return true;
        }
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Choisis un sort"));
        player.openInventory(inv);
        return true;
    }

    // add stat modifier
    private boolean handleAddStat(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(Msg.msg("<red>Usage: /addstat <player name> <stat name> <value> <duration>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player: <yellow><player><red> is unknown", Msg.text("player", args[0])));
            return true;
        }
        StatType    type = StatType.fromString(args[1]);
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
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        playerCustom.addStatModifier(type, value, duration);
        return true;
    }

    // remove stat modifier
    private boolean handleRemoveStat(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /removestatmodifier <player name> <id/stat name>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player: <yellow><player><red> is unknown", Msg.text("player", args[0])));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        StatType        type = StatType.fromString(args[1]);
        if (type != null)
            playerCustom.removeStatModifier(type);
        else {
            int id;
            try {
                id = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", args[1])));
                return true;
            }
            playerCustom.removeStatModifier(id);
        }
        return true;
    }

    // add skill modifier
    private boolean handleAddSkill(CommandSender sender, String[] args) {
        if (args.length != 4) {
            sender.sendMessage(Msg.msg("<red>Usage: /addskillmodifier <player name> <skill name> <value> <duration>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player: <yellow><player><red> is unknown", Msg.text("player", args[0])));
            return true;
        }
        SkillType    type = SkillType.fromString(args[1]);
        if (type == null) {
            sender.sendMessage(Msg.msg("<red>skill: <yellow><skill><red> is invalid", Msg.text("skill", args[1])));
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
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        playerCustom.addSkillModifier(type, value, duration);
        return true;
    }

    // remove skill modifier
    private boolean handleRemoveSkill(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /removeskillmodifier <player name> <id/skill name>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player: <yellow><player><red> is unknown", Msg.text("player", args[0])));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        SkillType        type = SkillType.fromString(args[1]);
        if (type != null)
            playerCustom.removeSkillModifier(type);
        else {
            int id;
            try {
                id = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage(Msg.msg("<red>id: <yellow><id><red> is invalid", Msg.text("id", args[1])));
                return true;
            }
            playerCustom.removeSkillModifier(id);
        }
        return true;
    }

    // print infos modifier (stats + skills)
	private boolean handlePrintModifiers(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /printmodifiers"));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        player.sendMessage(Msg.msg(""));
        player.sendMessage(Msg.msg("<u>INFOS MODIFIERS:</u>"));
        Map<Integer, StatModifier>  statsMod = playerCustom.getStatModifiers();
        statsMod.values().forEach(modifier -> {
            int     value = modifier.getValue();
            String  color = value < 0 ? "<red>" : (value > 0 ? "<green>+" : "<green>");
            player.sendMessage(Msg.msg("<green><id>: <stat>, " + color + "<value><green>, <duration>s",
                Msg.text("id", modifier.getId()),
                Msg.text("stat", modifier.getName()),
                Msg.text("value",value),
                Msg.text("duration", modifier.getTimeLeft())
            ));
        });
        Map<Integer, SkillModifier> skillsMod = playerCustom.getSkillModifiers();
        skillsMod.values().forEach(modifier -> {
            int     value = modifier.getValue();
            String  color = value < 0 ? "<red>" : (value > 0 ? "<green>+" : "<green>");
            player.sendMessage(Msg.msg("<green><id>: <stat>, " + color + "<value><green>, <duration>s",
                Msg.text("id", modifier.getId()),
                Msg.text("stat", modifier.getName()),
                Msg.text("value",value),
                Msg.text("duration", modifier.getTimeLeft())
            ));
        });
        return true;
    }

    // print infos general (class, race)
	private boolean handlePrint(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /print"));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        player.sendMessage(Msg.msg(""));
        player.sendMessage(Msg.msg("<u>INFOS:</u>"));
        // class
        ClassType   classType = playerCustom.getClassType();
        Component   component = Translatable.classType()
            .append(Component.text(": "))
            .append(classType.toComponent().color(classType.getColor()));
        player.sendMessage(component);
        // race
        RaceType    raceType = playerCustom.getRaceType();
        component = Translatable.raceType()
            .append(Component.text(": "))
            .append(raceType.toComponent().color(raceType.getColor()));
        player.sendMessage(component);
        return true;
    }

    // print infos resource
	private boolean handlePrintResources(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length != 0) {
            player.sendMessage(Msg.msg("<red>Usage: /printresource"));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        player.sendMessage(Msg.msg(""));
        Component   component = Translatable.resource().decorate(TextDecoration.UNDERLINED);
        player.sendMessage(component);
        // health
        Health  health = playerCustom.getHealth();
        component = health.toComponent()
            .append(Component.text(": "))
            .append(Component.text((int)health.getValue()).color(health.getColor()))
            .append(Component.text("/"))
            .append(Component.text((int)health.getValueMax()).color(health.getColor()));
        player.sendMessage(component);
        // power
        Resource    power = playerCustom.getPower();
        if (power == null)
            player.sendMessage(Component.text("--: --/--"));
        else {
            component = power.toComponent()
                .append(Component.text(": "))
                .append(Component.text((int)power.getValue()).color(power.getColor()))
                .append(Component.text("/"))
                .append(Component.text((int)power.getValueMax()).color(power.getColor()));
            player.sendMessage(component);
        }
        // level
        Level   level = playerCustom.getLevel();
        component = level.toComponent()
            .append(Component.text(": "))
            .append(Component.text((int)level.getValue()).color(level.getColor()))
            .append(Component.text("/"))
            .append(Component.text((int)level.getValueMax()).color(level.getColor()));
        player.sendMessage(component);
        return true;
    }

    // print infos stat
	private boolean handlePrintStats(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length != 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /printstats <player name>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player: <yellow><player><red> is unknown", Msg.text("player", args[0])));
            return true;
        }
        sender.sendMessage(Msg.msg(""));
        Print           print = new Print(player);
        for (Component component: print.printStatPrimary()) {
            sender.sendMessage(component);
        }
        for (Component component: print.printStatSecondary()) {
            sender.sendMessage(component);
        }
        return true;
    }

    // print infos skill
	private boolean handlePrintSkills(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length != 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /printskills <player name>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player: <yellow><player><red> is unknown", Msg.text("player", args[0])));
            return true;
        }
        player.sendMessage(Msg.msg(""));
        Print           print = new Print(player, entityManager.getEntityModifierManager());
        for (Component component: print.printSkillPrimary()) {
            player.sendMessage(component);
        }
        for (Component component: print.printSkillSecondary()) {
            player.sendMessage(component);
        }
        return true;
    }

	private boolean handleTeam(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Msg.msg("<red>Usage: /teamplayer <add> <team> <player name>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player <yellow>" + args[2] + "<red> is invalid"));
            return true;
        }
        TeamType    type = TeamType.fromString(args[1]);
        if (args[0].equals("add"))
            Team.add(player, type);
        else if (args[0].equals("remove"))
            Team.remove(player, type);
        else {
            sender.sendMessage(Msg.msg("<green>team's " + player.getName() + ":"));
            for (TeamType t: Team.get(player)) {
                sender.sendMessage(Msg.msg("<green> - " + t.getName()));
            }
        }
        return true;
    }

	private boolean handleRace(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /raceplayer <player name> <race type>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player <yellow>" + args[0] + "<red> is invalid"));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        RaceType        raceType = RaceType.fromString(args[1]);
        playerCustom.setRaceType(raceType);
        sender.sendMessage(Component.text(player.getName() + " have race: ").append(raceType.toComponent()));
        return true;
    }

	private boolean handleForm(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /formplayer <player name> <form type>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player <yellow>" + args[0] + "<red> is invalid"));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        FormType        formType = FormType.fromString(args[1]);
        playerCustom.setFormType(formType);
        sender.sendMessage(Msg.msg(player.getName() + " have form: <green>" + formType.getName()));
        return true;
    }

	private boolean handleClass(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /class <player name> <class type>"));
            return true;
        }
        Player  player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Msg.msg("<red>player <yellow>" + args[0] + "<red> is invalid"));
            return true;
        }
        PlayerCustom    playerCustom = PlayerCustomManager.getPlayerCustom(player);
        if (args[1].equals("reset")) {
            playerCustom.reset();
            sender.sendMessage(Msg.msg(player.getName() + " class is reset"));
        }
        else {
            ClassType   classType = ClassType.fromString(args[1]);
            playerCustom.setClassType(classType);
            sender.sendMessage(Component.text(player.getName() + " have class: ").append(classType.toComponent()));
        }
        return true;
    }
}