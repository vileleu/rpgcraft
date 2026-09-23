package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menucommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.Character;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
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
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.MenuHolder;
import fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.ParseAction;
import fr.jeunesauvage.entitycustom.livingentitycustom.racecustom.RaceType;
import fr.jeunesauvage.entitycustom.livingentitycustom.team.TeamType;
import fr.jeunesauvage.itemcustom.ItemCustomCategory;
import fr.jeunesauvage.itemcustom.ItemCustomType;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorMaterial;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorType;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.food.Food;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.potion.PotionType;
import fr.jeunesauvage.itemcustom.spell.Spell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;

public class MenuCommand implements MenuHolder {
    private final PlayerCustom          launcher;
    private final LivingEntityCustom    target;
    private Inventory                   inventory = null;

    public MenuCommand(PlayerCustom launcher, LivingEntityCustom target) {
        this.launcher = launcher;
        this.target = target;
        open();
    }

    @Override
    public void open() {
        this.inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu"));
        inventory.setItem(BACK_SLOT, createBack("close"));
        inventory.setItem(10, createSlot(Material.FLETCHING_TABLE, "Stats", "open_stats"));
        inventory.setItem(11, createSlot(Material.CRAFTING_TABLE, "Skills", "open_skills"));
        inventory.setItem(12, createSlot(Material.PHANTOM_SPAWN_EGG, "Race", "open_race"));
        inventory.setItem(13, createSlot(Material.BLAZE_ROD, "Class", "open_class"));
        inventory.setItem(14, createSlot(Material.WRITTEN_BOOK, "Team", "open_team"));
        if (launcher.isOp()) {
            inventory.setItem(15, createSlot(Material.IRON_SWORD, "Items", "open_items"));
            inventory.setItem(16, createSlot(Material.POTION, "Potions", "open_potions"));
            inventory.setItem(19, createSlot(Material.COOKED_BEEF, "Foods", "open_foods"));
            inventory.setItem(20, createSlot(Material.PUFFERFISH, "NPC", "open_npc"));
        }
        launcher.openInventory(inventory);
    }

    @Override
    public void close() {
        launcher.closeInventory();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        ItemStack   clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        String  action = MenuHolder.getAction(clicked);
        if (action == null) return;
        ParseAction parseAction = new ParseAction(action);
        parseAction.parse();
        switch (parseAction.getResult()) {
            case "back_main" -> open();
            case "back_stats" -> openStats();
            case "back_skills" -> openSkills();
            case "back_class" -> openClass();
            case "back_spell" -> openSpell();
            case "back_items" -> openItems();
            case "back_potions" -> openPotions();
            case "get" -> launcher.addItem(RpgCraft.getItemCustomRegistry().getClone(clicked));
            // stats + skills
            case "open_stats" -> openStats();
            case "open_skills" -> openSkills();
            case "add_stat" -> openStatsAdd();
            case "remove_stat" -> openStatsRemove();
            case "add_skill" -> openSkillsAdd();
            case "remove_skill" -> openSkillsRemove();
            // race + class
            case "open_race" -> openRace();
            case "open_class" -> openClass();
            case "change_race" -> openRaceChange();
            case "change_class" -> openClassChange();
            case "open_spell" -> openSpell();
            case "open_pyromancer" -> openSpellPyromancer();
            case "open_warrior" -> openSpellWarrior();
            case "open_rogue" -> openSpellRogue();
            case "open_priest" -> openSpellPriest();
            case "open_dracthyr" -> openSpellDracthyr();
            case "open_hunter" -> openSpellHunter();
            case "open_team" -> openTeam();
            case "add_team" -> openTeamAdd();
            case "delete_team" -> openTeamDelete();
            // npc
            case "get_placer_npc" -> RpgCraft.getNPCBuilderRegistry().createMyNPCPlacer(launcher);
            case "open_npc" -> openNPC();
            case "create_npc" -> openCreateNPC();
            case "patrol_npc" -> openPatrolNPC();
            case "aggro_npc" -> openAggroNPC();
            case "level_npc" -> openLevelNPC();
            case "chase_npc" -> openChaseNPC();
            case "boss_npc" -> openBossNPC();
            case "equip_npc" -> openEquipNPC();
            case "team_npc" -> openTeamNPC();
            case "drop_npc" -> openDropNPC();
            case "template_npc" -> openTemplateNPC();
            case "spawn_npc" -> openSpawnNPC();
            case "despawn_npc" -> openDespawnNPC();
            case "delete_npc" -> openDeleteNPC();
            // items
            case "open_items" -> openItems();
            case "open_claw" -> openClawsMenu(parseAction.getStart());
            case "open_sword" -> openSwordsMenu(parseAction.getStart());
            case "open_axe" -> openAxesMenu(parseAction.getStart());
            case "open_pickaxe" -> openPickaxesMenu(parseAction.getStart());
            case "open_hoe" -> openHoesMenu(parseAction.getStart());
            case "open_shovel" -> openShovelsMenu(parseAction.getStart());
            case "open_mace" -> openMacesMenu(parseAction.getStart());
            case "open_bow" -> openBowsMenu(parseAction.getStart());
            case "open_crossbow" -> openCrossbowsMenu(parseAction.getStart());
            case "open_staff" -> openStaffsMenu(parseAction.getStart());
            case "open_spellbook" -> openSpellbooksMenu(parseAction.getStart());
            case "open_shield" -> openShieldsMenu(parseAction.getStart());
            case "open_head" -> openHelmetsMenu(parseAction.getStart());
            case "open_chest" -> openChestplatesMenu(parseAction.getStart());
            case "open_legs" -> openLeggingsMenu(parseAction.getStart());
            case "open_feet" -> openBootsMenu(parseAction.getStart());
            case "open_elytra" -> openElytrasMenu(parseAction.getStart());
            // potions
            case "open_potions" -> openPotions();
            case "open_potion_health" -> openPotionsHealth();
            case "open_potion_mana" -> openPotionsMana();
            case "open_potion_rage" -> openPotionsRage();
            case "open_potion_energy" -> openPotionsEnergy();
            // foods
            case "open_foods" -> openFoods();
            default -> close();
        }
    }

    @Override
    public void onClose() {
        RpgCraft.getEntityCustomRegistry().deleteMenu(this);
    }

    @Override 
    public void giveBackItems() {}

    public void openStats() {
        inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Stats"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        inventory.setItem(11, createSlot(Material.GLOW_INK_SAC, "Print Primary", "print_stats_primary"));
        inventory.setItem(12, createSlot(Material.INK_SAC, "Print Secondary", "print_stats_secondary"));
        if (launcher.isOp()) {
            inventory.setItem(13, createSlot(Material.GREEN_DYE, "Add", "add_stat"));
            inventory.setItem(14, createSlot(Material.RED_DYE, "Remove", "remove_stat"));
        }
        launcher.openInventory(inventory);
    }

    public void openStatsAdd() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Stat Add")
            .text("stat value duration")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openStats();
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
                    openStats();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openStatsRemove() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Stat Remove")
            .text("id")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openStats();
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
                    openStats();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openSkills() {
        inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Skills"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        inventory.setItem(11, createSlot(Material.GLOW_INK_SAC, "Print Primary", "print_skills_primary"));
        inventory.setItem(12, createSlot(Material.INK_SAC, "Print Secondary", "print_skills_secondary"));
        if (launcher.isOp()) {
            inventory.setItem(13, createSlot(Material.GREEN_DYE, "Add", "add_skill"));
            inventory.setItem(14, createSlot(Material.RED_DYE, "Remove", "remove_skill"));
        }
        launcher.openInventory(inventory);
    }

    public void openSkillsAdd() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Skill Add")
            .text("skill value duration")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openSkills();
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
                    openSkills();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openSkillsRemove() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Skill Remove")
            .text("id")
            .itemLeft(new ItemStack(Material.ARROW))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openSkills();
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
                    openSkills();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openRace() {
        inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Race"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        inventory.setItem(11, createSlot(Material.PHANTOM_SPAWN_EGG, "Print", "print_race"));
        if (launcher.isOp()) 
            inventory.setItem(12, createSlot(Material.PAPER, "Change", "change_race"));
        launcher.openInventory(inventory);
    }

    public void openRaceChange() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Race Change")
            .text("race")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openRace();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String  text = stateSnapshot.getText().toLowerCase();
                    target.setRaceType(RaceType.fromString(text));
                    launcher.sendMessage(Message.c("Race Modified!", NamedTextColor.GREEN));
                    openRace();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openClass() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Class"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        inventory.setItem(11, createSlot(Material.BLAZE_ROD, "Print", "print_class"));
        if (launcher.isOp()) {
            inventory.setItem(12, createSlot(Material.PAPER, "Change", "change_class"));
            inventory.setItem(13, createSlot(Material.BLAZE_POWDER, "Spell", "open_spell"));
        }
        launcher.openInventory(inventory);
    }

    public void openClassChange() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Class Change")
            .text("class")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openClass();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String  text = stateSnapshot.getText().toLowerCase();
                    target.setClassType(ClassType.fromString(text));
                    launcher.sendMessage(Message.c("Class Modified!", NamedTextColor.GREEN));
                    openClass();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openSpell() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Spell"));
        inventory.setItem(BACK_SLOT, createBack("back_class"));
        int i = 11;
        for (ClassType classType: ClassType.values()) {
            if (classType == ClassType.BEGGAR || classType == ClassType.GOD) continue;
            inventory.setItem(i, createClass(classType));
            i++;
        }
        launcher.openInventory(inventory);
    }

    public void openSpellPyromancer() {
        Inventory   inventory = Bukkit.createInventory(this, 36, Component.text("Menu Spell"));
        inventory.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.PYROMANCER)) continue;
            inventory.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inventory);
    }

    public void openSpellWarrior() {
        Inventory   inventory = Bukkit.createInventory(this, 36, Component.text("Menu Spell"));
        inventory.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.WARRIOR)) continue;
            inventory.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inventory);
    }

    public void openSpellRogue() {
        Inventory   inventory = Bukkit.createInventory(this, 36, Component.text("Menu Spell"));
        inventory.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.ROGUE)) continue;
            inventory.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inventory);
    }

    public void openSpellPriest() {
        Inventory   inventory = Bukkit.createInventory(this, 36, Component.text("Menu Spell"));
        inventory.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.PRIEST)) continue;
            inventory.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inventory);
    }

    public void openSpellDracthyr() {
        Inventory   inventory = Bukkit.createInventory(this, 36, Component.text("Menu Spell"));
        inventory.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.DRACTHYR)) continue;
            inventory.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inventory);
    }

    public void openSpellHunter() {
        Inventory   inventory = Bukkit.createInventory(this, 36, Component.text("Menu Spell"));
        inventory.setItem(BACK_SLOT, createBack("back_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(ClassType.HUNTER)) continue;
            inventory.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inventory);
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

    public void openTeam() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Team"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        inventory.setItem(11, createSlot(Material.BOOK, "Print", "print_team"));
        if (launcher.isOp()) {
            inventory.setItem(12, createSlot(Material.PAPER, "Add Team", "add_team"));
            inventory.setItem(13, createSlot(Material.PAPER, "Delete Team", "delete_team"));
        }
        launcher.openInventory(inventory);
    }

    public void openTeamAdd() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Team Add")
            .text("team")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openTeam();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    TeamType    teamType = TeamType.fromString(text);
                    if (teamType == null) return Collections.emptyList();
                    target.addTeam(teamType);
                    launcher.sendMessage(Message.c("Team Added!", NamedTextColor.GREEN));
                    openTeam();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openTeamDelete() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Team Delete")
            .text("team")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openTeam();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String  text = stateSnapshot.getText().toLowerCase();
                    TeamType    teamType = TeamType.fromString(text);
                    if (teamType == null) return Collections.emptyList();
                    target.deleteTeam(teamType);
                    launcher.sendMessage(Message.c("Team Deleted!", NamedTextColor.YELLOW));
                    openTeam();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openItems() {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Items"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        int i = 9;
        for (WeaponType weaponType: WeaponType.values()) {
            if (weaponType == WeaponType.HAND || weaponType == WeaponType.UNKNOWN) continue;
            String  name = weaponType.getName();
            int     count = getEquipablesCount(weaponType);
            int     nb = 0;
            while (count > 0) {
                if (i >= BIG_SLOT) return;
                inventory.setItem(i++, createSlot(weaponType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1), "open_" + name + (nb > 0 ? nb : "")));
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
                inventory.setItem(i++, createSlot(armorType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1), "open_" + name + (nb > 0 ? nb : "")));
                nb++;
                count -= BIG_SLOT - 1;
            }
        }
        launcher.openInventory(inventory);
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
        return list;
    }

    public void openClawsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Claws"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.CLAW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openSwordsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Swords"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SWORD, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openAxesMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Axes"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.AXE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openPickaxesMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Pickaxes"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.PICKAXE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openHoesMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Hoes"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.HOE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openShovelsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Shovels"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SHOVEL, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openMacesMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Maces"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.MACE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openBowsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Bows"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.BOW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openCrossbowsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Crossbows"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.CROSSBOW, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openStaffsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Staffs"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.STAFF, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openSpellbooksMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Spellbooks"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SPELLBOOK, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openShieldsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Shields"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(WeaponType.SHIELD, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openHelmetsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Helmets"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_HELMET, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openChestplatesMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Chestplates"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_CHESTPLATE, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openLeggingsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Leggings"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_LEGGINGS, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openBootsMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Boots"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(Material.NETHERITE_BOOTS, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openElytrasMenu(int start) {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu Elytras"));
        inventory.setItem(BACK_SLOT, createBack("back_items"));
        List<Equipable<?>>  equipables = getEquipablesList(ArmorType.ELYTRA, start);
        int                 i = 1;
        for (Equipable<?> equipable: equipables) {
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    public void openPotions() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Potions"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        int i = 10;
        for (PotionType potionType: PotionType.values()) {
            String  name = potionType.getName();
            inventory.setItem(i++, createSlot(potionType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1).replaceAll("_", " "), "open_" + name));
        }
        launcher.openInventory(inventory);
    }

    public void openPotionsHealth() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Potions"));
        inventory.setItem(BACK_SLOT, createBack("back_potions"));
        List<Potion>  potions = getPotionsList(PotionType.POTION_HEALTH);
        int i = 10;
        for (Potion potion: potions) {
            inventory.setItem(i++, createPotion(potion));
        }
        launcher.openInventory(inventory);
    }

    public void openPotionsMana() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Potions"));
        inventory.setItem(BACK_SLOT, createBack("back_potions"));
        List<Potion>  potions = getPotionsList(PotionType.POTION_MANA);
        int i = 10;
        for (Potion potion: potions) {
            inventory.setItem(i++, createPotion(potion));
        }
        launcher.openInventory(inventory);
    }

    public void openPotionsRage() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Potions"));
        inventory.setItem(BACK_SLOT, createBack("back_potions"));
        List<Potion>  potions = getPotionsList(PotionType.POTION_RAGE);
        int i = 10;
        for (Potion potion: potions) {
            inventory.setItem(i++, createPotion(potion));
        }
        launcher.openInventory(inventory);
    }

    public void openPotionsEnergy() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Potions"));
        inventory.setItem(BACK_SLOT, createBack("back_potions"));
        List<Potion>  potions = getPotionsList(PotionType.POTION_ENERGY);
        int i = 10;
        for (Potion potion: potions) {
            inventory.setItem(i++, createPotion(potion));
        }
        launcher.openInventory(inventory);
    }

    private List<Potion> getPotionsList(PotionType potionType) {
        List<Potion>    list = new ArrayList<>();
        for (Potion potion: RpgCraft.getItemCustomRegistry().getPotions().values()) {
            if (potion.getType() != potionType) continue;
            boolean added = false;
            for (int i = 0; i < list.size(); i++) {
                if (potion.getLevel() <= list.get(i).getLevel()) {
                    list.add(i, potion);
                    added = true;
                    break;
                }
            }
            if (added == false)
                list.add(potion);
        }
        return list;
    }

    public void openFoods() {
        Inventory   inventory = Bukkit.createInventory(this, SMALL_SLOT, Component.text("Menu Foods"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        Collection<Food>  foods = RpgCraft.getItemCustomRegistry().getFoods().values();
        int i = 9;
        for (Food food: foods) {
            inventory.setItem(i++, createFood(food));
        }
        launcher.openInventory(inventory);
    }

    public void openNPC() {
        Inventory   inventory = Bukkit.createInventory(this, BIG_SLOT, Component.text("Menu NPC"));
        inventory.setItem(BACK_SLOT, createBack("back_main"));
        inventory.setItem(10, createSlot(Material.PUFFERFISH, "Get Placer", "get_placer_npc"));
        inventory.setItem(11, createSlot(Material.SPAWNER, "Create NPC", "create_npc"));
        inventory.setItem(12, createSlot(Material.EXPERIENCE_BOTTLE, "Change Level", "level_npc"));
        inventory.setItem(13, createSlot(Material.STONE_STAIRS, "Change Patrol", "patrol_npc"));
        inventory.setItem(14, createSlot(Material.SPYGLASS, "Change Aggro", "aggro_npc"));
        inventory.setItem(15, createSlot(Material.STICK, "Change Chase", "chase_npc"));
        inventory.setItem(16, createSlot(Material.DRAGON_EGG, "Change Boss", "boss_npc"));
        inventory.setItem(19, createSlot(Material.IRON_CHESTPLATE, "Change Equip", "equip_npc"));
        inventory.setItem(20, createSlot(Material.RED_BANNER, "Change Team", "team_npc"));
        inventory.setItem(21, createSlot(Material.BREAD, "Change Drop", "drop_npc"));
        inventory.setItem(22, createSlot(Material.ELDER_GUARDIAN_SPAWN_EGG, "Change Template", "template_npc"));
        inventory.setItem(23, createSlot(Material.MELON_SLICE, "Spawn", "spawn_npc"));
        inventory.setItem(24, createSlot(Material.RED_BED, "Despawn", "despawn_npc"));
        inventory.setItem(25, createSlot(Material.DARK_OAK_DOOR, "Delete", "delete_npc"));
        launcher.openInventory(inventory);
    }

    public void openCreateNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Create NPC")
            .text("templatetype levelMin levelMax")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
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
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openLevelNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Level NPC")
            .text("level npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
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
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openPatrolNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Patrol NPC")
            .text("patrolrange npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
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
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openAggroNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Aggro NPC")
            .text("aggrorange npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
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
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openChaseNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Chase NPC")
            .text("chaserange npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
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
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openBossNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Boss NPC")
            .text("isboss npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    boolean     isBoss = text[0].equals("true");
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeBoss(launcher, npcName, isBoss);
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openEquipNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Equip NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().changeEquipement(launcher, npcName, launcher.getInventory().getItemInMainHand());
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openTeamNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Team NPC")
            .text("action team npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 3);
                    String      action = text[0];
                    TeamType    teamType = TeamType.fromString(text[1]);
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 2, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeTeam(launcher, npcName, action, teamType);
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openDropNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Drop NPC")
            .text("drop npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openTemplateNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Template NPC")
            .text("templatetype npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    TemplateType    templateType = TemplateType.fromString(text[0]);
                    String          npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeTemplate(launcher, npcName, templateType);
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openSpawnNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Spawn NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().spawn(launcher, npcName);
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openDespawnNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Despawn NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().despawn(launcher, npcName);
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                return Collections.emptyList();
            })
        .open(launcher.getPlayer());
    }

    public void openDeleteNPC() {
        new AnvilGUI.Builder()
            .plugin(RpgCraft.instance())
            .title("Menu Delete NPC")
            .text("npcname")
            .itemLeft(new ItemStack(Material.PAPER))
            .onClick((slot, stateSnapshot) -> {
                if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                    openNPC();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().delete(launcher, npcName);
                    openNPC();
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
        Data.setString(pdc, KEY_MENU, "get");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPotion(Potion potion) {
        ItemStack               item = potion.getItemClone();
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, "get");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createFood(Food food) {
        ItemStack               item = food.getItemClone();
        ItemMeta				meta = item.getItemMeta();
		PersistentDataContainer	pdc = meta.getPersistentDataContainer();
        Data.setString(pdc, KEY_MENU, "get");
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
        Data.setString(pdc, KEY_MENU, "get");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSlot(Material mat, String name, String action) {
        ItemStack				item = new ItemStack(mat);
        ItemMeta				meta = item.getItemMeta();
        if (mat == Material.POTION) {
            PotionType  potionType = PotionType.fromString(action.substring(action.indexOf('_') + 1));
            if (potionType != null) {
                PotionMeta  potionMeta = (PotionMeta)meta;
                potionMeta.setColor(potionType.getColor());
            }
        }
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
}
