package me.fami6xx.rpuniverse.core.misc.language;

import me.fami6xx.rpuniverse.RPUniverse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAddonLanguage {

    // Registry for addon language instances (one per addon class)
    private static final Map<String, AbstractAddonLanguage> REGISTERED_ADDON_LANGUAGES = new HashMap<>();

    // Empty constructor
    public AbstractAddonLanguage() {
        // Do not perform reflection-based initialization here!
    }

    /**
     * Call this method from your subclass constructor (after default field initialization)
     * to update language values.
     */
    protected void initLanguage() {
        // Get the live LanguageHandler instance
        LanguageHandler languageHandler = RPUniverse.getLanguageHandler();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    // Create a key in the format: addon.<SimpleClassName>.<fieldName>
                    String key = "addon." + this.getClass().getSimpleName() + "." + field.getName();

                    // Get the default value from the subclass code
                    String defaultValue = (String) field.get(this);
                    System.out.println("Def: " + defaultValue);
                    if (defaultValue == null) {
                        defaultValue = "";
                    }

                    if (languageHandler.getAddonTranslations().containsKey(key)) {
                        String loadedValue = languageHandler.getAddonTranslation(key);
                        // If the loaded value is blank, use the default.
                        if (loadedValue == null || loadedValue.trim().isEmpty()) {
                            languageHandler.addAddonTranslation(key, defaultValue);
                            field.set(this, defaultValue);
                        } else {
                            field.set(this, loadedValue);
                        }
                        System.out.println("already contains: " + key + " = " + loadedValue);
                    } else {
                        languageHandler.addAddonTranslation(key, defaultValue);
                        field.set(this, defaultValue);
                        System.out.println("added: " + key + " = " + defaultValue);
                    }
                } catch (IllegalAccessException e) {
                    RPUniverse.getInstance().getLogger().severe(
                            "Error processing addon language field '" + field.getName() +
                                    "' in " + this.getClass().getSimpleName() + ": " + e.getMessage());
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

    /**
     * Creates a new instance of the given addon language class.
     * @param clazz The class of the addon language to create.
     * @return The new instance.
     * @param <T> The type of the addon language.
     */
    public static <T extends AbstractAddonLanguage> T create(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            instance.initLanguage();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Could not create language instance for " + clazz.getSimpleName(), e);
        }
    }
}
