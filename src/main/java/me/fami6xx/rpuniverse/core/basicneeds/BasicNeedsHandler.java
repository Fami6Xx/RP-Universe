package me.fami6xx.rpuniverse.core.basicneeds;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.events.FoodTrackerListener;

public class BasicNeedsHandler {
    private RPUniverse plugin;

    public void initialize(RPUniverse plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new FoodTrackerListener(), plugin);
    }
}
