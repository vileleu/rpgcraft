package fr.jeunesauvage.entitycustom.livingentitycustom;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.BoundingBox;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entitycustom.EntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeModifier;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.Stat;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;
import fr.jeunesauvage.entitycustom.livingentitycustom.group.Group;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;

public sealed interface LivingEntityCustom extends EntityCustom permits PlayerCustom, NPCCustom, MobCustom {
    public static final NamespacedKey   KEY_ATTRIBUTEMODIFIER = new NamespacedKey(RpgCraft.name(), "attributemodifier");
    public static final int             LEVEL_MAX = 60;

    LivingEntity                    getLivingEntity();
    Location                        getEyeLocation();
    EntityType                      getType();
    void                            swingMainHand();
    void                            playEffect(EntityEffect entityEffect);
    EntityEquipment                 getEquipment();
    boolean                         attackIsInCooldown();
    boolean                         isCreative();
    boolean                         isBlocking();
    boolean                         isSneaking();
    double                          getHealth();
    void                            setHealth(double amount);
    double                          getHealthMax();
    void                            setHealthMax(double amount);
    void                            damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom);
    void                            damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom, boolean unmodifiable);
    boolean                         damageIsUnmodifiable();
    void                            heal(double amount);
    void                            setFireTicks(int fireTicks);
    void                            setFreezeTicks(int freezeticks);
    boolean                         isInvulnerable();
    boolean                         isInvisible();
    boolean                         isHandRaised();
    boolean                         hasLineOfSight(LivingEntityCustom livingEntityCustom);
    BoundingBox                     getBoundingBox();
    <T extends Projectile> T        launchProjectile(Class<T> projectile);
    void                            setGlowing(boolean glowing);

    RaceType                        getRaceType();
    void                            setRaceType(RaceType raceType);
    FormType                        getFormType();
    void                            setFormType(FormType formType);
    ClassType                       getClassType();
    void                            setClassType(ClassType classType);
    void                            refreshScale();
    Set<TeamType>                   getTeams();
    void                            addTeam(TeamType teamType);
    void                            deleteTeam(TeamType teamType);
    int                             getLevel();
    void                            setLevel(int level);

    boolean                         isBoss();
    UUID                            getOwner();
    void                            setOwner(LivingEntityCustom livingEntityCustom); // never use (use setPet() instead)
    boolean                         isPet();
    UUID                            getPet();
    void                            setPet(LivingEntityCustom livingEntityCustom);
    boolean                         isOwner();
    boolean                         isFriend(LivingEntityCustom livingEntityCustom);
    boolean                         isGrouped(LivingEntityCustom livingEntityCustom);
    boolean                         hasGroup();
    void                            createGroup(LivingEntityCustom livingEntityCustom);
    void                            createGroup(Group group);
    void                            deleteGroup();
    LivingEntityCustom              getAlly();
    LivingEntityCustom              getTarget();
    void                            setTarget(LivingEntityCustom target);

    Stat                            getStat(StatType statType);
    Skill                           getSkill(SkillType skillType);
    int                             addStatModifier(StatType statType, int value, int duration);
    int                             addSkillModifier(SkillType skillType, int value, int duration);
    void                            deleteModifier(int id);
    Map<Integer, AttributeModifier> getModifiers();
    void                            refreshStat();
    void                            addSilence(int time);
    int                             isSilence();

    void                            refreshSkin();

    void                            onSpawn();
    void                            onDeath();
    void                            onJoin();
    void                            onQuit();
}
