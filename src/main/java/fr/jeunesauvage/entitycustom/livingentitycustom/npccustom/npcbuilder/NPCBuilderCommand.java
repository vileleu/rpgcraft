package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.npcbuilder;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;

public class NPCBuilderCommand implements CommandExecutor {
    // handle npc builder commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
            case "placemynpc":
                return handlePlaceMyNPC(sender, args);
            case "createmynpc":
                return handleCreateMyNPC(sender, args);
        	case "levelmynpc":
        	    return handleLevelMyNPC(sender, args);
        	case "patrolmynpc":
        	    return handlePatrolMyNPC(sender, args);
        	case "aggromynpc":
        	    return handleAggroMyNPC(sender, args);
        	case "chasemynpc":
        	    return handleChaseMyNPC(sender, args);
        	case "bossmynpc":
        	    return handleBossMyNPC(sender, args);
        	case "equipmynpc":
        	    return handleEquipMyNPC(sender, args);
        	case "teammynpc":
        	    return handleTeamMyNPC(sender, args);
        	case "dropmynpc":
        	    return handleDropMyNPC(sender, args);
        	case "templatemynpc":
        	    return handleTemplateMyNPC(sender, args);
            case "spawnmynpc":
                return handleSpawnMyNPC(sender, args);
            case "despawnmynpc":
                return handleDespawnMyNPC(sender, args);
        	case "deletemynpc":
        	    return handleDeleteMyNPC(sender, args);
		}
		return false;
    }

    // give player a placer
	private boolean handlePlaceMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length > 0) {
            sender.sendMessage(Message.m("<red>Usage: /placemynpc"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        RpgCraft.getNPCBuilderRegistry().createMyNPCPlacer(launcher);
		return true;
	}

    // create npc by placer
	private boolean handleCreateMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length != 2 && args.length != 3) {
            sender.sendMessage(Message.m("<red>Usage: /createmynpc <template> <level min> <level max>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        TemplateType    templateType = TemplateType.fromString(args[0]);
        int             levelMin;
        int             levelMax;
        try {
            levelMin = Integer.parseInt(args[1]);
            if (args.length == 3) levelMax = Integer.parseInt(args[2]);
            else levelMax = levelMin;
        }
        catch (NumberFormatException e) {
            levelMin = 1;
            levelMax = 1;
        }
        if (levelMax < levelMin) levelMax = levelMin;
        RpgCraft.getNPCBuilderRegistry().createMyNPC(launcher, templateType, levelMin, levelMax);
		return true;
	}

    // change level
    private boolean handleLevelMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 2) {
            sender.sendMessage(Message.m("<red>Usage: /levelmynpc <level> <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        int level;
        try {
            level = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            level = 1;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        RpgCraft.getNPCBuilderRegistry().changeLevel(launcher, npcName, level);
        return true;
    }

    // change patrolrange
    private boolean handlePatrolMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 2) {
            sender.sendMessage(Message.m("<red>Usage: /patrolmynpc <patrolrange> <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        int patrolRange;
        try {
            patrolRange = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            patrolRange = 0;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        RpgCraft.getNPCBuilderRegistry().changePatrolRange(launcher, npcName, patrolRange);
        return true;
    }

    // change aggromynpc
    private boolean handleAggroMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 2) {
            sender.sendMessage(Message.m("<red>Usage: /aggromynpc <aggrorange> <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        int aggroRange;
        try {
            aggroRange = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            aggroRange = 0;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        RpgCraft.getNPCBuilderRegistry().changeAggroRange(launcher, npcName, aggroRange);
        return true;
    }

    // change chasemynpc
    private boolean handleChaseMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 2) {
            sender.sendMessage(Message.m("<red>Usage: /chasemynpc <chaserange> <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        int chaseRange;
        try {
            chaseRange = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            chaseRange = 0;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        RpgCraft.getNPCBuilderRegistry().changeChaseRange(launcher, npcName, chaseRange);
        return true;
    }

    // change bossmynpc
    private boolean handleBossMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 2) {
            sender.sendMessage(Message.m("<red>Usage: /bossmynpc <isboss> <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        Boolean isBoss = args[0].equals("true");
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        RpgCraft.getNPCBuilderRegistry().changeBoss(launcher, npcName, isBoss);
        return true;
    }

    // change equipement
    private boolean handleEquipMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 1) {
            sender.sendMessage(Message.m("<red>Usage: /equipmynpc <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        ItemStack   item = launcher.getInventory().getItemInMainHand();
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        RpgCraft.getNPCBuilderRegistry().changeEquipement(launcher, npcName, item);
        return true;
    }

    // change team
    private boolean handleTeamMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 3) {
            sender.sendMessage(Message.m("<red>Usage: /teammynpc <action> <team> <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        String      action = args[0];
        TeamType    team = TeamType.fromString(args[1]);
        String      npcName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        RpgCraft.getNPCBuilderRegistry().changeTeam(launcher, npcName, action, team);
        return true;
    }

    // change drop
    private boolean handleDropMyNPC(CommandSender sender, String[] args) {
        /*
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) {
            sender.sendMessage(Message.m("<red>Usage: /racemynpc <drop> <npc name>"));
            return true;
        }
        String  drop = args[0];
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        npcBuilder.changeDrop(player, npcName, drop);
        */
        return true;
    }

    // change template
    private boolean handleTemplateMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 3) {
            sender.sendMessage(Message.m("<red>Usage: /templatemynpc <template> <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        TemplateType    templateType = TemplateType.fromString(args[0]);
        String          npcName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        RpgCraft.getNPCBuilderRegistry().changeTemplate(launcher, npcName, templateType);
        return true;
    }

    // spawn
    private boolean handleSpawnMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 1) {
            sender.sendMessage(Message.m("<red>Usage: /spawnmynpc <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        RpgCraft.getNPCBuilderRegistry().spawn(launcher, npcName);
        return true;
    }

    // despawn
    private boolean handleDespawnMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 1) {
            sender.sendMessage(Message.m("<red>Usage: /despawnmynpc <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        RpgCraft.getNPCBuilderRegistry().despawn(launcher, npcName);
        return true;
    }

    // remove
    private boolean handleDeleteMyNPC(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length < 1) {
            sender.sendMessage(Message.m("<red>Usage: /despawnmynpc <npc name>"));
            return true;
        }
        PlayerCustom    launcher = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
        if (launcher == null) {
            sender.sendMessage(Message.m("<red>player not found (CRITICAL ERROR)"));
            return true;
        }
        String  npcName = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
        RpgCraft.getNPCBuilderRegistry().delete(launcher, npcName);
        return true;
    }
}
