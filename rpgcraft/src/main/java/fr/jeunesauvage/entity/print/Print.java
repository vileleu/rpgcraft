package fr.jeunesauvage.entity.print;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entity.modifier.EntityModifier;
import fr.jeunesauvage.entity.modifier.EntityModifierManager;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillSecondary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.Stat;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatModifier;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Print {
	private final EntityModifierManager	entityModifierManager;
	private final LivingEntity			entity;
	private final boolean				isPlayer;

	public Print(Player entity) {
		this.entityModifierManager = null;
		this.entity = entity;
		this.isPlayer = true;
	}

	public Print(LivingEntity entity, EntityModifierManager entityModifierManager) {
		this.entityModifierManager = entityModifierManager;
		this.entity = entity;
		this.isPlayer = false;
	}

	public List<Component> printStatPrimary() {
        List<Component>	lore = new ArrayList<>();
		if (isPlayer) {
			PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom((Player)entity);
        	for (StatPrimary type: StatPrimary.values()) {
        	    Stat                        stat = playerCustom.getStat(type);
        	    int                         value = stat.getValue() - stat.getValueBonus();
				Component					component = Lore.stat(type, value);
        	    Map<Integer, StatModifier>  modifiers = playerCustom.getStatModifiers();
				for (StatModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
        	    	TextColor	color = value < 0 ? NamedTextColor.RED : NamedTextColor.GREEN;
					component = component.append(Component.text(" " + value).color(color));
				}
				lore.add(component);
        	}
		}
		else {
			for (StatPrimary type: StatPrimary.values()) {
        	    int			value = 0;
				Component	component = Lore.stat(type, value);
				lore.add(component);
			}
		}
		return lore;
	}

	public List<Component> printStatSecondary() {
        List<Component>	lore = new ArrayList<>();
		if (isPlayer) {
			PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom((Player)entity);
        	for (StatSecondary type: StatSecondary.values()) {
        	    Stat                        stat = playerCustom.getStat(type);
        	    int                         value = stat.getValue() - stat.getValueBonus();
				Component					component = Lore.stat(type, value);
        	    Map<Integer, StatModifier>  modifiers = playerCustom.getStatModifiers();
				for (StatModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
        	    	TextColor	color = value < 0 ? NamedTextColor.RED : NamedTextColor.GREEN;
					component = component.append(Component.text(" " + value).color(color));
				}
				lore.add(component);
        	}
		}
		else {
			for (StatSecondary type: StatSecondary.values()) {
        	    int                         value = 0;
				Component					component = Lore.stat(type, value);
        	    Map<Integer, EntityModifier>  modifiers = entityModifierManager.getEntityModifiers(entity);
				for (EntityModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	    	value = modifier.getValue();
        	    	TextColor	color = value < 0 ? NamedTextColor.RED : NamedTextColor.GREEN;
					component = component.append(Component.text(" " + value).color(color));
				}
				lore.add(component);
			}
		}
		return lore;
	}

	public List<Component> printSkillPrimary() {
        List<Component>	lore = new ArrayList<>();
		if (isPlayer) {
			PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom((Player)entity);
        	for (SkillPrimary type: SkillPrimary.values()) {
        	    Skill                       skill = playerCustom.getSkill(type);
        	    int                         value = skill.getValue() - skill.getValueBonus();
				Component					component = Lore.skill(type, value);
        	    Map<Integer, SkillModifier>  modifiers = playerCustom.getSkillModifiers();
				for (SkillModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
        	    	TextColor	color = value < 0 ? NamedTextColor.RED : NamedTextColor.GREEN;
					component = component.append(Component.text(" " + value).color(color));
				}
				lore.add(component);
        	}
		}
		else {
			for (SkillPrimary type: SkillPrimary.values()) {
        	    int			value = 0;
				Component	component = Lore.skill(type, value);
				lore.add(component);
			}
		}
		return lore;
	}

	public List<Component> printSkillSecondary() {
        List<Component>	lore = new ArrayList<>();
		if (isPlayer) {
			PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom((Player)entity);
        	for (SkillSecondary type: SkillSecondary.values()) {
        	    Skill                       skill = playerCustom.getSkill(type);
        	    int                         value = skill.getValue() - skill.getValueBonus();
				Component					component = Lore.skill(type, value);
        	    Map<Integer, SkillModifier>  modifiers = playerCustom.getSkillModifiers();
				for (SkillModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
        	    	TextColor	color = value < 0 ? NamedTextColor.RED : NamedTextColor.GREEN;
					component = component.append(Component.text(" " + value).color(color));
				}
				lore.add(component);
        	}
		}
		else {
			for (SkillSecondary type: SkillSecondary.values()) {
        	    int			value = 0;
				Component	component = Lore.skill(type, value);
				lore.add(component);
			}
		}
		return lore;
	}

    public LivingEntity getEntity() {
		return entity;
	}

	public boolean isPlayer() {
		return isPlayer;
	}
}
