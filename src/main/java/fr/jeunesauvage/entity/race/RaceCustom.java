package fr.jeunesauvage.entity.race;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.form.FormType;

public class RaceCustom {
	public static final NamespacedKey	KEY_RACE = new NamespacedKey(RpgCraft.name(), "race");
	public static final NamespacedKey	KEY_FORM = new NamespacedKey(RpgCraft.name(), "form");
	private final Player				player;
	private RaceType					raceType;
	private FormType					formType;

	public RaceCustom(Player player) {
		this.player = player;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		this.raceType = RaceType.fromString(Data.getString(pdc, KEY_RACE));
		String	formString = Data.getString(pdc, KEY_FORM);
		if (formString == null)
			this.formType = FormType.fromRaceType(raceType);
		else
			this.formType = FormType.fromString(formString);
	}

	/*
	** getter + setter
	*/

	public RaceType getRaceType() {
		return raceType;
	}

	public FormType getFormType() {
		return formType;
	}

	public void setFormType(FormType formType) {
		this.formType = formType;
		Data.setString(player.getPersistentDataContainer(), KEY_FORM, formType.getName());
	}

	public String getString() {
		return raceType.getName();
	}

	public void setRaceCustom(RaceType type) {
		if (type == null)
			type = RaceType.UNKNOWN;
		PersistentDataContainer	pdc = player.getPersistentDataContainer();
		Data.setString(pdc, KEY_RACE, type.getName());
		this.raceType = type;
		this.formType = FormType.fromRaceType(raceType);
	}
}
