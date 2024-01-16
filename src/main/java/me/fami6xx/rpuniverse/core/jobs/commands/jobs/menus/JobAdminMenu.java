package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class JobAdminMenu extends Menu {
    private Job job;

    public JobAdminMenu(PlayerMenu menu, Job job) {
        super(menu);
        this.job = job;
    }

    @Override
    public String getMenuName() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", job.getName());
        return FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAdminMenuName, placeholders);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        // 1-7
    }

    @Override
    public void setMenuItems() {
        // Rename :: 1
        // JobBank actions : Add / Remove money from job bank :: 2
        // Position actions : Add / Modify / Remove positions :: 3
        // Boss menu actions : Port to boss menu, change location :: 4
        // Select job type : Change job type :: 5
        // Open admin job type menu :: 6
        // Remove job :: 7
        this.inventory.setItem(1, makeItem(Material.NAME_TAG, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRenameItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRenameItemLore)));
        this.inventory.setItem(2, makeItem(Material.GOLD_INGOT, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobBankItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobBankItemLore)));
        this.inventory.setItem(3, makeItem(Material.BEACON, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuPositionItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuPositionItemLore)));
        this.inventory.setItem(4, makeItem(Material.ENDER_PEARL, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuBossItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuBossItemLore)));
        this.inventory.setItem(5, makeItem(Material.BOOK, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeItemLore)));
        this.inventory.setItem(6, makeItem(Material.BOOK_AND_QUILL, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeAdminItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeAdminItemLore)));
        this.inventory.setItem(7, makeItem(Material.BARRIER, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRemoveItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRemoveItemLore)));
        setFillerGlass();
    }
}
