package fr.jeunesauvage.world;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class WorldManager implements Listener {
	private static final Set<Material> INFINIBURN = EnumSet.of(
	    Material.NETHERRACK,
	    Material.MAGMA_BLOCK,
	    Material.SOUL_SAND,
	    Material.SOUL_SOIL,
	    Material.COAL_BLOCK
	);
	private static final Set<Material> POISON = EnumSet.of(
	    Material.LIME_STAINED_GLASS,
	    Material.LIME_STAINED_GLASS_PANE
	);

	public WorldManager() {
        WorldCommand   worldCommand = new WorldCommand(this);
    	RpgCraft.instance().getCommand("cleanentities").setExecutor(worldCommand);
		// task world
		new BukkitRunnable() {
		    @Override
		    public void run() {
		        for (PlayerCustom playerCustom : RpgCraft.getEntityCustomRegistry().getPlayerCustoms()) {
					World		world = playerCustom.getWorld();
					Location	center = playerCustom.getLocation();
		            Biome	currentBiome = center.getBlock().getBiome();
					if (playerCustom.hasPotionEffect(PotionEffectType.DARKNESS) && currentBiome != Biome.DEEP_DARK)
						playerCustom.removePotionEffect(PotionEffectType.DARKNESS);
					else if (!world.getName().equals("world_the_end") && !playerCustom.hasPotionEffect(PotionEffectType.DARKNESS) && currentBiome == Biome.DEEP_DARK)
		            	playerCustom.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 7199980, 0, false, true, false));
            		if (!playerCustom.hasPotionEffect(PotionEffectType.POISON) && isNearPoisonBlock(center, 1))
						playerCustom.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1, false, true, false));
		        }
		    }
		}.runTaskTimer(RpgCraft.instance(), 0L, 20L);
	}

	private boolean isNearPoisonBlock(Location center, int radius) {
	    World	world = center.getWorld();
	    int		baseX = center.getBlockX();
	    int		baseY = center.getBlockY();
	    int		baseZ = center.getBlockZ();
	    for (int x = -radius; x <= radius; x++) {
	        for (int y = -radius; y <= radius; y++) {
	            for (int z = -radius; z <= radius; z++) {
	                Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
	                if (POISON.contains(block.getType())) return true;
	            }
	        }
	    }
	    return false;
	}

	// world

	// cancel vanilla boss bar
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		LivingEntity	livingEntity = e.getEntity();
		if (livingEntity instanceof Wither wither)
	    	wither.getBossBar().setVisible(false);
	}

	// cancel repair + cancel enchanting on anvil
	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent e) {
		e.getView().setRepairCost(0);
    	ItemStack	right = e.getInventory().getItem(1);
    	if (right != null && right.getType() != Material.AIR) e.setResult(null);
	}

	// cancel enchanting
	@EventHandler
	public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
	    e.setCancelled(true);
	}

	// cancel level cost
	@EventHandler
	public void onEnchantItem(EnchantItemEvent e) {
	    e.setExpLevelCost(0);
	}

	// cancel zombie to burn
	@EventHandler
	public void onEntityCombust(EntityCombustEvent e) {
		LivingEntityCustom	zombie = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(e.getEntity().getUniqueId());
	    if (zombie == null || zombie.getRaceType() != RaceType.ZOMBIE) return;
		e.setCancelled(true);
	}

	// cancel break block
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onBlockBreak(BlockBreakEvent e) {
		Player	player = e.getPlayer();
		if (player.getInventory().getItemInOffHand().getType() != Material.BEDROCK)
	    	e.setCancelled(true);
	}

	// cancel place block
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player	player = e.getPlayer();
		if (player.getInventory().getItemInOffHand().getType() != Material.BEDROCK)
	    	e.setCancelled(true);
	}

	// cancel empty bucket
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	public void onBucketEmpty(PlayerBucketEmptyEvent e) {
		Player	player = e.getPlayer();
        Block	c = e.getBlockClicked();
		if (e.getBucket() == Material.LAVA_BUCKET && c.getType() == Material.NETHERRACK) return;
        if (c.getType() == Material.CAULDRON || c.getType() == Material.WATER_CAULDRON) return;
		if (player.getInventory().getItemInOffHand().getType() == Material.BEDROCK) return;
		e.setCancelled(true);
	}

	// cancel fire spread
	@EventHandler
	public void onSpread(BlockSpreadEvent e) {
		Material	material = e.getSource().getType();
	    if (material == Material.FIRE || material == Material.SOUL_FIRE) e.setCancelled(true);
	}

	// cancel fire burn
	@EventHandler
	public void onBurn(BlockBurnEvent e) {
	    e.setCancelled(true);
	}

	// cancel fire ignite
	@EventHandler
	public void onIgnite(BlockIgniteEvent e) {
    	Block	block = e.getBlock();
    	for (int x = -1; x <= 1; x++) {
    	    for (int y = -1; y <= 1; y++) {
    	        for (int z = -1; z <= 1; z++) {
    	            if (x == 0 && y == 0 && z == 0) continue;
    	            Block		relative = block.getRelative(x, y, z);
					Material	material = relative.getType();
    	            if (material.isBurnable() || INFINIBURN.contains(material)) {
    	                e.setCancelled(true);
    	                return;
    	            }
    	        }
    	    }
    	}
	}

	// cancel change on sign
	@EventHandler
	public void onSignOpen(PlayerOpenSignEvent e) {
		PlayerCustom	playerCustom = RpgCraft.getEntityCustomRegistry().getPlayerCustom(e.getPlayer().getUniqueId());
		if (playerCustom == null) return;
		ItemStack	bedrock = playerCustom.getEquipment().getItemInOffHand();
		if (bedrock != null && bedrock.getType() == Material.BEDROCK) return;
	    e.setCancelled(true);
	}

	// remove projectiles + display
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
    	for (Entity entity : e.getChunk().getEntities()) {
        	if (entity instanceof Projectile)
        	    entity.remove();
			else if (entity instanceof Display display)
				display.remove();
    	}
	}

    public void cleanEntities() {
        for (World world: Bukkit.getWorlds()) {
            for (Entity entity: world.getEntities()) {
                if (entity instanceof Projectile)
                    entity.remove();
				else if (entity instanceof Display display)
					display.remove();
            }
        }
        for (NPC npc: CitizensAPI.getNPCRegistry()) {
			NPCCustom	npcCustom = RpgCraft.getEntityCustomRegistry().getNPCCustom(npc.getUniqueId());
			if (npcCustom == null) continue;
			if (npcCustom.getRespawnTime() <= 0 || npcCustom.isPet() || npcCustom.getTemplateType() == TemplateType.DEFAULT)
				npcCustom.delete();
        }
    }
}
