package me.fami6xx.rpuniverse;

import me.fami6xx.rpuniverse.core.DataSystem;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPUniverse extends JavaPlugin {
    private DataSystem dataSystem;
    private HoloAPI holoAPI;

    @Override
    public void onEnable() {
        // Plugin startup logic
        dataSystem = new DataSystem();
        holoAPI = new HoloAPI();

        if(!holoAPI.enable()){
            getLogger().severe("DecentHolograms is not installed! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataSystem.shutdown();
        holoAPI.disable();
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
