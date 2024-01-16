package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemStack item = new ItemStack(Material.BEACON);
        ItemMeta meta = item.getItemMeta();
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", jobs.get(index).getName());
        meta.setDisplayName(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().allJobsMenuJobName, placeholders));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int getCollectionSize() {
        return jobs.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {

    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().allJobsMenuName);
    }
}
