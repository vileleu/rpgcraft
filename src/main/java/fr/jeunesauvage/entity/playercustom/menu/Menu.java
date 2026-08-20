package fr.jeunesauvage.entity.playercustom.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.Character;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.entity.modifier.EntityModifierManager;
import fr.jeunesauvage.entity.playercustom.PlayerCustom;
import fr.jeunesauvage.entity.playercustom.attributecustom.AttributeCategory;
import fr.jeunesauvage.entity.playercustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.entity.playercustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entity.playercustom.classcustom.ClassType;
import fr.jeunesauvage.entity.print.Print;
import fr.jeunesauvage.entity.race.RaceType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomManager;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorMaterial;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorType;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.spell.Spell;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.wesjd.anvilgui.AnvilGUI;

public class Menu {
	static public final NamespacedKey   KEY_MENU = new NamespacedKey(RpgCraft.name(), "menuid");
	static private final int            BACK_SLOT = 0;
	static private final int            SMALL_SLOT = 27;
	static private final int            BIG_SLOT = 54;
    private final EntityModifierManager entityModifierManager;
    private final PlayerCustom          sender;
    private final PlayerCustom          targetPlayer;
    private final LivingEntity          targetMob;

    public Menu(PlayerCustom sender, PlayerCustom target) {
        this.sender = sender;
        this.targetPlayer = target;
        this.targetMob = null;
        this.entityModifierManager = null;
        openMainMenu();
    }

    public Menu(PlayerCustom sender, LivingEntity target, EntityModifierManager entityModifierManager) {
        this.sender = sender;
        this.targetPlayer = null;
        this.targetMob = target;
        this.entityModifierManager = entityModifierManager;
        openMainMenu();
    }

    public void openMainMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu"));
        holder.setInventory(inv);
        inv.setItem(11, createSlot(Material.FLETCHING_TABLE, "Stats", "open_stats"));
        inv.setItem(12, createSlot(Material.CRAFTING_TABLE, "Skills", "open_skills"));
        inv.setItem(13, createSlot(Material.PHANTOM_SPAWN_EGG, "Race", "open_race"));
        inv.setItem(14, createSlot(Material.BLAZE_ROD, "Class", "open_class"));
        inv.setItem(15, createSlot(Material.IRON_SWORD, "Items", "open_items"));
        sender.getPlayer().openInventory(inv);
    }

    public void openStatsMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Stats"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(11, createSlot(Material.GLOW_INK_SAC, "Print Primary", "print_stats_primary"));
        inv.setItem(12, createSlot(Material.INK_SAC, "Print Secondary", "print_stats_secondary"));
        inv.setItem(13, createSlot(Material.GREEN_DYE, "Add", "add_stat"));
        inv.setItem(14, createSlot(Material.RED_DYE, "Remove", "remove_stat"));
        sender.getPlayer().openInventory(inv);
    }

    public void openStatsAddMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Stat Add")
            .text("stat value duration")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openStatsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]  texts = stateSnapshot.getText().toLowerCase().split(" ");
                    if (texts.length != 3) return Collections.emptyList();
                    StatType    statType = StatType.fromString(texts[0]);
                    int         value = 0;
                    int         duration = 0;
                    try {
                        value = Integer.parseInt(texts[1]);
                        duration = Integer.parseInt(texts[2]);
                    } catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    if (statType == null || value == 0 || duration < 0) return Collections.emptyList();
                    if (isPlayer())
                        targetPlayer.addStatModifier(statType, value, duration);
                    else if (statType.getCategory() == AttributeCategory.SECONDARY)
                        entityModifierManager.addModifier(targetMob, (StatSecondary)statType, value, duration);
                    else
                        return Collections.emptyList();
                    sender.getPlayer().sendMessage(Component.text("Stat Added!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(sender.getPlayer());
    }

    public void openStatsRemoveMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Stat Remove")
            .text("id")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openStatsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText();
                    int         id = 0;
                    try {
                        id = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    if (isPlayer())
                        targetPlayer.removeStatModifier(id);
                    else
                        entityModifierManager.removeModifier(targetMob, id);
                    sender.getPlayer().sendMessage(Component.text("Stat Removed!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(sender.getPlayer());
    }

    public void openSkillsMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Skills"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(11, createSlot(Material.GLOW_INK_SAC, "Print Primary", "print_skills_primary"));
        inv.setItem(12, createSlot(Material.INK_SAC, "Print Secondary", "print_skills_secondary"));
        inv.setItem(13, createSlot(Material.GREEN_DYE, "Add", "add_skill"));
        inv.setItem(14, createSlot(Material.RED_DYE, "Remove", "remove_skill"));
        sender.getPlayer().openInventory(inv);
    }

    public void openSkillsAddMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Skill Add")
            .text("skill value duration")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openSkillsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]  texts = stateSnapshot.getText().toLowerCase().split(" ");
                    if (texts.length != 3) return Collections.emptyList();
                    SkillType   skillType = SkillType.fromString(texts[0]);
                    int         value = 0;
                    int         duration = 0;
                    try {
                        value = Integer.parseInt(texts[1]);
                        duration = Integer.parseInt(texts[2]);
                    } catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    if (skillType != null && value != 0 && duration >= 0 && isPlayer()) {
                        targetPlayer.addSkillModifier(skillType, value, duration);
                        sender.getPlayer().sendMessage(Component.text("Skill Added!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
                    }
                }
                return Collections.emptyList();
            })
        .open(sender.getPlayer());   
    }

    public void openSkillsRemoveMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Skill Remove")
            .text("id")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openSkillsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText();
                    int         id = 0;
                    try {
                        id = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    if (isPlayer()) {
                        targetPlayer.removeSkillModifier(id);
                        sender.getPlayer().sendMessage(Component.text("Skill Removed!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
                    }
                }
                return Collections.emptyList();
            })
        .open(sender.getPlayer());   
    }

    public void openRaceMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Race"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(11, createSlot(Material.PHANTOM_SPAWN_EGG, "Print", "print_race"));
        inv.setItem(12, createSlot(Material.PAPER, "Change", "change_race"));
        sender.getPlayer().openInventory(inv);
    }

    public void openRaceChangeMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Race Change")
            .text("race")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openRaceMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String  text = stateSnapshot.getText().toLowerCase();
                    if (isPlayer()) {
                        targetPlayer.setRaceType(RaceType.fromString(text));
                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
                    }
                }
                return Collections.emptyList();
            })
        .open(sender.getPlayer());
    }

    public void openClassMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Class"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(11, createSlot(Material.BLAZE_ROD, "Print", "print_class"));
        inv.setItem(12, createSlot(Material.PAPER, "Change", "change_class"));
        inv.setItem(13, createSlot(Material.BLAZE_POWDER, "Spell", "print_spell"));
        sender.getPlayer().openInventory(inv);
    }

    public void openClassChangeMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Class Change")
            .text("class")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openClassMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String  text = stateSnapshot.getText().toLowerCase();
                    if (isPlayer()) {
                        targetPlayer.setClassType(ClassType.fromString(text));
                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
                    }
                }
                return Collections.emptyList();
            })
        .open(sender.getPlayer());
    }

    public void openSpellMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_class"));
        int i = 11;
        for (ClassType classType: ClassType.values()) {
            if (classType == ClassType.BEGGAR || classType == ClassType.GOD) continue;
            inv.setItem(i, createClass(classType));
            i++;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellPyromancerMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = itemCustomManager.getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.PYROMANCER)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellWarriorMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = itemCustomManager.getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.WARRIOR)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellRogueMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = itemCustomManager.getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.ROGUE)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellPriestMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = itemCustomManager.getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.PRIEST)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellDracthyrMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = itemCustomManager.getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.DRACTHYR)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellHunterMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = itemCustomManager.getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.HUNTER)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        sender.getPlayer().openInventory(inv);
    }

    private int getSlotSpell(Rarity rarity, int level) {
        return switch (rarity) {
            case POOR -> switch (level) {
                case 10 -> 2;
                case 15 -> 11;
                case 20 -> 20;
                case 25 -> 29;
                default -> 35;
            };
            case COMMON -> switch (level) {
                case 15 -> 3;
                case 20 -> 12;
                case 25 -> 21;
                case 30 -> 30;
                default -> 35;
            };
            case UNCOMMON -> switch (level) {
                case 20 -> 4;
                case 25 -> 13;
                case 30 -> 22;
                case 35 -> 31;
                default -> 35;
            };
            case RARE -> switch (level) {
                case 25 -> 5;
                case 30 -> 14;
                case 35 -> 23;
                case 40 -> 32;
                default -> 35;
            };
            case EPIC -> switch (level) {
                case 30 -> 6;
                case 35 -> 15;
                case 40 -> 24;
                case 45 -> 33;
                default -> 35;
            };
            case LEGENDARY -> switch (level) {
                case 35 -> 7;
                case 40 -> 16;
                case 45 -> 25;
                case 50 -> 34;
                default -> 35;
            };
        };
    }

    public void openItemsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Items"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        int i = 9;
        for (WeaponType weaponType: WeaponType.values()) {
            if (weaponType == WeaponType.HAND || weaponType == WeaponType.UNKNOWN) continue;
            String  name = weaponType.getName();
            int     count = getEquipablesCount(itemCustomManager, weaponType);
            int     nb = 0;
            while (count > 0) {
                if (i >= BIG_SLOT) return;
                inv.setItem(i++, createSlot(weaponType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1), "print_" + name + (nb > 0 ? nb : "")));
                nb++;
                count -= BIG_SLOT - 1;
            }
        }
        for (ArmorType armorType: ArmorType.values()) {
            if (armorType.getArmorMaterial() != ArmorMaterial.CLOTH && armorType != ArmorType.ELYTRA) continue;
            String  name = armorType.getName();
            int     underscore = name.indexOf('_');
            if (underscore != -1)
                name = name.substring(underscore + 1);
            int     count = getEquipablesCount(itemCustomManager, armorType.getMaterial());
            int     nb = 0;
            while (count > 0) {
                if (i >= BIG_SLOT) return;
                inv.setItem(i++, createSlot(armorType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1), "print_" + name + (nb > 0 ? nb : "")));
                nb++;
                count -= BIG_SLOT - 1;
            }
        }
        sender.getPlayer().openInventory(inv);
    }

    private int getEquipablesCount(ItemCustomManager itemCustomManager, ItemCustomType itemCustomType) {
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         count = 0;
        if (itemCustomType.getCategory() == ItemCustomCategory.WEAPON) {
            WeaponType  weaponType = (WeaponType)itemCustomType;
            WeaponType  equipableType = null;
            for (Equipable<?> equipable: equipables.values()) {
                if (equipable.getType().getCategory() != ItemCustomCategory.WEAPON) continue;
                equipableType = (WeaponType)equipable.getType();
                if (equipableType != weaponType) continue;
                count++;
            }
        }
        else if (itemCustomType.getCategory() == ItemCustomCategory.ARMOR) {
            ArmorType  armorType = (ArmorType)itemCustomType;
            ArmorType  equipableType = null;
            for (Equipable<?> equipable: equipables.values()) {
                if (equipable.getType().getCategory() != ItemCustomCategory.ARMOR) continue;
                equipableType = (ArmorType)equipable.getType();
                if (equipableType != armorType) continue;
                count++;
            }
        }
        return count;
    }

    // armor only
    private int getEquipablesCount(ItemCustomManager itemCustomManager, Material material) {
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         count = 0;
        ArmorType                   equipableType = null;
        for (Equipable<?> equipable: equipables.values()) {
            if (equipable.getType().getCategory() != ItemCustomCategory.ARMOR) continue;
            equipableType = (ArmorType)equipable.getType();
            if (equipableType.getMaterial() != material) continue;
            count++;
        }
        return count;
    }

    private List<Equipable<?>> getEquipablesList(ItemCustomManager itemCustomManager, ItemCustomType itemCustomType, int start) {
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        List<Equipable<?>>          list = new ArrayList<>();
        if (itemCustomType.getCategory() == ItemCustomCategory.WEAPON) {
            WeaponType  weaponType = (WeaponType)itemCustomType;
            WeaponType  equipableType = null;
            for (Equipable<?> equipable: equipables.values()) {
                if (equipable.getType().getCategory() != ItemCustomCategory.WEAPON) continue;
                equipableType = (WeaponType)equipable.getType();
                if (equipableType != weaponType) continue;
                if (start > 0) {
                    start--;
                    continue;
                }
                boolean added = false;
                for (int i = 0; i < list.size(); i++) {
                    if (equipable.getLevel() <= list.get(i).getLevel()) {
                        list.add(i, equipable);
                        added = true;
                        break;
                    }
                }
                if (added == false)
                    list.add(equipable);
                if (list.size() == BIG_SLOT - 1)
                    break;
            }
        }
        else if (itemCustomType.getCategory() == ItemCustomCategory.ARMOR) {
            ArmorType  armorType = (ArmorType)itemCustomType;
            ArmorType  equipableType = null;
            for (Equipable<?> equipable: equipables.values()) {
                if (equipable.getType().getCategory() != ItemCustomCategory.ARMOR) continue;
                equipableType = (ArmorType)equipable.getType();
                if (equipableType != armorType) continue;
                if (start > 0) {
                    start--;
                    continue;
                }
                boolean added = false;
                for (int i = 0; i < list.size(); i++) {
                    if (equipable.getLevel() <= list.get(i).getLevel()) {
                        list.add(i, equipable);
                        added = true;
                        break;
                    }
                }
                if (added == false)
                    list.add(equipable);
                if (list.size() == BIG_SLOT - 1)
                    break;
            }
        }
        return list;
    }

    // armor only
    private List<Equipable<?>> getEquipablesList(ItemCustomManager itemCustomManager, Material material, int start) {
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        List<Equipable<?>>          list = new ArrayList<>();
        ArmorType                   equipableType = null;
        for (Equipable<?> equipable: equipables.values()) {
            if (equipable.getType().getCategory() != ItemCustomCategory.ARMOR) continue;
            equipableType = (ArmorType)equipable.getType();
            if (equipableType.getMaterial() != material) continue;
            if (start > 0) {
                start--;
                continue;
            }
            boolean added = false;
            for (int i = 0; i < list.size(); i++) {
                if (equipable.getLevel() <= list.get(i).getLevel()) {
                    list.add(i, equipable);
                    added = true;
                    break;
                }
            }
            if (added == false)
                list.add(equipable);
            if (list.size() == BIG_SLOT - 1)
                break;
        }
        return list;
    }

    public void openClawsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Claws"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.CLAW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSwordsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Swords"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.SWORD, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openAxesMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Axes"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.AXE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openPickaxesMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Pickaxes"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.PICKAXE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openHoesMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Hoes"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.HOE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openShovelsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Shovels"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.SHOVEL, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openMacesMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Maces"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.MACE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openBowsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Bows"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.BOW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openCrossbowsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Crossbows"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.CROSSBOW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openStaffsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Staffs"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.STAFF, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellbooksMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Spellbooks"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.SPELLBOOK, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openShieldsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Shields"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, WeaponType.SHIELD, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openHelmetsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Helmets"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, Material.NETHERITE_HELMET, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openChestplatesMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Chestplates"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, Material.NETHERITE_CHESTPLATE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openLeggingsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Leggings"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, Material.NETHERITE_LEGGINGS, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openBootsMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Boots"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, Material.NETHERITE_BOOTS, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openElytrasMenu(ItemCustomManager itemCustomManager, int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Elytras"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(itemCustomManager, ArmorType.ELYTRA, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        sender.getPlayer().openInventory(inv);
    }

    private ItemStack createEquipable(Equipable<?> equipable) {
        ItemStack               item = equipable.getItemClone();
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, "get_equipable");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createClass(ClassType classType) {
        ItemStack               item = new ItemStack(Material.PAPER);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, "print_" + classType.getName());
        meta.lore(Lore.classType(classType));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSpell(Spell spell) {
        ItemStack               item = spell.getItemClone();
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, "get_spell");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSlot(Material mat, String name, String action) {
        ItemStack				item = new ItemStack(mat);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Component.text(name).decoration(TextDecoration.BOLD, false));
        Data.setString(pdc, KEY_MENU, action);
        if (action.equals("print_stats_primary")) {
            Print   print = isPlayer() ? new Print(targetPlayer) : new Print(targetMob, entityModifierManager);
            meta.lore(print.printStatPrimary());
        }
        else if (action.equals("print_stats_secondary")) {
            Print   print = isPlayer() ? new Print(targetPlayer) : new Print(targetMob, entityModifierManager);
            meta.lore(print.printStatSecondary());
        }
        else if (action.equals("print_skills_primary")) {
            Print   print = isPlayer() ? new Print(targetPlayer) : new Print(targetMob, entityModifierManager);
            meta.lore(print.printSkillPrimary());
        }
        else if (action.equals("print_skills_secondary")) {
            Print   print = isPlayer() ? new Print(targetPlayer) : new Print(targetMob, entityModifierManager);
            meta.lore(print.printSkillSecondary());
        }
        else if (action.startsWith("print_claw"))
            meta.setCustomModelData(164);
        else if (action.startsWith("print_staff"))
            meta.setCustomModelData(74);
        else if (action.startsWith("print_spellbook"))
            meta.setCustomModelData(103);
        else if (action.equals("print_race") && isPlayer())
            meta.lore(Lore.raceType(targetPlayer.getRaceType()));
        else if (action.equals("print_class") && isPlayer())
            meta.lore(Lore.classType(targetPlayer.getClassType()));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBack(String action) {
        ItemStack				item = new ItemStack(Material.ARROW);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Component.text("Back").decoration(TextDecoration.BOLD, false));
        Data.setString(pdc, KEY_MENU, action);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isOffline() {
        return isPlayer() ? Bukkit.getPlayer(targetPlayer.getPlayer().getUniqueId()) == null : Bukkit.getEntity(targetMob.getUniqueId()) == null;
    }

    public boolean isPlayer() {
        return (targetPlayer != null);
    }

    public PlayerCustom getSender() {
        return this.sender;
    }

    public PlayerCustom getTargetPlayer() {
        return this.targetPlayer;
    }

    public LivingEntity getTargetMob() {
        return this.targetMob;
    }

    static public String getAction(ItemStack item) {
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        return Data.getString(pdc, Menu.getKeyMenu());
    }

    static public NamespacedKey getKeyMenu() {
        return KEY_MENU;
    }
}
