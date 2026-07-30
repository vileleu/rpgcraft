package fr.jeunesauvage.sound;

import java.util.Map;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.entity.form.FormType;
import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import net.citizensnpcs.api.npc.NPC;

public class SoundManager implements Listener {
    private final static Map<FormType, Map<QuoteType, List<String>>>    QUOTES;

    static {
        QUOTES = Map.ofEntries(
            Map.entry(FormType.DWARF, Map.of(
                QuoteType.GREETING, List.of("dwarf_greeting1", "dwarf_greeting2", "dwarf_greeting3", "dwarf_greeting4", "dwarf_greeting5", "dwarf_greeting6"),
                QuoteType.FAREWELL, List.of("dwarf_farewell1", "dwarf_farewell2", "dwarf_farewell3", "dwarf_farewell4"),
                QuoteType.ATTACK, List.of("dwarf_attack1", "dwarf_attack2", "dwarf_attack3", "dwarf_attack4"),
                QuoteType.DEATH, List.of("dwarf_death1", "dwarf_death2", "dwarf_death3", "dwarf_death4", "dwarf_death5")
            )),
            Map.entry(FormType.DWARFIRON, Map.of(
                QuoteType.GREETING, List.of("dwarfiron_greeting1", "dwarfiron_greeting2", "dwarfiron_greeting3", "dwarfiron_greeting4", "dwarfiron_greeting5", "dwarfiron_greeting6"),
                QuoteType.FAREWELL, List.of("dwarfiron_farewell1", "dwarfiron_farewell2", "dwarfiron_farewell3", "dwarfiron_farewell4", "dwarfiron_farewell5", "dwarfiron_farewell6", "dwarfiron_farewell7"),
                QuoteType.ATTACK, List.of("dwarfiron_attack1", "dwarfiron_attack2", "dwarfiron_attack3", "dwarfiron_attack4", "dwarfiron_attack5", "dwarfiron_attack6", "dwarfiron_attack7", "dwarfiron_attack8", "dwarfiron_attack9"),
                QuoteType.DEATH, List.of("dwarfiron_death1", "dwarfiron_death2", "dwarfiron_death3", "dwarfiron_death4", "dwarfiron_death5")
            )),
            Map.entry(FormType.DWARFIRON_GUARD, Map.of(
                QuoteType.GREETING, List.of("dwarfiron_guard_greeting1", "dwarfiron_guard_greeting2", "dwarfiron_guard_greeting3", "dwarfiron_guard_greeting4", "dwarfiron_guard_greeting5", "dwarfiron_guard_greeting6"),
                QuoteType.FAREWELL, List.of("dwarfiron_guard_greeting1", "dwarfiron_guard_greeting2", "dwarfiron_guard_greeting3", "dwarfiron_guard_greeting4", "dwarfiron_guard_greeting5", "dwarfiron_guard_greeting6"),
                QuoteType.ATTACK, List.of("dwarfiron_guard_attack1", "dwarfiron_guard_attack2", "dwarfiron_guard_attack3", "dwarfiron_guard_attack4", "dwarfiron_guard_attack5", "dwarfiron_guard_attack6"),
                QuoteType.DEATH, List.of("dwarfiron_guard_death1", "dwarfiron_guard_death2", "dwarfiron_guard_death3")
            )),
            Map.entry(FormType.HUMAN, Map.of(
                QuoteType.GREETING, List.of("human_greeting1", "human_greeting2", "human_greeting3", "human_greeting4", "human_greeting5"),
                QuoteType.FAREWELL, List.of("human_farewell1", "human_farewell2", "human_farewell3", "human_farewell4", "human_farewell5"),
                QuoteType.ATTACK, List.of("human_attack1", "human_attack2", "human_attack3", "human_attack4", "human_attack5", "human_attack6"),
                QuoteType.DEATH, List.of("human_death1", "human_death2", "human_death3", "human_death4", "human_death5", "human_death6")
            )),
            Map.entry(FormType.TAUREN, Map.of(
                QuoteType.GREETING, List.of("tauren_greeting1", "tauren_greeting2", "tauren_greeting3", "tauren_greeting4", "tauren_greeting5", "tauren_greeting6"),
                QuoteType.FAREWELL, List.of("tauren_farewell1", "tauren_farewell2", "tauren_farewell3", "tauren_farewell4", "tauren_farewell5"),
                QuoteType.ATTACK, List.of("tauren_attack1", "tauren_attack2", "tauren_attack3", "tauren_attack4"),
                QuoteType.DEATH, List.of("tauren_death1", "tauren_death2", "tauren_death3", "tauren_death4", "tauren_death5")
            )),
            Map.entry(FormType.ORC, Map.of(
                QuoteType.GREETING, List.of("orc_greeting1", "orc_greeting2", "orc_greeting3", "orc_greeting4", "orc_greeting5", "orc_greeting6"),
                QuoteType.FAREWELL, List.of("orc_farewell1", "orc_farewell2", "orc_farewell3", "orc_farewell4", "orc_farewell5"),
                QuoteType.ATTACK, List.of("orc_attack1", "orc_attack2", "orc_attack3", "orc_attack4", "orc_attack5"),
                QuoteType.DEATH, List.of("orc_death1", "orc_death2", "orc_death3", "orc_death4", "orc_death5", "orc_death6")
            )),
            Map.entry(FormType.ORC_FEMALE, Map.of(
                QuoteType.GREETING, List.of("orc_female_greeting1", "orc_female_greeting2", "orc_female_greeting3", "orc_female_greeting4"),
                QuoteType.FAREWELL, List.of("orc_female_farewell1", "orc_female_farewell2", "orc_female_farewell3", "orc_female_farewell4", "orc_female_farewell5"),
                QuoteType.ATTACK, List.of("orc_female_attack1", "orc_female_attack2", "orc_female_attack3", "orc_female_attack4", "orc_female_attack5", "orc_female_attack6"),
                QuoteType.DEATH, List.of("orc_female_death1", "orc_female_death2", "orc_female_death3", "orc_female_death4", "orc_female_death5", "orc_female_death6", "orc_female_death7")
            )),
            Map.entry(FormType.ELFNIGHT, Map.of(
                QuoteType.GREETING, List.of("elfnight_greeting1", "elfnight_greeting2", "elfnight_greeting3", "elfnight_greeting4", "elfnight_greeting5", "elfnight_greeting6", "elfnight_greeting7", "elfnight_greeting8"),
                QuoteType.FAREWELL, List.of("elfnight_farewell1", "elfnight_farewell2", "elfnight_farewell3", "elfnight_farewell4", "elfnight_farewell5", "elfnight_farewell6"),
                QuoteType.ATTACK, List.of("elfnight_attack1", "elfnight_attack2", "elfnight_attack3"),
                QuoteType.DEATH, List.of("elfnight_death1", "elfnight_death2", "elfnight_death3", "elfnight_death4", "elfnight_death5", "elfnight_death6", "elfnight_death7")
            )),
            Map.entry(FormType.ELFBLOOD, Map.of(
                QuoteType.GREETING, List.of("elfblood_greeting1", "elfblood_greeting2", "elfblood_greeting3", "elfblood_greeting4", "elfblood_greeting5", "elfblood_greeting6"),
                QuoteType.FAREWELL, List.of("elfblood_farewell1", "elfblood_farewell2", "elfblood_farewell3", "elfblood_farewell4", "elfblood_farewell5", "elfblood_farewell6"),
                QuoteType.ATTACK, List.of("elfblood_attack1", "elfblood_attack2", "elfblood_attack3", "elfblood_attack4", "elfblood_attack5"),
                QuoteType.DEATH, List.of("elfblood_death1", "elfblood_death2", "elfblood_death3", "elfblood_death4", "elfblood_death5", "elfblood_death6", "elfblood_death7", "elfblood_death8")
            )),
            Map.entry(FormType.MURLOC, Map.of(
                QuoteType.GREETING, List.of("murloc_greeting1", "murloc_greeting2", "murloc_greeting3"),
                QuoteType.FAREWELL, List.of("murloc_farewell1", "murloc_farewell2", "murloc_farewell3"),
                QuoteType.ATTACK, List.of("murloc_attack1", "murloc_attack2", "murloc_attack3", "murloc_attack4"),
                QuoteType.DEATH, List.of("murloc_death1")
            )),
            Map.entry(FormType.MURLOC_ELITE, Map.of(
                QuoteType.GREETING, List.of("murloc_greeting1", "murloc_greeting2", "murloc_greeting3"),
                QuoteType.FAREWELL, List.of("murloc_farewell1", "murloc_farewell2", "murloc_farewell3"),
                QuoteType.ATTACK, List.of("murloc_attack1", "murloc_attack2", "murloc_attack3", "murloc_attack4"),
                QuoteType.DEATH, List.of("murloc_death1")
            )),
            Map.entry(FormType.MURLOC_BOSS, Map.of(
                QuoteType.GREETING, List.of("murloc_greeting1", "murloc_greeting2", "murloc_greeting3"),
                QuoteType.FAREWELL, List.of("murloc_farewell1", "murloc_farewell2", "murloc_farewell3"),
                QuoteType.ATTACK, List.of("murloc_attack1", "murloc_attack2", "murloc_attack3", "murloc_attack4"),
                QuoteType.DEATH, List.of("murloc_death1")
            )),
            Map.entry(FormType.TAUREN_EVIL, Map.of(
                QuoteType.GREETING, List.of("tauren_evil_greeting1", "tauren_evil_greeting2", "tauren_evil_greeting3"),
                QuoteType.FAREWELL, List.of("tauren_evil_farewell1", "tauren_evil_farewell2", "tauren_evil_farewell3"),
                QuoteType.ATTACK, List.of("tauren_evil_attack1", "tauren_evil_attack2", "tauren_evil_attack3", "tauren_evil_attack4", "tauren_evil_attack5", "tauren_evil_attack6"),
                QuoteType.DEATH, List.of("tauren_evil_death1", "tauren_evil_death2", "tauren_evil_death3", "tauren_evil_death4", "tauren_evil_death5")
            )),
            Map.entry(FormType.HUMAN_EVIL, Map.of(
                QuoteType.GREETING, List.of("human_evil_greeting1", "human_evil_greeting2", "human_evil_greeting3", "human_evil_greeting4", "human_evil_greeting5"),
                QuoteType.FAREWELL, List.of("human_evil_farewell1", "human_evil_farewell2", "human_evil_farewell3", "human_evil_farewell4", "human_evil_farewell5"),
                QuoteType.ATTACK, List.of("human_evil_attack1", "human_evil_attack2", "human_evil_attack3", "human_evil_attack4", "human_evil_attack5", "human_evil_attack6"),
                QuoteType.DEATH, List.of("human_evil_death1", "human_evil_death2", "human_evil_death3", "human_evil_death4", "human_evil_death5", "human_evil_death6")
            )),
            Map.entry(FormType.NECROMANCER, Map.of(
                QuoteType.GREETING, List.of("necromancer_greeting1", "necromancer_greeting2", "necromancer_greeting3", "necromancer_greeting4", "necromancer_greeting5", "necromancer_greeting6", "necromancer_greeting7"),
                QuoteType.FAREWELL, List.of("necromancer_farewell1", "necromancer_farewell2", "necromancer_farewell3", "necromancer_farewell4"),
                QuoteType.ATTACK, List.of("necromancer_attack1", "necromancer_attack2", "necromancer_attack3", "necromancer_attack4", "necromancer_attack5"),
                QuoteType.DEATH, List.of("necromancer_death1", "necromancer_death2", "necromancer_death3", "necromancer_death4", "necromancer_death5")
            )),
            Map.entry(FormType.NECROMANCER_SKELETON, Map.of(
                QuoteType.GREETING, List.of("necromancer_skeleton_greeting1", "necromancer_skeleton_greeting2", "necromancer_skeleton_greeting3"),
                QuoteType.FAREWELL, List.of("necromancer_skeleton_farewell1", "necromancer_skeleton_farewell2", "necromancer_skeleton_farewell3"),
                QuoteType.ATTACK, List.of("necromancer_skeleton_attack1", "necromancer_skeleton_attack2", "necromancer_skeleton_attack3", "necromancer_skeleton_attack4", "necromancer_skeleton_attack5", "necromancer_skeleton_attack6", "necromancer_skeleton_attack7", "necromancer_skeleton_attack8", "necromancer_skeleton_attack9", "necromancer_skeleton_attack10"),
                QuoteType.DEATH, List.of("necromancer_skeleton_death1", "necromancer_skeleton_death2", "necromancer_skeleton_death3", "necromancer_skeleton_death4", "necromancer_skeleton_death5")
            )),
            Map.entry(FormType.DRACTHYR, Map.of(
                QuoteType.GREETING, List.of("dracthyr_greeting1", "dracthyr_greeting2", "dracthyr_greeting3", "dracthyr_greeting4", "dracthyr_greeting5", "dracthyr_greeting6", "dracthyr_greeting7"),
                QuoteType.FAREWELL, List.of("dracthyr_farewell1", "dracthyr_farewell2", "dracthyr_farewell3", "dracthyr_farewell4", "dracthyr_farewell5", "dracthyr_farewell6", "dracthyr_farewell7"),
                QuoteType.ATTACK, List.of("dracthyr_attack1", "dracthyr_attack2", "dracthyr_attack3", "dracthyr_attack4", "dracthyr_attack5", "dracthyr_attack6", "dracthyr_attack7", "dracthyr_attack8", "dracthyr_attack9"),
                QuoteType.DEATH, List.of("dracthyr_death1", "dracthyr_death2", "dracthyr_death3", "dracthyr_death4", "dracthyr_death5", "dracthyr_death6", "dracthyr_death7", "dracthyr_death8", "dracthyr_death9", "dracthyr_death10")
            ))
        );
    }

    public SoundManager(JavaPlugin plugin) {
        SoundCommand   soundCommand = new SoundCommand();
        plugin.getCommand("quote").setExecutor(soundCommand);
        plugin.getCommand("quotegreeting").setExecutor(soundCommand);
        plugin.getCommand("quotefarewell").setExecutor(soundCommand);
        plugin.getCommand("quoteattack").setExecutor(soundCommand);
        plugin.getCommand("quotedeath").setExecutor(soundCommand);
    }
    
	/*
	** fonctions statiques
	*/

    public static void playQuote(NPC npc, QuoteType quoteType) {
        FormType    formType = npc.getOrAddTrait(TraitSentinel.class).getFormType();
        Location    loc;
        if (npc.getEntity() instanceof LivingEntity livingEntity)
            loc = livingEntity.getLocation();
        else
            loc = npc.getStoredLocation();
        playQuoteFromForm(loc, quoteType, formType);
    }

    public static void playQuote(PlayerCustom playerCustom, QuoteType quoteType) {
        Player      player = playerCustom.getPlayer();
        FormType    formType = playerCustom.getFormType();
        playQuoteFromForm(player.getLocation(), quoteType, formType);
    }

    private static void playQuoteFromForm(Location loc, QuoteType quoteType, FormType formType) {
        Map<QuoteType, List<String>>    map = QUOTES.get(formType);
        if (map == null) return;
        List<String> list = map.get(quoteType);
        if (list == null || list.size() < 1) return;
        loc.getWorld().playSound(loc, "sounds:" + list.get((new Random()).nextInt(list.size())), SoundCategory.MASTER, 1.5f, 1f);
    }

    public static void playSound(PlayerCustom playerCustom, String soundName) {
		Location	loc = playerCustom.getPlayer().getLocation();
        loc.getWorld().playSound(loc, "sounds:" + soundName, SoundCategory.MASTER, 1.5f, 1f);
    }

    public static void playSound(NPC npc, String soundName) {
		if (!(npc.getEntity() instanceof LivingEntity livingEntity)) return;
		Location	loc = livingEntity.getLocation();
        loc.getWorld().playSound(loc, "sounds:" + soundName, SoundCategory.MASTER, 1.5f, 1f);
    }

    public static void playSound(LivingEntity livingEntity, String soundName) {
		Location	loc = livingEntity.getLocation();
        loc.getWorld().playSound(loc, "sounds:" + soundName, SoundCategory.MASTER, 1.5f, 1f);
    }

    public static void playSound(Location loc, String soundName) {
        loc.getWorld().playSound(loc, "sounds:" + soundName, SoundCategory.MASTER, 1.5f, 1f);
    }
}

