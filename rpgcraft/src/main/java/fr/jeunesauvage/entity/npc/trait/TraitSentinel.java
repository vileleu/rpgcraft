package fr.jeunesauvage.entity.npc.trait;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.npc.goal.GoalPatrol;
import fr.jeunesauvage.entity.npc.npcspell.NPCSpellManager;
import fr.jeunesauvage.entity.npc.template.TemplateType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.entity.team.Team;
import fr.jeunesauvage.entity.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.skin.Skin;
import fr.jeunesauvage.skin.SkinData;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;

public class TraitSentinel extends Trait {
	// default values
	private static final int			LEVEL_DEFAULT = 1;                 // level
	private static final int			RESPAWNTIME_DEFAULT = 5;           // respawn time
	private static final double			HEALTH_DEFAULT = 20;               // health
	private static final double			DAMAGE_DEFAULT = 5;                // damage of attack
	private static final double			PATROLRANGE_DEFAULT = 0;           // range of patrol
	private static final double			AGGRORANGE_DEFAULT = 30;           // range of aggro
	private static final double			CHASERANGE_DEFAULT = 70;           // range of chase
	private static final double			ATTACKRANGERANGED_DEFAULT = 15;    // range of attack ranged
	private static final double			ATTACKRANGECLOSE_DEFAULT = 3;      // range of attack close
	private static final int			ATTACKRATE_DEFAULT = 2;            // time (in seconds) between each attack
	private static final int			SPELLRATE_DEFAULT = 10;            // time (in seconds) between each spell
	public static final float			SPEED_DEFAULT = 0.5f;              // speed walk
	public static final float			SPEEDCOMBAT_DEFAULT = 1.4f;        // speed walk in combat
	private static final double			LOOKRANGE_DEFAULT = 20;            // range of look
	private static final NamespacedKey	KEY_BOSS = new NamespacedKey(RpgCraft.name(), "boss");
	private static final NamespacedKey	KEY_OWNER = new NamespacedKey(RpgCraft.name(), "owner");
	private static final NamespacedKey	KEY_PET = new NamespacedKey(RpgCraft.name(), "pet");
	// persistent
	@Persist
	private String					templateSaved = null;
	@Persist
	private String					raceSaved = null;
	@Persist
	private String					formSaved = null;
	@Persist
	private Map<String, Integer>	statsSaved = new HashMap<>();
	@Persist
	private Set<String>				teamsSaved = new HashSet<>();
	@Persist
	private int						level = LEVEL_DEFAULT;
	@Persist
	private Location				respawn = null;
	@Persist
	private int						respawnTime = RESPAWNTIME_DEFAULT;
	@Persist
    private double					health = HEALTH_DEFAULT;
	@Persist
    private double					damage = DAMAGE_DEFAULT;
	@Persist
    private double					patrolRange = PATROLRANGE_DEFAULT;
	@Persist
    private double					aggroRange = AGGRORANGE_DEFAULT;
	@Persist
    private double					chaseRange = CHASERANGE_DEFAULT;
	@Persist
    private double					attackRangeClose = ATTACKRANGECLOSE_DEFAULT;
	@Persist
    private double					attackRangeRanged = ATTACKRANGERANGED_DEFAULT;
	@Persist
    private float					attackRate = ATTACKRATE_DEFAULT;
	@Persist
    private float					spellRate = SPELLRATE_DEFAULT;
	@Persist
	private float					speed = SPEED_DEFAULT;
	@Persist
	private float					speedCombat = SPEEDCOMBAT_DEFAULT;
	@Persist
	private double					lookRange = LOOKRANGE_DEFAULT;
	@Persist
	private boolean					isBoss = false;
	@Persist
	private UUID					uuidOwner = null;
	// utils
    private int						tick;
    private final int				tickActive;
    private final int				tickUnactive;
    private int						loseAggro;
    private final double			rangeActive;
    private final double			rangeActiveSquared;
    private AttributeHelper			attributeHelper;
    private TargetHelper			targetHelper;
    private GoalPatrol				goalPatrol;

    public TraitSentinel() {
        super("traitsentinel");
		// init utils
    	this.tick = 0;
    	this.tickActive = 10;     // 0.5 seconds if active
    	this.tickUnactive = 80;   // 4 seconds if unactive
    	this.rangeActive = 60;    // range where npc is active
    	this.rangeActiveSquared = rangeActive * rangeActive;
		this.attributeHelper = null;
    	this.targetHelper = null;
		this.goalPatrol = null;
    }

	@Override
	public void onAttach() {
		// if (raceSaved == null)
		// if (formSaved == null)
		if (statsSaved == null) statsSaved = new HashMap<>();
		if (teamsSaved == null) teamsSaved = new HashSet<>();
		if (level <= 0) level = LEVEL_DEFAULT;
		// if (respawnTime <= 0)
	    if (health <= 0) health = HEALTH_DEFAULT;
	    if (damage <= 0) damage = DAMAGE_DEFAULT;
		if (patrolRange <= 0) patrolRange = PATROLRANGE_DEFAULT;
	    if (aggroRange <= 0) aggroRange = AGGRORANGE_DEFAULT;
	    if (chaseRange <= 0) chaseRange = CHASERANGE_DEFAULT;
	    if (attackRangeClose <= 0) attackRangeClose = ATTACKRANGECLOSE_DEFAULT;
	    if (attackRangeRanged <= 0) attackRangeRanged = ATTACKRANGERANGED_DEFAULT;
	    if (attackRate <= 0) attackRate = ATTACKRATE_DEFAULT;
	    if (spellRate <= 0) spellRate = SPELLRATE_DEFAULT;
	    if (speed <= 0) speed = SPEED_DEFAULT;
	    if (speedCombat <= 0) speedCombat = SPEEDCOMBAT_DEFAULT;
	    if (lookRange <= 0) lookRange = LOOKRANGE_DEFAULT;
	    // if (uuidOwner == null)
	}

	@Override
	public void onSpawn() {
		npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, true);
		// trait lookclose
		LookClose	lookClose = npc.getOrAddTrait(LookClose.class);
		lookClose.lookClose(true);
		lookClose.setRealisticLooking(true);
		lookClose.setRange(lookRange);
		// living entity
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		if (respawn != null)
			livingNPC.teleport(respawn);
		TemplateType	templateType = TemplateType.fromString(templateSaved);
		if (templateType != null)
			spawnTemplate(TemplateType.fromString(templateSaved));
		else {
			// set health
			AttributeInstance	instance = livingNPC.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			if (instance != null)
				instance.setBaseValue(health);
			// set damage
			instance = livingNPC.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
			if (instance != null)
				instance.setBaseValue(damage);
			// set teams
			for (String s: teamsSaved) {
				Team.add(livingNPC, TeamType.fromString(s));
			}
			// isboss
			if (isBoss)
				Data.setBoolean(livingNPC.getPersistentDataContainer(), KEY_BOSS);
			// set scale
			EntityManager.setScale(npc);
		}
		// ispet
		if (uuidOwner != null) {
			Player	owner = Bukkit.getPlayer(uuidOwner);
			if (owner != null) {
				Data.setString(owner.getPersistentDataContainer(), KEY_PET, livingNPC.getUniqueId().toString());
				Data.setString(livingNPC.getPersistentDataContainer(), KEY_OWNER, uuidOwner.toString());
			}
		}
		if (patrolRange > 0) {
			goalPatrol = new GoalPatrol(npc, patrolRange);
			npc.getDefaultGoalController().addGoal(goalPatrol, 1);
		}
		// hide name
		Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false));
	}

	@Override
	public void onDespawn() {
		this.attributeHelper = null;
		this.targetHelper = null;
		if (goalPatrol != null)
			npc.getDefaultGoalController().removeGoal(goalPatrol);
	}

	public void init(ItemCustomManager itemCustomManager, NPCSpellManager npcSpellManager) {
		this.attributeHelper = new AttributeHelper(this);
		this.targetHelper = new TargetHelper(itemCustomManager, npcSpellManager, npc, attributeHelper);
	}

    @Override
    public void run() {
		// check all seconds
		if (targetHelper == null) return;
		if (tick-- > 0) return;
        if (npc == null || !npc.isSpawned()) return;
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		if (loseAggro > 0) {
			livingNPC.setHealth(health);
			if (--loseAggro == 0)
				npc.getNavigator().getDefaultParameters().speedModifier(speed);
			return;
		}
		if (!isActive(livingNPC)) {
			tick = tickUnactive;
			return;
		}
		tick = tickActive;
		if (uuidOwner != null) 
			targetHelper.findTargetPet(livingNPC);
		else
			targetHelper.findTarget(livingNPC);
		loseAggro = targetHelper.attackTarget(livingNPC);
    }

	private boolean isActive(LivingEntity livingNPC) {
		World	world = livingNPC.getWorld();
		for (LivingEntity livingTarget: world.getNearbyLivingEntities(livingNPC.getLocation(), rangeActive, rangeActive, rangeActive)) {
			if (livingTarget.equals(livingNPC)) continue;
			if (!(livingTarget instanceof Player)) continue;
			if (livingTarget.getLocation().distanceSquared(livingNPC.getLocation()) > rangeActiveSquared) continue;
			return true;
		}
		return false;
	}

	public void addAggro(LivingEntity livingEntity, double damage) {
		targetHelper.addAggro(livingEntity, damage);
	}

	// getter + setter

	public void spawnTemplate(TemplateType templateType) {
		if (templateType == null) return;
        setFormType(templateType.getFormType());
		setRaceType(templateType.getRaceType());
		removeAllStat();
		Map<String, Integer>	stats = templateType.getStats(level);
		if (stats != null) {
			for (Entry<String, Integer> entry: stats.entrySet()) {
				setStat(StatSecondary.fromString(entry.getKey()), entry.getValue());
			}
		}
		removeAllTeam();
		Set<String>	teams = templateType.getTeams();
		if (teams != null) {
			for (String team: teams) {
				addTeam(TeamType.fromString(team));
			}
		}
		// setRespawnTime(templateType.getRespawnTime());
		setHealth(templateType.getHealth(level));
		setDamage(templateType.getDamage(level));
		setAttackRate(templateType.getAttackRate());
		setSpellRate(templateType.getSpellRate());
		setSpeed(templateType.getSpeed());
		setSpeedCombat(templateType.getSpeedCombat());
		setBoss(templateType.isBoss());
	}

	public void setTemplate(TemplateType templateType) {
		if (templateType == null) return;
		this.templateSaved = templateType.getName();
		npc.setName(templateType.getHideName());
		EntityType	entityType = templateType.getEntityType();
		npc.setBukkitEntityType(entityType);
		if (entityType == EntityType.PLAYER) {
			String		skinName = templateType.getSkin();
        	SkinTrait   skinTrait = npc.getOrAddTrait(SkinTrait.class);
			SkinData	skinData = Skin.getSkinData(skinName);
			if (skinData != null)
				skinTrait.setSkinPersistent(skinName, skinData.getSignature(), skinData.getValue());
			else
				skinTrait.setSkinName(skinName);
		}
        setFormType(templateType.getFormType());
		setRaceType(templateType.getRaceType());
		removeAllStat();
		Map<String, Integer>	stats = templateType.getStats(level);
		if (stats != null) {
			for (Entry<String, Integer> entry: stats.entrySet()) {
				setStat(StatSecondary.fromString(entry.getKey()), entry.getValue());
			}
		}
		removeAllTeam();
		Set<String>	teams = templateType.getTeams();
		if (teams != null) {
			for (String team: teams) {
				addTeam(TeamType.fromString(team));
			}
		}
		setRespawnTime(templateType.getRespawnTime());
		setHealth(templateType.getHealth(level));
		setDamage(templateType.getDamage(level));
		setAttackRate(templateType.getAttackRate());
		setSpellRate(templateType.getSpellRate());
		setSpeed(templateType.getSpeed());
		setSpeedCombat(templateType.getSpeedCombat());
		setBoss(templateType.isBoss());
	}

	public void removeTemplate() {
		this.templateSaved = null;
	}

	public TemplateType getTemplateType() {
		return TemplateType.fromString(templateSaved);
	}

	public RaceType getRaceType() {
		return RaceType.fromString(raceSaved);
	}

	public void setRaceType(RaceType race) {
		this.raceSaved = race.getName();
	}

	public FormType getFormType() {
		return FormType.fromString(formSaved);
	}

	public void setFormType(FormType form) {
		this.formSaved = form.getName();
		EntityManager.setScale(npc);
	}

	public Map<String, Integer> getStatsSaved() {
		return statsSaved;
	}

	public void setStat(StatSecondary type, int value) {
		if (type == null) return;
		statsSaved.put(type.getName(), value);
	}

	public int getStat(StatSecondary type) {
		if (statsSaved == null) return 0;
		Integer	value = statsSaved.get(type.getName());
		if (value == null) return 0;
		return value;
	}

	public void removeAllStat() {
		this.statsSaved.clear();
	}

	public Set<String> getTeamsSaved() {
		return teamsSaved;
	}

	public void addTeam(TeamType type) {
		if (type == null) return;
		this.teamsSaved.add(type.getName());
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		Team.add(livingNPC, type);
	}

	public void removeTeam(TeamType type) {
		if (type == null) return;
		this.teamsSaved.remove(type.getName());
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		Team.remove(livingNPC, type);
	}

	public void removeAllTeam() {
		this.teamsSaved.clear();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		if (attributeHelper != null)
			attributeHelper.setLevel(level);
		TemplateType	tmp = TemplateType.fromString(templateSaved);
		if (tmp != null)
			statsSaved = tmp.getStats(level);
	}

	public Location getRespawn() {
		return respawn;
	}

	public void setRespawn(Location respawn) {
		this.respawn = respawn;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		if (health <= 0) return;
		this.health = health;
		if (attributeHelper != null)
			attributeHelper.setHealth(health);
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		AttributeInstance	instance = livingNPC.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (instance != null)
			instance.setBaseValue(health);
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		if (damage <= 0) return;
		this.damage = damage;
		if (attributeHelper != null)
			attributeHelper.setDamage(damage);
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		AttributeInstance	instance = livingNPC.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		if (instance != null)
			instance.setBaseValue(damage);
	}

	public double getPatrolRange() {
		return patrolRange;
	}

	public void setPatrolRange(double range) {
		if (range < 0) return;
		patrolRange = range;
		if (goalPatrol != null)
			npc.getDefaultGoalController().removeGoal(goalPatrol);
		if (patrolRange > 0) {
			goalPatrol = new GoalPatrol(npc, patrolRange);
			npc.getDefaultGoalController().addGoal(goalPatrol, 1);
		}
		else
			goalPatrol = null;
	}

	public double getAggroRange() {
		return aggroRange;
	}

	public void setAggroRange(double aggroRange) {
		if (aggroRange <= 0) return;
		this.aggroRange = aggroRange;
		if (attributeHelper != null)
			attributeHelper.setAggroRange(aggroRange);
	}

	public double getChaseRange() {
		return chaseRange;
	}

	public void setChaseRange(double chaseRange) {
		if (chaseRange <= 0) return;
		this.chaseRange = chaseRange;
		if (attributeHelper != null)
			attributeHelper.setChaseRange(chaseRange);
	}

	public double getAttackRangeClose() {
		return attackRangeClose;
	}

	public void setAttackRangeClose(double attackRangeClose) {
		if (attackRangeClose <= 0) return;
		this.attackRangeClose = attackRangeClose;
		if (attributeHelper != null)
			attributeHelper.setAttackRangeClose(attackRangeClose);
	}

	public double getAttackRangeRanged() {
		return attackRangeRanged;
	}

	public void setAttackRangeRanged(double attackRangeRanged) {
		if (attackRangeRanged <= 0) return;
		this.attackRangeRanged = attackRangeRanged;
		if (attributeHelper != null)
			attributeHelper.setAttackRangeRanged(attackRangeRanged);
	}

	public float getAttackRate() {
		return attackRate;
	}

	public void setAttackRate(float attackRate) {
		if (attackRate <= 0) return;
		this.attackRate = attackRate;
		if (attributeHelper != null)
			attributeHelper.setAttackRate(attackRate);
	}

	public float getSpellRate() {
		return spellRate;
	}

	public void setSpellRate(float spellRate) {
		if (spellRate <= 0) return;
		this.spellRate = spellRate;
		if (attributeHelper != null)
			attributeHelper.setSpellRate(spellRate);
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		if (speed <= 0) return;
		this.speed = speed;
		if (attributeHelper != null)
			attributeHelper.setSpeed(speed);
		npc.getNavigator().getDefaultParameters().speedModifier(speed);
	}

	public float getSpeedCombat() {
		return speedCombat;
	}

	public void setSpeedCombat(float speedCombat) {
		if (speedCombat <= 0) return;
		this.speedCombat = speedCombat;
		if (attributeHelper != null)
			attributeHelper.setSpeedCombat(speedCombat);
	}

	public double getLookRange() {
		return lookRange;
	}

	public void setLookRange(double lookRange) {
		if (lookRange <= 0) return;
		this.lookRange = lookRange;
		npc.getOrAddTrait(LookClose.class).setRange(lookRange);
	}

	public boolean isBoss() {
		return isBoss;
	}

	public static boolean isBoss(LivingEntity livingEntity) {
		return Data.hasBoolean(livingEntity.getPersistentDataContainer(), KEY_BOSS);
	}

	public void setBoss(boolean isBoss) {
		this.isBoss = isBoss;
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		if (isBoss)
			Data.setBoolean(livingNPC.getPersistentDataContainer(), KEY_BOSS);
		else
			Data.remove(livingNPC.getPersistentDataContainer(), KEY_BOSS);
	}

	public boolean isPet() {
		return uuidOwner != null;
	}

	public UUID getUUIDOwner() {
		return uuidOwner;
	}

	public static boolean isOwner(Player player) {
		return Data.hasString(player.getPersistentDataContainer(), KEY_PET);
	}

	public static LivingEntity getPet(Player player) {
		String	uuidString = Data.getString(player.getPersistentDataContainer(), KEY_PET);
		if (uuidString == null) return null;
		if (!(Bukkit.getEntity(UUID.fromString(uuidString)) instanceof LivingEntity pet)) return null;
		return pet;
	}

	public static boolean isPet(LivingEntity livingEntity) {
		return Data.hasString(livingEntity.getPersistentDataContainer(), KEY_OWNER);
	}

	public static Player getOwner(LivingEntity livingEntity) {
		String	uuidString = Data.getString(livingEntity.getPersistentDataContainer(), KEY_OWNER);
		if (uuidString == null) return null;
		Player	player = Bukkit.getPlayer(UUID.fromString(uuidString));
		return player;
	}

	public void setOwner(Player player) {
		UUID	oldUUID = uuidOwner;
		this.uuidOwner = (player == null ? null : player.getUniqueId());
		if (attributeHelper != null)
			attributeHelper.setOwner(player);
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		if (oldUUID != null) {
			Player	oldOwner = Bukkit.getPlayer(oldUUID);
			if (oldOwner != null) {
				Data.remove(oldOwner.getPersistentDataContainer(), KEY_PET);
				Data.remove(livingNPC.getPersistentDataContainer(), KEY_OWNER);
			}
			return;
		}
		if (player != null) {
			Data.setString(player.getPersistentDataContainer(), KEY_PET, livingNPC.getUniqueId().toString());
			Data.setString(livingNPC.getPersistentDataContainer(), KEY_OWNER, player.getUniqueId().toString());
		}
	}

	public TargetHelper getTargetHelper() {
		return targetHelper;
	}

	public boolean haveTarget() {
		return targetHelper.getTarget() == null && targetHelper.getTargetHide() == null;
	}
}
