package me.fami6xx.rpuniverse.core.misc.basichandlers;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BossBarHandler {
    private final Map<Player, BossBar> bossBars = new HashMap<>();

    public void addPlayer(Player player, String message) {
        BossBar bossBar = Bukkit.createBossBar(message, getBarColor(), BarStyle.SOLID);
        bossBar.addPlayer(player);
        bossBars.put(player, bossBar);
    }

    public void removePlayer(Player player) {
        BossBar bossBar = bossBars.remove(player);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    public void setMessage(Player player, String message) {
        BossBar bossBar = bossBars.get(player);
        if (bossBar != null) {
            bossBar.setTitle(message);
            return;
        }

        addPlayer(player, message);
    }

    private static BarColor getBarColor(){
        String string = RPUniverse.getInstance().getConfiguration().getString("general.bossBarColor");
        if(string == null){
            RPUniverse.getInstance().getLogger().severe("BossBar color is not set in the config.yml! Using default color RED");
            return BarColor.RED;
        }

        string = string.toUpperCase().trim();
        try{
            return BarColor.valueOf(string);
        }catch (IllegalArgumentException e){
            RPUniverse.getInstance().getLogger().severe("BossBar color is not valid in the config.yml! Using default color RED");
            return BarColor.RED;
        }
    }
}
