package me.fami6xx.rpuniverse.core.locks;

import java.util.ArrayList;
import java.util.List;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getItemFromIndex'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAdditionalItems'");
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().allLocksMenuName);
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> menuTags = new ArrayList<>();
        menuTags.add(MenuTag.ADMIN);
        return menuTags;
    }

}
