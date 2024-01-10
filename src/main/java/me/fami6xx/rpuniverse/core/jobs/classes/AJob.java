package me.fami6xx.rpuniverse.core.jobs.classes;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

/**
 * AJob is an abstract class that represents a job in a game.
 * It contains methods for managing positions, working steps, job bank,
 * and player assignments.
 */
public abstract class AJob {
    private Map<UUID, Position> playerPositions;
    private List<WorkingStep> workingSteps;
    private List<Position> jobPositions;

    private String jobName;
    private int jobBank = 0;
    private Location bossMenuLocation;

    /**
     * Creates a new AJob instance that is empty.
     */
    public AJob() {
        playerPositions = new HashMap<>();
        workingSteps = new ArrayList<>();
        jobPositions = new ArrayList<>();
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     */
    public AJob(String jobName, int jobBank, Location bossMenuLocation) {
        this();
        this.jobName = jobName;
        this.jobBank = jobBank;
        this.bossMenuLocation = bossMenuLocation;
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     * @param jobPositions     The list of positions associated with the job.
     * @param workingSteps     The list of working steps associated with the job.
     */
    public AJob(String jobName, int jobBank, Location bossMenuLocation, List<Position> jobPositions, List<WorkingStep> workingSteps) {
        this(jobName, jobBank, bossMenuLocation);
        this.jobPositions = jobPositions;
        this.workingSteps = workingSteps;
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     * @param jobPositions     The list of positions associated with the job.
     * @param workingSteps     The list of working steps associated with the job.
     * @param playerPositions  The map of player UUID to their assigned position.
     */
    public AJob(String jobName, int jobBank, Location bossMenuLocation, List<Position> jobPositions, List<WorkingStep> workingSteps, Map<UUID, Position> playerPositions) {
        this(jobName, jobBank, bossMenuLocation, jobPositions, workingSteps);
        this.playerPositions = playerPositions;
    }

    /**
     * Adds a position to the jobPositions list.
     *
     * @param position The position to be added.
     */
    // Position Management
    public void addPosition(Position position) {
        jobPositions.add(position);
    }

    /**
     * Edits the details of a position in the job.
     *
     * @param positionName   The name of the position to be edited.
     * @param updatedPosition The updated details of the position.
     */
    public void editPosition(String positionName, Position updatedPosition) {
        for(Position position : jobPositions) {
            if(position.getName().equals(positionName)) {
                position.setName(updatedPosition.getName());
                position.setSalary(updatedPosition.getSalary());
                position.setWorkingStepPermissionLevel(updatedPosition.getWorkingStepPermissionLevel());
                position.setBoss(updatedPosition.isBoss());
                position.setDefault(updatedPosition.isDefault());
            }
        }
    }

    /**
     * Removes a position from the jobPositions list based on the position name.
     *
     * @param positionName The name of the position to be removed.
     */
    public void removePosition(String positionName) {
        jobPositions.removeIf(position -> position.getName().equals(positionName));
    }

    /**
     * Checks if a list of positions contains at least one default position and at least one boss position.
     *
     * @param positions The list of positions to check.
     * @return {@code true} if the list contains at least one default position and at least one boss position, {@code false} otherwise.
     */
    public boolean checkPositions(List<Position> positions){
        boolean hasDefault = false;
        boolean hasBoss = false;

        for(Position position : positions){
            if(position.isDefault()){
                if(!hasDefault) hasDefault = true;
                else return false;
            }
            if(position.isBoss()){
                if(!hasBoss) hasBoss = true;
                else return false;
            }
        }

        return hasDefault && hasBoss;
    }

    /**
     * Changes the position of a player by updating the player's UUID and the new position in the playerPositions map.
     *
     * @param playerUUID    The UUID of the player to change the position for.
     * @param newPosition   The new position to assign to the player.
     */
    public void changePlayerPosition(UUID playerUUID, Position newPosition) {
        playerPositions.put(playerUUID, newPosition);
    }

    /**
     * Adds the specified amount of money to the job bank.
     *
     * @param money The amount of money to add to the job bank. Must be a positive integer.
     */
    public void addMoneyToJobBank(int money) {
        jobBank += money;
    }

    /**
     * Removes the specified amount of money from the job bank.
     *
     * @param money The amount of money to remove from the job bank. Must be a positive integer.
     * @return {@code true} if the money was successfully removed from the job bank, {@code false} otherwise.
     */
    public boolean removeMoneyFromJobBank(int money) {
        if(jobBank >= money) {
            jobBank -= money;
            return true;
        }
        return false;
    }

    /**
     * Retrieves the current amount of money available in the job bank.
     *
     * @return An integer representing the current amount of money in the job bank.
     */
    public int getCurrentMoneyInJobBank() {
        return jobBank;
    }

    /**
     * Adds a working step to the job.
     *
     * @param step The working step to be added.
     */
    public void addWorkingStep(WorkingStep step) {
        workingSteps.add(step);
    }

    /**
     * Removes a working step from the job.
     *
     * @param step The working step to be removed.
     * @return {@code true} if the working step was successfully removed, {@code false} otherwise.
     */
    public boolean removeWorkingStep(WorkingStep step) {
        return workingSteps.remove(step);
    }

    /**
     * Retrieves all the working steps associated with the job.
     *
     * @return A list of WorkingStep objects containing all the working steps.
     */
    public List<WorkingStep> getAllWorkingSteps() {
        return new ArrayList<>(workingSteps);
    }

    /**
     * Determines if a player has permission to perform a specific working step.
     *
     * @param player The player for which to check permission.
     * @param step   The working step to check permission for.
     * @return {@code true} if the player has permission for the working step, {@code false} otherwise.
     */
    public boolean hasPermissionForWorkingStep(Player player, WorkingStep step) {
        UUID playerUUID = player.getUniqueId();
        if(playerPositions.containsKey(playerUUID)) {
            Position position = playerPositions.get(playerUUID);
            return position.getWorkingStepPermissionLevel() >= step.getNeededPermissionLevel();
        }
        return false;
    }

    /**
     * Adds a player to a job by associating their UUID with a position.
     *
     * @param playerUUID The UUID of the player to add.
     * @param position The position to associate the player with.
     */
    public void addPlayerToJob(UUID playerUUID, Position position) {
        playerPositions.put(playerUUID, position);
    }

    /**
     * Adds a player to a job by associating their UUID with the default position.
     * @param playerUUID The UUID of the player to add.
     */
    public void addPlayerToJob(UUID playerUUID){
        for(Position position : jobPositions) {
            if(position.isDefault()) {
                playerPositions.put(playerUUID, position);
                return;
            }
        }
    }

    /**
     * Removes a player from the job by removing their UUID from the playerPositions map.
     *
     * @param playerUUID The UUID of the player to remove from the job.
     */
    public void removePlayerFromJob(UUID playerUUID) {
        playerPositions.remove(playerUUID);
    }

    /**
     * Checks whether a player is in a job by checking if their UUID is associated with a position.
     *
     * @param playerUUID The UUID of the player to check.
     * @return {@code true} if the player is in a job, {@code false} otherwise.
     */
    public boolean isPlayerInJob(UUID playerUUID) {
        return playerPositions.containsKey(playerUUID);
    }

    /**
     * Opens the boss menu for the specified player.
     *
     * @param player The player for whom to open the boss menu.
     */
    public abstract void openBossMenu(Player player);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AJob{");

        sb.append("playerPositions={");
        for (Map.Entry<UUID, Position> entry : playerPositions.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue().toString()).append(",");
        }
        if (!playerPositions.isEmpty()) sb.deleteCharAt(sb.length() - 1); // Remove the last comma
        sb.append("}, ");

        sb.append("workingSteps=[");
        for (WorkingStep step : workingSteps) {
            sb.append(step.toString()).append(",");
        }
        if (!workingSteps.isEmpty()) sb.deleteCharAt(sb.length() - 1); // Remove the last comma
        sb.append("], ");

        sb.append("jobPositions=[");
        for (Position position : jobPositions) {
            sb.append(position.toString()).append(",");
        }
        if (!jobPositions.isEmpty()) sb.deleteCharAt(sb.length() - 1); // Remove the last comma
        sb.append("], ");

        sb.append("jobName='").append(jobName).append("', ");
        sb.append("jobBank=").append(jobBank);

        sb.append("}");

        return sb.toString();
    }

    // ToDo: Implement fromString method
}