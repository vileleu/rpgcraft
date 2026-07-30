package fr.jeunesauvage.entity.npc.npcbuilder;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.component.Msg;
import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.npc.template.TemplateType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.entity.team.TeamType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class NPCBuilderCommand implements CommandExecutor {
    private final NPCBuilder npcBuilder;

    public NPCBuilderCommand(NPCBuilder npcBuilder) {
        this.npcBuilder = npcBuilder;
    }

    // handle npc builder commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "placemynpc":
                return handlePlaceMyNPC(sender, args);
            case "createmynpc":
                return handleCreateMyNPC(sender, args);
        	case "skinmynpc":
        	    return handleSkinMyNPC(sender, args);
        	case "typemynpc":
        	    return handleTypeMyNPC(sender, args);
        	case "setmynpc":
        	    return handleSetMyNPC(sender, args);
        	case "setstatmynpc":
        	    return handleSetStatMyNPC(sender, args);
        	case "equipmynpc":
        	    return handleEquipMyNPC(sender, args);
        	case "teammynpc":
        	    return handleTeamMyNPC(sender, args);
        	case "dropmynpc":
        	    return handleDropMyNPC(sender, args);
        	case "racemynpc":
        	    return handleRaceMyNPC(sender, args);
        	case "formmynpc":
        	    return handleFormMyNPC(sender, args);
        	case "templatemynpc":
        	    return handleTemplateMyNPC(sender, args);
            case "spawnmynpc":
                return handleSpawnMyNPC(sender, args);
            case "despawnmynpc":
                return handleDespawnMyNPC(sender, args);
        	case "removemynpc":
        	    return handleRemoveMyNPC(sender, args);
		}
		return false;
    }

    // give player a placer
	private boolean handlePlaceMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length > 0) {
            sender.sendMessage(Msg.msg("<red>Usage: /placemynpc"));
            return true;
        }
        player.getInventory().addItem(npcBuilder.createMyNPCPlacer());
		return true;
	}

    // create npc by placer
	private boolean handleCreateMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /createmynpc <team> <npc name>"));
            return true;
        }
        TeamType    type = TeamType.fromString(args[0]);
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        npcBuilder.createMyNPC(player, npcName, type);
		return true;
	}

    // change skin
    private boolean handleSkinMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /mynpc <skin name> <npc name>"));
            return true;
        }
        String  skinName = args[0];
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        npcBuilder.changeSkin(player, npcName, skinName);
        return true;
    }

    // change type
    private boolean handleTypeMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /typemynpc <type> <npc name>"));
            return true;
        }
        EntityType  type = EntityType.fromName(args[0]);
        if (type == null) {
            sender.sendMessage(Msg.msg("<red>type: <yellow><type><red> is invalid", TagResolver.resolver("type", Tag.inserting(Component.text(args[0])))));
            return true;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        npcBuilder.changeType(player, npcName, type);
        return true;
    }

    // change sentinel attribute
    private boolean handleSetMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 3) {
            sender.sendMessage(Msg.msg("<red>Usage: /mynpc <attribute> <value> <npc name>"));
            return true;
        }
        String  attribute = args[0];
        double  value;
        try {
            value = Double.parseDouble(args[1]);
        }
        catch (NumberFormatException e) {
            value = 0;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (attribute.equals("level"))
            npcBuilder.changeLevel(player, npcName, (int)value);
        else if (attribute.equals("health"))
            npcBuilder.changeHealth(player, npcName, value);
        else if (attribute.equals("damage"))
            npcBuilder.changeDamage(player, npcName, value);
        else if (attribute.equals("patrolrange"))
            npcBuilder.changePatrolRange(player, npcName, value);
        else if (attribute.equals("aggrorange"))
            npcBuilder.changeAggroRange(player, npcName, value);
        else if (attribute.equals("chaserange"))
            npcBuilder.changeChaseRange(player, npcName, value);
        else if (attribute.equals("attackrangeclose"))
            npcBuilder.changeAttackRangeClose(player, npcName, value);
        else if (attribute.equals("attackrangeranged"))
            npcBuilder.changeAttackRangeRanged(player, npcName, value);
        else if (attribute.equals("attackrate"))
            npcBuilder.changeAttackRate(player, npcName, (int)value);
        else if (attribute.equals("speed"))
            npcBuilder.changeSpeed(player, npcName, (float)value);
        else if (attribute.equals("speedcombat"))
            npcBuilder.changeSpeedCombat(player, npcName, (float)value);
        else if (attribute.equals("lookrange"))
            npcBuilder.changeLookRange(player, npcName, value);
        else if (attribute.equals("respawntime"))
            npcBuilder.changeRespawnTime(player, npcName, (int)value);
        else if (attribute.equals("boss"))
            npcBuilder.changeBoss(player, npcName, value);
        else {
            sender.sendMessage(Msg.msg("<red>attribute: <yellow><attribute><red> is invalid", TagResolver.resolver("attribute", Tag.inserting(Component.text(attribute)))));
            return true;
        }
        sender.sendMessage(Msg.msg("<green>attribute: <attribute> applied", TagResolver.resolver("attribute", Tag.inserting(Component.text(attribute)))));
        return true;
    }

    // set stat
    private boolean handleSetStatMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 3) {
            sender.sendMessage(Msg.msg("<red>Usage: /setstatmynpc <stat> <value> <npc name>"));
            return true;
        }
        StatSecondary   type = StatSecondary.fromString(args[0]);
        int  value;
        try {
            value = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            value = 0;
        }
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        npcBuilder.changeStat(player, npcName, type, value);
        return true;
    }

    // change equipement
    private boolean handleEquipMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /equipmynpc <npc name>"));
            return true;
        }
        ItemStack   item = player.getInventory().getItemInMainHand();
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        npcBuilder.changeEquipement(player, npcName, item);
        return true;
    }

    // change team
    private boolean handleTeamMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 3) {
            sender.sendMessage(Msg.msg("<red>Usage: /teammynpc <action> <team> <npc name>"));
            return true;
        }
        TeamType    team = TeamType.fromString(args[1]);
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        npcBuilder.changeTeam(player, npcName, args[0], team);
        return true;
    }

    // change drop
    private boolean handleDropMyNPC(CommandSender sender, String[] args) {
        /*
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /racemynpc <drop> <npc name>"));
            return true;
        }
        String  drop = args[0];
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        npcBuilder.changeDrop(player, npcName, drop);
        */
        return true;
    }

    // change race
    private boolean handleRaceMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /racemynpc <race> <npc name>"));
            return true;
        }
        RaceType    raceType = RaceType.fromString(args[0]);
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        npcBuilder.changeRace(player, npcName, raceType);
        return true;
    }

    // change form
    private boolean handleFormMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) {
            sender.sendMessage(Msg.msg("<red>Usage: /formmynpc <form> <npc name>"));
            return true;
        }
        FormType    formType = FormType.fromString(args[0]);
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        npcBuilder.changeForm(player, npcName, formType);
        return true;
    }

    // change template
    private boolean handleTemplateMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 3) {
            sender.sendMessage(Msg.msg("<red>Usage: /templatemynpc <template> <level min> <level max> <npc name>"));
            return true;
        }
        int  levelMin;
        try {
            levelMin = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            levelMin = 1;
        }
        int  levelMax;
        try {
            levelMax = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e) {
            levelMax = 1;
        }
        TemplateType    templateType = TemplateType.fromString(args[0]);
        String          npcName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        npcBuilder.changeTemplate(player, npcName, templateType, levelMin, levelMax);
        return true;
    }

    // spawn
    private boolean handleSpawnMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /spawnmynpc <npc name>"));
            return true;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        npcBuilder.spawn(player, npcName);
        return true;
    }

    // despawn
    private boolean handleDespawnMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /despawnmynpc <npc name>"));
            return true;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        npcBuilder.despawn(player, npcName);
        return true;
    }

    // remove
    private boolean handleRemoveMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 1) {
            sender.sendMessage(Msg.msg("<red>Usage: /removemynpc <npc name>"));
            return true;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        npcBuilder.remove(player, npcName);
        return true;
    }
}
