package fr.jeunesauvage.itemcustom.itembuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.consumable.Consumable;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorMaterial;
import fr.jeunesauvage.itemcustom.equipable.armor.ArmorType;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponMaterial;
import fr.jeunesauvage.itemcustom.equipable.weapon.WeaponType;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.potion.PotionType;
import fr.jeunesauvage.itemcustom.spell.Spell;
import fr.jeunesauvage.itemcustom.spell.SpellType;
import fr.jeunesauvage.itemcustom.usable.Usable;

public class ItemBuilder {
	private static final String                 EQUIPABLE_FILE = "equipableSet.json";
	private static final String                 EQUIPABLESTATS_FILE = "equipableSetStats.json";
	private static final String                 LAUNCHER_FILE = "equipable.json";
	private static final String                 LAUNCHERSTATS_FILE = "equipableStats.json";
    private static final Set<Material>          MATERIALS;
    private Map<Material, Map<String, Integer>> itemsSetJson;
    private Map<String, EquipableStat>	        itemsSetStatsJson;
    private Map<Material, Map<String, Integer>> itemsJson;
    private Map<String, EquipableStat>	        itemsStatsJson;
    private final Map<String, ItemCustom<?>>    items = new HashMap<>();

    static {
        MATERIALS = Set.of(
            Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE,
            Material.NETHERITE_LEGGINGS,
            Material.NETHERITE_BOOTS,
            Material.NETHERITE_SWORD,
            Material.NETHERITE_AXE,
            Material.NETHERITE_PICKAXE,
            Material.NETHERITE_SHOVEL,
            Material.NETHERITE_HOE,
            Material.MACE,
            Material.BOW,
            Material.CROSSBOW,
            Material.SHIELD,
            Material.ELYTRA
        );
    }

    public ItemBuilder() {
        try {
            itemsSetJson = loadEquipableSet();
            itemsSetStatsJson = loadEquipableSetStats();
            itemsJson = loadEquipable();
            itemsStatsJson = loadEquipableStats();
        }
        catch (Exception e) {
            RpgCraft.debug("File JSON not found or invalid");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(RpgCraft.instance());
        }
        buildEquipableSet();
        buildEquipable();
        buildPotion();
        buildSpell();
    }

    // build Equipable (Armor + Weapon)

    private void buildEquipableSet() {
        // equipable
        Map<String, Integer>    mapBoots = itemsSetJson.get(Material.NETHERITE_BOOTS);
        for (String s: mapBoots.keySet()) {
            String  nameBoots = s.toLowerCase();
            if (!nameBoots.endsWith(" boots")) continue;
            String      nameSet = nameBoots.substring(0, nameBoots.length() - 6);
            for (Material material : MATERIALS) {
                Map<String, Integer>                map = itemsSetJson.get(material);
                Iterator<Entry<String, Integer>>    it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, Integer>  entry = it.next();
                    String                  name = entry.getKey().toLowerCase();
					if (!isInSet(nameSet, name)) continue;
                    name = name.replaceAll("[ ']", "_").replaceAll("[()]", "");
                    int             id = entry.getValue();
                    Equipable<?>    item = buildSet(material, nameSet, name, id);
                    if (item == null) continue;
                    items.put(name, item);
                    if (map != mapBoots)
                        it.remove();
                }
            }
        }
    }

    private void buildEquipable() {
        // staff
        Map<String, Integer>    mapStaff = itemsJson.get(Material.BOW);
        for (Entry<String, Integer> e: mapStaff.entrySet()) {
            String  nameStaff = e.getKey().toLowerCase();
            if (!nameStaff.startsWith("staff")) continue;
            String          nameSet = nameStaff.substring(6);
            nameStaff = nameStaff.replaceAll("[ ']", "_").replaceAll("[()]", "");  
            int             id = e.getValue();
            Equipable<?>    item = build(Material.BOW, nameSet, nameStaff, id);
            if (item == null) continue;
            items.put(nameStaff, item);
        }
        // spellbook
        Map<String, Integer>    mapSpellBook = itemsJson.get(Material.CROSSBOW);
        for (Entry<String, Integer> e: mapSpellBook.entrySet()) {
            String  nameSpellBook = e.getKey().toLowerCase();
            if (!nameSpellBook.startsWith("spellbook")) continue;
            String          nameSet = nameSpellBook.substring(10);
            nameSpellBook = nameSpellBook.replaceAll("[ ']", "_").replaceAll("[()]", "");          
            int             id = e.getValue();
            Equipable<?>    item = build(Material.CROSSBOW, nameSet, nameSpellBook, id);
            if (item == null) continue;
            items.put(nameSpellBook, item);
        }
        // claw
        Map<String, Integer>    mapClaw = itemsJson.get(Material.NETHERITE_SWORD);
        for (Entry<String, Integer> e: mapClaw.entrySet()) {
            String  nameClaw = e.getKey().toLowerCase();
            if (!nameClaw.startsWith("claw")) continue;
            String          nameSet = nameClaw.substring(5);
            nameClaw = nameClaw.replaceAll("[ ']", "_").replaceAll("[()]", "");          
            int             id = e.getValue();
            Equipable<?>    item = build(Material.NETHERITE_SWORD, nameSet, nameClaw, id);
            if (item == null) continue;
            items.put(nameClaw, item);
        }
    }

    // equipable set are created with armorMaterial only
    private Equipable<?> buildSet(Material material, String nameSet, String name, int id) {
        EquipableStat    equipableSetStat = itemsSetStatsJson.get(nameSet);
        if (equipableSetStat == null) return null;
        if (equipableSetStat.getEquipableMaterial() instanceof ArmorMaterial armorMaterial) {
            WeaponType  weaponType = getWeaponType(material);
            if (weaponType != null)
                return new Weapon(name, weaponType, equipableSetStat, id);
            ArmorType  armorType = getArmorType(armorMaterial, material);
            if (armorType != null)
                return new Armor(name, armorType, equipableSetStat, id);
        }
        return null;
    }

    private Equipable<?> build(Material material, String nameSet, String name, int id) {
        EquipableStat    equipableStat = itemsStatsJson.get(nameSet);
        if (equipableStat == null) return null;
        if (equipableStat.getEquipableMaterial() instanceof ArmorMaterial armorMaterial) {
            ArmorType  armorType = getArmorType(armorMaterial, material);
            if (armorType == null) return null;
            return new Armor(name, armorType, equipableStat, id);
        }
        else if (equipableStat.getEquipableMaterial() instanceof WeaponMaterial weaponMaterial) {
            WeaponType  weaponType = getWeaponType(weaponMaterial);
            if (weaponType == null) return null;
            return new Weapon(name, weaponType, equipableStat, id);
        }
        return null;
    }

    private ArmorType getArmorType(ArmorMaterial armorMaterial, Material material) {
        if (material == Material.ELYTRA)
            return ArmorType.ELYTRA;
        for (ArmorType type: ArmorType.values()) {
            if (type.getArmorMaterial() == armorMaterial && type.getMaterial() == material)
                return type;
        }
        return null;
    }

    private WeaponType getWeaponType(Material material) {
        if (material == Material.NETHERITE_SWORD) return WeaponType.SWORD;
        for (WeaponType type: WeaponType.values()) {
            if (type.getMaterial() == material)
                return type;
        }
        return null;
    }

    private WeaponType getWeaponType(WeaponMaterial weaponMaterial) {
        for (WeaponType type: WeaponType.values()) {
            if (type.getWeaponMaterial() == weaponMaterial)
                return type;
        }
        return null;
    }

	private boolean isInSet(String nameSet, String name) {
        if (name.equals(nameSet)) return true;
		int	index = name.lastIndexOf(' ');
		if (index == -1) return false;
        String  newName = name.substring(0, index);
        if (newName.endsWith(" hamaxe"))
            newName = newName.substring(0, newName.length() - 7);
        return nameSet.equals(newName);
	}

    // build Spell + Potion

    private void buildPotion() {
        for (PotionType type: PotionType.values()) {
            for (Rarity rarity: Rarity.values()) {
                String  name = type.getName() + "_" + rarity.getNumber();
                items.put(name, new Potion(type, name, rarity, type.getLevel(rarity)));
            }
        }
    }

    private void buildSpell() {
        for (SpellType type: SpellType.values()) {
            for (Rarity rarity: Rarity.values()) {
                String  name = type.getName() + "_" + rarity.getNumber();
                items.put(name, new Spell(type, name, rarity, type.getLevel(rarity)));
            }
        }
    }

    /*
    ** getter + setter
    */

    public Map<String, ItemCustom<?>> getItems() {
        return items;
    }

    public Map<String, Equipable<?>> getEquipable() {
        Map<String, Equipable<?>>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Equipable<?> equipable)
                result.put(entry.getKey(), equipable);
        }
        return result;
    }

    public Map<String, Armor> getArmor() {
        Map<String, Armor>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Armor armor)
                result.put(entry.getKey(), armor);
        }
        return result;
    }

    public Map<String, Weapon> getWeapon() {
        Map<String, Weapon>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Weapon weapon)
                result.put(entry.getKey(), weapon);
        }
        return result;
    }

    public Map<String, Potion> getPotion() {
        Map<String, Potion>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Potion potion)
                result.put(entry.getKey(), potion);
        }
        return result;
    }

    public Map<String, Spell> getSpell() {
        Map<String, Spell>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Spell potion)
                result.put(entry.getKey(), potion);
        }
        return result;
    }

    public Map<String, Usable> getUsable() {
        Map<String, Usable>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Usable potion)
                result.put(entry.getKey(), potion);
        }
        return result;
    }

    public Map<String, Consumable> getConsumable() {
        Map<String, Consumable>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Consumable potion)
                result.put(entry.getKey(), potion);
        }
        return result;
    }

    // loader (.json)

    private Map<Material, Map<String, Integer>> loadEquipableSet() throws Exception {
        Map<Material, Map<String, Integer>> result = new HashMap<>();
        Type                                type = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
        Gson                                gson = new Gson();
        InputStream                         stream = RpgCraft.instance().getResource(EQUIPABLE_FILE);
        if (stream == null) throw new FileNotFoundException("Resource " + EQUIPABLE_FILE + " not found");
        Reader                              reader = new InputStreamReader(stream);
        Map<String, Map<String, Integer>>   map = gson.fromJson(reader, type);
        for (Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            String		itemName = entry.getKey();
            Material	material = Material.matchMaterial(itemName);
            if (material == null) continue;
            result.put(material, entry.getValue());
        }
        return result;
    }

    private Map<Material, Map<String, Integer>> loadEquipable() throws Exception {
        Map<Material, Map<String, Integer>> result = new HashMap<>();
        Type                                type = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
        Gson                                gson = new Gson();
        InputStream stream = RpgCraft.instance().getResource(LAUNCHER_FILE);
        if (stream == null) throw new FileNotFoundException("Resource " + LAUNCHER_FILE + " not found");
        Reader                              reader = new InputStreamReader(stream);
        Map<String, Map<String, Integer>>   map = gson.fromJson(reader, type);
        for (Entry<String, Map<String, Integer>> entry : map.entrySet()) {
            String		itemName = entry.getKey();
            Material	material = Material.matchMaterial(itemName);
            if (material == null) continue;
            Map<String, Integer>    launcher = result.get(material);
            if (launcher != null)
                launcher.putAll(entry.getValue());
            else
                result.put(material, entry.getValue());
        }
        return result;
    }

    private Map<String, EquipableStat> loadEquipableSetStats() throws Exception {
        Map<String, EquipableStat>  result = new HashMap<>();
        Gson    gson = new GsonBuilder()
            .registerTypeAdapter(Rarity.class, (JsonDeserializer<Rarity>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (Rarity r : Rarity.values())
                    if (String.valueOf(r.getNumber()).equals(value)) return r;
                throw new JsonParseException("Unknown Rarity: " + value);
            })
            .registerTypeAdapter(EquipableMaterial.class, (JsonDeserializer<EquipableMaterial>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (ArmorMaterial t : ArmorMaterial.values())
                    if (t.getName().equals(value)) return t;
                for (WeaponMaterial t : WeaponMaterial.values())
                    if (t.getName().equals(value)) return t;
                throw new JsonParseException("Unknown EquipableMaterial: " + value);
            })
            .registerTypeAdapter(StatPrimary.class, (JsonDeserializer<StatPrimary>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (StatPrimary s : StatPrimary.values())
                    if (s.getName().equals(value)) return s;
                throw new JsonParseException("Unknown StatPrimary: " + value);
            })
            .registerTypeAdapter(StatSecondary.class, (JsonDeserializer<StatSecondary>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (StatSecondary s : StatSecondary.values())
                    if (s.getName().equals(value)) return s;
                throw new JsonParseException("Unknown StatSecondary: " + value);
            })
            .create();
        Type        type = new TypeToken<Map<String, EquipableStat>>(){}.getType();
        InputStream stream = RpgCraft.instance().getResource(EQUIPABLESTATS_FILE);
        if (stream == null) throw new FileNotFoundException("Resource " + EQUIPABLESTATS_FILE + " not found");
        Reader      reader = new InputStreamReader(stream);
        result.putAll(gson.fromJson(reader, type));
        return result;
    }

    private Map<String, EquipableStat> loadEquipableStats() throws Exception {
        Map<String, EquipableStat>  result = new HashMap<>();
        Gson    gson = new GsonBuilder()
            .registerTypeAdapter(Rarity.class, (JsonDeserializer<Rarity>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (Rarity r : Rarity.values())
                    if (String.valueOf(r.getNumber()).equals(value)) return r;
                throw new JsonParseException("Unknown Rarity: " + value);
            })
            .registerTypeAdapter(EquipableMaterial.class, (JsonDeserializer<EquipableMaterial>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (ArmorMaterial t : ArmorMaterial.values())
                    if (t.getName().equals(value)) return t;
                for (WeaponMaterial t : WeaponMaterial.values())
                    if (t.getName().equals(value)) return t;
                throw new JsonParseException("Unknown EquipableMaterial: " + value);
            })
            .registerTypeAdapter(StatPrimary.class, (JsonDeserializer<StatPrimary>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (StatPrimary s : StatPrimary.values())
                    if (s.getName().equals(value)) return s;
                throw new JsonParseException("Unknown StatPrimary: " + value);
            })
            .registerTypeAdapter(StatSecondary.class, (JsonDeserializer<StatSecondary>) (json, typeOfT, ctx) -> {
                String value = json.getAsString();
                for (StatSecondary s : StatSecondary.values())
                    if (s.getName().equals(value)) return s;
                throw new JsonParseException("Unknown StatSecondary: " + value);
            })
            .create();
        Type        type = new TypeToken<Map<String, EquipableStat>>(){}.getType();
        InputStream stream = RpgCraft.instance().getResource(LAUNCHERSTATS_FILE);
        if (stream == null) throw new FileNotFoundException("Resource " + LAUNCHERSTATS_FILE + " not found");
        Reader      reader = new InputStreamReader(stream);
        result.putAll(gson.fromJson(reader, type));
        return result;
    }
}
