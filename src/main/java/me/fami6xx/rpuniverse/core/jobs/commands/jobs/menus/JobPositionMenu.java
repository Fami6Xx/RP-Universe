package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class JobPositionMenu extends Menu {
    private final Job job;
    private final Position position;
    private final boolean adminMenu;
    private final Menu previousMenu;

    public JobPositionMenu(PlayerMenu menu, Job job, Position position, boolean adminMenu, Menu previousMenu) {
        super(menu);

        if(menu.getEditingJob() != job)
            menu.setEditingJob(job);

        this.job = job;
        this.position = position;
        this.adminMenu = adminMenu;
        this.previousMenu = previousMenu;
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);

        return tags;
    }

    @Override
    public String getMenuName() {
        return null;
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

    }

    @Override
    public void setMenuItems() {

    }
}
