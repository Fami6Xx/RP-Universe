package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin.JobAdminMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class JobAllPlayersMenu extends EasyPaginatedMenu {
    private final boolean isAdmin;
    private final List<UUID> allPlayers = new ArrayList<>();
    private boolean sorted = false;

    public JobAllPlayersMenu(PlayerMenu menu, Menu previousMenu) {
        super(menu);
        this.isAdmin = previousMenu instanceof JobAdminMenu;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        if(!sorted)
            sort();


        return null;
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

    }

    @Override
    public void addAdditionalItems() {
        sorted = false;
    }

    @Override
    public String getMenuName() {
        return null;
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);
        if(isAdmin)
            tags.add(MenuTag.ADMIN);
        return tags;
    }
}
