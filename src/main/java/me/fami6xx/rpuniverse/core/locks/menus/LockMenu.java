package me.fami6xx.rpuniverse.core.locks.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LockMenu extends Menu {
    private final Menu previousMenu;
    private final Lock lock;
    public LockMenu(PlayerMenu menu, Menu previousMenu, Lock lock) {
        super(menu);
        this.previousMenu = previousMenu;
        this.lock = lock;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format("&c&lRPU &8- &cLock Menu");
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        switch (e.getSlot()) {
            case 0:
                player.closeInventory();
                FamiUtils.sendMessageWithPrefix(player, "&aType the new material in chat.");
                waitForMaterialInput(player);
                break;
            case 1:
                player.closeInventory();
                FamiUtils.sendMessageWithPrefix(player, "&aType the new owners in chat (comma-separated UUIDs).");
                waitForOwnersInput(player);
                break;
            case 2:
                player.closeInventory();
                FamiUtils.sendMessageWithPrefix(player, "&aType the new job name in chat.");
                waitForJobNameInput(player);
                break;
            case 3:
                player.closeInventory();
                FamiUtils.sendMessageWithPrefix(player, "&aType the new minimum working level in chat.");
                waitForMinWorkingLevelInput(player);
                break;
            case 7:
                RPUniverse.getInstance().getLockHandler().removeLock(lock);
                FamiUtils.sendMessageWithPrefix(player, "&cLock deleted.");
                new AllLocksMenu(playerMenu).open();
                break;
            case 8:
                previousMenu.open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(0, FamiUtils.makeItem(lock.getShownMaterial(), "&aChange Shown Material"));
        inventory.setItem(1, FamiUtils.makeItem(Material.PAPER, "&aChange Owners"));
        inventory.setItem(2, FamiUtils.makeItem(Material.NAME_TAG, "&aChange Job Name"));
        inventory.setItem(3, FamiUtils.makeItem(Material.EXPERIENCE_BOTTLE, "&aChange Min Working Level"));

        inventory.setItem(7, FamiUtils.makeItem(Material.BARRIER, "&cDelete"));
        inventory.setItem(8, FamiUtils.makeItem(Material.BARRIER, "&cBack"));
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        return tags;
    }

    private void waitForMaterialInput(Player player) {
        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                if (event.getPlayer().equals(player)) {
                    event.setCancelled(true);
                    try {
                        Material newMaterial = Material.valueOf(event.getMessage().toUpperCase());
                        lock.setShownMaterial(newMaterial);
                        FamiUtils.sendMessageWithPrefix(player, "&aShown material updated.");
                    } catch (IllegalArgumentException ex) {
                        FamiUtils.sendMessageWithPrefix(player, "&cInvalid material.");
                    }
                    HandlerList.unregisterAll(this);
                    new LockMenu(playerMenu, previousMenu, lock).open();
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, RPUniverse.getInstance());
    }

    private void waitForOwnersInput(Player player) {
        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                if (event.getPlayer().equals(player)) {
                    event.setCancelled(true);
                    List<String> newOwners = Arrays.asList(event.getMessage().split(","));
                    lock.setOwners(newOwners);
                    FamiUtils.sendMessageWithPrefix(player, "&aOwners updated.");
                    HandlerList.unregisterAll(this);
                    new LockMenu(playerMenu, previousMenu, lock).open();
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, RPUniverse.getInstance());
    }

    private void waitForJobNameInput(Player player) {
        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                if (event.getPlayer().equals(player)) {
                    event.setCancelled(true);
                    lock.setJobName(event.getMessage());
                    FamiUtils.sendMessageWithPrefix(player, "&aJob name updated.");
                    HandlerList.unregisterAll(this);
                    new LockMenu(playerMenu, previousMenu, lock).open();
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, RPUniverse.getInstance());
    }

    private void waitForMinWorkingLevelInput(Player player) {
        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                if (event.getPlayer().equals(player)) {
                    event.setCancelled(true);
                    if (FamiUtils.isInteger(event.getMessage())) {
                        int newLevel = Integer.parseInt(event.getMessage());
                        lock.setMinWorkingLevel(newLevel);
                        FamiUtils.sendMessageWithPrefix(player, "&aMinimum working level updated.");
                    } else {
                        FamiUtils.sendMessageWithPrefix(player, "&cInvalid number.");
                    }
                    HandlerList.unregisterAll(this);
                    new LockMenu(playerMenu, previousMenu, lock).open();
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, RPUniverse.getInstance());
    }
}
