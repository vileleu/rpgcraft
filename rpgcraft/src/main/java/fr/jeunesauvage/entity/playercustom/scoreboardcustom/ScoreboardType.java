package fr.jeunesauvage.entity.playercustom.scoreboardcustom;

public enum ScoreboardType {
	VOID_START("void_start", 6),
	HEALTH("health", 5),
	POWER("power", 4),
	CLASS("class", 3),
	RACE("race", 2),
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
