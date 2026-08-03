package fr.jeunesauvage.combat.combatant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.jeunesauvage.entity.playercustom.PlayerCustomManager;
import net.citizensnpcs.api.CitizensAPI;

public class Combatant<T> {
    private final T             combatant;
    private final LivingEntity	livingEntity;
    private final CombatantType type;

    private Combatant(T combatant, LivingEntity livingEntity, CombatantType type) {
        this.combatant = combatant;
		this.livingEntity = livingEntity;
        this.type = type;
    }

	public <C> C getEntityAs(Class<C> cast) {
	    if (cast.isInstance(combatant))
	        return cast.cast(combatant);
	    return null;
	}

    public T getCombatant() {
		return combatant;
	}

	public LivingEntity getLivingEntity() {
		return livingEntity;
	}

    public CombatantType getType() {
		return type;

	}

    public static Combatant<?> build(LivingEntity living) {
		if (living == null) return null;
        CombatantType type = CombatantType.fromLivingEntity(living);
		if (type == null) return null;
        return switch (type) {
            case PLAYER -> new Combatant<>(PlayerCustomManager.getPlayerCustom((Player)living), living, type);
            case NPC -> new Combatant<>(CitizensAPI.getNPCRegistry().getNPC(living), living, type);
            case LIVING_ENTITY -> new Combatant<>(living, living, type);
        };
    }
}
