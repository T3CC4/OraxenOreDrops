package de.tecca.oraxenoredrops.model;

public class DropEntry {
    public final String oraxenItemId;
    public final double chance;
    public final int minAmount;
    public final int maxAmount;

    public DropEntry(String oraxenItemId, double chance, int minAmount, int maxAmount) {
        this.oraxenItemId = oraxenItemId;
        this.chance = chance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
}
