package fr.jeunesauvage.itemcustom.spell;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.bossbar.BossBarData;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.powercustom.PowerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.usable.Usable;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class Spell extends ItemCustom<SpellType> implements Usable {
	private final Map<UUID, BossBarData>	bossBars = new HashMap<>();

	public Spell(SpellType type, String name, Rarity rarity, int level) {
		super(type, name, rarity, level);
		buildSpell();
	}

	private void buildSpell() {
		ItemMeta	meta = item.getItemMeta();
		meta.displayName(Lore.nameSpell(name, rarity));
        Data.setString(meta.getPersistentDataContainer(), KEY_IDENTIFIER, name);
		// write lore
		List<Component>	lore = new ArrayList<>();
		lore.add(Lore.type(type));
		lore.add(Lore.rarity(rarity));
		lore.add(Lore.level(level));
		if (type.getRaceTypes() != null)
			lore.add(Lore.raceType(type.getRaceTypes()));
		if (type.getClassTypes() != null)
			lore.add(Lore.classType(type.getClassTypes()));
		lore.add(Lore.cost(type.getCost(rarity)));
		lore.add(Lore.description(name));
		meta.lore(lore);
        item.setItemMeta(meta);
	}

	// override

	@Override
	public ItemCustomCategory getCategory() {
		return type.getCategory();
	}

	@Override
	public Material	getMaterial() {
		return type.getMaterial();
	}

	@Override
	public Component toComponent() {
        return Component.translatable("spell.rpgcraft." + name);
	}

	@Override
	public boolean canUse(PlayerCustom playerCustom, EquipmentSlot slot) {
		SpellRegistry	spellRegistry = RpgCraft.getSpellRegistry();
		if (type == SpellType.STEALTH && spellRegistry.hasStealth(playerCustom)) return true;
		else if (type == SpellType.PET && spellRegistry.hasPet(playerCustom)) return true;
		// check race
		RaceType		playerRace = playerCustom.getRaceType();
		Set<RaceType>	raceTypes = type.getRaceTypes();
		if (raceTypes != null && !raceTypes.contains(playerRace)) {
			playerCustom.sendActionBar(Message.cantUse());
			return false;
		}
		// check class
		ClassType		playerClass = playerCustom.getClassType();
		if (playerClass == ClassType.GOD) {
			if (type.isCast()) {
				startCast(playerCustom, slot);
				return false;
			}
			return true;
		}
		Set<ClassType>	classTypes = type.getClassTypes();
		if (classTypes != null && !classTypes.contains(playerClass)) {
			playerCustom.sendActionBar(Message.cantUse());
			return false;
		}
		// silence
		int	duration = playerCustom.isSilence();
		if (duration > 0) {
			playerCustom.sendActionBar(Message.silence(duration));
			return false;
		}
		// cooldown
		duration = playerCustom.hasCooldown(type.getMaterial());
		if (duration > 0) {
			playerCustom.sendActionBar(Message.cooldown(duration));
			return false;
		}
		// check power
		PowerCustom	power = playerCustom.getPowerCustom();
		if (power == null) {
			playerCustom.sendActionBar(Message.cantUse());
			return false;
		}
		int	cost = type.getCost(rarity);
		if (power.getValue() < cost) {
			playerCustom.sendActionBar(Message.notEnough(power.getType()));
			return false;
		}
		if (type.isCast()) {
			startCast(playerCustom, slot);
			return false;
		}
		// can use
		playerCustom.addCooldown(type.getMaterial(), type.getCooldown(rarity));
		power.decrease(cost);
		return true;
	}

	@Override
	public void use(PlayerCustom playerCustom, EquipmentSlot slot) {
		SpellRegistry	spellRegistry = RpgCraft.getSpellRegistry();
		switch (type) {
			// warrior
			case SpellType.KNEE_BREAKER -> spellRegistry.kneeBreaker(playerCustom, rarity);
			case SpellType.WHIRLWIND -> spellRegistry.whirlwind(playerCustom, rarity);
			case SpellType.LEAP -> spellRegistry.leap(playerCustom, null, rarity);
			case SpellType.DEADLY_MAGNET -> spellRegistry.deadlyMagnet(playerCustom, rarity);
			// pyromancer
			case SpellType.FIREBALL -> spellRegistry.fireBall(playerCustom, null, rarity);
			case SpellType.TELEPORT -> spellRegistry.teleport(playerCustom, rarity);
			case SpellType.MANA_THIRST -> spellRegistry.manaThirst(playerCustom, rarity);
			case SpellType.FLAME_NOVA -> spellRegistry.flameNova(playerCustom, rarity);
			// rogue
			case SpellType.STEALTH -> spellRegistry.stealth(playerCustom, rarity);
			case SpellType.ESCAPE -> spellRegistry.escape(playerCustom, rarity);
			case SpellType.SPRINT -> spellRegistry.sprint(playerCustom, rarity);
			case SpellType.COLDBLOOD -> spellRegistry.coldBlood(playerCustom, rarity);
			// priest
			case SpellType.HOLY_BOMB -> spellRegistry.holyBomb(playerCustom, rarity);
			case SpellType.HOLY_LAND -> spellRegistry.holyLand(playerCustom, rarity);
			case SpellType.HOLY_SHIELD -> spellRegistry.holyShield(playerCustom, rarity);
			case SpellType.SHADOW_WORD -> spellRegistry.shadowWord(playerCustom, null, rarity);
			// dracthyr
			case SpellType.DRAGON_BREATH -> spellRegistry.dragonBreath(playerCustom, null, rarity);
			case SpellType.DRAGON_SKIN -> spellRegistry.dragonSkin(playerCustom, rarity);
			case SpellType.METAMORPH -> spellRegistry.metamorph(playerCustom, rarity);
			case SpellType.STRIKE_BACK -> spellRegistry.strikeBack(playerCustom, rarity);
			// hunter
			case SpellType.EXPLOSIVE_SHOT -> spellRegistry.explosiveShot(playerCustom, rarity);
			case SpellType.PET -> spellRegistry.pet(playerCustom, rarity);
			case SpellType.HUNT -> spellRegistry.hunt(playerCustom, rarity);
			case SpellType.ICE_TRAP -> spellRegistry.iceTrap(playerCustom, rarity);
			default -> {}
		}
	}

	private void startCast(PlayerCustom playerCustom, EquipmentSlot slot) {
		cleanBossBar(playerCustom);
		Component	component = Message.c(Component.translatable("spell.rpgcraft.casting"));
		BossBar		bar = BossBar.bossBar(
		        component,
		        0f,
		        BossBar.Color.BLUE,
		        BossBar.Overlay.PROGRESS
		);
		playerCustom.showBossBar(bar);
		BossBarData	bossBarData = new BossBarData(bar, new BukkitRunnable() {
			Location	now = null;
			Location	last = playerCustom.getLocation();
			float		max = type.getCastTime() - (type.getCastTime() * (float)StatSecondary.CAST_SPEED.getAmount(playerCustom));
		    int			ticks = 0;
		    @Override
		    public void run() {
				if (max <= 0) {
					cleanBossBar(playerCustom);
					if (!castCanUse(playerCustom)) return;
					use(playerCustom, slot);
					return;
				}
				now = playerCustom.getLocation();
				if (ticks >= 5 && (now.getX() != last.getX() || now.getY() != last.getY() || now.getZ() != last.getZ())) {
					cleanBossBar(playerCustom);
					return;
				}
				float	start = ticks / 10f;
				float	progress = Math.min(1f, start / max);
				bar.progress(progress);
				last = now;
				ticks++;
				if (progress >= 1) {
					cleanBossBar(playerCustom);
					if (!castCanUse(playerCustom)) return;
					use(playerCustom, slot);
				}
		    }
		
		}.runTaskTimer(RpgCraft.instance(), 0L, 2L));
		bossBars.put(playerCustom.getUUID(), bossBarData);
	}

	private boolean castCanUse(PlayerCustom playerCustom) {
		// check race
		RaceType		playerRace = playerCustom.getRaceType();
		Set<RaceType>	raceTypes = type.getRaceTypes();
		if (raceTypes != null && !raceTypes.contains(playerRace)) {
			playerCustom.sendActionBar(Message.cantUse());
			return false;
		}
		// check class
		ClassType		playerClass = playerCustom.getClassType();
		if (playerClass == ClassType.GOD) return true;
		Set<ClassType>	classTypes = type.getClassTypes();
		if (classTypes != null && !classTypes.contains(playerClass)) {
			playerCustom.sendActionBar(Message.cantUse());
			return false;
		}
		// cooldown
		int	duration = playerCustom.hasCooldown(type.getMaterial());
		if (duration > 0) {
			playerCustom.sendActionBar(Message.cooldown(duration));
			return false;
		}
		// check power
		PowerCustom	power = playerCustom.getPowerCustom();
		if (power == null) {
			playerCustom.sendActionBar(Message.cantUse());
			return false;
		}
		int	cost = type.getCost(rarity);
		if (power.getValue() < cost) {
			playerCustom.sendActionBar(Message.notEnough(power.getType()));
			return false;
		}
		// can use
		playerCustom.addCooldown(type.getMaterial(), type.getCooldown(rarity));
		power.decrease(cost);
		return true;
	}

	private void cleanBossBar(PlayerCustom playerCustom) {
		UUID		uuid = playerCustom.getUUID();
		BossBarData	bossBarData = bossBars.get(uuid);
		if (bossBarData == null) return;
		playerCustom.hideBossBar(bossBarData.getBossBar());
		bossBarData.getBukkitTask().cancel();
		bossBars.remove(uuid);
	}
}