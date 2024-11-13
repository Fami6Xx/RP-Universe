package me.fami6xx.rpuniverse.core.properties.helpers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PriceInputListener implements Listener {

    private final Player player;
    private final Property property;

    public PriceInputListener(Player player, Property property) {
        this.player = player;
        this.property = property;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) return;

        event.setCancelled(true);
        String message = event.getMessage();
        try {
            double newPrice = Double.parseDouble(message);
            if (newPrice < 0) throw new NumberFormatException();

            property.setPrice(newPrice);
            RPUniverse.getInstance().getPropertyManager().saveProperty(property);
            FamiUtils.sendMessageWithPrefix(player, "&aPrice updated to $" + newPrice);
        } catch (NumberFormatException e) {
            FamiUtils.sendMessageWithPrefix(player, "&cInvalid price. Please enter a valid number.");
        }

        HandlerList.unregisterAll(this);
    }
}
