package me.fami6xx.rpuniverse.core.misc.language;

import me.fami6xx.rpuniverse.RPUniverse;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for addon language sections.
 * Upon initialization it uses reflection to iterate over all String fields
 * of the concrete subclass. It checks if the LanguageHandler already has translations
 * for each field (using the key format addon.<SimpleClassName>.<fieldName>).
 * If a translation exists, it sets the field to that value.
 * Otherwise, it registers the default value from the code.
 * Finally, the instance is registered in a static registry so that updates can be
 * pushed back into the addon instance.
 */
public abstract class AbstractAddonLanguage {

    // Registry for addon language instances (one per addon class)
    private static final Map<String, AbstractAddonLanguage> REGISTERED_ADDON_LANGUAGES = new HashMap<>();

    public AbstractAddonLanguage() {
        // Get the live LanguageHandler instance (for example via a singleton)
        LanguageHandler languageHandler = RPUniverse.getLanguageHandler();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    // Create a key in the format: addon.<SimpleClassName>.<fieldName>
                    String key = "addon." + this.getClass().getSimpleName() + "." + field.getName();
                    if (languageHandler.getAddonTranslations().containsKey(key)) {
                        // Translation already exists: load it.
                        String loadedValue = languageHandler.getAddonTranslation(key);
                        field.set(this, loadedValue);
                    } else {
                        // No translation exists: register the default.
                        String defaultValue = (String) field.get(this);
                        if (defaultValue == null) {
                            defaultValue = "";
                        }
                        languageHandler.addAddonTranslation(key, defaultValue);
                        field.set(this, defaultValue);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        // Register this addon instance by its simple class name.
        REGISTERED_ADDON_LANGUAGES.put(this.getClass().getSimpleName(), this);
    }

    /**
     * Returns the addon language instance for the given simple class name.
     */
    public static AbstractAddonLanguage getAddonLanguage(String simpleClassName) {
        return REGISTERED_ADDON_LANGUAGES.get(simpleClassName);
    }
}
