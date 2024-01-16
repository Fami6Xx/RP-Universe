package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.event.inventory.InventoryClickEvent;

public class JobAdminMenu extends Menu {
    private Job job;

    public JobAdminMenu(PlayerMenu menu, Job job) {
        super(menu);
        this.job = job;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuName);
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
    }
}
