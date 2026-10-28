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
		String	s = sound.toString();
		if (s.endsWith("_AMBIENT"))
			return SoundType.AMBIENT;
		else if (s.endsWith("_HURT"))
			return SoundType.HURT;
		else if (s.endsWith("_STEP"))
			return SoundType.STEP;
		else if (s.endsWith("_DEATH"))
			return SoundType.DEATH;
		return null;
	}
}