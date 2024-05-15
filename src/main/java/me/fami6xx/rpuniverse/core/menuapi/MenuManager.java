package me.fami6xx.rpuniverse.core.menuapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.handlers.MenuInvClickHandler;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Manages player menus and handles menu-related events.
 */
public class MenuManager {
    MenuInvClickHandler clickHandler;

    private static final HashMap<Player, PlayerMenu> playerMenuMap = new HashMap<>();

    /**
     * Enables the menu manager by registering the click handler.
     *
     * @return true if the operation was successful.
     */
    public boolean enable() {
        this.clickHandler = new MenuInvClickHandler();
        RPUniverse.getInstance().getServer().getPluginManager().registerEvents(this.clickHandler, RPUniverse.getInstance());
        return true;
    }

    /**
     * Disables the menu manager by unregistering the click handler.
     *
     * @return true if the operation was successful.
     */
    public boolean disable() {
        InventoryClickEvent.getHandlerList().unregister(this.clickHandler);
        return true;
    }

    /**
     * Retrieves the PlayerMenu for a given player, creating one if it doesn't exist.
     *
     * @param player the player whose menu is to be retrieved.
     * @return the PlayerMenu associated with the player.
     */
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

    /**
     * Closes all job menus that match the given predicate.
     *
     * @param predicate the predicate to test jobs against.
     */
    public void closeAllJobMenus(Predicate<Job> predicate) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getEditingJob())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        closeMenu(playerMenu.getPlayer());
                    }
                }.runTask(RPUniverse.getInstance());
            }
        }
    }

    /**
     * Closes all menus for players whose UUID matches the given predicate.
     *
     * @param predicate the predicate to test UUIDs against.
     */
    public void closeAllMenusUUIDPredicate(Predicate<UUID> predicate) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getPlayer().getUniqueId())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        closeMenu(playerMenu.getPlayer());
                    }
                }.runTask(RPUniverse.getInstance());
            }
        }
    }

    /**
     * Closes all menus for players whose UUID matches the given predicate and have the specified tags.
     *
     * @param predicate the predicate to test UUIDs against.
     * @param tags      the tags to check for.
     */
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
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            closeMenu(playerMenu.getPlayer());
                        }
                    }.runTask(RPUniverse.getInstance());
            }
        }
    }

    /**
     * Reopens the menu for a given player if it exists.
     *
     * @param player the player whose menu is to be reopened.
     */
    public void reopenMenu(Player player) {
        if (playerMenuMap.containsKey(player)) {
            PlayerMenu playerMenu = playerMenuMap.get(player);
            if (playerMenu.getCurrentMenu() != null) {
                playerMenu.getCurrentMenu().open();
            }
        }
    }

    /**
     * Reopens menus for players that match the given predicate.
     *
     * @param predicate the predicate to test players against.
     */
    public void reopenMenus(Predicate<Player> predicate) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getPlayer())) {
                playerMenu.getCurrentMenu().open();
            }
        }
    }

    /**
     * Reopens job menus for jobs that match the given predicate.
     *
     * @param predicate the predicate to test jobs against.
     */
    public void reopenJobMenus(Predicate<Job> predicate) {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null && predicate.test(playerMenu.getEditingJob())) {
                playerMenu.getCurrentMenu().open();
            }
        }
    }

    /**
     * Reopens job menus for jobs that match the given predicate and have the specified tags.
     *
     * @param predicate the predicate to test jobs against.
     * @param tags      the tags to check for.
     */
    public void reopenJobMenus(Predicate<Job> predicate, MenuTag... tags) {
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

    /**
     * Closes all job menus that match the given predicate and have the specified tags.
     *
     * @param predicate the predicate to test jobs against.
     * @param tags      the tags to check for.
     */
    public void closeAllJobMenus(Predicate<Job> predicate, MenuTag... tags) {
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
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            closeMenu(playerMenu.getPlayer());
                        }
                    }.runTask(RPUniverse.getInstance());
                }
            }
        }
    }

    /**
     * Closes all menus.
     */
    public void closeAllMenus() {
        for (PlayerMenu playerMenu : playerMenuMap.values()) {
            if (playerMenu.getCurrentMenu() != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        closeMenu(playerMenu.getPlayer());
                    }
                }.runTask(RPUniverse.getInstance());
            }
        }
    }

    /**
     * Closes the menu for a given player.
     *
     * @param player the player whose menu is to be closed.
     */
    public void closeMenu(Player player) {
        Player playerInstance = player;
        if (playerMenuMap.containsKey(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    playerInstance.closeInventory();
                }
            }.runTask(RPUniverse.getInstance());
        }
    }
}
