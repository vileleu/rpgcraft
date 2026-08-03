package fr.jeunesauvage.entity.npc.goal;

import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

import fr.jeunesauvage.entity.npc.trait.TraitSentinel;

import java.util.Random;

public class GoalPatrol implements Goal {
	private final NPC npc;
	private final Location center;
	private final double radius;
	private final Random random = new Random();

	public GoalPatrol(NPC npc, double radius) {
		this.npc = npc;
		Location	respawn = npc.getOrAddTrait(TraitSentinel.class).getRespawn();
		if (respawn != null)
			this.center = respawn.clone();
		else
			this.center = npc.getStoredLocation();
		this.radius = radius;
	}

	@Override
	public boolean shouldExecute(GoalSelector selector) {
		if (!npc.isSpawned()) return false;
		return !npc.getOrAddTrait(TraitSentinel.class).getTargetHelper().inChase();
	}

	@Override
	public void run(GoalSelector selector) {
		if (npc.getNavigator().isNavigating()) return;
		double angle = random.nextDouble() * 2 * Math.PI;
		double distance = random.nextDouble() * radius;
		double x = center.getX() + Math.cos(angle) * distance;
		double z = center.getZ() + Math.sin(angle) * distance;
		Location target = new Location(center.getWorld(), x, center.getY(), z);
		npc.getNavigator().setTarget(target);
	}

	@Override
	public void reset() {
		npc.getNavigator().cancelNavigation();
	}
}