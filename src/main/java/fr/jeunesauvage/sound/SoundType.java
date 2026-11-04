package fr.jeunesauvage.sound;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

public enum SoundType {
	AMBIENT("ambient"),
	HURT("hurt"),
	ATTACK("attack"),
	STEP("step"),
	DEATH("death");

	private final String	name;
	
	SoundType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static SoundType fromSound(Sound sound) {
        NamespacedKey   key = Registry.SOUNDS.getKey(sound);
		String			s = key.getKey();
		if (s.endsWith(".ambient"))
			return SoundType.AMBIENT;
		else if (s.endsWith(".hurt"))
			return SoundType.HURT;
		else if (s.endsWith(".step"))
			return SoundType.STEP;
		else if (s.endsWith(".death"))
			return SoundType.DEATH;
		return null;
	}
}