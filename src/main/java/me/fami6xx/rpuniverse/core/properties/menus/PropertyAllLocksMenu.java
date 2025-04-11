package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.properties.helpers.AddLockToPropertyListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PropertyAllLocksMenu extends EasyPaginatedMenu {
    private final Property property;
    public PropertyAllLocksMenu(PlayerMenu menu, Property property) {
        super(menu);
        this.property = property;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        Lock lock = property.getAssociatedLocks().get(index);

        ItemStack item = FamiUtils.makeItem(lock.getShownMaterial(), "&c&lLock &7- &e" + index, "&7Left click to teleport to the lock", "&7Right click to delete the lock");
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(RPUniverse.getJavaPlugin(), "lock-id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, index);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public int getCollectionSize() {
        return property.getAssociatedLocks().size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if (e.getSlot() == 45) {
            playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            Bukkit.getServer().getPluginManager().registerEvents(new AddLockToPropertyListener(playerMenu.getPlayer(), property, this), RPUniverse.getJavaPlugin());
            return;
        }

        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;
        NamespacedKey key = new NamespacedKey(RPUniverse.getJavaPlugin(), "lock-id");
        if (!e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return;

        int index = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        Lock lock = property.getAssociatedLocks().get(index);
        if (e.isLeftClick()) {
            playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            e.getWhoClicked().teleport(lock.getLocation());
        } else if (e.isRightClick()) {
            property.removeLock(lock);
            open();
        }
    }

    @Override
    public void addAdditionalItems() {
        inventory.setItem(45, FamiUtils.makeItem(Material.EMERALD_BLOCK, "&a&lCreate Lock", "&7Left click to create a new lock"));
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&c&lAll Locks");
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
