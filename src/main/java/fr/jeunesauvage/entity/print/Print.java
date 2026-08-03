package fr.jeunesauvage.entity.print;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;

import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entity.modifier.EntityModifier;
import fr.jeunesauvage.entity.modifier.EntityModifierManager;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
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

public class Print {
	private final EntityModifierManager	entityModifierManager;
	private final PlayerCustom			targetPlayer;
	private final LivingEntity			targetMob;

	public Print(PlayerCustom target) {
		this.entityModifierManager = null;
		this.targetPlayer = target;
		this.targetMob = null;
	}

	public Print(LivingEntity target, EntityModifierManager entityModifierManager) {
		this.entityModifierManager = entityModifierManager;
		this.targetPlayer = null;
		this.targetMob = target;
	}

	public List<Component> printStatPrimary() {
        List<Component>	lore = new ArrayList<>();
		if (isPlayer()) {
        	for (StatPrimary type: StatPrimary.values()) {
        	    Stat                        stat = targetPlayer.getStat(type);
        	    int                         value = stat.getValue() - stat.getValueBonus();
				Component					component = Lore.stat(type, value);
        	    Map<Integer, StatModifier>  modifiers = targetPlayer.getStatModifiers();
				for (StatModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
					if (value < 0)
						component = component.append(Component.text(" -" + value).color(NamedTextColor.RED));
					else
						component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
					component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
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
		if (isPlayer()) {
        	for (StatSecondary type: StatSecondary.values()) {
        	    Stat                        stat = targetPlayer.getStat(type);
        	    int                         value = stat.getValue() - stat.getValueBonus();
				Component					component = Lore.stat(type, value);
        	    Map<Integer, StatModifier>  modifiers = targetPlayer.getStatModifiers();
				for (StatModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
					if (value < 0)
						component = component.append(Component.text(" -" + value).color(NamedTextColor.RED));
					else
						component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
					component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
				}
				lore.add(component);
        	}
		}
		else {
			for (StatSecondary type: StatSecondary.values()) {
        	    int                         value = 0;
				Component					component = Lore.stat(type, value);
        	    Map<Integer, EntityModifier>  modifiers = entityModifierManager.getEntityModifiers(targetMob);
				for (EntityModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	    	value = modifier.getValue();
					if (value < 0)
						component = component.append(Component.text(" -" + value).color(NamedTextColor.RED));
					else
						component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
					component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
				}
				lore.add(component);
			}
		}
		return lore;
	}

	public List<Component> printSkillPrimary() {
        List<Component>	lore = new ArrayList<>();
		if (isPlayer()) {
        	for (SkillPrimary type: SkillPrimary.values()) {
        	    Skill                       skill = targetPlayer.getSkill(type);
        	    int                         value = skill.getValue() - skill.getValueBonus();
				Component					component = Lore.skill(type, value);
        	    Map<Integer, SkillModifier>  modifiers = targetPlayer.getSkillModifiers();
				for (SkillModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
					if (value < 0)
						component = component.append(Component.text(" -" + value).color(NamedTextColor.RED));
					else
						component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
					component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
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
		if (isPlayer()) {
        	for (SkillSecondary type: SkillSecondary.values()) {
        	    Skill                       skill = targetPlayer.getSkill(type);
        	    int                         value = skill.getValue() - skill.getValueBonus();
				Component					component = Lore.skill(type, value);
        	    Map<Integer, SkillModifier>  modifiers = targetPlayer.getSkillModifiers();
				for (SkillModifier modifier: modifiers.values()) {
        	        if (modifier.getType() != type) continue;
        	        value = modifier.getValue();
					if (value < 0)
						component = component.append(Component.text(" -" + value).color(NamedTextColor.RED));
					else
						component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
					component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
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

	private boolean isPlayer() {
		return targetPlayer != null;
	}
}
