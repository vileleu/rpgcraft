package fr.jeunesauvage.entity.bossbar;

import org.bukkit.entity.LivingEntity;

public class TargetData {
	private final BossBarData	bossBarData;
	private final LivingEntity	target;

	public TargetData(BossBarData bossBarData, LivingEntity livingEntity) {
		this.bossBarData = bossBarData;
		this.target = livingEntity;
	}

	public BossBarData getBossBarData() {
		return bossBarData;
	}

	public LivingEntity getTarget() {
		return target;
	}
}
