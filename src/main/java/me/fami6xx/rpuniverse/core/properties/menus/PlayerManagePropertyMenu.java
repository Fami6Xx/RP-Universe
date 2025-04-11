package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerManagePropertyMenu extends Menu {

    private final Property property;

    // How much of the property’s (price-based or rent-based) value should be returned on sell
    private final double SELL_PERCENTAGE = 0.5; // 50% example; adapt to your needs

    public PlayerManagePropertyMenu(PlayerMenu playerMenu, Property property) {
        super(playerMenu);
        this.property = property;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().managePropertyMenuName);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = playerMenu.getPlayer();

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        Material clickedMaterial = e.getCurrentItem().getType();

        switch (clickedMaterial) {
            case PLAYER_HEAD:
                // Open the TrustedPlayersMenu
                PlayerMenu trustedPlayerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                TrustedPlayersMenu trustedPlayersMenu = new TrustedPlayersMenu(trustedPlayerMenu, property);
                trustedPlayersMenu.open();
                break;

            case PAPER:
                // Open the PropertyDetailsMenu
                PlayerMenu detailsMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                PropertyDetailsMenu propertyDetailsMenu = new PropertyDetailsMenu(detailsMenu, property);
                propertyDetailsMenu.open();
                break;

            case CLOCK:
                // Handle rent extension
                if (property.isRentable()) {
                    double price = property.getPrice(); // Price for extending by 1 day
                    if (RPUniverse.getInstance().getEconomy().has(player, price)) {
                        long maxDuration = property.getRentMaximumDuration();
                        long currentDuration = (property.getRentDuration() + property.getRentStart()) - System.currentTimeMillis();
                        long newDuration = currentDuration + (24 * 60 * 60 * 1000L); // +1 day

                        if (maxDuration == 0 || newDuration <= maxDuration) {
                            RPUniverse.getInstance().getEconomy().withdrawPlayer(player, price);
                            property.setRentDuration(newDuration);
                            RPUniverse.getInstance().getPropertyManager().saveProperty(property);
                            player.sendMessage(FamiUtils.formatWithPrefix(
                                    RPUniverse.getLanguageHandler().rentExtensionSuccessMessage
                            ));
                            open(); // Refresh
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix(
                                    RPUniverse.getLanguageHandler().rentExtensionMaxDurationMessage
                            ));
                        }
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix(
                                RPUniverse.getLanguageHandler().rentExtensionInsufficientFundsMessage
                        ));
                    }
                }
                break;

            case EMERALD:
                // Sell property
                // 1. Calculate how much the player would get if they sell it now
                double amountToDeposit = property.calculateSellAmount(SELL_PERCENTAGE);

                // 2. Perform the sell
                property.sellProperty(SELL_PERCENTAGE);

                // Format the amount to 2 decimal places
                amountToDeposit = Math.round(amountToDeposit * 100.0) / 100.0;

                // 3. Notify the player
                player.sendMessage(FamiUtils.formatWithPrefix("&aYou have sold your property for &e"
                        + amountToDeposit + "&e$&a!"));
                player.closeInventory();
                break;

            case BARRIER:
                // Close menu
                player.closeInventory();
                break;

            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // Trusted Players Skull
        ItemStack trustedPlayersItem = FamiUtils.makeSkullItem(
                Bukkit.getOfflinePlayer(property.getOwner()),
                RPUniverse.getLanguageHandler().managePropertyMenuTrustedPlayersItemDisplayName,
                RPUniverse.getLanguageHandler().managePropertyMenuTrustedPlayersItemLore.split("~")
        );

        // Property Details Paper
        ItemStack propertyDetailsItem = FamiUtils.makeItem(
                Material.PAPER,
                RPUniverse.getLanguageHandler().managePropertyMenuPropertyDetailsItemDisplayName,
                RPUniverse.getLanguageHandler().managePropertyMenuPropertyDetailsItemLore.split("~")
        );

        // Close Button
        ItemStack closeItem = FamiUtils.makeItem(
                Material.BARRIER,
                RPUniverse.getLanguageHandler().managePropertyMenuCloseItemDisplayName,
                RPUniverse.getLanguageHandler().managePropertyMenuCloseItemLore.split("~")
        );

        // Extend Rent Button (only if rentable)
        if (property.isRentable()) {
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{price}", String.valueOf(property.getPrice()));
            placeholders.put("{rentable}", String.valueOf(property.isRentable()));

            String[] loreLines = RPUniverse.getLanguageHandler().managePropertyMenuExtendRentItemLore.split("~");
            List<String> loreList = new ArrayList<>();
            for (String line : loreLines) {
                loreList.add(FamiUtils.replace(line, placeholders));
            }
            loreLines = loreList.toArray(new String[0]);

            ItemStack extendRentItem = FamiUtils.makeItem(
                    Material.CLOCK,
                    RPUniverse.getLanguageHandler().managePropertyMenuExtendRentItemDisplayName,
                    loreLines
            );
            inventory.setItem(16, extendRentItem);
            inventory.setItem(10, trustedPlayersItem);
            inventory.setItem(13, propertyDetailsItem);
        } else {
            // Not rentable
            inventory.setItem(10, trustedPlayersItem);
            inventory.setItem(16, propertyDetailsItem);
        }

        double previewSellAmount = property.calculateSellAmount(SELL_PERCENTAGE);

        HashMap<String, String> sellPlaceholders = new HashMap<>();
        sellPlaceholders.put("{sellAmount}", String.format("%.2f",previewSellAmount));
        sellPlaceholders.put("{sellPercentage}", String.valueOf(SELL_PERCENTAGE * 100));

        String sellDisplayName = FamiUtils.format(RPUniverse.getLanguageHandler().managePropertySellItemDisplayName);
        String[] sellLore = RPUniverse.getLanguageHandler().managePropertySellItemLore.split("~");

        List<String> replacedSellLore = new ArrayList<>();
        for (String line : sellLore) {
            replacedSellLore.add(FamiUtils.replaceAndFormat(line, sellPlaceholders));
        }

        ItemStack sellPropertyItem = FamiUtils.makeItem(
                Material.EMERALD,
                sellDisplayName,
                replacedSellLore.toArray(new String[0])
        );
        inventory.setItem(22, sellPropertyItem);

        // Place the close item
        inventory.setItem(26, closeItem);

        // Fill the remaining slots with filler glass
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }
}
