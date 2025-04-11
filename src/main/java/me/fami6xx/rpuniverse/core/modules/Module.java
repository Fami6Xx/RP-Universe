package me.fami6xx.rpuniverse.core.modules;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Interface for all modules in the RPUniverse plugin.
 * <p>
 * Modules are components that can be enabled or disabled via configuration.
 * They provide specific functionality to the plugin and can be extended by third-party developers.
 * <p>
 * To create a new module, implement this interface and register it with the ModuleManager.
 */
public interface Module {

    /**
     * Called when the module is being initialized.
     * This is called before the module is enabled.
     * 
     * @param plugin The plugin instance
     * @return true if initialization was successful, false otherwise
     */
    boolean initialize(RPUniverse plugin);

    /**
     * Called when the module is being enabled.
     * This is called after initialization if the module is enabled in the configuration.
     * 
     * @return true if the module was enabled successfully, false otherwise
     */
    boolean enable();

    /**
     * Called when the module is being disabled.
     * This is called when the plugin is disabled or when the module is disabled via API.
     * 
     * @return true if the module was disabled successfully, false otherwise
     */
    boolean disable();

    /**
     * Called when the module is being shut down.
     * This is called when the plugin is disabled.
     * 
     * @return true if shutdown was successful, false otherwise
     */
    boolean shutdown();

    /**
     * Gets the name of the module.
     * This is used for configuration and logging.
     * 
     * @return The name of the module
     */
    String getName();

    /**
     * Gets the description of the module.
     * This is used for documentation and logging.
     * 
     * @return The description of the module
     */
    String getDescription();

    /**
     * Gets the version of the module.
     * This is used for documentation and logging.
     * 
     * @return The version of the module
     */
    String getVersion();

    /**
     * Gets the author of the module.
     * This is used for documentation and logging.
     * 
     * @return The author of the module
     */
    String getAuthor();

    /**
     * Gets the configuration section for this module.
     * This is used to get module-specific configuration.
     * 
     * @return The configuration section for this module, or null if not available
     */
    ConfigurationSection getConfig();

    /**
     * Checks if the module is enabled.
     * 
     * @return true if the module is enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Sets whether the module is enabled.
     * This should only be called by the ModuleManager.
     * 
     * @param enabled true to enable the module, false to disable it
     */
    void setEnabled(boolean enabled);

    /**
     * Gets the plugin instance.
     * 
     * @return The plugin instance
     */
    RPUniverse getPlugin();

    /**
     * Sets the plugin instance.
     * This should only be called by the ModuleManager.
     * 
     * @param plugin The plugin instance
     */
    void setPlugin(RPUniverse plugin);

    /**
     * Sets the configuration section for this module.
     * This should only be called by the ModuleManager.
     * 
     * @param config The configuration section for this module
     */
    void setConfig(ConfigurationSection config);

    /**
     * Saves the module's data.
     * <p>
     * This function is triggered by the DataSystem class every set period.
     */
    void saveData();
}