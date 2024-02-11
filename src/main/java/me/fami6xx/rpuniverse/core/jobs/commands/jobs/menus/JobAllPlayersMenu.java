package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin.JobAdminMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class JobAllPlayersMenu extends EasyPaginatedMenu {
    private final boolean isAdmin;
    private final List<UUID> allPlayers = new ArrayList<>();
    private final Menu previousMenu;
    private boolean sorted = false;

    public JobAllPlayersMenu(PlayerMenu menu, Menu previousMenu) {
        super(menu);
        this.isAdmin = previousMenu instanceof JobAdminMenu;
        this.previousMenu = previousMenu;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        if(!sorted)
            sort();

        return getPlayerItem(allPlayers.get(index));
    }

    private void sort(){
        List<UUID> allPlayers = playerMenu.getEditingJob().getAllPlayersInJob();

        allPlayers.sort(Comparator.comparing(player -> playerMenu.getEditingJob().getPositions().indexOf(playerMenu.getEditingJob().getPlayerPosition(player))));

        this.allPlayers.clear();
        this.allPlayers.addAll(allPlayers);
    }

    @Override
    public int getCollectionSize() {
        return playerMenu.getEditingJob().getAllPlayersInJob().size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if(e.getSlot() == 45){
            new JobAddPlayerMenu(playerMenu, this, isAdmin).open();
            return;
        }

        if(e.getSlot() == 53){
            previousMenu.open();
            return;
        }

        for(UUID p : allPlayers){
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(getPlayerItem(p).getItemMeta().getDisplayName())) {
                new JobPlayerMenu(playerMenu, p, this, isAdmin).open();
                return;
            }
        }
    }

    @Override
    public void addAdditionalItems() {
        sorted = false;

        inventory.setItem(45,makeItem(Material.EMERALD_BLOCK, FamiUtils.format(RPUniverse.getLanguageHandler().jobAllPlayersMenuAddPlayerItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().jobAllPlayersMenuAddPlayerItemLore)));
        inventory.setItem(53, makeItem(Material.BARRIER, FamiUtils.format(RPUniverse.getLanguageHandler().generalMenuBackItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().generalMenuBackItemLore)));
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().jobAllPlayersMenuName);
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

    private ItemStack getPlayerItem(UUID player){
        String playerName;
        Player p = Bukkit.getPlayer(player);
        if(p != null)
            playerName = p.getName();
        else
            playerName = Bukkit.getOfflinePlayer(player).getName();

        Position position = playerMenu.getEditingJob().getPlayerPosition(player);
        if(position == null)
            return null;

        String playerPosition = position.getName();
        String playerSalary = position.getSalary() + "";

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{playerName}", playerName);
        placeholders.put("{positionName}", playerPosition);
        placeholders.put("{salary}", playerSalary);
        placeholders.put("{jobName}", playerMenu.getEditingJob().getName());
        placeholders.put("{isOnline}", p == null ? "Yes" : "No");

        return makeItem(
                Material.DIAMOND_HELMET,
                FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAllPlayersMenuPlayerItemDisplayName, placeholders),
                FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAllPlayersMenuPlayerItemLore, placeholders)
        );
    }
}
