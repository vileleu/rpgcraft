package fr.jeunesauvage.entity.playercustom;

import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Health;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Level;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Resource;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillSecondary;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.Stat;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassCustom;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.playercustom.cooldown.Cooldown;
import fr.jeunesauvage.entity.playercustom.equipementcustom.EquipementCustom;
import fr.jeunesauvage.entity.playercustom.scoreboardcustom.ScoreboardCustom;
import fr.jeunesauvage.entity.playercustom.silence.Silence;
import fr.jeunesauvage.entity.race.RaceCustom;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.skin.Skin;
import fr.jeunesauvage.skin.SkinData;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.exception.MineSkinException;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;

public class PlayerCustom {
	// player data
	private final Player			player;
	private final ItemCustomManager	itemCustomManager;
	private final ResourceManager	resourceManager;
	private final RaceCustom		raceCustom;
	private final ClassCustom		classCustom;
	private final ScoreboardCustom	scoreboardCustom;
	private final AttributeManager	attributeManager;
	private final EquipementCustom	equipementCustom;
	private final Cooldown			cooldown;
	private final Silence			silence;

	PlayerCustom(Player p, ItemCustomManager itemCustomManager) {
		// initialisation
		this.player = p;
		this.itemCustomManager = itemCustomManager;
		this.raceCustom = new RaceCustom(player);
		this.classCustom = new ClassCustom(player);
		this.resourceManager = new ResourceManager(player, classCustom);
		this.scoreboardCustom = new ScoreboardCustom(player, resourceManager, raceCustom, classCustom);
		this.attributeManager = new AttributeManager(player, resourceManager, scoreboardCustom);
		this.equipementCustom = new EquipementCustom(player, attributeManager, itemCustomManager);
		this.cooldown = new Cooldown(player);
		this.silence = new Silence(player);
		scoreboardCustom.refreshAll();
	}

	// stat

	public int addStatModifier(StatType type, int value, int duration) {
		if (value == 0) return -1;
		return attributeManager.addStatModifier(type, value, duration);
	}

	public void removeStatModifier(int id) {
		attributeManager.removeStatModifier(id);
	}

	public void removeStatModifier(StatType type) {
		attributeManager.removeStatModifier(type);
	}

	// skill

	public int addSkillModifier(SkillType type, int value, int duration) {
		if (value == 0) return -1;
		return attributeManager.addSkillModifier(type, value, duration);
	}

	public void removeSkillModifier(int id) {
		attributeManager.removeSkillModifier(id);
	}

	public void removeSkillModifier(SkillType type) {
		attributeManager.removeSkillModifier(type);
	}

	public void incrementeSkill(SkillPrimary type) {
		attributeManager.incrementeSkill(type);
	}

	public void incrementeSkill(SkillSecondary type) {
		attributeManager.incrementeSkill(type);
	}

	// playercustom

	public void refreshEquipement() {
		RpgCraft.debug("refresh()");
		equipementCustom.refresh();
	}

	public void updateLevel(int n) {
		attributeManager.updateLevel(n);
	}

	// cooldown

	public void addCooldown(Material material, int duration) {
		cooldown.add(material, duration);
	}

	public int hasCooldown(Material material) {
		return cooldown.has(material);
	}

	public void refreshCooldown() {
		Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () ->  cooldown.refresh(), 10L);
	}

	// silence

	public void addSilence(int duration) {
		silence.add(duration);
	}

	public int isSilence() {
		return silence.is();
	}

	// clean

	public void cleanJoin() {
		attributeManager.cleanJoin();
		itemCustomManager.getSpellManager().clean(this);
	}

	public void cleanQuit() {
		attributeManager.cleanQuit();
		itemCustomManager.getSpellManager().clean(this);
	}

	public void cleanDeath() {
		attributeManager.cleanDeath();
		itemCustomManager.getSpellManager().clean(this);
	}

	/*
	** getter + setter
	*/

	public Player getPlayer() {
		return player;
	}

	public RaceType getRaceType() {
		return raceCustom.getRaceType();
	}

	public void setRaceType(RaceType type) {
		cleanJoin();
		raceCustom.setRaceCustom(type);
		scoreboardCustom.refreshRace();
		EntityManager.setScale(this);
	}

	public FormType getFormType() {
		return raceCustom.getFormType();
	}

	public void setFormType(FormType formType) {
		raceCustom.setFormType(formType);
		EntityManager.setScale(this);
	}

	public ClassType getClassType() {
		return classCustom.getClassType();
	}

	public void setClassType(ClassType type) {
		attributeManager.reset();
		classCustom.setClassCustom(type);
		resourceManager.loadPower(classCustom.getClassType());
		equipementCustom.refresh();
		scoreboardCustom.refreshAll();
	}

	public void reset() {
		attributeManager.resetAll();
		classCustom.setClassCustom(ClassType.BEGGAR);
		equipementCustom.refresh();
		scoreboardCustom.refreshAll();
	}

	public Health getHealth() {
		return attributeManager.getHealth();
	}

	public Level getLevel() {
		return attributeManager.getLevel();
	}

	public Resource getPower() {
		return attributeManager.getPower();
	}

	public Stat getStat(String name) {
		return attributeManager.getStat(name);
	}

	public Stat getStat(StatType type) {
		return attributeManager.getStat(type);
	}

	public Map<Integer, StatModifier> getStatModifiers() {
		return attributeManager.getStatModifiers();
	}

	public Skill getSkill(String name) {
		return attributeManager.getSkill(name);
	}

	public Skill getSkill(SkillType type) {
		return attributeManager.getSkill(type);
	}

	public Map<Integer, SkillModifier> getSkillModifiers() {
		return attributeManager.getSkillModifiers();
	}

	public ScoreboardCustom getScoreboardCustom() {
		return scoreboardCustom;
	}

	public static void setSkin(Player player, String skinName) {
		if (skinName != null) {
        	SkinStorage		skinStorage = RpgCraft.instanceSkinsRestorer().getSkinStorage();
			SkinData		skinData = Skin.getSkinData(skinName);
        	skinStorage.setCustomSkinData(skinName, SkinProperty.of(skinData.getValue(), skinData.getSignature()));
			Bukkit.getScheduler().runTaskAsynchronously(RpgCraft.instance(), () -> {
				try {
        			Optional<InputDataResult> result = skinStorage.findOrCreateSkinData(skinName);
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
		}
		else {
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
	}
}
