package me.fami6xx.rpuniverse.core.menuapi.handlers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuInvClickHandler implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {
                return;
            }

            if(e.getClickedInventory() == null || e.getView().getTopInventory() != e.getClickedInventory()){
                return;
            }

            Menu menu = (Menu) holder;
            menu.handleMenu(e);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu) {
            RPUniverse.getInstance().getMenuManager().getPlayerMenu((Player) e.getPlayer()).setCurrentMenu(null);
            RPUniverse.getInstance().getMenuManager().getPlayerMenu((Player) e.getPlayer()).setEditingJob(null);
        }
    }

    @EventHandler
    public void onMenuOpen(InventoryOpenEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu) {
            RPUniverse.getInstance().getMenuManager().getPlayerMenu((Player) e.getPlayer()).setCurrentMenu((Menu) holder);
        }
    }
}
