package me.fami6xx.rpuniverse;

import me.fami6xx.rpuniverse.core.DataSystem;
import me.fami6xx.rpuniverse.core.commands.DoCommand;
import me.fami6xx.rpuniverse.core.commands.MeCommand;
import me.fami6xx.rpuniverse.core.commands.StatusCommand;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.jobs.JobsHandler;
import me.fami6xx.rpuniverse.core.jobs.commands.createJob.CreateJobStarter;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.JobsCommand;
import me.fami6xx.rpuniverse.core.menuapi.MenuManager;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.language.LanguageHandler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class RPUniverse extends JavaPlugin {
    private DataSystem dataSystem;
    private HoloAPI holoAPI;
    private LanguageHandler languageHandler;
    private JobsHandler jobsHandler;
    private CreateJobStarter createJobStarter;
    private MenuManager menuManager;
    private UniversalChatHandler universalChatHandler;

    private FileConfiguration config;

    @Override
    public void onEnable() {
        dataSystem = new DataSystem();
        holoAPI = new HoloAPI();
        jobsHandler = new JobsHandler();
        menuManager = new MenuManager();

        if(!holoAPI.enable()){
            getLogger().severe("DecentHolograms is not installed! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }

        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
            this.saveDefaultConfig();
            config = this.getConfig();
        }else{
            String[] configYml = Arrays.stream(getDataFolder().list())
                    .filter(s -> s.equals("config.yml"))
                    .toArray(String[]::new);

            if(configYml.length == 0){
                this.saveDefaultConfig();
            }
            config = this.getConfig();
        }

        languageHandler = new LanguageHandler(this);

        if(!menuManager.enable()){
            getLogger().severe("Failed to enable MenuManager! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }

        this.getCommand("me").setExecutor(new MeCommand());
        this.getCommand("do").setExecutor(new DoCommand());
        this.getCommand("status").setExecutor(new StatusCommand());
        this.getCommand("stopstatus").setExecutor(new StatusCommand());
        this.getCommand("jobs").setExecutor(new JobsCommand());

        this.createJobStarter = new CreateJobStarter(this);
        this.createJobStarter.start();

        this.universalChatHandler = new UniversalChatHandler();
        getServer().getPluginManager().registerEvents(universalChatHandler, this);
    }

    @Override
    public void onDisable() {
        this.menuManager.disable();
        HandlerList.unregisterAll(this);
        this.jobsHandler.shutdown();
        this.dataSystem.shutdown();
        this.holoAPI.disable();
        this.createJobStarter.stop();
    }

    public UniversalChatHandler getUniversalChatHandler() {
        return universalChatHandler;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public static LanguageHandler getLanguageHandler(){
        return getInstance().languageHandler;
    }

    public FileConfiguration getConfiguration(){
        return config;
    }

    public static String format(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getPrefix() {
        return format(getInstance().getConfiguration().getString("prefix"));
    }

    public DataSystem getDataSystem() {
        return dataSystem;
    }

    public HoloAPI getHoloAPI() {
        return holoAPI;
    }

    public JobsHandler getJobsHandler() {
        return jobsHandler;
    }

    public static RPUniverse getInstance() {
        return (RPUniverse) getJavaPlugin();
    }

    public static JavaPlugin getJavaPlugin(){
        return getProvidingPlugin(RPUniverse.class);
    }
}
