package me.fami6xx.rpuniverse.core.locks;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.commands.LocksCommand;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * The lock handler class
 * 
 * @author Fami6xx
 * @version 1.0
 */
public class LockHandler implements Listener {
    private final List<Lock> locks = new ArrayList<>();

    /**
     * Constructor, which loads all locks when the plugin is initialized.
     */
    public LockHandler() {
        loadAllLocks();
        RPUniverse.getInstance().getCommand("locks").setExecutor(new LocksCommand());
        RPUniverse.getInstance().getServer().getPluginManager().registerEvents(this, RPUniverse.getInstance());
    }

    /**
     * Save all locks to the data system when the plugin is shut down.
     */
    public void shutdown() {
        locks.forEach(lock -> RPUniverse.getInstance().getDataSystem().getDataHandler().saveLockData(lock));
    }

    /**
     * Load all locks from the data system to the internal list.
     */
    private void loadAllLocks() {
        Lock[] loadedLocks = RPUniverse.getInstance().getDataSystem().getDataHandler().getAllLockData();

        for (Lock lock : loadedLocks) {
            if (lock == null) continue;
            this.locks.add(lock);
        }
    }

    /**
     * Create a lock at the specified location with the specified owners, job name, minimum working level, and shown material.
     * 
     * @param location The location for the lock.
     * @param shownMaterial The material to show for the lock.
     * @param owners The owners of the lock.
     * @param jobName The name of the job required to open the lock.
     * @param minWorkingLevel The minimum working level required to open the lock.
     */
    public void createLock(Location location, Material shownMaterial, List<String> owners, String jobName, int minWorkingLevel) {
        locks.add(new Lock(location, owners, jobName, minWorkingLevel, shownMaterial));
    }

    /**
     * Returns the lock located at the specified location.
     * 
     * @param location The location for which to find the lock.
     * @return The lock at the given location or null if no lock exists.
     */
    public Lock getLockByLocation(Location location) {
        for (Lock lock : locks) {
            if (lock.getLocation().equals(location)) {
                return lock;
            }
        }
        return null;
    }

    /**
     * Returns a list of locks owned by the specified owner.
     * 
     * @param owner The name of the owner for whom to find locks.
     * @return List of locks belonging to the specified owner.
     */
    public List<Lock> getLocksByOwner(String owner) {
        List<Lock> result = new ArrayList<>();
        for (Lock lock : locks) {
            if (lock.getOwners() != null && !lock.getOwners().isEmpty() ? lock.getOwners().contains(owner) : false) {
                result.add(lock);
            }
        }
        return result;
    }

    /**
     * Returns a list of locks associated with a specific job.
     * 
     * @param jobName The name of the job for which to find locks.
     * @return List of locks associated with the specified job.
     */
    public List<Lock> getLocksByJob(String jobName) {
        List<Lock> result = new ArrayList<>();
        for (Lock lock : locks) {
            if (lock.getJobName() != null && lock.getJobName().equals(jobName)) {
                result.add(lock);
            }
        }
        return result;
    }

    /**
     * Returns a list of all locks managed by this handler.
     * 
     * @return List of all locks.
     */
    public List<Lock> getAllLocks() {
        return new ArrayList<>(locks);
    }

    /**
     * Handles the player interact event.
     * 
     * @param event The event to handle.
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) return;

            Player player = event.getPlayer();
            PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());

            Material type = block.getType();
            List<Block> blocksToCheck = new ArrayList<>();

            if(type == Material.AIR) return;

            if (type.toString().contains("CHEST")) {
                Chest chest = (Chest) block.getState();
                InventoryHolder holder = chest.getInventory().getHolder();
                if (holder instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest) holder;
                    blocksToCheck.add(((Chest) doubleChest.getLeftSide()).getBlock());
                    blocksToCheck.add(((Chest) doubleChest.getRightSide()).getBlock());
                } else {
                    blocksToCheck.add(block);
                }
            }

            else if (type.toString().contains("TRAPDOOR")) {
                blocksToCheck.add(block);
            }

            else if (type.toString().contains("DOOR")) {
                blocksToCheck.add(block);

                BlockFace facing = (block.getData() & 0x8) == 0x8 ? BlockFace.DOWN : BlockFace.UP;
                blocksToCheck.add(block.getRelative(facing));
            }
            else {
                blocksToCheck.add(block);
            }

            for (Block checkBlock : blocksToCheck) {
                Lock lock = getLockByLocation(checkBlock.getLocation());
                if (lock != null && !playerData.canOpenLock(lock)) {
                    event.setCancelled(true);
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().cannotOpenLockMessage);
                    return;
                }
            }
        }
    }
}
