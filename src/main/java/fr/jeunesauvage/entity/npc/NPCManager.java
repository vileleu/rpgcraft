package fr.jeunesauvage.entity.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.betonquest.betonquest.api.PlayerConversationEndEvent;
import org.betonquest.betonquest.api.PlayerConversationStartEvent;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.jeunesauvage.entity.npc.trait.TraitSentinel;
import fr.jeunesauvage.entity.npc.trait.TraitSentinelManager;
import fr.jeunesauvage.entity.team.Team;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.sound.QuoteType;
import fr.jeunesauvage.sound.SoundManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entity.EntityManager;
import fr.jeunesauvage.entity.npc.npcbuilder.NPCBuilder;
import fr.jeunesauvage.entity.npc.npcspell.NPCSpellManager;


public class NPCManager implements Listener {
    private final Map<UUID, Integer>  lastNpcClicked = new HashMap<>();
    private final Map<UUID, Integer>  conversations = new HashMap<>();

	public NPCManager(JavaPlugin plugin, EntityManager entityManager, ItemCustomManager itemCustomManager) {
		// trait sentinel manager
        NPCSpellManager npcSpellManager = new NPCSpellManager(entityManager, itemCustomManager);
        plugin.getServer().getPluginManager().registerEvents(npcSpellManager, plugin);
		// trait sentinel manager
        TraitSentinelManager traitSentinelManager = new TraitSentinelManager(itemCustomManager, npcSpellManager);
        plugin.getServer().getPluginManager().registerEvents(traitSentinelManager, plugin);
        // npc builder
        NPCBuilder	npcBuilder = new NPCBuilder(plugin);
        plugin.getServer().getPluginManager().registerEvents(npcBuilder, plugin);
	}

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onClick(NPCRightClickEvent e) {
        Player  player = e.getClicker();
        NPC     npc = e.getNPC();
        if (npc.getEntity() instanceof LivingEntity livingEntity) {
            if (Team.isFriend(player, livingEntity)) {
                // is in combat
                if (npc.getOrAddTrait(TraitSentinel.class).getTargetHelper().getTarget() != null) {
                    player.sendMessage(Component.text(npc.getName() + " ").append(Message.npcCombat()).color(NamedTextColor.YELLOW));
                    e.setCancelled(true);
                }
                // is in conversation
                else if (conversations.containsValue(npc.getId())) {
                    player.sendMessage(Component.text(npc.getName() + " ").append(Message.npcOccuped()).color(NamedTextColor.YELLOW));
                    e.setCancelled(true);
                }
                // player is invisible
                else if (player.isInvisible()) {
                    player.sendMessage(Component.text(npc.getName() + " ").append(Message.npcCantSee()).color(NamedTextColor.YELLOW));
                    e.setCancelled(true);
                }
                else {
                    UUID    uuid = player.getUniqueId();
                    SoundManager.playQuote(npc, QuoteType.GREETING);
                    lastNpcClicked.put(uuid, npc.getId());
                    // delete from lastNpcClicked after 1 second
                    Bukkit.getScheduler().runTaskLater(RpgCraft.instance(), () -> {
                        lastNpcClicked.remove(uuid);
                    }, 20L);
                }
            }
        }
    }

	// start conversation
    @EventHandler
    public void onConversationStart(PlayerConversationStartEvent e) {
		Conversation	conversation = e.getConversation();
		if (conversation == null) return;
		Player  player = e.getProfile().getPlayer().getPlayer();
		if (player == null) {
            conversation.endConversation();
            return;
        }
		UUID    uuid = player.getUniqueId();
		int     id = lastNpcClicked.get(uuid);
		NPC     npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc == null) {
            conversation.endConversation();
            return;
        }
		conversations.put(uuid, id);
	}

	// end conversation
    @EventHandler
    public void onConversationEnd(PlayerConversationEndEvent e) {
		Player	player = e.getProfile().getPlayer().getPlayer();
		if (player == null) return;
		UUID    uuid = player.getUniqueId();
        Integer id = conversations.get(uuid);
		if (id != null) {
            if (!lastNpcClicked.containsKey(uuid)) {
		        NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		        if (npc != null)
		            SoundManager.playQuote(npc, QuoteType.FAREWELL);
            }
            conversations.remove(uuid, id);
        }
	}
}
