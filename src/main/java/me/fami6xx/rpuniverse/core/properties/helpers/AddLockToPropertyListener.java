package me.fami6xx.rpuniverse.core.properties.helpers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.locks.LockHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import me.fami6xx.rpuniverse.core.properties.menus.PropertyAllLocksMenu;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class AddLockToPropertyListener implements Listener {
    private final Player player;
    private final Property property;
    private final PropertyAllLocksMenu previousMenu;

    public AddLockToPropertyListener(Player player, Property property, PropertyAllLocksMenu menu) {
        this.player = player;
        this.property = property;
        this.previousMenu = menu;
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        FamiUtils.sendMessageWithPrefix(player, "&cPlease click on a block you want to add as a lock.");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().equals(player)) return;

        event.setCancelled(true);
        Block block = event.getClickedBlock();
        if (block == null) {
            FamiUtils.sendMessageWithPrefix(player, "&cYou must click on a block.");
            previousMenu.open();
            HandlerList.unregisterAll(this);
            return;
        }

        LockHandler lockHandler = RPUniverse.getInstance().getLockHandler();
        List<Block> blocksToCheck = new ArrayList<>();
        LockHandler.getAllLockBlocksFromBlock(block, block.getType(), blocksToCheck);
        for (Block b : blocksToCheck) {
            if(lockHandler.getLockByLocation(b.getLocation()) != null) {
                FamiUtils.sendMessageWithPrefix(player, "&cBlock is already locked");
                previousMenu.open();
                HandlerList.unregisterAll(this);
                return;
            }else if(!(b.getType().isBlock() && b.getType().isInteractable())) {
                FamiUtils.sendMessageWithPrefix(player, "&cBlock is not lockable");
                previousMenu.open();
                HandlerList.unregisterAll(this);
                return;
            }
        }

        Lock lock = lockHandler.createLock(block.getLocation(), block.getType(), new ArrayList<>(), null, 0);
        property.addLock(lock);
        FamiUtils.sendMessageWithPrefix(player, "&aLock added.");
        previousMenu.open();
        HandlerList.unregisterAll(this);
    }
}
