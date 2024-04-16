package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class JobBossLocationMenu extends Menu {
    private final Job job;
    private final Menu previousMenu;
    public JobBossLocationMenu(PlayerMenu menu, Job job, Menu previousMenu) {
        super(menu);
        this.job = job;
        this.previousMenu = previousMenu;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().jobBossLocationMenuName);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getSlot() == 3){
            playerMenu.getPlayer().teleport(job.getBossMenuLocation());
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobBossLocationMenuTeleportToBossMenuMessage);
        }

        if(e.getSlot() == 5){
            UniversalChatHandler universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();

            if(!universalChatHandler.canAddToQueue(playerMenu.getPlayer())){
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorYouAlreadyHaveSomethingToType);
                return;
            }

            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().createJobCommandBossMenuLocationMessage);
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().cancelActivityMessage);
            universalChatHandler.addToQueue(playerMenu.getPlayer(), (player, message) -> {
                if(message.equalsIgnoreCase("cancel")){
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().jobBossLocationMenuChangeLocationCancelMessage);
                    this.open();
                    return true;
                }

                if(message.equalsIgnoreCase("here")){
                    job.setBossMenuLocation(player.getLocation().toCenterLocation());
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().jobBossLocationMenuChangeLocationMessage);
                    this.open();
                    return true;
                }

                return false;
            });
        }

        if(e.getSlot() == 8){
            previousMenu.open();
        }
    }

    @Override
    public void setMenuItems() {
        // Teleport :: 3
        // Change :: 5
        // Back :: 8

        inventory.setItem(3, FamiUtils.makeItem(Material.COMPASS, RPUniverse.getLanguageHandler().jobBossLocationMenuTeleportToBossMenuItemDisplayName, RPUniverse.getLanguageHandler().jobBossLocationMenuTeleportToBossMenuItemLore));
        inventory.setItem(5, FamiUtils.makeItem(Material.BOOK, RPUniverse.getLanguageHandler().jobBossLocationMenuChangeLocationItemDisplayName, RPUniverse.getLanguageHandler().jobBossLocationMenuChangeLocationItemLore));
        inventory.setItem(8, FamiUtils.makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().generalMenuBackItemDisplayName, RPUniverse.getLanguageHandler().generalMenuBackItemLore));

        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        tags.add(MenuTag.JOB);
        return null;
    }
}
