package me.fami6xx.rpuniverse.core.misc.language;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.language.editor.LanguageField;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages reflection and data for the LanguageHandler fields,
 * returning a combined list of core fields (declared in LanguageHandler)
 * and addon translations (stored in a hashmap).
 * When updating a field, if it is an addon field, the update is also pushed
 * to the addon language instance.
 */
public class LanguageFieldsManager {

    /**
     * Returns a list of LanguageField objects for each String field
     * in the live LanguageHandler (core translations) as well as all addon translations.
     */
    public static List<LanguageField> getAllLanguageFields() {
        // The "live" handler that has the current config values.
        LanguageHandler liveHandler = RPUniverse.getLanguageHandler();
        // A "default" handler to see what was written in code for core translations.
        LanguageHandler defaultHandler = new LanguageHandler(RPUniverse.getInstance());

        List<LanguageField> fields = new ArrayList<>();

        // Core language fields (declared in LanguageHandler)
        for (Field field : liveHandler.getClass().getDeclaredFields()) {
            if (field.getType() == String.class) {
                field.setAccessible(true);
                try {
                    // The current config-based value.
                    String currentValue = (String) field.get(liveHandler);
                    // The original code default.
                    String defaultValue = (String) field.get(defaultHandler);
                    // multiLine = true if the default code string contains '~'
                    boolean multiLine = (defaultValue != null && defaultValue.contains("~"));
                    fields.add(new LanguageField(field, field.getName(), currentValue, multiLine));
                } catch (IllegalAccessException e) {
                    ErrorHandler.severe(
                            "Error accessing core language field '" + field.getName() + "': " + e.getMessage());
                }
            }
        }

        // Add addon translation fields from the hashmap.
        for (Map.Entry<String, String> entry : liveHandler.getAddonTranslations().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            boolean multiLine = (value != null && value.contains("~"));



            fields.add(new LanguageField(key, value, multiLine));
        }
        return fields;
    }

    /**
     * Applies the newValue to the live LanguageHandler,
     * then updates languages.yml so changes persist.
     * If the field is an addon field (reflectionField==null), the update is also pushed
     * to the addon language instance.
     */
    public static void setLanguageFieldValue(LanguageField languageField, String newValue) {
        LanguageHandler lh = RPUniverse.getLanguageHandler();
        if (languageField.getReflectionField() != null) {
            Field field = languageField.getReflectionField();
            field.setAccessible(true);
            try {
                // Update the live core language field.
                field.set(lh, newValue);
            } catch (IllegalAccessException e) {
                ErrorHandler.severe(
                        "Error updating core language field '" + field.getName() + "': " + e.getMessage());
                e.printStackTrace();
            }
            // Also update the config for core fields.
            FileConfiguration langCfg = lh.getLanguageConfig();
            langCfg.set(field.getName(), newValue);
        } else {
            // This is an addon translation field.
            lh.setAddonTranslation(languageField.getFieldName(), newValue);
            // Additionally, update the addon language instance.
            // The key format is: addon.<SimpleClassName>.<fieldName>
            String key = languageField.getFieldName();
            String[] parts = key.split("\\.");
            if (parts.length >= 3) {
                String simpleClassName = parts[1];
                String fieldName = parts[2];
                AbstractAddonLanguage addonInstance = AbstractAddonLanguage.getAddonLanguage(simpleClassName);
                if (addonInstance != null) {
                    try {
                        Field addonField = addonInstance.getClass().getDeclaredField(fieldName);
                        addonField.setAccessible(true);
                        addonField.set(addonInstance, newValue);
                    } catch (NoSuchFieldException | IllegalAccessException ex) {
                        ErrorHandler.severe(
                                "Error updating addon language field '" + fieldName + "' in " + simpleClassName +
                                        ": " + ex.getMessage());
                    }
                } else {
                    ErrorHandler.severe(
                            "No addon language instance registered for: " + simpleClassName);
                }
            }
        }
        // Force a save of the language file.
        saveLanguageFile();
    }

    private static void saveLanguageFile() {
        LanguageHandler lh = RPUniverse.getLanguageHandler();
        File file = new File(RPUniverse.getInstance().getDataFolder(), "languages.yml");
        try {
            lh.getLanguageConfig().save(file);
        } catch (IOException e) {
            ErrorHandler.severe("Error saving languages.yml: " + e.getMessage());
        }
    }
}
