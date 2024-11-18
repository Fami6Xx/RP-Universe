package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
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
                        long currentDuration = property.getRentDuration();
                        long newDuration = currentDuration + (1 * 24 * 60 * 60 * 1000L); // Adding 1 day

                        if (maxDuration == 0 || newDuration <= maxDuration) {
                            RPUniverse.getInstance().getEconomy().withdrawPlayer(player, price);
                            property.setRentDuration(newDuration);
                            RPUniverse.getInstance().getPropertyManager().saveProperty(property);
                            player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().rentExtensionSuccessMessage));
                            open(); // Refresh the menu
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().rentExtensionMaxDurationMessage));
                        }
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().rentExtensionInsufficientFundsMessage));
                    }
                }
                break;
            case BARRIER:
                player.closeInventory();
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        ItemStack trustedPlayersItem = FamiUtils.makeSkullItem(
                Bukkit.getOfflinePlayer(property.getOwner()),
                RPUniverse.getLanguageHandler().managePropertyMenuTrustedPlayersItemDisplayName,
                RPUniverse.getLanguageHandler().managePropertyMenuTrustedPlayersItemLore.split("~")
        );

        ItemStack propertyDetailsItem = FamiUtils.makeItem(
                Material.PAPER,
                RPUniverse.getLanguageHandler().managePropertyMenuPropertyDetailsItemDisplayName,
                RPUniverse.getLanguageHandler().managePropertyMenuPropertyDetailsItemLore.split("~")
        );

        ItemStack closeItem = FamiUtils.makeItem(
                Material.BARRIER,
                RPUniverse.getLanguageHandler().managePropertyMenuCloseItemDisplayName,
                RPUniverse.getLanguageHandler().managePropertyMenuCloseItemLore.split("~")
        );

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{price}", String.valueOf(property.getPrice()));
        placeholders.put("{rentable}", String.valueOf(property.isRentable()));

        String[] loreLines = RPUniverse.getLanguageHandler().managePropertyMenuExtendRentItemLore.split("~");
        List<String> loreList = new ArrayList<>();
        for (String line : loreLines) {
            loreList.add(FamiUtils.replace(line, placeholders));
        }
        loreLines = loreList.toArray(new String[0]);

        if (property.isRentable()) {
            // Extend Rent Item
            ItemStack extendRentItem = FamiUtils.makeItem(
                    Material.CLOCK,
                    RPUniverse.getLanguageHandler().managePropertyMenuExtendRentItemDisplayName,
                    loreLines
            );

            inventory.setItem(16, extendRentItem);
            inventory.setItem(10, trustedPlayersItem);
            inventory.setItem(13, propertyDetailsItem);
        } else {
            inventory.setItem(10, trustedPlayersItem);
            inventory.setItem(16, propertyDetailsItem);
        }

        inventory.setItem(22, closeItem);
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }
}
