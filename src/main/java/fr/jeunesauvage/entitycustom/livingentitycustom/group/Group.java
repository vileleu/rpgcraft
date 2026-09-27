package fr.jeunesauvage.entitycustom.livingentitycustom.group;

import java.util.UUID;

import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;

public class Group {
	private final UUID	uuid1;
	private final UUID	uuid2;

	public Group(LivingEntityCustom l1, LivingEntityCustom l2) {
		uuid1 = l1.getUUID();
		uuid2 = l2.getUUID();
	}

	public boolean in(LivingEntityCustom livingEntityCustom) {
		if (livingEntityCustom == null) return false;
		UUID	uuid = livingEntityCustom.getUUID();
		if (livingEntityCustom.getPet() == uuid1 || livingEntityCustom.getPet() == uuid2) return true;
		if (livingEntityCustom.getOwner() == uuid1 || livingEntityCustom.getOwner() == uuid2) return true;
		return (uuid == uuid1 || uuid == uuid2);
	}

	public UUID getUuid1() {
		return uuid1;
	}

	public UUID getUuid2() {
		return uuid2;
	}
}
