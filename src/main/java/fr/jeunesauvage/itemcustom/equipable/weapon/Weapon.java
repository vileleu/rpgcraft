package fr.jeunesauvage.itemcustom.equipable.weapon;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.itembuilder.EquipableStat;
import fr.jeunesauvage.itemcustom.usable.Usable;

public class Weapon extends Equipable<WeaponType> implements Usable {
	public Weapon(String name, WeaponType type, EquipableStat equipableStat, int customModelData) {
		super(name, equipableStat, type, customModelData);
	}

	@Override
	public ItemCustomCategory getCategory() {
		return type.getCategory();
	}

	@Override
	public Material getMaterial() {
		return type.getMaterial();
	}

	@Override
	public boolean canUse(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot) {
		// silence
		int	duration = playerCustom.isSilence();
		if (duration > 0) {
			playerCustom.getPlayer().sendActionBar(Message.silence(duration));
			return false;
		}
		// cooldown
		duration = playerCustom.hasCooldown(type.getMaterial());
		if (duration > 0) {
			playerCustom.getPlayer().sendActionBar(Message.cooldown(duration));
			return false;
		}
		return true;
	}

	@Override
	public void use(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot) {
		switch (name) {
			// hamaxe
			case "space_hamaxe_hammer", "space_hamaxe_axe", "vortex_hamaxe_hammer",
				"vortex_hamaxe_axe", "lava_warrior_hamaxe_hammer", "lava_warrior_hamaxe_axe",
				"cauldron_hamaxe_hammer", "cauldron_hamaxe_axe", "beenest_hamaxe_hammer",
				"beenest_hamaxe_axe" -> switchHamaxe(itemCustomManager, playerCustom, slot);
			// flap with claw
			case "claw_lightning" -> flap(itemCustomManager, playerCustom, slot);
			default -> {return;}
		}
	}

	// hamaxe

	private String findIdentifier() {
		int		slot = name.lastIndexOf('_');
		if (slot == -1) return null;
		slot++;
		if (name.endsWith("hammer"))
			return name.substring(0, slot) + "axe";
		else if (name.endsWith("axe"))
			return name.substring(0, slot) + "hammer";
		return null;
	}

	private void switchHamaxe(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot) {
		if (slot != EquipmentSlot.HAND && slot != EquipmentSlot.OFF_HAND) return;
		String	identifier = findIdentifier();
		if (identifier == null) return;
		Weapon	weapon = itemCustomManager.getWeapon(identifier);
		playerCustom.getPlayer().getInventory().setItem(slot, weapon.getItemClone());
		playerCustom.addCooldown(weapon.getMaterial(), 10);
	}

	private void flap(ItemCustomManager itemCustomManager, PlayerCustom playerCustom, EquipmentSlot slot) {
		Player	player = playerCustom.getPlayer();
		player.setVelocity(player.getVelocity().setY(1));
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5f, 1f);
		playerCustom.addCooldown(type.getMaterial(), 10);
	}
}
