package me.fami6xx.rpuniverse.core.basicneeds;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;

/**
 * This class represents the configuration for the basic needs of the player in the game.
 * The configuration includes settings for hunger, thirst, poop, and pee.
 */
public class BasicNeedsConfig {
    private final boolean enabled;
    private final int interval;
    private final boolean preferPermissionsOverModeForEdit;
    private final PlayerMode neededModeToEdit;
    private final int removedHunger;
    private final int removedThirst;
    private final int addedPoop;
    private final int addedPee;

    /**
     * Constructs a new BasicNeedsConfig with the given plugin.
     *
     * @param plugin the plugin used to get the configuration
     */
    public BasicNeedsConfig(RPUniverse plugin){
        try{
            enabled = plugin.getConfig().getBoolean("basicNeeds.enabled");
            interval = plugin.getConfig().getInt("basicNeeds.interval");
            preferPermissionsOverModeForEdit = plugin.getConfig().getBoolean("basicNeeds.preferPermissionsOverModeForEdit");
            neededModeToEdit = PlayerMode.valueOf(plugin.getConfig().getString("basicNeeds.neededModeToEdit"));
            removedHunger = plugin.getConfig().getInt("basicNeeds.removedHunger");
            removedThirst = plugin.getConfig().getInt("basicNeeds.removedThirst");
            addedPoop = plugin.getConfig().getInt("basicNeeds.addedPoop");
            addedPee = plugin.getConfig().getInt("basicNeeds.addedPee");
        } catch (Exception e){
            plugin.getLogger().severe("Error while loading basic needs config.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            throw new RuntimeException("Error while loading basic needs config.");
        }
    }

    /**
     * Returns the interval of the basic needs.
     *
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Returns whether permissions are preferred over mode for edit.
     *
     * @return true if permissions are preferred, false otherwise
     */
    public boolean isPreferPermissionsOverModeForEdit() {
        return preferPermissionsOverModeForEdit;
    }

    /**
     * Returns the needed mode to edit.
     *
     * @return the needed mode to edit
     */
    public PlayerMode getNeededModeToEdit() {
        return neededModeToEdit;
    }

    /**
     * Returns the amount of hunger removed.
     *
     * @return the amount of hunger removed
     */
    public int getRemovedHunger() {
        return removedHunger;
    }

    /**
     * Returns the amount of thirst removed.
     *
     * @return the amount of thirst removed
     */
    public int getRemovedThirst() {
        return removedThirst;
    }

    /**
     * Returns the amount of poop added.
     *
     * @return the amount of poop added
     */
    public int getAddedPoop() {
        return addedPoop;
    }

    /**
     * Returns the amount of pee added.
     *
     * @return the amount of pee added
     */
    public int getAddedPee() {
        return addedPee;
    }

    /**
     * Returns whether the basic needs are enabled.
     *
     * @return true if the basic needs are enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
}