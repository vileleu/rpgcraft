package fr.jeunesauvage.entity.playercustom.attributecustom.skill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitTask;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SkillManager {
	private final Player						player;
	private final ResourceManager				resourceManager;
	private final Map<String, SkillType>		skillsType = new HashMap<>();
	private final Map<SkillPrimary, Skill>		skillsPrimary = new HashMap<>();
	private final Map<SkillSecondary, Skill>	skillsSecondary = new HashMap<>();
	private final Map<Integer, SkillModifier>	modifiers = new HashMap<>();
	private final Set<BukkitTask>				tasks = new HashSet<>();
	private int									idCounter = 1;

	public SkillManager(Player player, ResourceManager resourceManager) {
		this.player = player;
		this.resourceManager = resourceManager;
		for (SkillPrimary type: SkillPrimary.values()) {
			Skill	skill = new Skill(player, type);
			skillsType.put(type.getName(), type);
			skillsPrimary.put(type, skill);
		}
		for (SkillSecondary type: SkillSecondary.values()) {
			Skill	skill = new Skill(player, type);
			skillsType.put(type.getName(), type);
			skillsSecondary.put(type, skill);
		}
	}

	// modifier

	public int addModifier(SkillType type, int value, int duration) {
		final int		id = idCounter++;
		Skill			skill = getSkill(type);
		if (skill == null) return id;
		duration = duration < 0 ? 0 : duration;
		modifiers.put(id, new SkillModifier(player, skill.getType(), id, value, duration));
		skill.increaseBonus(value);
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
		    if (AttributeManager.isSkillModifier(key)) {
				String[]		array = key.getKey().split("-");
				int				id = AttributeManager.parseId(array);
				if (id != 0) {
					String			name = array[1];
					NamespacedKey	keyTimer = new NamespacedKey(RpgCraft.name(), "skilltimer-" + name + "-" + id);
					long			endTime = Data.getLong(pdc, keyTimer);
					long			now = System.currentTimeMillis();
					if (now < endTime) {
						int	duration = (int)((endTime - now) / 1000L);
						if (duration > 0) {
							Skill	skill = getSkill(name);
							if (skill == null) continue;
							int		value = Data.getInteger(pdc, key);
							modifiers.put(id, new SkillModifier(player, skill.getType(), id, value, duration));
							skill.increaseBonus(value);
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
		SkillModifier	modifier = modifiers.get(id);
		if (modifier == null) return;
		Skill	skill = getSkill(modifier.getType());
		if (skill == null) return;
		skill.decreaseBonus(modifier.getValue());
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		Data.remove(pdc, modifier.getKeyValue());
		Data.remove(pdc, modifier.getKeyTimer());
		modifiers.remove(id);
	}

	public void removeModifiers(SkillType type) {
		if (type == null) return;
		Iterator<Entry<Integer, SkillModifier>>	it = modifiers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, SkillModifier>	entry = it.next();
			SkillModifier					modifier = entry.getValue();
			Skill							skill = getSkill(modifier.getType());
			if (skill == null) return;
			skill.decreaseBonus(modifier.getValue());
			PersistentDataContainer	pdc = player.getPersistentDataContainer();
			Data.remove(pdc, modifier.getKeyValue());
			Data.remove(pdc, modifier.getKeyTimer());
			it.remove();
		}
	}

	// Skill

	public void incrementeSkill(SkillPrimary type) {
		Skill	skill = getSkill(type);
		if (skill.isMaxed()) return;
		skill.increase(1);
		player.sendMessage(Message.increaseSkill().color(NamedTextColor.AQUA).append(Component.text(" " + skill.getName() + ": " + skill.getValue())));
	}

	public void incrementeSkill(SkillSecondary type) {
		Skill	skill = getSkill(type);
		if (skill.isMaxed()) return;
		double	diff = skill.getValueMax() - skill.getValue();
		if (diff >= 100) {
			skill.increase(1);
			player.sendMessage(Message.increaseSkill().color(NamedTextColor.AQUA).append(Component.text(" " + skill.getName() + ": " + skill.getValue())));
			return;
		}
		int		random = ThreadLocalRandom.current().nextInt(100);
		if (diff >= 80 && random % 2 == 0) {
			skill.increase(1);
			player.sendMessage(Message.increaseSkill().color(NamedTextColor.AQUA).append(Component.text(" " + skill.getName() + ": " + skill.getValue())));
			return;
		}
		else if (diff >= 40 && random % 3 == 0) {
			skill.increase(1);
			player.sendMessage(Message.increaseSkill().color(NamedTextColor.AQUA).append(Component.text(" " + skill.getName() + ": " + skill.getValue())));
			return;
		}
		else if (diff >= 20 && random % 4 == 0) {
			skill.increase(1);
			player.sendMessage(Message.increaseSkill().color(NamedTextColor.AQUA).append(Component.text(" " + skill.getName() + ": " + skill.getValue())));
			return;
		}
		else if (diff >= 10 && random % 8 == 0) {
			skill.increase(1);
			player.sendMessage(Message.increaseSkill().color(NamedTextColor.AQUA).append(Component.text(" " + skill.getName() + ": " + skill.getValue())));
			return;
		}
		else if (diff >= 5 && random % 10 == 0) {
			skill.increase(1);
			player.sendMessage(Message.increaseSkill().color(NamedTextColor.AQUA).append(Component.text(" " + skill.getName() + ": " + skill.getValue())));
			return;
		}
	}

	public void refreshSkill() {
		int	level = (int)resourceManager.getLevel().getValue();
		for (Skill skill: skillsPrimary.values())
			skill.setValueMax(level * 5);
		for (Skill skill: skillsSecondary.values())
			skill.setValueMax(level * 5);
	}

	public void clean() {
		PersistentDataContainer					pdc = player.getPersistentDataContainer();
		Iterator<Entry<Integer, SkillModifier>>	it = modifiers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, SkillModifier>	e = it.next();
			SkillModifier					modifier = e.getValue();
			if (modifier.getDuration() == 0) {
				Skill	skill = getSkill(modifier.getType());
				if (skill == null) continue;
				skill.decreaseBonus(modifier.getValue());
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
		for (SkillPrimary type: SkillPrimary.values())
			skillsPrimary.get(type).reset();
		for (SkillSecondary type: SkillSecondary.values())
			skillsSecondary.get(type).reset();
	}

	public void resetAll() {
		PersistentDataContainer					pdc = player.getPersistentDataContainer();
		Iterator<Entry<Integer, SkillModifier>>	it = modifiers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, SkillModifier>	e = it.next();
			SkillModifier					modifier = e.getValue();
			Skill	skill = getSkill(modifier.getType());
			if (skill == null) continue;
			skill.decreaseBonus(modifier.getValue());
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

	public Map<Integer, SkillModifier> getModifiers() {
		return modifiers;
	}

	public Set<SkillModifier> getModifier(SkillType type) {
		Set<SkillModifier>	setModifiers = new HashSet<>();
		for (SkillModifier modifier: modifiers.values()) {
			if (modifier.getType() == type)
				setModifiers.add(modifier);
		}
		return setModifiers;
	}

	public Skill getSkill(String name) {
		SkillType	type = skillsType.get(name);
		if (type == null) return null;
		return getSkill(type);
	}

	public Skill getSkill(SkillType type) {
		return type.getCategory() == AttributeCategory.PRIMARY ? skillsPrimary.get(type) : skillsSecondary.get(type);
	}
}
