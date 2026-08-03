package fr.jeunesauvage.sound;

public enum QuoteType {
	GREETING("greeting"),
	FAREWELL("farewell"),
	ATTACK("attack"),
	DEATH("death");

	private final String	name;
	
	QuoteType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
