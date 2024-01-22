package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.IChatExecuteQueue;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class JobAdminMenu extends Menu {
    private Job job;

    public JobAdminMenu(PlayerMenu menu, Job job) {
        super(menu);
        this.job = job;
    }

    @Override
    public String getMenuName() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", job.getName());
        return FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAdminMenuName, placeholders);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        // 1-7
        if(e.getSlot() == 1){
            JobAdminMenu menu = this;

            if(!RPUniverse.getInstance().getUniversalChatHandler().canAddToQueue(playerMenu.getPlayer())){
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorYouAlreadyHaveSomethingToType);
                return;
            }

            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().createJobCommandTypeNameMessage);
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().cancelActivityMessage);

            playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);

            RPUniverse.getInstance().getUniversalChatHandler().addToQueue(playerMenu.getPlayer(), (player, message) -> {
                if(message.equalsIgnoreCase("cancel")){
                    menu.open();
                    return true;
                }

                if(message.length() > 16){
                    playerMenu.getPlayer().sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorJobNameTooLongMessage));
                    return false;
                }
                if(RPUniverse.getInstance().getJobsHandler().getJobByName(message) != null){
                    playerMenu.getPlayer().sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorJobNameAlreadyExistsMessage));
                    return false;
                }
                job.renameJob(message);
                menu.open();
                return true;
            });
        }
        if(e.getSlot() == 2){
            new JobBankActionsMenu(playerMenu, this, job).open();
        }
        if(e.getSlot() == 3){
            new JobAllPositionsMenu(playerMenu, job, true).open();
        }
        if(e.getSlot() == 4){

        }
        if(e.getSlot() == 5){

        }
        if(e.getSlot() == 6){
            System.out.println("Open admin job type menu");
        }
        if(e.getSlot() == 7){
            RPUniverse.getInstance().getJobsHandler().getJobs().remove(job);
            RPUniverse.getInstance().getDataSystem().getDataHandler().removeJobData(job.getName());
            new AllJobsMenu(playerMenu).open();
        }
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
        this.inventory.setItem(1, makeItem(Material.NAME_TAG, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRenameItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRenameItemLore)));
        this.inventory.setItem(2, makeItem(Material.GOLD_INGOT, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobBankItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobBankItemLore)));
        this.inventory.setItem(3, makeItem(Material.BEACON, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuPositionItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuPositionItemLore)));
        this.inventory.setItem(4, makeItem(Material.ENDER_PEARL, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuBossItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuBossItemLore)));
        this.inventory.setItem(5, makeItem(Material.BOOK, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeItemLore)));
        if(job.getJobType() != null && !job.getJobType().hasAdminMenu())
            this.inventory.setItem(6, makeItem(Material.BOOK_AND_QUILL, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeAdminItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeAdminItemLore)));
        this.inventory.setItem(7, makeItem(Material.BARRIER, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRemoveItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRemoveItemLore)));
        setFillerGlass();
    }
}
