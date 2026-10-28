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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import fr.jeunesauvage.RpgCraft;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatPrimary;
import fr.jeunesauvage.entitycustom.livingentitycustom.attributecustom.stat.StatSecondary;
import fr.jeunesauvage.itemcustom.ArmorType;
import fr.jeunesauvage.itemcustom.FoodType;
import fr.jeunesauvage.itemcustom.ItemCustom;
import fr.jeunesauvage.itemcustom.PotionType;
import fr.jeunesauvage.itemcustom.Rarity;
import fr.jeunesauvage.itemcustom.SpellType;
import fr.jeunesauvage.itemcustom.WeaponType;
import fr.jeunesauvage.itemcustom.consumable.Consumable;
import fr.jeunesauvage.itemcustom.equipable.ArmorMaterial;
import fr.jeunesauvage.itemcustom.equipable.Equipable;
import fr.jeunesauvage.itemcustom.equipable.EquipableMaterial;
import fr.jeunesauvage.itemcustom.equipable.WeaponMaterial;
import fr.jeunesauvage.itemcustom.equipable.armor.Armor;
import fr.jeunesauvage.itemcustom.equipable.weapon.Weapon;
import fr.jeunesauvage.itemcustom.food.Food;
import fr.jeunesauvage.itemcustom.potion.Potion;
import fr.jeunesauvage.itemcustom.spell.Spell;
import fr.jeunesauvage.itemcustom.usable.Usable;

public class ItemBuilder {
	private static boolean                              USESQUARED = false;
	private static final String                         FILE_CUSTOMS = "customs.json";
	private static final String                         FILE_CUSTOMSSTATS = "customsStats.json";
	private static final String                         FILE_WEAPONS = "weapons.json";
	private static final String                         FILE_WEAPONSSTATS = "weaponsStats.json";
	private static final String                         FILE_ARMORS = "armors.json";
	private static final String                         FILE_ARMORSSTATS = "armorsStats.json";
	private static final Set<String>                    SUFFIX_SET = Set.of(
                                                            "_boots",
                                                            "_chestplate", "_tunic",
                                                            "_helmet", "_hood", "_hat", "_glasses", "_mask",
                                                            "_leggings"
                                                        );
    private Map<Material, Map<String, EquipableStat>>   customs;
    private Map<Material, Map<String, EquipableStat>>   weapons;
    private Map<Material, Map<String, EquipableStat>>   armors;
    private final Map<String, ItemCustom<?>>            items = new HashMap<>();

    public ItemBuilder() {
        try {
            customs = load(FILE_CUSTOMS, FILE_CUSTOMSSTATS);
            weapons = load(FILE_WEAPONS, FILE_WEAPONSSTATS);
            armors = load(FILE_ARMORS, FILE_ARMORSSTATS);
        }
        catch (Exception e) {
            RpgCraft.debug("File JSON not found or invalid");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(RpgCraft.instance());
        }
        buildCustoms();
        buildWeapons();
        buildArmors();
        buildPotion();
        buildSpell();
        buildFood();
    }

    // build Equipable (Armor + Weapon)

    private void buildWeapons() {
        for (Entry<Material, Map<String, EquipableStat>> e: weapons.entrySet()) {
            Map<String, EquipableStat>  map = e.getValue();
            for (Entry<String, EquipableStat> e2: map.entrySet()) {
                String          name = e2.getKey();
                EquipableStat   equipableStat = e2.getValue();
                WeaponType      weaponType = WeaponType.fromMaterial(equipableStat.getEquipableMaterial());
                Weapon          weapon = new Weapon(name, weaponType, equipableStat);
                items.put(name, weapon);
            }
        }
    }

    private void buildArmors() {
        for (Entry<Material, Map<String, EquipableStat>> e: armors.entrySet()) {
            Material                    material = e.getKey();
            Map<String, EquipableStat>  map = e.getValue();
            for (Entry<String, EquipableStat> e2: map.entrySet()) {
                String          name = e2.getKey();
                EquipableStat   equipableStat = e2.getValue();
                ArmorType       armortype = ArmorType.fromMaterial(equipableStat.getEquipableMaterial(), material);
                Armor           armor = new Armor(name, armortype, equipableStat);
                items.put(name, armor);
            }
        }
    }

    private void buildCustoms() {
        for (Entry<Material, Map<String, EquipableStat>> e: customs.entrySet()) {
            Material                    material = e.getKey();
            Map<String, EquipableStat>  map = e.getValue();
            for (Entry<String, EquipableStat> e2: map.entrySet()) {
                String          name = e2.getKey();
                EquipableStat   equipableStat = e2.getValue();
                if (equipableStat.getEquipableMaterial() instanceof WeaponMaterial) {
                    WeaponType      weapontype = WeaponType.fromMaterial(equipableStat.getEquipableMaterial());
                    Weapon          weapon = new Weapon(name, weapontype, equipableStat);
                    items.put(name, weapon);
                }
                else if (equipableStat.getEquipableMaterial() instanceof ArmorMaterial) {
                    ArmorType       armortype = ArmorType.fromMaterial(equipableStat.getEquipableMaterial(), material);
                    Armor           armor = new Armor(name, armortype, equipableStat);
                    items.put(name, armor);
                }
            }
        }
    }

    // build Spell + Potion + Food

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

    private void buildFood() {
        for (FoodType type: FoodType.values()) {
            items.put(type.getName(), new Food(type));
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
            if (entry.getValue() instanceof Equipable equipable)
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
            if (entry.getValue() instanceof Spell spell)
                result.put(entry.getKey(), spell);
        }
        return result;
    }

    public Map<String, Food> getFood() {
        Map<String, Food>   result = new HashMap<>();
        for (Entry<String, ItemCustom<?>> entry: items.entrySet()) {
            if (entry.getValue() instanceof Food food)
                result.put(entry.getKey(), food);
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

    private String getSet(String name) {
        for (String suffix: SUFFIX_SET) {
            if (name.endsWith(suffix))
                return name.substring(0, name.length() - suffix.length());
        }
        return null;
    }

    // loader (.json)

    private Map<Material, Map<String, EquipableStat>> load(String file, String fileStats) throws Exception {
        Map<Material, Map<String, EquipableStat>>          result = new HashMap<>();
        // weapons
        Gson                                gson = new Gson();
        Type                                type = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
        InputStream                         stream = RpgCraft.instance().getResource(file);
        if (stream == null) throw new FileNotFoundException("Resource " + file + " not found");
        Reader                              reader = new InputStreamReader(stream);
        Map<String, Map<String, Integer>>   map = gson.fromJson(reader, type);
        // stats
        Gson    gsonStats = new GsonBuilder()
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
        type = new TypeToken<Map<String, EquipableStat>>(){}.getType();
        stream = RpgCraft.instance().getResource(fileStats);
        if (stream == null) throw new FileNotFoundException("Resource " + fileStats + " not found");
        reader = new InputStreamReader(stream);
        Map<String, EquipableStat>            mapStats = gsonStats.fromJson(reader, type);
        // create result
        for (Entry<String, Map<String, Integer>> e : map.entrySet()) {
            String		itemName = e.getKey();
            Material	material = Material.matchMaterial(itemName);
            if (material == null) continue;
            for (Entry<String, Integer> e2: e.getValue().entrySet()) {
                String          name = e2.getKey();
                if (!USESQUARED && name.endsWith("2")) continue;
                String          fullName = material.name().toLowerCase() + "/" + name;
                String          set = getSet(name);
                EquipableStat   toCopy = (set != null ? mapStats.get(set) : mapStats.get(fullName));
                EquipableStat   equipableStat = null;
                if (toCopy == null) {
                    if (material != Material.ELYTRA) continue;
                    equipableStat = new EquipableStat(ArmorMaterial.ELYTRA);
                }
                else
                    equipableStat = new EquipableStat(toCopy);
                equipableStat.setSet(set);
                equipableStat.setCmd((float)e2.getValue());
                result.computeIfAbsent(material, m -> new HashMap<>()).put(fullName, equipableStat);
            }
        }
        return result;
    }
}
