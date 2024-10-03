package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class JobSelectJobType extends EasyPaginatedMenu {
    private final List<JobType> jobTypes;
    private final Job job;
    private final Menu previousMenu;

    public JobSelectJobType(PlayerMenu menu, Job job, Menu previousMenu) {
        super(menu);

        this.jobTypes = RPUniverse.getInstance().getJobsHandler().getJobTypes();
        this.job = job;
        this.previousMenu = previousMenu;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        JobType jobType = jobTypes.get(index);
        ItemStack item = jobType.getIcon();
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(RPUniverse.getInstance(), "jobType");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, index);
        return jobType.getIcon();
    }

    @Override
    public int getCollectionSize() {
        return jobTypes.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if (e.getSlot() == 53) {
            previousMenu.open();
            return;
        }

        ItemStack item = e.getCurrentItem();
        if(item == null) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(RPUniverse.getInstance(), "jobType");
        if(meta == null || !meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return;

        int index = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        JobType jobType = jobTypes.get(index);
        job.setJobType(jobType);
        new JobAdminMenu(playerMenu, job).open();
    }

    @Override
    public void addAdditionalItems() {
        inventory.setItem(53, FamiUtils.makeItem(Material.ARROW, FamiUtils.format("&7Back"), FamiUtils.format("&7Go back to the previous menu.")));
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format("&c&lRPU &8» &7Select Job Type");
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);
        tags.add(MenuTag.ADMIN);
        return tags;
    }
}
