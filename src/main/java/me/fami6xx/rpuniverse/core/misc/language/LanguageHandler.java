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
    public String errorYouAreNotInCorrectModeToUseThisCommandMessage = "&cYou are not in correct mode to use this command!";
    public String errorYouAlreadyHaveSomethingToType = "&cYou already have something to type, type that thing first.";
    public String errorYouAreNotInUserMode = "&cYou are not in user mode!";
    public String errorYouDontHaveAnyJob = "&cYou don't have any job!";
    public String errorYouDontHaveAnyMoreJobs = "&cYou don't have any more jobs!";
    public String errorYesOrNoMessage = "&cYou can only type yes or no!";
    public String cancelActivityMessage = "&7Type &ccancel &7to cancel.";
    public String cancelSuccessful = "&cCancelled!";
    public String bossBarColorBeforeJob = "&c&l";
    public String bossBarPlayerNoJob = "Unemployed";
    public String moderatorTag = "&b&lMODERATOR";
    public String adminTag = "&c&lADMIN";

    public String basicNeedsActionBarMessage = "&cFood: {food} &8| &cWater: {water} &8| &cPoop: {poop} &8| &cPee: {pee}";

    // JOB HOLOGRAM
    public String jobBossMenuHologram = "&cJob Boss Menu~&6{jobName}~&7Click to open the menu";
    public String jobBossMenuAdminHologram = "&c&lEDIT MODE&r~&cJob Boss Menu~&6{jobName}~&7Click to open the menu";

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
    public String jobAdminMenuInformationItemDisplayName = "&cInformation";
    public String jobAdminMenuInformationItemLore = "&7Is job ready? &c{jobReady}&r~&7Job type: &c{jobType}~&7Job bank: &c{jobBank}~&r~&7Click to view more information in chat";
    public String jobAdminMenuRenameItemDisplayName = "&cRename";
    public String jobAdminMenuRenameItemLore = "&7Click to rename the job";
    public String jobMenuJobBankItemDisplayName = "&cJob Bank";
    public String jobMenuJobBankItemLore = "&7Click to add / remove money from the job bank";
    public String jobMenuPositionItemDisplayName = "&cPositions";
    public String jobMenuPositionItemLore = "&7Click to add / modify / remove positions";
    public String jobAdminMenuBossItemDisplayName = "&cBoss Menu";
    public String jobAdminMenuBossItemLore = "&7Click to port to boss menu or change location";
    public String jobAdminMenuJobTypeItemDisplayName = "&cJob Type";
    public String jobAdminMenuJobTypeItemLore = "&7Click to change job type";
    public String jobMenuAllPlayersItemDisplayName = "&cAll Players";
    public String jobMenuAllPlayersItemLore = "&7Click to view all players with this job";
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
    public String jobAllPositionsMenuMovePositionsItemDisplayName = "&cMove Positions";
    public String jobAllPositionsMenuMovePositionsItemLore = "&7Hold shift and click left to move the position up~&7Hold shift and click right to move the position down";
    public String jobAllPositionsMenuCannotMovePositionMessage = "&cYou cannot move this position!";
    public String jobAllPositionsMenuCannotEditPositionMessage = "&cYou cannot edit this position!";
    public String jobAllPositionsMenuPositionItemDisplayName = "&e{positionName}";
    public String jobAllPositionsMenuPositionItemLore = "&7Salary: &6{positionSalary}~&7Is boss? &6{positionIsBoss}~&7Click to view details";
    public String jobPositionMenuName = "&6RPUniverse &7| &c{positionName}";
    public String jobPositionMenuRenameDisplayName = "&cRename";
    public String jobPositionMenuRenameLore = "&7Click to rename the position";
    public String jobPositionMenuRenameMessage = "&7Type the new name of the position in chat.";
    public String jobPositionMenuRenameCancelMessage = "&cYou cancelled the position renaming!";
    public String jobPositionMenuSalaryDisplayName = "&cSalary";
    public String jobPositionMenuSalaryLore = "&7Current: &6{salary}~&7Click to change the salary";
    public String jobPositionMenuSalaryMessage = "&7Type the new salary of the position in chat.";
    public String jobPositionMenuSalaryCancelMessage = "&cYou cancelled the salary change!";
    public String jobPositionMenuWorkingPermissionLevelDisplayName = "&cWorking Permission Level";
    public String jobPositionMenuWorkingPermissionLevelLore = "&7Current: &6{workingPermissionLevel}~&7Click to change the working permission level";
    public String jobPositionMenuWorkingPermissionLevelMessage = "&7Type the new working permission level of the position in chat.";
    public String jobPositionMenuWorkingPermissionLevelCancelMessage = "&cYou cancelled the working permission level change!";
    public String jobPositionMenuIsBossDisplayName = "&cIs Boss";
    public String jobPositionMenuIsBossLore = "&7Current: &6{isBoss}~&7Click to change if the position is a boss position";
    public String jobPositionMenuIsDefaultDisplayName = "&cIs Default";
    public String jobPositionMenuIsDefaultLore = "&7Current: &6{isDefault}~&7Click to change if the position is a default position";
    public String jobPositionMenuDeleteDisplayName = "&cDelete";
    public String jobPositionMenuDeleteLore = "&7Click to delete the position";
    public String jobBossLocationMenuName = "&6RPUniverse &7| &cBoss Location";
    public String jobBossLocationMenuChangeLocationItemDisplayName = "&cChange Location";
    public String jobBossLocationMenuChangeLocationItemLore = "&7Click to change the location of the boss menu";
    public String jobBossLocationMenuChangeLocationMessage = "&cYou changed the location of the boss menu!";
    public String jobBossLocationMenuChangeLocationCancelMessage = "&cYou cancelled the location change!";
    public String jobBossLocationMenuTeleportToBossMenuItemDisplayName = "&cTeleport to Boss Menu";
    public String jobBossLocationMenuTeleportToBossMenuItemLore = "&7Click to teleport to the boss menu";
    public String jobBossLocationMenuTeleportToBossMenuMessage = "&cYou teleported to the boss menu!";
    public String jobAllPlayersMenuName = "&6RPUniverse &7| &cAll Players";
    public String jobAllPlayersMenuPlayerItemDisplayName = "&e{playerName}";
    public String jobAllPlayersMenuPlayerItemLore = "&7Position: &c{positionName}~&7Salary: &c{salary}~&7Online: &c{isOnline}~&7Click to view details";
    public String jobAllPlayersMenuAddPlayerItemDisplayName = "&cAdd Player";
    public String jobAllPlayersMenuAddPlayerItemLore = "&7Click to add a player to the job";
    public String errorJobNotReadyMessage = "&cThis job is not ready!";
    public String jobAllPlayersMenuAddPlayerNoAvailablePlayersMessage = "&cThere are no available players to add to the job!";
    public String jobSelectPlayerToAddMenuName = "&6RPUniverse &7| &cSelect Player";
    public String jobSelectPlayerToAddMenuPlayerItemDisplayName = "&e{playerName}";
    public String jobSelectPlayerToAddMenuPlayerItemLore = "&7Click to add this player to the job";
    public String jobSelectPlayerToAddMenuPlayerAddedMessage = "&cYou added the player to the job!";
    public String jobPlayerMenuName = "&6RPUniverse &7| &c{playerName}";
    public String jobPlayerMenuPositionDisplayName = "&cPosition";
    public String jobPlayerMenuPositionLore = "&7Current: &6{positionName}~&7Click to change the position";
    public String jobPlayerMenuKickDisplayName = "&cKick";
    public String jobPlayerMenuKickLore = "&7Click to kick the player from the job";
    public String jobPlayerMenuKickMessage = "&cYou kicked the player from the job!";
    public String jobPlayerMenuCannotKickMessage = "&cYou cannot kick this player!";
    public String jobPlayerMenuCannotChangePositionMessage = "&cYou cannot change the position of this player!";
    public String jobSelectPositionMenuName = "&6RPUniverse &7| &cSelect Position";
    public String jobSelectPositionMenuPositionItemDisplayName = "&e{positionName}";
    public String jobSelectPositionMenuPositionItemLore = "&7Salary: &6{positionSalary}~&7Click to select this position";
    public String jobSelectPositionMenuSelectPositionMessage = "&cYou selected the position!";
    public String jobBossMenuName = "&6RPUniverse &7| &cJob Boss Menu";
    public String jobMenuJobTypeBossItemDisplayName = "&cJob Type Boss Menu";
    public String jobMenuJobTypeBossItemLore = "&7Click to open the boss menu for your job type";


    // BASIC NEEDS
    public String errorBasicNeedsDisabledMessage = "&cBasic needs are disabled!";
    public String allConsumablesMenuName = "&6RPUniverse &7| &cAll Consumables";
    public String allConsumableMenuAddItemDisplayName = "&cAdd Consumable";
    public String allConsumableMenuAddItemLore = "&7Click to add a consumable";
    public String allConsumableMenuAddItemMessage = "&7Hold the item you want to be added as consumable in your hand and type &cadd&7 or &ccancel&7 to cancel.";
    public String allConsumableMenuAddItemMessageError = "&cYou can't add air as consumable!";
    public String allConsumableMenuAddItemMessageErrorAlreadyAdded = "&cThis item is already added as consumable!";
    public String allConsumableMenuAddItemMessageSuccess = "&cYou added the item as consumable!";
    public String allConsumableMenuInfoDisplayName = "&cInformation";
    public String allConsumableMenuInfoLore = "&7Click an consumable to edit it.";
    public String editConsumableMenuName = "&6RPUniverse &7| &cEdit Consumable";
    public String editConsumableMenuPoopItemDisplayName = "&cPoop";
    public String editConsumableMenuPoopItemLore = "&7Current value: &c{value}~&7Click to edit the poop consumable";
    public String editConsumableMenuPeeItemDisplayName = "&cPee";
    public String editConsumableMenuPeeItemLore = "&7Current value: &c{value}~&7Click to edit the pee consumable";
    public String editConsumableMenuFoodItemDisplayName = "&cFood";
    public String editConsumableMenuFoodItemLore = "&7Current value: &c{value}~&7Click to edit the food consumable";
    public String editConsumableMenuHealthItemDisplayName = "&cHealth";
    public String editConsumableMenuHealthItemLore = "&7Current value: &c{value}~&7Click to edit the health consumable";
    public String editConsumableMenuDrinkItemDisplayName = "&cDrink";
    public String editConsumableMenuDrinkItemLore = "&7Current value: &c{value}~&7Click to edit the drink consumable";
    public String editConsumableMenuRemoveItemDisplayName = "&cRemove";
    public String editConsumableMenuRemoveItemLore = "&7Click to remove the consumable";
    public String editConsumableMenuRemoveItemMessage = "&cYou removed the consumable!";



    // CREATE JOB COMMAND MESSAGES
    public String errorYouAreAlreadyCreatingAJobMessage = "&cYou are already creating a job!";
    public String errorJobNameTooLongMessage = "&cThe job name is too long!";
    public String errorJobNameAlreadyExistsMessage = "&cA job with this name already exists!";
    public String createJobCommandCancelMessage = "&cYou cancelled the job creation!";
    public String createJobCommandTypeNameMessage = "&7Type the name of the job in chat.";
    public String createJobCommandBossMenuLocationMessage = "&7Go to the location you want the boss menu to be and type &chere&7.";
    public String createJobCommandJobCreatedMessage = "&7Job created! You can now edit it in the /jobs menu.";


    // GLOBAL AND LOCAL OOC
    public String errorGlobalOOCUsage = "&cUsage: /globalooc <message>";


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
    public String addPositionShouldBeWorkingPermissionLevelMessage = "&7Type the working permission level of the position in chat.";
    public String addPositionShouldBeBossMessage = "&7Should this position be a boss position? &c(yes/no)";
    public String addPositionSuccessMessage = "&cPosition added!";
    public String errorPositionNameTooLongMessage = "&cThe position name is too long!";
    public String errorPositionNameAlreadyExistsMessage = "&cA position with this name already exists!";
    public String errorPositionSalaryNotANumberMessage = "&cThe salary must be a number!";
    public String errorPositionSalaryTooLowMessage = "&cThe salary must be higher than 0!";
    public String errorPositionWorkingPermissionLevelNotANumberMessage = "&cThe working permission level must be a number!";
    public String errorPositionWorkingPermissionLevelTooLowMessage = "&cThe working permission level must be higher than 0!";
    public String cancelPositionCreationMessage = "&cYou cancelled the position creation!";

    // MOD MODE MESSAGES
    public String modModeDisabledMessage = "&cDisabled mod mode!";
    public String modModeEnabledMessage = "&cEnabled mod mode!";
    public String modModeErrorPlayerNotFoundMessage = "&cPlayer not found!";
    public String modesErrorCannotChangeModeMessage = "&cYou cannot change the mode of this player!";


    // ADMIN MODE MESSAGES
    public String adminModeDisabledMessage = "&cDisabled admin mode!";
    public String adminModeEnabledMessage = "&cEnabled admin mode!";
    public String adminModeErrorPlayerNotFoundMessage = "&cPlayer not found!";

    // TAG MESSAGES
    public String tagEnabledMessage = "&cEnabled tag!";
    public String tagDisabledMessage = "&cDisabled tag!";
    public String successTagSetMessage = "&cTag set!";
    public String errorSetTagCommandUsage = "&cUsage: /settag <player> <tag>";
    public String errorSetTagCommandPlayerNotFound = "&cPlayer not found!";
    public String errorSetTagCommandTagTooLong = "&cThe tag is too long!";

    //  SWITCH JOB
    public String switchJobCommandInfo = "&7Type the id of the job you want to switch to in the command. &7(&c/switchjob <id>&7)";
    public String switchJobCommandJobList = "&7» &c{jobId} &7- &e{jobName}";
    public String switchJobCommandSuccess = "&cYou switched to the job! &7(&c{jobName}&7)";
    public String switchJobCommandError = "&cNo job found!";
    public String switchJobCommandErrorAlreadyInJob = "&cYou are already in this job!";


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
