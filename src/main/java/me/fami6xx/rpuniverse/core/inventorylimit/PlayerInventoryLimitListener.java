package me.fami6xx.rpuniverse.core.inventorylimit;

import me.fami6xx.rpuniverse.core.menuapi.types.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerInventoryLimitListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        for (int i = 9; i < 36; i++) {
            event.getPlayer().getInventory().setItem(i, PaginatedMenu.BORDER_GLASS);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() >= 9 && event.getSlot() < 36) {
            event.setCancelled(true);
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(PaginatedMenu.BORDER_GLASS)) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        event.getRawSlots().stream().filter(slot -> slot >= 9 && slot < 36).forEach(slot -> event.setCancelled(true));
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
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(item -> item != null && item.getType() == Material.BARRIER);
    }
}
