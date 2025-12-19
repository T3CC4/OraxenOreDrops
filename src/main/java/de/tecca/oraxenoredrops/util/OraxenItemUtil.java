package de.tecca.oraxenoredrops.util;

import de.tecca.oraxenoredrops.OraxenOreDrops;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class for Oraxen item operations
 * Provides safe access to Oraxen items with proper error handling
 */
public class OraxenItemUtil {

    /**
     * Builds an Oraxen item with logging
     *
     * @param oraxenItemId The Oraxen item ID
     * @param plugin Plugin instance for logging
     * @return ItemStack or null if not found
     */
    public static ItemStack buildItem(String oraxenItemId, OraxenOreDrops plugin) {
        if (oraxenItemId == null || oraxenItemId.isEmpty()) {
            return null;
        }
        try {
            var builder = io.th0rgal.oraxen.api.OraxenItems.getItemById(oraxenItemId);
            if (builder != null) {
                return builder.build();
            }
            plugin.getPluginLogger().warn("Oraxen item not found: '" + oraxenItemId + "'");
            return null;
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Item error '" + oraxenItemId + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Builds an Oraxen item without logging (for performance-critical areas)
     *
     * @param oraxenItemId The Oraxen item ID
     * @return ItemStack or null if not found
     */
    public static ItemStack buildItemSilent(String oraxenItemId) {
        if (oraxenItemId == null || oraxenItemId.isEmpty()) {
            return null;
        }
        try {
            var builder = io.th0rgal.oraxen.api.OraxenItems.getItemById(oraxenItemId);
            return builder != null ? builder.build() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validates if an Oraxen item exists
     *
     * @param oraxenItemId The Oraxen item ID
     * @return true if item exists
     */
    public static boolean validate(String oraxenItemId) {
        if (oraxenItemId == null || oraxenItemId.isEmpty()) {
            return false;
        }
        try {
            return io.th0rgal.oraxen.api.OraxenItems.exists(oraxenItemId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates if an Oraxen item exists with logging
     *
     * @param oraxenItemId The Oraxen item ID
     * @param plugin Plugin instance for logging
     * @return true if item exists
     */
    public static boolean validateWithLogging(String oraxenItemId, OraxenOreDrops plugin) {
        if (oraxenItemId == null || oraxenItemId.isEmpty()) {
            plugin.getPluginLogger().warn("Oraxen item ID is null or empty!");
            return false;
        }
        try {
            boolean exists = io.th0rgal.oraxen.api.OraxenItems.exists(oraxenItemId);
            if (!exists) {
                plugin.getPluginLogger().warn("Oraxen item does not exist: '" + oraxenItemId + "'");
            }
            return exists;
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Error validating '" + oraxenItemId + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets Oraxen ID from an ItemStack
     *
     * @param item The ItemStack
     * @return Oraxen ID or null if not found
     */
    public static String getIdByItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        try {
            return io.th0rgal.oraxen.api.OraxenItems.getIdByItem(item);
        } catch (Exception e) {
            return null;
        }
    }
}