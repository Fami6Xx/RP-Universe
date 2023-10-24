package me.fami6xx.rpuniverse;

import me.fami6xx.rpuniverse.core.DataSystem;
import org.bukkit.plugin.java.JavaPlugin;

public final class RPUniverse extends JavaPlugin {
    private DataSystem dataSystem;

    @Override
    public void onEnable() {
        // Plugin startup logic
        dataSystem = new DataSystem();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dataSystem.shutdown();
    }

    public DataSystem getDataSystem() {
        return dataSystem;
    }

    public static RPUniverse getInstance() {
        return (RPUniverse) getJavaPlugin();
    }

    public static JavaPlugin getJavaPlugin(){
        return getProvidingPlugin(RPUniverse.class);
    }
}
