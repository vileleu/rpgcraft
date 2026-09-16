package fr.jeunesauvage.sound;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;

public class SoundPacket extends PacketAdapter {
    private static final Map<FormType, Map<SoundType, List<String>>>    SOUNDS;
	private static final Map<Sound, Sound>	                            WOLF_TO_SPIDER = new HashMap<>();

    static {
        SOUNDS = Map.ofEntries(
            Map.entry(FormType.TARENTULA, Map.of(
                SoundType.AMBIENT, List.of("spider_boss_ambient1", "spider_boss_ambient2", "spider_boss_ambient3", "spider_boss_ambient4", "spider_boss_ambient5", "spider_boss_ambient6", "spider_boss_ambient7", "spider_boss_ambient8", "spider_boss_ambient9"),
                SoundType.HURT, List.of("spider_boss_hurt1", "spider_boss_hurt2", "spider_boss_hurt3", "spider_boss_hurt4", "spider_boss_hurt5", "spider_boss_hurt6", "spider_boss_hurt7"),
                SoundType.ATTACK, List.of("spider_boss_attack1", "spider_boss_attack2", "spider_boss_attack3", "spider_boss_attack4", "spider_boss_attack5", "spider_boss_attack6"),
                SoundType.STEP, List.of("spider_boss_step1", "spider_boss_step2", "spider_boss_step3"),
                SoundType.DEATH, List.of("spider_boss_death1", "spider_boss_death2", "spider_boss_death3")
            )),
            Map.entry(FormType.SCORPION, Map.of(
                SoundType.AMBIENT, List.of("scorpion_ambient1", "scorpion_ambient2", "scorpion_ambient3", "scorpion_ambient4", "scorpion_ambient5"),
                SoundType.HURT, List.of("scorpion_hurt1", "scorpion_hurt2", "scorpion_hurt3", "scorpion_hurt4", "scorpion_hurt5"),
                SoundType.ATTACK, List.of("scorpion_attack1", "scorpion_attack2", "scorpion_attack3", "scorpion_attack4", "scorpion_attack5"),
                SoundType.STEP, List.of("scorpion_step1", "scorpion_step2", "scorpion_step3", "scorpion_step4", "scorpion_step5", "scorpion_step6", "scorpion_step7", "scorpion_step8", "scorpion_step9", "scorpion_step10"),
                SoundType.DEATH, List.of("scorpion_death1", "scorpion_death2", "scorpion_death3", "scorpion_death4", "scorpion_death5", "scorpion_death6", "scorpion_death7")
            )),
            Map.entry(FormType.ELEMENTAL_FIRE, Map.of(
                SoundType.AMBIENT, List.of("elemental_fire_ambient1", "elemental_fire_ambient2", "elemental_fire_ambient3", "elemental_fire_ambient4", "elemental_fire_ambient5"),
                SoundType.HURT, List.of("elemental_fire_hurt1", "elemental_fire_hurt2", "elemental_fire_hurt3", "elemental_fire_hurt4", "elemental_fire_hurt5", "elemental_fire_hurt6", "elemental_fire_hurt7", "elemental_fire_hurt8", "elemental_fire_hurt9", "elemental_fire_hurt10"),
                SoundType.ATTACK, List.of("elemental_fire_attack1", "elemental_fire_attack2", "elemental_fire_attack3", "elemental_fire_attack4", "elemental_fire_attack5", "elemental_fire_attack6", "elemental_fire_attack7", "elemental_fire_attack8", "elemental_fire_attack9", "elemental_fire_attack10"),
                SoundType.STEP, List.of("elemental_fire_step1"),
                SoundType.DEATH, List.of("elemental_fire_death1")
            )),
            Map.entry(FormType.ELEMENTAL_WIND, Map.of(
                SoundType.AMBIENT, List.of("elemental_wind_ambient1", "elemental_wind_ambient2", "elemental_wind_ambient3", "elemental_wind_ambient4", "elemental_wind_ambient5", "elemental_wind_ambient6", "elemental_wind_ambient7", "elemental_wind_ambient8", "elemental_wind_ambient9", "elemental_wind_ambient10"),
                SoundType.HURT, List.of("elemental_wind_hurt1", "elemental_wind_hurt2", "elemental_wind_hurt3"),
                SoundType.ATTACK, List.of("elemental_wind_attack1", "elemental_wind_attack2", "elemental_wind_attack3"),
                SoundType.STEP, List.of("elemental_wind_step1", "elemental_wind_step2", "elemental_wind_step3", "elemental_wind_step4", "elemental_wind_step5", "elemental_wind_step6", "elemental_wind_step7", "elemental_wind_step8", "elemental_wind_step9", "elemental_wind_step10"),
                SoundType.DEATH, List.of("elemental_wind_death1")
            )),
            Map.entry(FormType.WHISPERER, Map.of(
                SoundType.AMBIENT, List.of("whisperer_attack1", "whisperer_attack2", "whisperer_attack3", "whisperer_attack4", "whisperer_attack5"),
                SoundType.HURT, List.of("whisperer_hurt1", "whisperer_hurt2", "whisperer_hurt3", "whisperer_hurt4", "whisperer_hurt5", "whisperer_hurt6", "whisperer_hurt7", "whisperer_hurt8", "whisperer_hurt9", "whisperer_hurt10"),
                SoundType.ATTACK, List.of("whisperer_attack1", "whisperer_attack2", "whisperer_attack3", "whisperer_attack4", "whisperer_attack5"),
                SoundType.STEP, List.of("whisperer_step1", "whisperer_step2", "whisperer_step3", "whisperer_step4", "whisperer_step5", "whisperer_step6", "whisperer_step7", "whisperer_step8", "whisperer_step9"),
                SoundType.DEATH, List.of("whisperer_death1", "whisperer_death2", "whisperer_death3", "whisperer_death4", "whisperer_death5")
            )),
            Map.entry(FormType.LEAPER, Map.of(
                SoundType.AMBIENT, List.of("leaper_ambient1", "leaper_ambient2", "leaper_ambient3", "leaper_ambient4", "leaper_ambient5"),
                SoundType.HURT, List.of("leaper_hurt1", "leaper_hurt2", "leaper_hurt3", "leaper_hurt4", "leaper_hurt5", "leaper_hurt6", "leaper_hurt7", "leaper_hurt8", "leaper_hurt9", "leaper_hurt10"),
                SoundType.ATTACK, List.of("leaper_attack1", "leaper_attack2", "leaper_attack3", "leaper_attack4", "leaper_attack5", "leaper_attack6", "leaper_attack7", "leaper_attack8"),
                SoundType.STEP, List.of("leaper_step1", "leaper_step2", "leaper_step3", "leaper_step4", "leaper_step5", "leaper_step6", "leaper_step7", "leaper_step8", "leaper_step9", "leaper_step10", "leaper_step11", "leaper_step12", "leaper_step13", "leaper_step14", "leaper_step15", "leaper_step16", "leaper_step17", "leaper_step18", "leaper_step19", "leaper_step20"),
                SoundType.DEATH, List.of("leaper_death1", "leaper_death2", "leaper_death3", "leaper_death4", "leaper_death5", "leaper_death6", "leaper_death7", "leaper_death8", "leaper_death9", "leaper_death10")
            )),
            Map.entry(FormType.FROZER, Map.of(
                SoundType.AMBIENT, List.of("whisperer_ambient1", "whisperer_ambient2", "whisperer_ambient3", "whisperer_ambient4", "whisperer_ambient5"),
                SoundType.HURT, List.of("whisperer_hurt1", "whisperer_hurt2", "whisperer_hurt3", "whisperer_hurt4", "whisperer_hurt5", "whisperer_hurt6", "whisperer_hurt7", "whisperer_hurt8", "whisperer_hurt9", "whisperer_hurt10"),
                SoundType.ATTACK, List.of("whisperer_attack1", "whisperer_attack2", "whisperer_attack3", "whisperer_attack4", "whisperer_attack5"),
                SoundType.STEP, List.of("whisperer_step1", "whisperer_step2", "whisperer_step3", "whisperer_step4", "whisperer_step5", "whisperer_step6", "whisperer_step7", "whisperer_step8", "whisperer_step9"),
                SoundType.DEATH, List.of("whisperer_death1", "whisperer_death2", "whisperer_death3", "whisperer_death4", "whisperer_death5")
            )),
            Map.entry(FormType.DEMON, Map.of(
                SoundType.AMBIENT, List.of("demon_ambient1", "demon_ambient2", "demon_ambient3", "demon_ambient4", "demon_ambient5", "demon_ambient6", "demon_ambient7", "demon_ambient8", "demon_ambient9"),
                SoundType.HURT, List.of("demon_hurt1", "demon_hurt2", "demon_hurt3", "demon_hurt4", "demon_hurt5", "demon_hurt6", "demon_hurt7", "demon_hurt8", "demon_hurt9", "demon_hurt10"),
                SoundType.ATTACK, List.of("demon_attack1", "demon_attack2", "demon_attack3", "demon_attack4", "demon_attack5"),
                SoundType.STEP, List.of("demon_step1", "demon_step2", "demon_step3", "demon_step4", "demon_step5", "demon_step6", "demon_step7", "demon_step8", "demon_step9", "demon_step10", "demon_step11", "demon_step12", "demon_step13", "demon_step14", "demon_step15", "demon_step16", "demon_step17", "demon_step18", "demon_step19", "demon_step20"),
                SoundType.DEATH, List.of("demon_death1", "demon_death2", "demon_death3", "demon_death4", "demon_death5", "demon_death6")
            )),
            Map.entry(FormType.ELEMENTAL_VOID, Map.of(
                SoundType.AMBIENT, List.of("elemental_void_ambient1", "elemental_void_ambient2", "elemental_void_ambient3", "elemental_void_ambient4", "elemental_void_ambient5", "elemental_void_ambient6", "elemental_void_ambient7", "elemental_void_ambient8", "elemental_void_ambient9", "elemental_void_ambient10", "elemental_void_ambient11", "elemental_void_ambient12"),
                SoundType.HURT, List.of("elemental_void_hurt1", "elemental_void_hurt2", "elemental_void_hurt3", "elemental_void_hurt4", "elemental_void_hurt5", "elemental_void_hurt6", "elemental_void_hurt7", "elemental_void_hurt8", "elemental_void_hurt9"),
                SoundType.ATTACK, List.of("elemental_void_attack1", "elemental_void_attack2", "elemental_void_attack3", "elemental_void_attack4", "elemental_void_attack5", "elemental_void_attack6", "elemental_void_attack7", "elemental_void_attack8", "elemental_void_attack9"),
                SoundType.STEP, List.of(),
                SoundType.DEATH, List.of("elemental_void_death1", "elemental_void_death2", "elemental_void_death3", "elemental_void_death4", "elemental_void_death5", "elemental_void_death6", "elemental_void_death7", "elemental_void_death8", "elemental_void_death9")
            )),
            Map.entry(FormType.ZOMBIE, Map.of(
                SoundType.AMBIENT, List.of("zombie_ambient1", "zombie_ambient2"),
                SoundType.HURT, List.of("zombie_hurt1", "zombie_hurt2", "zombie_hurt3", "zombie_hurt4"),
                SoundType.ATTACK, List.of("zombie_attack1", "zombie_attack2", "zombie_attack3", "zombie_attack4"),
                SoundType.STEP, List.of("zombie_step1", "zombie_step2", "zombie_step3", "zombie_step4"),
                SoundType.DEATH, List.of("zombie_death1")
            )));
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_AMBIENT, Sound.ENTITY_SPIDER_AMBIENT);
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_DEATH,   Sound.ENTITY_SPIDER_DEATH);
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_HURT,    Sound.ENTITY_SPIDER_HURT);
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_GROWL,   Sound.ENTITY_SPIDER_AMBIENT);
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_WHINE,   Sound.ENTITY_SPIDER_HURT);
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_SHAKE,   Sound.ENTITY_SPIDER_AMBIENT);
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_STEP,    Sound.ENTITY_SPIDER_STEP);
        WOLF_TO_SPIDER.put(Sound.ENTITY_WOLF_PANT,    Sound.ENTITY_SPIDER_AMBIENT);
    }

    public SoundPacket() {
        super(RpgCraft.instance(), ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT);
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        Sound sound = packet.getSoundEffects().read(0);
        if (sound == null) return;
        String	soundName = sound.name();
        if (soundName.startsWith("ENTITY_WOLF_")) {
    		handleWolf(e, sound);
		}
        else if (soundName.startsWith("ENTITY_BLAZE_")) {
    		handleBlaze(e, sound);
		}
        else if (soundName.startsWith("ENTITY_SPIDER_")) {
    		handleSpider(e, sound);
		}
        else if (soundName.startsWith("ENTITY_EVOKER_")) {
    		handleEvoker(e, sound);
		}
        else if (soundName.startsWith("ENTITY_RAVAGER_")) {
    		handleRavager(e, sound);
		}
        else if (soundName.startsWith("ENTITY_WITHER_SKELETON_")) {
    		handleWitherSkeleton(e, sound);
		}
        else if (soundName.startsWith("ENTITY_WITHER_")) {
    		handleWither(e, sound);
		}
        else if (soundName.startsWith("ENTITY_ZOMBIE_")) {
    		handleZombie(e, sound);
		}
    }

    // replace blaze sounds
	private void handleBlaze(PacketEvent e, Sound sound) {
        SoundType	    soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        PacketContainer	packet = e.getPacket();
        Player          player = e.getPlayer();
    	World			world = player.getWorld();
        Location	    loc = getLocation(packet, world);
        e.setCancelled(true);
        playSoundToPlayer(player, loc, soundType, FormType.ELEMENTAL_FIRE);
	}

    // replace spider sounds
	private void handleSpider(PacketEvent e, Sound sound) {
        SoundType	soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        PacketContainer				packet = e.getPacket();
        Player                      player = e.getPlayer();
    	World						world = e.getPlayer().getWorld();
    	Location					loc = getLocation(packet, world);
		Optional<LivingEntity>		closestSpider = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof Spider).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestSpider.isEmpty()) return;
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(closestSpider.get().getUniqueId());
        if (npcCustom == null) return;
        switch (npcCustom.getTemplateType()) {
            case TARENTULA -> {
                e.setCancelled(true);
                if (soundType == SoundType.STEP && ThreadLocalRandom.current().nextDouble() > 0.2) return;
                playSoundToPlayer(player, loc, soundType, FormType.TARENTULA);
            }
            case SCORPION -> {
                e.setCancelled(true);
                playSoundToPlayer(player, loc, soundType, FormType.SCORPION);
            }
            default -> {}
        }
	}

    // replace evoker sounds
	private void handleEvoker(PacketEvent e, Sound sound) {
        SoundType	    soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        PacketContainer	packet = e.getPacket();
        Player          player = e.getPlayer();
    	World			world = player.getWorld();
        Location	    loc = getLocation(packet, world);
		Optional<LivingEntity>		closestEvoker = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof Evoker).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestEvoker.isEmpty()) return;
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(closestEvoker.get().getUniqueId());
        if (npcCustom == null) return;
        switch (npcCustom.getTemplateType()) {
            case WHISPERER -> {
                e.setCancelled(true);
                if (soundType == SoundType.AMBIENT && ThreadLocalRandom.current().nextDouble() > 0.2) return;
                playSoundToPlayer(player, loc, soundType, FormType.WHISPERER);
            }
            case FROZER -> {
                e.setCancelled(true);
                if (soundType == SoundType.AMBIENT && ThreadLocalRandom.current().nextDouble() > 0.2) return;
                playSoundToPlayer(player, loc, soundType, FormType.FROZER);
            }
            default -> {}
        }
	}

    // replace ravager sounds
	private void handleRavager(PacketEvent e, Sound sound) {
        SoundType	    soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        PacketContainer	packet = e.getPacket();
        Player          player = e.getPlayer();
    	World			world = player.getWorld();
        Location	    loc = getLocation(packet, world);
		Optional<LivingEntity>		closestRavager = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof Ravager).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestRavager.isEmpty()) return;
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(closestRavager.get().getUniqueId());
        if (npcCustom == null || npcCustom.getTemplateType() != TemplateType.LEAPER) return;
        e.setCancelled(true);
        playSoundToPlayer(player, loc, soundType, FormType.LEAPER);
	}

    // replace wither skeleton sounds
	private void handleWitherSkeleton(PacketEvent e, Sound sound) {
        SoundType	    soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        PacketContainer	packet = e.getPacket();
        Player          player = e.getPlayer();
    	World			world = player.getWorld();
        Location	    loc = getLocation(packet, world);
		Optional<LivingEntity>		closestWitherSkeleton = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof WitherSkeleton).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestWitherSkeleton.isEmpty()) return;
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(closestWitherSkeleton.get().getUniqueId());
        if (npcCustom == null || npcCustom.getTemplateType() != TemplateType.DEMON) return;
        e.setCancelled(true);
        if (soundType == SoundType.AMBIENT && ThreadLocalRandom.current().nextDouble() > 0.2) return;
        playSoundToPlayer(player, loc, soundType, FormType.DEMON);
	}

    // replace wither sounds
	private void handleWither(PacketEvent e, Sound sound) {
        SoundType	    soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        PacketContainer	packet = e.getPacket();
        Player          player = e.getPlayer();
    	World			world = player.getWorld();
        Location	    loc = getLocation(packet, world);
		Optional<LivingEntity>		closestWither = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof Wither).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestWither.isEmpty()) return;
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(closestWither.get().getUniqueId());
        if (npcCustom == null || npcCustom.getTemplateType() != TemplateType.ELEMENTAL_VOID) return;
        e.setCancelled(true);
        if (soundType == SoundType.AMBIENT && ThreadLocalRandom.current().nextDouble() > 0.2) return;
        playSoundToPlayer(player, loc, soundType, FormType.ELEMENTAL_VOID);
	}

    // replace zombie sounds
	private void handleZombie(PacketEvent e, Sound sound) {
        SoundType	    soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        PacketContainer	packet = e.getPacket();
        Player          player = e.getPlayer();
    	World			world = player.getWorld();
        Location	    loc = getLocation(packet, world);
		Optional<LivingEntity>		closestZombie = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof Zombie).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestZombie.isEmpty()) return;
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(closestZombie.get().getUniqueId());
        if (npcCustom == null || npcCustom.getTemplateType() != TemplateType.ZOMBIE) return;
        e.setCancelled(true);
        if (soundType == SoundType.AMBIENT && ThreadLocalRandom.current().nextDouble() > 0.5) return;
        playSoundToPlayer(player, loc, soundType, FormType.ZOMBIE);
	}

    private void playSoundToPlayer(Player player, Location loc, SoundType soundType, FormType formType) {
        Map<SoundType, List<String>>    map = SOUNDS.get(formType);
        if (map == null) return;
        List<String> list = map.get(soundType);
        if (list == null || list.isEmpty()) return;
        player.playSound(loc, "sounds:" + list.get((new Random()).nextInt(list.size())), SoundCategory.HOSTILE, 1.5f, 1f);
    }

    // replace wolf sounds by spider sounds
	private void handleWolf(PacketEvent e, Sound sound) {
        PacketContainer				packet = e.getPacket();
    	World						world = e.getPlayer().getWorld();
    	Location					loc = getLocation(packet, world);
		Optional<LivingEntity>		closestWolf = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof Wolf).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestWolf.isEmpty()) return;
        NPCCustom   npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(closestWolf.get().getUniqueId());
        if (npcCustom == null || npcCustom.getTemplateType() != TemplateType.SMALL_SPIDER) return;
        Sound	replacement = WOLF_TO_SPIDER.get(sound);
        if (replacement == null) {
            e.setCancelled(true);
            return;
        }
        packet.getSoundEffects().write(0, replacement);
	}

    // get location of sound packet
    private Location getLocation(PacketContainer packet, World world) {
		StructureModifier<Integer>	ints = packet.getIntegers();
    	double						x = ints.read(0) / 8.0;
    	double						y = ints.read(1) / 8.0;
    	double						z = ints.read(2) / 8.0;
        return new Location(world, x, y, z);
    }

    public static void playSound(LivingEntityCustom livingEntityCustom, SoundType soundType) {
        World   world = livingEntityCustom.getWorld();
        if (world == null) return;
        FormType        formType = livingEntityCustom.getFormType();
        if (formType == null) return;
        Map<SoundType, List<String>>    map = SOUNDS.get(formType);
        if (map == null) return;
        List<String> list = map.get(soundType);
        if (list == null || list.size() < 1) return;
       world.playSound(livingEntityCustom.getLocation(), "sounds:" + list.get((new Random()).nextInt(list.size())), SoundCategory.HOSTILE, 1.5f, 1f);
    }
}