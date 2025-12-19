package de.tecca.oraxenoredrops.managers;

import de.tecca.oraxenoredrops.OraxenOreDrops;
import de.tecca.oraxenoredrops.enums.DropMethod;
import de.tecca.oraxenoredrops.model.DropEntry;
import de.tecca.oraxenoredrops.util.DropMechanics;
import de.tecca.oraxenoredrops.util.OraxenItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class BlockDropManager {

    private OraxenOreDrops plugin;
    private boolean debugMode;

    private final Map<Material, List<DropEntry>> blockDrops = new ConcurrentHashMap<>();

    // Config: Which drop method to use?
    private DropMethod dropMethod = DropMethod.HYBRID; // Default: Hybrid

    public BlockDropManager(OraxenOreDrops plugin) {
        this.plugin = plugin;
        this.debugMode = plugin.getConfig().getBoolean("debug-mode", false);
        loadConfig();
        loadBlockDrops();
    }

    private void loadConfig() {
        String methodStr = plugin.getConfig().getString("drop-mechanics.method", "HYBRID");
        try {
            dropMethod = DropMethod.valueOf(methodStr.toUpperCase());
            info("Drop method: " + dropMethod);
        } catch (IllegalArgumentException e) {
            warn("Invalid drop-method: " + methodStr + ", using HYBRID");
            dropMethod = DropMethod.HYBRID;
        }
    }

    private void loadBlockDrops() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("block-drops");
        if (section == null) {
            warn("No block-drops section in config!");
            return;
        }

        blockDrops.clear();
        int totalDrops = 0;
        int invalidBlocks = 0;

        for (String blockType : section.getKeys(false)) {
            try {
                Material material = Material.valueOf(blockType.toUpperCase());
                List<DropEntry> drops = loadDropEntries(
                        section.getConfigurationSection(blockType),
                        "block-drops." + blockType
                );

                if (!drops.isEmpty()) {
                    blockDrops.put(material, drops);
                    totalDrops += drops.size();

                    debug("Block drops: " + material + " → " + drops.size() + " items");
                    for (DropEntry entry : drops) {
                        debug("  - " + entry.oraxenItemId() + " (" + entry.chance() + "%)");

                        // Debug: Show fortune scaling
                        if (debugMode) {
                            debugFortuneScaling(entry.chance());
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                warn("Invalid block type: " + blockType);
                invalidBlocks++;
            }
        }

        info("Block drops: " + totalDrops + " items for " + blockDrops.size() + " blocks" +
                (invalidBlocks > 0 ? " (" + invalidBlocks + " invalid)" : ""));
    }

    private List<DropEntry> loadDropEntries(ConfigurationSection section, String path) {
        List<DropEntry> drops = new ArrayList<>();

        if (section == null) {
            warn(path + ": Section is null!");
            return drops;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection entrySection = section.getConfigurationSection(key);
            if (entrySection == null) continue;

            String itemId = entrySection.getString("oraxen-item");
            double chance = entrySection.getDouble("chance", 0);
            int minAmount = entrySection.getInt("min-amount", 1);
            int maxAmount = entrySection.getInt("max-amount", 1);

            // Validation
            if (!OraxenItemUtil.validate(itemId)) {
                warn(path + "." + key + ": Item '" + itemId + "' invalid");
                continue;
            }

            if (chance <= 0 || chance > 100) {
                warn(path + "." + key + ": Invalid chance " + chance + "%");
                continue;
            }

            if (minAmount < 0 || maxAmount < minAmount) {
                warn(path + "." + key + ": Invalid amounts (min=" + minAmount + ", max=" + maxAmount + ")");
                continue;
            }

            drops.add(new DropEntry(itemId, chance, minAmount, maxAmount));
        }

        return drops;
    }

    /**
     * Debug: Shows how Fortune scales the chance
     */
    private void debugFortuneScaling(double baseChance) {
        debug("  Fortune scaling for " + baseChance + "%:");
        for (int fortune = 0; fortune <= 10; fortune += 3) {
            String result = switch (dropMethod) {
                case DIMINISHING -> {
                    double finalChance = DropMechanics.calculateDropChance(baseChance, fortune);
                    yield String.format("    Fortune %d: %.3f%% (+%.3f%%)",
                            fortune, finalChance, finalChance - baseChance);
                }
                case BONUS_ROLLS -> {
                    var rolls = DropMechanics.calculateBonusRolls(baseChance, fortune);
                    yield String.format("    Fortune %d: %d rolls @ %.3f%% = %.3f%% total",
                            fortune, rolls.rolls, rolls.chancePerRoll, rolls.totalChance);
                }
                case HYBRID -> {
                    var hybrid = DropMechanics.calculateHybridDrop(baseChance, fortune);
                    yield String.format("    Fortune %d: %d rolls @ %.3f%% = %.3f%% total",
                            fortune, hybrid.rolls, hybrid.chancePerRoll, hybrid.totalChance);
                }
            };
            debug(result);
        }
    }

    /**
     * Gets drops for a block with Fortune level
     */
    public List<ItemStack> getDrops(Material material, int fortuneLevel) {
        List<DropEntry> entries = blockDrops.get(material);

        debug("getBlockDrops(" + material + ", Fortune=" + fortuneLevel + ")");
        debug("  Method: " + dropMethod);
        debug("  Entries: " + (entries != null ? entries.size() : 0));

        if (entries == null || entries.isEmpty()) {
            debug("  → NO drops configured");
            return Collections.emptyList();
        }

        return processDrops(entries, fortuneLevel);
    }

    /**
     * Processes drops with new mechanics
     */
    private List<ItemStack> processDrops(List<DropEntry> entries, int fortuneLevel) {
        List<ItemStack> drops = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (DropEntry entry : entries) {
            debug("  Drop: " + entry.oraxenItemId() + " (Base: " + entry.chance() + "%)");

            boolean dropped = false;
            int amount = 0;

            // Choose drop method
            switch (dropMethod) {
                case DIMINISHING:
                    dropped = rollDiminishing(entry.chance(), fortuneLevel, random);
                    if (dropped) {
                        amount = DropMechanics.calculateDropAmount(
                                entry.minAmount(), entry.maxAmount(), fortuneLevel);
                    }
                    break;

                case BONUS_ROLLS:
                    amount = rollBonusRolls(entry.chance(), fortuneLevel,
                            entry.minAmount(), entry.maxAmount(), random);
                    dropped = amount > 0;
                    break;

                case HYBRID:
                    amount = rollHybrid(entry.chance(), fortuneLevel,
                            entry.minAmount(), entry.maxAmount(), random);
                    dropped = amount > 0;
                    break;
            }

            if (dropped && amount > 0) {
                ItemStack item = OraxenItemUtil.buildItem(entry.oraxenItemId(), plugin);
                if (item != null) {
                    item.setAmount(amount);
                    drops.add(item);
                    debug("    ✓ SUCCESS: " + item.getType() + " x" + amount);
                }
            } else {
                debug("    ✗ MISS");
            }
        }

        debug("  TOTAL: " + drops.size() + " drops");
        return drops;
    }

    /**
     * METHOD 1: Diminishing Returns
     */
    private boolean rollDiminishing(double baseChance, int fortuneLevel, ThreadLocalRandom random) {
        double finalChance = DropMechanics.calculateDropChance(baseChance, fortuneLevel);
        double roll = random.nextDouble() * 100;

        if (debugMode) {
            debug("    Diminishing: " + baseChance + "% → " +
                    String.format("%.3f%%", finalChance) +
                    " | Roll: " + String.format("%.3f", roll));
        }

        return roll < finalChance;
    }

    /**
     * METHOD 2: Bonus Rolls
     */
    private int rollBonusRolls(double baseChance, int fortuneLevel,
                               int minAmount, int maxAmount, ThreadLocalRandom random) {
        var result = DropMechanics.calculateBonusRolls(baseChance, fortuneLevel);

        if (debugMode) {
            debug("    Bonus Rolls: " + result);
        }

        int totalAmount = 0;

        // Roll for each attempt
        for (int i = 0; i < result.rolls; i++) {
            double roll = random.nextDouble() * 100;

            if (roll < result.chancePerRoll) {
                int amount = random.nextInt(minAmount, maxAmount + 1);
                totalAmount += amount;

                if (debugMode) {
                    debug("      Roll " + (i + 1) + ": " +
                            String.format("%.3f < %.3f", roll, result.chancePerRoll) +
                            " → +" + amount);
                }
            } else if (debugMode) {
                debug("      Roll " + (i + 1) + ": " +
                        String.format("%.3f >= %.3f", roll, result.chancePerRoll) +
                        " → Miss");
            }
        }

        return totalAmount;
    }

    /**
     * METHOD 3: Hybrid
     */
    private int rollHybrid(double baseChance, int fortuneLevel,
                           int minAmount, int maxAmount, ThreadLocalRandom random) {
        var result = DropMechanics.calculateHybridDrop(baseChance, fortuneLevel);

        if (debugMode) {
            debug("    Hybrid: " + result);
        }

        int totalAmount = 0;

        for (int i = 0; i < result.rolls; i++) {
            double roll = random.nextDouble() * 100;

            if (roll < result.chancePerRoll) {
                int amount = random.nextInt(minAmount, maxAmount + 1);
                totalAmount += amount;

                if (debugMode) {
                    debug("      Roll " + (i + 1) + ": " +
                            String.format("%.3f < %.3f", roll, result.chancePerRoll) +
                            " → +" + amount);
                }
            } else if (debugMode) {
                debug("      Roll " + (i + 1) + ": " +
                        String.format("%.3f >= %.3f", roll, result.chancePerRoll) +
                        " → Miss");
            }
        }

        return totalAmount;
    }

    /**
     * Sets drop method (for commands/testing)
     */
    public void setDropMethod(DropMethod method) {
        this.dropMethod = method;
        info("Drop method changed to: " + method);
    }

    public DropMethod getDropMethod() {
        return dropMethod;
    }

    protected void info(String message) {
        plugin.getLogger().info(message);
    }

    protected void warn(String message) {
        plugin.getLogger().warning(message);
    }

    protected void debug(String message) {
        if (debugMode) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void reload() {
        blockDrops.clear();
        debugMode = plugin.getConfig().getBoolean("debug-mode", false);
        loadConfig();
        loadBlockDrops();
    }
}