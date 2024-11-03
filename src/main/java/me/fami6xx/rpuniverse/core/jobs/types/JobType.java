package me.fami6xx.rpuniverse.core.jobs.types;

import com.google.gson.JsonObject;
import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Represents a type of job that can be assigned to a job.
 * <p>
 *  For all data saving and loading use {@link JobTypeData} class. Any type of data that is not in this class will not be saved after a server restart.
 *  <p>
 *   If you don't want to save some data, just use the {@code transient} keyword before the variable.
 */
public interface JobType {
    /**
     * Retrieves the name of the job type.
     *
     * @return the name of the job type as a String.
     */
    String getName();

    /**
     * Retrieves the description of the job type.
     *
     * @return the description of the job type as a String.
     */
    String getDescription();

    /**
     * Checks if the job type has an admin menu.
     *
     * @return true if the job type has an admin menu, false otherwise
     */
    boolean hasAdminMenu();

    /**
     * Initializes the job type.
     * <p>
     * This is called when a job using this job type is ready to be initialized. This method should be used to set up any
     * initial state or perform any necessary setup for the job type.
     */
    void initialize();

    /**
     * Stops the job type.
     */
    void stop();

    /**
     * Opens the admin menu for the JobType.
     * <p>
     * This method is used to open the admin menu for a specific JobType. The admin menu allows users with the appropriate
     * privileges to perform administrative tasks related to the JobType, such as configuring settings, managing
     * permissions, or performing other administrative actions.
     * </p>
     * @param player the player to open the admin menu for.
     */
    void openAdminMenu(Player player);

    /**
     * Checks if the job type has a boss menu.
     *
     * @return true if the job type has a boss menu, false otherwise.
     */
    boolean hasBossMenu();

    /**
     * Opens the boss menu for the JobType.
     * <p>
     * This method is used to open the boss menu for a specific JobType. The boss menu allows users with the appropriate
     * privileges to perform boss-level tasks related to the JobType.
     * </p>
     * <p>
     * Note: The boss menu is only available if the JobType has a boss menu.
     * </p>
     * @param player the player to open the boss menu for.
     */
    void openBossMenu(Player player);

    /**
     * Retrieves the icon of the job type that will be displayed for admins in the /jobs menu
     * and when an admin is selecting what job type to assign to a job.
     * <p>
     * Do not use colored glass as an icon, as clicking on it will not work in the /jobs menu.
     *
     * @return the icon of the job type as an ItemStack, or null if there is no icon.
     */
    @Nullable
    ItemStack getIcon();

    /**
     * Should return just a new instance of the JobType.
     * <p>
     * This method is used when adding a jobType to the list of job types so that the jobType can be instantiated.
     * @return a new instance of the JobType.
     */
    JobType getNewInstance(Job job);

    /**
     * Retrieves the data of the job type in JSON format.
     * @return The data of the job type in JSON format.
     */
    JsonObject getJsonJobTypeData();

    /**
     * Sets the data of the job type from JSON format.
     * @param json The data of the job type in JSON format.
     */
    void fromJsonJobTypeData(JsonObject json);
}
