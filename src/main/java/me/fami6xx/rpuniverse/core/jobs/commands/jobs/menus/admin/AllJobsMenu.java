package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin.JobAdminMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
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
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", jobs.get(index).getName());
        return makeItem(Material.BEACON, FamiUtils.replace(RPUniverse.getLanguageHandler().allJobsMenuJobName, placeholders));
    }

    @Override
    public int getCollectionSize() {
        return jobs.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        jobs.stream()
                .filter(job1 -> {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("{jobName}", job1.getName());
                    return e.getCurrentItem().getItemMeta().getDisplayName().equals(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().allJobsMenuJobName, placeholders));
                })
                .findFirst()
                .ifPresent(job -> new JobAdminMenu(playerMenu, job).open());

    }

    @Override
    public void addAdditionalItems() {

    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().allJobsMenuName);
    }
}
