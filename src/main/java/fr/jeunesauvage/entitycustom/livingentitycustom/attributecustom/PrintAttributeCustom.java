package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.Skill;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillSecondary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.Stat;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PrintAttributeCustom {
	private final LivingEntityCustom	target;

	public PrintAttributeCustom(LivingEntityCustom target) {
		this.target = target;
	}

	public List<Component> printStatPrimary() {
        List<Component>	lore = new ArrayList<>();
        for (StatPrimary type: StatPrimary.values()) {
            Stat                        	stat = target.getStat(type);
            int                         	value = stat.getValue();
			Component						component = Lore.stat(type, value);
            Map<Integer, AttributeModifier>	modifiers = target.getModifiers();
			for (AttributeModifier modifier: modifiers.values()) {
                if (modifier.getType() != type) continue;
                value = modifier.getValue();
				if (value < 0)
					component = component.append(Component.text(" " + value).color(NamedTextColor.RED));
				else
					component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
				component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
			}
			lore.add(Message.c(component));
        }
		return lore;
	}

	public List<Component> printStatSecondary() {
        List<Component>	lore = new ArrayList<>();
        for (StatSecondary type: StatSecondary.values()) {
            Stat                        	stat = target.getStat(type);
            int                         	value = stat.getValue();
			Component						component = Lore.stat(type, value);
            Map<Integer, AttributeModifier>	modifiers = target.getModifiers();
			for (AttributeModifier modifier: modifiers.values()) {
                if (modifier.getType() != type) continue;
                value = modifier.getValue();
				if (value < 0)
					component = component.append(Component.text(" " + value).color(NamedTextColor.RED));
				else
					component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
				component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
			}
			lore.add(Message.c(component));
        }
		return lore;
	}

	public List<Component> printSkillPrimary() {
        List<Component>	lore = new ArrayList<>();
        for (SkillPrimary type: SkillPrimary.values()) {
            Skill                        	skill = target.getSkill(type);
            int                         	value = skill.getValue();
			Component						component = Lore.skill(type, value);
            Map<Integer, AttributeModifier>	modifiers = target.getModifiers();
			for (AttributeModifier modifier: modifiers.values()) {
                if (modifier.getType() != type) continue;
                value = modifier.getValue();
				if (value < 0)
					component = component.append(Component.text(" " + value).color(NamedTextColor.RED));
				else
					component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
				component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
			}
			lore.add(Message.c(component));
        }
		return lore;
	}

	public List<Component> printSkillSecondary() {
        List<Component>	lore = new ArrayList<>();
        for (SkillSecondary type: SkillSecondary.values()) {
            Skill                        	skill = target.getSkill(type);
            int                         	value = skill.getValue();
			Component						component = Lore.skill(type, value);
            Map<Integer, AttributeModifier>	modifiers = target.getModifiers();
			for (AttributeModifier modifier: modifiers.values()) {
                if (modifier.getType() != type) continue;
                value = modifier.getValue();
				if (value < 0)
					component = component.append(Component.text(" " + value).color(NamedTextColor.RED));
				else
					component = component.append(Component.text(" +" + value).color(NamedTextColor.GREEN));
				component = component.append(Component.text("(id:" + modifier.getId() + ")").color(NamedTextColor.BLUE));
			}
			lore.add(Message.c(component));
        }
		return lore;
	}
}
