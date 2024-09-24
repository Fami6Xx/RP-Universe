package me.fami6xx.rpuniverse.core.locks;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.commands.LocksCommand;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryHolder;

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


    HashMap<UUID, Hologram> holograms = new HashMap<>();
    HashMap<UUID, Lock> lockMap = new HashMap<>();
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
        if (playerData == null) return;
        if (playerData.getPlayerMode() != PlayerMode.ADMIN) return;
        Block block = player.getTargetBlock(8);
        if (block == null) return;

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

        for (Block checkBlock : blocksToCheck) {
            Lock lock = getLockByLocation(checkBlock.getLocation());
            if (lock == null) return;
            RPUniverse.getInstance().getLogger().info("Lock found");

            UUID playerUUID = player.getUniqueId();
            Hologram existingHologram = holograms.get(playerUUID);
            Location hologramLocation = lock.getLocation().add(player.getLocation().getDirection().normalize().multiply(-1).multiply(1.5)).add(0, 1, 0);

            if (existingHologram != null) {
                if (lock.equals(lockMap.get(playerUUID))) {
                    RPUniverse.getInstance().getLogger().info("Moving hologram");
                    DHAPI.moveHologram(existingHologram, hologramLocation);
                    return;
                } else {
                    existingHologram.delete();
                    holograms.remove(playerUUID);

                    RPUniverse.getInstance().getLogger().info("Deleting old hologram");
                }
            }

            Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), hologramLocation);
            hologram.setDefaultVisibleState(false);
            DHAPI.addHologramLine(hologram, FamiUtils.format("&8&k&lLLL"));
            DHAPI.addHologramLine(hologram, FamiUtils.format("&c&lLocked"));
            StringBuilder owners = new StringBuilder();
            if (lock.getOwners() == null || lock.getOwners().isEmpty()) {
                owners = new StringBuilder("None");
            } else if (lock.getOwners().size() > 1){
                for (int i = 0; i < lock.getOwners().size(); i++) {
                    String uuid = lock.getOwners().get(i);
                    OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    owners.append(offlineOwner.getName());
                    if (i < lock.getOwners().size() - 1) {
                        owners.append(", ");
                    }
                }
            } else {
                OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(UUID.fromString(lock.getOwners().get(0)));
                owners = new StringBuilder(Objects.requireNonNull(offlineOwner.getName()));
            }
            DHAPI.addHologramLine(hologram, FamiUtils.format("&cOwners: &7" + owners));
            String jobName = lock.getJobName() == null ? "None" : lock.getJobName();
            DHAPI.addHologramLine(hologram, FamiUtils.format("&cJob: &7" + jobName));
            int minWorkingLevel = lock.getMinWorkingLevel() == 0 ? 0 : lock.getMinWorkingLevel();
            DHAPI.addHologramLine(hologram, FamiUtils.format("&cMin Working Level: &7" + minWorkingLevel));
            DHAPI.addHologramLine(hologram, FamiUtils.format("&8&k&lLLL"));
            hologram.setShowPlayer(player);
            holograms.put(player.getUniqueId(), hologram);
            lockMap.put(player.getUniqueId(), lock);

            RPUniverse.getInstance().getLogger().info("Created hologram");
        }
    }
}
