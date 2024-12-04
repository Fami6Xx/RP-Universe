package me.fami6xx.rpuniverse.core.chestlimit;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.LockOpenedEvent;
import me.fami6xx.rpuniverse.core.locks.LockHandler;
import me.fami6xx.rpuniverse.core.misc.persistentdatatypes.ItemStackArrayDataType;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ChestLimitListener implements Listener {
    private final Plugin plugin;
    private final Map<Block, Player> chestLocks = new HashMap<>();
    private final NamespacedKey key;

    public ChestLimitListener(Plugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "chest_items");
    }

    @EventHandler
    public void LockOpenedEvent(LockOpenedEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getLock().getLocation().isChunkLoaded()) throw new RuntimeException("Chunk is not loaded but an event of opening a lock was called");
        Block block = event.getLock().getLocation().getBlock();
        if (block.getState() instanceof Chest) {
            if (chestLocks.containsKey(block)) {
                FamiUtils.sendMessageWithPrefix(event.getPlayer(), RPUniverse.getLanguageHandler().chestLimitChestAlreadyInUse);
                event.setCancelled(true);
                return;
            }

            if (event.getPlayer().isSneaking()) return;

            chestLocks.put(block, event.getPlayer());

            Chest chest = (Chest) block.getState();

            int size = RPUniverse.getInstance().getConfiguration().getInt("chestLimit.single-chest-rows") * 9;
            if (chest.getInventory().getHolder() instanceof DoubleChest) {
                size = RPUniverse.getInstance().getConfiguration().getInt("chestLimit.double-chest-rows") * 9;
            }

            Inventory customInventory = Bukkit.createInventory(event.getPlayer(), size, FamiUtils.format(RPUniverse.getLanguageHandler().chestLimitMenuName));

            PersistentDataContainer dataContainer = ((TileState) chest).getPersistentDataContainer();
            ItemStack[] items = dataContainer.get(key, new ItemStackArrayDataType());
            if (items != null) {
                customInventory.setContents(items);
            }

            event.setCancelled(true);
            event.getPlayer().openInventory(customInventory);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getClickedBlock().getLocation().isChunkLoaded()) throw new RuntimeException("Chunk is not loaded but an event of opening a lock was called");
        Block block = event.getClickedBlock();
        if (block.getState() instanceof Chest) {
            if (chestLocks.containsKey(block)) {
                FamiUtils.sendMessageWithPrefix(event.getPlayer(), RPUniverse.getLanguageHandler().chestLimitChestAlreadyInUse);
                event.setCancelled(true);
                return;
            }

            if (event.getPlayer().isSneaking()) return;
            if (LockHandler.checkBlockForAnyLocks(block)) return;

            chestLocks.put(block, event.getPlayer());

            Chest chest = (Chest) block.getState();

            int size = RPUniverse.getInstance().getConfiguration().getInt("chestLimit.single-chest-rows") * 9;
            if (chest.getInventory().getHolder() instanceof DoubleChest) {
                size = RPUniverse.getInstance().getConfiguration().getInt("chestLimit.double-chest-rows") * 9;
                chest = (Chest) ((DoubleChest) chest.getInventory().getHolder()).getLeftSide();
            }

            Inventory customInventory = Bukkit.createInventory(event.getPlayer(), size, FamiUtils.format(RPUniverse.getLanguageHandler().chestLimitMenuName));

            PersistentDataContainer dataContainer = ((TileState) chest).getPersistentDataContainer();
            ItemStack[] items = dataContainer.get(key, new ItemStackArrayDataType());
            if (items != null) {
                customInventory.setContents(items);
            }

            event.setCancelled(true);
            event.getPlayer().openInventory(customInventory);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(FamiUtils.format(RPUniverse.getLanguageHandler().chestLimitMenuName))) return;

        Player player = (Player) event.getPlayer();
        Block block = null;

        for (Map.Entry<Block, Player> entry : chestLocks.entrySet()) {
            if (entry.getValue().equals(player)) {
                block = entry.getKey();
                break;
            }
        }

        if (block != null && block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();

            if (chest.getInventory().getHolder() instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
                chest = (Chest) doubleChest.getLeftSide();
            }

            PersistentDataContainer dataContainer = ((TileState) chest).getPersistentDataContainer();
            dataContainer.set(key, new ItemStackArrayDataType(), event.getInventory().getContents());

            ((TileState) chest).update();

            chestLocks.remove(block);
        }
    }
}
