package de.tecca.oraxenoredrops.util;

import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for AdvancedEnchantments API integration
 * Provides safe access to custom enchantments with fallback handling
 */
public class AEAPIUtil {

    private static boolean available = false;

    /**
     * Initializes the AEAPI (called on plugin startup)
     */
    public static boolean initialize() {
        try {
            // Check if AEAPI class exists
            Class.forName("net.advancedplugins.ae.api.AEAPI");
            available = true;
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            available = false;
            return false;
        }
    }

    /**
     * Checks if AdvancedEnchantments is available
     */
    public static boolean isAvailable() {
        return available;
    }

    /**
     * Checks if an item has a specific custom enchantment
     *
     * @param item The item to check
     * @param enchantName Name of the enchantment (e.g. "Looting", "Luck")
     * @return true if the enchantment is present
     */
    public static boolean hasEnchantment(ItemStack item, String enchantName) {
        return getEnchantmentLevel(item, enchantName) > 0;
    }

    /**
     * Gets the level of a custom enchantment
     *
     * @param item The item
     * @param enchantName Name of the enchantment
     * @return Level of the enchantment, 0 if not present
     */
    public static int getEnchantmentLevel(ItemStack item, String enchantName) {
        if (!isAvailable() || item == null || enchantName == null) {
            return 0;
        }

        try {
            // getEnchantmentLevel returns 0 if enchant is not present
            return AEAPI.getEnchantLevel(enchantName, item);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Gets the highest level from multiple possible enchantment names
     *
     * @param item The item
     * @param enchantNames Array of possible names (e.g. ["Looting", "looting", "LOOTING"])
     * @return Highest found level
     */
    public static int getHighestEnchantmentLevel(ItemStack item, String... enchantNames) {
        if (!isAvailable() || item == null || enchantNames == null) {
            return 0;
        }

        int maxLevel = 0;

        for (String enchantName : enchantNames) {
            int level = getEnchantmentLevel(item, enchantName);
            maxLevel = Math.max(maxLevel, level);
        }

        return maxLevel;
    }

    /**
     * Applies a custom enchantment to an item
     *
     * @param item The item
     * @param enchantName Name of the enchantment
     * @param level Level of the enchantment
     * @return The modified item
     */
    public static ItemStack applyEnchantment(ItemStack item, String enchantName, int level) {
        if (!isAvailable() || item == null || enchantName == null) {
            return item;
        }

        try {
            return AEAPI.applyEnchant(enchantName, level, item);
        } catch (Exception e) {
            return item;
        }
    }

    /**
     * Removes a custom enchantment from an item
     *
     * @param item The item
     * @param enchantName Name of the enchantment
     * @return The modified item
     */
    public static ItemStack removeEnchantment(ItemStack item, String enchantName) {
        if (!isAvailable() || item == null || enchantName == null) {
            return item;
        }

        try {
            return AEAPI.removeEnchantment(item, enchantName);
        } catch (Exception e) {
            return item;
        }
    }

    // ========== COMMONLY USED ENCHANTMENTS ==========

    /**
     * Gets Looting level (for mob drops)
     */
    public static int getLootingLevel(ItemStack weapon) {
        return getHighestEnchantmentLevel(weapon, "Looting", "looting", "LOOTING");
    }

    /**
     * Gets Fortune/Luck level (for block drops)
     */
    public static int getFortuneLevel(ItemStack tool) {
        return getHighestEnchantmentLevel(tool, "Fortune", "Luck", "fortune", "luck");
    }

    /**
     * Gets Veinminer level
     */
    public static int getVeinminerLevel(ItemStack tool) {
        return getHighestEnchantmentLevel(tool, "Veinminer", "veinminer", "VeinMiner");
    }

    /**
     * Gets Efficiency level
     */
    public static int getEfficiencyLevel(ItemStack tool) {
        return getHighestEnchantmentLevel(tool, "Efficiency", "efficiency", "EFFICIENCY");
    }

    // ========== BLOCK METADATA COMPATIBILITY ==========

    /**
     * Checks if a block should be ignored by AdvancedEnchantments
     * See: https://ae.advancedplugins.net/for-developers/plugin-compatiblity-issues
     *
     * @param block The block to check
     * @return true if block should be ignored
     */
    public static boolean shouldIgnoreBlock(org.bukkit.block.Block block) {
        if (!isAvailable() || block == null) {
            return false;
        }

        try {
            // Use reflection if AEAPI.ignoreBlockEvent() exists
            Class<?> aeapiClass = Class.forName("net.advancedplugins.ae.api.AEAPI");
            java.lang.reflect.Method method = aeapiClass.getMethod("ignoreBlockEvent", org.bukkit.block.Block.class);
            return (boolean) method.invoke(null, block);
        } catch (NoSuchMethodException e) {
            // Method doesn't exist - old AE version
            return false;
        } catch (Exception e) {
            // Other error
            return false;
        }
    }

    /**
     * Marks a block to be ignored by AdvancedEnchantments
     * Prevents duplicate drops when both plugins use BlockBreakEvent
     *
     * @param block The block to mark
     */
    public static void setIgnoreBlockEvent(org.bukkit.block.Block block) {
        if (!isAvailable() || block == null) {
            return;
        }

        try {
            // Use reflection if AEAPI.setIgnoreBlockEvent() exists
            Class<?> aeapiClass = Class.forName("net.advancedplugins.ae.api.AEAPI");
            java.lang.reflect.Method method = aeapiClass.getMethod("setIgnoreBlockEvent", org.bukkit.block.Block.class);
            method.invoke(null, block);
        } catch (Exception e) {
            // Ignore
        }
    }
}