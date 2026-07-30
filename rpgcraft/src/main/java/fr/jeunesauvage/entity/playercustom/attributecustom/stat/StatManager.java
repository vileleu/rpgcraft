package fr.jeunesauvage.entity.playercustom.attributecustom.stat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitTask;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Resource;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceType;
import fr.jeunesauvage.entity.playercustom.scoreboardcustom.ScoreboardCustom;

public class StatManager {
	private final Player						player;
	private final ScoreboardCustom				scoreboardCustom;
	private final ResourceManager				resourceManager;
	private final Map<String, StatType>			statsType = new HashMap<>();
	private final Map<StatPrimary, Stat>		statsPrimary = new HashMap<>();
	private final Map<StatSecondary, Stat>		statsSecondary = new HashMap<>();
	private final Map<Integer, StatModifier>	modifiers = new HashMap<>();
	private final Set<BukkitTask>				tasks = new HashSet<>();
	private int									idCounter = 1;

	public StatManager(Player player, ResourceManager resourceManager, ScoreboardCustom scoreboardCustom) {
		this.player = player;
		this.scoreboardCustom = scoreboardCustom;
		this.resourceManager = resourceManager;
		for (StatPrimary type: StatPrimary.values()) {
			Stat	stat = new Stat(type, 0);
			statsType.put(type.getName(), type);
			statsPrimary.put(type, stat);
		}
		for (StatSecondary type: StatSecondary.values()) {
			Stat	stat = new Stat(type, 0);
			statsType.put(type.getName(), type);
			statsSecondary.put(type, stat);
		}
	}

	public int addModifier(StatType type, int value, int duration) {
		final int	id = idCounter++;
		Stat		stat = getStat(type);
		if (stat == null) return id;
		duration = duration < 0 ? 0 : duration;
		modifiers.put(id, new StatModifier(player, stat.getType(), id, value, duration));
		increaseBonus(stat, value);
		// launch task
		if (duration > 0) {
			tasks.add(Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
				removeModifier(id);
			}, 20L * duration));
		}
		return id;
	}

	public void loadModifiers() {
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		for (NamespacedKey key : pdc.getKeys()) {
		    if (AttributeManager.isStatModifier(key)) {
				String[]		array = key.getKey().split("-");
				int				id = AttributeManager.parseId(array);
				if (id != 0) {
					String			name = array[1];
					NamespacedKey	keyTimer = new NamespacedKey(RpgCraft.name(), "stattimer-" + name + "-" + id);
					long			endTime = Data.getLong(pdc, keyTimer);
					long			now = System.currentTimeMillis();
					if (now < endTime) {
						int	duration = (int)((endTime - now) / 1000l);
						if (duration > 0) {
							Stat	stat = getStat(name);
							if (stat == null) continue;
							int		value = Data.getInteger(pdc, key);
							modifiers.put(id, new StatModifier(player, stat.getType(), id, value, duration));
							increaseBonus(stat, value);
							// launch task
							tasks.add(Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
								removeModifier(id);
							}, 20L * duration));
							continue;
						}
					}
					Data.remove(pdc, key);
					Data.remove(pdc, keyTimer);
				}
		    }
		}
	}

	public void removeModifier(int id) {
		StatModifier	modifier = modifiers.get(id);
		if (modifier == null) return;
		Stat	stat = getStat(modifier.getType());
		if (stat == null) return;
		decreaseBonus(stat, modifier.getValue());
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		Data.remove(pdc, modifier.getKeyValue());
		Data.remove(pdc, modifier.getKeyTimer());
		modifiers.remove(id);
	}

	public void removeModifiers(StatType type) {
		if (type == null) return;
		Iterator<Entry<Integer, StatModifier>>	it = modifiers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, StatModifier>	entry = it.next();
			StatModifier					modifier = entry.getValue();
			Stat							stat = getStat(modifier.getType());
			if (stat == null) return;
			decreaseBonus(stat, modifier.getValue());
			PersistentDataContainer	pdc = player.getPersistentDataContainer();
			Data.remove(pdc, modifier.getKeyValue());
			Data.remove(pdc, modifier.getKeyTimer());
			it.remove();
		}
	}

	private void increaseBonus(Stat stat, int value) {
		initBonus(stat);
		stat.increaseBonus(value);
		refresh(stat);
	}

	private void decreaseBonus(Stat stat, int value) {
		initBonus(stat);
		stat.decreaseBonus(value);
		refresh(stat);
	}

	private void initBonus(Stat stat) {
		if (stat.getType() instanceof StatPrimary primary) {
			switch (primary) {
				case StatPrimary.AGILITY -> {
					statsSecondary.get(StatSecondary.CRITICAL_CHANCE).decrease(stat.getValue());
					statsSecondary.get(StatSecondary.DODGE).decrease(stat.getValue());
				}
				case StatPrimary.INTELLECT -> {
					statsSecondary.get(StatSecondary.SPELL_DAMAGE).decrease(stat.getValue());
					statsSecondary.get(StatSecondary.CRITICAL_CHANCE).decrease(stat.getValue());
					statsSecondary.get(StatSecondary.MAXIMUM_MANA).decrease(stat.getValue());
				}
				case StatPrimary.SPIRIT -> {
					statsSecondary.get(StatSecondary.REGENERATION_HEALTH).decrease(stat.getValue());
					statsSecondary.get(StatSecondary.REGENERATION_MANA).decrease(stat.getValue());
				}
				case StatPrimary.STAMINA -> {
					statsSecondary.get(StatSecondary.DEFENSE).decrease(stat.getValue());
					statsSecondary.get(StatSecondary.MAXIMUM_HEALTH).decrease(stat.getValue());
				}
				case StatPrimary.STRENGTH -> {
					statsSecondary.get(StatSecondary.PHYSICAL_DAMAGE).decrease(stat.getValue());
					statsSecondary.get(StatSecondary.JUMP_STRENGTH).decrease(stat.getValue());
				}
			}
		}
	}

	public void clean() {
		PersistentDataContainer					pdc = player.getPersistentDataContainer();
		Iterator<Entry<Integer, StatModifier>>	it = modifiers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, StatModifier>	e = it.next();
			StatModifier					modifier = e.getValue();
			if (modifier.getDuration() == 0) {
				Stat	stat = getStat(modifier.getType());
				if (stat == null) continue;
				decreaseBonus(stat, modifier.getValue());
				Data.remove(pdc, modifier.getKeyValue());
				Data.remove(pdc, modifier.getKeyTimer());
				it.remove();
			}
		}
	}

	public void cleanTask() {
		tasks.forEach(task -> task.cancel());
		tasks.clear();
	}

	public void reset() {
		for (StatPrimary type: StatPrimary.values())
			statsPrimary.get(type).reset();
		for (StatSecondary type: StatSecondary.values())
			statsSecondary.get(type).reset();
	}

	public void resetAll() {
		PersistentDataContainer					pdc = player.getPersistentDataContainer();
		Iterator<Entry<Integer, StatModifier>>	it = modifiers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, StatModifier>	e = it.next();
			StatModifier					modifier = e.getValue();
			Stat	stat = getStat(modifier.getType());
			if (stat == null) continue;
			decreaseBonus(stat, modifier.getValue());
			Data.remove(pdc, modifier.getKeyValue());
			Data.remove(pdc, modifier.getKeyTimer());
			it.remove();
		}
		cleanTask();
		reset();
	}

	/*
	** getter + setter
	*/

	public Map<Integer, StatModifier> getModifiers() {
		return modifiers;
	}

	public Set<StatModifier> getModifier(StatType type) {
		Set<StatModifier>	setModifiers = new HashSet<>();
		for (StatModifier modifier: modifiers.values()) {
			if (modifier.getType() == type)
				setModifiers.add(modifier);
		}
		return setModifiers;
	}

	public Stat getStat(String name) {
		StatType	type = statsType.get(name);
		if (type == null) return null;
		return getStat(type);
	}

	public Stat getStat(StatType type) {
		return type.getCategory() == AttributeCategory.PRIMARY ? statsPrimary.get(type) : statsSecondary.get(type);
	}

	/*
	** refresh functions
	*/

	public void refresh() {
		for (StatPrimary type: StatPrimary.values()) {
			refresh(statsPrimary.get(type));
		}
		for (StatSecondary type: StatSecondary.values()) {
			refresh(statsSecondary.get(type));
		}
	}

	private void refresh(Stat stat) {
		if (!stat.needRefresh()) return;
		StatType	type = stat.getType();
		switch (type) {
			// primary
			case StatPrimary.AGILITY -> refreshAgility(stat);
			case StatPrimary.INTELLECT -> refreshIntellect(stat);
			case StatPrimary.SPIRIT -> refreshSpirit(stat);
			case StatPrimary.STAMINA -> refreshStamina(stat);
			case StatPrimary.STRENGTH -> refreshStrength(stat);
			// secondary
			// combat
			case StatSecondary.PHYSICAL_DAMAGE -> refreshPhysicalDamage(stat);
			case StatSecondary.SPELL_DAMAGE -> refreshSpellDamage(stat);
			case StatSecondary.PHYSICAL_ARMOR -> refreshPhysicalArmor(stat);
			case StatSecondary.SPELL_ARMOR -> refreshSpellArmor(stat);
			case StatSecondary.CRITICAL_CHANCE -> refreshCriticalChance(stat);
			case StatSecondary.DEFENSE -> refreshDefense(stat);
			case StatSecondary.DODGE -> refreshDodge(stat);
			case StatSecondary.CAST_SPEED -> refreshCastSpeed(stat);
			// resources
			case StatSecondary.MAXIMUM_HEALTH -> refreshMaximumHealth(stat);
			case StatSecondary.MAXIMUM_MANA -> refreshMaximumMana(stat);
			case StatSecondary.REGENERATION_HEALTH -> refreshRegenerationHealth(stat);
			case StatSecondary.REGENERATION_MANA -> refreshRegenerationMana(stat);
			// attributes vanilla
			case StatSecondary.JUMP_STRENGTH -> refreshJumpStrength(stat);
			case StatSecondary.SPEED -> refreshSpeed(stat);
			case StatSecondary.ATTACK_SPEED -> refreshAttackSpeed(stat);
			case StatSecondary.PHYSICAL_RANGE -> refreshPhysicalRange(stat);
			case StatSecondary.KNOCKBACK -> refreshKnockback(stat);
			case StatSecondary.KNOCKBACK_RESISTANCE -> refreshKnockbackResistance(stat);
			case StatSecondary.FALL_DAMAGE -> refreshFallDamage(stat);
			case StatSecondary.GRAVITY -> refreshGravity(stat);
		}
		stat.setLastValue(stat.getValue());
	}

	/*
	** refresh primary
	*/

	private void refreshAgility(Stat stat) {
		int		value = stat.getValue();
		Stat	critical_chance = getStat(StatSecondary.CRITICAL_CHANCE);
		critical_chance.increase(value);
		refreshCriticalChance(critical_chance);
		critical_chance.setLastValue(critical_chance.getValue());
		Stat	dodge = getStat(StatSecondary.DODGE);
		dodge.increase(value);
		refreshDodge(dodge);
		dodge.setLastValue(dodge.getValue());
	}

	private void refreshIntellect(Stat stat) {
		int		value = stat.getValue();
		Stat	spell_damage = getStat(StatSecondary.SPELL_DAMAGE);
		spell_damage.increase(value);
		refreshSpellDamage(spell_damage);
		spell_damage.setLastValue(spell_damage.getValue());
		Stat	critical_chance = getStat(StatSecondary.CRITICAL_CHANCE);
		critical_chance.increase(value);
		refreshCriticalChance(critical_chance);
		critical_chance.setLastValue(critical_chance.getValue());
		Stat	maximum_mana = getStat(StatSecondary.MAXIMUM_MANA);
		maximum_mana.increase(value);
		refreshMaximumMana(maximum_mana);
		maximum_mana.setLastValue(maximum_mana.getValue());
	}

	private void refreshSpirit(Stat stat) {
		int		value = stat.getValue();
		Stat	regeneration_health = getStat(StatSecondary.REGENERATION_HEALTH);
		regeneration_health.increase(value);
		refreshRegenerationHealth(regeneration_health);
		regeneration_health.setLastValue(regeneration_health.getValue());
		Stat	regeneration_mana = getStat(StatSecondary.REGENERATION_MANA);
		regeneration_mana.increase(value);
		refreshRegenerationMana(regeneration_mana);
		regeneration_mana.setLastValue(regeneration_mana.getValue());
	}

	private void refreshStamina(Stat stat) {
		int		value = stat.getValue();
		Stat	defense = getStat(StatSecondary.DEFENSE);
		defense.increase(value);
		refreshDefense(defense);
		defense.setLastValue(defense.getValue());
		Stat	maximum_health = getStat(StatSecondary.MAXIMUM_HEALTH);
		maximum_health.increase(value);
		refreshMaximumHealth(maximum_health);
		maximum_health.setLastValue(maximum_health.getValue());
	}

	private void refreshStrength(Stat stat) {
		int		value = stat.getValue();
		Stat	physical_damage = getStat(StatSecondary.PHYSICAL_DAMAGE);
		physical_damage.increase(value);
		refreshPhysicalDamage(physical_damage);
		physical_damage.setLastValue(physical_damage.getValue());
		Stat	jump_strength = getStat(StatSecondary.JUMP_STRENGTH);
		jump_strength.increase(value);
		refreshJumpStrength(jump_strength);
		jump_strength.setLastValue(jump_strength.getValue());
	}

	/*
	** refresh secondary
	*/

	// combat
	private void refreshPhysicalDamage(Stat stat) {
	}

	private void refreshSpellDamage(Stat stat) {
	}

	private void refreshPhysicalArmor(Stat stat) {
	}

	private void refreshSpellArmor(Stat stat) {
	}

	private void refreshCriticalChance(Stat stat) {
	}

	private void refreshDefense(Stat stat) {
	}

	private void refreshDodge(Stat stat) {
	}

	private void refreshCastSpeed(Stat stat) {
	}

	// resources
	private void refreshRegenerationHealth(Stat stat) {
	}

	private void refreshRegenerationMana(Stat stat) {
	}

	private void refreshMaximumHealth(Stat stat) {
		double	value = (stat.getValue() - stat.getLastValue()) * StatSecondary.MAXIMUM_HEALTH.getAmplifier();
		resourceManager.getHealth().increaseMax(value);
		scoreboardCustom.refreshHealth();
	}

	private void refreshMaximumMana(Stat stat) {
		double		value = (stat.getValue() - stat.getLastValue()) * StatSecondary.MAXIMUM_MANA.getAmplifier();
		Resource	power = resourceManager.getPower();
		if (power != null && power.getType() == ResourceType.MANA) {
			power.increaseMax(value);
			scoreboardCustom.refreshPower();
		}
	}

	// attributes vanilla
	private void refreshJumpStrength(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH);
		double				value = (stat.getValue()) * StatSecondary.JUMP_STRENGTH.getAmplifier();
		addAttributeModifier(instance, StatSecondary.JUMP_STRENGTH.getKey(), value);
	}

	private void refreshSpeed(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		double				value = (stat.getValue()) * StatSecondary.SPEED.getAmplifier();
		addAttributeModifier(instance, StatSecondary.SPEED.getKey(), value);
	}

	private void refreshAttackSpeed(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		double				value = (stat.getValue()) * StatSecondary.ATTACK_SPEED.getAmplifier();
		addAttributeModifier(instance, StatSecondary.ATTACK_SPEED.getKey(), value);
	}

	private void refreshPhysicalRange(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE);
		double				value = (stat.getValue()) * StatSecondary.PHYSICAL_RANGE.getAmplifier();
		addAttributeModifier(instance, StatSecondary.PHYSICAL_RANGE.getKey(), value);
	}

	private void refreshKnockback(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK);
		double				value = (stat.getValue()) * StatSecondary.KNOCKBACK.getAmplifier();
		addAttributeModifier(instance, StatSecondary.KNOCKBACK.getKey(), value);
	}

	private void refreshKnockbackResistance(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
		double				value = (stat.getValue()) * StatSecondary.KNOCKBACK_RESISTANCE.getAmplifier();
		addAttributeModifier(instance, StatSecondary.KNOCKBACK_RESISTANCE.getKey(), value);
		instance = player.getAttribute(Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE);
		value = (stat.getValue()) * StatSecondary.KNOCKBACK_RESISTANCE.getAmplifier();
		addAttributeModifier(instance, StatSecondary.KNOCKBACK_RESISTANCE.getKey(), value);
	}

	private void refreshFallDamage(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER);
		double				value = (stat.getValue()) * StatSecondary.FALL_DAMAGE.getAmplifier();
		addAttributeModifier(instance, StatSecondary.FALL_DAMAGE.getKey(), value);
	}

	private void refreshGravity(Stat stat) {
		AttributeInstance	instance = player.getAttribute(Attribute.GENERIC_GRAVITY);
		double				value = (stat.getValue()) * StatSecondary.GRAVITY.getAmplifier();
		addAttributeModifier(instance, StatSecondary.GRAVITY.getKey(), value);
	}

	// add/replace/keep/remove modifier
	private void addAttributeModifier(AttributeInstance instance, NamespacedKey key, double value) {
		AttributeModifier attributeModifier = instance.getModifier(key);
		if (attributeModifier != null) {
			if (attributeModifier.getAmount() == value)
				return;
			instance.removeModifier(attributeModifier);
		}
		if (value == 0) return;
		instance.addModifier(new AttributeModifier(key, value, AttributeModifier.Operation.ADD_SCALAR));
	}
}
