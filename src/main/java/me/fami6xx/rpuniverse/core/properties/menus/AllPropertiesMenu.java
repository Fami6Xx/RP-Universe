package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.properties.process.CreatePropertyProcess;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class AllPropertiesMenu extends EasyPaginatedMenu {
    private final List<Property> properties;

    public AllPropertiesMenu(PlayerMenu menu) {
        super(menu);
        this.properties = RPUniverse.getInstance().getPropertyManager().getAllProperties().stream().toList();
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        Property property = properties.get(index);
        ItemStack item = FamiUtils.makeItem(Material.BLACK_WOOL, "&cProperty: " + (index + 1),
                "&7Owner: &a" + (property.getOwner() == null ? "&cNone" : Bukkit.getOfflinePlayer(property.getOwner()).getName()),
                "&7Rentable: " + (property.isRentable() ? "&aYes" : "&cNo"),
                "&7Price: &a" + property.getPrice() + "&7$");
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(25565);
        NamespacedKey key = new NamespacedKey(RPUniverse.getInstance(), "property");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, index);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int getCollectionSize() {
        return properties.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;

        if (e.getSlot() == 45) {
            // Create a new property
            e.getWhoClicked().closeInventory();
            new CreatePropertyProcess((Player) e.getWhoClicked());
            return;
        }

        ItemMeta meta = e.getCurrentItem().getItemMeta();
        NamespacedKey key = new NamespacedKey(RPUniverse.getInstance(), "property");
        if (!meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return;
        int index = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        Property property = properties.get(index);
        // TODO Open the property menu
    }

    @Override
    public void addAdditionalItems() {
        // Create a new property
        ItemStack createProperty = FamiUtils.makeItem(Material.GREEN_TERRACOTTA, "&aCreate a new property", "&7Click here to start process of creating a new property");
        inventory.setItem(45, createProperty);
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&cAll properties");
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
