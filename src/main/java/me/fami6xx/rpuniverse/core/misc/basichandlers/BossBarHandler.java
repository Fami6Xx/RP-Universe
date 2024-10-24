package me.fami6xx.rpuniverse.core.misc.basichandlers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * The BossBarHandler is the main class for handling the boss bars.
 * It is responsible for adding, removing, and updating the boss bars.
 * <p>
 * The BossBarHandler is a singleton class, so only one instance of it should be created.
 * <p>
 * The BossBarHandler is also a listener for the PlayerJoinEvent and PlayerQuitEvent.
 * This means that the BossBarHandler will also listen for these events and update the boss bars accordingly.
 */
public class BossBarHandler {
    private final Map<Player, BossBar> bossBars = new HashMap<>();
    private final boolean enabled;

    public BossBarHandler() {
        enabled = RPUniverse.getInstance().getConfiguration().getBoolean("general.bossBarEnabled");
    }

    /**
     * Add a player to the boss bar
     * @param player The player to add
     * @param message The message to display on the boss bar
     */
    public void addPlayer(Player player, String message) {
        if (!enabled) {
            return;
        }

        BossBar bossBar = Bukkit.createBossBar(message, getBarColor(), BarStyle.SOLID);
        bossBar.addPlayer(player);
        bossBars.put(player, bossBar);
    }

    /**
     * Remove a player from the boss bar
     * @param player The player to remove
     */
    public void removePlayer(Player player) {
        if (!enabled) {
            return;
        }

        BossBar bossBar = bossBars.remove(player);
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    /**
     * Set the message of a player's boss bar
     * @param player The player to set the message of
     * @param message The message to set the boss bar to
     */
    public void setMessage(Player player, String message) {
        if (!enabled) {
            return;
        }

        BossBar bossBar = bossBars.get(player);
        if (bossBar != null) {
            bossBar.setTitle(message);
            return;
        }

        addPlayer(player, message);
    }

    /**
     * Update the boss bar of a player
     * @param player The player to update the boss bar of
     */
    public void updateBossBar(Player player){
        if (!enabled) {
            return;
        }

        String message = "";
        PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());

        if(playerData.getPlayerMode() != PlayerMode.USER){
            if(playerData.getPlayerMode() == PlayerMode.MODERATOR){
                message = "MODMODE";
            }else{
                message = "ADMINMODE";
            }
        }else if(playerData.getSelectedPlayerJob() != null){
            message = playerData.getSelectedPlayerJob().getName() + " - " + playerData.getSelectedPlayerJob().getPlayerPosition(player.getUniqueId()).getName();
        }else{
            message = RPUniverse.getLanguageHandler().bossBarPlayerNoJob;
        }

        this.setMessage(player, FamiUtils.format(RPUniverse.getLanguageHandler().bossBarColorBeforeJob + message));
    }

    /**
     * Get the bar color from the config.yml
     * @return The bar color
     */
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
