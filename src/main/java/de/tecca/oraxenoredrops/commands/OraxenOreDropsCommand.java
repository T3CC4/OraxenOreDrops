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
                sender.sendMessage("§cUnbekannter Befehl. Nutze /oraxenoredrops help");
                return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("oraxenoredrops.reload")) {
            sender.sendMessage("§cKeine Berechtigung!");
            return true;
        }

        sender.sendMessage("§e[OraxenOreDrops] §7Lade Config neu...");

        try {
            // Config neu laden
            plugin.reloadConfig();

            // BlockDropManager neu laden
            plugin.getBlockDropManager().reload();

            sender.sendMessage("§a[OraxenOreDrops] §7Config erfolgreich neu geladen!");
            sender.sendMessage("§7Drop-Methode: §e" + plugin.getBlockDropManager().getDropMethod());
            sender.sendMessage("§7Debug-Modus: §e" + plugin.getConfig().getBoolean("debug-mode", false));

        } catch (Exception e) {
            sender.sendMessage("§c[OraxenOreDrops] Fehler beim Reload: " + e.getMessage());
            plugin.getPluginLogger().severe("Reload-Fehler: " + e.getMessage());
            e.printStackTrace();
            return true;
        }

        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("oraxenoredrops.debug")) {
            sender.sendMessage("§cKeine Berechtigung!");
            return true;
        }

        if (args.length < 2) {
            boolean current = plugin.getConfig().getBoolean("debug-mode", false);
            sender.sendMessage("§e[OraxenOreDrops] Debug-Modus: §7" + current);
            sender.sendMessage("§7Nutze: /oraxenoredrops debug <on|off>");
            return true;
        }

        String action = args[1].toLowerCase();
        boolean enable = action.equals("on") || action.equals("true");

        plugin.getConfig().set("debug-mode", enable);
        plugin.saveConfig();

        plugin.getBlockDropManager().setDebugMode(enable);

        sender.sendMessage("§e[OraxenOreDrops] §7Debug-Modus " +
                (enable ? "§aaktiviert" : "§cdeaktiviert"));

        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        if (!sender.hasPermission("oraxenoredrops.info")) {
            sender.sendMessage("§cKeine Berechtigung!");
            return true;
        }

        sender.sendMessage("§e=== OraxenOreDrops Info ===");
        sender.sendMessage("§7Version: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("§7Drop-Methode: §f" + plugin.getBlockDropManager().getDropMethod());
        sender.sendMessage("§7Debug-Modus: §f" + plugin.getConfig().getBoolean("debug-mode", false));
        sender.sendMessage("§7AdvancedEnchantments: §f" +
                (de.tecca.oraxenoredrops.util.AEAPIUtil.isAvailable() ? "§aAktiv" : "§cInaktiv"));

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§e=== OraxenOreDrops Befehle ===");
        sender.sendMessage("§7/oraxenoredrops reload §f- Config neu laden");
        sender.sendMessage("§7/oraxenoredrops debug <on|off> §f- Debug-Modus umschalten");
        sender.sendMessage("§7/oraxenoredrops info §f- Plugin-Informationen");
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