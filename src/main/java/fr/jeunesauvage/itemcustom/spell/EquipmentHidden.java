package fr.jeunesauvage.itemcustom.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;

public class EquipmentHidden extends PacketAdapter {
    private final Map<Integer, UUID>    invisibleEntities = new HashMap<>();

    public EquipmentHidden() {
        super(RpgCraft.instance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT);
    }

    public void onStealthActivated(LivingEntityCustom target) {
        sendEquipmentUpdate(target, true);
    }

    // à appeler quand le stealth se désactive
    public void onStealthDeactivated(LivingEntityCustom target) {
        sendEquipmentUpdate(target, false);
    }

    private void sendEquipmentUpdate(LivingEntityCustom target, boolean hide) {
        EntityEquipment equipment = target.getEquipment();
        if (equipment == null) return;
        ProtocolManager protocolManager = RpgCraft.instanceProtocolLib();
        List<Pair<ItemSlot, ItemStack>> slots = new ArrayList<>();
        slots.add(new Pair<>(ItemSlot.HEAD, hide ? new ItemStack(Material.AIR) : equipment.getHelmet()));
        slots.add(new Pair<>(ItemSlot.CHEST, hide ? new ItemStack(Material.AIR) : equipment.getChestplate()));
        slots.add(new Pair<>(ItemSlot.LEGS, hide ? new ItemStack(Material.AIR) : equipment.getLeggings()));
        slots.add(new Pair<>(ItemSlot.FEET, hide ? new ItemStack(Material.AIR) : equipment.getBoots()));
        slots.add(new Pair<>(ItemSlot.MAINHAND, hide ? new ItemStack(Material.AIR) : equipment.getItemInMainHand()));
        slots.add(new Pair<>(ItemSlot.OFFHAND, hide ? new ItemStack(Material.AIR) : equipment.getItemInOffHand()));
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, target.getEntityId());
        packet.getSlotStackPairLists().write(0, slots);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (viewer.getUniqueId().equals(target.getUUID())) continue;
            try {
                protocolManager.sendServerPacket(viewer, packet);
            }
            catch (Exception e) {
                RpgCraft.debug("Error sendEquipmentUpdate(): " + e.getMessage());
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        int                 id = event.getPacket().getIntegers().read(0);
        UUID                uuid = invisibleEntities.get(id);
        if (uuid == null) return;
        LivingEntityCustom  target = RpgCraft.getEntityCustomRegistry().getLivingEntityCustom(uuid);
        if (target == null) return;
        if (RpgCraft.getSpellRegistry().hasStealth(target)) {
            List<Pair<ItemSlot, ItemStack>> slots = event.getPacket().getSlotStackPairLists().read(0);
            List<Pair<ItemSlot, ItemStack>> filtered = new ArrayList<>();
            for (Pair<ItemSlot, ItemStack> pair : slots) {
                boolean isHiddenSlot = pair.getFirst() == ItemSlot.HEAD
                        || pair.getFirst() == ItemSlot.CHEST
                        || pair.getFirst() == ItemSlot.LEGS
                        || pair.getFirst() == ItemSlot.FEET
                        || pair.getFirst() == ItemSlot.MAINHAND
                        || pair.getFirst() == ItemSlot.OFFHAND;
                filtered.add(isHiddenSlot ? new Pair<>(pair.getFirst(), new ItemStack(Material.AIR)) : pair);
            }
            event.getPacket().getSlotStackPairLists().write(0, filtered);
        }
    }
}
