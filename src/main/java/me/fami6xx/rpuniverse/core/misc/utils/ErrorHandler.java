package me.fami6xx.rpuniverse.core.misc.utils;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized error handling and logging system for RPUniverse.
 * <p>
 * This class provides methods for logging messages at different severity levels
 * and includes a debug mode that can be enabled in the configuration.
 */
public class ErrorHandler {
    private static final Logger logger = RPUniverse.getInstance().getLogger();
    private static boolean debugMode = false;
    
    /**
     * Initializes the ErrorHandler with configuration settings.
     * Should be called during plugin startup.
     */
    public static void init() {
        FileConfiguration config = RPUniverse.getInstance().getConfiguration();
        debugMode = config.getBoolean("debug.enabled", false);
        
        if (debugMode) {
            info("Debug mode is enabled");
        }
    }
    
    /**
     * Logs a severe error message.
     * 
     * @param message The error message
     */
    public static void severe(String message) {
        logger.severe(message);
    }
    
    /**
     * Logs a severe error message with an exception.
     * 
     * @param message The error message
     * @param e The exception that occurred
     */
    public static void severe(String message, Throwable e) {
        logger.log(Level.SEVERE, message, e);
    }
    
    /**
     * Logs a warning message.
     * 
     * @param message The warning message
     */
    public static void warning(String message) {
        logger.warning(message);
    }
    
    /**
     * Logs a warning message with an exception.
     * 
     * @param message The warning message
     * @param e The exception that occurred
     */
    public static void warning(String message, Throwable e) {
        logger.log(Level.WARNING, message, e);
    }
    
    /**
     * Logs an info message.
     * 
     * @param message The info message
     */
    public static void info(String message) {
        logger.info(message);
    }
    
    /**
     * Logs a debug message if debug mode is enabled.
     * 
     * @param message The debug message
     */
    public static void debug(String message) {
        if (debugMode) {
            logger.info("[DEBUG] " + message);
        }
    }
    
    /**
     * Logs a debug message with an exception if debug mode is enabled.
     * 
     * @param message The debug message
     * @param e The exception that occurred
     */
    public static void debug(String message, Throwable e) {
        if (debugMode) {
            logger.log(Level.INFO, "[DEBUG] " + message, e);
        }
    }
    
    /**
     * Checks if debug mode is enabled.
     * 
     * @return true if debug mode is enabled, false otherwise
     */
    public static boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * Sets the debug mode.
     * 
     * @param enabled true to enable debug mode, false to disable
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        if (enabled) {
            info("Debug mode has been enabled");
        } else {
            info("Debug mode has been disabled");
        }
    }
}