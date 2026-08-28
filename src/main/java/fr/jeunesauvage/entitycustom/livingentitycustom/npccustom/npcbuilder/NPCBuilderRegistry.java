package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.npcbuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait.FightTrait;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.waypoint.LinearWaypointProvider;
import net.citizensnpcs.trait.waypoint.Waypoint;
import net.citizensnpcs.trait.waypoint.Waypoints;
import net.kyori.adventure.text.format.NamedTextColor;

public class NPCBuilderRegistry {
    public static final NamespacedKey	    KEY_PLACER = new NamespacedKey(RpgCraft.name(), "npcplacer");
	public final Map<UUID, Set<Location>>	placers = new HashMap<>();

	// add placer
	public void addNPCPlacer(PlayerCustom launcher, Location location) {
        placers.computeIfAbsent(launcher.getUUID(), set -> new HashSet<>()).add(location);
        Set<Location>   set = placers.get(launcher.getUUID());
        launcher.sendMessage(Message.c("NPC placer placed (" + set.size() + ")", NamedTextColor.GREEN));
	}

	// delete placer
	public void deleteNPCPlacer(PlayerCustom launcher, Location location) {
        placers.computeIfPresent(launcher.getUUID(), (k, set) -> {
            set.remove(location);
            return set.isEmpty() ? null : set;
        });
        Set<Location>   set = placers.get(launcher.getUUID());
        launcher.sendMessage(Message.c("NPC placer removed (" + (set != null ? set.size() : "0") + ")", NamedTextColor.YELLOW));
	}

	// give player a placer
	public void createMyNPCPlacer(PlayerCustom launcher) {
	    ItemStack	head = new ItemStack(Material.PLAYER_HEAD);
	    SkullMeta	meta = (SkullMeta)head.getItemMeta();
	    meta.displayName(Message.c("NPC Placer", NamedTextColor.DARK_GRAY));
	    Data.setBoolean(meta.getPersistentDataContainer(), KEY_PLACER);
	    head.setItemMeta(meta);
        launcher.getInventory().addItem(head);
	}

	// create npc by placer
	public void createMyNPC(PlayerCustom launcher, TemplateType templateType, int levelMin, int levelMax) {
		if (templateType == null) {
			launcher.sendMessage(Message.m("<red>template is invalid"));
			return;
		}
		if (levelMin <= 0 || levelMin > LivingEntityCustom.LEVEL_MAX) {
			launcher.sendMessage(Message.m("<red>levelMin is invalid"));
			return;
		}
		if (levelMax <= 0 || levelMax > LivingEntityCustom.LEVEL_MAX) {
			launcher.sendMessage(Message.m("<red>levelMax is invalid"));
			return;
		}
        Set<Location>   set = placers.get(launcher.getUUID());
        if (set == null) {
			launcher.sendMessage(Message.m("<red>no placer"));
			return;
		}
		int	count = 0;
	    for (Location loc: set) {
			loc.getBlock().setType(Material.AIR);
			Location				spawn = loc.clone().add(0.5, 0, 0.5);
			Location				path = loc.clone();
			NPC						rawNPC = CitizensAPI.getNPCRegistry().createNPC(templateType.getEntityType(), templateType.getHideName(), spawn);
			NPCCustom				npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(rawNPC.getUniqueId());
			Waypoints				waypoints = rawNPC.getOrAddTrait(Waypoints.class);
			LinearWaypointProvider	linearWaypointProvider = (LinearWaypointProvider)waypoints.getCurrentProvider();
			linearWaypointProvider.addWaypoint(new Waypoint(path));
			linearWaypointProvider.setCycle(true);
			rawNPC.setProtected(false);
			npcCustom.setRespawn(spawn);
            int level = ThreadLocalRandom.current().nextInt(levelMin, levelMax + 1);
			npcCustom.setLevel(level);
			npcCustom.setTemplate(templateType);
		}
		placers.clear();
		launcher.sendMessage(Message.m("<green>" + count + " NPC created (" + templateType.getHideName() + ")"));
	}

	// change level
	public void changeLevel(PlayerCustom launcher, String npcName, int level) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		if (level <= 0 || level > LivingEntityCustom.LEVEL_MAX) {
			launcher.sendMessage(Message.m("<red>level is invalid"));
			return;
		}
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
			if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			NPCCustom	npcCustom = entityCustomRegistry.getNPCCustom(npc.getUniqueId());
			npcCustom.setLevel(level);
			count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " level " + level +" applied"));
	}

	// change patrol range
	public void changePatrolRange(PlayerCustom launcher, String npcName, double patrolRange) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		if (patrolRange < 0 || patrolRange > FightTrait.AGGRORANGE_DEFAULT) {
			launcher.sendMessage(Message.m("<red>patrolRange is invalid"));
			return;
		}
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			FightTrait	fightTrait = npc.getOrAddTrait(FightTrait.class);
        	fightTrait.setPatrolRange(patrolRange);
			count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " patrol range " + patrolRange + " applied"));
	}

	// change aggro range
	public void changeAggroRange(PlayerCustom launcher, String npcName, double aggroRange) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		if (aggroRange < 0 || aggroRange > FightTrait.CHASERANGE_DEFAULT) {
			launcher.sendMessage(Message.m("<red>aggroRange is invalid"));
			return;
		}
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			FightTrait	fightTrait = npc.getOrAddTrait(FightTrait.class);
        	fightTrait.setAggroRange(aggroRange);
			count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " aggro range " + aggroRange + " applied"));
	}

	// change chase range
	public void changeChaseRange(PlayerCustom launcher, String npcName, double chaseRange) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		if (chaseRange < 0 || chaseRange > 200) {
			launcher.sendMessage(Message.m("<red>chaseRange is invalid"));
			return;
		}
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			FightTrait	fightTrait = npc.getOrAddTrait(FightTrait.class);
        	fightTrait.setChaseRange(chaseRange);
			count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " chase range " + chaseRange + " applied"));
	}

	// change boss
	public void changeBoss(PlayerCustom launcher, String npcName, boolean isBoss) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			FightTrait	fightTrait = npc.getOrAddTrait(FightTrait.class);
        	fightTrait.setBoss(isBoss);
			count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " change boss " + isBoss + " applied"));
	}

	// change equipement
	public void changeEquipement(PlayerCustom launcher, String npcName, ItemStack item) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		if (item == null) {
			launcher.sendMessage(Message.m("<red>item is invalid"));
			return;
		}
		int				count = 0;
		Material		material = item.getType();
		EquipmentSlot	slot = material.getEquipmentSlot();
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			Equipment equipment = npc.getOrAddTrait(Equipment.class);
			if (material.equals(Material.AIR)) {
				equipment.set(Equipment.EquipmentSlot.HELMET, null);
				equipment.set(Equipment.EquipmentSlot.CHESTPLATE, null);
				equipment.set(Equipment.EquipmentSlot.LEGGINGS, null);
				equipment.set(Equipment.EquipmentSlot.BOOTS, null);
				equipment.set(Equipment.EquipmentSlot.HAND, null);
				equipment.set(Equipment.EquipmentSlot.OFF_HAND, null);
			}
			else if (slot.equals(EquipmentSlot.HEAD))
				equipment.set(Equipment.EquipmentSlot.HELMET, item.clone());
			else if (slot.equals(EquipmentSlot.CHEST))
				equipment.set(Equipment.EquipmentSlot.CHESTPLATE, item.clone());
			else if (slot.equals(EquipmentSlot.LEGS))
				equipment.set(Equipment.EquipmentSlot.LEGGINGS, item.clone());
			else if (slot.equals(EquipmentSlot.FEET))
				equipment.set(Equipment.EquipmentSlot.BOOTS, item.clone());
			else if (slot.equals(EquipmentSlot.HAND)) {
				ItemStack	hand = equipment.get(Equipment.EquipmentSlot.HAND);
				if (hand != null && !hand.getType().isAir())
					equipment.set(Equipment.EquipmentSlot.OFF_HAND, item.clone());
				else
					equipment.set(Equipment.EquipmentSlot.HAND, item.clone());
			}
			else if (slot.equals(EquipmentSlot.OFF_HAND))
				equipment.set(Equipment.EquipmentSlot.OFF_HAND, item.clone());
			count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " equip " + slot.name() + " applied"));
	}

	// change team
	public void changeTeam(PlayerCustom launcher, String npcName, String action, TeamType teamType) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		if (action == null) {
			launcher.sendMessage(Message.m("<red>action is invalid"));
			return;
		}
		if (teamType == null) {
			launcher.sendMessage(Message.m("<red>teamType is invalid"));
			return;
		}
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		int	count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			NPCCustom	npcCustom = entityCustomRegistry.getNPCCustom(npc.getUniqueId());
			if (action.equals("add"))
            	npcCustom.addTeam(teamType);
			else
            	npcCustom.deleteTeam(teamType);
            count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " team " + teamType.getName() + " applied"));
	}

	/*
	// change drop
	public void changeDrop(PlayerCustom player, String npcName, String drop) {
		int	count = 0;
        if (drop.startsWith("drop-")) {
        	for (NPC npc: CitizensAPI.getNPCRegistry()) {
        	    if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
        	    if (!(npc.getEntity() instanceof LivingEntity living)) continue;
        	    for (String tag: new HashSet<String>(living.getScoreboardTags())) {
        	        if (tag.startsWith("drop-"))
        	            living.removeScoreboardTag(tag);
        	    }
        	    living.addScoreboardTag(drop);
        	    count++;
        	}
			player.sendMessage(Msg.msg("<green><count> drop <drop> applied", Msg.text("count", count), Msg.text("drop", drop)));
        }
		player.sendMessage(Msg.msg("<red> drop <yellow><drop><red> is invalid", Msg.text("drop", drop)));
	}
	*/

	// change template
	public void changeTemplate(PlayerCustom launcher, String npcName, TemplateType templateType) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		if (templateType == null) {
			launcher.sendMessage(Message.m("<red>templateType is invalid"));
			return;
		}
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		int	count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			NPCCustom		npcCustom = entityCustomRegistry.getNPCCustom(npc.getUniqueId());
            npcCustom.setTemplate(templateType);
            count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " template " + templateType.getName() + " applied"));
	}

	// spawn
	public void spawn(PlayerCustom launcher, String npcName) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		EntityCustomRegistry	entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
		boolean	all = npcName.equals("all");
		int		count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!all && !npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			NPCCustom	npcCustom = entityCustomRegistry.getNPCCustom(npc.getUniqueId());
            Location	spawn = npcCustom.getRespawn();
			if (spawn == null) continue;
			npcCustom.spawn(spawn);
            count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " NPC spawned"));
	}

	// despawn
	public void despawn(PlayerCustom launcher, String npcName) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		boolean	all = npcName.equals("all");
		int	count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!all && !npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			npc.despawn();
            count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " NPC despawned"));
	}

	// delete
	public void delete(PlayerCustom launcher, String npcName) {
		if (npcName == null) {
			launcher.sendMessage(Message.m("<red>npcName is invalid"));
			return;
		}
		boolean	all = npcName.equals("all");
		int	count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!all && !npc.getName().toLowerCase().equals(npcName.toLowerCase())) continue;
			npc.destroy();
            count++;
        }
		launcher.sendMessage(Message.m("<green>" + count + " NPC deleted"));
	}
}
