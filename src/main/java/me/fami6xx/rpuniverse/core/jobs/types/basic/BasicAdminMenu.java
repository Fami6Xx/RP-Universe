package me.fami6xx.rpuniverse.core.jobs.types.basic;

import me.fami6xx.rpuniverse.core.api.menus.AllWorkingStepsMenu;
import me.fami6xx.rpuniverse.core.api.menus.SellStepsMenu;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class BasicAdminMenu extends Menu {
    private final Job job;
    private final BasicJobTypeData jobTypeData;
    private final Menu menu = this;

    public BasicAdminMenu(PlayerMenu menu, Job job, BasicJobTypeData jobTypeData) {
        super(menu);
        this.job = job;
        this.jobTypeData = jobTypeData;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&6Basic Admin Menu");
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getCurrentItem() == null) return;

        switch (e.getCurrentItem().getType()) {
            case ANVIL:
                new AllWorkingStepsMenu(playerMenu, jobTypeData.workingSteps, job).open();
                break;

            case GOLD_NUGGET:
                new SellStepsMenu(playerMenu, jobTypeData.sellSteps, job.getJobUUID()).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(12, FamiUtils.makeItem(Material.ANVIL, "&6Working Steps", "&7Set the working steps for reworking the item"));
        inventory.setItem(14, FamiUtils.makeItem(Material.GOLD_NUGGET, "&6Sell Steps", "&7Set the locations where you can sell the final item"));

        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
