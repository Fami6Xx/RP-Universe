package me.fami6xx.rpuniverse.core.properties.helpers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChangeOwnerInputListener implements Listener {

    private final Player player;
    private final Property property;

    public ChangeOwnerInputListener(Player player, Property property) {
        this.player = player;
        this.property = property;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) return;

        event.setCancelled(true);
        String username = event.getMessage();
        Player newOwner = Bukkit.getPlayerExact(username);

        if (newOwner == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cPlayer not found or is offline.");
        } else {
            property.setOwner(newOwner.getUniqueId());
            if (property.isRentable()) {
                property.setRentStart(System.currentTimeMillis());
                property.setRentDuration(30 * 24 * 60 * 60 * 1000L); // 30 days
            }
            RPUniverse.getInstance().getPropertyManager().saveProperty(property);
            FamiUtils.sendMessageWithPrefix(player, "&aOwner changed to " + newOwner.getName() + ".");
        }

        HandlerList.unregisterAll(this);
    }
}
