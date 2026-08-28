package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.bossbar;

import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;

public class TargetData {
	private BossBarData			bossBarData;
	private PlayerCustom		launcher;
	private LivingEntityCustom	target;

	public TargetData(PlayerCustom launcher) {
		this.bossBarData = null;
		this.launcher = launcher;
		this.target = null;
	}

	public BossBarData getBossBarData() {
		return bossBarData;
	}

	public LivingEntityCustom getTarget() {
		return target;
	}

	public void active(BossBarData bossBarData, LivingEntityCustom target) {
		this.bossBarData = bossBarData;
		this.target = target;
	}

	public void deactive() {
		if (!isActive()) return;
		launcher.hideBossBar(bossBarData.getBossBar());
		bossBarData.getBukkitTask().cancel();
		bossBarData = null;
		target = null;
	}

	private boolean isActive() {
		return (bossBarData != null && target != null);
	}
}
