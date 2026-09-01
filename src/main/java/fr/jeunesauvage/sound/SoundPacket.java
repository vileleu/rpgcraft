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
import org.bukkit.entity.Wolf;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.formcustom.FormType;

public class SoundPacket extends PacketAdapter {
    private static final Map<FormType, Map<SoundType, List<String>>>    SOUNDS;
	private static final Map<Sound, Sound>	                            WOLF_TO_SPIDER = new HashMap<>();

    static {
        SOUNDS = Map.ofEntries(
            Map.entry(FormType.SPIDER_BOSS, Map.of(
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
                SoundType.AMBIENT, List.of("whisperer_ambient1", "whisperer_ambient2", "whisperer_ambient3", "whisperer_ambient4", "whisperer_ambient5"),
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
		String	name = closestSpider.get().getName();
		if (name == null) return;
        if (name.equals("Spider 5")) {
            e.setCancelled(true);
            if (soundType == SoundType.STEP && ThreadLocalRandom.current().nextDouble() > 0.2) return;
            playSoundToPlayer(player, loc, soundType, FormType.SPIDER_BOSS);
        }
        else if (name.equals("Spider 7")) {
            e.setCancelled(true);
            // if (soundType == SoundType.STEP && ThreadLocalRandom.current().nextDouble() > 0.2) return;
            playSoundToPlayer(player, loc, soundType, FormType.SCORPION);
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
		String	name = closestEvoker.get().getName();
		if (name == null || !name.equals("Whisperer")) return;
        e.setCancelled(true);
        if (soundType == SoundType.AMBIENT && ThreadLocalRandom.current().nextDouble() > 0.2) return;
        playSoundToPlayer(player, loc, soundType, FormType.WHISPERER);
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
		String	name = closestRavager.get().getName();
		if (name == null || !name.equals("Ravager 2")) return;
        e.setCancelled(true);
        playSoundToPlayer(player, loc, soundType, FormType.LEAPER);
	}

    private void playSoundToPlayer(Player player, Location loc, SoundType soundType, FormType formType) {
        Map<SoundType, List<String>>    map = SOUNDS.get(formType);
        if (map == null) return;
        List<String> list = map.get(soundType);
        if (list == null || list.size() < 1) return;
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
		String	name = closestWolf.get().getName();
		if (name == null || !name.equals("Wolf 3")) return;
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

    public static void playSound(NPCCustom npcCustom, SoundType soundType) {
        World   world = npcCustom.getWorld();
        if (world == null) return;
        FormType        formType = npcCustom.getFormType();
        if (formType == null) return;
        Map<SoundType, List<String>>    map = SOUNDS.get(formType);
        if (map == null) return;
        List<String> list = map.get(soundType);
        if (list == null || list.size() < 1) return;
       world.playSound(npcCustom.getLocation(), "sounds:" + list.get((new Random()).nextInt(list.size())), SoundCategory.HOSTILE, 1.5f, 1f);
    }
}