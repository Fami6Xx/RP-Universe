package me.fami6xx.rpuniverse;

import me.fami6xx.rpuniverse.core.DataSystem;
import me.fami6xx.rpuniverse.core.commands.DoCommand;
import me.fami6xx.rpuniverse.core.commands.MeCommand;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.misc.language.LanguageHandler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class RPUniverse extends JavaPlugin {
    private DataSystem dataSystem;
    private HoloAPI holoAPI;
    private LanguageHandler languageHandler;

    private FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        dataSystem = new DataSystem();
        holoAPI = new HoloAPI();

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

        this.getCommand("me").setExecutor(new MeCommand());
        this.getCommand("do").setExecutor(new DoCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataSystem.shutdown();
        holoAPI.disable();

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

    public static RPUniverse getInstance() {
        return (RPUniverse) getJavaPlugin();
    }

    public static JavaPlugin getJavaPlugin(){
        return getProvidingPlugin(RPUniverse.class);
    }
}
