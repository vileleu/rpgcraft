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
		return (uuid == uuid1 || uuid == uuid2);
	}

	public UUID getUuid1() {
		return uuid1;
	}

	public UUID getUuid2() {
		return uuid2;
	}
}
