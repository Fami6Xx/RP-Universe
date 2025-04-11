package me.fami6xx.rpuniverse.core.payment;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.modules.AbstractModule;
import me.fami6xx.rpuniverse.core.payment.commands.PayCommand;

/**
 * Module for custom payment functionality.
 * <p>
 * This module provides a custom implementation of the /pay command that works only
 * within a configurable distance and only when the player can see the other player.
 */
public class PaymentModule extends AbstractModule {

    @Override
    public boolean initialize(RPUniverse plugin) {
        boolean result = super.initialize(plugin);
        if (!result) {
            return false;
        }

        try {
            // No special initialization needed
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to initialize PaymentModule", e);
            return false;
        }
    }

    @Override
    public boolean enable() {
        try {
            // Check if the module is enabled in the configuration
            if (!getConfigBoolean("enabled", true)) {
                ErrorHandler.debug("PaymentModule is disabled in configuration");
                return false;
            }

            // Register commands
            getPlugin().getCommand("pay").setExecutor(new PayCommand(this));

            ErrorHandler.debug("PaymentModule enabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to enable PaymentModule", e);
            return false;
        }
    }

    @Override
    public boolean disable() {
        try {
            ErrorHandler.debug("PaymentModule disabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to disable PaymentModule", e);
            return false;
        }
    }

    @Override
    public String getName() {
        return "Payment";
    }

    @Override
    public String getDescription() {
        return "Provides a custom implementation of the /pay command with distance and line-of-sight requirements";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getAuthor() {
        return "Fami6Xx";
    }

    /**
     * Checks if the command is enabled in the configuration.
     * 
     * @return true if the command is enabled, false otherwise
     */
    public boolean isCommandEnabled() {
        return getConfigBoolean("commandEnabled", true);
    }

    /**
     * Checks if the distance requirement is enabled in the configuration.
     * 
     * @return true if the distance requirement is enabled, false otherwise
     */
    public boolean isDistanceCheckEnabled() {
        return getConfigBoolean("distanceCheckEnabled", true);
    }

    /**
     * Gets the maximum distance between players for the payment to be allowed.
     * 
     * @return the maximum distance
     */
    public double getMaxDistance() {
        return getConfigDouble("maxDistance", 10.0);
    }

    /**
     * Checks if the line-of-sight requirement is enabled in the configuration.
     * 
     * @return true if the line-of-sight requirement is enabled, false otherwise
     */
    public boolean isLineOfSightCheckEnabled() {
        return getConfigBoolean("lineOfSightCheckEnabled", true);
    }

    /**
     * Saves the module's data.
     * <p>
     * This method is called periodically by the DataSystem and when the plugin is disabled.
     * The PaymentModule doesn't have any persistent data beyond its configuration,
     * so it just calls the parent implementation.
     */
    @Override
    public void saveData() {
        try {
            // Call the parent implementation to save the config
            super.saveData();
            ErrorHandler.debug("Saved data for PaymentModule");
        } catch (Exception e) {
            ErrorHandler.warning("Failed to save data for PaymentModule: " + e.getMessage());
        }
    }
}
