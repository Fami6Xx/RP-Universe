package me.fami6xx.rpuniverse.core.locks.menus;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

public class CreateLockSelectTypeMenu extends Menu {
    private Block block;

    public CreateLockSelectTypeMenu(PlayerMenu menu, Block block) {
        super(menu);
        this.block = block;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().createLockSelectTypeMenuName);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleMenu'");
    }

    @Override
    public void setMenuItems() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMenuItems'");
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        return tags;
    }
    
}
