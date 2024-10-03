package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.HashMap;
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
        tags.add(MenuTag.JOB_POSITION);
        tags.add(MenuTag.JOB_POSITION_INTERNAL);
        if(adminMenu)
            tags.add(MenuTag.ADMIN);
        else{
            tags.add(MenuTag.BOSS);
            tags.add(MenuTag.PLAYER);
        }
        return tags;
    }

    @Override
    public String getMenuName() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{positionName}", position.getName());
        return FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobPositionMenuName, placeholders);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getSlot() == 1 ){
            UniversalChatHandler universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();
            if(!universalChatHandler.canAddToQueue(playerMenu.getPlayer())) {
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorYouAlreadyHaveSomethingToType);
                return;
            }

            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobPositionMenuRenameMessage);
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().cancelActivityMessage);
            playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            universalChatHandler.addToQueue(playerMenu.getPlayer(), (player, message) -> {
                if(message.equalsIgnoreCase("cancel")) {
                    FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobPositionMenuRenameCancelMessage);
                    return true;
                }

                if(message.isEmpty())
                    return false;

                if(message.length() > 16){
                    FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorPositionNameTooLongMessage);
                    return false;
                }

                position.setName(message);
                this.open();
                return true;
            });
        }

        if(e.getSlot() == 2){
            UniversalChatHandler universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();
            if(!universalChatHandler.canAddToQueue(playerMenu.getPlayer())) {
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorYouAlreadyHaveSomethingToType);
                return;
            }

            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobPositionMenuSalaryMessage);
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().cancelActivityMessage);
            playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            universalChatHandler.addToQueue(playerMenu.getPlayer(), (player, message) -> {
                if(message.equalsIgnoreCase("cancel")) {
                    FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobPositionMenuSalaryCancelMessage);
                    return true;
                }

                try{
                    double salary = Double.parseDouble(message);
                    if(salary < 0){
                        FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorPositionSalaryTooLowMessage);
                        return false;
                    }

                    position.setSalary(salary);
                    this.open();
                    return true;
                }catch (NumberFormatException ex){
                    FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorPositionSalaryNotANumberMessage);
                    return false;
                }
            });
        }

        if(e.getSlot() == 3){
            if(!position.isBoss()){
                UniversalChatHandler universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();
                if(!universalChatHandler.canAddToQueue(playerMenu.getPlayer())) {
                    FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorYouAlreadyHaveSomethingToType);
                    return;
                }

                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobPositionMenuWorkingPermissionLevelMessage);
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().cancelActivityMessage);
                playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                universalChatHandler.addToQueue(playerMenu.getPlayer(), (player, message) -> {
                    if(message.equalsIgnoreCase("cancel")) {
                        FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobPositionMenuWorkingPermissionLevelCancelMessage);
                        return true;
                    }

                    try{
                        int workingPermissionLevel = Integer.parseInt(message);
                        if(workingPermissionLevel < 0){
                            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorPositionWorkingPermissionLevelTooLowMessage);
                            return false;
                        }

                        position.setWorkingStepPermissionLevel(workingPermissionLevel);
                        this.open();
                        return true;
                    }catch (NumberFormatException ex){
                        FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorPositionWorkingPermissionLevelNotANumberMessage);
                        return false;
                    }
                });
            }

            else if(adminMenu){
                position.setBoss(!position.isBoss());
                this.open();
            }

            else {
                position.setDefault(!position.isDefault());
                this.open();
            }
        }

        if(e.getSlot() == 4){
            if(adminMenu){
                position.setBoss(!position.isBoss());
                this.open();
            }

            else {
                position.setDefault(!position.isDefault());
                this.open();
            }
        }

        if(e.getSlot() == 5){
            position.setDefault(!position.isDefault());
            this.open();
        }

        if(e.getSlot() == 7){
            previousMenu.open();
        }

        if(e.getSlot() == 8){
            job.removePosition(position.getName());
            previousMenu.open();
        }
    }

    @Override
    public void setMenuItems() {
        // Name :: 1
        // Salary :: 2
        // Working permission level :: 3 (Only if not boss position)
        // Is boss :: 4 (Only if adminMenu)
        // Is Default :: 5
        // Back :: 7
        // Delete :: 8
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{positionName}", position.getName());
        placeholders.put("{salary}", String.valueOf(position.getSalary()));
        placeholders.put("{workingPermissionLevel}", String.valueOf(position.getWorkingStepPermissionLevel()));
        placeholders.put("{isBoss}", position.isBoss() ? "Yes" : "No");
        placeholders.put("{isDefault}", position.isDefault() ? "Yes" : "No");

        int shownSlots = 0;
        inventory.setItem(++shownSlots, FamiUtils.makeItem(Material.PAPER, RPUniverse.getLanguageHandler().jobPositionMenuRenameDisplayName, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobPositionMenuRenameLore, placeholders)));
        inventory.setItem(++shownSlots, FamiUtils.makeItem(Material.GOLD_INGOT, RPUniverse.getLanguageHandler().jobPositionMenuSalaryDisplayName, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobPositionMenuSalaryLore, placeholders)));
        if(!position.isBoss()){
            inventory.setItem(++shownSlots, FamiUtils.makeItem(Material.COMPASS, RPUniverse.getLanguageHandler().jobPositionMenuWorkingPermissionLevelDisplayName, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobPositionMenuWorkingPermissionLevelLore, placeholders)));
        }
        if(adminMenu){
            inventory.setItem(++shownSlots, FamiUtils.makeItem(Material.DIAMOND, RPUniverse.getLanguageHandler().jobPositionMenuIsBossDisplayName, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobPositionMenuIsBossLore, placeholders)));
        }
        inventory.setItem(++shownSlots, FamiUtils.makeItem(Material.EMERALD, RPUniverse.getLanguageHandler().jobPositionMenuIsDefaultDisplayName, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobPositionMenuIsDefaultLore, placeholders)));
        inventory.setItem(7, FamiUtils.makeItem(Material.REDSTONE_BLOCK, RPUniverse.getLanguageHandler().generalMenuBackItemDisplayName, RPUniverse.getLanguageHandler().generalMenuBackItemLore));
        inventory.setItem(8, FamiUtils.makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().jobPositionMenuDeleteDisplayName, RPUniverse.getLanguageHandler().jobPositionMenuDeleteLore));

        setFillerGlass();
    }
}
