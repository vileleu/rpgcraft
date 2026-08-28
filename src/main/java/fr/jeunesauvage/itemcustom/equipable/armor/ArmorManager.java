package fr.jeunesauvage.itemcustom.equipable.armor;

import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;

public class ArmorManager implements Listener {
	// armor equip
	@EventHandler
	public void onArmorEquip(PlayerArmorChangeEvent e) {
		Player			p = e.getPlayer();
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(p.getUniqueId());
		if (playerCustom == null) return;
		World	world = playerCustom.getWorld();
		if (world == null) return;
		if (!canWear(playerCustom, e.getNewItem())) {
			PlayerInventory	inv = playerCustom.getInventory();
			EquipmentSlot	slot = EquipmentSlot.valueOf(e.getSlotType().name());
			ItemStack		armor = inv.getItem(slot);
			inv.setItem(slot, null);
			world.dropItem(playerCustom.getLocation(), armor);
		}
		else if (isSameArmor(e.getOldItem(), e.getNewItem())) return;
		playerCustom.refreshStat();
	}

	private boolean canWear(PlayerCustom playerCustom, ItemStack item) {
		Armor	armor = RpgCraft.getItemCustomRegistry().getArmor(item);
		if (armor == null) return true;
		Set<ClassType>	classTypes = armor.getType().getArmorMaterial().getClassTypes();
		ClassType		classPlayer = playerCustom.getClassType();
		if (classTypes.contains(ClassType.BEGGAR) || classPlayer == ClassType.GOD) return true;
		return classTypes.contains(classPlayer);
	}

	private boolean isSameArmor(ItemStack a, ItemStack b) {
	    if (a == null || b == null) return false;
	    if (a.getType() != b.getType()) return false;
	    ItemMeta	metaA = a.getItemMeta();
	    ItemMeta	metaB = b.getItemMeta();
	    if (metaA == null && metaB == null) return true;
	    if (metaA == null || metaB == null) return false;
	    if (metaA instanceof Damageable) ((Damageable)metaA).setDamage(0);
	    if (metaB instanceof Damageable) ((Damageable)metaB).setDamage(0);
	    return metaA.equals(metaB);
	}
}
