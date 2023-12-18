package me.fami6xx.rpuniverse.core;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.datahandlers.IDataHandler;
import me.fami6xx.rpuniverse.core.misc.datahandlers.JSONDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.*;

public class DataSystem {
    private static final String HANDLER_TYPE = "JSONDataHandler";
    private final IDataHandler dataHandler;
    private final ConcurrentMap<UUID, PlayerData> playerDataMap;
    private final ConcurrentLinkedQueue<PlayerData> saveQueue;
    private BukkitTask saveTask;

    public DataSystem() {
        this.dataHandler = selectDataHandler();
        this.playerDataMap = new ConcurrentHashMap<>();
        this.saveQueue = new ConcurrentLinkedQueue<>();
        scheduleSaveTask();
    }

    public void shutdown(){
        saveTask.cancel();
        processSaveQueue();
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
            if (queuedData.getUuid().equals(uuid)) {
                data = queuedData;
            }
        }

        if(data != null){
            saveQueue.remove(data);
            playerDataMap.put(uuid, data);
            return data;
        }

        // Lastly, ask the dataHandler
        data = dataHandler.loadPlayerData(uuid.toString());
        if (data != null) {
            playerDataMap.put(uuid, data);
        }else {
            data = new PlayerData();
            playerDataMap.put(uuid, data);
        }
        return data;
    }

    /**
     * Queues the player data for saving.
     * @param data The player data to save.
     */
    public void queuePlayerDataForSaving(PlayerData data) {
        saveQueue.offer(data);
    }

    /**
     * Schedules a task to save the player data every 5 minutes.
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
     * Processes the save queue.
     */
    private void processSaveQueue() {
        while (!saveQueue.isEmpty()) {
            PlayerData data = saveQueue.poll();
            if (data != null) {
                dataHandler.savePlayerData(data);
            }
        }
    }
}
