package fr.jeunesauvage.itemcustom.equipable.armor;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;

import fr.jeunesauvage.itemcustom.equipable.EquipableManager;

public class ArmorManager implements Listener {
	private final EquipableManager	equipableManager;

	public ArmorManager(EquipableManager equipableManager) {
		this.equipableManager = equipableManager;
	}

	// armor equip
	@EventHandler
	public void onArmorEquip(PlayerArmorChangeEvent e) {
		if (isSameArmor(e.getOldItem(), e.getNewItem())) return;
		equipableManager.refreshEquipement(e.getPlayer());
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
