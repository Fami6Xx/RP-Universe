package me.fami6xx.rpuniverse.core.misc.language;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;

public class LanguageHandler {
    public String exampleMessageInHolograms = "&cThis is an example message in holograms";
    public String exampleMessageOnJoin = "&cThis is an example message on join";

    private final JavaPlugin plugin;
    private FileConfiguration languageConfig;

    public LanguageHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        loadLanguageFile();
    }

    private void loadLanguageFile() {
        try {
            File languageFile = new File(plugin.getDataFolder(), "languages.yml");
            if (!languageFile.exists()) {
                boolean result = languageFile.createNewFile();
                if(!result){
                    plugin.getLogger().severe("Failed to create languages.yml file!");
                    plugin.getPluginLoader().disablePlugin(plugin);
                    return;
                }
                generateDefaultLanguageFile(languageFile);
            }

            languageConfig = YamlConfiguration.loadConfiguration(languageFile);
            boolean modified = false;

            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getType() != String.class) continue;

                String fieldName = field.getName();
                if (!languageConfig.isSet(fieldName)) {
                    plugin.getLogger().warning("Missing field in languages.yml: " + fieldName + ". Adding default value.");
                    languageConfig.set(fieldName, field.get(this));
                    modified = true;
                } else {
                    Object value = languageConfig.get(fieldName);
                    if (field.getType().isInstance(value)) {
                        field.set(this, value);
                    }
                }
            }

            if (modified) {
                languageConfig.save(languageFile);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load languages.yml file!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private void generateDefaultLanguageFile(File languageFile) {
        try {
            languageConfig = new YamlConfiguration();
            for (Field field : this.getClass().getDeclaredFields()) {
                if(field.getType() != String.class) continue;

                languageConfig.set(field.getName(), field.get(this));
            }
            languageConfig.save(languageFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to create languages.yml file!");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }
}