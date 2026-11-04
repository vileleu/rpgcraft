package fr.jeunesauvage.entitycustom.livingentitycustom.drop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.MobCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.NPCCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.Rarity;

public class DropRegistry {
    private static final int                    RANGE_LEVEL = 5;
    private final Map<String, ItemCustom<?>>    equipables = new HashMap<>();
    private final Map<String, ItemCustom<?>>    potions = new HashMap<>();
    private final Map<RaceType, Material>       foods = new HashMap<>();
    private final Map<RaceType, Boolean>        golds = new HashMap<>();

    public DropRegistry() {
        this.equipables.putAll(RpgCraft.getItemCustomRegistry().getWeaponsDroppable());
        this.equipables.putAll(RpgCraft.getItemCustomRegistry().getArmorsDroppable());
        this.potions.putAll(RpgCraft.getItemCustomRegistry().getPotions());
        for (RaceType raceType: RaceType.values()) {
            Material    food = null;
            Boolean     gold = false;
            switch (raceType) {
                case ORC, TAUREN, ELFBLOOD -> {
                    food = Material.BREAD;
                    gold = true;
                }
                case HUMAN, DWARF, ELFNIGHT, DWARFIRON, IRON_GUARD -> {
                    food = Material.APPLE;
                    gold = true;
                }
                case MURLOC -> {
                    food = Material.SALMON;
                    gold = true;
                }
                case SKELETON, ZOMBIE, NECROMANCER, NECROMANCER_SKELETON -> {
                    food = Material.ROTTEN_FLESH;
                    gold = true;
                }
                case DEMON -> {
                    food = Material.CARROT;
                    gold = true;
                }
                case SPIDER, SCORPION -> food = Material.POTATO;
                case KODO -> food = Material.BEEF;
                case ELEMENTAL, ANIMAL, UNKNOWN -> food = null;
            }
            foods.put(raceType, food);
            golds.put(raceType, gold);
        }
        int count = 0;
        for (ItemCustom<?> itemCustom: equipables.values()) {
            RpgCraft.debug("itemCustom " + ++count + ": " + itemCustom.getIdentifier());
        }
    }

    private Rarity getRarity() {
        int		random = ThreadLocalRandom.current().nextInt(100);
        // EPIC 5%
        if (random < 5) return Rarity.EPIC;
        // RARE 10%
        else if (random < 15) return Rarity.RARE;
        // UNCOMMON 20%
        else if (random < 35) return Rarity.UNCOMMON;
        // COMMON 30%
        else if (random < 65) return Rarity.COMMON;
        // POOR 35%
        else return Rarity.POOR;
    }

    private List<ItemCustom<?>> getRangeItemCustoms(int level, Rarity rarity, Map<String, ItemCustom<?>> map) {
        List<ItemCustom<?>> result = new ArrayList<>();
        for (ItemCustom<?> itemCustom: map.values()) {
            int itemLevel = itemCustom.getLevel();
            if (rarity == itemCustom.getRarity() && (level - RANGE_LEVEL >= itemLevel && level + RANGE_LEVEL <= itemLevel)) result.add(itemCustom);
        }
        return result;
    }

    public Set<ItemStack> dropPlayer(PlayerCustom playerCustom) {
        Set<ItemStack>  result = new HashSet<>();
        PlayerInventory inv = playerCustom.getInventory();
        List<Integer>   itemSlots = new ArrayList<>();
        List<Integer>   otherSlots = new ArrayList<>();
		ItemStack[]		contents = inv.getContents();
    	for (int i = 0; i < contents.length; i++) {
			ItemStack	item = contents[i];
            if (item == null || item.getType() == Material.AIR) continue;
            ItemCustom<?>   itemCustom = RpgCraft.getItemCustomRegistry().getItemCustom(item);
            if (itemCustom != null) itemSlots.add(i);
            else otherSlots.add(i);
		}
        if (!itemSlots.isEmpty())
            result.add(inv.getItem(itemSlots.get(ThreadLocalRandom.current().nextInt(itemSlots.size() + 1))));
        if (!otherSlots.isEmpty())
            result.add(inv.getItem(otherSlots.get(ThreadLocalRandom.current().nextInt(otherSlots.size() + 1))));
        return result;
    }

    private Set<ItemStack> defaultDrop(NPCCustom npcCustom) {
        Set<ItemStack> result = new HashSet<>();
        switch (npcCustom.getTemplateType()) {
            case MURLOC_MRGL -> {
                result.add(RpgCraft.getItemCustomRegistry().getWeapon("bow/staff_bermundes_hermit").getItemClone());
            }
            case TAUREN_DESERT -> {

            }
            case PALPOUTINE -> {
                result.add(RpgCraft.getItemCustomRegistry().getWeapon("bow/staff_night").getItemClone());
            }
            case REDSTONE_GOLEM -> {
                
            }
            case TARENTULA -> {
                
            }
            case FROZER -> {
                
            }
            case ICE_LORD -> {
                
            }
            default -> {}
        };
        return result;
    }

    public Set<ItemStack> dropNPC(NPCCustom npcCustom) {
        Set<ItemStack>  result = defaultDrop(npcCustom);
        if (npcCustom.getRespawnTime() == 0 || npcCustom.getRespawnTime() == -1) return result;
        int             level = npcCustom.getLevel();
        int		        random = ThreadLocalRandom.current().nextInt(100);
        // 100% to drop gold
        if (golds.get(npcCustom.getRaceType())) {
            int amountNuggets = ThreadLocalRandom.current().nextInt(level / 2);
            int amountIngots = 0;
            while (amountNuggets >= 9) {
                amountIngots++;
                amountNuggets -= 9;
            }
            if (amountIngots > 0)
                result.add(new ItemStack(Material.GOLD_INGOT, amountIngots));
            if (amountNuggets > 0)
                result.add(new ItemStack(Material.GOLD_NUGGET, amountNuggets));
        }
		// 20% to drop food
		if (random % 5 == 0) {
            Material    material = foods.get(npcCustom.getRaceType());
            if (material != null)
                result.add(new ItemStack(material));
		}
		// 25% to drop equipable
		if (random % 4 == 0) {
            Rarity  rarity = getRarity();
            List<ItemCustom<?>> rangeEquipables = null;
            while (rangeEquipables == null || rangeEquipables.isEmpty()) {
                rangeEquipables = getRangeItemCustoms(level, rarity, equipables);
                if (rangeEquipables.isEmpty()) {
                    if (rarity == Rarity.POOR) break;
                    rarity = Rarity.fromInt(rarity.getNumber() - 1);
                }
            }
            if (!rangeEquipables.isEmpty())
                result.add(rangeEquipables.get(ThreadLocalRandom.current().nextInt(rangeEquipables.size() + 1)).getItemClone());
		}
		// 10% to drop potion
		if (random % 10 == 0) {
            Rarity  rarity = getRarity();
            List<ItemCustom<?>> rangePotions = null;
            while (rangePotions == null || rangePotions.isEmpty()) {
                rangePotions = getRangeItemCustoms(level, rarity, potions);
                if (rangePotions.isEmpty()) {
                    if (rarity == Rarity.POOR) break;
                    rarity = Rarity.fromInt(rarity.getNumber() - 1);
                }
            }
            if (!rangePotions.isEmpty())
                result.add(rangePotions.get(ThreadLocalRandom.current().nextInt(rangePotions.size() + 1)).getItemClone());
		}
        return result;
    }

    public Set<ItemStack> dropMob(MobCustom mobCustom) {
        return new HashSet<>();
    }

    public void drop(LivingEntityCustom livingEntityCustom) {
        World           world = livingEntityCustom.getWorld();
        if (world == null) return;
        Set<ItemStack>  drops = switch (livingEntityCustom) {
            case PlayerCustom playerCustom -> dropPlayer(playerCustom);
            case NPCCustom npcCustom -> dropNPC(npcCustom);
            case MobCustom mobCustom -> dropMob(mobCustom);
        };
        for (ItemStack itemStack: drops) {
            world.dropItemNaturally(livingEntityCustom.getEyeLocation(), itemStack);
        }
    }

    public static int xpNeeded(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    public static int xpPercent(int level, int percent) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }
}
