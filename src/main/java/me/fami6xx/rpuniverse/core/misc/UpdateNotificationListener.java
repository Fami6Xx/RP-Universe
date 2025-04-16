package me.fami6xx.rpuniverse.core.misc;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener for notifying players about available updates when they join the server.
 */
public class UpdateNotificationListener implements Listener {

    /**
     * Handles player join events to notify players with the appropriate permission about available updates.
     *
     * @param event The player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RPUniverse plugin = RPUniverse.getInstance();
        
        // Check if update notifications are enabled
        if (!plugin.getConfigManager().getConfig().getBoolean("updateNotification.enabled", true)) {
            return;
        }
        
        // Check if the player has the permission to receive update notifications
        if (!player.hasPermission("rpu.update.notify")) {
            return;
        }
        
        // Check if an update is available
        if (plugin.isUpdateAvailable()) {
            String currentVersion = VersionInfo.getVersion();
            String latestVersion = plugin.getLatestVersion();
            
            // Send update notification to the player
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                RPUniverse.getPrefix() + " &cAn update is available! " +
                "&7Current version: &f" + currentVersion + " &7Latest version: &f" + latestVersion));
            
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                RPUniverse.getPrefix() + " &7Please update to the latest version from: &fhttps://modrinth.com/plugin/rpuniverse"));
        }
    }
}