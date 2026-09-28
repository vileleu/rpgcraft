package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.goal;

import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait.FightTrait;
import fr.jeunesauvage.world.WorldManager;

import java.util.Random;

public class GoalPatrol implements Goal {
	private final NPCCustom	npcCustom;
	private final NPC npc;
	private final Location center;
	private final double radius;
	private final Random random = new Random();

	public GoalPatrol(NPC npc, double radius) {
		this.npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(npc.getUniqueId());
		this.npc = npc;
		Location	respawn = npc.getOrAddTrait(FightTrait.class).getRespawn();
		if (respawn != null)
			this.center = respawn.clone();
		else
			this.center = npc.getStoredLocation();
		this.radius = radius;
	}

	@Override
	public boolean shouldExecute(GoalSelector selector) {
		return npcCustom.isPresent() && !npc.getOrAddTrait(FightTrait.class).getFightAI().inChase();
	}

	@Override
	public void run(GoalSelector selector) {
		if (npc.getNavigator().isNavigating()) return;
		double angle = random.nextDouble() * 2 * Math.PI;
		double distance = random.nextDouble() * radius;
		double x = center.getX() + Math.cos(angle) * distance;
		double z = center.getZ() + Math.sin(angle) * distance;
		Location target = new Location(center.getWorld(), x, center.getY(), z);
		int	y = WorldManager.getHighestSolidBlockY(target, target.getBlockY()) + 1;
		if (y < target.getBlockY()) target.setY(y);
		FightTrait.setTarget(npcCustom, target);
	}

	@Override
	public void reset() {
		npc.getNavigator().cancelNavigation();
	}
}