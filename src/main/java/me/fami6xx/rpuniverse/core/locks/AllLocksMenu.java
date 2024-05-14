package me.fami6xx.rpuniverse.core.locks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

public class AllLocksMenu extends EasyPaginatedMenu {
    List<Lock> locks;

    public AllLocksMenu(PlayerMenu menu, List<Lock> locks) {
        super(menu);
        this.locks = locks;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        Lock lock = locks.get(index);
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{lockOwner}", lock.getOwner() == null ? "None" : lock.getOwner());
        placeholders.put("{lockJobName}", lock.getJobName() == null ? "None" : lock.getJobName());
        placeholders.put("{lockMinWorkingLevel}", lock.getMinWorkingLevel() == 0 ? "None" : String.valueOf(lock.getMinWorkingLevel()));
        return FamiUtils.makeItem(lock.getShownMaterial(), FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().lockItemDisplayName, placeholders), FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().lockItemLore, placeholders));
    }

    @Override
    public int getCollectionSize() {
        return locks.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePaginatedMenu'");
    }

    @Override
    public void addAdditionalItems() {
        // 45 - Create Lock
        // 52 - Search
        // 53 - Filter
        inventory.setItem(45, FamiUtils.makeItem(Material.EMERALD_BLOCK, RPUniverse.getLanguageHandler().allLocksMenuCreateLockDisplayName, RPUniverse.getLanguageHandler().allLocksMenuCreateLockLore));
        inventory.setItem(52, FamiUtils.makeItem(Material.BARREL, RPUniverse.getLanguageHandler().allLocksMenuSearchDisplayName, RPUniverse.getLanguageHandler().allLocksMenuSearchLore));
        inventory.setItem(53, FamiUtils.makeItem(Material.BOOK, RPUniverse.getLanguageHandler().allLocksMenuFilterDisplayName, RPUniverse.getLanguageHandler().allLocksMenuFilterLore));
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().allLocksMenuName);
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> menuTags = new ArrayList<>();
        menuTags.add(MenuTag.ADMIN);
        menuTags.add(MenuTag.ALL_LOCKS);
        return menuTags;
    }

}
