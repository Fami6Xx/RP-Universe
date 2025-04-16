package me.fami6xx.rpuniverse.core.misc.config;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles migration of configuration files between versions.
 * <p>
 * This class is responsible for updating older configuration versions to the latest version,
 * ensuring backward compatibility and preserving user settings.
 */
public class ConfigMigrator {

    // The current config version
    private static final int CURRENT_CONFIG_VERSION = 10;

    // Map of migration handlers for each version
    private static final Map<Integer, MigrationHandler> migrationHandlers = new HashMap<>();

    static {
        // Register migration handlers for each version
        migrationHandlers.put(4, ConfigMigrator::migrateFromV4ToV5);
        migrationHandlers.put(5, ConfigMigrator::migrateFromV5ToV6);
        migrationHandlers.put(6, ConfigMigrator::migrateFromV6ToV7);
        migrationHandlers.put(7, ConfigMigrator::migrateFromV7ToV8);
        migrationHandlers.put(8, ConfigMigrator::migrateFromV8ToV9);
        migrationHandlers.put(9, ConfigMigrator::migrateFromV9ToV10);
    }

    /**
     * Checks if the configuration needs migration and performs it if necessary.
     * 
     * @param config The current configuration
     * @param configFile The configuration file
     * @return true if migration was successful or not needed, false otherwise
     */
    public static boolean migrateConfig(FileConfiguration config, File configFile) {
        int version = config.getInt("configVersion", -1);

        // If version is not found or invalid, we can't migrate
        if (version == -1) {
            ErrorHandler.severe("Configuration version not found or invalid. Cannot migrate.");
            return false;
        }

        // If version is current, no migration needed
        if (version == CURRENT_CONFIG_VERSION) {
            ErrorHandler.debug("Configuration is already at the latest version (" + CURRENT_CONFIG_VERSION + ")");
            return true;
        }

        // If version is higher than current, something is wrong
        if (version > CURRENT_CONFIG_VERSION) {
            ErrorHandler.severe("Configuration version (" + version + ") is higher than the supported version (" + 
                               CURRENT_CONFIG_VERSION + "). This may cause issues.");
            return false;
        }

        ErrorHandler.info("Migrating configuration from version " + version + " to " + CURRENT_CONFIG_VERSION);

        // Create a backup of the current config
        if (!createBackup(configFile, version)) {
            ErrorHandler.severe("Failed to create backup of configuration. Migration aborted.");
            return false;
        }

        // Perform migrations sequentially
        FileConfiguration currentConfig = config;
        boolean success = true;

        while (version < CURRENT_CONFIG_VERSION && success) {
            MigrationHandler handler = migrationHandlers.get(version);

            if (handler == null) {
                ErrorHandler.severe("No migration handler found for version " + version + ". Migration aborted.");
                return false;
            }

            ErrorHandler.info("Migrating from version " + version + " to " + (version + 1));
            success = handler.migrate(currentConfig);

            if (success) {
                version++;
                currentConfig.set("configVersion", version);
                ErrorHandler.info("Successfully migrated to version " + version);
            } else {
                ErrorHandler.severe("Failed to migrate from version " + version + " to " + (version + 1));
            }
        }

        // Save the migrated config
        if (success) {
            try {
                currentConfig.save(configFile);
                ErrorHandler.info("Configuration successfully migrated to version " + CURRENT_CONFIG_VERSION);
            } catch (IOException e) {
                ErrorHandler.severe("Failed to save migrated configuration", e);
                success = false;
            }
        }

        return success;
    }

    /**
     * Creates a backup of the configuration file.
     * 
     * @param configFile The configuration file to backup
     * @param version The current version of the configuration
     * @return true if backup was successful, false otherwise
     */
    private static boolean createBackup(File configFile, int version) {
        try {
            File backupFile = new File(configFile.getParentFile(), "config_backup_v" + version + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.save(backupFile);
            ErrorHandler.info("Created backup of configuration at " + backupFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to create backup of configuration", e);
            return false;
        }
    }


    /**
     * Migrates configuration from version 4 to version 5.
     * 
     * @param config The configuration to migrate
     * @return true if migration was successful, false otherwise
     */
    private static boolean migrateFromV4ToV5(FileConfiguration config) {
        try {
            // Add new sections and fields introduced in version 5

            // Add debug section if it doesn't exist
            if (!config.contains("debug")) {
                config.createSection("debug");
                config.set("debug.enabled", false);
            }

            // Add modules section if it doesn't exist
            if (!config.contains("modules")) {
                config.createSection("modules");
            }

            // Add BasicNeeds module section if it doesn't exist
            if (!config.contains("modules.BasicNeeds")) {
                config.createSection("modules.BasicNeeds");
                config.set("modules.BasicNeeds.enabled", true);
            }

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error during migration from v4 to v5", e);
            return false;
        }
    }

    /**
     * Migrates configuration from version 5 to version 6.
     * 
     * @param config The configuration to migrate
     * @return true if migration was successful, false otherwise
     */
    private static boolean migrateFromV5ToV6(FileConfiguration config) {
        try {
            // Add new sections and fields introduced in version 6

            // Add regionVisualization section if it doesn't exist
            if (!config.contains("regionVisualization")) {
                config.createSection("regionVisualization");
                config.set("regionVisualization.step", 0.5);
                config.set("regionVisualization.maxRenderDistance", 50);
                config.set("regionVisualization.edgeOnly", true);
                config.set("regionVisualization.particleColor", "BLACK");
            }

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error during migration from v5 to v6", e);
            return false;
        }
    }

    /**
     * Migrates configuration from version 6 to version 7.
     * 
     * @param config The configuration to migrate
     * @return true if migration was successful, false otherwise
     */
    private static boolean migrateFromV6ToV7(FileConfiguration config) {
        try {
            // Add new sections and fields introduced in version 7

            // Add Payment module section if it doesn't exist
            if (!config.contains("modules.Payment")) {
                config.createSection("modules.Payment");
                config.set("modules.Payment.enabled", true);
                config.set("modules.Payment.commandEnabled", true);
                config.set("modules.Payment.distanceCheckEnabled", true);
                config.set("modules.Payment.maxDistance", 10.0);
                config.set("modules.Payment.lineOfSightCheckEnabled", true);
            }

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error during migration from v6 to v7", e);
            return false;
        }
    }

    /**
     * Migrates configuration from version 7 to version 8.
     * 
     * @param config The configuration to migrate
     * @return true if migration was successful, false otherwise
     */
    private static boolean migrateFromV7ToV8(FileConfiguration config) {
        try {
            // Add new sections and fields introduced in version 8

            // Add Invoices module section if it doesn't exist
            if (!config.contains("modules.Invoices")) {
                config.createSection("modules.Invoices");
                config.set("modules.Invoices.enabled", true);
                config.set("modules.Invoices.mustSeePlayer", true);
                config.set("modules.Invoices.maxDistance", 5.0);
                config.set("modules.Invoices.defaultCurrency", "$");
                config.set("modules.Invoices.saveInterval", 5);
                config.set("modules.Invoices.notifyOnJoin", true);
                config.set("modules.Invoices.allowDecimal", true);
            }

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error during migration from v7 to v8", e);
            return false;
        }
    }

    /**
     * Migrates configuration from version 8 to version 9.
     * 
     * @param config The configuration to migrate
     * @return true if migration was successful, false otherwise
     */
    private static boolean migrateFromV8ToV9(FileConfiguration config) {
        try {
            // Add new fields introduced in version 9

            // Add enableDistanceCheck field to Invoices module if it doesn't exist
            if (!config.contains("modules.Invoices.enableDistanceCheck")) {
                config.set("modules.Invoices.enableDistanceCheck", true);
            }

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error during migration from v8 to v9", e);
            return false;
        }
    }

    /**
     * Migrates configuration from version 9 to version 10.
     * 
     * @param config The configuration to migrate
     * @return true if migration was successful, false otherwise
     */
    private static boolean migrateFromV9ToV10(FileConfiguration config) {
        try {
            // Add new sections and fields introduced in version 10

            // Add updateNotification section if it doesn't exist
            if (!config.contains("updateNotification")) {
                config.createSection("updateNotification");
                config.set("updateNotification.enabled", true);
                config.set("updateNotification.notifyPermissionedPlayersOnJoin", true);
                config.set("updateNotification.permission", "rpu.update.notify");
            }

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error during migration from v9 to v10", e);
            return false;
        }
    }

    /**
     * Functional interface for migration handlers.
     */
    @FunctionalInterface
    private interface MigrationHandler {
        /**
         * Migrates the configuration to the next version.
         * 
         * @param config The configuration to migrate
         * @return true if migration was successful, false otherwise
         */
        boolean migrate(FileConfiguration config);
    }
}
