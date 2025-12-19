package de.tecca.oraxenoredrops.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Unified enchantment helper that combines vanilla and AdvancedEnchantments
 * Provides consistent access to enchantments from both sources
 */
public class EnchantmentHelper {

    // ==================== FORTUNE/LUCK ====================

    /**
     * Gets Fortune/Luck level for block drops
     *
     * Combines:
     * - Vanilla Fortune
     * - AdvancedEnchantments Fortune/Luck
     *
     * @param tool The tool (Pickaxe, Axe, Shovel, etc.)
     * @return Highest Fortune level (0 if none)
     */
    public static int getFortuneLevel(ItemStack tool) {
        if (tool == null || !tool.hasItemMeta()) {
            return 0;
        }

        // Vanilla Fortune
        int vanillaFortune = tool.getEnchantmentLevel(Enchantment.FORTUNE);

        // AdvancedEnchantments Fortune/Luck
        int aeFortune = AEAPIUtil.getFortuneLevel(tool);

        // Return highest level
        return Math.max(vanillaFortune, aeFortune);
    }

    // ==================== LOOTING ====================

    /**
     * Gets Looting level for mob drops
     *
     * Combines:
     * - Vanilla Looting
     * - AdvancedEnchantments Looting
     *
     * @param weapon The weapon (Sword, Axe, etc.)
     * @return Highest Looting level (0 if none)
     */
    public static int getLootingLevel(ItemStack weapon) {
        if (weapon == null || !weapon.hasItemMeta()) {
            return 0;
        }

        // Vanilla Looting
        int vanillaLooting = weapon.getEnchantmentLevel(Enchantment.LOOTING);

        // AdvancedEnchantments Looting
        int aeLooting = AEAPIUtil.getLootingLevel(weapon);

        // Return highest level
        return Math.max(vanillaLooting, aeLooting);
    }

    // ==================== SILK TOUCH ====================

    /**
     * Checks if tool has Silk Touch (Vanilla or AE)
     *
     * With Silk Touch, NO custom drops should be given!
     *
     * @param tool The tool
     * @return true if Silk Touch is active
     */
    public static boolean hasSilkTouch(ItemStack tool) {
        if (tool == null || !tool.hasItemMeta()) {
            return false;
        }

        // Vanilla Silk Touch
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return true;
        }

        // AdvancedEnchantments Silk Touch
        if (AEAPIUtil.isAvailable()) {
            int aeSilkTouch = AEAPIUtil.getEnchantmentLevel(tool, "Silk Touch");
            if (aeSilkTouch > 0) {
                return true;
            }
        }

        return false;
    }

    // ==================== EFFICIENCY ====================

    /**
     * Gets Efficiency level (for Veinminer detection)
     *
     * @param tool The tool
     * @return Highest Efficiency level (0 if none)
     */
    public static int getEfficiencyLevel(ItemStack tool) {
        if (tool == null || !tool.hasItemMeta()) {
            return 0;
        }

        // Vanilla Efficiency
        int vanillaEfficiency = tool.getEnchantmentLevel(Enchantment.EFFICIENCY);

        // AdvancedEnchantments Efficiency
        int aeEfficiency = AEAPIUtil.getEfficiencyLevel(tool);

        return Math.max(vanillaEfficiency, aeEfficiency);
    }

    // ==================== UNBREAKING ====================

    /**
     * Gets Unbreaking level
     *
     * @param item The item
     * @return Highest Unbreaking level (0 if none)
     */
    public static int getUnbreakingLevel(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return 0;
        }

        // Vanilla Unbreaking
        int vanillaUnbreaking = item.getEnchantmentLevel(Enchantment.UNBREAKING);

        // AdvancedEnchantments Unbreaking (if present)
        int aeUnbreaking = 0;
        if (AEAPIUtil.isAvailable()) {
            aeUnbreaking = AEAPIUtil.getEnchantmentLevel(item, "Unbreaking");
        }

        return Math.max(vanillaUnbreaking, aeUnbreaking);
    }

    // ==================== MENDING ====================

    /**
     * Checks if item has Mending
     *
     * @param item The item
     * @return true if Mending is active
     */
    public static boolean hasMending(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        // Vanilla Mending
        if (item.containsEnchantment(Enchantment.MENDING)) {
            return true;
        }

        // AdvancedEnchantments Mending
        if (AEAPIUtil.isAvailable()) {
            int aeMending = AEAPIUtil.getEnchantmentLevel(item, "Mending");
            if (aeMending > 0) {
                return true;
            }
        }

        return false;
    }

    // ==================== UTILITY ====================

    /**
     * Checks if item has any enchantment
     *
     * @param item The item
     * @return true if at least one enchantment is present
     */
    public static boolean hasAnyEnchantment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        // Vanilla enchantments
        if (!item.getEnchantments().isEmpty()) {
            return true;
        }

        // AdvancedEnchantments (if present)
        // Cannot be checked directly, but if tool has e.g. Looting...
        // For performance reasons only basic check
        return false;
    }

    /**
     * Debug: Outputs all enchantments of an item
     *
     * @param item The item
     * @return String with all enchantments
     */
    public static String getEnchantmentInfo(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return "No enchantments";
        }

        StringBuilder sb = new StringBuilder();

        // Vanilla
        sb.append("Vanilla: ");
        if (item.getEnchantments().isEmpty()) {
            sb.append("None");
        } else {
            item.getEnchantments().forEach((ench, level) ->
                    sb.append(ench.getKey().getKey()).append(" ").append(level).append(", ")
            );
        }

        sb.append(" | ");

        // AdvancedEnchantments
        sb.append("AE: ");
        if (AEAPIUtil.isAvailable()) {
            int fortune = AEAPIUtil.getFortuneLevel(item);
            int looting = AEAPIUtil.getLootingLevel(item);
            int silkTouch = AEAPIUtil.getEnchantmentLevel(item, "Silk Touch");

            if (fortune > 0) sb.append("Fortune ").append(fortune).append(", ");
            if (looting > 0) sb.append("Looting ").append(looting).append(", ");
            if (silkTouch > 0) sb.append("Silk Touch ").append(silkTouch).append(", ");

            if (fortune == 0 && looting == 0 && silkTouch == 0) {
                sb.append("None");
            }
        } else {
            sb.append("Not Available");
        }

        return sb.toString();
    }

    // ==================== ADVANCED ====================

    /**
     * Calculates effective drop chance with enchantment bonus
     *
     * Uses DropMechanics for advanced calculation
     *
     * @param baseChance Base chance in %
     * @param enchantLevel Fortune/Looting level
     * @return Final chance in %
     */
    public static double calculateDropChance(double baseChance, int enchantLevel) {
        return DropMechanics.calculateDropChance(baseChance, enchantLevel);
    }

    /**
     * Calculates drop amount with enchantment bonus
     *
     * @param minAmount Min amount
     * @param maxAmount Max amount
     * @param enchantLevel Fortune/Looting level
     * @return Final amount
     */
    public static int calculateDropAmount(int minAmount, int maxAmount, int enchantLevel) {
        return DropMechanics.calculateDropAmount(minAmount, maxAmount, enchantLevel);
    }
}