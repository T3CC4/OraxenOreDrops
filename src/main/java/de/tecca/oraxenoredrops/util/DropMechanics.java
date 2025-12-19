package de.tecca.oraxenoredrops.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Advanced drop mechanics system for balanced Fortune/Looting scaling
 *
 * Provides three different methods:
 * 1. DIMINISHING - Logarithmic reduction for rare items
 * 2. BONUS_ROLLS - Additional rolls instead of chance increase
 * 3. HYBRID - Mix of both (recommended)
 */
public class DropMechanics {

    // ==================== DROP TIERS ====================

    /**
     * Drop rarity determines the bonus formula
     */
    public enum DropRarity {
        COMMON(50.0, 100.0),        // 50-100%: Full bonus
        UNCOMMON(10.0, 50.0),       // 10-50%: Reduced bonus
        RARE(1.0, 10.0),            // 1-10%: Heavily reduced
        VERY_RARE(0.1, 1.0),        // 0.1-1%: Minimal
        LEGENDARY(0.0, 0.1);        // <0.1%: Almost no bonus

        final double minChance;
        final double maxChance;

        DropRarity(double minChance, double maxChance) {
            this.minChance = minChance;
            this.maxChance = maxChance;
        }

        public static DropRarity fromChance(double chance) {
            for (DropRarity rarity : values()) {
                if (chance > rarity.minChance && chance <= rarity.maxChance) {
                    return rarity;
                }
            }
            return LEGENDARY; // Fallback for very rare items
        }
    }

    // ==================== FORTUNE/LOOTING MECHANICS ====================

    /**
     * Calculates final drop chance with Fortune/Looting
     *
     * METHOD 1: Diminishing Returns (Default)
     * - Rare items get less bonus
     * - Uses logarithmic scaling
     * - Soft cap at 95%
     *
     * @param baseChance Base chance in % (e.g. 0.1 or 50.0)
     * @param enchantLevel Fortune/Looting level (0-10+)
     * @return Final chance in %
     */
    public static double calculateDropChance(double baseChance, int enchantLevel) {
        if (enchantLevel <= 0) {
            return baseChance;
        }

        DropRarity rarity = DropRarity.fromChance(baseChance);

        // Calculate bonus based on rarity
        double bonus = calculateBonus(baseChance, enchantLevel, rarity);

        // Final chance
        double finalChance = baseChance + bonus;

        // Soft cap at 95%
        return Math.min(95.0, finalChance);
    }

    /**
     * Calculates bonus based on rarity
     */
    private static double calculateBonus(double baseChance, int enchantLevel, DropRarity rarity) {
        switch (rarity) {
            case COMMON:
                // Full linear bonus: +1% per level
                return enchantLevel * 1.0;

            case UNCOMMON:
                // Reduced bonus: +0.5% per level
                return enchantLevel * 0.5;

            case RARE:
                // Logarithmic bonus
                // Fortune I: +0.3%, II: +0.5%, III: +0.7%, X: +1.4%
                return Math.log(enchantLevel + 1) * baseChance * 0.15;

            case VERY_RARE:
                // Heavily reduced - square root scaling
                // Fortune I: +0.01%, III: +0.017%, X: +0.03%
                return Math.sqrt(enchantLevel) * baseChance * 0.1;

            case LEGENDARY:
                // Minimal - only symbolic bonus
                // Fortune I: +0.001%, III: +0.002%, X: +0.003%
                return Math.log(enchantLevel + 1) * baseChance * 0.02;

            default:
                return 0;
        }
    }

    // ==================== BONUS-ROLL SYSTEM ====================

    /**
     * METHOD 2: Bonus-Rolls instead of chance increase
     *
     * Instead of increasing chance, Fortune/Looting gives
     * additional rolls ("Bonus-Rolls")
     *
     * Example with 0.1% chance:
     * - Fortune 0: 1 roll → 0.1% chance
     * - Fortune III: 4 rolls → ~0.4% chance (not 3.1%!)
     * - Fortune X: 11 rolls → ~1.1% chance
     *
     * @param baseChance Base chance in %
     * @param enchantLevel Fortune/Looting level
     * @return BonusRollResult with number of rolls and chance
     */
    public static BonusRollResult calculateBonusRolls(double baseChance, int enchantLevel) {
        if (enchantLevel <= 0) {
            return new BonusRollResult(1, baseChance);
        }

        DropRarity rarity = DropRarity.fromChance(baseChance);

        int bonusRolls = calculateBonusRollCount(enchantLevel, rarity);
        int totalRolls = 1 + bonusRolls;

        // Chance per roll stays the same!
        return new BonusRollResult(totalRolls, baseChance);
    }

    /**
     * Calculates number of bonus rolls based on rarity
     */
    private static int calculateBonusRollCount(int enchantLevel, DropRarity rarity) {
        switch (rarity) {
            case COMMON:
                // Full rolls: 1 per level
                return enchantLevel;

            case UNCOMMON:
                // Reduced: 1 roll per 2 levels
                return enchantLevel / 2;

            case RARE:
                // Heavily reduced: 1 roll per 3 levels
                return enchantLevel / 3;

            case VERY_RARE:
                // Minimal: 1 roll per 5 levels
                return enchantLevel / 5;

            case LEGENDARY:
                // Almost none: 1 roll per 10 levels
                return enchantLevel / 10;

            default:
                return 0;
        }
    }

    // ==================== HYBRID SYSTEM ====================

    /**
     * METHOD 3: Hybrid of chance boost and bonus rolls
     *
     * - Common items: Mainly chance boost
     * - Rare items: Mainly bonus rolls
     *
     * Best balance between both systems
     */
    public static HybridDropResult calculateHybridDrop(double baseChance, int enchantLevel) {
        if (enchantLevel <= 0) {
            return new HybridDropResult(baseChance, 1);
        }

        DropRarity rarity = DropRarity.fromChance(baseChance);

        double boostedChance;
        int totalRolls;

        switch (rarity) {
            case COMMON:
                // 80% chance boost, 20% bonus rolls
                boostedChance = baseChance + (enchantLevel * 0.8);
                totalRolls = 1 + (enchantLevel / 5);
                break;

            case UNCOMMON:
                // 50% chance boost, 50% bonus rolls
                boostedChance = baseChance + (enchantLevel * 0.4);
                totalRolls = 1 + (enchantLevel / 3);
                break;

            case RARE:
                // 20% chance boost, 80% bonus rolls
                boostedChance = baseChance + (Math.log(enchantLevel + 1) * baseChance * 0.1);
                totalRolls = 1 + (enchantLevel / 2);
                break;

            case VERY_RARE:
            case LEGENDARY:
                // Almost only bonus rolls
                boostedChance = baseChance + (Math.sqrt(enchantLevel) * baseChance * 0.05);
                totalRolls = 1 + enchantLevel;
                break;

            default:
                boostedChance = baseChance;
                totalRolls = 1;
        }

        // Soft cap
        boostedChance = Math.min(95.0, boostedChance);

        return new HybridDropResult(boostedChance, totalRolls);
    }

    // ==================== AMOUNT CALCULATION ====================

    /**
     * Calculates drop amount with Fortune/Looting
     *
     * Instead of increasing min-max, there's a chance for extra items
     *
     * @param minAmount Min amount
     * @param maxAmount Max amount
     * @param enchantLevel Fortune/Looting level
     * @return Final amount
     */
    public static int calculateDropAmount(int minAmount, int maxAmount, int enchantLevel) {
        // Base amount
        int baseAmount = minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));

        if (enchantLevel <= 0) {
            return baseAmount;
        }

        // Bonus items with decreasing chance
        int bonusItems = 0;

        for (int i = 0; i < enchantLevel; i++) {
            // Chance decreases per bonus item
            // Level 1: 100%, Level 2: 66%, Level 3: 50%, etc.
            double chance = 1.0 / (i + 1);

            if (Math.random() < chance) {
                bonusItems++;
            }
        }

        return baseAmount + bonusItems;
    }

    // ==================== HELPER CLASSES ====================

    public static class BonusRollResult {
        public final int rolls;
        public final double chancePerRoll;
        public final double totalChance;

        public BonusRollResult(int rolls, double chancePerRoll) {
            this.rolls = rolls;
            this.chancePerRoll = chancePerRoll;
            // Calculate cumulative chance: 1 - (1 - p)^n
            this.totalChance = Math.min(95.0,
                    100.0 * (1.0 - Math.pow(1.0 - chancePerRoll / 100.0, rolls)));
        }

        @Override
        public String toString() {
            return String.format("%d rolls @ %.2f%% = %.2f%% total",
                    rolls, chancePerRoll, totalChance);
        }
    }

    public static class HybridDropResult {
        public final double chancePerRoll;
        public final int rolls;
        public final double totalChance;

        public HybridDropResult(double chancePerRoll, int rolls) {
            this.chancePerRoll = Math.min(95.0, chancePerRoll);
            this.rolls = rolls;
            // Cumulative chance
            this.totalChance = Math.min(95.0,
                    100.0 * (1.0 - Math.pow(1.0 - this.chancePerRoll / 100.0, rolls)));
        }

        @Override
        public String toString() {
            return String.format("%d rolls @ %.2f%% = %.2f%% total",
                    rolls, chancePerRoll, totalChance);
        }
    }

    // ==================== DEBUG HELPERS (PAPER) ====================

    /**
     * Provides detailed info about drop calculation
     *
     * PAPER: As Adventure Component
     */
    public static Component getDropInfoComponent(double baseChance, int enchantLevel) {
        DropRarity rarity = DropRarity.fromChance(baseChance);

        // Method 1: Diminishing Returns
        double method1 = calculateDropChance(baseChance, enchantLevel);

        // Method 2: Bonus Rolls
        BonusRollResult method2 = calculateBonusRolls(baseChance, enchantLevel);

        // Method 3: Hybrid
        HybridDropResult method3 = calculateHybridDrop(baseChance, enchantLevel);

        return Component.text()
                .append(Component.text("Base: ", NamedTextColor.GRAY))
                .append(Component.text(String.format("%.2f%%", baseChance), NamedTextColor.WHITE))
                .append(Component.text(" (" + rarity + ")", NamedTextColor.DARK_GRAY))
                .appendNewline()

                .append(Component.text("Method 1 (Diminishing): ", NamedTextColor.YELLOW))
                .append(Component.text(String.format("%.2f%%", method1), NamedTextColor.GREEN))
                .append(Component.text(" (+" + String.format("%.2f%%", method1 - baseChance) + ")", NamedTextColor.DARK_GRAY))
                .appendNewline()

                .append(Component.text("Method 2 (Bonus Rolls): ", NamedTextColor.YELLOW))
                .append(Component.text(method2.toString(), NamedTextColor.GREEN))
                .appendNewline()

                .append(Component.text("Method 3 (Hybrid): ", NamedTextColor.YELLOW))
                .append(Component.text(method3.toString(), NamedTextColor.GREEN))

                .build();
    }

    /**
     * Legacy string version for logging
     */
    public static String getDropInfo(double baseChance, int enchantLevel) {
        DropRarity rarity = DropRarity.fromChance(baseChance);

        StringBuilder sb = new StringBuilder();
        sb.append("Base: ").append(String.format("%.2f%%", baseChance))
                .append(" (").append(rarity).append(")\n");

        // Method 1: Diminishing Returns
        double method1 = calculateDropChance(baseChance, enchantLevel);
        sb.append("Method 1 (Diminishing): ").append(String.format("%.2f%%", method1))
                .append(" (+" + String.format("%.2f%%", method1 - baseChance) + ")\n");

        // Method 2: Bonus Rolls
        BonusRollResult method2 = calculateBonusRolls(baseChance, enchantLevel);
        sb.append("Method 2 (Bonus Rolls): ").append(method2).append("\n");

        // Method 3: Hybrid
        HybridDropResult method3 = calculateHybridDrop(baseChance, enchantLevel);
        sb.append("Method 3 (Hybrid): ").append(method3).append("\n");

        return sb.toString();
    }

    /**
     * Compares all three methods
     *
     * PAPER: Console output for debug
     */
    public static void printComparison(double baseChance) {
        System.out.println("=== DROP COMPARISON: " + baseChance + "% ===\n");

        for (int level = 0; level <= 10; level += 3) {
            System.out.println("Fortune/Looting " + level + ":");
            System.out.println(getDropInfo(baseChance, level));
        }
    }
}