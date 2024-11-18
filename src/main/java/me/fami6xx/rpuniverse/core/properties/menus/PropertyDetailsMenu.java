package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

public class PropertyDetailsMenu extends Menu {

    private final Property property;

    public PropertyDetailsMenu(PlayerMenu playerMenu, Property property) {
        super(playerMenu);
        this.property = property;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().propertyDetailsMenuName);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;

        if (e.getCurrentItem().getType() == Material.BARRIER) {
            // Go back to the previous menu
            PlayerManagePropertyMenu manageMenu = new PlayerManagePropertyMenu(playerMenu, property);
            manageMenu.open();
        }
    }

    @Override
    public void setMenuItems() {
        // Create item to display property details
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{owner}", getOwnerName());
        placeholders.put("{price}", String.valueOf(property.getPrice()));
        placeholders.put("{rentable}", String.valueOf(property.isRentable()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (property.getOwner() != null && property.getRentStart() > 0) {
            placeholders.put("{rentStart}", dateFormat.format(new Date(property.getRentStart())));
            placeholders.put("{rentDuration}", String.valueOf(property.getRentDuration() / (24 * 60 * 60 * 1000L)));
        } else {
            placeholders.put("{rentStart}", "N/A");
            placeholders.put("{rentDuration}", "N/A");
        }

        String[] loreLines = RPUniverse.getLanguageHandler().propertyDetailsMenuDetailsItemLore.split("~");
        List<String> loreList = new ArrayList<>();
        for (String line : loreLines) {
            loreList.add(FamiUtils.replace(line, placeholders));
        }

        ItemStack detailsItem = FamiUtils.makeItem(
                Material.BOOK,
                RPUniverse.getLanguageHandler().propertyDetailsMenuDetailsItemDisplayName,
                loreList.toArray(new String[0])
        );

        // Back button
        ItemStack backItem = FamiUtils.makeItem(
                Material.BARRIER,
                RPUniverse.getLanguageHandler().propertyDetailsMenuBackItemDisplayName,
                RPUniverse.getLanguageHandler().propertyDetailsMenuBackItemLore.split("~")
        );

        inventory.setItem(13, detailsItem);
        inventory.setItem(22, backItem);
        setFillerGlass();
    }

    private String getOwnerName() {
        if (property.getOwner() == null) {
            return "None";
        } else {
            UUID ownerUUID = property.getOwner();
            Player owner = RPUniverse.getInstance().getServer().getPlayer(ownerUUID);
            if (owner != null) {
                return owner.getName();
            } else {
                return RPUniverse.getInstance().getServer().getOfflinePlayer(ownerUUID).getName();
            }
        }
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }
}
