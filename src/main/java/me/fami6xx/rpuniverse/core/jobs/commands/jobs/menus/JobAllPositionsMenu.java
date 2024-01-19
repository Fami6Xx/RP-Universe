package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobAllPositionsMenu extends EasyPaginatedMenu {
    private final Job job;
    private final boolean adminMenu;
    private final List<Position> positionList = new ArrayList<>();
    public JobAllPositionsMenu(PlayerMenu menu, Job job, boolean adminMenu) {
        super(menu);
        this.job = job;
        this.adminMenu = adminMenu;
        this.positionList.addAll(job.getPositions());
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        Position position = positionList.get(index);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{positionName}", position.getName());
        placeholders.put("{positionSalary}", String.valueOf(position.getSalary()));
        placeholders.put("{positionWorkingPermissionLevel}", String.valueOf(position.getWorkingStepPermissionLevel()));
        String displayName = FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAllPositionsMenuPositionItemDisplayName, placeholders);
        String lore = FamiUtils.format(RPUniverse.getLanguageHandler().jobAllPositionsMenuPositionItemLore);

        return makeItem(Material.PAPER, displayName, lore);
    }

    @Override
    public int getCollectionSize() {
        return positionList.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {

    }

    @Override
    public void addAdditionalItems() {
        super.inventory.setItem(45, makeItem(Material.EMERALD_BLOCK, RPUniverse.getLanguageHandler().jobAllPositionsMenuAddPositionItemDisplayName, RPUniverse.getLanguageHandler().jobAllPositionsMenuAddPositionItemLore));
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().jobAllPositionsMenuName);
    }
}
