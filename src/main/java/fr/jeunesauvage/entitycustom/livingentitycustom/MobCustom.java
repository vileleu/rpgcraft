package fr.jeunesauvage.entitycustom.livingentitycustom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.combat.CombatDamage;
import fr.jeunesauvage.entitycustom.EntityCustomRegistry;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.AttributeModifier;
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
import fr.jeunesauvage.entitycustom.livingentitycustom.group.Group;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.saveequipment.SaveEquipment;
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

public final class MobCustom implements LivingEntityCustom {
    private final Mob                               mob;
    private RaceType                                raceType = RaceType.UNKNOWN;
    private FormType                                formType = FormType.UNKNOWN;
    private ClassType                               classType = ClassType.BEGGAR;
    private FormType                                metamorph = FormType.UNKNOWN;
    private final Set<TeamType>                     teams = new HashSet<>();
    private int                                     level;
    private final Map<StatType, Stat>               stats = new HashMap<>();
    private final Map<SkillType, Skill>             skills = new HashMap<>();
    private final Map<Integer, AttributeModifier>   modifiers = new HashMap<>();
    private int                                     modifierId = 1;
    private UUID                                    ownerUUID;
    private UUID                                    petUUID;
    private boolean                                 damageIsUnmodifiable = false;
    private Group                                   group = null;
    private final Silence                           silence;

    public MobCustom(Mob mob) {
        this.mob = mob;
        level = ThreadLocalRandom.current().nextInt(1, LivingEntityCustom.LEVEL_MAX + 1);
        this.silence = new Silence(mob);
        loadStats();
        loadSkills();
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
            skills.put(skillPrimary, new Skill(skillPrimary, level * 5));
        }
        for (SkillSecondary skillSecondary: SkillSecondary.values()) {
            skills.put(skillSecondary, new Skill(skillSecondary, level * 5));
        }
    }

    @Override
    public UUID getUUID() {
        return mob.getUniqueId();
    }

    @Override
    public int getEntityId() {
        return mob.getEntityId();
    }

    @Override
    public String getName() {
        return mob.getName();
    }

    @Override
    public Location getLocation() {
        return mob.getLocation();
    }

    @Override
    public World getWorld() {
        return mob.getWorld();
    }

    @Override
    public double getWidth() {
        return mob.getWidth();
    }

    @Override
    public double getHeight() {
        return mob.getHeight();
    }

    @Override
    public boolean isPresent() {
        return !mob.isDead() && mob.isValid();
    }

    @Override
    public void teleport(Location location) {
        mob.teleport(location);
    }

    @Override
    public void setFallDistance(Float fallDistance) {
        mob.setFallDistance(fallDistance);
    }

    @Override
    public void setVelocity(Vector vector) {
        mob.setVelocity(vector);
    }

    @Override
    public Vector getVelocity() {
        return mob.getVelocity();
    }   

    @Override
    public LivingEntity getLivingEntity() {
        return mob;
    }

    @Override
    public Location getEyeLocation() {
        return mob.getEyeLocation();
    }

    @Override
    public EntityType getType() {
        return mob.getType();
    }

    @Override
    public void swingMainHand() {
        mob.swingMainHand();
    }

    @Override
    public void playEffect(EntityEffect entityEffect) {
        mob.playEffect(entityEffect);
    }

    @Override
    public EntityEquipment getEquipment() {
        return mob.getEquipment();
    }

    @Override
    public boolean attackIsInCooldown() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isSneaking() {
        return mob.isSneaking();
    }

    @Override
    public double getHealth() {
        return mob.getHealth();
    }

    @Override
    public void setHealth(double amount) {
        amount = Math.max(0, amount);
        amount = Math.min(getHealthMax(), amount);
        mob.setHealth(amount);
    }

    @Override
    public double getHealthMax() {
        AttributeInstance   attributeInstance = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attributeInstance == null) return 0;
        return attributeInstance.getValue();
    }

    @Override
    public void setHealthMax(double amount) {
        AttributeInstance   attributeInstance = mob.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attributeInstance == null) return;
        amount = Math.max(1, amount);
        attributeInstance.setBaseValue(amount);
        if (getHealth() > amount) mob.setHealth(amount);
    }

    @Override
    public void damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom) {
        mob.damage(amount, combatDamage.getDamageSource(livingEntityCustom));
    }

    @Override
    public void damage(double amount, CombatDamage combatDamage, LivingEntityCustom livingEntityCustom, boolean unmodifiable) {
        if (unmodifiable == true)
            damageIsUnmodifiable = true;
        mob.damage(amount, combatDamage.getDamageSource(livingEntityCustom));
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
        mob.setFireTicks(fireTicks);
    }

    @Override
    public void setFreezeTicks(int fireTicks) {
        mob.setFreezeTicks(fireTicks);
    }

    @Override
    public boolean isInvulnerable() {
        return mob.isInvulnerable();
    }

    @Override
    public boolean isInvisible() {
        return mob.isInvisible();
    }

    @Override
    public boolean isHandRaised() {
        return mob.isHandRaised();
    }

    @Override
    public boolean hasLineOfSight(LivingEntityCustom livingEntityCustom) {
        return mob.hasLineOfSight(livingEntityCustom.getLivingEntity());
    }

    @Override
    public BoundingBox getBoundingBox() {
        return mob.getBoundingBox();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<T> projectile) {
        return mob.launchProjectile(projectile);
    }

    @Override
    public void setGlowing(boolean glowing) {
        mob.setGlowing(glowing);
    }

    @Override
    public void setAI(boolean ai) {
        mob.setAI(ai);
    }

    @Override
    public void setGravity(boolean gravity) {
        mob.setGravity(gravity);
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        mob.setInvulnerable(invulnerable);
    }

    @Override
    public RaceType getRaceType() {
        return raceType;
    }

    @Override
    public void setRaceType(RaceType raceType) {
        if (raceType == null) raceType = RaceType.UNKNOWN;
        this.raceType = raceType;
    }

    @Override
    public FormType getFormType() {
        return formType;
    }

    @Override
    public void setFormType(FormType formType) {
        if (formType == null) formType = FormType.UNKNOWN;
        this.formType = formType;
    }

    @Override
    public FormType getMetamorph() {
        return metamorph;
    }

    @Override
    public void setMetamorph(FormType formType) {
        if (formType == null) formType = FormType.UNKNOWN;
        metamorph = formType;
    }

    @Override
	public boolean isOnGround() {
    	BoundingBox	box = getBoundingBox();
		if (box == null) return false;
		World	    world = getWorld();
		if (world == null) return false;
    	double		y = box.getMinY() - 0.01;
    	for (double x = box.getMinX(); x <= box.getMaxX(); x += 0.3) {
    	    for (double z = box.getMinZ(); z <= box.getMaxZ(); z += 0.3) {
    	        Block block = world.getBlockAt((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    	        if (block.getType().isSolid())
    	            return true;
    	    }
    	}
    	return false;
	}

    @Override
    public ClassType getClassType() {
        return classType;
    }

    @Override
    public void setClassType(ClassType classType) {
        if (classType == null) classType = ClassType.BEGGAR;
        this.classType = classType;
    }

    @Override
    public void refreshScale() {
        AttributeInstance   attributeInstance = mob.getAttribute(Attribute.GENERIC_SCALE);
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
        Data.setString(mob.getPersistentDataContainer(), teamType.getKey(), teamType.getName());
    }

    @Override
    public void deleteTeam(TeamType teamType) {
        if (teamType == null) return;
        teams.remove(teamType);
        Data.remove(mob.getPersistentDataContainer(), teamType.getKey());

    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
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
        // mark
        if (livingEntityCustom instanceof PlayerCustom playerCustom && playerCustom.isMarked()) return false;
        if ((ownerUUID != null && ownerUUID.equals(livingEntityCustom.getUUID())) || (petUUID != null && petUUID.equals(livingEntityCustom.getUUID()))) return true;
        for (TeamType teamType: teams) {
            if (livingEntityCustom.getTeams().contains(teamType)) return true;
        }
        if (group == null) return false;
        if (group.in(livingEntityCustom)) return true;
        EntityCustomRegistry    entityCustomRegistry = RpgCraft.getEntityCustomRegistry();
        if (ownerUUID != null && group.in(entityCustomRegistry.getLivingEntityCustom(livingEntityCustom.getOwner()))) return true;
        if (petUUID != null && group.in(entityCustomRegistry.getLivingEntityCustom(livingEntityCustom.getPet()))) return true;
        return false;
    }

    @Override
    public boolean isGrouped(LivingEntityCustom livingEntityCustom) {
        return isFriend(livingEntityCustom);
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
        LivingEntity        l = mob.getTarget();
        if (l == null) return null;
        LivingEntityCustom  target = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(l.getUniqueId());
        return target;
    }

    @Override
    public void setTarget(LivingEntityCustom livingEntityCustom) {
        LivingEntity target = livingEntityCustom.getLivingEntity();
        if (target == null) return;
        mob.setTarget(target);
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
        skills.get(skillType).increaseModifier(value);
        refreshStat();
        return id;
    }

    @Override
    public void deleteModifier(int id) {
        AttributeModifier   attributeModifier = modifiers.get(id);
        if (attributeModifier == null) return;
        stats.get(attributeModifier.getType()).decreaseModifier(attributeModifier.getValue());
        modifiers.remove(id, attributeModifier);
        refreshStat();
    }

    @Override
    public Map<Integer, AttributeModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public void refreshStat() {
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
                case StatSecondary.MAXIMUM_HEALTH -> setHealthMax(getHealthMax() + StatSecondary.MAXIMUM_HEALTH.getAmount(this));
                case StatSecondary.MAXIMUM_MANA -> {}
                case StatSecondary.JUMP_STRENGTH -> addAttributeModifier(mob.getAttribute(Attribute.GENERIC_JUMP_STRENGTH), StatSecondary.JUMP_STRENGTH.getAmount(this));
                case StatSecondary.SPEED -> addAttributeModifier(mob.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED), StatSecondary.SPEED.getAmount(this));
                case StatSecondary.ATTACK_SPEED -> addAttributeModifier(mob.getAttribute(Attribute.GENERIC_ATTACK_SPEED), StatSecondary.ATTACK_SPEED.getAmount(this));
                case StatSecondary.PHYSICAL_RANGE -> addAttributeModifier(mob.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE), StatSecondary.PHYSICAL_RANGE.getAmount(this));
                case StatSecondary.KNOCKBACK -> addAttributeModifier(mob.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK), StatSecondary.KNOCKBACK.getAmount(this));
                case StatSecondary.KNOCKBACK_RESISTANCE -> {
                    addAttributeModifier(mob.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), StatSecondary.KNOCKBACK_RESISTANCE.getAmount(this));
                    addAttributeModifier(mob.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE), StatSecondary.KNOCKBACK_RESISTANCE.getAmount(this));
                }
                case StatSecondary.FALL_DAMAGE -> addAttributeModifier(mob.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER), StatSecondary.FALL_DAMAGE.getAmount(this));
                case StatSecondary.GRAVITY -> addAttributeModifier(mob.getAttribute(Attribute.GENERIC_GRAVITY), StatSecondary.GRAVITY.getAmount(this));
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
        silence.add(time);
    }

    @Override
    public int isSilence() {
        return silence.is();
    }

    @Override
    public Map<SaveEquipment, ItemStack> getSavedEquipment() {
        Map<SaveEquipment, ItemStack>   result = new HashMap<>();
        PersistentDataContainer         pdc = mob.getPersistentDataContainer();
        for (SaveEquipment slot: SaveEquipment.values()) {
            if (Data.hasString(pdc, slot.getKey())) {
                result.put(slot, Data.fromBase64(Data.getString(pdc, slot.getKey())));
            }
        }
        return result;
    }

    @EventHandler
    public void saveEquipment(SaveEquipment slot, ItemStack item) {
        PersistentDataContainer pdc = mob.getPersistentDataContainer();
        if (Data.hasString(pdc, slot.getKey())) return;
        Data.setString(pdc, slot.getKey(), Data.toBase64(item));
    }

    @Override
    public void deleteSavedEquipment() {
        PersistentDataContainer         pdc = mob.getPersistentDataContainer();
        for (SaveEquipment slot: SaveEquipment.values()) {
            if (Data.hasString(pdc, slot.getKey())) {
                Data.remove(pdc, slot.getKey());
            }
        }
    }

    @Override
    public void refreshSkin() {}

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
        greeting();
    }

    @Override
    public void onDeath() {
        modifiers.values().forEach(modifier -> {
            modifier.cancel();
        });
        death();
    }

    @Override
    public void onJoin() {}

    @Override
    public void onQuit() {
        death();
    }
}
