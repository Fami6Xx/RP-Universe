package me.fami6xx.rpuniverse.core.modules;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Abstract base class for modules in the RPUniverse plugin.
 * <p>
 * This class provides a basic implementation of the Module interface,
 * handling common functionality like storing the plugin instance,
 * configuration, and enabled state.
 * <p>
 * Extend this class to create a new module with minimal boilerplate code.
 */
public abstract class AbstractModule implements Module {

    private RPUniverse plugin;
    private ConfigurationSection config;
    private boolean enabled = false;

    @Override
    public boolean initialize(RPUniverse plugin) {
        this.plugin = plugin;
        return true;
    }

    @Override
    public boolean shutdown() {
        if (isEnabled()) {
            return disable();
        }
        return true;
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public RPUniverse getPlugin() {
        return plugin;
    }

    @Override
    public void setPlugin(RPUniverse plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setConfig(ConfigurationSection config) {
        this.config = config;
    }

    /**
     * Gets a configuration value with a default fallback.
     * 
     * @param path The path to the configuration value
     * @param defaultValue The default value to return if the path doesn't exist
     * @param <T> The type of the value
     * @return The configuration value, or the default value if not found
     */
    protected <T> T getConfigValue(String path, T defaultValue) {
        if (config == null) {
            ErrorHandler.warning("Module " + getName() + " tried to access config before it was set");
            return defaultValue;
        }

        Object value = config.get(path);
        if (value == null) {
            return defaultValue;
        }

        try {
            @SuppressWarnings("unchecked")
            T typedValue = (T) value;
            return typedValue;
        } catch (ClassCastException e) {
            ErrorHandler.warning("Module " + getName() + " tried to access config value with wrong type: " + path);
            return defaultValue;
        }
    }

    /**
     * Gets a boolean configuration value with a default fallback.
     * 
     * @param path The path to the configuration value
     * @param defaultValue The default value to return if the path doesn't exist
     * @return The boolean configuration value, or the default value if not found
     */
    protected boolean getConfigBoolean(String path, boolean defaultValue) {
        if (config == null) {
            ErrorHandler.warning("Module " + getName() + " tried to access config before it was set");
            return defaultValue;
        }

        return config.getBoolean(path, defaultValue);
    }

    /**
     * Gets a string configuration value with a default fallback.
     * 
     * @param path The path to the configuration value
     * @param defaultValue The default value to return if the path doesn't exist
     * @return The string configuration value, or the default value if not found
     */
    protected String getConfigString(String path, String defaultValue) {
        if (config == null) {
            ErrorHandler.warning("Module " + getName() + " tried to access config before it was set");
            return defaultValue;
        }

        return config.getString(path, defaultValue);
    }

    /**
     * Gets an integer configuration value with a default fallback.
     * 
     * @param path The path to the configuration value
     * @param defaultValue The default value to return if the path doesn't exist
     * @return The integer configuration value, or the default value if not found
     */
    protected int getConfigInt(String path, int defaultValue) {
        if (config == null) {
            ErrorHandler.warning("Module " + getName() + " tried to access config before it was set");
            return defaultValue;
        }

        return config.getInt(path, defaultValue);
    }

    /**
     * Gets a double configuration value with a default fallback.
     * 
     * @param path The path to the configuration value
     * @param defaultValue The default value to return if the path doesn't exist
     * @return The double configuration value, or the default value if not found
     */
    protected double getConfigDouble(String path, double defaultValue) {
        if (config == null) {
            ErrorHandler.warning("Module " + getName() + " tried to access config before it was set");
            return defaultValue;
        }

        return config.getDouble(path, defaultValue);
    }

    /**
     * Gets a long configuration value with a default fallback.
     * 
     * @param path The path to the configuration value
     * @param defaultValue The default value to return if the path doesn't exist
     * @return The long configuration value, or the default value if not found
     */
    protected long getConfigLong(String path, long defaultValue) {
        if (config == null) {
            ErrorHandler.warning("Module " + getName() + " tried to access config before it was set");
            return defaultValue;
        }

        return config.getLong(path, defaultValue);
    }

    /**
     * Gets a configuration section with a default fallback.
     * 
     * @param path The path to the configuration section
     * @return The configuration section, or null if not found
     */
    protected ConfigurationSection getConfigSection(String path) {
        if (config == null) {
            ErrorHandler.warning("Module " + getName() + " tried to access config before it was set");
            return null;
        }

        return config.getConfigurationSection(path);
    }

    @Override
    public void saveData() {
        // Default implementation - can be overridden by specific modules
        if (config != null && plugin != null) {
            try {
                // Save the module's configuration if it exists
                plugin.saveConfig();
            } catch (Exception e) {
                ErrorHandler.warning("Failed to save data for module " + getName() + ": " + e.getMessage());
            }
        }
    }
}
