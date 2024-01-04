package me.fami6xx.rpuniverse.core.misc.language;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;

public class LanguageHandler {
    // GENERAL MESSAGES
    public String errorOnlyPlayersCanUseThisCommandMessage = "&cOnly players can use this command!";
    public String errorYouDontHavePermissionToUseThisCommandMessage = "&cYou don't have permission to use this command!";

    // MENU THINGS
    public String closeItemDisplayName = "&cClose";
    public String closeItemLore = "&7Click to close the menu";
    public String nextPageItemDisplayName = "&aNext Page";
    public String nextPageItemLore = "&7Click to go to the next page";
    public String previousPageItemDisplayName = "&aPrevious Page";
    public String previousPageItemLore = "&7Click to go to the previous page";
    public String errorMenuAlreadyOnLastPage = "&cYou are already on the last page!";
    public String errorMenuAlreadyOnFirstPage = "&cYou are already on the first page!";

    // CREATE JOB COMMAND MESSAGES
    public String errorYouAreAlreadyCreatingAJobMessage = "&cYou are already creating a job!";
public String createJobCommandTypeNameMessage = "&aType the name of the job in chat. Type &ccancel &ato cancel.";

    // ME COMMAND MESSAGES
    public String errorMeCommandUsage = "&cUsage: /me <message>";
    public String meCommandMessage = "&7* &e{player} &c{message}";
    public String meCommandHologram = "&7* &c{message} &7*";

    // DO COMMAND MESSAGES
    public String errorDoCommandUsage = "&cUsage: /do <message>";
    public String doCommandMessage = "&7* &e{player} &e{message}";
    public String doCommandHologram = "&7* &e{message} &7*";

    // STATUS AND STOP STATUS MESSAGES
    public String errorStatusCommandUsage = "&cUsage: /status <message>";
    public String errorStatusAlreadySet = "&cYou already have an active status";
    public String errorNoStatusSet = "&cYou don't have any active status";
    public String statusCommandHologram = "&7* &b{message} &7*";
    public String statusCommandMessage = "&cYou have started an status.";
    public String stopStatusCommandMessage = "&cYou stopped your status!";

    // CONFIG MESSAGES
    public String invalidValueInConfigMessage = "&cInvalid value in config.yml! &7(&c{value}&7)";

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
