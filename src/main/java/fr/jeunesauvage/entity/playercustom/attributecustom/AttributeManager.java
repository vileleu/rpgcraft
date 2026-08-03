package fr.jeunesauvage.entity.playercustom.attributecustom;

import java.util.Map;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Health;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Level;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Resource;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillSecondary;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.Stat;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.playercustom.scoreboardcustom.ScoreboardCustom;

public class AttributeManager {
	private final Player			player;
	private final ScoreboardCustom	scoreboardCustom;
	private final ResourceManager	resourceManager;
	private final StatManager		statManager;
	private final SkillManager		skillManager;

	public AttributeManager(Player player, ResourceManager resourceManager, ScoreboardCustom scoreboardCustom) {
		this.player = player;
		this.scoreboardCustom = scoreboardCustom;
		this.resourceManager = resourceManager;
		this.statManager = new StatManager(player, resourceManager, scoreboardCustom);
		this.skillManager = new SkillManager(player, resourceManager);
		statManager.loadModifiers();
		skillManager.loadModifiers();
	}

	// resource

	public void updateLevel(int n) {
		Level	level = resourceManager.getLevel();
		level.setValue(n);
		skillManager.refreshSkill();
		scoreboardCustom.updateLevel(level);
	}

	// stat

	public int addStatModifier(StatType type, int value, int duration) {
		return statManager.addModifier(type, value, duration);
	}

	public void removeStatModifier(int id) {
		statManager.removeModifier(id);
	}

	public void removeStatModifier(StatType type) {
		statManager.removeModifiers(type);
	}

	public void refreshStats() {
		statManager.refresh();
	}

	// skill

	public int addSkillModifier(SkillType type, int value, int duration) {
		return skillManager.addModifier(type, value, duration);
	}

	public void removeSkillModifier(int id) {
		skillManager.removeModifier(id);
	}

	public void removeSkillModifier(SkillType type) {
		skillManager.removeModifiers(type);
	}

	public void incrementeSkill(SkillPrimary type) {
		skillManager.incrementeSkill(type);
	}

	public void incrementeSkill(SkillSecondary type) {
		skillManager.incrementeSkill(type);
	}

	public void cleanJoin() {
		statManager.clean();
		skillManager.clean();
	}

	public void cleanQuit() {
		statManager.clean();
		statManager.cleanTask();
		skillManager.clean();
		skillManager.cleanTask();
	}

	public void cleanDeath() {
		statManager.clean();
		skillManager.clean();
	}

	public void reset() {
		statManager.reset();
		resourceManager.reset();
	}

	public void resetAll() {
		statManager.resetAll();
		skillManager.resetAll();
		resourceManager.reset();
	}

	/*
	** getter + setter
	*/

	public Player getPlayer() {
		return player;
	}

	// resource

	public void loadPower(ClassType type) {
		resourceManager.loadPower(type);
	}

	public Health getHealth() {
		return resourceManager.getHealth();
	}

	public Level getLevel() {
		return resourceManager.getLevel();
	}

	public Resource getPower() {
		return resourceManager.getPower();
	}

	// stat

	public Stat getStat(String name) {
		return statManager.getStat(name);
	}
	
	public Stat getStat(StatType type) {
		return statManager.getStat(type);
	}

	public Set<StatModifier> getStatModifier(StatType type) {
		return statManager.getModifier(type);
	}

	public Map<Integer, StatModifier> getStatModifiers() {
		return statManager.getModifiers();
	}

	// skill

	public Skill getSkill(String name) {
		return skillManager.getSkill(name);
	}

	public Skill getSkill(SkillType type) {
		return skillManager.getSkill(type);
	}

	public Set<SkillModifier> getSkillModifier(SkillType type) {
		return skillManager.getModifier(type);
	}

	public Map<Integer, SkillModifier> getSkillModifiers() {
		return skillManager.getModifiers();
	}

	// static

	public static boolean isStatModifier(NamespacedKey key) {
		return key.getNamespace().equals(RpgCraft.name()) && key.getKey().startsWith("statmodifier");
	}

	public static boolean isSkillModifier(NamespacedKey key) {
		return key.getNamespace().equals(RpgCraft.name()) && key.getKey().startsWith("skillmodifier");
	}

	public static int parseId(String[] array) {
		int		id = 0;
		try {
			id = Integer.parseInt(array[2]);
		}
		catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			// do nothing
		}
		return id;
	}
}
