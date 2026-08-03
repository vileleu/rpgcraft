package fr.jeunesauvage.entity.playercustom.menu;

import java.util.Collections;
import java.util.Map;

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
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorType;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.spell.Spell;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.wesjd.anvilgui.AnvilGUI;

public class Menu {
	static public NamespacedKey         KEY_MENU = new NamespacedKey(RpgCraft.name(), "menuid");
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
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu"));
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
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Stats"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_main"));
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
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Skills"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_main"));
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
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Race"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_main"));
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
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Class"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_main"));
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
        Inventory   inv = Bukkit.createInventory(holder, 27, Component.text("Menu Spell"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_class"));
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
        inv.setItem(0, createBack("back_spell"));
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
        inv.setItem(0, createBack("back_spell"));
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
        inv.setItem(0, createBack("back_spell"));
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
        inv.setItem(0, createBack("back_spell"));
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
        inv.setItem(0, createBack("back_spell"));
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
        inv.setItem(0, createBack("back_spell"));
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

    public void openItemsMenu() {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 36, Component.text("Menu Items"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_main"));
        inv.setItem(9, createSlot(Material.NETHERITE_SWORD, "Claw", "print_claws"));
        inv.setItem(10, createSlot(Material.IRON_SWORD, "Sword", "print_swords"));
        inv.setItem(11, createSlot(Material.IRON_SWORD, "Sword2", "print_swords2"));
        inv.setItem(12, createSlot(Material.IRON_AXE, "Axe", "print_axes"));
        inv.setItem(13, createSlot(Material.IRON_PICKAXE, "Pickaxe", "print_pickaxes"));
        inv.setItem(14, createSlot(Material.IRON_HOE, "Hoe", "print_hoes"));
        inv.setItem(15, createSlot(Material.IRON_SHOVEL, "Shovel", "print_shovels"));
        inv.setItem(16, createSlot(Material.MACE, "Mace", "print_maces"));
        inv.setItem(17, createSlot(Material.BOW, "Bow", "print_bows"));
        inv.setItem(18, createSlot(Material.CROSSBOW, "Crossbow", "print_crossbows"));
        inv.setItem(19, createSlot(Material.BOW, "Staff", "print_staffs"));
        inv.setItem(20, createSlot(Material.CROSSBOW, "Spellbook", "print_spellbooks"));
        inv.setItem(21, createSlot(Material.SHIELD, "Shield", "print_shields"));
        inv.setItem(22, createSlot(Material.IRON_HELMET, "Helmet", "print_helmets"));
        inv.setItem(23, createSlot(Material.IRON_HELMET, "Helmet2", "print_helmets2"));
        inv.setItem(24, createSlot(Material.IRON_HELMET, "Helmet3", "print_helmets3"));
        inv.setItem(25, createSlot(Material.IRON_HELMET, "Helmet4", "print_helmets4"));
        inv.setItem(26, createSlot(Material.IRON_CHESTPLATE, "Chestplate", "print_chestplates"));
        inv.setItem(27, createSlot(Material.IRON_CHESTPLATE, "Chestplate2", "print_chestplates2"));
        inv.setItem(28, createSlot(Material.IRON_CHESTPLATE, "Chestplate3", "print_chestplates3"));
        inv.setItem(29, createSlot(Material.IRON_LEGGINGS, "Leggings", "print_leggings"));
        inv.setItem(30, createSlot(Material.IRON_LEGGINGS, "Leggings2", "print_leggings2"));
        inv.setItem(31, createSlot(Material.IRON_BOOTS, "Boots", "print_boots"));
        inv.setItem(32, createSlot(Material.IRON_BOOTS, "Boots2", "print_boots2"));
        inv.setItem(33, createSlot(Material.ELYTRA, "Elytra", "print_elytras"));
        sender.getPlayer().openInventory(inv);
    }

    public void openClawsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Claws"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.CLAW) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSwordsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Swords"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.SWORD) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSwords2Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Swords"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.SWORD) {
                    if (skip) {
                        if (i == 53) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openAxesMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Axes"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.AXE) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openPickaxesMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Pickaxes"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.PICKAXE) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openHoesMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Hoes"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.HOE) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openShovelsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Shovels"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.SHOVEL) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openMacesMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Maces"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.MACE) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openBowsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Bows"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.BOW) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openCrossbowsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Crossbows"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
              ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.CROSSBOW) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openStaffsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Staffs"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.STAFF) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openSpellbooksMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Spellbooks"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.SPELLBOOK) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openShieldsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Shields"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.WEAPON) {
                WeaponType  weaponType = (WeaponType)type;
                if (weaponType == WeaponType.SHIELD) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openHelmetsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Helmets"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("head")) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openHelmets2Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Helmets"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("head")) {
                    if (skip) {
                        if (i == 53) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openHelmets3Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Helmets"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("head")) {
                    if (skip) {
                        if (i == 106) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openHelmets4Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Helmets"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("head")) {
                    if (skip) {
                        if (i == 159) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openChestplatesMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Chestplates"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("chest")) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openChestplates2Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Chestplates"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("chest")) {
                    if (skip) {
                        if (i == 53) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openChestplates3Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Chestplates"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("chest")) {
                    if (skip) {
                        if (i == 106) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openLeggingsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Leggings"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("legs")) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openLeggings2Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Leggings"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("legs")) {
                    if (skip) {
                        if (i == 53) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openBootsMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Boots"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("feet")) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openBoots2Menu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Boots"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        boolean                     skip = true;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType.getName().endsWith("feet")) {
                    if (skip) {
                        if (i == 53) {
                            i = 0;
                            skip = false;
                        }
                        i++;
                        continue;
                    }
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
        }
        sender.getPlayer().openInventory(inv);
    }

    public void openElytrasMenu(ItemCustomManager itemCustomManager) {
        MenuHolder  holder = new MenuHolder();
        Inventory   inv = Bukkit.createInventory(holder, 54, Component.text("Menu Elytras"));
        holder.setInventory(inv);
        inv.setItem(0, createBack("back_items"));
        Map<String, Equipable<?>>   equipables = itemCustomManager.getEquipables();
        int                         i = 1;
        for (Equipable<?> equipable: equipables.values()) {
            ItemCustomType  type = equipable.getType();
            if (type.getCategory() == ItemCustomCategory.ARMOR) {
                ArmorType  armorType = (ArmorType)type;
                if (armorType == ArmorType.ELYTRA) {
                    inv.setItem(i, createEquipable(equipable));
                    i++;
                }
            }
            if (i == 54) break;
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
        else if (action.equals("print_claws"))
            meta.setCustomModelData(164);
        else if (action.equals("print_staffs"))
            meta.setCustomModelData(74);
        else if (action.equals("print_spellbooks"))
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
