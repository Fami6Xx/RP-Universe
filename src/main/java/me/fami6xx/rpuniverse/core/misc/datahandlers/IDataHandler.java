package me.fami6xx.rpuniverse.core.misc.datahandlers;

import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.basicneeds.ConsumableItem;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

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
    boolean saveJobData(String name, Job data);

    /**
     * Saves the consumables for the BasicNeedsHandler. The data is serialized and deserialized by the selected Data Handler.
     *
     * @param handler The BasicNeedsHandler to save consumables for.
     * @return true if the data was saved successfully, false otherwise.
     */
    boolean saveConsumables(BasicNeedsHandler handler);

    /**
     * Loads the consumables for the BasicNeedsHandler. The data is serialized and deserialized by the selected Data Handler.
     *
     * @return A HashMap containing all consumables.
     */
    HashMap<ItemStack, ConsumableItem> loadConsumables();

    /**
     * Renames the job data from the old name to the new name. The data is serialized and deserialized by the selected Data Handler.
     *
     * @param oldName The old name of the job.
     * @param newName The new name of the job.
     * @return true if the data was renamed successfully, false otherwise.
     */
    boolean renameJobData(String oldName, String newName);

    /**
     * Retrieves all job data stored by the selected Data Handler.
     *
     * @return An array containing all job data.
     */
    Job[] getAllJobData();

    /**
     * Removes the job data with the specified name.
     *
     * @param name The name of the job to remove data for.
     * @return true if the data was removed successfully, false otherwise.
     */
    boolean removeJobData(String name);

    /**
     * Called at the start of the plugin, this is used to determine how often the save task should run.
     * @return The time in ticks between each save task run.
     */
    int getQueueSaveTime();

    /**
     * Saves lock
     * @return Status if the lock has been saved.
     */
    boolean saveLockData(Lock lock);

    /**
     * Gets all the saved lock data.
     * @return An array of all the saved lock data.
     */
    Lock[] getAllLockData();
}
