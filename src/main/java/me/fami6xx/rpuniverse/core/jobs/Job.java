package me.fami6xx.rpuniverse.core.jobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.decentsoftware.holograms.api.actions.Action;
import eu.decentsoftware.holograms.api.actions.ActionType;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.StaticHologram;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin.JobAdminMenu;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import me.fami6xx.rpuniverse.core.menuapi.MenuManager;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a job in the system.
 */
public class Job {
    private static final Logger LOGGER = RPUniverse.getInstance().getLogger();
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();
    private transient JobType jobType;
    private transient Hologram bossMenuHologram;
    private String jobTypeName = null;
    private String JSONJobTypeData = null;

    private Map<UUID, Position> playerPositions;
    private List<Position> jobPositions;
    private String jobName;
    private int jobBank = 0;
    private Location bossMenuLocation;

    /**
     * Creates a new AJob instance that is empty.
     */
    public Job() {
        playerPositions = new HashMap<>();
        jobPositions = new ArrayList<>();
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     */
    public Job(String jobName, int jobBank, Location bossMenuLocation) {
        this();
        this.jobName = jobName;
        this.jobBank = jobBank;
        this.bossMenuLocation = bossMenuLocation;
        createBossMenuHologram();
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     * @param jobPositions     The list of positions associated with the job.
     */
    public Job(String jobName, int jobBank, Location bossMenuLocation, List<Position> jobPositions) {
        this(jobName, jobBank, bossMenuLocation);
        this.jobPositions = jobPositions;
        createBossMenuHologram();
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     * @param jobPositions     The list of positions associated with the job.
     * @param playerPositions  The map of player UUID to their assigned position.
     */
    public Job(String jobName, int jobBank, Location bossMenuLocation, List<Position> jobPositions, Map<UUID, Position> playerPositions) {
        this(jobName, jobBank, bossMenuLocation, jobPositions);
        this.playerPositions = playerPositions;
        createBossMenuHologram();
    }

    /**
     * Initializes the object by creating the boss menu hologram.
     */
    protected void initialize(){
        createBossMenuHologram();
    }

    /**
     * Removes the job by deleting the boss menu hologram if it exists.
     */
    protected void remove(){
        if(bossMenuHologram != null){
            bossMenuHologram.delete();
        }
    }

    /**
     * Creates a hologram for the boss menu.
     * If bossMenuLocation is not null, creates a static hologram with the specified range.
     * The hologram displays information about the job, such as job name, job bank, and job type.
     * The hologram has two pages - one for regular players and one for admins.
     * The regular player page allows opening the JobAdminMenu.
     * The admin page allows opening the JobAdminMenu as well.
     */
    protected void createBossMenuHologram(){
        if(bossMenuLocation != null){
            if(bossMenuHologram != null){
                bossMenuHologram.delete();
            }

            Job job = this;

            int range;

            HashMap<String, String> replace = new HashMap<>();
            try {
                range = RPUniverse.getInstance().getConfiguration().getInt("jobs.menuRange");
            }catch (Exception exc){
                replace.put("{value}", "jobs.menuRange");
                RPUniverse.getInstance().getLogger().severe(FamiUtils.formatWithPrefix(FamiUtils.replace(RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace)));
                return;
            }

            Location toCreate = bossMenuLocation.clone();
            toCreate.add(0, 1.5, 0);

            StaticHologram staticHologram = new StaticHologram(toCreate, false, range, false) {
                @Override
                public int getPageToDisplay(Player player) {
                    return job.getPageToDisplay(player);
                }

                @Override
                public boolean shouldShow(Player player) {
                    return RPUniverse.getPlayerData(player.getUniqueId().toString()).shouldDisplayJob(job);
                }
            };

            Hologram holo = staticHologram.getHologram();
            this.bossMenuHologram = holo;

            replace.put("{jobName}", jobName);
            replace.put("{jobBank}", String.valueOf(jobBank));
            if(jobTypeName != null)
                replace.put("{jobType}", jobTypeName);
            else
                replace.put("{jobType}", "None");

            if(holo.size() == 0){
                holo.addPage();
                holo.addPage();
            } else if(holo.size() == 1){
                holo.addPage();
            }

            String[] hologramLines = RPUniverse.getLanguageHandler().jobBossMenuHologram.split("~");
            HologramPage bossPage = holo.getPage(0);
            for(String line : hologramLines){
                line = FamiUtils.replaceAndFormat(line, replace);
                bossPage.addLine(new HologramLine(bossPage, bossPage.getNextLineLocation(), line));
            }

            bossPage.addAction(ClickType.RIGHT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    new JobAdminMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), job).open();
                    return true;
                }
            }, ""));

            String[] adminHologramLines = RPUniverse.getLanguageHandler().jobBossMenuAdminHologram.split("~");
            HologramPage adminPage = holo.getPage(1);
            for(String line : adminHologramLines){
                line = FamiUtils.replaceAndFormat(line, replace);
                adminPage.addLine(new HologramLine(adminPage, adminPage.getNextLineLocation(), line));
            }

            adminPage.addAction(ClickType.RIGHT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    new JobAdminMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), job).open();
                    return true;
                }
            }, ""));
        }
    }

    /**
     * Retrieves the page to display for a given player.
     *
     * @param player the player whose page to retrieve
     * @return the page number to display, either 1 if the player has permission for editing jobs, or 0 otherwise
     */
    private int getPageToDisplay(Player player){
        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

        return data.hasPermissionForEditingJobs() ? 1 : 0;
    }

    /**
     * Retrieves the name of the job.
     *
     * @return The name of the job as a String.
     */
    public String getName() {
        return jobName;
    }

    /**
     * Renames the job with the given new name.
     *
     * @param newName The new name for the job. Must not be null.
     */
    public void renameJob(String newName) {
        RPUniverse.getInstance().getDataSystem().getDataHandler().renameJobData(jobName, newName);
        this.jobName = newName;
        createBossMenuHologram();
        RPUniverse.getInstance().getMenuManager().closeAllMenus(j -> j == this);
    }

    /**
     * Retrieves the location of the boss menu associated with the job.
     *
     * @return The location of the boss menu as a Location object.
     */
    public Location getBossMenuLocation() {
        return bossMenuLocation;
    }

    /**
     * Sets the location of the boss menu associated with the job.
     *
     * @param location The location of the boss menu. Must not be null.
     */
    public void setBossMenuLocation(@Nonnull Location location){
        this.bossMenuLocation = location;
        createBossMenuHologram();
    }

    /**
     * Retrieves the job type of the job.
     *
     * @return The job type of the job as a JobType object.
     */
    @Nullable
    public JobType getJobType() {
        return jobType;
    }

    /**
     * Retrieves the name of the job type.
     *
     * @return The name of the job type as a String.
     */
    public String getJobTypeName() {
        return jobTypeName;
    }

    /**
     * Sets the job type of the job.
     *
     * @param jobType The job type to be set. Must not be null.
     */
    public void setJobType(JobType jobType) {
        this.jobType = jobType;
        this.jobTypeName = jobType.getName();
        RPUniverse.getInstance().getMenuManager().closeAllMenus(j -> j == this);
    }

    /**
     * Retrieves the job type data of the job.
     *
     * @return The job type data of the job as a String.
     */
    public String getJobTypeData() {
        return JSONJobTypeData;
    }

    /**
     * Adds a position to the jobPositions list.
     *
     * @param position The position to be added.
     */
    public void addPosition(Position position) {
        jobPositions.add(position);
        RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
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
        RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
    }

    /**
     * Removes a position from the jobPositions list based on the position name.
     *
     * @param positionName The name of the position to be removed.
     */
    public void removePosition(String positionName) {
        int index = -1;
        for(int i = 0; i < jobPositions.size(); i++) {
            if(jobPositions.get(i).getName().equals(positionName)) {
                index = i;
                break;
            }
        }

        if(index == -1) {
            return;
        }

        Position removedPosition = jobPositions.get(index);
        jobPositions.remove(index);

        if(playerPositions.containsValue(removedPosition)){
            for(UUID playerUUID : playerPositions.keySet()){
                if(playerPositions.get(playerUUID).equals(removedPosition)){
                    playerPositions.remove(playerUUID);
                    if(jobPositions.isEmpty()){
                        RPUniverse.getPlayerData(playerUUID.toString()).removeJob(this);
                    }else addPlayerToJob(playerUUID);
                }
            }
        }

        jobPositions.removeIf(position -> position.getName().equals(positionName));

        RPUniverse.getInstance().getMenuManager().closeAllMenus(j -> j == this, MenuTag.JOB_POSITION, MenuTag.JOB_POSITION_INTERNAL);
        RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
    }

    /**
     * Retrieves the list of positions associated with the job.
     *
     * @return A List of Position objects representing the job positions.
     */
    public List<Position> getPositions(){
        return jobPositions;
    }

    /**
     * Retrieves a Position object from the jobPositions list based on the provided position name.
     *
     * @param name The name of the position to retrieve. Must not be null.
     * @return The Position object with the specified name, or null if no position with that name exists.
     */
    public Position getPositionByName(String name){
        for(Position position : jobPositions){
            if(position.getName().equalsIgnoreCase(name)){
                return position;
            }
        }

        return null;
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
     * Checks if the job is ready to be executed by validating various conditions.
     *
     * @return A list of error messages indicating the reasons why the job is not ready. An empty list signifies that the job is ready.
     */
    public List<String> isJobReady(){
        List<String> errors = new ArrayList<>();
        if(jobPositions.isEmpty()) errors.add("No positions have been added to the job.");
        if(!checkPositions(jobPositions)) errors.add("The job must have at least one default position and at least one boss position.");

        if(jobTypeName == null) errors.add("The job type has not been set.");
        else if(jobType == null) errors.add("The job type '" + jobTypeName + "' does not exist or has not been set.");

        return errors;
    }

    /**
     * Changes the position of a player by updating the player's UUID and the new position in the playerPositions map.
     *
     * @param playerUUID    The UUID of the player to change the position for.
     * @param newPosition   The new position to assign to the player.
     */
    public void changePlayerPosition(UUID playerUUID, Position newPosition) {
        playerPositions.put(playerUUID, newPosition);
        RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
    }

    /**
     * Adds the specified amount of money to the job bank.
     *
     * @param money The amount of money to add to the job bank. Must be a positive integer.
     */
    public void addMoneyToJobBank(int money) {
        jobBank += money;
        RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
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
            RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
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
     * Adds a player to a job by associating their UUID with a position.
     *
     * @param playerUUID The UUID of the player to add.
     * @param position The position to associate the player with.
     */
    public void addPlayerToJob(UUID playerUUID, Position position) {
        playerPositions.put(playerUUID, position);
        RPUniverse.getPlayerData(playerUUID.toString()).addJob(this);
        RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
    }

    /**
     * Adds a player to a job by associating their UUID with the default position.
     * @param playerUUID The UUID of the player to add.
     */
    public void addPlayerToJob(UUID playerUUID){
        for(Position position : jobPositions) {
            if(position.isDefault()) {
                playerPositions.put(playerUUID, position);
                RPUniverse.getPlayerData(playerUUID.toString()).addJob(this);
                RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
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
        RPUniverse.getPlayerData(playerUUID.toString()).removeJob(this);
        RPUniverse.getInstance().getMenuManager().reopenMenus(j -> j == this);
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
     * Retrieves the position of a player identified by their UUID.
     *
     * @param playerUUID The UUID of the player.
     * @return The position of the player, or null if the player's position is not found.
     */
    @Nullable
    public Position getPlayerPosition(UUID playerUUID){
        return playerPositions.getOrDefault(playerUUID, null);
    }

    /**
     * Retrieves a list of all players in a job.
     *
     * @return a {@link List} of {@link UUID} representing the player IDs in the job.
     */
    public List<UUID> getAllPlayersInJob(){
        return new ArrayList<>(playerPositions.keySet());
    }

    /**
     * Prepares the Job object for saving by serializing the jobType to a JSON string.
     * <p>
     * This method checks if the jobType field is not null. If it is not null, it serializes the jobType
     * to a JSON string using the {@link JobType#toString()} method. The JSON string is then stored
     * in the JSONJobTypeData field of the Job object.
     * <p>
     * Note that it is crucial to use the GSON library to serialize the jobType to a JSON string.
     *
     * @see JobType#toString()
     */
    public void prepareForSave(){
        if(jobType != null)
            JSONJobTypeData = jobType.toString();
    }

    /**
     * Retrieves a Job object with the specified job name.
     *
     * @param jobName The name of the job to retrieve. Must not be null.
     * @return The Job object with the specified job name, or null if no job with that name exists.
     */
    @Nullable
    public static Job getJob(String jobName){
        for(Job job : RPUniverse.getInstance().getJobsHandler().getJobs()){
            if(job.getName().equalsIgnoreCase(jobName)){
                return job;
            }
        }

        return null;
    }

    public static Job fromString(String s) {
        try {
            return GSON.fromJson(s, Job.class);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error converting string to Job instance: " + e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        if(jobType != null)
            JSONJobTypeData = jobType.toString();

        return GSON.toJson(this);
    }
}