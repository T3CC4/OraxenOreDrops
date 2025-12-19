package de.tecca.oraxenoredrops;

import de.tecca.oraxenoredrops.commands.OraxenOreDropsCommand;
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

        // Check for Oraxen
        if (Bukkit.getPluginManager().getPlugin("Oraxen") == null) {
            getLogger().severe("Oraxen not found! Plugin will be disabled.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize AdvancedEnchantments API
        if (AEAPIUtil.initialize()) {
            pluginLogger.info("✓ AdvancedEnchantments detected - Custom enchants enabled!");
        } else {
            pluginLogger.info("AdvancedEnchantments not found - Using vanilla enchants only");
        }

        saveDefaultConfig();

        blockDropManager = new BlockDropManager(this);

        blockBreakListener = new BlockBreakListener(this);

        Bukkit.getPluginManager().registerEvents(blockBreakListener, this);

        // Register command
        OraxenOreDropsCommand commandExecutor = new OraxenOreDropsCommand(this);
        getCommand("oraxenoredrops").setExecutor(commandExecutor);
        getCommand("oraxenoredrops").setTabCompleter(commandExecutor);

        pluginLogger.info("OraxenOreDrops successfully enabled!");
    }

    @Override
    public void onDisable() {
        if (blockBreakListener != null) {
            blockBreakListener.shutdown();
        }

        pluginLogger.info("OraxenOreDrops disabled!");
    }

    public static OraxenOreDrops getInstance() {
        return instance;
    }

    public BlockDropManager getBlockDropManager() {
        return blockDropManager;
    }

    public PluginLogger getPluginLogger() {
        return pluginLogger;
    }
}