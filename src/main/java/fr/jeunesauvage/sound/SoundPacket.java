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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
        PacketContainer				packet = e.getPacket();
        StructureModifier<Sound>	sounds = packet.getSoundEffects();
        if (sounds.size() == 0) return;
        Sound	sound = sounds.read(0);
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
    }

    // replace blaze sounds
	private void handleBlaze(PacketEvent e, Sound sound) {
        PacketContainer	packet = e.getPacket();
        Player          player = e.getPlayer();
    	World			world = player.getWorld();
        Location	    loc = getLocation(packet, world);
        SoundType	    soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        e.setCancelled(true);
        if (soundType == SoundType.STEP && ThreadLocalRandom.current().nextDouble() > 0.4) return;
        player.playSound(null);
        playSoundToPlayer(player, loc, soundType, FormType.ELEMENTAL_FIRE);
	}

    // replace spider sounds
	private void handleSpider(PacketEvent e, Sound sound) {
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
            SoundType	soundType = SoundType.fromSound(sound);
            if (soundType == null) return;
            e.setCancelled(true);
            if (soundType == SoundType.STEP && ThreadLocalRandom.current().nextDouble() > 0.2) return;
            playSoundToPlayer(player, loc, soundType, FormType.SPIDER_BOSS);
        }
        else if (name.equals("Spider 7")) {
            SoundType	soundType = SoundType.fromSound(sound);
            if (soundType == null) return;
            e.setCancelled(true);
            // if (soundType == SoundType.STEP && ThreadLocalRandom.current().nextDouble() > 0.2) return;
            playSoundToPlayer(player, loc, soundType, FormType.SCORPION);
        }
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