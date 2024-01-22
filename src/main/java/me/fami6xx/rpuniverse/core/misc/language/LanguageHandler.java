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
    public String errorYouAlreadyHaveSomethingToType = "&cYou already have something to type, type that thing first.";
    public String cancelActivityMessage = "Type &ccancel &7to cancel.";

    // MENU THINGS
    public String closeItemDisplayName = "&cClose";
    public String closeItemLore = "&7Click to close the menu";
    public String nextPageItemDisplayName = "&aNext Page";
    public String nextPageItemLore = "&7Click to go to the next page";
    public String previousPageItemDisplayName = "&aPrevious Page";
    public String previousPageItemLore = "&7Click to go to the previous page";
    public String errorMenuAlreadyOnLastPage = "&cYou are already on the last page!";
    public String errorMenuAlreadyOnFirstPage = "&cYou are already on the first page!";
    public String generalMenuBackItemDisplayName = "&cBack";
    public String generalMenuBackItemLore = "&7Click to go back";


    // JOBS MENUS
    public String allJobsMenuName = "&6RPUniverse &7| &cAll Jobs";
    public String allJobsMenuJobName = "&e{jobName}";
    public String jobAdminMenuName = "&6RPUniverse &7| &c{jobName}";
    public String jobAdminMenuRenameItemDisplayName = "&cRename";
    public String jobAdminMenuRenameItemLore = "&7Click to rename the job";
    public String jobAdminMenuJobBankItemDisplayName = "&cJob Bank";
    public String jobAdminMenuJobBankItemLore = "&7Click to add / remove money from the job bank";
    public String jobAdminMenuPositionItemDisplayName = "&cPositions";
    public String jobAdminMenuPositionItemLore = "&7Click to add / modify / remove positions";
    public String jobAdminMenuBossItemDisplayName = "&cBoss Menu";
    public String jobAdminMenuBossItemLore = "&7Click to port to boss menu or change location";
    public String jobAdminMenuJobTypeItemDisplayName = "&cJob Type";
    public String jobAdminMenuJobTypeItemLore = "&7Click to change job type";
    public String jobAdminMenuJobTypeAdminItemDisplayName = "&cJob Type Admin";
    public String jobAdminMenuJobTypeAdminItemLore = "&7Click to open admin job type menu";
    public String jobAdminMenuRemoveItemDisplayName = "&cRemove";
    public String jobAdminMenuRemoveItemLore = "&7Click to remove the job";
    public String jobBankActionsMenuName = "&6RPUniverse &7| &cJob Bank Actions";
    public String jobBankActionsMenuAddMoneyItemDisplayName = "&cAdd Money";
    public String jobBankActionsMenuAddMoneyItemLore = "&7Click to add money to the job bank";
    public String jobBankActionsMenuRemoveMoneyItemDisplayName = "&cRemove Money";
    public String jobBankActionsMenuRemoveMoneyItemLore = "&7Click to remove money from the job bank";
    public String jobBankActionsMenuCurrentMoneyItemDisplayName = "&cCurrent Money";
    public String jobBankActionsMenuCurrentMoneyItemLore = "&7Current money in the job bank: &c{jobMoney}&7$";
    public String jobBankActionsMenuAddMoneyMessage = "&7Type the amount of money you want to add to the job bank in chat.";
    public String jobBankActionsMenuAddMoneySuccessMessage = "&cYou added money to the job bank!";
    public String jobBankActionsMenuAddMoneyErrorMessage = "&cYou can't add this amount of money to the job bank!";
    public String jobBankActionsMenuRemoveMoneyMessage = "&7Type the amount of money you want to remove from the job bank in chat.";
    public String jobBankActionsMenuRemoveMoneySuccessMessage = "&cYou removed money from the job bank!";
    public String jobBankActionsMenuRemoveMoneyErrorMessage = "&cYou can't remove this amount of money from the job bank!";
    public String jobAllPositionsMenuName = "&6RPUniverse &7| &c All positions";
    public String jobAllPositionsMenuAddPositionItemDisplayName = "&cCreate Position";
    public String jobAllPositionsMenuAddPositionItemLore = "&7Click to start the process of creating a position";
    public String jobAllPositionsMenuPositionItemDisplayName = "&e{positionName}";
    public String jobAllPositionsMenuPositionItemLore = "&7Click to view details";


    // CREATE JOB COMMAND MESSAGES
    public String errorYouAreAlreadyCreatingAJobMessage = "&cYou are already creating a job!";
    public String errorJobNameTooLongMessage = "&cThe job name is too long!";
    public String errorJobNameAlreadyExistsMessage = "&cA job with this name already exists!";
    public String createJobCommandCancelMessage = "&cYou cancelled the job creation!";
    public String createJobCommandTypeNameMessage = "&7Type the name of the job in chat.";
    public String createJobCommandBossMenuLocationMessage = "&7Go to the location you want the boss menu to be and type &chere&7.";
    public String createJobCommandJobCreatedMessage = "&7Job created! You can now edit it in the /jobs menu.";


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

    // Add position messages
    public String addPositionTypePositionNameMessage = "&7Type the name of the position in chat.";
    public String addPositionTypePositionSalaryMessage = "&7Type the salary of the position in chat.";
    public String addPositionTypePositionWorkingPermissionLevelMessage = "&7Type the working permission level of the position in chat.";
    public String addPositionSuccessMessage = "&cPosition added!";
    public String errorPositionNameTooLongMessage = "&cThe position name is too long!";
    public String errorPositionNameAlreadyExistsMessage = "&cA position with this name already exists!";
    public String errorPositionSalaryNotANumberMessage = "&cThe salary must be a number!";
    public String errorPositionSalaryTooLowMessage = "&cThe salary must be higher than 0!";
    public String errorPositionWorkingPermissionLevelNotANumberMessage = "&cThe working permission level must be a number!";
    public String errorPositionWorkingPermissionLevelTooLowMessage = "&cThe working permission level must be higher than 0!";
    public String cancelPositionCreationMessage = "&cYou cancelled the position creation!";

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
