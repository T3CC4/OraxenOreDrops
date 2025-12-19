package de.tecca.oraxenoredrops.commands;

import de.tecca.oraxenoredrops.OraxenOreDrops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OraxenOreDropsCommand implements CommandExecutor, TabCompleter {

    private final OraxenOreDrops plugin;

    public OraxenOreDropsCommand(OraxenOreDrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                return handleReload(sender);

            case "debug":
                return handleDebug(sender, args);

            case "info":
                return handleInfo(sender);

            default:
                sender.sendMessage("§cUnknown command. Use /oraxenoredrops help");
                return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("oraxenoredrops.reload")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }

        sender.sendMessage("§e[OraxenOreDrops] §7Reloading config...");

        try {
            // Reload config
            plugin.reloadConfig();

            // Reload BlockDropManager
            plugin.getBlockDropManager().reload();

            sender.sendMessage("§a[OraxenOreDrops] §7Config successfully reloaded!");
            sender.sendMessage("§7Drop method: §e" + plugin.getBlockDropManager().getDropMethod());
            sender.sendMessage("§7Debug mode: §e" + plugin.getConfig().getBoolean("debug-mode", false));

        } catch (Exception e) {
            sender.sendMessage("§c[OraxenOreDrops] Error during reload: " + e.getMessage());
            plugin.getPluginLogger().severe("Reload error: " + e.getMessage());
            e.printStackTrace();
            return true;
        }

        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oraxenoredrops.debug")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }

        if (args.length < 2) {
            boolean current = plugin.getConfig().getBoolean("debug-mode", false);
            sender.sendMessage("§e[OraxenOreDrops] Debug mode: §7" + current);
            sender.sendMessage("§7Use: /oraxenoredrops debug <on|off>");
            return true;
        }

        String action = args[1].toLowerCase();
        boolean enable = action.equals("on") || action.equals("true");

        plugin.getConfig().set("debug-mode", enable);
        plugin.saveConfig();

        plugin.getBlockDropManager().setDebugMode(enable);

        sender.sendMessage("§e[OraxenOreDrops] §7Debug mode " +
                (enable ? "§aenabled" : "§cdisabled"));

        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        if (!sender.hasPermission("oraxenoredrops.info")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }

        sender.sendMessage("§e=== OraxenOreDrops Info ===");
        sender.sendMessage("§7Version: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("§7Drop method: §f" + plugin.getBlockDropManager().getDropMethod());
        sender.sendMessage("§7Debug mode: §f" + plugin.getConfig().getBoolean("debug-mode", false));
        sender.sendMessage("§7AdvancedEnchantments: §f" +
                (de.tecca.oraxenoredrops.util.AEAPIUtil.isAvailable() ? "§aActive" : "§cInactive"));

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e=== OraxenOreDrops Commands ===");
        sender.sendMessage("§7/oraxenoredrops reload §f- Reload config");
        sender.sendMessage("§7/oraxenoredrops debug <on|off> §f- Toggle debug mode");
        sender.sendMessage("§7/oraxenoredrops info §f- Plugin information");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("oraxenoredrops.reload")) completions.add("reload");
            if (sender.hasPermission("oraxenoredrops.debug")) completions.add("debug");
            if (sender.hasPermission("oraxenoredrops.info")) completions.add("info");
            completions.add("help");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            completions.add("on");
            completions.add("off");
        }

        return completions;
    }
}