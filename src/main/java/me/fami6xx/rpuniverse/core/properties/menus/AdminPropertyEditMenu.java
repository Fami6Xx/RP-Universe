package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.properties.helpers.ChangeOwnerInputListener;
import me.fami6xx.rpuniverse.core.properties.helpers.MaxRentDurationInputListener;
import me.fami6xx.rpuniverse.core.properties.helpers.PriceInputListener;
import me.fami6xx.rpuniverse.core.properties.helpers.RentDurationInputListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AdminPropertyEditMenu extends Menu {

    private final Property property;

    /**
     * Constructs a new AdminPropertyEditMenu.
     *
     * @param menu     The PlayerMenu instance.
     * @param property The Property to be edited.
     */
    public AdminPropertyEditMenu(PlayerMenu menu, Property property) {
        super(menu);
        this.property = property;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&cEdit Property: &7" + property.getHologramLocation().getBlockX() + ", " + property.getHologramLocation().getBlockY() + ", " + property.getHologramLocation().getBlockZ());
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        switch (displayName) {
            case "Toggle Rentable":
                toggleRentable(player);
                break;
            case "Set Price":
                setPrice(player);
                break;
            case "Set Rent Duration":
                setRentDuration(player);
                break;
            case "Set Max Rent Duration":
                setMaxRentDuration(player);
                break;
            case "Change Owner":
                if (e.isShiftClick()) {
                    property.setOwner(null);
                    property.setTrustedPlayers(new ArrayList<>());
                    property.setRentStart(0);
                    property.setRentDuration(0);
                    RPUniverse.getInstance().getPropertyManager().saveProperty(property);
                    open();
                    break;
                }
                changeOwner(player);
                break;
            case "Edit Trusted Players":
                openTrustedPlayersMenu(player);
                break;
            case "Set Hologram Location":
                property.setHologramLocation(player.getLocation().add(0, 2, 0));
                FamiUtils.sendMessageWithPrefix(player, "&aHologram location set to your current location.");
                open();
                break;
            case "Delete Property":
                FamiUtils.sendMessageWithPrefix(player, "&cProperty " + property.getPropertyId().toString() + " has been deleted.");
                RPUniverse.getInstance().getPropertyManager().removeProperty(property.getPropertyId());
                new AllPropertiesMenu(playerMenu).open();
                break;
            case "Locks":
                new PropertyAllLocksMenu(playerMenu, property).open();
                break;
            case "Back":
                new AllPropertiesMenu(playerMenu).open();
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // Toggle Rentable
        ItemStack toggleRentable = FamiUtils.makeItem(
                property.isRentable() ? Material.LIME_DYE : Material.RED_DYE,
                "&aToggle Rentable",
                property.isRentable() ? "&7Property is currently rentable." : "&7Property is not rentable."
        );
        inventory.setItem(10, toggleRentable);

        // Set Price
        ItemStack setPrice = FamiUtils.makeItem(
                Material.GOLD_INGOT,
                "&6Set Price",
                "&7Current Price: $" + property.getPrice(),
                "&eClick to set a new price."
        );
        inventory.setItem(11, setPrice);

        // Set Rent Duration
        ItemStack setRentDuration = FamiUtils.makeItem(
                Material.COMPASS,
                "&6Set Rent Duration",
                "&7Current Duration: " + formatDuration(property.getRentDuration()),
                "&eClick to set a new duration."
        );
        inventory.setItem(12, setRentDuration);

        // Set Max Rent Duration
        ItemStack setMaxRentDuration = FamiUtils.makeItem(
                Material.NETHER_STAR,
                "&6Set Max Rent Duration",
                "&7Current Max Duration: " + formatDuration(property.getRentMaximumDuration()),
                "&eClick to set a new maximum duration."
        );
        inventory.setItem(13, setMaxRentDuration);

        // Change Owner
        ItemStack changeOwner = FamiUtils.makeItem(
                Material.PLAYER_HEAD,
                "&6Change Owner",
                "&7Current Owner: " + (property.getOwner() != null ? Bukkit.getOfflinePlayer(property.getOwner()).getName() : "None"),
                "&eClick to assign a new owner.",
                "&eShift + Click to remove owner."
        );
        inventory.setItem(14, changeOwner);

        // Edit Trusted Players
        ItemStack editTrustedPlayers = FamiUtils.makeItem(
                Material.NAME_TAG,
                "&6Edit Trusted Players",
                "&7Manage trusted players for this property.",
                "&eClick to edit trusted players."
        );
        inventory.setItem(15, editTrustedPlayers);

        // Set Hologram Location
        ItemStack setHologramLocation = FamiUtils.makeItem(
                Material.END_CRYSTAL,
                "&6Set Hologram Location",
                "&7Current Location: &a" + property.getHologramLocation().getBlockX() + ", " + property.getHologramLocation().getBlockY() + ", " + property.getHologramLocation().getBlockZ(),
                "&eClick to set the hologram location to your current location."
        );
        inventory.setItem(16, setHologramLocation);

        // Back Button (if needed)
        ItemStack backButton = FamiUtils.makeItem(
                Material.ARROW,
                "&cBack",
                "&7Return to the previous menu."
        );
        inventory.setItem(20, backButton);

        // Locks
        ItemStack locks = FamiUtils.makeItem(
                Material.IRON_DOOR,
                "&6Locks",
                "&7Manage locks for this property."
        );
        inventory.setItem(22, locks);

        // Delete Property
        ItemStack deleteButton = FamiUtils.makeItem(
                Material.BARRIER,
                "&cDelete Property",
                "&7Click to delete this property."
        );
        inventory.setItem(24, deleteButton);

        // Fill the rest of the inventory with filler glass
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }

    // Helper Methods

    private void toggleRentable(Player player) {
        property.setRentable(!property.isRentable());
        RPUniverse.getInstance().getPropertyManager().saveProperty(property);
        FamiUtils.sendMessageWithPrefix(player, "&aRentable status toggled to " + (property.isRentable() ? "enabled." : "disabled."));
        open();
    }

    private void setPrice(Player player) {
        player.closeInventory();
        // Implement a chat-based input for setting price
        FamiUtils.sendMessageWithPrefix(player, "&ePlease enter the new price:");

        RPUniverse.getInstance().getServer().getPluginManager().registerEvents(new PriceInputListener(player, property), RPUniverse.getInstance());
    }

    private void setRentDuration(Player player) {
        player.closeInventory();
        // Implement a chat-based input for setting rent duration
        FamiUtils.sendMessageWithPrefix(player, "&ePlease enter the new rent duration in days:");

        RPUniverse.getInstance().getServer().getPluginManager().registerEvents(new RentDurationInputListener(player, property), RPUniverse.getInstance());
    }

    private void setMaxRentDuration(Player player) {
        player.closeInventory();
        // Implement a chat-based input for setting max rent duration
        FamiUtils.sendMessageWithPrefix(player, "&ePlease enter the new maximum rent duration in days:");

        RPUniverse.getInstance().getServer().getPluginManager().registerEvents(new MaxRentDurationInputListener(player, property), RPUniverse.getInstance());
    }

    private void changeOwner(Player player) {
        player.closeInventory();
        // Implement a chat-based input for setting a new owner
        FamiUtils.sendMessageWithPrefix(player, "&ePlease enter the username of the new owner:");

        RPUniverse.getInstance().getServer().getPluginManager().registerEvents(new ChangeOwnerInputListener(player, property), RPUniverse.getInstance());
    }

    private void openTrustedPlayersMenu(Player player) {
        TrustedPlayersMenu trustedPlayersMenu = new TrustedPlayersMenu(new PlayerMenu(player), property);
        trustedPlayersMenu.open();
    }

    private String formatDuration(long durationMillis) {
        long days = durationMillis / (24 * 60 * 60 * 1000);
        long hours = (durationMillis / (60 * 60 * 1000)) % 24;
        long minutes = (durationMillis / (60 * 1000)) % 60;
        return days + "d " + hours + "h " + minutes + "m";
    }
}
