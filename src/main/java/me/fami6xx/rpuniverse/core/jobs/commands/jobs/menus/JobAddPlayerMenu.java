package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobAddPlayerMenu extends EasyPaginatedMenu {
    private final Menu previousMenu;
    private final boolean isAdmin;
    private final List<Player> playersThatCanBeAdded = new ArrayList<>();

    public JobAddPlayerMenu(PlayerMenu menu, Menu previousMenu, boolean isAdmin) {
        super(menu);
        this.previousMenu = previousMenu;
        this.isAdmin = isAdmin;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{playerName}", playersThatCanBeAdded.get(index).getName());

        return makeItem(Material.DIAMOND, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobSelectPlayerToAddMenuPlayerItemDisplayName, placeholders), RPUniverse.getLanguageHandler().jobSelectPlayerToAddMenuPlayerItemLore);
    }

    @Override
    public int getCollectionSize() {
        return playersThatCanBeAdded.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if(e.getSlot() == 53){
            previousMenu.open();
            return;
        }

        Player player = null;
        for(Player p : playersThatCanBeAdded){
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{playerName}", p.getName());

            if(e.getCurrentItem().getItemMeta().getDisplayName().equals(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobSelectPlayerToAddMenuPlayerItemDisplayName, placeholders))){
                player = p;
                break;
            }
        }
        if(player == null) return;

        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());
        if(!data.canBeAddedToJob(playerMenu.getEditingJob())) return;

        playerMenu.getEditingJob().addPlayerToJob(player.getUniqueId());
        FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobSelectPlayerToAddMenuPlayerAddedMessage);
        previousMenu.open();
    }

    @Override
    public void addAdditionalItems() {
        playerMenu.getPlayer().getLocation().getNearbyPlayers(RPUniverse.getInstance().getConfiguration().getDouble("jobs.distanceToAddToJob")).forEach(player -> {
            if(playerMenu.getEditingJob().getPlayerPosition(player.getUniqueId()) == null){
                if(RPUniverse.getPlayerData(player.getUniqueId().toString()).canBeAddedToJob(playerMenu.getEditingJob())) {
                    playersThatCanBeAdded.add(player);
                }
            }
        });

        if(playersThatCanBeAdded.isEmpty())
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobAllPlayersMenuAddPlayerNoAvailablePlayersMessage);

        inventory.setItem(53, makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().generalMenuBackItemDisplayName, RPUniverse.getLanguageHandler().generalMenuBackItemLore));
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().jobSelectPlayerToAddMenuName);
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
