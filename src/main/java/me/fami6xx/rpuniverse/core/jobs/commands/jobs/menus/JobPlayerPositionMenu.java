package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JobPlayerPositionMenu extends EasyPaginatedMenu {
    private final List<Position> positions = new ArrayList<>();
    private final Menu previousMenu;
    private final boolean isAdmin;
    private final UUID player;

    public JobPlayerPositionMenu(PlayerMenu menu, UUID player, Menu previousMenu, boolean isAdmin) {
        super(menu);
        this.player = player;
        this.previousMenu = previousMenu;
        this.isAdmin = isAdmin;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().jobSelectPositionMenuName);
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        Position position = positions.get(index);
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{positionName}", position.getName());
        placeholders.put("{positionSalary}", String.valueOf(position.getSalary()));
        return FamiUtils.makeItem(Material.PAPER, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobSelectPositionMenuPositionItemDisplayName, placeholders), FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobSelectPositionMenuPositionItemLore, placeholders));
    }

    @Override
    public int getCollectionSize() {
        return positions.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if(e.getSlot() == 53){
            previousMenu.open();
            return;
        }

        for(Position position : positions){
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{positionName}", position.getName());
            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobSelectPositionMenuPositionItemDisplayName, placeholders))){
                playerMenu.getEditingJob().changePlayerPosition(player, position);
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobSelectPositionMenuSelectPositionMessage);
                previousMenu.open();
                return;
            }
        }
    }

    @Override
    public void addAdditionalItems() {
        this.positions.clear();
        if(isAdmin)
            this.positions.addAll(playerMenu.getEditingJob().getPositions());
        else
            this.positions.addAll(playerMenu.getEditingJob().getAllPositionsPlayerCanAssign(player));

        this.inventory.setItem(53, FamiUtils.makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().generalMenuBackItemDisplayName, RPUniverse.getLanguageHandler().generalMenuBackItemLore));
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
