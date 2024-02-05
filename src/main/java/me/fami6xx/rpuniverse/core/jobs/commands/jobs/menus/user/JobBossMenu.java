package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.user;

import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class JobBossMenu extends Menu {
    private final Job job;
    public JobBossMenu(PlayerMenu menu, Job job) {
        super(menu);
        this.job = job;

        if(menu.getEditingJob() != job)
            menu.setEditingJob(job);
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

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);
        tags.add(MenuTag.BOSS);
        return tags;
    }
}
