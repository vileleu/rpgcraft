package fr.jeunesauvage.combat;

import java.util.Set;

import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;

public enum CombatType {
	CLOSE("close", Set.of(WeaponType.AXE, WeaponType.HAND, WeaponType.HOE, WeaponType.MACE, WeaponType.PICKAXE, WeaponType.SHOVEL, WeaponType.SWORD, WeaponType.TRIDENT)),
	RANGE("range", Set.of(WeaponType.BOW, WeaponType.CROSSBOW, WeaponType.SPELLBOOK, WeaponType.STAFF, WeaponType.TRIDENT)),
	UNKNOWN("unknown", Set.of(WeaponType.UNKNOWN));

	private final String			name;
	private final Set<WeaponType>	weaponTypes;

	CombatType(String name, Set<WeaponType> weaponTypes) {
		this.name = name;
		this.weaponTypes = weaponTypes;
	}

	public String getName() {
		return name;
	}

	public Set<WeaponType> getWeaponTypes() {
		return weaponTypes;
	}
}
