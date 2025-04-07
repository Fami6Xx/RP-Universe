package me.fami6xx.rpuniverse.core.basicneeds;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.commands.ConsumablesCommand;
import me.fami6xx.rpuniverse.core.basicneeds.commands.PeeCommand;
import me.fami6xx.rpuniverse.core.basicneeds.commands.PoopCommand;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.modules.AbstractModule;

/**
 * Module for basic needs functionality.
 * <p>
 * This module provides basic needs functionality like hunger, thirst, poop, and pee.
 * It wraps the existing BasicNeedsHandler and provides a module interface for it.
 */
public class BasicNeedsModule extends AbstractModule {
    
    private BasicNeedsHandler handler;
    
    @Override
    public boolean initialize(RPUniverse plugin) {
        boolean result = super.initialize(plugin);
        if (!result) {
            return false;
        }
        
        try {
            this.handler = new BasicNeedsHandler();
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to initialize BasicNeedsModule", e);
            return false;
        }
    }
    
    @Override
    public boolean enable() {
        try {
            // Check if the module is enabled in the configuration
            if (!getConfigBoolean("enabled", true)) {
                ErrorHandler.info("BasicNeedsModule is disabled in configuration");
                return false;
            }
            
            // Initialize the handler
            this.handler.initialize(getPlugin());
            
            // Register commands
            getPlugin().getCommand("consumables").setExecutor(new ConsumablesCommand());
            getPlugin().getCommand("poop").setExecutor(new PoopCommand());
            getPlugin().getCommand("pee").setExecutor(new PeeCommand());
            
            ErrorHandler.info("BasicNeedsModule enabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to enable BasicNeedsModule", e);
            return false;
        }
    }
    
    @Override
    public boolean disable() {
        try {
            if (handler != null) {
                handler.shutdown();
            }
            
            ErrorHandler.info("BasicNeedsModule disabled");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to disable BasicNeedsModule", e);
            return false;
        }
    }
    
    @Override
    public String getName() {
        return "BasicNeeds";
    }
    
    @Override
    public String getDescription() {
        return "Provides basic needs functionality like hunger, thirst, poop, and pee";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getAuthor() {
        return "fami6xx";
    }
    
    /**
     * Gets the BasicNeedsHandler instance.
     * 
     * @return The BasicNeedsHandler instance
     */
    public BasicNeedsHandler getHandler() {
        return handler;
    }
}