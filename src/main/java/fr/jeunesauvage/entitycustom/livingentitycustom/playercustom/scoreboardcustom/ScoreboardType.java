package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.scoreboardcustom;

public enum ScoreboardType {
	VOID_START("void_start", 8),
	HEALTH("health", 7),
	POWER("power", 6),
	RACE("race", 5),
	CLASS("class", 4),
	ALLY("ally", 3),
	ALLY_LOCATION("ally_location", 2),
	VOID_END("void_end", 1);

	private final String	name;
	private final int		line;

	ScoreboardType(String name, int line) {
		this.name = name;
		this.line = line;
	}

	public String getName() {
		return this.name;
	}

	public int getLine() {
		return this.line;
	}
}
