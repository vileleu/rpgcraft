package fr.jeunesauvage.entity.npc.npcbuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Msg;
import fr.jeunesauvage.skin.Skin;
import fr.jeunesauvage.skin.SkinData;
import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.npc.template.TemplateType;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.entity.team.TeamType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.waypoint.LinearWaypointProvider;
import net.citizensnpcs.trait.waypoint.Waypoint;
import net.citizensnpcs.trait.waypoint.Waypoints;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class NPCBuilder implements Listener {
	private static final NamespacedKey	KEY_PLACER = new NamespacedKey(RpgCraft.name(), "npcplacer");
	public final Set<Location>			placers = new HashSet<>();

	public NPCBuilder(JavaPlugin plugin) {
		// command
        NPCBuilderCommand   npcBuilderCommand = new NPCBuilderCommand(this);
        plugin.getCommand("placemynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("createmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("skinmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("typemynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("setmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("setstatmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("equipmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("teammynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("dropmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("racemynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("formmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("templatemynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("spawnmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("despawnmynpc").setExecutor(npcBuilderCommand);
        plugin.getCommand("removemynpc").setExecutor(npcBuilderCommand);
	}

	// put a placer
	@EventHandler(ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent e) {
	    if (e.getBlock().getType() != Material.PLAYER_HEAD) return;
	    ItemStack	item = e.getItemInHand();
	    if (!(item.getItemMeta() instanceof SkullMeta meta)) return;
	    if (!Data.hasBoolean(meta.getPersistentDataContainer(), KEY_PLACER)) return;
		placers.add(e.getBlock().getLocation());
		int	count = placers.size();
		e.getPlayer().sendMessage(Msg.msg("<green>NPC placer <count> OK", Msg.text("count", count)));
	}

	// break a placer
	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() != Material.PLAYER_HEAD) return;
    	Location loc = e.getBlock().getLocation();
    	if (placers.remove(loc)) e.getPlayer().sendMessage(Msg.msg("<red>NPC placer removed"));
	}

	// give player a placer
	public ItemStack createMyNPCPlacer() {
	    ItemStack	head = new ItemStack(Material.PLAYER_HEAD);
	    SkullMeta	meta = (SkullMeta) head.getItemMeta();
	    meta.displayName(Component.text("NPC Placer").color(NamedTextColor.RED));
	    Data.setBoolean(meta.getPersistentDataContainer(), KEY_PLACER);
	    head.setItemMeta(meta);
	    return head;
	}

	// create npc by placer
	public void createMyNPC(Player player, String npcName, TeamType team) {
		if (npcName.contains(".")) {
			player.sendMessage(Msg.msg("<red>name is invalid"));
			return;
		}
		if (team == null) {
			player.sendMessage(Msg.msg("<red>team is invalid"));
			return;
		}
		int	count = 0;
	    for (Location loc: placers) {
			loc.getBlock().setType(Material.AIR);
			Location		spawn = loc.clone().add(0.5, 0, 0.5);
			Location		path = loc.clone();
			NPC				npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcName, spawn);
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
			traitSentinel.setRespawn(spawn);
			traitSentinel.addTeam(team);
			traitSentinel.setLevel(1);
			Waypoints				waypoints = npc.getOrAddTrait(Waypoints.class);
			LinearWaypointProvider	linearWaypointProvider = (LinearWaypointProvider)waypoints.getCurrentProvider();
			linearWaypointProvider.addWaypoint(new Waypoint(path));
			linearWaypointProvider.setCycle(true);
			npc.setProtected(false);
		}
		placers.clear();
		player.sendMessage(Msg.msg("<green><count> NPC created (<npcname>)", Msg.text("count", count), Msg.text("npcname", npcName)));
	}

	// change skin
	public void changeSkin(Player player, String npcName, String skinName) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
        	SkinTrait   skinTrait = npc.getOrAddTrait(SkinTrait.class);
			SkinData	skinData = Skin.SKINS.get(skinName);
			if (skinData != null)
				skinTrait.setSkinPersistent(skinName, skinData.getSignature(), skinData.getValue());
			else
				skinTrait.setSkinName(skinName);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> skin <skinname> applied", Msg.text("count", count), Msg.text("skinname", skinName)));
	}

	// change skin
	public void changeType(Player player, String npcName, EntityType type) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
        	npc.setBukkitEntityType(type);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> type <typename> applied", Msg.text("count", count), Msg.text("typename", type.name())));
	}

	// change level
	public void changeLevel(Player player, String npcName, int value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
			if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setLevel(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> level <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change health
	public void changeHealth(Player player, String npcName, double value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setHealth(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> health <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change damage
	public void changeDamage(Player player, String npcName, double value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setDamage(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> damage <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change patrol range
	public void changePatrolRange(Player player, String npcName, double value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setPatrolRange(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> patrol range <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change aggro range
	public void changeAggroRange(Player player, String npcName, double value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setAggroRange(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> aggroRange <value> applied", Msg.text("count", count), Msg.text("value", value)));

	}

	// change chase range
	public void changeChaseRange(Player player, String npcName, double value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setChaseRange(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> chaseRange <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change attack range close
	public void changeAttackRangeClose(Player player, String npcName, double value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setAttackRangeClose(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> attackRangeClose <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change attack range ranged
	public void changeAttackRangeRanged(Player player, String npcName, double value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setAttackRangeRanged(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> attackRangeRanged <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change attack rate
	public void changeAttackRate(Player player, String npcName, int value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setAttackRate(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> attackRate <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change speed out combat
	public void changeSpeed(Player player, String npcName, float value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setSpeed(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> speed <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change speed combat
	public void changeSpeedCombat(Player player, String npcName, float value) {
		int	count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setSpeedCombat(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> speed combat <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change respawntime
	public void changeLookRange(Player player, String npcName, double value) {
		int		count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setLookRange(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> lookRange <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change respawntime
	public void changeRespawnTime(Player player, String npcName, int value) {
		int		count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setRespawnTime(value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> respawnTime <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change boss
	public void changeBoss(Player player, String npcName, double value) {
		boolean	isBoss = value != 0 ? true : false;
		int		count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setBoss(isBoss);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> boss <value> applied", Msg.text("count", count), Msg.text("value", isBoss)));
	}

	// set stat
	public void changeStat(Player player, String npcName, StatSecondary type, int value) {
		if (type == null) {
			player.sendMessage(Msg.msg("<red>stat is invalid"));
			return;
		}
		int		count = 0;
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
        	traitSentinel.setStat(type, value);
			count++;
        }
		player.sendMessage(Msg.msg("<green><count> stat <value> applied", Msg.text("count", count), Msg.text("value", value)));
	}

	// change equipement
	public void changeEquipement(Player player, String npcName, ItemStack item) {
		int				count = 0;
		Material		material = item.getType();
		EquipmentSlot	slot = material.getEquipmentSlot();
		for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
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
		player.sendMessage(Msg.msg("<green><count> equip <slotname> applied", Msg.text("count", count), Msg.text("slotname", slot.name())));
	}

	// change team
	public void changeTeam(Player player, String npcName, String action, TeamType team) {
		boolean		print = !action.equals("add") && !action.equals("remove");
		Set<String>	teams = new HashSet<>();
		if (!print && team == null) {
			player.sendMessage(Msg.msg("<red>team is invalid"));
			return;
		}
		int	count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			if (action.equals("add"))
            	npc.getOrAddTrait(TraitSentinel.class).addTeam(team);
			else if (action.equals("remove"))
            	npc.getOrAddTrait(TraitSentinel.class).removeTeam(team);
			else  {
            	for (String t: npc.getOrAddTrait(TraitSentinel.class).getTeamsSaved())
					teams.add(t);
        	}
            count++;
        }
		if (print) {
            player.sendMessage(Msg.msg("<green>team's " + npcName + ":"));
           	for (String t: teams) {
        	    player.sendMessage(Msg.msg("<green> - " + t));
            }
		}
		else
			player.sendMessage(Msg.msg("<green><count> team <team> applied", Msg.text("count", count), Msg.text("team", team.getName())));
	}

	/*
	// change drop
	public void changeDrop(Player player, String npcName, String drop) {
		int	count = 0;
        if (drop.startsWith("drop-")) {
        	for (NPC npc: CitizensAPI.getNPCRegistry()) {
        	    if (!npc.getName().equals(npcName)) continue;
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

	// change race
	public void changeRace(Player player, String npcName, RaceType raceType) {
		int	count = 0;
        if (raceType == null) {
			player.sendMessage(Msg.msg("<red>race is invalid"));
			return;
        }
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
            traitSentinel.setRaceType(raceType);
            count++;
        }
		player.sendMessage(Msg.msg("<green><count> race " + raceType.getName() + " applied", Msg.text("count", count)));
	}

	// change form
	public void changeForm(Player player, String npcName, FormType formType) {
		int	count = 0;
        if (formType == null) {
			player.sendMessage(Msg.msg("<red>form is invalid"));
			return;
        }
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
            traitSentinel.setFormType(formType);
            count++;
        }
		player.sendMessage(Msg.msg("<green><count> form " + formType.getName() + " applied", Msg.text("count", count)));
	}

	// change template
	public void changeTemplate(Player player, String npcName, TemplateType templateType, int levelMin, int levelMax) {
		int	count = 0;
        if (templateType == null || levelMin < 1 || levelMax < 1 || levelMin > levelMax) {
			player.sendMessage(Msg.msg("<red>level is invalid"));
			TemplateType	type = null;
        	for (NPC npc: CitizensAPI.getNPCRegistry()) {
        	    if (!npc.getName().equals(npcName)) continue;
				TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
				type = traitSentinel.getTemplateType();
        	    count++;
        	}
			if (type == null)
				player.sendMessage(Msg.msg("<green><count> template null get", Msg.text("count", count)));
			else
				player.sendMessage(Msg.msg("<green><count> template " + type.getName() + " get", Msg.text("count", count)));
        }
		else {
        	for (NPC npc: CitizensAPI.getNPCRegistry()) {
        	    if (!npc.getName().equals(npcName)) continue;
				// random level between levelMin and levelMax
				int				level = ThreadLocalRandom.current().nextInt(levelMin, levelMax + 1);
				TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
				traitSentinel.setLevel(level);
				traitSentinel.setTemplate(templateType);
        	    count++;
        	}
			player.sendMessage(Msg.msg("<green><count> template " + templateType.getName() + " applied", Msg.text("count", count)));
		}
	}

	// spawn
	public void spawn(Player player, String npcName) {
        int     count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npcName.equals("all") && !npc.getName().equals(npcName)) continue;
			TraitSentinel	traitSentinel = npc.getOrAddTrait(TraitSentinel.class);
			Location		loc = traitSentinel.getRespawn();
			if (loc == null) continue;
            npc.spawn(loc, SpawnReason.PLUGIN);
            count++;
        }
		player.sendMessage(Msg.msg("<green><count> NPC spawned", Msg.text("count", count)));
	}

	// despawn
	public void despawn(Player player, String npcName) {
        int     count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npcName.equals("all") && !npc.getName().equals(npcName)) continue;
            npc.despawn(DespawnReason.PLUGIN);
            count++;
        }
		player.sendMessage(Msg.msg("<green><count> NPC despawned", Msg.text("count", count)));
	}

	// remove
	public void remove(Player player, String npcName) {
        int     count = 0;
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
            if (!npc.getName().equals(npcName)) continue;
            npc.destroy(player);
            count++;
        }
		player.sendMessage(Msg.msg("<green><count> NPC removed", Msg.text("count", count)));
	}
}
