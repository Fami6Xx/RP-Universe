package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllJobsMenu extends EasyPaginatedMenu {
    List<Job> jobs = new ArrayList<>();

    public AllJobsMenu(PlayerMenu menu) {
        super(menu);
        jobs.addAll(RPUniverse.getInstance().getJobsHandler().getJobs());
        menu.setEditingJob(null);
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        return FamiUtils.makeItem(Material.BOOK, "&6" + jobs.get(index).getName(), "&7Click to edit");
    }

    @Override
    public int getCollectionSize() {
        return jobs.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        jobs.stream()
                .filter(job1 -> e.getCurrentItem().getItemMeta().getDisplayName().equals(FamiUtils.format("&6" + job1.getName())))
                .findFirst()
                .ifPresent(job -> new JobAdminMenu(playerMenu, job).open());

    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        tags.add(MenuTag.JOB);
        return tags;
    }

    @Override
    public void addAdditionalItems() {

    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&6All Jobs");
    }
}
