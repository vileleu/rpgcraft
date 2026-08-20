package fr.jeunesauvage.itemcustom.equipable.armor;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.itemcustom.ItemCustomManager;

public class ArmorManager implements Listener {
	private final ItemCustomManager	itemCustomManager;

	public ArmorManager(ItemCustomManager itemCustomManager) {
		this.itemCustomManager = itemCustomManager;
	}

	// armor equip
	@EventHandler
	public void onArmorEquip(PlayerArmorChangeEvent e) {
		Player	player = e.getPlayer();
		if (!canWear(player, e.getNewItem())) {
			PlayerInventory	inv = player.getInventory();
			EquipmentSlot	slot = EquipmentSlot.valueOf(e.getSlotType().name());
			ItemStack		armor = inv.getItem(slot);
			inv.setItem(slot, null);
			player.getWorld().dropItem(player.getLocation(), armor);
		}
		else if (isSameArmor(e.getOldItem(), e.getNewItem())) return;
		itemCustomManager.getEquipableManager().refreshEquipement(e.getPlayer());
	}

	private boolean canWear(Player player, ItemStack item) {
		if (player.hasMetadata("NPC")) return true;
		PlayerCustom	playerCustom = PlayerCustomManager.getPlayerCustom(player);
		Armor	armor = itemCustomManager.getArmor(item);
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

	// is Armor
	public static boolean isArmor(Material m) {
		String	s = m.name();
	    if (s.endsWith("_HELMET") 
		|| s.endsWith("_CHESTPLATE")
		|| s.endsWith("_LEGGINGS")
		|| s.endsWith("_BOOTS")
		|| s.equals("ELYTRA"))
			return true;
		return false;
	}
}
