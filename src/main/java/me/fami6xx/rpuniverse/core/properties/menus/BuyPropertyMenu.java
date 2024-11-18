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

import java.util.*;

public class BuyPropertyMenu extends Menu {

    private final Property property;

    public BuyPropertyMenu(PlayerMenu playerMenu, Property property) {
        super(playerMenu);
        this.property = property;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().buyPropertyMenuName);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = playerMenu.getPlayer();

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        switch (e.getCurrentItem().getType()) {
            case EMERALD_BLOCK:
                // Handle purchase
                double price = property.getPrice();
                if (RPUniverse.getInstance().getEconomy().has(player, price)) {
                    RPUniverse.getInstance().getEconomy().withdrawPlayer(player, price);
                    property.setOwner(player.getUniqueId());
                    property.updateLastActive();
                    RPUniverse.getInstance().getPropertyManager().saveProperty(property);
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().successfullyPurchasedPropertyMessage));
                    player.closeInventory();
                } else {
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouDoNotHaveEnoughMoneyToPurchasePropertyMessage));
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
        String[] lore = RPUniverse.getLanguageHandler().buyPropertyMenuConfirmPurchaseItemLore.split("~");
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{price}", String.valueOf(property.getPrice()));
        List<String> loreList = Arrays.stream(lore).map(s -> FamiUtils.replace(s, placeholders)).toList();


        ItemStack buyItem = FamiUtils.makeItem(Material.EMERALD_BLOCK, RPUniverse.getLanguageHandler().buyPropertyMenuConfirmPurchaseItemDisplayName,
                loreList.toArray(new String[0]));

        ItemStack cancelItem = FamiUtils.makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().buyPropertyMenuCancelItemDisplayName,
                RPUniverse.getLanguageHandler().buyPropertyMenuCancelItemLore.split("~"));

        inventory.setItem(11, buyItem);
        inventory.setItem(15, cancelItem);
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }
}
