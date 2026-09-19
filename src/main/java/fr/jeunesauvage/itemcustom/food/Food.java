package fr.jeunesauvage.itemcustom.food;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.consumable.Consumable;
import net.kyori.adventure.text.Component;

public class Food extends ItemCustom<FoodType> implements Consumable {
	public Food(FoodType type) {
		super(type, type.getName(), Rarity.COMMON, 1);
		buildFood();
	}

	private void buildFood() {
		ItemMeta	meta = item.getItemMeta();
		meta.displayName(Lore.nameFood(name, rarity));
        Data.setString(meta.getPersistentDataContainer(), KEY_IDENTIFIER, name);
		// write lore
		List<Component>	lore = new ArrayList<>();
		lore.add(Lore.type(type));
		lore.add(Lore.rarity(rarity));
		lore.add(Lore.level(level));
		if (type.getClassTypes() != null)
			lore.add(Lore.classType(type.getClassTypes()));
		meta.lore(lore);
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
	public void consume(PlayerCustom playerCustom) {
		return;
	}

	@Override
	public boolean canConsume(PlayerCustom playerCustom) {
		// cooldown
		int	duration = playerCustom.hasCooldown(getMaterial());
		if (duration > 0) {
			playerCustom.sendActionBar(Message.cooldown(duration));
			return false;
		}
		playerCustom.addCooldown(type.getMaterial(), type.getCooldown());
		return true;
	}
}
