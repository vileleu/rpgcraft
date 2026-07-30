package fr.jeunesauvage.entity.bossbar;

import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.bossbar.BossBar;

public class BossBarData {
	private final BossBar		bossBar;
	private final BukkitTask	bukkitTask;

	public BossBarData(BossBar bossBar, BukkitTask bukkitTask) {
		this.bossBar = bossBar;
		this.bukkitTask = bukkitTask;
	}

	public BossBar getBossBar() {
		return bossBar;
	}

	public BukkitTask getBukkitTask() {
		return bukkitTask;
	}
}
