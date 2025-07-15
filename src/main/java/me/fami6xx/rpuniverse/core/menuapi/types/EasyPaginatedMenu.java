package me.fami6xx.rpuniverse.core.menuapi.types;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EasyPaginatedMenu extends PaginatedMenu {
    private static final int START_SLOT = 10;
    private static final int SLOTS_PER_ROW = 7;

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

        if(e.getCurrentItem().getType().equals(BORDER_GLASS.getType())){
            return;
        }

        if(e.getCurrentItem().getType().equals(Material.BARRIER)
                && name.equalsIgnoreCase(closeName)){
            p.closeInventory();
            return;
        }else if(e.getCurrentItem().getType().equals(Material.STONE_BUTTON)){
            if(name.equalsIgnoreCase(previousName)){
                if(page == 0){
                    p.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorMenuAlreadyOnFirstPage));
                }else{
                    page--;
                    super.open();
                }
                return;
            }else if(name.equalsIgnoreCase(nextName)){
                if(index + 1 < getCollectionSize()){
                    page++;
                    super.open();
                }else{
                    p.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorMenuAlreadyOnLastPage));
                }
                return;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                handlePaginatedMenu(e);
            }
        }.runTask(RPUniverse.getInstance());
    }

    @Override
    public void setMenuItems(){
        addMenuBorder();
        addAdditionalItems();

        Integer[] borderSlots = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        List<Integer> borderSlotsList = new ArrayList<>(Arrays.asList(borderSlots));
        int slot = 10;
        for(int i = 0; i < getMaxItemsPerPage(); i++){
            index = getMaxItemsPerPage() * page + i;

            while(borderSlotsList.contains(slot)){
                slot++;
            }

            if(index >= getCollectionSize()) {
                super.inventory.setItem(slot, new ItemStack(Material.AIR));
                slot++;
                continue;
            }

            super.inventory.setItem(slot, getItemFromIndex(index));
            slot++;
        }
    }

    /**
     * A method where you can add your own items to the inventory border for example.
     */
    public abstract void addAdditionalItems();

    /**
     * Maps the clicked inventory slot to the index in the collection.
     *
     * @param slot The inventory slot that was clicked (0-53)
     * @return The corresponding index in the collection, or -1 if the slot doesn't map to an item
     * @throws IllegalArgumentException if the slot is negative or exceeds inventory size
     */
    public int getSlotIndex(int slot) {
        // sanity check
        if (slot < 0 || slot >= getSlots()) {
            throw new IllegalArgumentException("Invalid slot number: " + slot);
        }

        // compute inventory row/col in a 9-wide grid
        int invRow = slot / 9;    // 0..5
        int invCol = slot % 9;    // 0..8

        // we only use rows 1..4 (i.e. the 2nd through 5th rows)
        // and cols 1..7 (i.e. we skip the first and last column as borders)
        if (invRow < 1 || invRow > 4 || invCol < 1 || invCol > 7) {
            return -1;
        }

        // map to a 7×4 matrix of item‐slots:
        int itemRow = invRow - 1;  // 0..3
        int itemCol = invCol - 1;  // 0..6

        // compute index within this page
        int indexInPage = itemRow * SLOTS_PER_ROW + itemCol;  // 0..(maxItemsPerPage-1)
        long indexLong = (long) page * getMaxItemsPerPage() + indexInPage;

        // overflow / bounds check
        if (indexLong < 0 || indexLong >= getCollectionSize()) {
            return -1;
        }
        return (int) indexLong;
    }
}