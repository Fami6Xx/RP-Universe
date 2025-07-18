package me.fami6xx.rpuniverse;

import me.fami6xx.rpuniverse.core.DataSystem;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsModule;
import me.fami6xx.rpuniverse.core.invoice.InvoiceModule;
import me.fami6xx.rpuniverse.core.payment.PaymentModule;
import me.fami6xx.rpuniverse.core.chestlimit.ChestLimitListener;
import me.fami6xx.rpuniverse.core.commands.*;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.inventorylimit.PlayerInventoryLimitListener;
import me.fami6xx.rpuniverse.core.jobs.JobsHandler;
import me.fami6xx.rpuniverse.core.jobs.commands.createJob.CreateJobStarter;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.JobsCommand;
import me.fami6xx.rpuniverse.core.jobs.types.basic.BasicJobType;
import me.fami6xx.rpuniverse.core.locks.LockHandler;
import me.fami6xx.rpuniverse.core.menuapi.MenuManager;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.UpdateNotificationListener;
import me.fami6xx.rpuniverse.core.misc.VersionInfo;
import me.fami6xx.rpuniverse.core.misc.balance.BalanceChangeNotifier;
import me.fami6xx.rpuniverse.core.misc.basichandlers.ActionBarHandler;
import me.fami6xx.rpuniverse.core.misc.basichandlers.BossBarHandler;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.config.ConfigManager;
import me.fami6xx.rpuniverse.core.misc.language.LanguageHandler;
import me.fami6xx.rpuniverse.core.misc.papi.RPUExpansion;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.NickHider;
import me.fami6xx.rpuniverse.core.modules.ModuleManager;
import me.fami6xx.rpuniverse.core.properties.PropertyManager;
import me.fami6xx.rpuniverse.core.properties.commands.PropertiesCommand;
import me.fami6xx.rpuniverse.core.regions.RegionManager;
import net.milkbowl.vault.economy.Economy;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Main class of the plugin <p>
 * Contains API access and plugin setup. <p>
 * To get the instance of this class, use <code>RPUniverse.getInstance()</code>
 */
public class RPUniverse extends JavaPlugin {
    private DataSystem dataSystem;
    private HoloAPI holoAPI;
    private LanguageHandler languageHandler;
    private JobsHandler jobsHandler;
    private CreateJobStarter createJobStarter;
    private MenuManager menuManager;
    private UniversalChatHandler universalChatHandler;
    private BossBarHandler bossBarHandler;
    private ActionBarHandler actionBarHandler;
    private NickHider nickHider;
    private BasicNeedsHandler basicNeedsHandler;
    private LockHandler lockHandler;
    private BalanceChangeNotifier balanceChangeNotifier;
    private PropertyManager propertyManager;
    private ModuleManager moduleManager;

    private ConfigManager configManager;
    private FileConfiguration config;
    private Economy econ;
    private Metrics metrics;

    private boolean isServerReload = false;
    private boolean isUpdateAvailable = false;
    private String latestVersion = "";

    @Override
    public void onEnable() {
        if (isServerReload) {
            ErrorHandler.severe("We suspect you used /reload, RPUniverse does not support this and any issues reported after reloading will be ignored!");
        }

        // Initialize the config manager
        this.configManager = new ConfigManager(this);
        if (!configManager.loadConfig()) {
            ErrorHandler.severe("Failed to load configuration. Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Set the config reference for backward compatibility
        this.config = configManager.getConfig();

        // Initialize the error handler
        ErrorHandler.init();

        if (!setupEconomy()) {
            ErrorHandler.severe("Vault is not installed or doesn't have any Economy plugin! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else{
            ErrorHandler.info("Economy plugin hooked!");
        }

        // Initialize the module manager
        this.moduleManager = new ModuleManager(this);
        ErrorHandler.info("Module manager initialized");

        // Register modules
        moduleManager.registerModule(new InvoiceModule());
        ErrorHandler.debug("Registered InvoiceModule");

        moduleManager.registerModule(new BasicNeedsModule());
        ErrorHandler.debug("Registered BasicNeedsModule");

        moduleManager.registerModule(new PaymentModule());
        ErrorHandler.debug("Registered PaymentModule");

        languageHandler = new LanguageHandler(this);
        dataSystem = new DataSystem();
        holoAPI = new HoloAPI();

        if (!holoAPI.enable()) {
            ErrorHandler.severe("DecentHolograms is not installed! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else{
            ErrorHandler.info("Hologram system enabled!");
        }

        jobsHandler = new JobsHandler();
        getServer().getPluginManager().registerEvents(jobsHandler, this);
        menuManager = new MenuManager();

        if (!menuManager.enable()) {
            ErrorHandler.severe("Failed to enable MenuManager! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }else{
            ErrorHandler.info("MenuManager enabled!");
        }

        try {
            if(!compareVersions()){
                ErrorHandler.warning("Your version of RPUniverse is outdated! Please update to the latest version " + latestVersion + ".");

                // Register player join listener for update notifications if enabled in config
                if (config.getBoolean("updateNotification.enabled", true) && 
                    config.getBoolean("updateNotification.notifyPermissionedPlayersOnJoin", true)) {
                    getServer().getPluginManager().registerEvents(new UpdateNotificationListener(), this);
                    ErrorHandler.debug("Registered update notification listener");
                }
            } else {
                ErrorHandler.debug("Running the latest version of RPUniverse");
            }
        } catch (Exception e) {
            ErrorHandler.severe("Failed to check for updates! Please check your internet connection.", e);
        }

        this.lockHandler = new LockHandler();

        this.propertyManager = new PropertyManager(this);

        RegionManager.getInstance().init();

        this.getCommand("me").setExecutor(new MeCommand());
        this.getCommand("do").setExecutor(new DoCommand());
        this.getCommand("try").setExecutor(new TryCommand());
        this.getCommand("status").setExecutor(new StatusCommand());
        this.getCommand("stopstatus").setExecutor(new StatusCommand());
        this.getCommand("jobs").setExecutor(new JobsCommand());
        this.getCommand("modmode").setExecutor(new ModModeCommand());
        this.getCommand("adminmode").setExecutor(new AdminModeCommand());
        this.getCommand("settag").setExecutor(new SetTagCommand());
        this.getCommand("tag").setExecutor(new TagCommand());
        this.getCommand("switchjob").setExecutor(new SwitchJobCommand());
        this.getCommand("properties").setExecutor(new PropertiesCommand());

        DocCommand docCommand = new DocCommand();
        this.getCommand("doc").setExecutor(docCommand);
        getServer().getPluginManager().registerEvents(docCommand, this);

        this.createJobStarter = new CreateJobStarter(this);
        this.createJobStarter.start();

        this.universalChatHandler = new UniversalChatHandler();
        getServer().getPluginManager().registerEvents(universalChatHandler, this);
        this.getCommand("globalooc").setExecutor(this.universalChatHandler);

        this.bossBarHandler = new BossBarHandler();
        this.actionBarHandler = new ActionBarHandler();

        // BasicNeeds is now handled by the BasicNeedsModule

        if (getConfiguration().getBoolean("general.hideNicknames")) {
            this.nickHider = new NickHider();
            this.nickHider.init();
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RPUExpansion(this).register();
            ErrorHandler.info("PlaceholderAPI hooked!");
        } else {
            ErrorHandler.debug("PlaceholderAPI not found, skipping hook");
        }

        if (this.config.getBoolean("chestLimit.enabled")) {
            getServer().getPluginManager().registerEvents(new ChestLimitListener(this), this);
            ErrorHandler.info("ChestLimit enabled!");
        } else {
            ErrorHandler.debug("ChestLimit disabled in config");
        }

        this.getCommand("rpuniverse").setExecutor(new RPUCoreCommand());
        this.getCommand("rpuniverse").setTabCompleter(new RPUCoreAutoComplete());

        if (this.getConfig().getBoolean("inventoryLimit.enabled")) {
            getServer().getPluginManager().registerEvents(new PlayerInventoryLimitListener(), this);
            ErrorHandler.info("InventoryLimit enabled!");
        } else {
            ErrorHandler.debug("InventoryLimit disabled in config");
        }

        if (config.getBoolean("balance.enableTracker")) {
            balanceChangeNotifier = new BalanceChangeNotifier(this);
            getServer().getPluginManager().registerEvents(balanceChangeNotifier, this);
            balanceChangeNotifier.runTaskTimer(this, 0, config.getLong("balance.check-interval"));
            ErrorHandler.info("Balance tracker enabled with interval: " + config.getLong("balance.check-interval") + " ticks");
        } else {
            ErrorHandler.debug("Balance tracker disabled in config");
        }

        getJobsHandler().addJobType(new BasicJobType());
        ErrorHandler.debug("Added basic job type");

        int pluginId = 25314;
        metrics = new Metrics(this, pluginId);
        ErrorHandler.info("Metrics enabled!");

        // Initialize modules
        moduleManager.initializeModules();

        ErrorHandler.info("RPUniverse enabled!");
    }

    @Override
    public void onDisable() {
        ErrorHandler.info("Disabling RPUniverse...");
        try {
            ErrorHandler.debug("Shutting down RegionManager");
            RegionManager.getInstance().shutdown();

            ErrorHandler.debug("Disabling MenuManager");
            this.menuManager.disable();

            ErrorHandler.debug("Shutting down JobsHandler");
            this.jobsHandler.shutdown();

            ErrorHandler.debug("Disabling HoloAPI");
            this.holoAPI.disable();

            ErrorHandler.debug("Stopping CreateJobStarter");
            this.createJobStarter.stop();

            // BasicNeeds is now handled by the BasicNeedsModule

            if (nickHider != null) {
                ErrorHandler.debug("Shutting down NickHider");
                this.nickHider.shutdown();
            }

            ErrorHandler.debug("Shutting down LockHandler");
            this.lockHandler.shutdown();

            ErrorHandler.debug("Disabling PropertyManager");
            this.propertyManager.disable();

            if (balanceChangeNotifier != null) {
                ErrorHandler.debug("Cancelling BalanceChangeNotifier");
                balanceChangeNotifier.cancel();
            }

            ErrorHandler.debug("Shutting down DataSystem");
            this.dataSystem.shutdown();

            ErrorHandler.debug("Unregistering event handlers");
            HandlerList.unregisterAll(this);

            ErrorHandler.debug("Shutting down Metrics");
            metrics.shutdown();

            ErrorHandler.debug("Shutting down ModuleManager");
            if (moduleManager != null) {
                moduleManager.shutdownModules();
            }

            ErrorHandler.info("RPUniverse disabled successfully");
        } catch (Exception e) {
            ErrorHandler.severe("Error during plugin shutdown", e);
        }
        isServerReload = true;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    /**
     * Get the LockHandler
     * @return The LockHandler
     */
    public LockHandler getLockHandler() {
        return lockHandler;
    }

    /**
     * Get the Economy
     * @return The Economy
     */
    public Economy getEconomy() {
        return econ;
    }

    /**
     * Get the UniversalChatHandler
     * @return The UniversalChatHandler
     */
    public UniversalChatHandler getUniversalChatHandler() {
        return universalChatHandler;
    }

    /**
     * Get the MenuManager
     * @return The MenuManager
     */
    public MenuManager getMenuManager() {
        return menuManager;
    }

    /**
     * Get the LanguageHandler
     * @return The LanguageHandler
     */
    public static LanguageHandler getLanguageHandler() {
        return getInstance().languageHandler;
    }

    /**
     * Get the FileConfiguration
     * @return The FileConfiguration
     */
    public FileConfiguration getConfiguration() {
        return configManager.getConfig();
    }

    /**
     * Get the ConfigManager
     * @return The ConfigManager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Format a message
     * @param message The message to format
     * @return The formatted message
     */
    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Get the prefix from the config
     * @return The prefix
     */
    public static String getPrefix() {
        return format(getInstance().getConfiguration().getString("prefix"));
    }

    /**
     * Get the DataSystem
     * @return The DataSystem
     */
    public DataSystem getDataSystem() {
        return dataSystem;
    }

    /**
     * Get the HoloAPI
     * @return The HoloAPI
     */
    public HoloAPI getHoloAPI() {
        return holoAPI;
    }

    /**
     * Get the JobsHandler
     * @return The JobsHandler
     */
    public JobsHandler getJobsHandler() {
        return jobsHandler;
    }

    /**
     * Get the RPUniverse instance
     * @return The RPUniverse instance
     */
    public static RPUniverse getInstance() {
        return (RPUniverse) getJavaPlugin();
    }

    /**
     * Get the JavaPlugin instance
     * @return The JavaPlugin instance
     */
    public static JavaPlugin getJavaPlugin() {
        return getProvidingPlugin(RPUniverse.class);
    }

    /**
     * Get the PlayerData from the DataSystem
     * @param UUID The UUID of the player
     * @return The PlayerData
     */
    public static PlayerData getPlayerData(String UUID) {
        return getInstance().getDataSystem().getPlayerData(UUID);
    }

    /**
     * Get the BossBarHandler
     * @return The BossBarHandler
     */
    public BossBarHandler getBossBarHandler() {
        return bossBarHandler;
    }

    /**
     * Get the ActionBarHandler
     * @return The ActionBarHandler
     */
    public ActionBarHandler getActionBarHandler() {
        return actionBarHandler;
    }

    /**
     * Get the BasicNeedsHandler
     * @return The BasicNeedsHandler, or null if the module is not enabled
     */
    public BasicNeedsHandler getBasicNeedsHandler() {
        if (moduleManager == null) {
            return null;
        }

        me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsModule module = 
            (me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsModule) moduleManager.getModule("BasicNeeds");

        if (module == null || !module.isEnabled()) {
            return null;
        }

        return module.getHandler();
    }

    /**
     * Get the version from the API
     * @return The version
     * @throws IOException This is thrown if the plugin fails to connect to the API
     */
    private String getVersionFromAPI() throws Exception {
        String urlString = "https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=5845&key=version";
        ErrorHandler.debug("Checking version from API: " + urlString);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        ErrorHandler.debug("API response code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String version = response.toString();
            ErrorHandler.debug("API returned version: " + version);
            return version;
        } else {
            throw new RuntimeException("Failed version check: HTTP error code : " + responseCode);
        }
    }

    /**
     * Compare the version from the API with the version from the config
     * @return If the versions are equal
     * @throws Exception If the version check fails
     */
    private boolean compareVersions() throws Exception {
        String apiVersion = getVersionFromAPI();
        String configVersion = VersionInfo.getVersion();

        this.latestVersion = apiVersion;
        this.isUpdateAvailable = !apiVersion.equals(configVersion);

        // Check if the API version is lower than the config version
        // This means the plugin is being developed with a newer version than the API
        if (compareVersions(apiVersion, configVersion) < 0) {
            ErrorHandler.warning("API version is lower than the config version! This may indicate a development build.");
            return true; // Consider it up-to-date for development purposes
        }

        return !isUpdateAvailable;
    }

    /**
     * Compares two version strings in the format "major.minor.patch"
     * @param v1 First version string
     * @param v2 Second version string
     * @return -1 if v1 < v2, 1 if v1 > v2, 0 if they are equal
     */
    public static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0; // versions are equal
    }


    /**
     * Checks if an update is available
     * @return true if an update is available, false otherwise
     */
    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    /**
     * Gets the latest version from the API
     * @return The latest version
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * Gets the PropertyManager instance
     * @return The PropertyManager instance
     */
    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    /**
     * Gets the ModuleManager instance
     * @return The ModuleManager instance
     */
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}
