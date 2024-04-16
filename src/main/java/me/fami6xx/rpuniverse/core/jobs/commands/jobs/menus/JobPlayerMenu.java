package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JobPlayerMenu extends Menu {
    private final Menu previousMenu;
    private final boolean isAdmin;
    private final UUID player;

    public JobPlayerMenu(PlayerMenu menu, UUID player, Menu previousMenu, boolean isAdmin) {
        super(menu);
        this.player = player;
        this.previousMenu = previousMenu;
        this.isAdmin = isAdmin;
    }

    @Override
    public String getMenuName() {
        HashMap<String, String> replace = new HashMap<>();
        replace.put("{jobName}", playerMenu.getEditingJob().getName());
        String playerName;
        Player p = Bukkit.getPlayer(player);
        if(p != null)
            playerName = p.getName();
        else
            playerName = Bukkit.getOfflinePlayer(player).getName();
        replace.put("{playerName}", playerName);

        return FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobPlayerMenuName, replace);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getSlot() == 1){
            if(!isAdmin && !playerMenu.getEditingJob().canPlayerKickPlayer(playerMenu.getPlayer().getUniqueId(), player) && !playerMenu.getEditingJob().getAllPositionsPlayerCanAssign(playerMenu.getPlayer().getUniqueId()).isEmpty()){
                FamiUtils.sendMessageWithPrefix((Player) e.getWhoClicked(), RPUniverse.getLanguageHandler().jobPlayerMenuCannotChangePositionMessage);
                return;
            }
            new JobPlayerPositionMenu(playerMenu, player, this, isAdmin).open();
            return;
        }

        if(e.getSlot() == 3){
            if(!isAdmin && !playerMenu.getEditingJob().canPlayerKickPlayer(playerMenu.getPlayer().getUniqueId(), player)){
                FamiUtils.sendMessageWithPrefix((Player) e.getWhoClicked(), RPUniverse.getLanguageHandler().jobPlayerMenuCannotKickMessage);
                return;
            }
            FamiUtils.sendMessageWithPrefix((Player) e.getWhoClicked(), RPUniverse.getLanguageHandler().jobPlayerMenuKickMessage);
            playerMenu.getEditingJob().removePlayerFromJob(player);
            previousMenu.open();
            return;
        }

        if(e.getSlot() == 7){
            previousMenu.open();
        }
    }

    @Override
    public void setMenuItems() {
        this.setFillerGlass();
        HashMap<String, String> replace = new HashMap<>();
        replace.put("{jobName}", playerMenu.getEditingJob().getName());
        replace.put("{positionName}", playerMenu.getEditingJob().getPlayerPosition(player).getName());
        inventory.setItem(1, FamiUtils.makeItem(Material.ANVIL, RPUniverse.getLanguageHandler().jobPlayerMenuPositionDisplayName, FamiUtils.replace(RPUniverse.getLanguageHandler().jobPlayerMenuPositionLore, replace)));
        inventory.setItem(3, FamiUtils.makeItem(Material.REDSTONE, RPUniverse.getLanguageHandler().jobPlayerMenuKickDisplayName, RPUniverse.getLanguageHandler().jobPlayerMenuKickLore));
        inventory.setItem(7, FamiUtils.makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().generalMenuBackItemDisplayName, RPUniverse.getLanguageHandler().generalMenuBackItemLore));
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);
        if(isAdmin)
            tags.add(MenuTag.ADMIN);
        else
            tags.add(MenuTag.BOSS);
        return tags;
    }
}
