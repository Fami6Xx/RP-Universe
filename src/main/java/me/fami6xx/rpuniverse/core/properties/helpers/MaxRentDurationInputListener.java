package me.fami6xx.rpuniverse.core.properties.helpers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MaxRentDurationInputListener implements Listener {

    private final Player player;
    private final Property property;

    public MaxRentDurationInputListener(Player player, Property property) {
        this.player = player;
        this.property = property;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) return;

        event.setCancelled(true);
        String message = event.getMessage();
        try {
            long days = Long.parseLong(message);
            if (days <= 0) throw new NumberFormatException();

            long maxDurationMillis = days * 24 * 60 * 60 * 1000;
            property.setRentMaximumDuration(maxDurationMillis);
            RPUniverse.getInstance().getPropertyManager().saveProperty(property);
            FamiUtils.sendMessageWithPrefix(player, "&aMax rent duration set to " + days + " days.");
        } catch (NumberFormatException e) {
            FamiUtils.sendMessageWithPrefix(player, "&cInvalid duration. Please enter a positive number.");
        }

        HandlerList.unregisterAll(this);
    }
}
