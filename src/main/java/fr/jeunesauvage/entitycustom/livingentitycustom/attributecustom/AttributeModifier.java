package fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom;

import org.bukkit.NamespacedKey;
import org.bukkit.scheduler.BukkitTask;

import fr.jeunesauvage.RpgCraft;

public class AttributeModifier {
    public static final String  MODIFIER_VALUE_STRING = "modifiervalue";
    public static final String  MODIFIER_END_STRING = "modifierend";
    private final AttributeType attributeType;
    private final int           value;
    private final int           duration;
    private final long          end;
    private final BukkitTask    task;
    private final int           id;
    private final NamespacedKey keyValue;
    private final NamespacedKey keyEnd;
    
    public AttributeModifier(AttributeType attributeType, int value, int duration, int id, BukkitTask task) {
        this.attributeType = attributeType;
        this.value = value;
        this.duration = (duration < 0 ? -1 : duration);
        if (duration <= 0) {
            this.end = duration;
            this.task = null;
        }
        else {
            this.end = System.currentTimeMillis() + ((long)duration * 1000L);
            this.task = task;
        }
        this.id = id;
        this.keyValue = new NamespacedKey(RpgCraft.name(), MODIFIER_VALUE_STRING + '/' + attributeType.getName() + '/' + id);
        this.keyEnd = new NamespacedKey(RpgCraft.name(), MODIFIER_END_STRING + '/' + attributeType.getName() + '/' + id);
    }

    public AttributeType getType() {
        return attributeType;
    }

    public int getValue() {
        return value;
    }

    public int getDuration() {
        return duration;
    }

    public long getEnd() {
        return end;
    }

    public int getTimeLeft() {
        return (int)((end - System.currentTimeMillis()) / 1000);
    }

    public int getId() {
        return id;
    }

    public NamespacedKey getKeyValue() {
        return keyValue;
    }

    public NamespacedKey getKeyEnd() {
        return keyEnd;
    }

    public void cancel() {
        if (task == null) return;
        task.cancel();
    }
}
