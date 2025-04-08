package me.fami6xx.rpuniverse.core.invoice;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.modules.AbstractModule;

/**
 * Module for invoice functionality.
 * <p>
 * This module provides a comprehensive framework for creating, managing, and tracking invoices between players.
 * It allows players in specific jobs to create invoices for other players, which can then be viewed, paid,
 * or deleted through an intuitive interface.
 * <p>
 * Key features include:
 * - Invoice creation for players in jobs
 * - Invoice management (view, pay, delete)
 * - Job integration for verification
 * - Distance and visibility checking
 * - Filtering options for invoices
 * - Data persistence using GSON
 */
public class InvoiceModule extends AbstractModule {
    
    private InvoiceManager manager;
    
    @Override
    public boolean initialize(RPUniverse plugin) {
        boolean result = super.initialize(plugin);
        if (!result) {
            return false;
        }
        
        try {
            // Initialize the invoice manager
            this.manager = new InvoiceManager(this);
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to initialize InvoiceModule", e);
            return false;
        }
    }
    
    @Override
    public boolean enable() {
        try {
            // Check if the module is enabled in the configuration
            if (!getConfigBoolean("enabled", true)) {
                ErrorHandler.debug("InvoiceModule is disabled in configuration");
                return false;
            }
            
            // Initialize the manager
            this.manager.initialize(getPlugin());
            
            // Register commands
            // Note: These command classes will need to be implemented
            // getPlugin().getCommand("invoices").setExecutor(new InvoiceCommand(this));
            // getPlugin().getCommand("createinvoice").setExecutor(new CreateInvoiceCommand(this));
            
            // Set up periodic data saving
            int saveInterval = getConfigInt("saveInterval", 5);
            if (saveInterval > 0) {
                getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(
                    getPlugin(),
                    () -> manager.saveData(),
                    saveInterval * 1200L, // Convert minutes to ticks (20 ticks/second * 60 seconds/minute)
                    saveInterval * 1200L
                );
            }
            
            ErrorHandler.debug("InvoiceModule enabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to enable InvoiceModule", e);
            return false;
        }
    }
    
    @Override
    public boolean disable() {
        try {
            if (manager != null) {
                // Save data before shutting down
                manager.saveData();
                manager.shutdown();
            }
            
            ErrorHandler.debug("InvoiceModule disabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to disable InvoiceModule", e);
            return false;
        }
    }
    
    @Override
    public String getName() {
        return "Invoices";
    }
    
    @Override
    public String getDescription() {
        return "Provides a comprehensive framework for creating, managing, and tracking invoices between players";
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
     * Gets the InvoiceManager instance.
     * 
     * @return The InvoiceManager instance
     */
    public InvoiceManager getManager() {
        return manager;
    }
    
    /**
     * Checks if the distance requirement is enabled in the configuration.
     * 
     * @return true if the distance requirement is enabled, false otherwise
     */
    public boolean isDistanceCheckEnabled() {
        return getConfigBoolean("mustSeePlayer", true);
    }
    
    /**
     * Gets the maximum distance between players for the invoice creation to be allowed.
     * 
     * @return the maximum distance
     */
    public double getMaxDistance() {
        return getConfigDouble("maxDistance", 5.0);
    }
    
    /**
     * Checks if the line-of-sight requirement is enabled in the configuration.
     * 
     * @return true if the line-of-sight requirement is enabled, false otherwise
     */
    public boolean isMustSeePlayerEnabled() {
        return getConfigBoolean("mustSeePlayer", true);
    }
    
    /**
     * Gets the default currency symbol to use.
     * 
     * @return the default currency symbol
     */
    public String getDefaultCurrency() {
        return getConfigString("defaultCurrency", "$");
    }
    
    /**
     * Checks if decimal amounts are allowed in invoices.
     * 
     * @return true if decimal amounts are allowed, false otherwise
     */
    public boolean isDecimalAmountAllowed() {
        return getConfigBoolean("allowDecimal", true);
    }
    
    /**
     * Checks if players should be notified about pending invoices when they join.
     * 
     * @return true if join notification is enabled, false otherwise
     */
    public boolean isJoinNotificationEnabled() {
        return getConfigBoolean("notifyOnJoin", true);
    }
}