package me.fami6xx.rpuniverse.core.basicneeds;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.events.FoodTrackerListener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BasicNeedsHandler {
    private RPUniverse plugin;
    private final HashMap<ItemStack, ConsumableItem> consumables = new HashMap<>();
    private BasicNeedsConfig config;


    public void initialize(RPUniverse plugin) {
        this.plugin = plugin;
        this.config = new BasicNeedsConfig(plugin);

        plugin.getServer().getPluginManager().registerEvents(new FoodTrackerListener(), plugin);
    }

    /**
     * Get the consumable items.
     *
     * @return the consumable items
     */
    public HashMap<ItemStack, ConsumableItem> getConsumables() {
        return consumables;
    }

    /**
     * Add a consumable item to the handler.
     *
     * @param item the item to add
     * @param consumable the consumable item
     */
    public void addConsumable(ItemStack item, ConsumableItem consumable) {
        consumables.put(item, consumable);
    }

    /**
     * Get the consumable item for the given item.
     *
     * @param item the item to get the consumable item for
     * @return the consumable item
     */
    public ConsumableItem getConsumable(ItemStack item) {
        return consumables.get(item);
    }

    /**
     * Remove a consumable item from the handler.
     *
     * @param item the item to remove
     */
    public void removeConsumable(ItemStack item) {
        consumables.remove(item);
    }

    /**
     * Check if the given item is a consumable.
     *
     * @param item the item to check
     * @return true if the item is a consumable, false otherwise
     */
    public boolean isConsumable(ItemStack item) {
        return consumables.containsKey(item.asOne());
    }

    /**
     * Gets the basic need config.
     *
     * @return the basic needs config
     */
    public BasicNeedsConfig getConfig() {
        return config;
    }
}
