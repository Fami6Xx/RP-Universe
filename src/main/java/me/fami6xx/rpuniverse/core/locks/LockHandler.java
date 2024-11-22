package me.fami6xx.rpuniverse.core.locks;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.LockCreatedEvent;
import me.fami6xx.rpuniverse.core.api.LockDeletedEvent;
import me.fami6xx.rpuniverse.core.locks.commands.LocksCommand;
import me.fami6xx.rpuniverse.core.locks.menus.AllLocksMenu;
import me.fami6xx.rpuniverse.core.locks.menus.LockMenu;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.*;

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
     *
     * @return The created lock. Null if a lock already exists at the specified location.
     */
    public synchronized Lock createLock(Location location, Material shownMaterial, List<String> owners, String jobName, int minWorkingLevel) {
        // Check if a lock already exists at the specified location
        if (getLockByLocation(location) != null) {
            RPUniverse.getInstance().getLogger().warning("A lock already exists at this location!");
            return null;
        }

        // If the block is a chest, check both sides of the chest
        if (location.getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) location.getBlock().getState();
            InventoryHolder holder = chest.getInventory().getHolder();
            if (holder instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) holder;
                if (getLockByLocation(((Chest) doubleChest.getLeftSide()).getBlock().getLocation()) != null ||
                        getLockByLocation(((Chest) doubleChest.getRightSide()).getBlock().getLocation()) != null) {
                    RPUniverse.getInstance().getLogger().warning("A lock already exists on one side of this double chest!");
                    return null;
                }
            }
        }

        // If the block is a door, check both sides of the door
        if (location.getBlock().getType().toString().contains("DOOR") && location.getBlock().getBlockData() instanceof Door && !location.getBlock().getType().toString().contains("TRAP")) {
            Door door = (Door) location.getBlock().getBlockData();

            if (door.getHalf() == Bisected.Half.TOP) {
                if (getLockByLocation(location.getBlock().getRelative(BlockFace.DOWN).getLocation()) != null) {
                    RPUniverse.getInstance().getLogger().warning("A lock already exists on the bottom side of this door!");
                    return null;
                }
            } else {
                if (getLockByLocation(location.getBlock().getRelative(BlockFace.UP).getLocation()) != null) {
                    RPUniverse.getInstance().getLogger().warning("A lock already exists on the top side of this door!");
                    return null;
                }
            }
        }

        Lock createdLock = new Lock(location, owners, jobName, minWorkingLevel, shownMaterial);
        new LockCreatedEvent(createdLock).callEvent();
        locks.add(createdLock);
        return createdLock;
    }

    /**
     * Returns the lock located at the specified location.
     * 
     * @param location The location for which to find the lock.
     * @return The lock at the given location or null if no lock exists.
     */
    public @Nullable Lock getLockByLocation(Location location) {
        for (Lock lock : locks) {
            if (lock.getLocation().getBlockX() == location.getBlockX() &&
                    lock.getLocation().getBlockY() == location.getBlockY() &&
                    lock.getLocation().getBlockZ() == location.getBlockZ() &&
                    lock.getLocation().getWorld().equals(location.getWorld())) {
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
            if (lock.getOwners() != null && !lock.getOwners().isEmpty() && lock.getOwners().contains(owner)) {
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
     * Returns the lock with the specified UUID.
     *
     * @param uuid The UUID of the lock to find.
     * @return The lock with the specified UUID or null if no lock exists.
     */
    public Lock getLockByUUID(UUID uuid) {
        for (Lock lock : locks) {
            if (lock.getUUID().toString().equals(uuid.toString())) {
                return lock;
            }
        }
        return null;
    }

    /**
     * Returns a list of all locks managed by this handler.
     * This list is a copy of the internal list and can be modified without affecting the internal list.
     * 
     * @return List of all locks.
     */
    public List<Lock> getAllLocks() {
        return new ArrayList<>(locks);
    }

    /**
     * Removes the specified lock from the list of locks.
     * <p>
     * This method also triggers a {@link LockDeletedEvent} which can be cancelled to prevent the lock from being removed.
     *
     * @param lock The lock to remove.
     */
    public synchronized void removeLock(Lock lock) {
        LockDeletedEvent event = new LockDeletedEvent(lock);
        RPUniverse.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        locks.remove(lock);
        RPUniverse.getInstance().getDataSystem().getDataHandler().removeLockData(lock);
    }

    /**
     * Handles the player interact event.
     * 
     * @param event The event to handle.
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) return;

            Player player = event.getPlayer();
            PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());

            Material type = block.getType();
            List<Block> blocksToCheck = new ArrayList<>();

            if (type == Material.AIR) return;

            getAllLockBlocksFromBlock(block, type, blocksToCheck);

            for (Block checkBlock : blocksToCheck) {
                Lock lock = getLockByLocation(checkBlock.getLocation());
                if (lock != null) {
                    if (player.isSneaking()) {
                        if(playerData.getPlayerMode() == PlayerMode.ADMIN) {
                            new LockMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), new AllLocksMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player)), lock).open();
                            event.setCancelled(true);
                            return;
                        }else if(!playerData.canOpenLock(lock)) {
                            event.setCancelled(true);
                            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().cannotOpenLockMessage);
                            return;
                        }
                    } else if (!playerData.canOpenLock(lock)) {
                        event.setCancelled(true);
                        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().cannotOpenLockMessage);
                        return;
                    }
                }
            }
        }
    }


    HashMap<UUID, Hologram> holograms = new HashMap<>();
    HashMap<UUID, Lock> lockMap = new HashMap<>();
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
        if (playerData == null) return;
        if (playerData.getPlayerMode() != PlayerMode.ADMIN) {
            checkHoloAndDelete(player.getUniqueId());
            return;
        }
        Block block = player.getTargetBlock(8);
        if (block == null) {
            checkHoloAndDelete(player.getUniqueId());
            return;
        }

        Material type = block.getType();
        List<Block> blocksToCheck = new ArrayList<>();

        if(type == Material.AIR) {
            checkHoloAndDelete(player.getUniqueId());
            return;
        }

        getAllLockBlocksFromBlock(block, type, blocksToCheck);

        boolean found = false;

        for (Block checkBlock : blocksToCheck) {
            Lock lock = getLockByLocation(checkBlock.getLocation());
            if (lock == null) continue;
            found = true;

            UUID playerUUID = player.getUniqueId();
            Hologram existingHologram = holograms.get(playerUUID);
            Vector direction = player.getLocation().getDirection().normalize();
            direction.setY(0);

            Location hologramLocation = lock.getLocation().add(direction.multiply(-1).multiply(1.5));
            hologramLocation.setY(event.getPlayer().getEyeLocation().getY() + 0.5);

            if (existingHologram != null) {
                if (lock.equals(lockMap.get(playerUUID))) {
                    DHAPI.moveHologram(existingHologram, hologramLocation);
                    continue;
                } else {
                    existingHologram.delete();
                    holograms.remove(playerUUID);
                }
            }

            Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), hologramLocation);
            hologram.setDefaultVisibleState(false);
            DHAPI.addHologramLine(hologram, FamiUtils.format("&8&k&lLLL"));
            DHAPI.addHologramLine(hologram, FamiUtils.format("&c&lLocked"));
            DHAPI.addHologramLine(hologram, FamiUtils.format("&cOwners: &7" + lock.getOwnersAsString()));
            String jobName = lock.getJobName() == null ? "None" : lock.getJobName();
            DHAPI.addHologramLine(hologram, FamiUtils.format("&cJob: &7" + jobName));
            int minWorkingLevel = lock.getMinWorkingLevel() == 0 ? 0 : lock.getMinWorkingLevel();
            DHAPI.addHologramLine(hologram, FamiUtils.format("&cMin Working Level: &7" + minWorkingLevel));
            DHAPI.addHologramLine(hologram, FamiUtils.format("&8&k&lLLL"));
            hologram.setShowPlayer(player);
            holograms.put(player.getUniqueId(), hologram);
            lockMap.put(player.getUniqueId(), lock);
        }

        if (!found) {
            checkHoloAndDelete(player.getUniqueId());
        }
    }

    public static void getAllLockBlocksFromBlock(Block block, Material type, List<Block> blocksToCheck) {
        if (type.toString().contains("CHEST")) {
            if(!type.toString().contains("ENDER")) {
                Chest chest = (Chest) block.getState();
                InventoryHolder holder = chest.getInventory().getHolder();
                if (holder instanceof DoubleChest) {
                    DoubleChest doubleChest = (DoubleChest) holder;
                    blocksToCheck.add(((Chest) doubleChest.getLeftSide()).getBlock());
                    blocksToCheck.add(((Chest) doubleChest.getRightSide()).getBlock());
                } else {
                    blocksToCheck.add(block);
                }
            }else{
                blocksToCheck.add(block);
            }
        }

        else if (type.toString().contains("DOOR") && block.getBlockData() instanceof Door && !block.getType().toString().contains("TRAP")) {
            blocksToCheck.add(block);

            Door door = (Door) block.getBlockData();

            if (door.getHalf() == Bisected.Half.TOP) {
                blocksToCheck.add(block.getRelative(BlockFace.DOWN));
            } else {
                blocksToCheck.add(block.getRelative(BlockFace.UP));
            }
        }
        else {
            blocksToCheck.add(block);
        }
    }

    private void checkHoloAndDelete(UUID playerUUID) {
        if(holograms.containsKey(playerUUID)) {
            holograms.get(playerUUID).delete();
            holograms.remove(playerUUID);
            lockMap.remove(playerUUID);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        List<Block> blocksToCheck = new ArrayList<>();
        getAllLockBlocksFromBlock(block, block.getType(), blocksToCheck);

        boolean locked = false;
        for (Block checkBlock : blocksToCheck) {
            Lock lock = getLockByLocation(checkBlock.getLocation());
            if (lock != null) {
                locked = true;
            }
        }

        if (locked) {
            event.setCancelled(true);
            FamiUtils.sendMessageWithPrefix(player, "&cYou need to remove the lock first before breaking this block.");
        }
    }
}
