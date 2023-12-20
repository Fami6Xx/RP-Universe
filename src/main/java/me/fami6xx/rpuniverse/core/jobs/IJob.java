package me.fami6xx.rpuniverse.core.jobs;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The IJob interface represents a job with different positions and functionalities.
 * It provides methods to add and edit positions, update salary, change player positions,
 * open the main menu, add and remove players, and check if a player is in the job.
 */
public interface IJob {
    // ToDo: Positions shouldn't be strings but an class holding information about the position

    /**
     * Adds a new position to the job.
     *
     * @param position the name of the position to add
     */
    void addPosition(String position);

    /**
     * Edits the details of a position in the job.
     *
     * @param position the name of the position to edit
     * @param newDetails the new details for the position
     */
    void bossEditPosition(String position, String newDetails);

    /**
     * Allows the boss to edit the salary of a particular position.
     *
     * @param position the name of the position whose salary needs to be edited
     * @param newSalary the new salary to set for the position
     */
    void bossEditSalary(String position, int newSalary);

    /**
     * Changes the position of a player by updating their UUID and the desired position.
     *
     * @param changingPlayerUUID the UUID of the player who is initiating the position change
     * @param playerUUID the UUID of the player whose position is being changed
     * @param position the new position for the player
     */
    void changePlayerPosition(UUID changingPlayerUUID, UUID playerUUID, String position);

    /**
     * Opens the main menu for the specified player.
     *
     * @param player the player for whom to open the main menu
     */
    void openMainMenu(Player player);

    /**
     * Adds a player to the job.
     *
     * @param addingPlayer the UUID of the player adding another player to the job
     * @param player the UUID of the player being added to the job
     */
    void addPlayerToJob(UUID addingPlayer, UUID player);

    /**
     * Removes a player from the job.
     *
     * @param removingPlayer the UUID of the player who is removing another player from the job
     * @param player the UUID of the player to be removed from the job
     */
    void removePLayerFromJob(UUID removingPlayer, UUID player);

    /**
     * Checks if a player is in the job.
     *
     * @param player the UUID of the player to check
     */
    void isPlayerInJob(UUID player);
}
