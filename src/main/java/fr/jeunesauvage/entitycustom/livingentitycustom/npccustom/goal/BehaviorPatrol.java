package fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.goal;

import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.trait.FightTrait;

import java.util.Random;

public class BehaviorPatrol implements Behavior {
	private final NPCCustom	npcCustom;
	private final NPC 		npc;
	private final Location	center;
	private final double 	radius;
	private final Random	random = new Random();
	private Location		target = null;

	public BehaviorPatrol(NPC npc, double radius) {
		this.npc = npc;
		this.npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(npc.getUniqueId());
		Location	respawn = npc.getOrAddTrait(FightTrait.class).getRespawn();
		this.center = respawn != null ? respawn.clone() : npc.getStoredLocation();
		this.radius = radius;
	}

	@Override
	public boolean shouldExecute() {
		return npcCustom.isPresent() && !npc.getOrAddTrait(FightTrait.class).getFightAI().inChase();
	}

	@Override
	public BehaviorStatus run() {
		if (npc.getNavigator().isNavigating()) return BehaviorStatus.RUNNING;
		if (target == null) {
			double angle = random.nextDouble() * 2 * Math.PI;
			double distance = random.nextDouble() * radius;
			double x = center.getX() + Math.cos(angle) * distance;
			double z = center.getZ() + Math.sin(angle) * distance;
			target = new Location(center.getWorld(), x, center.getY(), z);
		}
		if (!(npc.getEntity() instanceof LivingEntity l)) return BehaviorStatus.FAILURE;
		if (l.getLocation().distanceSquared(target) <= 4) return BehaviorStatus.SUCCESS;
		npc.getNavigator().setTarget(target);
		return BehaviorStatus.RUNNING;
	}

	@Override
	public void reset() {
		target = null;
		npc.getNavigator().cancelNavigation();
	}
}