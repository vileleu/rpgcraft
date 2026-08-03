package fr.jeunesauvage.entity.modifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;

public class EntityModifierManager implements Listener {
	private int												counter;
	private final Map<UUID, Map<Integer, EntityModifier>>	modifiers = new HashMap<>();
	private BukkitTask										task;

	public EntityModifierManager(JavaPlugin plugin) {
		// command
		EntityModifierCommand	entityModifierCommand = new EntityModifierCommand(this);
		plugin.getCommand("addentitymodifier").setExecutor(entityModifierCommand);
		plugin.getCommand("removeentitymodifier").setExecutor(entityModifierCommand);
        plugin.getCommand("infosentitymodifier").setExecutor(entityModifierCommand);
		// init
		this.counter = 1;
		initEntities();
		this.task = startTask();
	}

	// load entities

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e) {
		long	now = System.currentTimeMillis();
	    for (Entity entity : e.getChunk().getEntities()) {
	        if (!(entity instanceof LivingEntity livingEntity)) continue;
	    	loadEntity(livingEntity, now);
	    }
	}

	private void initEntities() {
		long	now = System.currentTimeMillis();
		for (World world : Bukkit.getWorlds()) {
		    for (LivingEntity livingEntity : world.getLivingEntities())
				loadEntity(livingEntity, now);
		}
	}

	private void loadEntity(LivingEntity livingEntity, long now) {
		if (isPlayer(livingEntity)) return;
		cleanLivingEntity(livingEntity);
		PersistentDataContainer	pdc = livingEntity.getPersistentDataContainer();
		for (NamespacedKey keyTimer: Set.copyOf(pdc.getKeys())) {
			if (!isKeyTimer(keyTimer)) continue;
			NamespacedKey	keyValue = getKeyValueFromKeyTimer(keyTimer);
			long	end = Data.getLong(pdc, keyTimer);
			if (end > now) {
				StatSecondary	type = getStatSecondaryFromKeyTimer(keyTimer);
				long			duration = ((end - now) / 1000) + 1;
				int				value = Data.getInteger(pdc, keyValue);
			    modifiers.computeIfAbsent(livingEntity.getUniqueId(), u -> new HashMap<>()).put(counter++, new EntityModifier(livingEntity, type, value, duration, counter));
			}
			else {
				Data.remove(pdc, keyValue);
				Data.remove(pdc, keyTimer);
			}
		}
	}

	private boolean isKeyTimer(NamespacedKey keyTimer) {
		return (keyTimer.getKey().startsWith("statentitytimer-"));
	}

	private NamespacedKey getKeyValueFromKeyTimer(NamespacedKey keyTimer) {
		if (keyTimer == null || keyTimer.getKey().length() < 16) return null;
		return new NamespacedKey(RpgCraft.name(), "statentityvalue-" + keyTimer.getKey().substring(16));
	}

	private StatSecondary getStatSecondaryFromKeyTimer(NamespacedKey keyTimer) {
		if (keyTimer == null) return null;
		String	keyString = keyTimer.getKey();
		int		first = keyString.indexOf('-');
		int		last = keyString.lastIndexOf('-');
		if (first == -1 || last == -1) return null;
		return StatSecondary.fromString(keyString.substring(first + 1, last));
	}

	private void cleanLivingEntity(LivingEntity livingEntity) {
		for (Attribute attribute: Attribute.values()) {
			AttributeInstance				attributeInstance = livingEntity.getAttribute(attribute);
			if (attributeInstance == null) continue;
			Collection<AttributeModifier>	attributeModifiers = attributeInstance.getModifiers();
			for (AttributeModifier attributeModifier: Set.copyOf(attributeModifiers)) {
				RpgCraft.debug("(living " + livingEntity.getName() + ") modifier vanilla: " + attributeModifier.getKey().getKey());
				if (attributeModifier.getKey().getNamespace().equals(RpgCraft.name()))
					attributeInstance.removeModifier(attributeModifier);
			}
		}
	}

	// task timer (all seconds)

	private BukkitTask startTask() {
	    return Bukkit.getScheduler().runTaskTimer(RpgCraft.instance(), () -> {
	        if (isEmpty()) {
	            task.cancel();
	            task = null;
	            return;
	        }
	        long												now = System.currentTimeMillis();
			Iterator<Entry<UUID, Map<Integer, EntityModifier>>>	it1 = modifiers.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<UUID, Map<Integer, EntityModifier>>	e1 = it1.next();
				UUID										uuid = e1.getKey();
				if (!(Bukkit.getEntity(uuid) instanceof LivingEntity livingEntity)) {
					it1.remove();
					continue;
				}
				if (livingEntity.isDead() || !livingEntity.isValid()) {
					it1.remove();
					continue;
				}
				PersistentDataContainer						pdc = livingEntity.getPersistentDataContainer();
				Map<Integer, EntityModifier>				map = e1.getValue();
				Iterator<Entry<Integer, EntityModifier>>	it2 = map.entrySet().iterator();
				while (it2.hasNext()) {
					Entry<Integer, EntityModifier>	e2 = it2.next();
					EntityModifier					entityStatModifier = e2.getValue();
					long	end = Data.getLong(pdc, entityStatModifier.getKeyTimer());
					if (now >= end) {
						entityStatModifier.remove();
						it2.remove();
					}
				}
				if (map.isEmpty())
					it1.remove();
			}
	    }, 20L, 20L);
	}

	private boolean isEmpty() {
		if (modifiers.isEmpty()) return true;
		for (Map<Integer, EntityModifier> map :modifiers.values()) {
			for (EntityModifier entityModifier: map.values()) {
				if (entityModifier.getDuration() != 0)
					return false;
			}
		}
		return true;
	}

	// method external

	public int addModifier(LivingEntity livingEntity, StatSecondary type, int value, long duration) {
		if (isPlayer(livingEntity)) return 0;
		if (type == null) return 0;
		duration = (duration < 0 ? 0 : duration);
		int	id = counter++;
		modifiers.computeIfAbsent(livingEntity.getUniqueId(), u -> new HashMap<>()).put(id, new EntityModifier(livingEntity, type, value, duration, id));
		if (task == null) task = startTask();
		return id;
	}

	public void removeModifier(LivingEntity livingEntity, StatSecondary type) {
		if (isPlayer(livingEntity)) return;
		UUID										uuid = livingEntity.getUniqueId();
		Map<Integer, EntityModifier>				map = modifiers.get(uuid);
		if (map == null) return;
		Iterator<Entry<Integer, EntityModifier>>	it = map.entrySet().iterator();
		while (it.hasNext()) {
			EntityModifier	entityModifier = it.next().getValue();
			if (entityModifier.getType() != type) continue;
			it.remove();
		}
		if (map.isEmpty()) modifiers.remove(uuid);
	}

	public void removeModifier(LivingEntity livingEntity, int id) {
		if (isPlayer(livingEntity)) return;
		UUID							uuid = livingEntity.getUniqueId();
		Map<Integer, EntityModifier>	map = modifiers.get(uuid);
		if (map == null) return;
		map.remove(id);
		if (map.isEmpty()) modifiers.remove(uuid);
	}

	public int getModifierValue(LivingEntity livingEntity, StatSecondary type) {
		if (isPlayer(livingEntity)) return 0;
		if (type == null) return 0;
		Map<Integer, EntityModifier>	map = modifiers.get(livingEntity.getUniqueId());
		if (map == null) return 0;
		int	value = 0;
		for (EntityModifier entityStatModifier: map.values()) {
			if (entityStatModifier.getType() != type) continue;
			value += entityStatModifier.getValue();
		}
		return value;
	}

	public Map<Integer, EntityModifier> getEntityModifiers(LivingEntity livingEntity) {
		return modifiers.get(livingEntity.getUniqueId());
	}

	private boolean isPlayer(LivingEntity livingEntity) {
		return (!livingEntity.hasMetadata("NPC") && livingEntity instanceof Player);
	}
}
