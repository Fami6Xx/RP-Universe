package me.fami6xx.rpuniverse.core.properties.helpers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.menus.TrustedPlayersMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class AddTrustedPlayerInputListener implements Listener {

    private final Player player;
    private final Property property;
    private final TrustedPlayersMenu menu;

    public AddTrustedPlayerInputListener(Player player, Property property, TrustedPlayersMenu menu) {
        this.player = player;
        this.property = property;
        this.menu = menu;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) return;

        event.setCancelled(true);
        String username = event.getMessage();
        Player target = Bukkit.getPlayerExact(username);

        if (target == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cPlayer not found or is offline.");
        } else {
            if (target.equals(player)) {
                FamiUtils.sendMessageWithPrefix(player, "&cYou cannot add yourself as a trusted player.");
                return;
            }

            UUID targetUUID = target.getUniqueId();
            if (property.getTrustedPlayers().contains(targetUUID)) {
                FamiUtils.sendMessageWithPrefix(player, "&cPlayer is already a trusted player.");
            } else {
                property.addTrustedPlayer(targetUUID);
                RPUniverse.getInstance().getPropertyManager().saveProperty(property);
                FamiUtils.sendMessageWithPrefix(player, "&aAdded " + target.getName() + " as a trusted player.");
            }
        }

        // Refresh the menu
        menu.open();

        HandlerList.unregisterAll(this);
    }
}
