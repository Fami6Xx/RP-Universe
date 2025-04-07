package me.fami6xx.rpuniverse.core.misc.config;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Manages the configuration system for RPUniverse.
 * <p>
 * This class is responsible for loading, validating, migrating, and providing access to the configuration.
 * It integrates the ConfigValidator and ConfigMigrator classes to ensure the configuration is valid and up-to-date.
 */
public class ConfigManager {

    private final RPUniverse plugin;
    private FileConfiguration config;
    private File configFile;
    private boolean isValid = false;

    /**
     * Creates a new ConfigManager.
     *
     * @param plugin The plugin instance
     */
    public ConfigManager(RPUniverse plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    /**
     * Loads the configuration file.
     * If the file doesn't exist, it will be created with default values.
     * The configuration will be validated and migrated if necessary.
     *
     * @return true if the configuration was loaded successfully, false otherwise
     */
    public boolean loadConfig() {
        // Create plugin directory if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            if (!plugin.getDataFolder().mkdirs()) {
                ErrorHandler.severe("Failed to create plugin directory");
                return false;
            }
        }

        // Create config file if it doesn't exist
        if (!configFile.exists()) {
            ErrorHandler.info("Config file not found, creating default config");
            plugin.saveDefaultConfig();
        }

        // Load the config
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            ErrorHandler.debug("Loaded configuration from " + configFile.getAbsolutePath());
        } catch (Exception e) {
            ErrorHandler.severe("Failed to load configuration", e);
            return false;
        }

        // Compare with default config to add missing values
        InputStream defaultConfigStream = plugin.getResource("config.yml");
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8));
            
            // Add missing keys from default config
            for (String key : defaultConfig.getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defaultConfig.get(key));
                    ErrorHandler.debug("Added missing key to config: " + key);
                }
            }
        }

        // Migrate the config if necessary
        if (!ConfigMigrator.migrateConfig(config, configFile)) {
            ErrorHandler.warning("Configuration migration failed or was not needed");
            // Continue loading even if migration fails, as validation will catch issues
        }

        // Validate the config
        isValid = ConfigValidator.validateConfig(config);
        if (!isValid) {
            ErrorHandler.warning("Configuration validation failed. Some features may not work correctly.");
            // Continue loading even if validation fails, to allow the plugin to function with defaults
        } else {
            ErrorHandler.info("Configuration loaded and validated successfully");
        }

        // Save any changes made during migration or validation
        try {
            config.save(configFile);
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save configuration after migration/validation", e);
            return false;
        }

        return true;
    }

    /**
     * Gets the configuration.
     *
     * @return The configuration
     */
    public FileConfiguration getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    /**
     * Reloads the configuration.
     *
     * @return true if the configuration was reloaded successfully, false otherwise
     */
    public boolean reloadConfig() {
        // Save any changes before reloading
        saveConfig();
        
        // Reload the config
        return loadConfig();
    }

    /**
     * Saves the configuration.
     *
     * @return true if the configuration was saved successfully, false otherwise
     */
    public boolean saveConfig() {
        if (config == null || configFile == null) {
            ErrorHandler.warning("Cannot save config: config or configFile is null");
            return false;
        }

        try {
            config.save(configFile);
            ErrorHandler.debug("Saved configuration to " + configFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save configuration", e);
            return false;
        }
    }

    /**
     * Checks if the configuration is valid.
     *
     * @return true if the configuration is valid, false otherwise
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Gets the configuration file.
     *
     * @return The configuration file
     */
    public File getConfigFile() {
        return configFile;
    }
}