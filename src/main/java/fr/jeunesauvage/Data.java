package fr.jeunesauvage;

import java.util.Base64;
import java.util.Objects;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import io.papermc.paper.persistence.PersistentDataContainerView;

public class Data {
	public static long d(int duration) {
		return duration * 20;
	}

	/*
	** boolean
	*/

	public static boolean hasBoolean(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.BOOLEAN));
	}

	public static void setBoolean(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		pdc.set(key, PersistentDataType.BOOLEAN, true);
	}

	public static boolean hasBoolean(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.BOOLEAN));
	}

	/*
	** integer
	*/

	public static boolean hasInteger(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.INTEGER));
	}

	public static void setInteger(PersistentDataContainer pdc, @NonNull NamespacedKey key, int v) {
		pdc.set(key, PersistentDataType.INTEGER, v);
	}

	public static int getInteger(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		Integer value = pdc.get(key, Objects.requireNonNull(PersistentDataType.INTEGER));
		return value == null ? 0 : value;
	}

	public static boolean hasInteger(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.INTEGER));
	}

	public static int getInteger(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		Integer value = pdc.get(key, Objects.requireNonNull(PersistentDataType.INTEGER));
		return value == null ? 0 : value;
	}

	/*
	** double
	*/

	public static boolean hasDouble(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.DOUBLE));
	}

	public static void setDouble(PersistentDataContainer pdc, @NonNull NamespacedKey key, double v) {
		pdc.set(key, PersistentDataType.DOUBLE, v);
	}

	public static double getDouble(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		Double value = pdc.get(key, Objects.requireNonNull(PersistentDataType.DOUBLE));
		return value == null ? 0 : value;
	}

	public static boolean hasDouble(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.DOUBLE));
	}

	public static double getDouble(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		Double value = pdc.get(key, Objects.requireNonNull(PersistentDataType.DOUBLE));
		return value == null ? 0 : value;
	}

	/*
	** long
	*/

	public static boolean hasLong(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.LONG));
	}

	public static void setLong(PersistentDataContainer pdc, @NonNull NamespacedKey key, long v) {
		pdc.set(key, PersistentDataType.LONG, v);
	}

	public static long getLong(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		Long value = pdc.get(key, Objects.requireNonNull(PersistentDataType.LONG));
		return value == null ? 0 : value;
	}

	public static boolean hasLong(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.LONG));
	}

	public static long getLong(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		Long value = pdc.get(key, Objects.requireNonNull(PersistentDataType.LONG));
		return value == null ? 0 : value;
	}

	/*
	** string
	*/

	public static boolean hasString(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.STRING));
	}

	public static void setString(PersistentDataContainer pdc, @NonNull NamespacedKey key, String v) {
		pdc.set(key, PersistentDataType.STRING, v);
	}

	public static String getString(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		String value = pdc.get(key, Objects.requireNonNull(PersistentDataType.STRING));
		return value;
	}

	public static boolean hasString(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		return pdc.has(key, Objects.requireNonNull(PersistentDataType.STRING));
	}

	public static String getString(PersistentDataContainerView pdc, @NonNull NamespacedKey key) {
		String value = pdc.get(key, Objects.requireNonNull(PersistentDataType.STRING));
		return value;
	}

	/*
	** remove
	*/

	public static void remove(PersistentDataContainer pdc, @NonNull NamespacedKey key) {
		pdc.remove(key);
	}

	/*
	** base64
	*/

	public static String toBase64(ItemStack item) {
	    try {
	        YamlConfiguration	config = new YamlConfiguration();
	        config.set("item", item);
	        String	yaml = config.saveToString();
	        return Base64.getEncoder().encodeToString(yaml.getBytes());
	    }
		catch (Exception e) {
	        throw new IllegalStateException("Error convert to Base64", e);
	    }
	}

	public static ItemStack fromBase64(String base64) {
	    try {
	        String				yaml = new String(Base64.getDecoder().decode(base64));
	        YamlConfiguration	config = new YamlConfiguration();
	        config.loadFromString(yaml);
	        return config.getItemStack("item");
	    }
		catch (Exception e) {
	        throw new IllegalStateException("Error convert from Base64", e);
	    }
	}
}
