package fr.jeunesauvage.entitycustom.livingentitycustom.playercustom.menu.menucommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.Character;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
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
import fr.jeunesauvage.itemcustom.PotionType;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.equipable.ArmorMaterial;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;
import fr.jeunesauvage.itemcustom.equipable.WeaponMaterial;
import fr.jeunesauvage.itemcustom.food.Food;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.spell.Spell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.wesjd.anvilgui.AnvilGUI;

public class MenuCommand extends MenuHolder {
    private final LivingEntityCustom    target;

    public MenuCommand(PlayerCustom launcher, LivingEntityCustom target) {
        super(launcher);
        this.target = target;
        this.inventory = Bukkit.createInventory(this, INVENTORY_SIZE, Component.text("Menu Command"));
        open();
    }

    @Override
    public void open() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("close"));
        inventory.setItem(0, createSlot(Material.FLETCHING_TABLE, "Stats", "open_stats"));
        inventory.setItem(1, createSlot(Material.CRAFTING_TABLE, "Skills", "open_skills"));
        inventory.setItem(2, createSlot(Material.PHANTOM_SPAWN_EGG, "Race", "open_race"));
        inventory.setItem(3, createSlot(Material.BLAZE_ROD, "Class", "open_class"));
        inventory.setItem(4, createSlot(Material.WRITTEN_BOOK, "Team", "open_team"));
        if (launcher.isOp()) {
            inventory.setItem(5, createSlot(Material.IRON_SWORD, "Items", "open_items"));
            inventory.setItem(6, createSlot(Material.POTION, "Potions", "open_potions"));
            inventory.setItem(7, createSlot(Material.COOKED_BEEF, "Foods", "open_foods"));
            inventory.setItem(8, createSlot(Material.PUFFERFISH, "NPC", "open_npc"));
        }
        launcher.openInventory(inventory);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        e.setCancelled(true);
        ItemStack   clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        String  action = getAction(clicked);
        if (action == null) return;
        ParseAction parseAction = new ParseAction(action);
        parseAction.parse();
        switch (parseAction.getResult()) {
            case "open" -> open();
            case "open_stats" -> openStats();
            case "open_skills" -> openSkills();
            case "open_race" -> openRace();
            case "open_class" -> openClass();
            case "open_spell" -> openSpell();
            case "open_items" -> openItems();
            case "open_potions" -> openPotions();
            case "get" -> launcher.addItem(RpgCraft.getItemCustomRegistry().getClone(clicked));
            // stats + skills
            case "add_stat" -> openStatsAdd();
            case "remove_stat" -> openStatsRemove();
            case "add_skill" -> openSkillsAdd();
            case "remove_skill" -> openSkillsRemove();
            // race + class
            case "change_race" -> openRaceChange();
            case "change_class" -> openClassChange();
            case "open_pyromancer" -> openSpell(ClassType.PYROMANCER);
            case "open_warrior" -> openSpell(ClassType.WARRIOR);
            case "open_rogue" -> openSpell(ClassType.ROGUE);
            case "open_priest" -> openSpell(ClassType.PRIEST);
            case "open_dracthyr" -> openSpell(ClassType.DRACTHYR);
            case "open_hunter" -> openSpell(ClassType.HUNTER);
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
            case "open_claw" -> openItems(WeaponMaterial.CLAW, parseAction.getStart());
            case "open_sword" -> openItems(WeaponMaterial.SWORD, parseAction.getStart());
            case "open_axe" -> openItems(WeaponMaterial.AXE, parseAction.getStart());
            case "open_pickaxe" -> openItems(WeaponMaterial.PICKAXE, parseAction.getStart());
            case "open_hoe" -> openItems(WeaponMaterial.HOE, parseAction.getStart());
            case "open_shovel" -> openItems(WeaponMaterial.SHOVEL, parseAction.getStart());
            case "open_mace" -> openItems(WeaponMaterial.MACE, parseAction.getStart());
            case "open_bow" -> openItems(WeaponMaterial.BOW, parseAction.getStart());
            case "open_crossbow" -> openItems(WeaponMaterial.CROSSBOW, parseAction.getStart());
            case "open_staff" -> openItems(WeaponMaterial.STAFF, parseAction.getStart());
            case "open_spellbook" -> openItems(WeaponMaterial.SPELLBOOK, parseAction.getStart());
            case "open_shield" -> openItems(WeaponMaterial.SHIELD, parseAction.getStart());
            case "open_cloth" -> openItems(ArmorMaterial.CLOTH, parseAction.getStart());
            case "open_leather" -> openItems(ArmorMaterial.LEATHER, parseAction.getStart());
            case "open_mail" -> openItems(ArmorMaterial.MAIL, parseAction.getStart());
            case "open_plate" -> openItems(ArmorMaterial.PLATE, parseAction.getStart());
            case "open_elytra" -> openItems(ArmorMaterial.ELYTRA, parseAction.getStart());
            // potions
            case "open_potion_health" -> openPotions(PotionType.POTION_HEALTH);
            case "open_potion_mana" -> openPotions(PotionType.POTION_MANA);
            case "open_potion_rage" -> openPotions(PotionType.POTION_RAGE);
            case "open_potion_energy" -> openPotions(PotionType.POTION_ENERGY);
            // foods
            case "open_foods" -> openFoods();
            default -> close();
        }
    }

    @Override
    public void onClose() {
        RpgCraft.getEntityCustomRegistry().deleteMenu(this);
    }

    public void openStats() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.GLOW_INK_SAC, "Print Primary", "print_stats_primary"));
        inventory.setItem(1, createSlot(Material.INK_SAC, "Print Secondary", "print_stats_secondary"));
        if (launcher.isOp()) {
            inventory.setItem(2, createSlot(Material.GREEN_DYE, "Add", "add_stat"));
            inventory.setItem(3, createSlot(Material.RED_DYE, "Remove", "remove_stat"));
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
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.GLOW_INK_SAC, "Print Primary", "print_skills_primary"));
        inventory.setItem(1, createSlot(Material.INK_SAC, "Print Secondary", "print_skills_secondary"));
        if (launcher.isOp()) {
            inventory.setItem(2, createSlot(Material.GREEN_DYE, "Add", "add_skill"));
            inventory.setItem(3, createSlot(Material.RED_DYE, "Remove", "remove_skill"));
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
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.PHANTOM_SPAWN_EGG, "Print", "print_race"));
        if (launcher.isOp()) 
            inventory.setItem(1, createSlot(Material.PAPER, "Change", "change_race"));
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
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.BLAZE_ROD, "Print", "print_class"));
        if (launcher.isOp()) {
            inventory.setItem(1, createSlot(Material.PAPER, "Change", "change_class"));
            inventory.setItem(2, createSlot(Material.BLAZE_POWDER, "Spell", "open_spell"));
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
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_class"));
        int i = 0;
        for (ClassType classType: ClassType.values()) {
            if (classType == ClassType.BEGGAR || classType == ClassType.GOD) continue;
            inventory.setItem(i, createSpellClass(classType));
            i++;
        }
        launcher.openInventory(inventory);
    }

    public void openSpell(ClassType classType) {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_spell"));
        Map<String, Spell>  spells = RpgCraft.getItemCustomRegistry().getSpells();
        for (Spell spell: spells.values()) {
            if (!spell.getType().getClassTypes().contains(classType)) continue;
            inventory.setItem(getSlotSpell(spell.getRarity(), spell.getLevel()), createSpell(spell));
        }
        launcher.openInventory(inventory);
    }

    private int getSlotSpell(Rarity rarity, int level) {
        return switch (rarity) {
            case POOR -> switch (level) {
                case 10 -> 11;
                case 15 -> 20;
                case 20 -> 29;
                case 25 -> 38;
                default -> INVENTORY_SIZE - 1;
            };
            case COMMON -> switch (level) {
                case 15 -> 12;
                case 20 -> 21;
                case 25 -> 30;
                case 30 -> 39;
                default -> INVENTORY_SIZE - 1;
            };
            case UNCOMMON -> switch (level) {
                case 20 -> 13;
                case 25 -> 22;
                case 30 -> 31;
                case 35 -> 40;
                default -> INVENTORY_SIZE - 1;
            };
            case RARE -> switch (level) {
                case 25 -> 14;
                case 30 -> 23;
                case 35 -> 32;
                case 40 -> 41;
                default -> INVENTORY_SIZE - 1;
            };
            case EPIC -> switch (level) {
                case 30 -> 15;
                case 35 -> 24;
                case 40 -> 33;
                case 45 -> 42;
                default -> INVENTORY_SIZE - 1;
            };
            case LEGENDARY -> switch (level) {
                case 35 -> 16;
                case 40 -> 25;
                case 45 -> 34;
                case 50 -> 43;
                default -> INVENTORY_SIZE - 1;
            };
        };
    }

    public void openTeam() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.BOOK, "Print", "print_team"));
        if (launcher.isOp()) {
            inventory.setItem(1, createSlot(Material.PAPER, "Add Team", "add_team"));
            inventory.setItem(2, createSlot(Material.PAPER, "Delete Team", "delete_team"));
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
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        List<EquipableMaterial>  equipablesMaterial = getEquipableTypeList();
        int i = 0;
        for (EquipableMaterial equipableMaterial: equipablesMaterial) {
            String  name = equipableMaterial.getName();
            int     count = getEquipablesCount(equipableMaterial);
            int     nb = 0;
            while (count > 0) {
                if (i > inventory.getSize()) return;
                inventory.setItem(i++, createSlot(equipableMaterial.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1), "open_" + name + (nb > 0 ? nb : "")));
                nb++;
                count -= INVENTORY_SIZE - 1;
            }
        }
        launcher.openInventory(inventory);
    }

    private List<EquipableMaterial> getEquipableTypeList() {
        Set<EquipableMaterial> tmp = new HashSet<>();
        for (Equipable<?> equipable: RpgCraft.getItemCustomRegistry().getEquipables().values()) {
            tmp.add(equipable.getType().getEquipableMaterial());
        }
        List<EquipableMaterial> list = new ArrayList<>(tmp);
        list.sort(Comparator.comparingInt(m -> (m instanceof WeaponMaterial) ? 0 : 1));
        return list;
    }

    private int getEquipablesCount(EquipableMaterial equipableMaterial) {
        Map<String, Equipable<?>>   equipables = RpgCraft.getItemCustomRegistry().getEquipables();
        int                         count = 0;
        for (Equipable<?> equipable: equipables.values()) {
            if (equipable.getType().getEquipableMaterial() != equipableMaterial) continue;
            count++;
        }
        return count;
    }

    public void openItems(EquipableMaterial equipableMaterial, int start) {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_items"));
        List<Equipable<?>>  equipables = getEquipablesList(equipableMaterial, start);
        int i = 0;
        for (Equipable<?> equipable: equipables) {
            if (i == BACK_SLOT) break;
            inventory.setItem(i++, createEquipable(equipable));
        }
        launcher.openInventory(inventory);
    }

    private List<Equipable<?>> getEquipablesList(EquipableMaterial equipableMaterial, int start) {
        List<Equipable<?>>    list = new ArrayList<>();
        for (Equipable<?> equipable: RpgCraft.getItemCustomRegistry().getEquipables().values()) {
            if (equipable.getType().getEquipableMaterial() != equipableMaterial) continue;
            list.add(equipable);
        }
        list.sort(Comparator.comparing((Equipable<?> equipable) -> equipable.getRarity())
            .thenComparingInt(equipable -> equipable.getLevel()));
        if (list.size() > INVENTORY_SIZE - 1) list = new ArrayList<>(list.subList(start, Math.min(start + INVENTORY_SIZE - 1, list.size())));
        return list;
    }

    public void openPotions() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        int i = 0;
        for (PotionType potionType: PotionType.values()) {
            String  name = potionType.getName();
            inventory.setItem(i++, createSlot(potionType.getMaterial(), Character.toUpperCase(name.charAt(0)) + name.substring(1).replaceAll("_", " "), "open_" + name));
        }
        launcher.openInventory(inventory);
    }

    public void openPotions(PotionType potionType) {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_potions"));
        List<Potion>  potions = getPotionsList(potionType);
        int i = 0;
        for (Potion potion: potions) {
            inventory.setItem(i++, createPotion(potion));
        }
        launcher.openInventory(inventory);
    }

    private List<Potion> getPotionsList(PotionType potionType) {
        List<Potion>    list = new ArrayList<>();
        for (Potion potion: RpgCraft.getItemCustomRegistry().getPotions().values()) {
            if (potion.getType() != potionType) continue;
            list.add(potion);
        }
        list.sort(Comparator.comparing(potion -> potion.getRarity()));
        return list;
    }

    public void openFoods() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        List<Food>  foods = getFoodsList();
        int i = 0;
        for (Food food: foods) {
            inventory.setItem(i++, createFood(food));
        }
        launcher.openInventory(inventory);
    }

    private List<Food> getFoodsList() {
        List<Food>    list = new ArrayList<>(RpgCraft.getItemCustomRegistry().getFoods().values());
        list.sort(Comparator.comparing(food -> food.getType()));
        return list;
    }

    public void openNPC() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.PUFFERFISH, "Get Placer", "get_placer_npc"));
        inventory.setItem(1, createSlot(Material.SPAWNER, "Create NPC", "create_npc"));
        inventory.setItem(2, createSlot(Material.EXPERIENCE_BOTTLE, "Change Level", "level_npc"));
        inventory.setItem(3, createSlot(Material.STONE_STAIRS, "Change Patrol", "patrol_npc"));
        inventory.setItem(4, createSlot(Material.SPYGLASS, "Change Aggro", "aggro_npc"));
        inventory.setItem(5, createSlot(Material.STICK, "Change Chase", "chase_npc"));
        inventory.setItem(6, createSlot(Material.DRAGON_EGG, "Change Boss", "boss_npc"));
        inventory.setItem(7, createSlot(Material.IRON_CHESTPLATE, "Change Equip", "equip_npc"));
        inventory.setItem(8, createSlot(Material.RED_BANNER, "Change Team", "team_npc"));
        inventory.setItem(9, createSlot(Material.BREAD, "Change Drop", "drop_npc"));
        inventory.setItem(10, createSlot(Material.ELDER_GUARDIAN_SPAWN_EGG, "Change Template", "template_npc"));
        inventory.setItem(11, createSlot(Material.MELON_SLICE, "Spawn", "spawn_npc"));
        inventory.setItem(12, createSlot(Material.RED_BED, "Despawn", "despawn_npc"));
        inventory.setItem(13, createSlot(Material.DARK_OAK_DOOR, "Delete", "delete_npc"));
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

    private ItemStack createSpellClass(ClassType classType) {
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
