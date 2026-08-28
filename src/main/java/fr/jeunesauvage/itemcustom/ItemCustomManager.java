package fr.jeunesauvage.itemcustom;

import org.bukkit.event.Listener;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.itemcustom.consumable.ConsumableManager;
import fr.jeunesauvage.itemcustom.equipable.EquipableManager;
import fr.jeunesauvage.itemcustom.usable.UsableManager;

public class ItemCustomManager implements Listener {
	public ItemCustomManager() {
		RpgCraft	rpgCraft = RpgCraft.instance();
		// command
        ItemCustomCommand   itemCustomCommand = new ItemCustomCommand();
        rpgCraft.getCommand("giveequipable").setExecutor(itemCustomCommand);
        rpgCraft.getCommand("givespell").setExecutor(itemCustomCommand);
        rpgCraft.getCommand("givepotion").setExecutor(itemCustomCommand);
		// EquipableManager listener
		EquipableManager	equipableManager = new EquipableManager();
		rpgCraft.getServer().getPluginManager().registerEvents(equipableManager, rpgCraft);
		// UsableManager listener
		UsableManager   usablebleManager = new UsableManager();
		rpgCraft.getServer().getPluginManager().registerEvents(usablebleManager, rpgCraft);
		// ConsumableManager listener
		ConsumableManager   consumablebleManager = new ConsumableManager();
		rpgCraft.getServer().getPluginManager().registerEvents(consumablebleManager, rpgCraft);
	}
}
