package me.fami6xx.rpuniverse.core.menuapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.handlers.MenuInvClickHandler;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class MenuManager {
    MenuInvClickHandler clickHandler;

    private static final HashMap<Player, PlayerMenu> playerMenuMap = new HashMap<>();

    public boolean enable() {
        this.clickHandler = new MenuInvClickHandler();
        RPUniverse.getInstance().getServer().getPluginManager().registerEvents(this.clickHandler, RPUniverse.getInstance());
        return true;
    }

    public boolean disable() {
        InventoryClickEvent.getHandlerList().unregister(this.clickHandler);
        return true;
    }

    public PlayerMenu getPlayerMenu(Player player){
        PlayerMenu playerMenu;
        if (!(playerMenuMap.containsKey(player))) {
            playerMenu = new PlayerMenu(player);
            playerMenuMap.put(player, playerMenu);

            return playerMenu;
        } else {
            return playerMenuMap.get(player);
        }
    }
}
