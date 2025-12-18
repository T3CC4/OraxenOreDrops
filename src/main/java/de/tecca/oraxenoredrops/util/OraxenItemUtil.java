package de.tecca.oraxenoredrops.util;

import de.tecca.oraxenoredrops.OraxenOreDrops;
import org.bukkit.inventory.ItemStack;

public class OraxenItemUtil {

    /**
     * Baut ein Oraxen-Item mit Logging
     *
     * @param oraxenItemId Die Oraxen-Item ID
     * @param plugin Plugin-Instanz für Logging
     * @return ItemStack oder null wenn nicht gefunden
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
            plugin.getPluginLogger().warn("Oraxen-Item nicht gefunden: '" + oraxenItemId + "'");
            return null;
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Item-Fehler '" + oraxenItemId + "': " + e.getMessage());
            return null;
        }
    }

    /**
     * Baut ein Oraxen-Item ohne Logging (für Performance-kritische Bereiche)
     *
     * @param oraxenItemId Die Oraxen-Item ID
     * @return ItemStack oder null wenn nicht gefunden
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
     * Validiert ob ein Oraxen-Item existiert
     *
     * @param oraxenItemId Die Oraxen-Item ID
     * @return true wenn Item existiert
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
     * Validiert ob ein Oraxen-Item existiert mit Logging
     *
     * @param oraxenItemId Die Oraxen-Item ID
     * @param plugin Plugin-Instanz für Logging
     * @return true wenn Item existiert
     */
    public static boolean validateWithLogging(String oraxenItemId, OraxenOreDrops plugin) {
        if (oraxenItemId == null || oraxenItemId.isEmpty()) {
            plugin.getPluginLogger().warn("Oraxen-Item ID ist null oder leer!");
            return false;
        }
        try {
            boolean exists = io.th0rgal.oraxen.api.OraxenItems.exists(oraxenItemId);
            if (!exists) {
                plugin.getPluginLogger().warn("Oraxen-Item existiert nicht: '" + oraxenItemId + "'");
            }
            return exists;
        } catch (Exception e) {
            plugin.getPluginLogger().warn("Fehler beim Validieren von '" + oraxenItemId + "': " + e.getMessage());
            return false;
        }
    }

    /**
     * Holt Oraxen-ID von einem ItemStack
     *
     * @param item Das ItemStack
     * @return Oraxen-ID oder null wenn nicht gefunden
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