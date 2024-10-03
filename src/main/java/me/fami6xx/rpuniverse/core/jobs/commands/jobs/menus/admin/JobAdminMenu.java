package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.JobAllPlayersMenu;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.JobAllPositionsMenu;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.JobBankActionsMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobAdminMenu extends Menu {
    private Job job;

    public JobAdminMenu(PlayerMenu menu, Job job) {
        super(menu);
        this.job = job;
        if(menu.getEditingJob() != job)
            menu.setEditingJob(job);
    }

    @Override
    public String getMenuName() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", job.getName());
        return FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAdminMenuName, placeholders);
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        // 1-7
        if(e.getSlot() == 13){
            Player player = playerMenu.getPlayer();
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            List<String> ready = job.isJobReady();
            FamiUtils.sendMessageWithPrefix(player, "&7Showing all information for job &6" + job.getName());
            FamiUtils.sendMessage(player, "&7Job type: &6" +(job.getJobType() == null ? "None" : job.getJobType().getName()));
            FamiUtils.sendMessage(player, "&7Job bank: &6" + job.getCurrentMoneyInJobBank());
            FamiUtils.sendMessage(player, "&7Current positions: &6" + job.getPositions().size());
            FamiUtils.sendMessage(player, "&7All players with this job: &6" + job.getAllPlayersInJob().size());
            FamiUtils.sendMessage(player, "&7Boss can edit positions? &6" + (job.isBossCanEditPositions() ? "Yes" : "No"));
            FamiUtils.sendMessage(player, "&7Players receive salary? &6" + (job.isPlayersReceiveSalary() ? "Yes" : "No"));
            FamiUtils.sendMessage(player, "&7Salary interval: &6" + job.getSalaryInterval());
            FamiUtils.sendMessage(player, "&7Is job ready? &6" + (ready.isEmpty() ? "Yes" : "No"));
            if(!ready.isEmpty()){
                FamiUtils.sendMessage(player, "&7Reasons:");
                ready.forEach(s -> FamiUtils.sendMessage(player, "&7- " + s));
            }

        }
        if(e.getSlot() == 19){
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
        if(e.getSlot() == 20){
            new JobBankActionsMenu(playerMenu, this, job).open();
        }
        if(e.getSlot() == 21){
            new JobAllPositionsMenu(playerMenu, job, true).open();
        }
        if(e.getSlot() == 22){
            new JobBossLocationMenu(playerMenu, job, this).open();
        }
        if(e.getSlot() == 23){
            new JobSelectJobType(playerMenu, job, this).open();
        }
        if(e.getSlot() == 24){
            if(job.isJobReady().isEmpty())
                new JobAllPlayersMenu(playerMenu, this).open();
            else
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorJobNotReadyMessage);
        }
        if(e.getSlot() == 25){
            new JobSettingsMenu(playerMenu, this).open();
        }
        if(e.getSlot() == 31){
            if(job.getJobType() == null || !job.getJobType().hasAdminMenu())
                return;

            job.getJobType().openAdminMenu(playerMenu.getPlayer());
        }
        if(e.getSlot() == 44){
            RPUniverse.getInstance().getJobsHandler().removeJob(job);
            new AllJobsMenu(playerMenu).open();
        }
    }

    @Override
    public void setMenuItems() {
        // 11,13,15
        // 19-25
        // 29,31,33

        // Information about the job :: 13
        // Rename :: 19
        // JobBank actions : Add / Remove money from job bank :: 20
        // Position actions : Add / Modify / Remove positions :: 21
        // Boss menu actions : Port to boss menu, change location :: 22
        // Select job type : Change job type :: 23
        // All players with this job :: 24
        // Admin menu for this job :: 25
        // Job Settings :: 29
        // Something :: 30

        // Open admin job type menu :: 31 - Because it doesn't have to be there
        // Remove job :: 44
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", job.getName());
        placeholders.put("{jobType}", job.getJobType() == null ? "None" : job.getJobType().getName());
        placeholders.put("{jobBank}", String.valueOf(job.getCurrentMoneyInJobBank()));
        placeholders.put("{jobPositions}", String.valueOf(job.getPositions().size()));
        placeholders.put("{jobPlayers}", String.valueOf(job.getAllPlayersInJob().size()));
        placeholders.put("{jobReady}", job.isJobReady().isEmpty() ? "Yes" : "No");

        this.inventory.setItem(13, FamiUtils.makeItem(Material.BOOK, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAdminMenuInformationItemDisplayName, placeholders), FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAdminMenuInformationItemLore, placeholders)));
        this.inventory.setItem(19, FamiUtils.makeItem(Material.NAME_TAG, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRenameItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRenameItemLore)));
        this.inventory.setItem(20, FamiUtils.makeItem(Material.GOLD_INGOT, FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuJobBankItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuJobBankItemLore)));
        this.inventory.setItem(21, FamiUtils.makeItem(Material.BEACON, FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuPositionItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuPositionItemLore)));
        this.inventory.setItem(22, FamiUtils.makeItem(Material.ENDER_PEARL, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuBossItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuBossItemLore)));
        this.inventory.setItem(23, FamiUtils.makeItem(Material.BOOK, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeItemLore)));
        this.inventory.setItem(24, FamiUtils.makeItem(Material.PLAYER_HEAD, FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuAllPlayersItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobMenuAllPlayersItemLore)));
        this.inventory.setItem(25, FamiUtils.makeItem(Material.IRON_SWORD, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuSettingsItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuSettingsItemLore)));
        if(job.getJobType() != null && job.getJobType().hasAdminMenu())
            this.inventory.setItem(31, FamiUtils.makeItem(Material.WRITABLE_BOOK, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeAdminItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuJobTypeAdminItemLore)));
        this.inventory.setItem(44, FamiUtils.makeItem(Material.BARRIER, FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRemoveItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAdminMenuRemoveItemLore)));
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);
        tags.add(MenuTag.ADMIN);
        return tags;
    }
}
