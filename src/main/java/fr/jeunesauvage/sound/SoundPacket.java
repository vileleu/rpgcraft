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
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import net.citizensnpcs.api.npc.NPC;

public class SoundPacket extends PacketAdapter {
    private final static Map<FormType, Map<SoundType, List<String>>>    SOUNDS;
	private static final Map<Sound, Sound>	                            WOLF_TO_SPIDER = new HashMap<>();

    static {
        SOUNDS = Map.ofEntries(
            Map.entry(FormType.SPIDER_BOSS, Map.of(
                SoundType.AMBIENT, List.of("spider_boss_ambient1", "spider_boss_ambient2", "spider_boss_ambient3", "spider_boss_ambient4", "spider_boss_ambient5", "spider_boss_ambient6", "spider_boss_ambient7", "spider_boss_ambient8", "spider_boss_ambient9"),
                SoundType.HURT, List.of("spider_boss_hurt1", "spider_boss_hurt2", "spider_boss_hurt3", "spider_boss_hurt4", "spider_boss_hurt5", "spider_boss_hurt6", "spider_boss_hurt7"),
                SoundType.ATTACK, List.of("spider_boss_attack1", "spider_boss_attack2", "spider_boss_attack3", "spider_boss_attack4", "spider_boss_attack5", "spider_boss_attack6"),
                SoundType.STEP, List.of("spider_boss_step1", "spider_boss_step2", "spider_boss_step3"),
                SoundType.DEATH, List.of("spider_boss_death1", "spider_boss_death2", "spider_boss_death3")
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

    public SoundPacket(JavaPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT);
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
    		wolfSpider(e, sound);
		}
        if (soundName.startsWith("ENTITY_SPIDER_")) {
    		bossSpider(e, sound);
		}
    }

    private void playSound(Location loc, SoundType soundType, FormType formType) {
        Map<SoundType, List<String>>    map = SOUNDS.get(formType);
        if (map == null) return;
        List<String> list = map.get(soundType);
        if (list == null || list.size() < 1) return;
        loc.getWorld().playSound(loc, "sounds:" + list.get((new Random()).nextInt(list.size())), SoundCategory.HOSTILE, 1.5f, 1f);
    }

    // replace spider sounds by boss spider sounds
	private void bossSpider(PacketEvent e, Sound sound) {
        PacketContainer				packet = e.getPacket();
    	World						world = e.getPlayer().getWorld();
    	Location					loc = getLocation(packet, world);
		Optional<LivingEntity>		closestSpider = world.getNearbyLivingEntities(loc, 1).stream()
			.filter(en -> en instanceof Spider).min(Comparator.comparingDouble(en-> en.getLocation().distanceSquared(loc)));
		if (closestSpider.isEmpty()) return;
		String	name = closestSpider.get().getName();
		if (name == null || !name.equals("Spider 5")) return;
        SoundType	soundType = SoundType.fromSound(sound);
        if (soundType == null) return;
        if (soundType == SoundType.STEP && ThreadLocalRandom.current().nextDouble() > 0.2) return;
        e.setCancelled(true);
        playSound(loc, soundType, FormType.SPIDER_BOSS);
	}

    // replace wolf sounds by spider sounds
	private void wolfSpider(PacketEvent e, Sound sound) {
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

    public static void playSound(NPC npc, SoundType soundType) {
        if (!(npc.getEntity() instanceof LivingEntity livingNPC)) return;
        FormType                        formType = npc.getOrAddTrait(TraitSentinel.class).getFormType();
        if (formType == null) return;
        Map<SoundType, List<String>>    map = SOUNDS.get(formType);
        if (map == null) return;
        List<String> list = map.get(soundType);
        if (list == null || list.size() < 1) return;
        livingNPC.getWorld().playSound(livingNPC.getLocation(), "sounds:" + list.get((new Random()).nextInt(list.size())), SoundCategory.HOSTILE, 1.5f, 1f);
    }
}