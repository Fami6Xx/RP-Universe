package me.fami6xx.rpuniverse.core.jobs.types;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

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
     * Opens the admin menu for the JobType.
     * <p>
     * This method is used to open the admin menu for a specific JobType. The admin menu allows users with the appropriate
     * privileges to perform administrative tasks related to the JobType, such as configuring settings, managing
     * permissions, or performing other administrative actions.
     * </p>
     */
    void openAdminMenu();

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
     */
    void openBossMenu();

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
     * Returns a string representation of the object.
     * <p>
     * The toString method returns a string representation of the object. The returned string should be a concise
     * and human-readable representation that can be used for debugging or logging purposes.
     * <p>
     * IT IS CRUCIAL THAT YOU USE GSON TO SERIALIZE THE OBJECT TO A STRING.
     *
     * @return a String representation of the object.
     */
    String toString();

    /**
     * Converts a string representation of a JobType to an actual JobType object.
     *
     * @param string the string representation of the JobType to convert
     * @return the JobType object
     */
    JobType fromString(String string);
}
