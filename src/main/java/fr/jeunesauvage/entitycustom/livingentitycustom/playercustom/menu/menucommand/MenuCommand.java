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
            // race
            case "change_race" -> openRaceChange();
            case "change_tauren" -> raceChange(RaceType.TAUREN);
            case "change_orc" -> raceChange(RaceType.ORC);
            case "change_dwarf" -> raceChange(RaceType.DWARF);
            case "change_human" -> raceChange(RaceType.HUMAN);
            case "change_dwarfiron" -> raceChange(RaceType.DWARFIRON);
            case "change_iron_guard" -> raceChange(RaceType.IRON_GUARD);
            case "change_elfnight" -> raceChange(RaceType.ELFNIGHT);
            case "change_elfblood" -> raceChange(RaceType.ELFBLOOD);
            case "change_murloc" -> raceChange(RaceType.MURLOC);
            case "change_necromancer" -> raceChange(RaceType.NECROMANCER);
            // class
            case "change_class" -> openClassChange();
            case "change_beggar" -> classChange(ClassType.BEGGAR);
            case "change_pyromancer" -> classChange(ClassType.PYROMANCER);
            case "change_priest" -> classChange(ClassType.PRIEST);
            case "change_rogue" -> classChange(ClassType.ROGUE);
            case "change_hunter" -> classChange(ClassType.HUNTER);
            case "change_dracthyr" -> classChange(ClassType.DRACTHYR);
            case "change_warrior" -> classChange(ClassType.WARRIOR);
            case "change_god" -> classChange(ClassType.GOD);
            // spell
            case "open_pyromancer" -> openSpell(ClassType.PYROMANCER);
            case "open_priest" -> openSpell(ClassType.PRIEST);
            case "open_rogue" -> openSpell(ClassType.ROGUE);
            case "open_hunter" -> openSpell(ClassType.HUNTER);
            case "open_dracthyr" -> openSpell(ClassType.DRACTHYR);
            case "open_warrior" -> openSpell(ClassType.WARRIOR);
            // team
            case "open_team" -> openTeam();
            case "add_team" -> openTeamAdd();
	        case "add_player" -> teamAdd(TeamType.PLAYER);
	        case "add_horde" -> teamAdd(TeamType.HORDE);
	        case "add_alliance" -> teamAdd(TeamType.ALLIANCE);
	        case "add_murloc" -> teamAdd(TeamType.MURLOC);
	        case "add_desert" -> teamAdd(TeamType.DESERT);
	        case "add_black" -> teamAdd(TeamType.BLACK);
	        case "add_forest" -> teamAdd(TeamType.FOREST);
	        case "add_ice" -> teamAdd(TeamType.ICE);
	        case "add_necro" -> teamAdd(TeamType.NECRO);
	        case "add_spider" -> teamAdd(TeamType.SPIDER);
	        case "add_elemental" -> teamAdd(TeamType.ELEMENTAL);
	        case "add_demon" -> teamAdd(TeamType.DEMON);
            case "delete_team" -> openTeamDelete();
	        case "delete_player" -> teamDelete(TeamType.PLAYER);
	        case "delete_horde" -> teamDelete(TeamType.HORDE);
	        case "delete_alliance" -> teamDelete(TeamType.ALLIANCE);
	        case "delete_murloc" -> teamDelete(TeamType.MURLOC);
	        case "delete_desert" -> teamDelete(TeamType.DESERT);
	        case "delete_black" -> teamDelete(TeamType.BLACK);
	        case "delete_forest" -> teamDelete(TeamType.FOREST);
	        case "delete_ice" -> teamDelete(TeamType.ICE);
	        case "delete_necro" -> teamDelete(TeamType.NECRO);
	        case "delete_spider" -> teamDelete(TeamType.SPIDER);
	        case "delete_elemental" -> teamDelete(TeamType.ELEMENTAL);
	        case "delete_demon" -> teamDelete(TeamType.DEMON);
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
            case "print_stats_primary", "print_stats_secondary",
                "print_skills_primary", "print_skills_secondary",
                "print_race", "print_class", "print_team" -> {}
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
    }

    public void openRaceChange() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_race"));
        int i = 0;
        for (RaceType raceType: RaceType.values()) {
            if (!raceType.isPlayable()) continue;
            inventory.setItem(i++, createSlot(Material.PHANTOM_SPAWN_EGG, raceType.getName(), "change_" + raceType.getName()));
        }
    }

    public void raceChange(RaceType raceType) {
        target.setRaceType(raceType);
        launcher.sendMessage(Message.c("Race Modified!", NamedTextColor.GREEN));
        if (target != launcher && target instanceof PlayerCustom playerCustom)
            playerCustom.sendMessage(Message.c("Race Modified!", NamedTextColor.GREEN));
        openRaceChange();
    }

    public void openClass() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.BLAZE_ROD, "Print", "print_class"));
        if (launcher.isOp()) {
            inventory.setItem(1, createSlot(Material.PAPER, "Change", "change_class"));
            inventory.setItem(2, createSlot(Material.BLAZE_POWDER, "Spell", "open_spell"));
        }
    }

    public void openClassChange() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_class"));
        int i = 0;
        for (ClassType classType: ClassType.values()) {
            inventory.setItem(i++, createSlot(Material.PAPER, classType.getName(), "change_" + classType.getName()));
        }
    }

    public void classChange(ClassType classType) {
        target.setClassType(classType);
        launcher.sendMessage(Message.c("Class Modified!", NamedTextColor.GREEN));
        if (target != launcher && target instanceof PlayerCustom playerCustom)
            playerCustom.sendMessage(Message.c("Class Modified!", NamedTextColor.GREEN));
        openClassChange();
    }

    public void openSpell() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_class"));
        int i = 0;
        for (ClassType classType: ClassType.values()) {
            if (classType == ClassType.BEGGAR || classType == ClassType.GOD) continue;
            inventory.setItem(i++, createSpellClass(classType));
        }
    }

    public void openSpell(ClassType classType) {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_spell"));
        List<Spell>  spells = getSpellList(classType);
        int         i = 0;
        Spell       last = null;
        for (Spell spell: spells) {
            if (last != null && spell.getType() != last.getType()) i += (i % 9 != 0 ? 9 - i % 9 : 0);
            inventory.setItem(i++, createSpell(spell));
            last = spell;
        }
    }

    private List<Spell> getSpellList(ClassType classType) {
        List<Spell> list = new ArrayList<>();
        for (Spell spell: RpgCraft.getItemCustomRegistry().getSpells().values()) {
            if (!spell.getType().getClassTypes().contains(classType)) continue;
            list.add(spell);
        }
        list.sort(Comparator.comparing((Spell spell) -> spell.getType())
            .thenComparing(spell -> spell.getRarity()));
        return list;
    }

    public void openTeam() {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open"));
        inventory.setItem(0, createSlot(Material.BOOK, "Print", "print_team"));
        if (launcher.isOp()) {
            inventory.setItem(1, createSlot(Material.PAPER, "Add Team", "add_team"));
            inventory.setItem(2, createSlot(Material.PAPER, "Delete Team", "delete_team"));
        }
    }

    public void openTeamAdd() {
        inventory.setItem(BACK_SLOT, createBack("open_team"));
        int i = 0;
        for (TeamType teamType: TeamType.values()) {
            inventory.setItem(i++, createSlot(Material.PAPER, teamType.getName(), "add_" + teamType.getName()));
        }
    }

    public void teamAdd(TeamType teamType) {
        target.addTeam(teamType);
        launcher.sendMessage(Message.c("Team Added!", NamedTextColor.GREEN));
        if (target != launcher && target instanceof PlayerCustom playerCustom)
            playerCustom.sendMessage(Message.c("Team Added!", NamedTextColor.GREEN));
    }

    public void openTeamDelete() {
        inventory.setItem(BACK_SLOT, createBack("open_team"));
        int i = 0;
        for (TeamType teamType: TeamType.values()) {
            inventory.setItem(i++, createSlot(Material.PAPER, teamType.getName(), "delete_" + teamType.getName()));
        }
    }

    public void teamDelete(TeamType teamType) {
        target.deleteTeam(teamType);
        launcher.sendMessage(Message.c("Team Deleted!", NamedTextColor.YELLOW));
        if (target != launcher && target instanceof PlayerCustom playerCustom)
            playerCustom.sendMessage(Message.c("Team Deleted!", NamedTextColor.YELLOW));
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
    }

    public void openPotions(PotionType potionType) {
        clearInventory();
        inventory.setItem(BACK_SLOT, createBack("open_potions"));
        List<Potion>  potions = getPotionsList(potionType);
        int i = 0;
        for (Potion potion: potions) {
            inventory.setItem(i++, createPotion(potion));
        }
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    boolean     isBoss = text[0].equals("true");
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeBoss(launcher, npcName, isBoss);
                    openNPC();
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().changeEquipement(launcher, npcName, launcher.getInventory().getItemInMainHand());
                    openNPC();
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 3);
                    String      action = text[0];
                    TeamType    teamType = TeamType.fromString(text[1]);
                    String      npcName = String.join(" ", Arrays.copyOfRange(text, 2, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeTeam(launcher, npcName, action, teamType);
                    openNPC();
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    openNPC();
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String[]    text = stateSnapshot.getText().toLowerCase().split(" ", 2);
                    TemplateType    templateType = TemplateType.fromString(text[0]);
                    String          npcName = String.join(" ", Arrays.copyOfRange(text, 1, text.length));
                    RpgCraft.getNPCBuilderRegistry().changeTemplate(launcher, npcName, templateType);
                    openNPC();
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().spawn(launcher, npcName);
                    openNPC();
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().despawn(launcher, npcName);
                    openNPC();
                    openInventory();
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
                    openInventory();
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                }
                else if (slot == AnvilGUI.Slot.OUTPUT) {
                    String      text = stateSnapshot.getText().toLowerCase();
                    String      npcName = text;
                    RpgCraft.getNPCBuilderRegistry().delete(launcher, npcName);
                    openNPC();
                    openInventory();
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

    private void openInventory() {
        launcher.openInventory(inventory);
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
