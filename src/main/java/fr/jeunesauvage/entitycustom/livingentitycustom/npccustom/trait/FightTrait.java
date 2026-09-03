package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeModifier;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.goal.GoalPatrol;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;

public class FightTrait extends Trait {
	// default values
	private static final int			LEVEL_DEFAULT = 1;                                                     // level
	public static final int				RESPAWNTIME_DEFAULT = TemplateType.DEFAULT.getRespawnTime();           // respawn time (in seconds)
	public static final double			HEALTHBYLEVEL_DEFAULT = TemplateType.DEFAULT.getHealth(LEVEL_DEFAULT); // health
	public static final double			DAMAGEBYLEVEL_DEFAULT = TemplateType.DEFAULT.getDamage(LEVEL_DEFAULT); // damage of attack
	private static final double			PATROLRANGE_DEFAULT = 0;                                               // range of patrol
	public static final double			AGGRORANGE_DEFAULT = 30;                                               // range of aggro
	public static final double			CHASERANGE_DEFAULT = 70;                                               // range of chase
	public static final double			ATTACKRANGERANGED_DEFAULT = 15;                                        // range of attack ranged
	private static final double			ATTACKRANGECLOSE_DEFAULT = 3;                                          // range of attack close
	public static final float			ATTACKRATE_DEFAULT = TemplateType.DEFAULT.getAttackRate();             // time (in seconds) between each attack
	public static final float			SPELLRATE_DEFAULT = TemplateType.DEFAULT.getSpellRate();               // time (in seconds) between each spell
	public static final float			SPEED_DEFAULT = TemplateType.DEFAULT.getSpeed();                       // speed walk
	public static final float			SPEEDCOMBAT_DEFAULT = TemplateType.DEFAULT.getSpeedCombat();           // speed walk in combat
	private static final double			LOOKRANGE_DEFAULT = 20;                                                // range of look

	// persistent
	@Persist
	private String					templateType = null;
	@Persist
	private String					raceType = null;
	@Persist
	private String					formType = null;
	@Persist
	private String					classType = null;
	@Persist
	private Map<String, Integer>	stats = new HashMap<>();
	@Persist
	private Map<Integer, String>	modifiers = new HashMap<>();
	@Persist
	private Set<String>				teams = new HashSet<>();
	@Persist
	private int						level = LEVEL_DEFAULT;
	@Persist
	private long					silence = 0;
	@Persist
	private Location				respawn = null;
	@Persist
	private int						respawnTime = RESPAWNTIME_DEFAULT;
	@Persist
    private double					health = HEALTHBYLEVEL_DEFAULT * level;
	@Persist
    private double					damage = DAMAGEBYLEVEL_DEFAULT * level;
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
	private UUID					ownerUUID = null;
	@Persist
	private UUID					petUUID = null;
	// utils
    private int						tick;
    private final int				tickActive;
    private final int				tickUnactive;
    private int						loseAggro;
    private final double			rangeActive;
    private final double			rangeActiveSquared;
    private FightData				fightData;
    private FightAI					FightAI;
    private GoalPatrol				goalPatrol;

    public FightTrait() {
        super("fighttrait");
		// init utils
    	this.tick = 0;
    	this.tickActive = 10;     // 0.5 seconds if active
    	this.tickUnactive = 80;   // 4 seconds if unactive
    	this.rangeActive = 60;    // range where npc is active
    	this.rangeActiveSquared = rangeActive * rangeActive;
		this.fightData = null;
    	this.FightAI = null;
		this.goalPatrol = null;
    }


    @Override
    public void onAttach() {
		TemplateType	templateType = getTemplateType();
		// name
		npc.setName(templateType.getHideName());
		// entity type
		npc.setBukkitEntityType(templateType.getEntityType());
	}

	@Override
	public void onSpawn() {
		// trait lookclose
		LookClose	lookClose = npc.getOrAddTrait(LookClose.class);
		lookClose.lookClose(true);
		lookClose.setRealisticLooking(true);
		lookClose.setRange(lookRange);
		// template
		setTemplate(getTemplateType());
		// patrol
		if (patrolRange > 0) {
			goalPatrol = new GoalPatrol(npc, patrolRange);
			npc.getDefaultGoalController().addGoal(goalPatrol, 1);
		}
		fightData = new FightData(this);
		FightAI = new FightAI(npc, fightData);
	}

	@Override
	public void onDespawn() {
		FightAI = null;
		fightData = null;
		if (goalPatrol != null) {
			npc.getDefaultGoalController().removeGoal(goalPatrol);
			goalPatrol = null;
		}
	}

    @Override
    public void run() {
		// check all seconds
		if (tick-- > 0) return;
		if (FightAI == null) return;
        if (npc == null || !npc.isSpawned() || getTemplateType() == TemplateType.DEFAULT) return;
		NPCCustom	npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(npc.getUniqueId());
		if (npcCustom == null) return;
		if (loseAggro > 0) {
			npcCustom.setHealth(health);
			if (--loseAggro == 0)
				npc.getNavigator().getDefaultParameters().speedModifier(speed);
			return;
		}
		if (!isActive(npcCustom)) {
			tick = tickUnactive;
			return;
		}
		tick = tickActive;
		if (ownerUUID != null) 
			FightAI.findTargetPet(npcCustom);
		else
			FightAI.findTarget(npcCustom);
		loseAggro = FightAI.attackTarget(npcCustom);
    }

	private boolean isActive(NPCCustom npcCustom) {
		World	world = npcCustom.getWorld();
		if (world == null) return false;
		for (LivingEntity livingTarget: world.getNearbyLivingEntities(npcCustom.getLocation(), rangeActive)) {
			PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(livingTarget.getUniqueId());
			if (playerCustom == null) continue;
			if (playerCustom.getLocation().distanceSquared(npcCustom.getLocation()) > rangeActiveSquared) continue;
			return true;
		}
		return false;
	}

	public void addAggro(LivingEntityCustom livingEntityCustom, double damage) {
		if (FightAI == null) return;
		FightAI.addAggro(livingEntityCustom, damage);
	}

	public void cleanAggro() {
		if (FightAI == null) return;
		FightAI.cleanAggro();
	}

	// getter + setter

	public TemplateType getTemplateType() {
		return TemplateType.fromString(templateType);
	}

	public void setTemplate(TemplateType templateType) {
		if (templateType == null) return;
		this.templateType = templateType.getName();
		if (npc.getEntity() instanceof LivingEntity l && l.getType() != templateType.getEntityType()) {
			npc.setBukkitEntityType(templateType.getEntityType());
			return;
		}
		// race + form + class
		setRaceType(templateType.getRaceType());
        setFormType(templateType.getFormType());
		setClassType(templateType.getClassType());
		// stats
		stats.clear();
		Map<String, Integer>	statsTmp = templateType.getStats(level);
		if (statsTmp != null) statsTmp.forEach((s, v) -> setStat(StatType.fromString(s), v));
		// teams
		teams.clear();
		Set<TeamType> teamsCopy = templateType.getTeams();
		if (teamsCopy != null) teamsCopy.forEach(t -> addTeam(t));
		// others
		setRespawnTime(templateType.getRespawnTime());
		setHealth(templateType.getHealth(level));
		setDamage(templateType.getDamage(level));
		setAttackRate(templateType.getAttackRate());
		setSpellRate(templateType.getSpellRate());
		setSpeed(templateType.getSpeed());
		setSpeedCombat(templateType.getSpeedCombat());
		setBoss(templateType.isBoss());
		// name
		npc.setName(templateType.getHideName());
		npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, true);
		Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, false));
		if (fightData != null) fightData.setTemplateType(templateType);
	}

	public RaceType getRaceType() {
		return RaceType.fromString(raceType);
	}

	public void setRaceType(RaceType raceType) {
		this.raceType = raceType.getName();
	}

	public FormType getFormType() {
		return FormType.fromString(formType);
	}

	public void setFormType(FormType formType) {
		this.formType = formType.getName();
	}

	public ClassType getClassType() {
		return ClassType.fromString(classType);
	}

	public void setClassType(ClassType classType) {
		this.classType = classType.getName();
	}

	public Map<String, Integer> getStats() {
		return stats;
	}

	public void setStat(StatType statType, int value) {
		if (statType == null) return;
		stats.put(statType.getName(), value);
	}

	public int getStat(StatType statType) {
		if (statType == null) return 0;
		Integer	value = stats.get(statType.getName());
		if (value == null) return 0;
		return value;
	}

	public Map<Integer, String> getModifiers() {
		return modifiers;
	}

	public void addModifier(AttributeModifier m) {
		if (m == null) return;
		int		id = m.getId();
		String	attributeModifierString = m.getType().getName() + '/' + m.getValue() + '/' + m.getEnd();
		modifiers.put(id, attributeModifierString);
	}

	public void deleteModifier(int id) {
		modifiers.remove(id);
	}

	public Set<TeamType> getTeams() {
		Set<TeamType>	result = new HashSet<>();
		teams.forEach(t -> result.add(TeamType.fromString(t)));
		return result;
	}

	public void addTeam(TeamType teamType) {
		if (teamType == null) return;
		teams.add(teamType.getName());
	}

	public void removeTeam(TeamType teamType) {
		if (teamType == null) return;
		teams.remove(teamType.getName());
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
		if (fightData != null) fightData.setLevel(level);
		TemplateType	tmp = getTemplateType();
		setHealth(tmp.getHealth(level));
		setDamage(tmp.getDamage(level));
		Map<String, Integer>	tmpStats = tmp.getStats(level);
		if (tmpStats != null)
			this.stats = tmpStats;
	}

	public long getSilence() {
		return silence;
	}

	public void setSilence(long silence) {
		this.silence = silence;
		if (fightData != null) fightData.setSilence(silence);
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
		if (fightData != null) fightData.setHealth(health);
		if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
		AttributeInstance	attributeInstance = livingNPC.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		if (attributeInstance != null)
			attributeInstance.setBaseValue(health);
		if (livingNPC.getHealth() > health)
			livingNPC.setHealth(health);
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		if (damage <= 0) return;
		this.damage = damage;
		if (fightData != null) fightData.setDamage(damage);
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
		if (fightData != null) fightData.setAggroRange(aggroRange);
	}

	public double getChaseRange() {
		return chaseRange;
	}

	public void setChaseRange(double chaseRange) {
		if (chaseRange <= 0) return;
		this.chaseRange = chaseRange;
		if (fightData != null) fightData.setChaseRange(chaseRange);
	}

	public double getAttackRangeClose() {
		return attackRangeClose;
	}

	public void setAttackRangeClose(double attackRangeClose) {
		if (attackRangeClose <= 0) return;
		this.attackRangeClose = attackRangeClose;
		if (fightData != null) fightData.setAttackRangeClose(attackRangeClose);
	}

	public double getAttackRangeRanged() {
		return attackRangeRanged;
	}

	public void setAttackRangeRanged(double attackRangeRanged) {
		if (attackRangeRanged <= 0) return;
		this.attackRangeRanged = attackRangeRanged;
		if (fightData != null) fightData.setAttackRangeRanged(attackRangeRanged);
	}

	public float getAttackRate() {
		return attackRate;
	}

	public void setAttackRate(float attackRate) {
		if (attackRate <= 0) return;
		this.attackRate = attackRate;
		if (fightData != null) fightData.setAttackRate(attackRate);
	}

	public float getSpellRate() {
		return spellRate;
	}

	public void setSpellRate(float spellRate) {
		if (spellRate <= 0) return;
		this.spellRate = spellRate;
		if (fightData != null) fightData.setSpellRate(spellRate);
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		if (speed <= 0) return;
		this.speed = speed;
		if (fightData != null) fightData.setSpeed(speed);
		npc.getNavigator().getDefaultParameters().speedModifier(speed);
	}

	public float getSpeedCombat() {
		return speedCombat;
	}

	public void setSpeedCombat(float speedCombat) {
		if (speedCombat <= 0) return;
		this.speedCombat = speedCombat;
		if (fightData != null) fightData.setSpeedCombat(speedCombat);
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

	public void setBoss(boolean isBoss) {
		this.isBoss = isBoss;
	}

	// is pet

	public boolean isPet() {
		return ownerUUID != null;
	}

	public LivingEntityCustom getOwner() {
		if (ownerUUID == null) return null;
		LivingEntityCustom	livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(ownerUUID);
		if (livingEntityCustom == null) ownerUUID = null;
		return livingEntityCustom;
	}

	public UUID	getOwnerUUID() {
		return ownerUUID;
	}

	public void setOwner(UUID uuid) {
		ownerUUID = uuid;
		if (fightData != null) fightData.setOwnerUUID(uuid);
	}

	// is owner

	public boolean isOwner() {
		return petUUID != null;
	}

	public LivingEntityCustom getPet() {
		if (petUUID == null) return null;
		LivingEntityCustom	livingEntityCustom = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(petUUID);
		if (livingEntityCustom == null) petUUID = null;
		return livingEntityCustom;
	}

	public UUID	getPetUUID() {
		return ownerUUID;
	}

	public void setPet(UUID uuid) {
		petUUID = uuid;
		if (fightData != null) fightData.setPetUUID(uuid);
	}

	// fight IA

	public FightAI getFightAI() {
		return FightAI;
	}

	public boolean haveTarget() {
		if (FightAI == null) return false;
		return FightAI.getTarget() == null && FightAI.getTargetHide() == null;
	}
}
