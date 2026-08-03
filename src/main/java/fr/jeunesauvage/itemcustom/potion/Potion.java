package fr.jeunesauvage.itemcustom.potion;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Health;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.Resource;
import fr.jeunesauvage.entity.playercustom.attributecustom.resource.ResourceType;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.consumable.Consumable;
import fr.jeunesauvage.itemcustom.consumable.ConsumableManager;
import net.kyori.adventure.text.Component;

public class Potion extends ItemCustom<PotionType> implements Consumable {
	public Potion(PotionType type, String name, Rarity rarity, int level) {
		super(type, name, rarity, level);
		buildPotion();
	}

	private void buildPotion() {
		ItemMeta	meta = item.getItemMeta();
		meta.displayName(Lore.nameSpell(name, rarity));
        Data.setString(meta.getPersistentDataContainer(), KEY_IDENTIFIER, name);
		// write lore
		List<Component>	lore = new ArrayList<>();
		lore.add(Lore.type(type));
		lore.add(Lore.rarity(rarity));
		lore.add(Lore.level(level));
		if (type.getClassTypes() != null)
			lore.add(Lore.classType(type.getClassTypes()));
		lore.add(Lore.description(name));
		meta.lore(lore);
		if (meta instanceof PotionMeta potionMeta)
		    potionMeta.setColor(type.getColor());
        item.setItemMeta(meta);
	}

	@Override
	public ItemCustomCategory getCategory() {
		return type.getCategory();
	}

	@Override
	public Material	getMaterial() {
		return type.getMaterial();
	}

	@Override
	public Component toComponent() {
        return Component.translatable("potion.rpgcraft." + name);
	}

	@Override
	public void consume(ConsumableManager consumableManager, PlayerCustom playerCustom) {
		switch (type) {
			case PotionType.POTION_HEALTH -> potionHealth(playerCustom);
			case PotionType.POTION_MANA -> potionMana(playerCustom);
			case PotionType.POTION_RAGE -> potionRage(playerCustom);
			case PotionType.POTION_ENERGY -> potionEnergy(playerCustom);
			default -> {return;}
		}
	}

	@Override
	public boolean canConsume(PlayerCustom playerCustom) {
		// cooldown
		int	duration = playerCustom.hasCooldown(getMaterial());
		if (duration > 0) {
			playerCustom.getPlayer().sendActionBar(Message.cooldown(duration));
			return false;
		}
		playerCustom.addCooldown(type.getMaterial(), type.getCooldown());
		return true;
	}

	private void potionHealth(PlayerCustom playerCustom) {
		Health	health = playerCustom.getHealth();
		int	amount = 50 * rarity.getNumber();
		health.increase(amount);
		playerCustom.getScoreboardCustom().refreshHealth();
	}

	private void potionMana(PlayerCustom playerCustom) {
		Resource	power = playerCustom.getPower();
		if (power == null || power.getType() != ResourceType.MANA) return;
		int	amount = 10 * rarity.getNumber();
		power.increase(amount);
		playerCustom.getScoreboardCustom().refreshPower();
	}

	private void potionRage(PlayerCustom playerCustom) {
		Resource	power = playerCustom.getPower();
		if (power == null || power.getType() != ResourceType.RAGE) return;
		int	amount = 10 * rarity.getNumber();
		power.increase(amount);
		playerCustom.getScoreboardCustom().refreshPower();
	}

	private void potionEnergy(PlayerCustom playerCustom) {
		Resource	power = playerCustom.getPower();
		if (power == null || power.getType() != ResourceType.ENERGY) return;
		int	amount = 10 * rarity.getNumber();
		power.increase(amount);
		playerCustom.getScoreboardCustom().refreshPower();
	}
}
