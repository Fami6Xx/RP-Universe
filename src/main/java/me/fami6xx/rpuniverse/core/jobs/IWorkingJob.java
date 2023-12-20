package me.fami6xx.rpuniverse.core.jobs;

import java.util.List;
import java.util.UUID;

/**
 * The IWorkingJob interface represents a job that involves performing work-related steps.
 * It extends the IMoneyJob interface and provides additional methods for managing working steps.
 */
public interface IWorkingJob extends IMoneyJob {
    // ToDo: Working steps shouldn't be strings but an class holding the information about the step

    /**
     * Adds a working step to the job.
     *
     * @param step the working step to add
     */
    void adminAddWorkingStep(String step);

    /**
     * Removes a working step from the job.
     *
     * @param step the working step to remove
     * @return true if the step was successfully removed, false otherwise
     */
    boolean adminRemoveWorkingStep(String step);

    /**
     * Retrieves a list of all the working steps for a job.
     *
     * @return a list of all the working steps for the job
     */
    List<String> getAllWorkingSteps();

    /**
     * Checks if the specified player has permission to perform a working step.
     *
     * @param playerUUID the UUID of the player
     */
    void hasPermissionForWorkingStep(UUID playerUUID);
}
