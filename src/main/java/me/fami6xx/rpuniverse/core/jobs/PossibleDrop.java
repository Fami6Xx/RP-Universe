package me.fami6xx.rpuniverse.core.jobs;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Represents a possible drop that can occur upon completing a working step.
 * Holds an ItemStack and the percentage chance that it will drop.
 */
public class PossibleDrop {
    private final ItemStack item;
    private double chance; // 0.0 to 100.0

    /**
     * Creates a new PossibleDrop with the specified item and chance.
     *
     * @param item   The ItemStack that might drop.
     * @param chance The probability (0.0 to 100.0) that the item will drop.
     *               Values outside of this range will throw an IllegalArgumentException.
     */
    public PossibleDrop(@Nonnull ItemStack item, double chance) {
        if (chance < 0.0 || chance > 100.0) {
            throw new IllegalArgumentException("Chance must be between 0 and 100.");
        }
        this.item = item;
        this.chance = chance;
    }

    /**
     * @return The ItemStack associated with this drop.
     */
    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    /**
     * @return The percentage chance (0.0 to 100.0) that this item will drop.
     */
    public double getChance() {
        return chance;
    }

    /**
     * Sets the chance that this item will drop.
     * <p>
     * Values outside the range 0.0 to 100.0 will throw an IllegalArgumentException.
     *
     * @param chance The probability (0.0 to 100.0) that the item will drop.
     */
    public void setChance(double chance) {
        if (chance < 0.0 || chance > 100.0) {
            throw new IllegalArgumentException("Chance must be between 0 and 100.");
        }
        this.chance = chance;
    }
}
