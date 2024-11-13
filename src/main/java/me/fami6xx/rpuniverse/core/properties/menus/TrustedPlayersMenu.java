package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.properties.helpers.AddTrustedPlayerInputListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TrustedPlayersMenu extends EasyPaginatedMenu {

    private final Property property;

    /**
     * Constructs a new TrustedPlayersMenu.
     *
     * @param menu     The PlayerMenu instance.
     * @param property The Property whose trusted players are being managed.
     */
    public TrustedPlayersMenu(PlayerMenu menu, Property property) {
        super(menu);
        this.property = property;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format("&6Trusted Players");
    }

    @Override
    public int getSlots() {
        return 54; // Standard double chest size
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        List<UUID> trustedPlayers = property.getTrustedPlayers();
        if (index >= trustedPlayers.size()) {
            return new ItemStack(Material.AIR);
        }

        UUID playerUUID = trustedPlayers.get(index);
        Player trustedPlayer = RPUniverse.getInstance().getServer().getPlayer(playerUUID);
        String playerName = trustedPlayer != null ? trustedPlayer.getName() : "Offline Player";

        // Display name with the option to remove
        ItemStack playerItem = FamiUtils.makeItem(
                Material.PLAYER_HEAD,
                "&a" + playerName,
                "&7UUID: " + playerUUID.toString(),
                "&eClick to remove from trusted players."
        );

        return playerItem;
    }

    @Override
    public int getCollectionSize() {
        return property.getTrustedPlayers().size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        if (displayName.startsWith("a")) { // Assuming all trusted players have display names starting with &a
            // Remove trusted player
            String playerName = displayName.substring(2); // Remove the color code
            UUID playerUUID = getUUIDByName(playerName);
            if (playerUUID != null) {
                property.removeTrustedPlayer(playerUUID);
                RPUniverse.getInstance().getPropertyManager().saveProperty(property);
                FamiUtils.sendMessageWithPrefix(player, "&aRemoved " + playerName + " from trusted players.");
                open(); // Refresh the menu
            } else {
                FamiUtils.sendMessageWithPrefix(player, "&cPlayer not found.");
            }
        }
    }

    /**
     * Retrieves a player's UUID by their username.
     *
     * @param name The player's username.
     * @return The UUID if found, otherwise null.
     */
    private UUID getUUIDByName(String name) {
        Player target = RPUniverse.getInstance().getServer().getPlayerExact(name);
        if (target != null) {
            return target.getUniqueId();
        }

        target = RPUniverse.getInstance().getServer().getOfflinePlayer(name).getPlayer();
        if (target != null) {
            return target.getUniqueId();
        }

        return null;
    }

    /**
     * Adds additional items to the paginated menu, such as an Add Trusted Player button.
     */
    @Override
    public void addAdditionalItems() {
        // Add an item to add a new trusted player
        ItemStack addTrustedPlayer = FamiUtils.makeItem(
                Material.GREEN_DYE,
                "&aAdd Trusted Player",
                "&7Click to add a new trusted player."
        );
        inventory.setItem(49, addTrustedPlayer);
    }

    /**
     * Override the handleMenu to include handling the Add Trusted Player button.
     */
    @Override
    public void handleMenu(InventoryClickEvent e) {
        super.handleMenu(e);
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        if (displayName.equals("Add Trusted Player")) {
            e.setCancelled(true);
            player.closeInventory();
            FamiUtils.sendMessageWithPrefix(player, "&ePlease enter the username of the player to trust:");

            RPUniverse.getInstance().getServer().getPluginManager().registerEvents(new AddTrustedPlayerInputListener(player, property, this), RPUniverse.getInstance());
        }
    }
}
