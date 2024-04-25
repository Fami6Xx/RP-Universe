package me.fami6xx.rpuniverse.core.basicneeds;

/**
 * This class represents a consumable item in the game.
 * Each consumable item has a food, water, health, poop, and pee value.
 */
public class ConsumableItem {
    private int food;
    private int water;
    private int health;
    private int poop;
    private int pee;

    /**
     * Constructs a new ConsumableItem with the given values.
     *
     * @param food the food value of the item
     * @param water the water value of the item
     * @param health the health value of the item
     * @param poop the poop value of the item
     * @param pee the pee value of the item
     */
    public ConsumableItem(int food, int water, int health, int poop, int pee) {
        this.food = food;
        this.water = water;
        this.health = health;
        this.poop = poop;
        this.pee = pee;
    }

    /**
     * Returns the food value of the item.
     *
     * @return the food value
     */
    public int getFood() {
        return food;
    }

    /**
     * Sets the food value of the item and returns the new value.
     *
     * @param food the new food value
     * @return the new food value
     */
    public int setFood(int food) {
        return this.food = food;
    }

    /**
     * Returns the water value of the item.
     *
     * @return the water value
     */
    public int getWater() {
        return water;
    }

    /**
     * Sets the water value of the item and returns the new value.
     *
     * @param water the new water value
     * @return the new water value
     */
    public int setWater(int water) {
        return this.water = water;
    }

    /**
     * Returns the health value of the item.
     *
     * @return the health value
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health value of the item and returns the new value.
     *
     * @param health the new health value
     * @return the new health value
     */
    public int setHealth(int health) {
        return this.health = health;
    }

    /**
     * Returns the poop value of the item.
     *
     * @return the poop value
     */
    public int getPoop() {
        return poop;
    }

    /**
     * Sets the poop value of the item and returns the new value.
     *
     * @param poop the new poop value
     * @return the new poop value
     */
    public int setPoop(int poop) {
        return this.poop = poop;
    }

    /**
     * Returns the pee value of the item.
     *
     * @return the pee value
     */
    public int getPee() {
        return pee;
    }

    /**
     * Sets the pee value of the item and returns the new value.
     *
     * @param pee the new pee value
     * @return the new pee value
     */
    public int setPee(int pee) {
        return this.pee = pee;
    }
}