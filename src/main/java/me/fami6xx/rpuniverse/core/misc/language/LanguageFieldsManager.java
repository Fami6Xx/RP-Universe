package me.fami6xx.rpuniverse.core.misc.language;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.language.editor.LanguageField;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages reflection and data for the LanguageHandler fields,
 * letting us know which fields are multi-line, etc.
 */
public class LanguageFieldsManager {

    /**
     * Returns a list of LanguageField objects for each String field
     * in the *live* LanguageHandler, with knowledge if the code default
     * contained '~'.
     */
    public static List<LanguageField> getAllLanguageFields() {
        // The "live" handler that has the current config values
        LanguageHandler liveHandler = RPUniverse.getLanguageHandler();

        // A "default" handler to see what was written in code
        LanguageHandler defaultHandler = new LanguageHandler(RPUniverse.getInstance());

        List<LanguageField> fields = new ArrayList<>();

        for (Field field : liveHandler.getClass().getDeclaredFields()) {
            if (field.getType() == String.class) {
                field.setAccessible(true);
                try {
                    // The current config-based value
                    String currentValue = (String) field.get(liveHandler);

                    // The original "code default" for this field
                    String defaultValue = (String) field.get(defaultHandler);

                    // multiLine = true if the default code string has '~'
                    boolean multiLine = (defaultValue != null && defaultValue.contains("~"));

                    fields.add(new LanguageField(
                            field,
                            field.getName(),
                            currentValue,
                            multiLine
                    ));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return fields;
    }

    /**
     * Applies the newValue to the actual "live" LanguageHandler,
     * then updates languages.yml so changes persist.
     */
    public static void setLanguageFieldValue(LanguageField languageField, String newValue) {
        LanguageHandler lh = RPUniverse.getLanguageHandler();
        Field field = languageField.getReflectionField();
        field.setAccessible(true);

        try {
            // Update the live field
            field.set(lh, newValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // Also update the config
        FileConfiguration langCfg = lh.getLanguageConfig();
        langCfg.set(field.getName(), newValue);

        // Force a save
        saveLanguageFile();
    }

    private static void saveLanguageFile() {
        LanguageHandler lh = RPUniverse.getLanguageHandler();
        File file = new File(RPUniverse.getInstance().getDataFolder(), "languages.yml");
        try {
            lh.getLanguageConfig().save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
