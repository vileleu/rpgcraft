package fr.jeunesauvage.itemcustom.spell;

import java.util.Set;

import org.bukkit.Material;

import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import fr.jeunesauvage.itemcustom.Rarity;
import net.kyori.adventure.text.Component;

public enum SpellType implements ItemCustomType {
	// warrior
    KNEE_BREAKER("knee_breaker", Material.PRISMARINE_SHARD, Set.of(ClassType.WARRIOR), null, 10, 5, 20, 0, 20, 0, false, 0f),
    WHIRLWIND("whirlwind", Material.NAUTILUS_SHELL, Set.of(ClassType.WARRIOR), null, 15, 5, 10, -1, 40, 0, false, 0f),
    LEAP("leap", Material.REDSTONE, Set.of(ClassType.WARRIOR), null, 20, 5, 30, 0, 30, 0, false, 0f),
	DEADLY_MAGNET("deadly_magnet", Material.HEAVY_CORE, Set.of(ClassType.WARRIOR), null, 25, 5, 20, 0, 50, 0, false, 0f),
	// pyromancer
	FIREBALL("fireball", Material.FIRE_CHARGE, Set.of(ClassType.PYROMANCER), null, 10, 5, 10, -1, 30, 0, true, 2f),
	TELEPORT("teleport", Material.ENDER_PEARL, Set.of(ClassType.PYROMANCER), null, 15, 5, 10, 0, 20, 0, false, 0f),
	MANA_THIRST("mana_thirst", Material.AMETHYST_SHARD, Set.of(ClassType.PYROMANCER), null, 20, 5, 30, 0, 0, 0, false, 0f),
	FLAME_NOVA("flame_nova", Material.BLAZE_POWDER, Set.of(ClassType.PYROMANCER), null, 25, 5, 25, -2, 60, 0, false, 0f),
	// rogue
	STEALTH("stealth", Material.GLOWSTONE_DUST, Set.of(ClassType.ROGUE), null, 10, 5, 10, 0, 0, 0, false, 0f),
	ESCAPE("escape", Material.GHAST_TEAR, Set.of(ClassType.ROGUE), null, 15, 5, 40, -1, 50, 0, false, 0f),
	SPRINT("sprint", Material.FEATHER, Set.of(ClassType.ROGUE), null, 20, 5, 30, -2, 50, 0, false, 0f),
	COLDBLOOD("coldblood", Material.SPIDER_EYE, Set.of(ClassType.ROGUE), null, 25, 5, 20, -2, 80, 0, false, 0f),
	// priest
	HOLY_BOMB("holy_bomb", Material.MAGMA_CREAM, Set.of(ClassType.PRIEST), null, 10, 5, 10, -1, 20, -1, false, 0f),
	HOLY_LAND("holy_land", Material.NETHER_STAR, Set.of(ClassType.PRIEST), null, 15, 5, 40, -2, 50, 0, true, 2f),
	HOLY_SHIELD("holy_shield", Material.HONEYCOMB, Set.of(ClassType.PRIEST), null, 20, 5, 30, 0, 30, 0, false, 0f),
	SHADOW_WORD("shadow_word", Material.WITHER_ROSE, Set.of(ClassType.PRIEST), null, 25, 5, 20, -1, 60, 0, true, 2f),
	// dracthyr
	DRAGON_BREATH("dragon_breath", Material.WIND_CHARGE, Set.of(ClassType.DRACTHYR), null, 10, 5, 20, -1, 40, -1, false, 0f),
	DRAGON_SKIN("dragon_skin", Material.LEATHER, Set.of(ClassType.DRACTHYR), null, 15, 5, 30, 0, 50, 0, true, 2f),
	METAMORPH("metamorph", Material.DRAGON_BREATH, Set.of(ClassType.DRACTHYR), null, 20, 5, 120, 0, 100, 0, true, 1f),
	STRIKE_BACK("strike_back", Material.HEART_OF_THE_SEA, Set.of(ClassType.DRACTHYR), null, 25, 5, 40, -2, 60, 0, false, 0f),
	// hunter
	EXPLOSIVE_SHOT("explosive_shot", Material.GUNPOWDER, Set.of(ClassType.HUNTER), null, 10, 5, 20, -1, 30, -1, false, 0f),
	PET("pet", Material.BONE, Set.of(ClassType.HUNTER), null, 15, 5, 30, 0, 100, 0, true, 1f),
	HUNT("hunt", Material.COMPASS, Set.of(ClassType.HUNTER), null, 20, 5, 60, 0, 20, 0, false, 0f),
	ICE_TRAP("ice_trap", Material.POWDER_SNOW_BUCKET, Set.of(ClassType.HUNTER), null, 25, 5, 42, -2, 50, 0, true, 2f);

	private final String			name;
	private final Material			material;
	private final Set<ClassType>	classTypes;
	private final Set<RaceType>		raceTypes;
	private final int				level;
	private final int				changeLevel;
	private final int				cooldown;
	private final int				changeCooldown;
	private final int				cost;
	private final int				changeCost;
	private final boolean			isCast;
	private final float				castTime;

	SpellType(String name, Material material, Set<ClassType> classTypes, Set<RaceType> raceTypes, int level, int CLevel, int cooldown, int Ccooldown, int cost, int Ccost, boolean isCast, float castTime) {
		this.name = name;
		this.material = material;
		this.classTypes = classTypes;
		this.raceTypes = raceTypes;
		this.level = level;
		this.changeLevel = CLevel;
		this.cooldown = cooldown;
		this.changeCooldown = Ccooldown;
		this.cost = cost;
		this.changeCost = Ccost;
		this.isCast = isCast;
		this.castTime = castTime;
	}

	@Override
	public String getName() {
		return name;
	}

    @Override
    public ItemCustomCategory getCategory() {
        return ItemCustomCategory.SPELL;
    }

    @Override
    public Component toComponent() {
        return Component.translatable("type.rpgcraft.spell");
    }

    @Override
	public Set<ClassType> getClassTypes() {
		return classTypes;
	}

	public Set<RaceType> getRaceTypes() {
		return raceTypes;
	}

    @Override
	public Material getMaterial() {
		return material;
	}

	public int getLevel(Rarity rarity) {
		return level + changeLevel * (rarity.getNumber() - 1);
	}

	public int getCooldown(Rarity rarity) {
		return cooldown + changeCooldown * (rarity.getNumber() - 1);
	}

	public int getCost(Rarity rarity) {
		return cost + changeCost * (rarity.getNumber() - 1);

	}

	public boolean isCast() {
		return isCast;
	}

	public float getCastTime() {
		return castTime;
	}
}
