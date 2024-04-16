package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.user;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.JobAllPlayersMenu;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.JobAllPositionsMenu;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.JobBankActionsMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.HashMap;
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
        return ChatColor.translateAlternateColorCodes('&', RPUniverse.getLanguageHandler().jobBossMenuName);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getSlot() == 1) {
            new JobBankActionsMenu(playerMenu, this, job).open();
        } else if (e.getSlot() == 3) {
            new JobAllPositionsMenu(playerMenu, job, false).open();
        } else if (e.getSlot() == 5) {
            new JobAllPlayersMenu(playerMenu, this).open();
        } else if (e.getSlot() == 7) {
            if (job.getJobType() != null) {
                if (job.getJobType().hasBossMenu()) {
                    job.getJobType().openBossMenu(playerMenu.getPlayer());
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(1, makeItem(Material.GOLD_INGOT, FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuJobBankItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuJobBankItemLore)));
        inventory.setItem(3, makeItem(Material.PAPER, FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuPositionItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuPositionItemLore)));
        inventory.setItem(5, makeItem(Material.LEATHER, FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuAllPlayersItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuAllPlayersItemLore)));
        if (job.getJobType() != null) {
            if (job.getJobType().hasBossMenu()) {
                inventory.setItem(7, makeItem(Material.DIAMOND, FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuJobTypeBossItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuJobTypeBossItemLore)));
            }
        }
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);
        tags.add(MenuTag.BOSS);
        return tags;
    }
}
