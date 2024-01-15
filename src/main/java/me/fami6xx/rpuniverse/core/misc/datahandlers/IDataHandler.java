package me.fami6xx.rpuniverse.core.misc.datahandlers;

import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;

public interface IDataHandler {

    /**
     * Called when the plugin is enabled and the data handler is selected.
     * @return true if the data handler started up successfully, false otherwise.
     */
    boolean startUp();

    /**
     * Called when the plugin is disabled and the data handler is selected.
     * @return true if the data handler shut down successfully, false otherwise.
     */
    boolean shutDown();

    /**
     * Called when the data handlers name is needed.
     * @return The name of the data handler.
     */
    String getHandlerName();

    /**
     * Called when the user data needs to be loaded.
     * @param uuid The UUID of the player to load data for.
     * @return The player data for the specified UUID.
     */
    PlayerData loadPlayerData(String uuid);

    /**
     * Called when the user data needs to be saved.
     * @param data The player data to save.
     * @return true if the data was saved successfully, false otherwise.
     */
    boolean savePlayerData(PlayerData data);

    /**
     * Retrieves job data for a given name. The data is serialized and deserialized
     * by the selected Data Handler.
     *
     * @param name The name of the job to retrieve data for.
     * @return The job data for the specified name.
     */
    Job getJobData(String name);

    /**
     * Saves the job data for a given name. The data is serialized and deserialized by the selected Data Handler.
     *
     * @param name The name of the job to save data for.
     * @param data The job data to save.
     * @return true if the data was saved successfully, false otherwise.
     */
    boolean saveJobData(String name, String data);

    /**
     * Called at the start of the plugin, this is used to determine how often the save task should run.
     * @return The time in ticks between each save task run.
     */
    int getQueueSaveTime();
}
