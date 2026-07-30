package fr.jeunesauvage.sound;

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
		if (sound.name().endsWith("_AMBIENT"))
			return SoundType.AMBIENT;
		else if (sound.name().endsWith("_HURT"))
			return SoundType.HURT;
		else if (sound.name().endsWith("_STEP"))
			return SoundType.STEP;
		else if (sound.name().endsWith("_DEATH"))
			return SoundType.DEATH;
		return null;
	}
}