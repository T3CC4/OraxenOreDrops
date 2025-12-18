package de.tecca.oraxenoredrops;

import de.tecca.oraxenoredrops.listeners.BlockBreakListener;
import de.tecca.oraxenoredrops.managers.BlockDropManager;
import de.tecca.oraxenoredrops.util.AEAPIUtil;
import de.tecca.oraxenoredrops.util.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class OraxenOreDrops extends JavaPlugin {

    private static OraxenOreDrops instance;

    private BlockDropManager blockDropManager;
    private BlockBreakListener blockBreakListener;

    private PluginLogger pluginLogger;

    @Override
    public void onEnable() {
        instance = this;

        pluginLogger = new PluginLogger(this);

        // Oraxen Check
        if (Bukkit.getPluginManager().getPlugin("Oraxen") == null) {
            getLogger().severe("Oraxen nicht gefunden! Plugin wird deaktiviert.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // AdvancedEnchantments API
        if (AEAPIUtil.initialize()) {
            pluginLogger.info("✓ AdvancedEnchantments erkannt - Custom Enchants aktiv!");
        } else {
            pluginLogger.info("AdvancedEnchantments nicht gefunden - Vanilla Enchants");
        }

        saveDefaultConfig();

        blockDropManager = new BlockDropManager(this);

        blockBreakListener = new BlockBreakListener(this);

        Bukkit.getPluginManager().registerEvents(blockBreakListener, this);

        pluginLogger.info("OraxenOrePlugin erfolgreich gestartet!");
    }

    @Override
    public void onDisable() {
        if (blockBreakListener != null) {
            blockBreakListener.shutdown();
        }

        pluginLogger.info("OraxenOrePlugin deaktiviert!");
    }

    public BlockDropManager getBlockDropManager() {
        return blockDropManager;
    }

    public PluginLogger getPluginLogger() {
        return pluginLogger;
    }
}
