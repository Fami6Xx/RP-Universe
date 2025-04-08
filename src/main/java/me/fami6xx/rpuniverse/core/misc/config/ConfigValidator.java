package me.fami6xx.rpuniverse.core.misc.config;

import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Validates the configuration file for RPUniverse.
 * <p>
 * This class checks that all required fields are present and have the correct data types.
 * It also provides helpful error messages for invalid configurations.
 */
public class ConfigValidator {

    /**
     * Validates the entire configuration file.
     *
     * @param config The configuration to validate
     * @return true if the configuration is valid, false otherwise
     */
    public static boolean validateConfig(FileConfiguration config) {
        ErrorHandler.debug("Validating configuration...");
        List<String> validationErrors = new ArrayList<>();

        // Check config version
        if (!config.contains("configVersion")) {
            validationErrors.add("Missing required field: configVersion");
        } else if (!config.isInt("configVersion")) {
            validationErrors.add("Invalid type for configVersion: expected integer");
        }

        // Validate general section
        validateGeneralSection(config, validationErrors);

        // Validate jobs section
        validateJobsSection(config, validationErrors);

        // Validate holograms section
        validateHologramsSection(config, validationErrors);

        // Validate data section
        validateDataSection(config, validationErrors);

        // Validate properties section
        validatePropertiesSection(config, validationErrors);

        // Validate basicNeeds section
        validateBasicNeedsSection(config, validationErrors);

        // Validate chestLimit section
        validateChestLimitSection(config, validationErrors);

        // Validate inventoryLimit section
        validateInventoryLimitSection(config, validationErrors);

        // Validate balance section
        validateBalanceSection(config, validationErrors);

        // Validate debug section
        validateDebugSection(config, validationErrors);

        // Validate modules section
        validateModulesSection(config, validationErrors);

        // Report validation errors
        if (!validationErrors.isEmpty()) {
            ErrorHandler.warning("Configuration validation failed with " + validationErrors.size() + " errors:");
            for (String error : validationErrors) {
                ErrorHandler.warning("- " + error);
            }
            return false;
        }

        ErrorHandler.debug("Configuration validation successful");
        return true;
    }

    /**
     * Validates the general section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateGeneralSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("general")) {
            errors.add("Missing required section: general");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("general");
        validateBoolean(section, "bossBarEnabled", errors);
        validateString(section, "bossBarColor", errors, Arrays.asList("BLUE", "GREEN", "PINK", "PURPLE", "RED", "WHITE", "YELLOW"));
        validateBoolean(section, "hideNicknames", errors);
        validateBoolean(section, "localOOC", errors);
        validateInt(section, "localOOCRange", errors);
        validateString(section, "localOOCFormat", errors);
        validateBoolean(section, "logLocalToConsole", errors);
        validateBoolean(section, "globalOOC", errors);
        validateString(section, "globalOOCFormat", errors);
    }

    /**
     * Validates the jobs section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateJobsSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("jobs")) {
            errors.add("Missing required section: jobs");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("jobs");
        validateBoolean(section, "preferPermissionsOverModeForEdit", errors);
        validateString(section, "neededModeToEditJobs", errors, Arrays.asList("ADMIN", "MODERATOR"));
        validateBoolean(section, "needsPermissionToHaveMultipleJobs", errors);
        validateInt(section, "maxJobsPerPlayer", errors);
        validateInt(section, "menuRange", errors);
        validateInt(section, "distanceToAddToJob", errors);
    }

    /**
     * Validates the holograms section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateHologramsSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("holograms")) {
            errors.add("Missing required section: holograms");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("holograms");
        validateInt(section, "range", errors);
        validateInt(section, "timeAlive", errors);
        validateInt(section, "maximumAbovePlayer", errors);
    }

    /**
     * Validates the data section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateDataSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("data")) {
            errors.add("Missing required section: data");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("data");
        validateInt(section, "saveInterval", errors);
        validateInt(section, "completeSaveInterval", errors);
        validateString(section, "selectedSaveMethod", errors, Arrays.asList("json"));
    }

    /**
     * Validates the properties section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validatePropertiesSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("properties")) {
            errors.add("Missing required section: properties");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("properties");
        validateBoolean(section, "unlockedByDefault", errors);
    }

    /**
     * Validates the basicNeeds section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateBasicNeedsSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("basicNeeds")) {
            errors.add("Missing required section: basicNeeds");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("basicNeeds");
        validateBoolean(section, "enabled", errors);
        validateBoolean(section, "sendToActionBar", errors);
        validateInt(section, "interval", errors);
        validateBoolean(section, "ignoreInMode", errors);
        validateBoolean(section, "preferPermissionsOverModeForEdit", errors);
        validateString(section, "neededModeToEdit", errors, Arrays.asList("ADMIN", "MODERATOR"));
        validateInt(section, "removedHunger", errors);
        validateInt(section, "removedThirst", errors);
        validateInt(section, "addedPoop", errors);
        validateInt(section, "addedPee", errors);
    }

    /**
     * Validates the chestLimit section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateChestLimitSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("chestLimit")) {
            errors.add("Missing required section: chestLimit");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("chestLimit");
        validateBoolean(section, "enabled", errors);
        validateInt(section, "single-chest-rows", errors);
        validateInt(section, "double-chest-rows", errors);
    }

    /**
     * Validates the inventoryLimit section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateInventoryLimitSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("inventoryLimit")) {
            errors.add("Missing required section: inventoryLimit");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("inventoryLimit");
        validateBoolean(section, "enabled", errors);
    }

    /**
     * Validates the balance section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateBalanceSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("balance")) {
            errors.add("Missing required section: balance");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("balance");
        validateBoolean(section, "enableTracker", errors);
        validateInt(section, "check-interval", errors);
        validateString(section, "discordWebhookURL", errors);
    }

    /**
     * Validates the debug section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateDebugSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("debug")) {
            errors.add("Missing required section: debug");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("debug");
        validateBoolean(section, "enabled", errors);
    }

    /**
     * Validates the modules section of the configuration.
     *
     * @param config The configuration to validate
     * @param errors List to add validation errors to
     */
    private static void validateModulesSection(FileConfiguration config, List<String> errors) {
        if (!config.contains("modules")) {
            errors.add("Missing required section: modules");
            return;
        }

        ConfigurationSection section = config.getConfigurationSection("modules");
        Set<String> moduleKeys = section.getKeys(false);

        for (String moduleKey : moduleKeys) {
            ConfigurationSection moduleSection = section.getConfigurationSection(moduleKey);
            if (moduleSection != null) {
                validateBoolean(moduleSection, "enabled", errors);

                // Validate specific module sections
                if (moduleKey.equals("Invoices")) {
                    validateInvoicesModuleSection(moduleSection, errors);
                }
            }
        }
    }

    /**
     * Validates the Invoices module section of the configuration.
     *
     * @param section The Invoices module section
     * @param errors List to add validation errors to
     */
    private static void validateInvoicesModuleSection(ConfigurationSection section, List<String> errors) {
        validateBoolean(section, "mustSeePlayer", errors);
        validateDouble(section, "maxDistance", errors);
        validateString(section, "defaultCurrency", errors);
        validateInt(section, "saveInterval", errors);
        validateBoolean(section, "notifyOnJoin", errors);
        validateBoolean(section, "allowDecimal", errors);
    }

    /**
     * Validates a boolean configuration value.
     *
     * @param section The configuration section
     * @param key The key to validate
     * @param errors List to add validation errors to
     */
    private static void validateBoolean(ConfigurationSection section, String key, List<String> errors) {
        if (section == null) {
            errors.add("Section is null when validating " + key);
            return;
        }

        if (!section.contains(key)) {
            errors.add("Missing required field: " + section.getCurrentPath() + "." + key);
        } else if (!section.isBoolean(key)) {
            errors.add("Invalid type for " + section.getCurrentPath() + "." + key + ": expected boolean");
        }
    }

    /**
     * Validates an integer configuration value.
     *
     * @param section The configuration section
     * @param key The key to validate
     * @param errors List to add validation errors to
     */
    private static void validateInt(ConfigurationSection section, String key, List<String> errors) {
        if (section == null) {
            errors.add("Section is null when validating " + key);
            return;
        }

        if (!section.contains(key)) {
            errors.add("Missing required field: " + section.getCurrentPath() + "." + key);
        } else if (!section.isInt(key)) {
            errors.add("Invalid type for " + section.getCurrentPath() + "." + key + ": expected integer");
        }
    }

    /**
     * Validates a string configuration value.
     *
     * @param section The configuration section
     * @param key The key to validate
     * @param errors List to add validation errors to
     */
    private static void validateString(ConfigurationSection section, String key, List<String> errors) {
        if (section == null) {
            errors.add("Section is null when validating " + key);
            return;
        }

        if (!section.contains(key)) {
            errors.add("Missing required field: " + section.getCurrentPath() + "." + key);
        } else if (!section.isString(key)) {
            errors.add("Invalid type for " + section.getCurrentPath() + "." + key + ": expected string");
        }
    }

    /**
     * Validates a string configuration value against a list of valid values.
     *
     * @param section The configuration section
     * @param key The key to validate
     * @param errors List to add validation errors to
     * @param validValues List of valid values
     */
    private static void validateString(ConfigurationSection section, String key, List<String> errors, List<String> validValues) {
        if (section == null) {
            errors.add("Section is null when validating " + key);
            return;
        }

        if (!section.contains(key)) {
            errors.add("Missing required field: " + section.getCurrentPath() + "." + key);
        } else if (!section.isString(key)) {
            errors.add("Invalid type for " + section.getCurrentPath() + "." + key + ": expected string");
        } else {
            String value = section.getString(key);
            if (validValues != null && !validValues.contains(value)) {
                errors.add("Invalid value for " + section.getCurrentPath() + "." + key + ": " + value + 
                           ". Valid values are: " + String.join(", ", validValues));
            }
        }
    }

    /**
     * Validates a double configuration value.
     *
     * @param section The configuration section
     * @param key The key to validate
     * @param errors List to add validation errors to
     */
    private static void validateDouble(ConfigurationSection section, String key, List<String> errors) {
        if (section == null) {
            errors.add("Section is null when validating " + key);
            return;
        }

        if (!section.contains(key)) {
            errors.add("Missing required field: " + section.getCurrentPath() + "." + key);
        } else if (!section.isDouble(key) && !section.isInt(key)) {
            errors.add("Invalid type for " + section.getCurrentPath() + "." + key + ": expected double");
        }
    }
}
