package me.fami6xx.rpuniverse.core;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.datahandlers.IDataHandler;
import me.fami6xx.rpuniverse.core.misc.datahandlers.JSONDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class DataSystem implements Listener {
    private static final String HANDLER_TYPE = "JSONDataHandler";
    private final IDataHandler dataHandler;
    private final ConcurrentMap<UUID, PlayerData> playerDataMap;
    private final ConcurrentLinkedQueue<PlayerData> saveQueue;
    private BukkitTask saveTask;

    public DataSystem() {
        this.dataHandler = selectDataHandler();
        this.playerDataMap = new ConcurrentHashMap<>();
        this.saveQueue = new ConcurrentLinkedQueue<>();
        this.dataHandler.startUp();
        scheduleSaveTask();
        Bukkit.getPluginManager().registerEvents(this, RPUniverse.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        this.getPlayerData(event.getPlayer().getUniqueId()).updatePlayer(event.getPlayer());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        PlayerData data = getPlayerData(event.getPlayer().getUniqueId());
        data.setCurrentTagHologram(null);
        this.queuePlayerDataForSaving(data);
    }

    public void shutdown(){
        saveTask.cancel();
        playerDataMap.forEach((uuid,data) -> queuePlayerDataForSaving(data));
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
        Logger log = RPUniverse.getInstance().getLogger();
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
            return data;
        }

        // Lastly, ask the dataHandler
        data = dataHandler.loadPlayerData(uuid.toString());
        if (data != null) {
            data.loadAfterSave();
            playerDataMap.put(uuid, data);
        }else {
            if(RPUniverse.getInstance().getServer().getPlayer(uuid) == null){
                OfflinePlayer player = RPUniverse.getInstance().getServer().getOfflinePlayer(uuid);
                data = new PlayerData(player);
                playerDataMap.put(uuid, data);
            }

            data = new PlayerData(RPUniverse.getInstance().getServer().getPlayer(uuid));
            playerDataMap.put(uuid, data);
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
                data.prepareForSave();
                dataHandler.savePlayerData(data);
            }
        }
    }
}
