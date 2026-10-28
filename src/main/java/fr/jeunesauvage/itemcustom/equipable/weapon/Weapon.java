package fr.jeunesauvage.itemcustom.equipable.weapon;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.EquipmentSlot;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.itemcustom.WeaponType;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.itembuilder.EquipableStat;
import fr.jeunesauvage.itemcustom.usable.Usable;

public class Weapon extends Equipable<WeaponType> implements Usable {
	public Weapon(String name, WeaponType type, EquipableStat equipableStat) {
		super(name, equipableStat, type);
	}

	@Override
	public Material getMaterial() {
		return type.getMaterial();
	}

	@Override
	public boolean canUse(PlayerCustom playerCustom, EquipmentSlot slot) {
		// silence
		int	duration = playerCustom.isSilence();
		if (duration > 0) {
			playerCustom.sendActionBar(Message.silence(duration));
			return false;
		}
		// cooldown
		duration = playerCustom.hasCooldown(type.getMaterial());
		if (duration > 0) {
			playerCustom.sendActionBar(Message.cooldown(duration));
			return false;
		}
		return true;
	}

	@Override
	public boolean use(PlayerCustom playerCustom, EquipmentSlot slot) {
		if (name.contains("hamaxe") || name.contains("slapperaxe")
			|| name.endsWith("hit_of_the_cauldron")
			|| name.endsWith("sulfurizer")
			|| name.endsWith("haemorrhaxe")
		)
			switchHamaxe(playerCustom, slot);
		else if (name.equals("netherite_sword/claw_lightning"))
			flap(playerCustom, slot);
		else if (playerCustom.isSneaking() && name.contains("power_ring"))
			switchPowerRing(playerCustom, slot);
		else
			return false;
		return true;
	}

	// hamaxe

	private void switchHamaxe(PlayerCustom playerCustom, EquipmentSlot slot) {
		if (slot != EquipmentSlot.HAND && slot != EquipmentSlot.OFF_HAND) return;
		Weapon	actual = RpgCraft.getItemCustomRegistry().getWeapon(playerCustom.getInventory().getItem(slot));
		if (actual == null) return;
		String	id = actual.getIdentifier();
		Weapon	weapon = null;
		if (actual.getType() == WeaponType.AXE)
			weapon = RpgCraft.getItemCustomRegistry().getWeapon("mace" + id.substring(id.indexOf('/')));
		else if (actual.getType() == WeaponType.MACE)
			weapon = RpgCraft.getItemCustomRegistry().getWeapon("netherite_axe" + id.substring(id.indexOf('/')));
		if (weapon == null) return;
		playerCustom.getInventory().setItem(slot, weapon.getItemClone());
		playerCustom.addCooldown(weapon.getMaterial(), 2);
	}

	private void flap(PlayerCustom playerCustom, EquipmentSlot slot) {
		World	world = playerCustom.getWorld();
		if (world == null) return;
		playerCustom.setVelocity(playerCustom.getVelocity().setY(1));
		world.playSound(playerCustom.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5f, 1f);
		playerCustom.addCooldown(type.getMaterial(), 10);
	}

	// power ring

	private void switchPowerRing(PlayerCustom playerCustom, EquipmentSlot slot) {
		if (slot != EquipmentSlot.HAND && slot != EquipmentSlot.OFF_HAND) return;
		Weapon	actual = RpgCraft.getItemCustomRegistry().getWeapon(playerCustom.getInventory().getItem(slot));
		if (actual == null) return;
		String	id = actual.getIdentifier();
		Weapon	weapon = switch (actual.getType()) {
			case BOW -> RpgCraft.getItemCustomRegistry().getWeapon("crossbow" + id.substring(id.indexOf('/')));
			case CROSSBOW -> RpgCraft.getItemCustomRegistry().getWeapon("netherite_axe" + id.substring(id.indexOf('/')));
			case AXE -> RpgCraft.getItemCustomRegistry().getWeapon("netherite_hoe" + id.substring(id.indexOf('/')));
			case HOE -> RpgCraft.getItemCustomRegistry().getWeapon("mace" + id.substring(id.indexOf('/')));
			case MACE -> RpgCraft.getItemCustomRegistry().getWeapon("netherite_pickaxe" + id.substring(id.indexOf('/')));
			case PICKAXE -> RpgCraft.getItemCustomRegistry().getWeapon("netherite_shovel" + id.substring(id.indexOf('/')));
			case SHOVEL -> RpgCraft.getItemCustomRegistry().getWeapon("netherite_sword" + id.substring(id.indexOf('/')));
			case SWORD -> RpgCraft.getItemCustomRegistry().getWeapon("shield" + id.substring(id.indexOf('/')));
			case SHIELD -> RpgCraft.getItemCustomRegistry().getWeapon("bow" + id.substring(id.indexOf('/')));
			default -> null;
		};
		if (weapon == null) return;
		playerCustom.getInventory().setItem(slot, weapon.getItemClone());
		playerCustom.addCooldown(weapon.getMaterial(), 1);
	}
}
