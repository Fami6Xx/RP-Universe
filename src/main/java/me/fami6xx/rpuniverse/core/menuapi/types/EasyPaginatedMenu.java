package me.fami6xx.rpuniverse.core.menuapi.types;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class EasyPaginatedMenu extends PaginatedMenu {
    public EasyPaginatedMenu(PlayerMenu menu){
        super(menu);
    }

    /**
     * Gets ItemStack you created from your collection
     * @param index Index of item you want to get from collection
     * @return ItemStack you create
     */
    public abstract ItemStack getItemFromIndex(int index);

    /**
     *
     * @return Size of collection you use
     */
    public abstract int getCollectionSize();

    /**
     * Handles click on your item
     * @param e Previously handled InventoryClickEvent
     */
    public abstract void handlePaginatedMenu(InventoryClickEvent e);

    @Override
    public void handleMenu(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        String closeName = ChatColor.stripColor(FamiUtils.format(RPUniverse.getLanguageHandler().closeItemDisplayName));
        String nextName = ChatColor.stripColor(FamiUtils.format(RPUniverse.getLanguageHandler().nextPageItemDisplayName));
        String previousName = ChatColor.stripColor(FamiUtils.format(RPUniverse.getLanguageHandler().previousPageItemDisplayName));

        if(e.getCurrentItem().getType().equals(Material.BARRIER)
                && name.equalsIgnoreCase(closeName)){
            p.closeInventory();
        }else if(e.getCurrentItem().getType().equals(Material.STONE_BUTTON)){
            if(name.equalsIgnoreCase(previousName)){
                if(page == 0){
                    p.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorMenuAlreadyOnFirstPage));
                }else{
                    page--;
                    super.open();
                }
            }else if(name.equalsIgnoreCase(nextName)){
                if(index + 1 < getCollectionSize()){
                    page++;
                    super.open();
                }else{
                    p.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorMenuAlreadyOnLastPage));
                }
            }
        }

        handlePaginatedMenu(e);
    }

    @Override
    public void setMenuItems(){
        addMenuBorder();

        for(int i = 0; i < getMaxItemsPerPage(); i++){
            index = getMaxItemsPerPage() * page + i;
            if(index >= getCollectionSize()) break;

            ItemStack item = getItemFromIndex(index);

            if(item == null) break;

            super.inventory.setItem(i + 10, item);
        }
    }

}