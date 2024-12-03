package me.fami6xx.rpuniverse.core.inventorylimit;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerInventoryLimitListener implements Listener {

    public PlayerInventoryLimitListener() {
        Bukkit.getScheduler().runTaskTimer(RPUniverse.getJavaPlugin(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            for (int i = 0; i < 36; i++) {
                if (i < 9) {
                    if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).isSimilar(PaginatedMenu.BORDER_GLASS)) {
                        player.getInventory().setItem(i, null);
                    }
                }
                if (i >= 9) {
                    player.getInventory().setItem(i, PaginatedMenu.BORDER_GLASS);
                }
            }
        }), 0, 5);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (int i = 9; i < 36; i++) {
            event.getPlayer().getInventory().setItem(i, PaginatedMenu.BORDER_GLASS);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getSlot() >= 9 && event.getSlot() < 36 && event.getClickedInventory() == event.getWhoClicked().getInventory()) {
            event.setCancelled(true);
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(PaginatedMenu.BORDER_GLASS)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory() != event.getWhoClicked().getInventory()) return;
        if (event.getRawSlots().stream().anyMatch(slot -> slot >= 9 && slot < 36)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryPickupItemEvent(InventoryPickupItemEvent event) {
        if (event.getItem().getItemStack().isSimilar(PaginatedMenu.BORDER_GLASS)) {
            event.getItem().setItemStack(null);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        if (event.getItem().isSimilar(PaginatedMenu.BORDER_GLASS)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(PaginatedMenu.BORDER_GLASS)) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerSwapHand(PlayerSwapHandItemsEvent event) {
        if ((event.getOffHandItem() != null && event.getOffHandItem().isSimilar(PaginatedMenu.BORDER_GLASS)) || (event.getMainHandItem() != null && event.getMainHandItem().isSimilar(PaginatedMenu.BORDER_GLASS))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(item -> item != null && item.isSimilar(PaginatedMenu.BORDER_GLASS));
    }
}
