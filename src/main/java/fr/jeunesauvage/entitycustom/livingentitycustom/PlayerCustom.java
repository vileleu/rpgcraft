package fr.jeunesauvage.entitycustom.livingentitycustom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.DataTask;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeModifier;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeType;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillSecondary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.Stat;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.SkinData;
import fr.jeunesauvage.entitycustom.livingentitycustom.group.Group;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.bossbar.BossBarData;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.bossbar.TargetData;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.cooldown.Cooldown;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerType;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.scoreboardcustom.ScoreboardCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.silence.Silence;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.sound.QuoteType;
import fr.jeunesauvage.sound.SoundManager;
import fr.jeunesauvage.sound.SoundPacket;
import fr.jeunesauvage.sound.SoundType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;

public final class PlayerCustom implements LivingEntityCustom {
    private static final NamespacedKey              KEY_MARK = new NamespacedKey(RpgCraft.name(), "mark");
	public static final double	                    RANGETARGET_DEFAULT = 60; // blocks
	public static final long	                    TIMETARGET_DEFAULT = 200; // ticks
    private final Player                            player;
    private RaceType                                raceType;
    private FormType                                formType;
    private ClassType                               classType;
    private PowerCustom                             power;
    private final Set<TeamType>                     teams = new HashSet<>();
    private final Map<StatType, Stat>               stats = new HashMap<>();
    private final Map<SkillType, Skill>             skills = new HashMap<>();
    private final Map<Integer, AttributeModifier>   modifiers = new HashMap<>();
    private int                                     modifierId = 1;
    private UUID                                    ownerUUID;
    private UUID                                    petUUID;
    private boolean                                 damageIsUnmodifiable = false;
    private Group                                   group = null;
    private final Cooldown                          cooldown;
    private final Silence                           silence;
    private final ScoreboardCustom                  scoreboardCustom;
	private final TargetData                        targetData;
	private final DataTask<Long>                    mark;

    public PlayerCustom(Player player) {
        this.player = player;
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        this.raceType = RaceType.fromString(Data.getString(pdc, RaceType.KEY));
        this.formType = FormType.fromString(Data.getString(pdc, FormType.KEY));
        this.classType = ClassType.fromString(Data.getString(pdc, ClassType.KEY));
        this.power = classType.buildPower();
        this.cooldown = new Cooldown(player);
        this.silence = new Silence(player);
        this.scoreboardCustom = new ScoreboardCustom(this);
        this.targetData = new TargetData(this);
        this.mark = new DataTask<Long>(0l, null);
        loadTeams();
        loadStats();
        loadSkills();
        loadModifiers();
        loadMark();
    }

    private void loadTeams() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.getKeys().forEach(k -> {
            for (TeamType teamType: TeamType.values()) {
                if (k.equals(teamType.getKey()))
                    teams.add(teamType);
            }
        });
    }

    private void loadStats() {
        for (StatPrimary statPrimary: StatPrimary.values()) {
            stats.put(statPrimary, new Stat(statPrimary));
        }
        for (StatSecondary statSecondary: StatSecondary.values()) {
            stats.put(statSecondary, new Stat(statSecondary));
        }
    }

    private void loadSkills() {
        for (SkillPrimary skillPrimary: SkillPrimary.values()) {
            skills.put(skillPrimary, new Skill(player, skillPrimary));
        }
        for (SkillSecondary skillSecondary: SkillSecondary.values()) {
            skills.put(skillSecondary, new Skill(player, skillSecondary));
        }
    }

    private void loadModifiers() {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        for (NamespacedKey keyEnd: Set.copyOf(pdc.getKeys())) {
            String  s = keyEnd.getKey();
            if (s.startsWith(AttributeModifier.MODIFIER_END_STRING)) {
                String[]        array = s.split("/");
                NamespacedKey   keyValue = new NamespacedKey(RpgCraft.name(), AttributeModifier.MODIFIER_VALUE_STRING + '/' + array[1] + '/' + array[2]);
                final int       id;
                try {
                    id = Integer.parseInt(array[2]);
                }
                catch (NumberFormatException e) {
                    pdc.remove(keyValue);
                    pdc.remove(keyEnd);
                    continue;
                }
                int     duration;
                long    end = Data.getLong(pdc, keyEnd);
                long	now = System.currentTimeMillis();
                if (end < 0)
                    duration = (int)end;
                else if (end <= now) {
                    pdc.remove(keyValue);
                    pdc.remove(keyEnd);
                    continue;
                }
                else {
                    duration = (int)(((end - now) / 1000));
                    if (duration <= 0) {
                        pdc.remove(keyValue);
                        pdc.remove(keyEnd);
                        continue;
                    }
                }
                AttributeType   attributeType = AttributeType.fromString(array[1]);
                if (attributeType == null) {
                    pdc.remove(keyValue);
                    pdc.remove(keyEnd);
                    continue;
                }
                int         value = Data.getInteger(pdc, keyValue);
                BukkitTask  task = (duration <= 0 ? null : Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> deleteModifier(id), Data.d(duration)));
                modifiers.put(id, new AttributeModifier(
                    attributeType,
                    value,
                    duration,
                    id,
                    task
                ));
            }
        }
    }

    private void loadMark() {
        long    end = Data.getLong(player.getPersistentDataContainer(), KEY_MARK);
        if (end == 0) return;
        mark.setData(end);
        mark.setTask(new BukkitRunnable() {
		        int     seconds = 0;
                long    end = mark.getData();
			    int     secondsMax = (int)((end - System.currentTimeMillis()) / 1000 + 1);
		        @Override
		        public void run() {
                    if (seconds >= secondsMax || !isPresent()) {
                        deleteMark();
                        return;
                    }
                    int n = (int)((end - System.currentTimeMillis()) / 1000 + 1);
                    sendActionBar(Message.c(Component.text("Mark: ").color(NamedTextColor.RED)
                        .append(Component.text(n).color(NamedTextColor.YELLOW))
                        .append(Component.text("s").color(NamedTextColor.RED))
                    ));
	    	    	seconds++;
		        }
		    }.runTaskTimer(RpgCraft.instance(), 0L, 20L)
        );
    }

    public void addMark() {
        mark.cancel();
        mark.setData(System.currentTimeMillis() + (60 * 1000l));
        mark.setTask(new BukkitRunnable() {
		        int     seconds = 0;
			    int     secondsMax = 60;
                long    end = mark.getData();
		        @Override
		        public void run() {
                    if (seconds >= secondsMax || !isPresent()) {
                        deleteMark();
                        return;
                    }
                    int n = (int)((end - System.currentTimeMillis()) / 1000 + 1);
                    sendActionBar(Message.c(Component.text("Mark: ").color(NamedTextColor.RED)
                        .append(Component.text(n).color(NamedTextColor.YELLOW))
                        .append(Component.text("s").color(NamedTextColor.RED))
                    ));
	    	    	seconds++;
		        }
		    }.runTaskTimer(RpgCraft.instance(), 0L, 20L)
        );
        Data.setLong(player.getPersistentDataContainer(), KEY_MARK, mark.getData());
    }

    public boolean isMarked() {
        return mark.getData() != 0;
    }

    public void deleteMark() {
        mark.setData(0l);
        mark.cancel();
    }

    public void sendMessage(Component component) {
        player.sendMessage(component);
    }

    public void sendActionBar(Component component) {
        player.sendActionBar(component);
    }

    public void addCooldown(Material material, int time) {
        cooldown.add(material, time);
    }

    public int hasCooldown(Material material) {
        return cooldown.has(material);
    }

    public void refreshCooldown() {
        cooldown.refresh();
    }

    public PlayerInventory getInventory() {
        return player.getInventory();
    }

    public void openInventory(Inventory inventory) {
        player.openInventory(inventory);
    }

    public void showBossBar(BossBar bossBar) {
        player.showBossBar(bossBar);
    }

    public void hideBossBar(BossBar bossBar) {
        player.hideBossBar(bossBar);
    }

    public Player getPlayer() {
        return player;
    }

    public ScoreboardCustom getScoreboardCustom() {
        return scoreboardCustom;
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public int getEntityId() {
        return player.getEntityId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public World getWorld() {
        return player.getWorld();
    }

    @Override
    public double getWidth() {
        return player.getWidth();
    }

    @Override
    public double getHeight() {
        return player.getHeight();
    }

    @Override
    public boolean isPresent() {
        return player.isOnline() && !player.isDead() && player.isValid();
    }

    @Override
    public void teleport(Location location) {
        player.teleport(location);
    }

    @Override
    public void setFallDistance(Float fallDistance) {
        player.setFallDistance(fallDistance);
    }

    @Override
    public Vector getVelocity() {
        return player.getVelocity();
    }    

    @Override
    public void setVelocity(Vector vector) {
        player.setVelocity(vector);
    }

    @Override
    public LivingEntity getLivingEntity() {
        return player;
    }

    @Override
    public Location getEyeLocation() {
        return player.getEyeLocation();
    }

    @Override
    public EntityType getType() {
        return player.getType();
    }

    @Override
    public void swingMainHand() {
        player.swingMainHand();
    }

    @Override
    public void playEffect(EntityEffect entityEffect) {
        player.playEffect(entityEffect);
    }

    @Override
    public EntityEquipment getEquipment() {
        return player.getEquipment();
    }

    @Override
    public boolean attackIsInCooldown() {
        return player.getAttackCooldown() != 1f;
    }

    @Override
    public boolean isCreative() {
        return player.getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public boolean isBlocking() {
        return player.isBlocking();
    }

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public double getHealth() {
        return player.getHealth();
    }

    @Override
    public void setHealth(double amount) {
        amount = Math.max(0, amount);
        amount = Math.min(getHealthMax(), amount);
        player.setHealth(amount);
    }

    @Override
    public double getHealthMax() {
        AttributeInstance   attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attributeInstance == null) return 0;
        return attributeInstance.getValue();
    }

    @Override
    public void setHealthMax(double amount) {
        AttributeInstance   attributeInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attributeInstance == null) return;
        amount = Math.max(1, amount);
        attributeInstance.setBaseValue(amount);
        if (getHealth() > amount) player.setHealth(amount);
        scoreboardCustom.refreshHealth(this);
    }

    public double getPower() {
        return power.getValue();
    }

    public void setPower(double amount) {
        power.setValueMax(Math.max(0, amount));
        scoreboardCustom.refreshPower(this);
    }

    public double getPowerMax() {
        return power.getValue();
    }

    public void setPowerMax(double amount) {
        amount = Math.max(1, amount);
        power.setValueMax(amount);
        if (getPower() > getPowerMax()) power.setValue(amount);
        scoreboardCustom.refreshPower(this);
    }

    @Override
    public void damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom) {
        player.damage(amount, combatDamage.getDamageSource(livingEntityCustom));
    }

    @Override
    public void damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom, boolean unmodifiable) {
        if (unmodifiable == true)
            damageIsUnmodifiable = true;
        player.damage(amount, combatDamage.getDamageSource(livingEntityCustom));
    }

    @Override
    public boolean damageIsUnmodifiable() {
        boolean result = damageIsUnmodifiable;
        damageIsUnmodifiable = false;
        return result;
    }

    @Override
    public void heal(double amount) {
        CombatDamage.heal(this, amount);
    }

    @Override
    public void setFireTicks(int fireTicks) {
        player.setFireTicks(fireTicks);
    }

    @Override
    public void setFreezeTicks(int fireTicks) {
        player.setFreezeTicks(fireTicks);
    }

    @Override
    public boolean isInvulnerable() {
        return player.isInvulnerable();
    }

    @Override
    public boolean isInvisible() {
        return player.isInvisible();
    }

    @Override
    public boolean isHandRaised() {
        return player.isHandRaised();
    }

    @Override
    public boolean hasLineOfSight(LivingEntityCustom livingEntityCustom) {
        return player.hasLineOfSight(livingEntityCustom.getLivingEntity());
    }

    @Override
    public BoundingBox getBoundingBox() {
        return player.getBoundingBox();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<T> projectile) {
        return player.launchProjectile(projectile);
    }

    @Override
    public void setGlowing(boolean glowing) {
        player.setGlowing(glowing);
    }

    @Override
    public RaceType getRaceType() {
        return raceType;
    }

    @Override
    public void setRaceType(RaceType raceType) {
        this.raceType = raceType;
        Data.setString(player.getPersistentDataContainer(), RaceType.KEY, raceType.getName());
        scoreboardCustom.refreshRace(this);
        setFormType(FormType.fromRaceType(raceType));
    }

    @Override
    public FormType getFormType() {
        return formType;
    }

    @Override
    public void setFormType(FormType formType) {
        this.formType = formType;
        Data.setString(player.getPersistentDataContainer(), FormType.KEY, formType.getName());
        refreshSkin();
        refreshScale();
    }

    @Override
    public ClassType getClassType() {
        return classType;
    }

    @Override
    public void setClassType(ClassType classType) {
        this.classType = classType;
        Data.setString(player.getPersistentDataContainer(), ClassType.KEY, classType.getName());
        power = classType.buildPower();
        scoreboardCustom.refreshClass(this);
        scoreboardCustom.refreshPower(this);
    }

    public PowerCustom getPowerCustom() {
        return power;
    }

    @Override
    public void refreshScale() {
        AttributeInstance   attributeInstance = player.getAttribute(Attribute.GENERIC_SCALE);
        if (attributeInstance == null) return;
        attributeInstance.setBaseValue(formType.getScale());
    }

    @Override
    public Set<TeamType> getTeams() {
        return teams;
    }

    @Override
    public void addTeam(TeamType teamType) {
        if (teamType == null) return;
        teams.add(teamType);
        Data.setString(player.getPersistentDataContainer(), teamType.getKey(), teamType.getName());
    }

    @Override
    public void deleteTeam(TeamType teamType) {
        if (teamType == null) return;
        teams.remove(teamType);
        Data.remove(player.getPersistentDataContainer(), teamType.getKey());
    }

    @Override
    public int getLevel() {
        return player.getLevel();
    }

    @Override
    public void setLevel(int level) {
        player.setLevel(level);
    }

    @Override
    public boolean isBoss() {
        return false;
    }

    @Override
    public UUID getOwner() {
        return ownerUUID;
    }

    @Override
    public void setOwner(LivingEntityCustom livingEntityCustom) {
        if (livingEntityCustom == null) ownerUUID = null;
        else ownerUUID = livingEntityCustom.getUUID();
    }

    @Override
    public boolean isPet() {
        return ownerUUID != null;
    }

    @Override
    public UUID getPet() {
        return petUUID;
    }

    @Override
    public void setPet(LivingEntityCustom livingEntityCustom) {
        if (livingEntityCustom == null) {
            if (petUUID == null) return;
            LivingEntityCustom  pet = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(petUUID);
            if (pet == null) {
                petUUID = null;
                return;
            }
            pet.setOwner(null);
        }
        else {
            petUUID = livingEntityCustom.getUUID();
            livingEntityCustom.setOwner(this);
        }
    }

    @Override
    public boolean isOwner() {
        return petUUID != null;
    }


    @Override
    public boolean isFriend(LivingEntityCustom livingEntityCustom) {
        if (livingEntityCustom == this) return true;
        for (TeamType teamType: teams) {
            if (livingEntityCustom.getTeams().contains(teamType)) return true;
        }
        return false;
    }

    @Override
    public boolean isGrouped(LivingEntityCustom livingEntityCustom) {
        if (livingEntityCustom == this) return true;
        if ((ownerUUID != null && ownerUUID.equals(livingEntityCustom.getUUID())) || petUUID != null && petUUID.equals(livingEntityCustom.getUUID())) return true;
        if (group == null) return false;
        if (group.in(livingEntityCustom)) return true;
        EntityCustomRegistry    entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
        if (ownerUUID != null && group.in(entityCustomRegistry.getLivingEntityCustom(livingEntityCustom.getOwner()))) return true;
        if (petUUID != null && group.in(entityCustomRegistry.getLivingEntityCustom(livingEntityCustom.getPet()))) return true;
        return false;
    }

    @Override
    public boolean hasGroup() {
        return group != null;
    }

    @Override
    public void createGroup(LivingEntityCustom livingEntityCustom) {
        if (group != null) deleteGroup();
        Group   g = new Group(this, livingEntityCustom);
        group = g;
        livingEntityCustom.createGroup(g);
        scoreboardCustom.refreshAlly(this);
    }

    @Override
    public void createGroup(Group group) {
        this.group = group;
        scoreboardCustom.refreshAlly(this);
    }

    @Override
    public void deleteGroup() {
        EntityCustomRegistry    entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
        LivingEntityCustom      l1 = entityCustomRegistry.getLivingEntityCustom(group.getUuid1());
        LivingEntityCustom      l2 = entityCustomRegistry.getLivingEntityCustom(group.getUuid2());
        group = null;
        if (l1 != null && l1 != this) {
            if (l1.hasGroup()) l1.deleteGroup();
        }
        else if (l2 != null && l2 != this) {
            if (l2.hasGroup()) l2.deleteGroup();
        }
        scoreboardCustom.refreshAlly(this);
    }

    @Override
    public LivingEntityCustom getAlly() {
        if (!hasGroup()) return null;
        EntityCustomRegistry    entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
        LivingEntityCustom      l = entityCustomRegistry.getLivingEntityCustom(group.getUuid1());
        if (l != this) return l;
        l = entityCustomRegistry.getLivingEntityCustom(group.getUuid2());
        if (l != this) return l;
        return null;
    }

    @Override
    public LivingEntityCustom getTarget() {
        return targetData.getTarget();
    }

    @Override
    public void setTarget(LivingEntityCustom target) {
		targetData.deactive();
		double		health = target.getHealth();
		double		maxHealth = target.getHealthMax();
		float		progress = (float)(health / maxHealth);
		BossBar	bar = BossBar.bossBar(
		        Message.c(Component.translatable("level.rpgcraft.short").append(Component.text(target.getLevel() + " " + target.getName()))),
		        progress,
		        BossBar.Color.RED,
		        BossBar.Overlay.PROGRESS
		);
		showBossBar(bar);
		BossBarData	bossBarData = new BossBarData(bar, new BukkitRunnable() {
		    int	ticks = 0;
		    @Override
		    public void run() {
				if (!isPresent() || !target.isPresent()) {
					targetData.deactive();
					return;
				}
				double		health = target.getHealth();
				double		maxHealth = target.getHealthMax();
				float		progress = (float)(health / maxHealth);
				bar.name(Message.c(Component.translatable("level.rpgcraft.short").append(Component.text(target.getLevel() + " " + target.getName()))));
				bar.progress(progress);
				if (ticks == TIMETARGET_DEFAULT) {
					if (getLocation().distanceSquared(getLocation()) > RANGETARGET_DEFAULT * RANGETARGET_DEFAULT)
						targetData.deactive();
					else
						ticks = 0;
				}
                ticks += 5;
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 5L));
		targetData.active(bossBarData, target);
    }

    @Override
    public Stat getStat(StatType statType) {
        return stats.get(statType);
    }

    @Override
    public Skill getSkill(SkillType skillType) {
        return skills.get(skillType);
    }

    @Override
    public int addStatModifier(StatType statType, int value, int duration) {
        final int           id = modifierId++;
        BukkitTask          task = (duration <= 0 ? null : Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> deleteModifier(id), Data.d(duration)));
        AttributeModifier   attributeModifier = new AttributeModifier(
            statType,
            value,
            duration,
            id,
            task
        );
        modifiers.put(id, attributeModifier);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        Data.setInteger(pdc, attributeModifier.getKeyValue(), attributeModifier.getValue());
        Data.setLong(pdc, attributeModifier.getKeyEnd(), attributeModifier.getEnd());
        stats.get(statType).increaseModifier(value);
        refreshStat();
        return id;
    }

    @Override
    public int addSkillModifier(SkillType skillType, int value, int duration) {
        final int           id = modifierId++;
        BukkitTask          task = (duration <= 0 ? null : Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> deleteModifier(id), Data.d(duration)));
        AttributeModifier   attributeModifier = new AttributeModifier(
            skillType,
            value,
            duration,
            id,
            task
        );
        modifiers.put(id, attributeModifier);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        Data.setInteger(pdc, attributeModifier.getKeyValue(), attributeModifier.getValue());
        Data.setLong(pdc, attributeModifier.getKeyEnd(), attributeModifier.getEnd());
        skills.get(skillType).increaseModifier(value);
        refreshStat();
        return id;
    }

    @Override
    public void deleteModifier(int id) {
        AttributeModifier   attributeModifier = modifiers.get(id);
        if (attributeModifier == null) return;
        stats.get(attributeModifier.getType()).decreaseModifier(attributeModifier.getValue());
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        Data.remove(pdc, attributeModifier.getKeyValue());
        Data.remove(pdc, attributeModifier.getKeyEnd());
        modifiers.remove(id, attributeModifier);
        refreshStat();
    }

    @Override
    public Map<Integer, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public void refreshStat() {
        Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
            ItemCustomRegistry                  itemCustomRegistry = RpgCraft.getItemCustomRegistry();
            EntityEquipment                     equipment = getEquipment();
            Map<EquipmentSlot, Equipable<?>>    equiped = new HashMap<>();
            equiped.put(EquipmentSlot.HEAD, isArmor(itemCustomRegistry.getEquipable(equipment.getHelmet())));
            equiped.put(EquipmentSlot.CHEST, isArmor(itemCustomRegistry.getEquipable(equipment.getChestplate())));
            equiped.put(EquipmentSlot.LEGS, isArmor(itemCustomRegistry.getEquipable(equipment.getLeggings())));
            equiped.put(EquipmentSlot.FEET, isArmor(itemCustomRegistry.getEquipable(equipment.getBoots())));
            equiped.put(EquipmentSlot.HAND, isWeapon(itemCustomRegistry.getEquipable(equipment.getItemInMainHand())));
            equiped.put(EquipmentSlot.OFF_HAND, isWeapon(itemCustomRegistry.getEquipable(equipment.getItemInOffHand())));
            stats.values().forEach(stat -> stat.setValue(0));
            for (Equipable<?> equipable: equiped.values()) {
                if (equipable == null) continue;
                equipable.getStatsPrimary().forEach((s, v) -> stats.get(s).increase(v));
                equipable.getStatsSecondary().forEach((s, v) -> stats.get(s).increase(v));
            }
            // primary change secondary
            stats.values().forEach(stat -> {
                switch(stat.getType()) {
                    case StatPrimary.AGILITY -> {
                        stats.get(StatSecondary.CRITICAL_CHANCE).increase(stat.getValue() + stat.getValueModifier());
                        stats.get(StatSecondary.DODGE).increase(stat.getValue() + stat.getValueModifier());
                    }
                    case StatPrimary.INTELLECT -> {
                        stats.get(StatSecondary.SPELL_DAMAGE).increase(stat.getValue() + stat.getValueModifier());
                        stats.get(StatSecondary.CRITICAL_CHANCE).increase(stat.getValue() + stat.getValueModifier());
                        stats.get(StatSecondary.MAXIMUM_MANA).increase(stat.getValue() + stat.getValueModifier());
                    }
                    case StatPrimary.SPIRIT -> {
                        stats.get(StatSecondary.REGENERATION_HEALTH).increase(stat.getValue() + stat.getValueModifier());
                        stats.get(StatSecondary.REGENERATION_MANA).increase(stat.getValue() + stat.getValueModifier());
                    }
                    case StatPrimary.STAMINA -> {
                        stats.get(StatSecondary.DEFENSE).increase(stat.getValue() + stat.getValueModifier());
                        stats.get(StatSecondary.MAXIMUM_HEALTH).increase(stat.getValue() + stat.getValueModifier());
                    }
                    case StatPrimary.STRENGTH -> {
                        stats.get(StatSecondary.PHYSICAL_DAMAGE).increase(stat.getValue() + stat.getValueModifier());
                        stats.get(StatSecondary.JUMP_STRENGTH).increase(stat.getValue() + stat.getValueModifier());
                    }
                    default -> {}
                }
            });
            // refresh alls vanilla attributes
            stats.values().forEach(stat -> {
                switch (stat.getType()) {
                    case StatSecondary.MAXIMUM_HEALTH -> setHealthMax(StatSecondary.MAXIMUM_HEALTH.getAmount(this));
                    case StatSecondary.MAXIMUM_MANA -> {
                        if (power.getType() == PowerType.MANA) setPowerMax(StatSecondary.MAXIMUM_MANA.getAmount(this));
                    }
                    case StatSecondary.JUMP_STRENGTH -> addAttributeModifier(player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH), StatSecondary.JUMP_STRENGTH.getAmount(this));
                    case StatSecondary.SPEED -> addAttributeModifier(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), StatSecondary.SPEED.getAmount(this));
                    case StatSecondary.ATTACK_SPEED -> addAttributeModifier(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED), StatSecondary.ATTACK_SPEED.getAmount(this));
                    case StatSecondary.PHYSICAL_RANGE -> addAttributeModifier(player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE), StatSecondary.PHYSICAL_RANGE.getAmount(this));
                    case StatSecondary.KNOCKBACK -> addAttributeModifier(player.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK), StatSecondary.KNOCKBACK.getAmount(this));
                    case StatSecondary.KNOCKBACK_RESISTANCE -> {
                        addAttributeModifier(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), StatSecondary.KNOCKBACK_RESISTANCE.getAmount(this));
                        addAttributeModifier(player.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE), StatSecondary.KNOCKBACK_RESISTANCE.getAmount(this));
                    }
                    case StatSecondary.FALL_DAMAGE -> addAttributeModifier(player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER), StatSecondary.FALL_DAMAGE.getAmount(this));
                    case StatSecondary.GRAVITY -> addAttributeModifier(player.getAttribute(Attribute.GENERIC_GRAVITY), StatSecondary.GRAVITY.getAmount(this));
                    default -> {}
                }
            });
        });
    }

    private Equipable<?> isArmor(Equipable<?> equipable) {
        if (equipable instanceof Armor) return equipable;
        return null;
    }

    private Equipable<?> isWeapon(Equipable<?> equipable) {
        if (equipable instanceof Weapon) return equipable;
        return null;
    }

	private void addAttributeModifier(AttributeInstance attributeInstance, double value) {
        if (attributeInstance == null) return;
		org.bukkit.attribute.AttributeModifier attributeModifier = attributeInstance.getModifier(KEY_ATTRIBUTEMODIFIER);
		if (attributeModifier != null) {
			if (attributeModifier.getAmount() == value)
				return;
			attributeInstance.removeModifier(attributeModifier);
		}
		if (value == 0) return;
		attributeInstance.addModifier(new org.bukkit.attribute.AttributeModifier(KEY_ATTRIBUTEMODIFIER, value, org.bukkit.attribute.AttributeModifier.Operation.ADD_SCALAR));
	}

    @Override
    public void addSilence(int time) {
        silence.add(time);
    }

    @Override
    public int isSilence() {
        return silence.is();
    }

    @Override
    public void refreshSkin() {
		SkinData		skinData = formType.getFormTypeSkin().getSkinData();
        if (skinData != null) {
            SkinStorage		skinStorage = RpgCraft.instanceSkinsRestorer().getSkinStorage();
            skinStorage.setCustomSkinData(formType.getName(), SkinProperty.of(skinData.getValue(), skinData.getSignature()));
		    Bukkit.getScheduler().runTaskAsynchronously(RpgCraft.instance(), () -> {
		    	try {
            		Optional<InputDataResult> result = skinStorage.findOrCreateSkinData(formType.getName());
            		if (result.isEmpty()) {
            		    RpgCraft.debug("Impossible to found skin");
            		    return;
            		}
		    		Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
		    			try {
		    				if (!player.isOnline()) return;
            				PlayerStorage	playerStorage = RpgCraft.instanceSkinsRestorer().getPlayerStorage();
            				playerStorage.setSkinIdOfPlayer(player.getUniqueId(), result.get().getIdentifier());
		    				RpgCraft.instanceSkinsRestorer().getSkinApplier(Player.class).applySkin(player);
		    			}
		    			catch (DataRequestException e) {
		    				RpgCraft.debug("Impossible to found skin");
		    				return;
		    			}
		    		});
		    	}
		    	catch (DataRequestException | MineSkinException e) {
		    		RpgCraft.debug("Impossible to found skin");
		    		return;
		    	}
		    });
            return;
        }
		Bukkit.getScheduler().runTaskAsynchronously(RpgCraft.instance(), () -> {
		    PlayerStorage playerStorage = RpgCraft.instanceSkinsRestorer().getPlayerStorage();
		    playerStorage.removeSkinIdOfPlayer(player.getUniqueId());
		    Bukkit.getScheduler().runTask(RpgCraft.instance(), () -> {
		        try {
		            if (!player.isOnline()) return;
		            RpgCraft.instanceSkinsRestorer().getSkinApplier(Player.class).applySkin(player);
		        }
				catch (DataRequestException e) {
		            e.printStackTrace();
		        }
		    });
		});
	}

    @Override
    public void greeting() {
        if (getType() == EntityType.PLAYER) SoundManager.playQuote(this, QuoteType.GREETING);
        else SoundPacket.playSound(this, SoundType.AMBIENT);
    }

    @Override
    public void farewell() {
        if (getType() == EntityType.PLAYER) SoundManager.playQuote(this, QuoteType.FAREWELL);
        else SoundPacket.playSound(this, SoundType.AMBIENT);
    }

    @Override
    public void attack() {
        if (getType() == EntityType.PLAYER) SoundManager.playQuote(this, QuoteType.ATTACK);
        else SoundPacket.playSound(this, SoundType.AMBIENT);
    }

    @Override
    public void death() {
        if (getType() == EntityType.PLAYER) SoundManager.playQuote(this, QuoteType.DEATH);
        else SoundPacket.playSound(this, SoundType.AMBIENT);
    }

    @Override
    public void onSpawn() {
        RpgCraft.getSpellRegistry().clean(this);
        RpgCraft.getMetamorphRegistry().removeDracthyr(this);
        refreshStat();
        refreshCooldown();
        scoreboardCustom.refreshAll(this);
        refreshSkin();
        greeting();
    }

    @Override
    public void onDeath() {
        modifiers.values().removeIf(modifier -> {
            if (modifier.getDuration() == 0) {
                modifier.cancel();
                return true;
            }
            return false;
        });
        RpgCraft.getSpellRegistry().clean(this);
		death();
    }

    @Override
    public void onJoin() {
        RpgCraft.getMetamorphRegistry().removeDracthyr(this);
        refreshStat();
        refreshCooldown();
        scoreboardCustom.refreshAll(this);
        refreshSkin();
        greeting();
    }

    @Override
    public void onQuit() {
        modifiers.values().forEach(modifier -> {
            modifier.cancel();
        });
        RpgCraft.getSpellRegistry().clean(this);
        farewell();
    }
}
