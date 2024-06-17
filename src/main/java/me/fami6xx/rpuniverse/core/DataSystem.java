package me.fami6xx.rpuniverse.core;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.datahandlers.IDataHandler;
import me.fami6xx.rpuniverse.core.misc.datahandlers.JSONDataHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * The DataSystem is the main class for handling the data.
 * It is responsible for loading the player data, saving the player data, and handling the data handler.
 * <p>
 * The DataSystem is a singleton class, so only one instance of it should be created.
 * <p>
 * The DataSystem is also a listener for the PlayerJoinEvent and PlayerQuitEvent.
 * This means that the DataSystem will also listen for these events and update the player data accordingly.
 */
public class DataSystem implements Listener {
    private static final String HANDLER_TYPE = "JSONDataHandler";
    private static final long DATA_EXPIRATION_TIME = 300000; // 300 seconds in milliseconds
    private final IDataHandler dataHandler;
    private final ConcurrentMap<UUID, PlayerData> playerDataMap;
    private final ConcurrentLinkedQueue<PlayerData> saveQueue;
    private final ConcurrentMap<UUID, Long> lastAccessTime;
    private BukkitTask saveTask;
    private BukkitTask completeSaveTask;
    private BukkitTask expirationTask;

    /**
     * Constructor for the DataSystem.
     * Initializes the data handler, player data map, save queue, and last access time map.
     * Also starts the data handler and schedules the save tasks and expiration task.
     */
    public DataSystem() {
        this.dataHandler = selectDataHandler();
        this.playerDataMap = new ConcurrentHashMap<>();
        this.saveQueue = new ConcurrentLinkedQueue<>();
        this.lastAccessTime = new ConcurrentHashMap<>();
        this.dataHandler.startUp();
        scheduleSaveTask();
        scheduleCompleteSaveTask();
        scheduleExpirationTask();
        Bukkit.getPluginManager().registerEvents(this, RPUniverse.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        this.getPlayerData(event.getPlayer().getUniqueId()).updatePlayer(event.getPlayer());
        this.lastAccessTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        PlayerData data = getPlayerData(event.getPlayer().getUniqueId());
        data.setCurrentTagHologram(null);
        this.queuePlayerDataForSaving(data);
        this.lastAccessTime.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Shuts down the data system.
     * Cancels the save tasks, saves all player data, and shuts down the data handler.
     */
    public void shutdown(){
        saveTask.cancel();
        completeSaveTask.cancel();
        expirationTask.cancel();
        playerDataMap.forEach((uuid,data) -> queuePlayerDataForSaving(data));
        processSaveQueue();
        RPUniverse.getInstance().getJobsHandler().getJobs().forEach(job -> {
            job.prepareForSave();
            dataHandler.saveJobData(job.getName(), job);
        });
        dataHandler.saveConsumables(RPUniverse.getInstance().getBasicNeedsHandler());
        RPUniverse.getInstance().getLockHandler().getAllLocks().forEach(lock -> {
            dataHandler.saveLockData(lock);
        });
        RPUniverse.getInstance().getLogger().info("Saved all data");
        dataHandler.shutDown();
    }

    private IDataHandler selectDataHandler() {
        // This will be much more complex but for now this is fine.
        switch (HANDLER_TYPE) {
            case "JSONDataHandler":
                return new JSONDataHandler();
            default:
                throw new IllegalArgumentException("Unknown data handler type: " + HANDLER_TYPE);
        }
    }

    /**
     * Gets the data handler.
     * @return The data handler.
     */
    public IDataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Gets the player data for the specified UUID.
     * @param uuid The UUID of the player to get the data for.
     * @return The player data for the specified UUID. Null if not found.
     */
    public PlayerData getPlayerData(UUID uuid) {
        // Firstly, check the playerDataMap if the user has already been loaded.
        PlayerData data = playerDataMap.get(uuid);
        if (data != null) {
            return data;
        }

        // Next, check the saveQueue if the player hasn't been saved yet. (This acts as a cache)
        for (PlayerData queuedData : saveQueue) {
            if(queuedData.getBindedPlayer() != null)
                if (queuedData.getBindedPlayer().getUniqueId().equals(uuid))
                    data = queuedData;
            else if(queuedData.getBindedOfflinePlayer() != null)
                if (queuedData.getBindedOfflinePlayer().getUniqueId().equals(uuid))
                    data = queuedData;
        }

        if(data != null){
            saveQueue.remove(data);
            playerDataMap.put(uuid, data);
            this.lastAccessTime.put(uuid, System.currentTimeMillis());
            return data;
        }

        // Lastly, ask the dataHandler
        data = dataHandler.loadPlayerData(uuid.toString());
        if (data != null) {
            data.loadAfterSave();
            playerDataMap.put(uuid, data);
            this.lastAccessTime.put(uuid, System.currentTimeMillis());
        }else {
            if(RPUniverse.getInstance().getServer().getPlayer(uuid) == null){
                OfflinePlayer player = RPUniverse.getInstance().getServer().getOfflinePlayer(uuid);
                data = new PlayerData(player);
                playerDataMap.put(uuid, data);
                this.lastAccessTime.put(uuid, System.currentTimeMillis());
            }

            data = new PlayerData(RPUniverse.getInstance().getServer().getPlayer(uuid));
            playerDataMap.put(uuid, data);
            this.lastAccessTime.put(uuid, System.currentTimeMillis());
        }
        return data;
    }

    /**
     * Retrieves the player data for the specified UUID.
     *
     * @param UUID The UUID of the player to get the data for.
     * @return The player data for the specified UUID. Null if not found.
     */
    public PlayerData getPlayerData(String UUID){
        return getPlayerData(java.util.UUID.fromString(UUID));
    }

    /**
     * Queues the player data for saving.
     * @param data The player data to save.
     */
    public void queuePlayerDataForSaving(PlayerData data) {
        playerDataMap.remove(data.getPlayerUUID());
        lastAccessTime.remove(data.getPlayerUUID());
        saveQueue.offer(data);
    }

    /**
     * Schedules a task to save the player data every given time.
     * <p>
     * This is done to prevent the server from lagging when saving the player data and also acts as a cache.
     */
    private void scheduleSaveTask() {
        saveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                RPUniverse.getInstance(),
                this::processSaveQueue,
                0L,
                dataHandler.getQueueSaveTime()
        );
    }

    /**
     * Schedules a task to save all data at a specified interval.
     */
    private void scheduleCompleteSaveTask() {
        completeSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                RPUniverse.getInstance(),
                this::saveAllData,
                0L,
                this.getCompleteSaveTime()
        );
    }

    /**
     * Schedules a task to expire old player data.
     */
    private void scheduleExpirationTask() {
        expirationTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                RPUniverse.getInstance(),
                this::expireOldPlayerData,
                0L,
                6000L // Check every 5 minutes
        );
    }

    /**
     * Expires old player data.
     */
    private void expireOldPlayerData() {
        long currentTime = System.currentTimeMillis();
        for (UUID uuid : lastAccessTime.keySet()) {
            if (currentTime - lastAccessTime.get(uuid) > DATA_EXPIRATION_TIME) {
                PlayerData data = playerDataMap.remove(uuid);
                if (data != null) {
                    saveQueue.offer(data);
                }
                lastAccessTime.remove(uuid);
            }
        }
    }

    /**
     * Gets the complete save time from the configuration.
     * @return The complete save time.
     */
    private int getCompleteSaveTime(){
        int time = 0;
        try{
            time = RPUniverse.getInstance().getConfiguration().getInt("data.completeSaveInterval");
        }catch (Exception exc){
            HashMap<String, String> replace = new HashMap<>();
            replace.put("{value}", "data.completeSaveInterval");
            RPUniverse.getInstance().getLogger().severe(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace));
            return 600;
        }
        return time;
    }

    /**
     * Saves all data.
     */
    public void saveAllData(){
        playerDataMap.forEach((uuid,data) -> {
            data.prepareForSave();
            dataHandler.savePlayerData(data);
        });
        processSaveQueue();
        dataHandler.saveConsumables(RPUniverse.getInstance().getBasicNeedsHandler());
        RPUniverse.getInstance().getJobsHandler().getJobs().forEach(job -> {
            job.prepareForSave();
            dataHandler.saveJobData(job.getName(), job);
        });
        RPUniverse.getInstance().getLockHandler().getAllLocks().forEach(lock -> {
            dataHandler.saveLockData(lock);
        });
    }

    /**
     * Processes the save queue.
     */
    private void processSaveQueue() {
        while (!saveQueue.isEmpty()) {
            PlayerData data = saveQueue.poll();
            if (data != null) {
                data.prepareForSave();
                dataHandler.savePlayerData(data);
            }
        }
    }
}
