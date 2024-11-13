package me.fami6xx.rpuniverse;

import me.fami6xx.rpuniverse.core.DataSystem;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.basicneeds.commands.ConsumablesCommand;
import me.fami6xx.rpuniverse.core.basicneeds.commands.PeeCommand;
import me.fami6xx.rpuniverse.core.basicneeds.commands.PoopCommand;
import me.fami6xx.rpuniverse.core.commands.*;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.jobs.JobsHandler;
import me.fami6xx.rpuniverse.core.jobs.commands.createJob.CreateJobStarter;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.JobsCommand;
import me.fami6xx.rpuniverse.core.locks.LockHandler;
import me.fami6xx.rpuniverse.core.menuapi.MenuManager;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.VersionInfo;
import me.fami6xx.rpuniverse.core.misc.balance.BalanceChangeNotifier;
import me.fami6xx.rpuniverse.core.misc.basichandlers.ActionBarHandler;
import me.fami6xx.rpuniverse.core.misc.basichandlers.BossBarHandler;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.language.LanguageHandler;
import me.fami6xx.rpuniverse.core.misc.papi.RPUExpansion;
import me.fami6xx.rpuniverse.core.misc.utils.NickHider;
import me.fami6xx.rpuniverse.core.properties.PropertyManager;
import me.fami6xx.rpuniverse.core.properties.commands.PropertiesCommand;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Main class of the plugin <p>
 * Contains API access and plugin setup. <p>
 * To get the instance of this class, use <code>RPUniverse.getInstance()</code>
 */
public final class RPUniverse extends JavaPlugin {
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

    private FileConfiguration config;
    private Economy econ;

    private boolean isServerReload = false;

    @Override
    public void onEnable() {
        if (isServerReload) {
            getLogger().severe("We suspect you used /reload, RPUniverse does not support this and any issues reported after reloading will be ignored!");
        }

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
            this.saveDefaultConfig();
            config = this.getConfig();
        } else {
            String[] configYml = Arrays.stream(getDataFolder().list())
                    .filter(s -> s.equals("config.yml"))
                    .toArray(String[]::new);

            if (configYml.length == 0) {
                this.saveDefaultConfig();
            }
            this.reloadConfig();
            config = this.getConfig();
        }

        int confVersion = config.getInt("configVersion", -1);
        if (confVersion != 2) {
            getLogger().severe("Your config is outdated! Please delete it and restart the server to generate a new one.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupEconomy()) {
            getLogger().severe("Vault is not installed or doesn't have any Economy plugin! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else{
            getLogger().info("Economy plugin hooked!");
        }

        languageHandler = new LanguageHandler(this);
        dataSystem = new DataSystem();
        holoAPI = new HoloAPI();

        if (!holoAPI.enable()) {
            getLogger().severe("DecentHolograms is not installed! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else{
            getLogger().info("Hologram system enabled!");
        }

        jobsHandler = new JobsHandler();
        getServer().getPluginManager().registerEvents(jobsHandler, this);
        menuManager = new MenuManager();

        if (!menuManager.enable()) {
            getLogger().severe("Failed to enable MenuManager! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }else{
            getLogger().info("MenuManager enabled!");
        }

        try {
            if(!compareVersions()){
                getLogger().severe("Your version of RPUniverse is outdated! Please update to the latest version.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.lockHandler = new LockHandler();

        this.propertyManager = new PropertyManager(this);

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
        this.getCommand("poop").setExecutor(new PoopCommand());
        this.getCommand("pee").setExecutor(new PeeCommand());
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

        try{
            this.basicNeedsHandler = new BasicNeedsHandler();
            this.basicNeedsHandler.initialize(this);
        } catch (Exception ignored){}
        this.getCommand("consumables").setExecutor(new ConsumablesCommand());

        if (getConfiguration().getBoolean("general.hideNicknames")) {
            this.nickHider = new NickHider();
            this.nickHider.init();
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RPUExpansion(this).register();
            getLogger().info("PlaceholderAPI hooked!");
        }

        this.getCommand("rpuniverse").setExecutor(new RPUCoreCommand());
        this.getCommand("rpuniverse").setTabCompleter(new RPUCoreAutoComplete());

        if (config.getBoolean("balance.enableTracker")) {
            balanceChangeNotifier = new BalanceChangeNotifier(this);
            getServer().getPluginManager().registerEvents(balanceChangeNotifier, this);
            balanceChangeNotifier.runTaskTimer(this, 0, config.getLong("balance.check-interval"));
        }

        getLogger().info("RPUniverse enabled!");
    }

    @Override
    public void onDisable() {
        try {
            this.menuManager.disable();
            HandlerList.unregisterAll(this);
            this.jobsHandler.shutdown();
            this.holoAPI.disable();
            this.createJobStarter.stop();
            this.basicNeedsHandler.shutdown();
            if (nickHider != null)
                this.nickHider.shutdown();
            this.dataSystem.shutdown();
            this.lockHandler.shutdown();
        } catch (NullPointerException ignored) {
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
        return config;
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
     * @return The BasicNeedsHandler
     */
    public BasicNeedsHandler getBasicNeedsHandler() {
        return basicNeedsHandler;
    }

    /**
     * Get the version from the API
     * @return The version
     * @throws Exception
     */
    private String getVersionFromAPI() throws Exception {
        String urlString = "https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=5845&key=version";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Return the version
            return response.toString();
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

        return apiVersion.equals(configVersion);
    }

    /**
     * Gets the PropertyManager instance
     * @return The PropertyManager instance
     */
    public PropertyManager getPropertyManager() {
        return propertyManager;
    }
}
