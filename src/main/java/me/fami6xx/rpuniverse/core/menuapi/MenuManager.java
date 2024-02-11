package me.fami6xx.rpuniverse.core.menuapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.handlers.MenuInvClickHandler;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

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

    public void closeAllMenus(Predicate<Job> predicate) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getEditingJob())) {
                closeMenu(playerMenu.getPlayer());
            }
        }
    }

    public void closeAllMenusUUIDPredicate(Predicate<UUID> predicate) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getPlayer().getUniqueId())) {
                closeMenu(playerMenu.getPlayer());
            }
        }
    }

    public void closeAllMenusUUIDPredicate(Predicate<UUID> predicate, MenuTag... tags) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getPlayer().getUniqueId())) {
                boolean hasTag = false;
                for (MenuTag tag : playerMenu.getCurrentMenu().getMenuTags()) {
                    for (MenuTag tag2 : tags) {
                        if (tag == tag2) {
                            hasTag = true;
                            break;
                        }
                    }
                }
                if(hasTag)
                    closeMenu(playerMenu.getPlayer());
            }
        }
    }

    public void reopenMenu(Player player) {
        if (playerMenuMap.containsKey(player)) {
            PlayerMenu playerMenu = playerMenuMap.get(player);
            if (playerMenu.getCurrentMenu() != null) {
                playerMenu.getCurrentMenu().open();
            }
        }
    }

    public void reopenMenus(Predicate<Job> predicate) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getEditingJob())) {
                playerMenu.getCurrentMenu().open();
            }
        }
    }

    public void reopenMenus(Predicate<Job> predicate, MenuTag... tags) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getEditingJob())) {
                boolean hasTag = false;
                for (MenuTag tag : playerMenu.getCurrentMenu().getMenuTags()) {
                    for (MenuTag tag2 : tags) {
                        if (tag == tag2) {
                            hasTag = true;
                            break;
                        }
                    }
                }
                if (hasTag) {
                    playerMenu.getCurrentMenu().open();
                }
            }
        }
    }

    public void closeAllMenus(Predicate<Job> predicate, MenuTag... tags) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getEditingJob())) {
                boolean hasTag = false;
                for (MenuTag tag : playerMenu.getCurrentMenu().getMenuTags()) {
                    for (MenuTag tag2 : tags) {
                        if (tag == tag2) {
                            hasTag = true;
                            break;
                        }
                    }
                }
                if (hasTag) {
                    closeMenu(playerMenu.getPlayer());
                }
            }
        }
    }

    public void closeAllMenus() {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null) {
                closeMenu(playerMenu.getPlayer());
            }
        }
    }

    public void closeMenu(Player player) {
        if (playerMenuMap.containsKey(player)) {
            player.closeInventory();
        }
    }
}
