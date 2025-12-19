package de.tecca.oraxenoredrops.util;

import de.tecca.oraxenoredrops.OraxenOreDrops;

/**
 * Centralized logging utility for the plugin
 * Handles debug mode and log level management
 */
public class PluginLogger {

    private final OraxenOreDrops plugin;

    public PluginLogger(OraxenOreDrops plugin) {
        this.plugin = plugin;
    }

    /**
     * Debug log - ONLY when debug-mode: true
     */
    public void debug(String message) {
        if (isDebugMode()) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    /**
     * Info log - ALWAYS (important info)
     * Use sparingly!
     */
    public void info(String message) {
        plugin.getLogger().info(message);
    }

    /**
     * Warning log - ALWAYS
     */
    public void warn(String message) {
        plugin.getLogger().warning(message);
    }

    /**
     * Error log - ALWAYS
     */
    public void severe(String message) {
        plugin.getLogger().severe(message);
    }

    /**
     * Fine log (for deep debug info)
     */
    public void fine(String message) {
        if (isDebugMode()) {
            plugin.getLogger().fine(message);
        }
    }

    /**
     * Checks debug-mode from config
     */
    private boolean isDebugMode() {
        return plugin.getConfig().getBoolean("debug-mode", false);
    }

    /**
     * Static method for quick access
     */
    public static PluginLogger get(OraxenOreDrops plugin) {
        return new PluginLogger(plugin);
    }
}