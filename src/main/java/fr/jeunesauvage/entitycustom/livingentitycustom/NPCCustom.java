package fr.jeunesauvage.entitycustom.livingentitycustom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
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
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait.FightTrait;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomRegistry;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.sound.QuoteType;
import fr.jeunesauvage.sound.SoundManager;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;

public final class NPCCustom implements LivingEntityCustom {
    private final NPC                               npc;
    private RaceType                                raceType = RaceType.UNKNOWN;
    private FormType                                formType = FormType.UNKNOWN;
    private ClassType                               classType = ClassType.BEGGAR;
    private final Set<TeamType>                     teams = new HashSet<>();
    private int                                     level = 1;
    private final Map<StatType, Stat>               stats = new HashMap<>();
    private final Map<SkillType, Skill>             skills = new HashMap<>();
    private final Map<Integer, AttributeModifier>   modifiers = new HashMap<>();
    private int                                     modifierId = 1;
    private BukkitTask                              respawnTask = null;
    private UUID                                    ownerUUID = null;
    private UUID                                    petUUID = null;
    private boolean                                 damageIsUnmodifiable = false;
    private Group                                   group = null;

    public NPCCustom(NPC npc) {
        this.npc = npc;
    }

    private void loadStats(TemplateType templateType) {
        for (StatPrimary statPrimary: StatPrimary.values()) {
            stats.put(statPrimary, new Stat(statPrimary));
        }
        for (StatSecondary statSecondary: StatSecondary.values()) {
            stats.put(statSecondary, new Stat(statSecondary));
        }
		Map<String, Integer>    statsString = templateType.getStats(level);
		if (statsString != null) {
			statsString.forEach((s, v) -> {
                StatType    statType = StatType.fromString(s);
                if (statType == null) return;
                modifiers.put(v, new AttributeModifier(statType, v, -1, modifierId++, null));
            });
		}
    }

    private void loadSkills() {
        for (SkillPrimary skillPrimary: SkillPrimary.values()) {
            skills.put(skillPrimary, new Skill(skillPrimary, level * 5));
        }
        for (SkillSecondary skillSecondary: SkillSecondary.values()) {
            skills.put(skillSecondary, new Skill(skillSecondary, level * 5));
        }
    }

    private void loadModifiers() {
        Map<Integer, String>                modifiersString = getFightTrait().getModifiers();
        Iterator<Entry<Integer, String>>    it = modifiersString.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, String>  e = it.next();
            String[]                array = e.getValue().split("/");
            int                     id = e.getKey();
            int                     value;
            long                    end;
            try {
                value = Integer.parseInt(array[1]);
                end = Long.parseLong(array[2]);
            }
            catch (NumberFormatException err) {
                it.remove();
                continue;
            }
            int     duration;
            if (end < 0)
                duration = (int)end;
            else {
                duration = (int)(((end - System.currentTimeMillis()) / 1000));
                if (duration <= 0) {
                    it.remove();
                    continue;
                }
            }
            AttributeType   attributeType = AttributeType.fromString(array[0]);
            if (attributeType == null) {
                it.remove();
                continue;
            }
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

    public FightTrait getFightTrait() {
        return npc.getOrAddTrait(FightTrait.class);
    }

    public void setPatrolRange(double patrolRange) {
        getFightTrait().setPatrolRange(patrolRange);
    }

    public void spawn(Location location) {
        npc.spawn(location);
    }

    public void despawn() {
        npc.despawn();
    }

    public void delete() {
        npc.destroy();
    }

    public void addAggro(LivingEntityCustom livingEntityCustom, double amount) {
        getFightTrait().addAggro(livingEntityCustom, amount);
    }

    public void cleanAggro() {
        getFightTrait().cleanAggro();
    }

    public TemplateType getTemplateType() {
        return getFightTrait().getTemplateType();
    }

    public void setTemplate(TemplateType templateType) {
        FightTrait  fightTrait = getFightTrait();
        fightTrait.setTemplate(templateType);
        if (skinIsApply(templateType.getFormType())) return;
        setRaceType(templateType.getRaceType());
        setFormType(templateType.getFormType());
        setClassType(templateType.getClassType());
        if (templateType.getTeams() != null) teams.addAll(templateType.getTeams());
        this.level = fightTrait.getLevel();
        // stats + skills
        loadStats(templateType);
        loadSkills();
        loadModifiers();
    }

    public Location getRespawn() {
        return getFightTrait().getRespawn();
    }

    public void setRespawn(Location respawn) {
        getFightTrait().setRespawn(respawn);
    }

    public int getRespawnTime() {
        return getFightTrait().getRespawnTime();
    }

    public void setRespawnTime(int respawnTime) {
        getFightTrait().setRespawnTime(respawnTime);
    }

    @Override
    public UUID getUUID() {
        return npc.getUniqueId();
    }

    @Override
    public int getEntityId() {
        LivingEntity l = getLivingEntity();
        if (l == null) return 0;
        return l.getEntityId();
    }

    @Override
    public String getName() {
        return npc.getName();
    }

    @Override
    public Location getLocation() {
        LivingEntity l = getLivingEntity();
        if (l == null) return npc.getStoredLocation();
        return l.getLocation();
    }

    @Override
    public World getWorld() {
        LivingEntity l = getLivingEntity();
        if (l == null) return null;
        return l.getWorld();
    }

    @Override
    public double getWidth() {
        LivingEntity l = getLivingEntity();
        if (l == null) return 0;
        return l.getWidth();
    }

    @Override
    public double getHeight() {
        LivingEntity l = getLivingEntity();
        if (l == null) return 0;
        return l.getHeight();
    }

    @Override
    public boolean isPresent() {
        return npc.isSpawned() && npc.getEntity() instanceof LivingEntity l && !l.isDead() && l.isValid();
    }

    @Override
    public void teleport(Location location) {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        l.teleport(location);
    }

    @Override
    public void setFallDistance(Float fallDistance) {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        l.setFallDistance(fallDistance);
    }

    @Override
    public void setVelocity(Vector vector) {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        l.setVelocity(vector);
    }

    @Override
    public Vector getVelocity() {
        LivingEntity l = getLivingEntity();
        if (l == null) return null;
        return l.getVelocity();
    }   

    @Override
    public LivingEntity getLivingEntity() {
        if (!(npc.getEntity() instanceof LivingEntity l)) return null;
        return l;
    }

    @Override
    public Location getEyeLocation() {
        LivingEntity l = getLivingEntity();
        if (l == null) return npc.getStoredLocation();
        return l.getEyeLocation();
    }

    @Override
    public EntityType getType() {
        LivingEntity l = getLivingEntity();
        if (l == null) return getFightTrait().getTemplateType().getEntityType();
        return l.getType();
    }

    @Override
    public void swingMainHand() {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        l.swingMainHand();
    }

    @Override
    public void playEffect(EntityEffect entityEffect) {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        l.playEffect(entityEffect);
    }

    @Override
    public EntityEquipment getEquipment() {
        LivingEntity l = getLivingEntity();
        if (l == null) return null;
        return l.getEquipment();
    }

    @Override
    public boolean attackIsInCooldown() {
        return false;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isSneaking() {
        LivingEntity l = getLivingEntity();
        if (l == null) return false;
        return l.isSneaking();
    }

    @Override
    public double getHealth() {
        LivingEntity l = getLivingEntity();
        if (l == null) return 0;
        return l.getHealth();
    }

    @Override
    public void setHealth(double amount) {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        amount = Math.max(0, amount);
        amount = Math.min(getHealthMax(), amount);
        l.setHealth(amount);
    }

    @Override
    public double getHealthMax() {
        return getFightTrait().getHealth();
    }

    @Override
    public void setHealthMax(double amount) {
        amount = Math.max(1, amount);
        getFightTrait().setHealth(amount);
    }

    @Override
    public void damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom) {
        LivingEntity    l = getLivingEntity();
        if (l == null) return;
        l.damage(amount, combatDamage.getDamageSource(livingEntityCustom));
    }

    @Override
    public void damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom, boolean unmodifiable) {
        LivingEntity    l = getLivingEntity();
        if (l == null) return;
        if (unmodifiable == true)
            damageIsUnmodifiable = true;
        l.damage(amount, combatDamage.getDamageSource(livingEntityCustom));
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
        LivingEntity    l = getLivingEntity();
        if (l == null) return;
        l.setFireTicks(fireTicks);
    }

    @Override
    public void setFreezeTicks(int fireTicks) {
        LivingEntity    l = getLivingEntity();
        if (l == null) return;
        l.setFreezeTicks(fireTicks);
    }

    @Override
    public boolean isInvulnerable() {
        LivingEntity l = getLivingEntity();
        if (l == null) return false;
        return l.isInvulnerable();
    }

    @Override
    public boolean isInvisible() {
        LivingEntity l = getLivingEntity();
        if (l == null) return false;
        return l.isInvisible();
    }

    @Override
    public boolean isHandRaised() {
        LivingEntity l = getLivingEntity();
        if (l == null) return false;
        return l.isHandRaised();
    }

    @Override
    public boolean hasLineOfSight(LivingEntityCustom livingEntityCustom) {
        LivingEntity l = getLivingEntity();
        LivingEntity l2 = livingEntityCustom.getLivingEntity();
        if (l == null || l2 == null) return false;
        return l.hasLineOfSight(l2);
    }

    @Override
    public BoundingBox getBoundingBox() {
        LivingEntity l = getLivingEntity();
        if (l == null) return null;
        return l.getBoundingBox();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<T> projectile) {
        LivingEntity    l = getLivingEntity();
        if (l == null) return null;
        return l.launchProjectile(projectile);
    }

    @Override
    public void setGlowing(boolean glowing) {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        l.setGlowing(glowing);
    }

    @Override
    public RaceType getRaceType() {
        return raceType;
    }

    @Override
    public void setRaceType(RaceType raceType) {
        this.raceType = raceType;
        getFightTrait().setRaceType(raceType);
    }

    @Override
    public FormType getFormType() {
        return formType;
    }

    @Override
    public void setFormType(FormType formType) {
        this.formType = formType;
        getFightTrait().setFormType(formType);
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
        getFightTrait().setClassType(classType);
    }

    @Override
    public void refreshScale() {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
        AttributeInstance   attributeInstance = l.getAttribute(Attribute.GENERIC_SCALE);
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
        getFightTrait().addTeam(teamType);
    }

    @Override
    public void deleteTeam(TeamType teamType) {
        if (teamType == null) return;
        teams.remove(teamType);
        getFightTrait().removeTeam(teamType);
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
        FightTrait      fightTrait = getFightTrait();
        TemplateType    templateType = fightTrait.getTemplateType();
        setHealthMax(templateType.getHealth(level));
        fightTrait.setLevel(level);
        modifiers.values().forEach(modifier -> {
            modifier.cancel();
        });
        loadStats(templateType);
        loadSkills();
        loadModifiers();
    }

    @Override
    public boolean isBoss() {
        return getFightTrait().isBoss();
    }

    @Override
    public UUID getOwner() {
        return ownerUUID;
    }

    @Override
    public void setOwner(LivingEntityCustom livingEntityCustom) {
        if (livingEntityCustom == null) ownerUUID = null;
        else {
            ownerUUID = livingEntityCustom.getUUID();
            getFightTrait().setOwner(ownerUUID);
        }
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
            getFightTrait().setPet(petUUID);
            livingEntityCustom.setOwner(this);
        }
    }

    @Override
    public boolean isOwner() {
        return petUUID != null;
    }

    @Override
    public boolean isFriend(LivingEntityCustom livingEntityCustom) {
        if ((ownerUUID != null && ownerUUID.equals(livingEntityCustom.getUUID())) || (petUUID != null && petUUID.equals(livingEntityCustom.getUUID()))) return true;
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
    }

    @Override
    public void createGroup(Group group) {
        this.group = group;
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
        return getFightTrait().getFightAI().getTarget();
    }

    @Override
    public void setTarget(LivingEntityCustom livingEntityCustom) {
        getFightTrait().getFightAI().addAggro(livingEntityCustom, 1000);
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
        getFightTrait().addModifier(attributeModifier);
        stats.get(statType).increaseModifier(value);
        return id;
    }

    @Override
    public void addSkillModifier(SkillType skillType, int value, int duration) {
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
        getFightTrait().addModifier(attributeModifier);
        skills.get(skillType).increaseModifier(value);
    }

    @Override
    public void deleteModifier(int id) {
        AttributeModifier   attributeModifier = modifiers.get(id);
        if (attributeModifier == null) return;
        stats.get(attributeModifier.getType()).decreaseModifier(attributeModifier.getValue());
        getFightTrait().deleteModifier(id);
        modifiers.remove(id, attributeModifier);
    }

    @Override
    public Map<Integer, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public void refreshStat() {
        LivingEntity l = getLivingEntity();
        if (l == null) return;
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
                case StatSecondary.MAXIMUM_MANA -> {}
                case StatSecondary.JUMP_STRENGTH -> addAttributeModifier(l.getAttribute(Attribute.GENERIC_JUMP_STRENGTH), StatSecondary.JUMP_STRENGTH.getAmount(this));
                case StatSecondary.SPEED -> addAttributeModifier(l.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), StatSecondary.SPEED.getAmount(this));
                case StatSecondary.ATTACK_SPEED -> addAttributeModifier(l.getAttribute(Attribute.GENERIC_ATTACK_SPEED), StatSecondary.ATTACK_SPEED.getAmount(this));
                case StatSecondary.PHYSICAL_RANGE -> addAttributeModifier(l.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE), StatSecondary.PHYSICAL_RANGE.getAmount(this));
                case StatSecondary.KNOCKBACK -> addAttributeModifier(l.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK), StatSecondary.KNOCKBACK.getAmount(this));
                case StatSecondary.KNOCKBACK_RESISTANCE -> {
                    addAttributeModifier(l.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), StatSecondary.KNOCKBACK_RESISTANCE.getAmount(this));
                    addAttributeModifier(l.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE), StatSecondary.KNOCKBACK_RESISTANCE.getAmount(this));
                }
                case StatSecondary.FALL_DAMAGE -> addAttributeModifier(l.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER), StatSecondary.FALL_DAMAGE.getAmount(this));
                case StatSecondary.GRAVITY -> addAttributeModifier(l.getAttribute(Attribute.GENERIC_GRAVITY), StatSecondary.GRAVITY.getAmount(this));
                default -> {}
            }
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
        long    end = System.currentTimeMillis() + (time * 1000);
        getFightTrait().setSilence(end);
    }

    @Override
    public int isSilence() {
        long    end = getFightTrait().getSilence();
        if (end <= 0) return 0;
        long    now = System.currentTimeMillis();
        if (end <= now) {
            getFightTrait().setSilence(0);
            return 0;
        }
        return (int)((end - now) / 1000 + 1);
    }

    @Override
    public void refreshSkin() {
        if (getEntityType() != EntityType.PLAYER) return;
        SkinTrait   skinTrait = npc.getOrAddTrait(SkinTrait.class);
        String      currentSkin = skinTrait.getSkinName();
        if (!formType.getName().equals(currentSkin)) {
            RpgCraft.debug("////////////");
            RpgCraft.debug("refreshSkin()");
		    SkinData	skinData = formType.getFormTypeSkin().getSkinData();
		    if (skinData != null)
		    	skinTrait.setSkinPersistent(formType.getName(), skinData.getSignature(), skinData.getValue());
		    else
		    	skinTrait.setSkinName(formType.getName());
        }
    }

    @Override
    public EntityType getEntityType() {
        LivingEntity l = getLivingEntity();
        if (l == null) return null;
        return l.getType();
    }

    private boolean skinIsApply(FormType formType) {
        if (getEntityType() != EntityType.PLAYER) return false;
        SkinTrait   skinTrait = npc.getOrAddTrait(SkinTrait.class);
        String      currentSkin = skinTrait.getSkinName();
        if (!formType.getName().equals(currentSkin)) {
		    SkinData	skinData = formType.getFormTypeSkin().getSkinData();
		    if (skinData != null)
		    	skinTrait.setSkinPersistent(formType.getName(), skinData.getSignature(), skinData.getValue());
		    else
		    	skinTrait.setSkinName(formType.getName());
            return true;
        }
        return false;
    }

    @Override
    public void onSpawn() {
        FightTrait      fightTrait = getFightTrait();
        TemplateType    templateType = fightTrait.getTemplateType();
        if (skinIsApply(templateType.getFormType())) return;
        setRaceType(templateType.getRaceType());
        setFormType(templateType.getFormType());
        setClassType(templateType.getClassType());
        if (templateType.getTeams() != null) teams.addAll(templateType.getTeams());
        this.level = fightTrait.getLevel();
        // stats + skills
        loadStats(templateType);
        loadSkills();
        loadModifiers();
        // cancel respawn
        if (respawnTask != null) respawnTask.cancel();
        // spawn location
		if (fightTrait.getRespawn() != null) {
            LivingEntity    l = getLivingEntity();
            if (l != null) getLivingEntity().teleport(fightTrait.getRespawn());
        }
        SoundManager.playQuote(this, QuoteType.GREETING);
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
        if (respawnTask != null) respawnTask.cancel();
        int respawnTime = getFightTrait().getRespawnTime();
		if (respawnTime == 0) return;
		else if (respawnTime == -1) {
			Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> delete(), 20);
			return;
		}
		respawnTask = Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
            NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(getUUID());
            if (npcCustom != this || npcCustom.isPresent()) return;
			Location	respawn = npcCustom.getFightTrait().getRespawn();
			if (respawn != null)
		    	npcCustom.spawn(respawn);
			else
				npcCustom.spawn(npcCustom.getLocation());
        }, Data.d(respawnTime));
        SoundManager.playQuote(this, QuoteType.DEATH);
    }

    @Override
    public void onJoin() {
        getFightTrait();
    }

    @Override
    public void onQuit() {
        modifiers.values().forEach(modifier -> {
            modifier.cancel();
        });
        if (respawnTask != null) respawnTask.cancel();
        SoundManager.playQuote(this, QuoteType.FAREWELL);
    }
}
