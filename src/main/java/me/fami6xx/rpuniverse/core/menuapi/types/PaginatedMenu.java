package me.fami6xx.rpuniverse.core.menuapi.types;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class PaginatedMenu extends Menu{
    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    protected ItemStack BORDER_GLASS = makeColoredGlass((short) 15);

    public PaginatedMenu(PlayerMenu menu){
        super(menu);
    }
    public void addMenuBorder(){
        inventory.setItem(48, makeItem(Material.STONE_BUTTON, FamiUtils.format(RPUniverse.getLanguageHandler().previousPageItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().previousPageItemLore)));

        inventory.setItem(49, makeItem(Material.BARRIER, FamiUtils.format(RPUniverse.getLanguageHandler().closeItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().closeItemLore)));

        inventory.setItem(50, makeItem(Material.STONE_BUTTON, FamiUtils.format(RPUniverse.getLanguageHandler().nextPageItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().nextPageItemLore)));

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, BORDER_GLASS);
            }
        }

        inventory.setItem(17, BORDER_GLASS);
        inventory.setItem(18, BORDER_GLASS);
        inventory.setItem(26, BORDER_GLASS);
        inventory.setItem(27, BORDER_GLASS);
        inventory.setItem(35, BORDER_GLASS);
        inventory.setItem(36, BORDER_GLASS);

        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, BORDER_GLASS);
            }
        }
    }
    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    @Override
    public int getSlots(){
        return 54;
    }
}

