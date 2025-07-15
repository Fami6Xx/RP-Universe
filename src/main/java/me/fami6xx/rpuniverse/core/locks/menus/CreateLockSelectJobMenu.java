package me.fami6xx.rpuniverse.core.locks.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.locks.LockHandler;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.IChatExecuteQueue;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class CreateLockSelectJobMenu extends EasyPaginatedMenu{
    private Block block;
    private List<Job> jobs = new ArrayList<>();

    public CreateLockSelectJobMenu(PlayerMenu menu, Block block) {
        super(menu);
        this.block = block;
        jobs.addAll(RPUniverse.getInstance().getJobsHandler().getJobs());
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", jobs.get(index).getName());
        return FamiUtils.makeItem(Material.BEACON, FamiUtils.replace(RPUniverse.getLanguageHandler().allJobsMenuJobName, placeholders));
    }

    @Override
    public int getCollectionSize() {
        return jobs.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        Optional<Job> optionalJob = jobs.stream()
            .filter(job1 -> {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("{jobName}", job1.getName());
                return e.getCurrentItem().getItemMeta().getDisplayName().equals(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().allJobsMenuJobName, placeholders));
            })
            .findFirst();

        if(!optionalJob.isPresent()) {
            return;
        }

        Job job = optionalJob.get();
        
        playerMenu.getPlayer().closeInventory();
        FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().createLockTypeMinimalWorkingLevel);
        FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().cancelActivityMessage);
        RPUniverse.getInstance().getUniversalChatHandler().addToQueue(playerMenu.getPlayer(), new IChatExecuteQueue() {
            @Override
            public boolean execute(Player player, String message) {
                if (message.equalsIgnoreCase("cancel")) {
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createLockCanceled);
                    return true;
                }

                if(FamiUtils.isInteger(message)) {
                    int level = Integer.parseInt(message);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            LockHandler lockHandler = RPUniverse.getInstance().getLockHandler();
                            lockHandler.createLock(block.getLocation().toCenterLocation(), block.getType(), null, job.getName(), level);
                            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().lockCreationSuccess);
                        }
                    }.runTaskLater(RPUniverse.getInstance(), 1L);
                    return true;
                }

                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createLockTypeMinimalWorkingLevel);
                return false;
            }
        });
    }

    @Override
    public void addAdditionalItems() {
        return;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().createLockSelectJobMenuName);
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        tags.add(MenuTag.JOB);
        return tags;
    }

}
