package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.Character;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import fr.jeunesauvage.Data;
import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.component.Lore;
import fr.jeunesauvage.component.Message;
import fr.jeunesauvage.entitycustom.livingentitycustom.LivingEntityCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.PlayerCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.PrintAttributeCustom;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.skill.SkillType;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatType;
import fr.jeunesauvage.entitycustom.livingentitycustom.classcustom.ClassType;
import fr.jeunesauvage.entitycustom.livingentitycustom.npccustom.template.TemplateType;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
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
import net.wesjd.anvilgui.AnvilGUI;

public class Menu {
	static public final NamespacedKey   KEY_MENU = new NamespacedKey(RpgCraft.name(), "menuid");
	static private final int            BACK_SLOT = 0;
	static private final int            SMALL_SLOT = 27;
	static public final int             BIG_SLOT = 54;
    private final PlayerCustom          launcher;
    private final LivingEntityCustom    target;

    public Menu(PlayerCustom launcher, LivingEntityCustom target) {
        this.launcher = launcher;
        this.target = target;
        openMainMenu();
    }

    public void openMainMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu"));
        holder.setInventory(inv);
        inv.setItem(10, createSlot(Material.FLETCHING_TABLE, "Stats", "open_stats"));
        inv.setItem(11, createSlot(Material.CRAFTING_TABLE, "Skills", "open_skills"));
        inv.setItem(12, createSlot(Material.PHANTOM_SPAWN_EGG, "Race", "open_race"));
        inv.setItem(13, createSlot(Material.BLAZE_ROD, "Class", "open_class"));
        inv.setItem(14, createSlot(Material.WRITTEN_BOOK, "Team", "open_team"));
        inv.setItem(15, createSlot(Material.IRON_SWORD, "Items", "open_items"));
        inv.setItem(16, createSlot(Material.PUFFERFISH, "NPC", "open_npc"));
        launcher.openInventory(inv);
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
        launcher.openInventory(inv);
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
                    if (statType == null || value == 0) return Collections.emptyList();
                    target.addStatModifier(statType, value, duration);
                    launcher.sendMessage(Message.c("Stat Added!", NamedTextColor.GREEN));
                    openStatsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
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
                    target.deleteModifier(id);
                    launcher.sendMessage(Message.c("Stat Deleted!", NamedTextColor.RED));
                    openStatsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
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
        launcher.openInventory(inv);
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
                    if (skillType == null || value == 0) return Collections.emptyList();
                    target.addSkillModifier(skillType, value, duration);
                    launcher.sendMessage(Message.c("Skill Added!", NamedTextColor.GREEN));
                    openSkillsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
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
                    target.deleteModifier(id);
                    launcher.sendMessage(Message.c("Skill Removed!", NamedTextColor.RED));
                    openSkillsMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openRaceMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Race"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(11, createSlot(Material.PHANTOM_SPAWN_EGG, "Print", "print_race"));
        inv.setItem(12, createSlot(Material.PAPER, "Change", "change_race"));
        launcher.openInventory(inv);
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
                    target.setRaceType(RaceType.fromString(text));
                    launcher.sendMessage(Message.c("Race Modified!", NamedTextColor.GREEN));
                    openRaceMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openClassMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Class"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(11, createSlot(Material.BLAZE_ROD, "Print", "print_class"));
        inv.setItem(12, createSlot(Material.PAPER, "Change", "change_class"));
        inv.setItem(13, createSlot(Material.BLAZE_POWDER, "Spell", "open_spell"));
        launcher.openInventory(inv);
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
                    target.setClassType(ClassType.fromString(text));
                    launcher.sendMessage(Message.c("Class Modified!", NamedTextColor.GREEN));
                    openClassMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
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
        launcher.openInventory(inv);
    }

    public void openSpellPyromancerMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.PYROMANCER)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inv);
    }

    public void openSpellWarriorMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.WARRIOR)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inv);
    }

    public void openSpellRogueMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.ROGUE)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inv);
    }

    public void openSpellPriestMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.PRIEST)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inv);
    }

    public void openSpellDracthyrMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.DRACTHYR)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inv);
    }

    public void openSpellHunterMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.HUNTER)) continue;
            inv.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inv);
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

    public void openTeamMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, SMALL_SLOT, Component.text("Menu Team"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(11, createSlot(Material.BOOK, "Print", "print_team"));
        inv.setItem(12, createSlot(Material.PAPER, "Add Team", "add_team"));
        inv.setItem(13, createSlot(Material.PAPER, "Delete Team", "delete_team"));
        launcher.openInventory(inv);
    }

    public void openTeamAddMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Team Add")
            .text("team")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openTeamMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    TeamType    teamType = TeamType.fromString(text);
                    if (teamType == null) return Collections.emptyList();
                    target.addTeam(teamType);
                    launcher.sendMessage(Message.c("Team Added!", NamedTextColor.GREEN));
                    openTeamMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openTeamDeleteMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Team Delete")
            .text("team")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openTeamMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String  text = stateSnapshot.getText().toLowerCase();
                    TeamType    teamType = TeamType.fromString(text);
                    if (teamType == null) return Collections.emptyList();
                    target.deleteTeam(teamType);
                    launcher.sendMessage(Message.c("Team Deleted!", NamedTextColor.YELLOW));
                    openTeamMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openItemsMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Items"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        int i = 9;
        for (WeaponType weaponType: WeaponType.values()) {
            if (weaponType == WeaponType.HAND || weaponType == WeaponType.UNKNOWN) continue;
            String  name = weaponType.getName();
            int     count = getEquipablesCount(weaponType);
            int     nb = 0;
            while (count > 0) {
                if (i >= BIG_SLOT) return;
                inv.setItem(i++, createSlot(weaponType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1), "open_" + name + (nb > 0 ? nb : "")));
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
            int     count = getEquipablesCount(armorType.getMaterial());
            int     nb = 0;
            while (count > 0) {
                if (i >= BIG_SLOT) return;
                inv.setItem(i++, createSlot(armorType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1), "open_" + name + (nb > 0 ? nb : "")));
                nb++;
                count -= BIG_SLOT - 1;
            }
        }
        launcher.openInventory(inv);
    }

    private int getEquipablesCount(ItemCustomType itemCustomType) {
        Map<String, Equipable<?>>   equipables = RpgCraft.getItemCustomRegistry().getEquipables();
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
    private int getEquipablesCount(Material material) {
        Map<String, Equipable<?>>   equipables = RpgCraft.getItemCustomRegistry().getEquipables();
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

    private List<Equipable<?>> getEquipablesList(ItemCustomType itemCustomType, int start) {
        Map<String, Equipable<?>>   equipables = RpgCraft.getItemCustomRegistry().getEquipables();
        List<Equipable<?>>          list = new ArrayList<>();
        if (itemCustomType.getCategory() == ItemCustomCategory.WEAPON) {
            WeaponType  weaponType = (WeaponType)itemCustomType;
            WeaponType  equipableType = null;
            for (Equipable<?> equipable: equipables.values()) {
                if (equipable.getType().getCategory() != ItemCustomCategory.WEAPON) continue;
                equipableType = (WeaponType)equipable.getType();
                if (equipableType != weaponType) continue;
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
            }
        }
        else if (itemCustomType.getCategory() == ItemCustomCategory.ARMOR) {
            ArmorType  armorType = (ArmorType)itemCustomType;
            ArmorType  equipableType = null;
            for (Equipable<?> equipable: equipables.values()) {
                if (equipable.getType().getCategory() != ItemCustomCategory.ARMOR) continue;
                equipableType = (ArmorType)equipable.getType();
                if (equipableType != armorType) continue;
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
            }
        }
        if (list.size() > BIG_SLOT - 1) list = new ArrayList<>(list.subList(start, Math.min(start + BIG_SLOT - 1, list.size())));
        RpgCraft.debug("list.size() = " + list.size());
        return list;
    }

    // armor only
    private List<Equipable<?>> getEquipablesList(Material material, int start) {
        Map<String, Equipable<?>>   equipables = RpgCraft.getItemCustomRegistry().getEquipables();
        List<Equipable<?>>          list = new ArrayList<>();
        ArmorType                   equipableType = null;
        for (Equipable<?> equipable: equipables.values()) {
            if (equipable.getType().getCategory() != ItemCustomCategory.ARMOR) continue;
            equipableType = (ArmorType)equipable.getType();
            if (equipableType.getMaterial() != material) continue;
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
        }
        if (list.size() > BIG_SLOT - 1) list = new ArrayList<>(list.subList(start, Math.min(start + BIG_SLOT - 1, list.size())));
        RpgCraft.debug("list.size() = " + list.size());
        return list;
    }

    public void openClawsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Claws"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.CLAW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openSwordsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Swords"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SWORD, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openAxesMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Axes"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.AXE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openPickaxesMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Pickaxes"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.PICKAXE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openHoesMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Hoes"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.HOE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openShovelsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Shovels"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SHOVEL, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openMacesMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Maces"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.MACE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openBowsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Bows"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.BOW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openCrossbowsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Crossbows"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.CROSSBOW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openStaffsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Staffs"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.STAFF, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openSpellbooksMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Spellbooks"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SPELLBOOK, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openShieldsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Shields"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SHIELD, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openHelmetsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Helmets"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_HELMET, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openChestplatesMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Chestplates"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_CHESTPLATE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openLeggingsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Leggings"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_LEGGINGS, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openBootsMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Boots"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_BOOTS, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openElytrasMenu(int start) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu Elytras"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(ArmorType.ELYTRA, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inv.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inv);
    }

    public void openNPCMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, BIG_SLOT, Component.text("Menu NPC"));
        holder.setInventory(inv);
        inv.setItem(BACK_SLOT, createBack("back_main"));
        inv.setItem(10, createSlot(Material.PUFFERFISH, "Get Placer", "get_placer_npc"));
        inv.setItem(11, createSlot(Material.SPAWNER, "Create NPC", "create_npc"));
        inv.setItem(12, createSlot(Material.EXPERIENCE_BOTTLE, "Change Level", "level_npc"));
        inv.setItem(13, createSlot(Material.STONE_STAIRS, "Change Patrol", "patrol_npc"));
        inv.setItem(14, createSlot(Material.SPYGLASS, "Change Aggro", "aggro_npc"));
        inv.setItem(15, createSlot(Material.STICK, "Change Chase", "chase_npc"));
        inv.setItem(16, createSlot(Material.DRAGON_EGG, "Change Boss", "boss_npc"));
        inv.setItem(19, createSlot(Material.IRON_CHESTPLATE, "Change Equip", "equip_npc"));
        inv.setItem(20, createSlot(Material.RED_BANNER, "Change Team", "team_npc"));
        inv.setItem(21, createSlot(Material.BREAD, "Change Drop", "drop_npc"));
        inv.setItem(22, createSlot(Material.ELDER_GUARDIAN_SPAWN_EGG, "Change Template", "template_npc"));
        inv.setItem(23, createSlot(Material.MELON_SLICE, "Spawn", "spawn_npc"));
        inv.setItem(24, createSlot(Material.RED_BED, "Despawn", "despawn_npc"));
        inv.setItem(25, createSlot(Material.DARK_OAK_DOOR, "Delete", "delete_npc"));
        launcher.openInventory(inv);
    }

    public void openCreateNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Create NPC")
            .text("templatetype levelMin levelMax")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ");
                    if (text.length != 2 && text.length != 3) return Collections.emptyList();
                    int levelMin;
                    int levelMax;
                    try {
                        levelMin = Integer.parseInt(text[1]);
                        if (text.length == 3) levelMax = Integer.parseInt(text[2]);
                        else levelMax = levelMin;
                    }
                    catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    if (levelMax < levelMin) return Collections.emptyList();
                    TemplateType    templateType = TemplateType.fromString(text[0]);
                    RpgCraft.getNPCBuilderRegistry().createMyNPC(launcher, templateType, levelMin, levelMax);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openLevelNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Level NPC")
            .text("level npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    int level;
                    try {
                        level = Integer.parseInt(text[0]);
                    }
                    catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeLevel(launcher, npcName, level);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openPatrolNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Patrol NPC")
            .text("patrolrange npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    int patrolRange;
                    try {
                        patrolRange = Integer.parseInt(text[0]);
                    }
                    catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changePatrolRange(launcher, npcName, patrolRange);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openAggroNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Aggro NPC")
            .text("aggrorange npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    int aggroRange;
                    try {
                        aggroRange = Integer.parseInt(text[0]);
                    }
                    catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeAggroRange(launcher, npcName, aggroRange);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openChaseNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Chase NPC")
            .text("chaserange npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    int chaseRange;
                    try {
                        chaseRange = Integer.parseInt(text[0]);
                    }
                    catch (NumberFormatException e) {
                        return Collections.emptyList();
                    }
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeChaseRange(launcher, npcName, chaseRange);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openBossNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Boss NPC")
            .text("isboss npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    boolean     isBoss = text[0].equals("true");
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeBoss(launcher, npcName, isBoss);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openEquipNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Equip NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().changeEquipement(launcher, npcName, launcher.getInventory().getItemInMainHand());
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openTeamNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Team NPC")
            .text("action team npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 3);
                    String      action = text[0];
                    TeamType    teamType = TeamType.fromString(text[1]);
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 2, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeTeam(launcher, npcName, action, teamType);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openDropNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Drop NPC")
            .text("drop npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openTemplateNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Template NPC")
            .text("templatetype npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    TemplateType    templateType = TemplateType.fromString(text[0]);
                    String          npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeTemplate(launcher, npcName, templateType);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openSpawnNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Spawn NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().spawn(launcher, npcName);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openDespawnNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Despawn NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().despawn(launcher, npcName);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openDeleteNPCMenu() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Delete NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().delete(launcher, npcName);
                    openNPCMenu();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
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
        Data.setString(pdc, KEY_MENU, "open_" + classType.getName());
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
        meta.displayName(Message.c(Component.text(name)));
        Data.setString(pdc, KEY_MENU, action);
        if (action.equals("print_stats_primary"))
            meta.lore(new PrintAttributeCustom(target).printStatPrimary());
        else if (action.equals("print_stats_secondary"))
            meta.lore(new PrintAttributeCustom(target).printStatSecondary());
        else if (action.equals("print_skills_primary"))
            meta.lore(new PrintAttributeCustom(target).printSkillPrimary());
        else if (action.equals("print_skills_secondary"))
            meta.lore(new PrintAttributeCustom(target).printSkillSecondary());
        else if (action.startsWith("print_team"))
            meta.lore(Lore.team(target.getTeams()));
        else if (action.startsWith("open_claw"))
            meta.setCustomModelData(164);
        else if (action.startsWith("open_staff"))
            meta.setCustomModelData(74);
        else if (action.startsWith("open_spellbook"))
            meta.setCustomModelData(103);
        else if (action.equals("print_race"))
            meta.lore(Lore.raceType(target.getRaceType()));
        else if (action.equals("print_class"))
            meta.lore(Lore.classType(target.getClassType()));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBack(String action) {
        ItemStack				item = new ItemStack(Material.ARROW);
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        meta.displayName(Message.c(Component.text("Back")));
        Data.setString(pdc, KEY_MENU, action);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isPresent() {
        return target.isPresent();
    }

    public PlayerCustom getlauncher() {
        return this.launcher;
    }

    public LivingEntityCustom getTarget() {
        return this.target;
    }

    static public String getAction(ItemStack item) {
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        return Data.getString(pdc, Menu.getKeyMenu());
    }

    static public NamespacedKey getKeyMenu() {
        return KEY_MENU;
    }
}
