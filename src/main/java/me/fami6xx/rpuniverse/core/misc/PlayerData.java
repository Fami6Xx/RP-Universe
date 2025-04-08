package me.fami6xx.rpuniverse.core.misc;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.PlayerAddedToJobEvent;
import me.fami6xx.rpuniverse.core.api.PlayerRemovedFromJobEvent;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.JobsHandler;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to store player data. You can extend and add fields to this class, but be aware that the added fields might not be saved depending on the selected Data Handler.
 * <p>
 * It is serialized and deserialized by the selected Data Handler.
 * <p>
 * To ignore a field from being serialized, use the transient keyword.
 */
public class PlayerData {
    private transient Player bindedPlayer;
    private transient OfflinePlayer bindedOfflinePlayer;
    private transient List<Job> playerJobs = new ArrayList<>();
    private transient Job selectedPlayerJob = null;
    private transient PlayerMode playerMode = PlayerMode.USER;
    private transient famiHologram currentTagHologram;
    private transient int timeOnline = 0;

    private final UUID dataUUID;
    private String playerUUID;
    private String selectedJobUUID;
    private String tag;
    private List<String> playerJobUUIDs;

    private int foodLevel = 100;
    private int waterLevel = 100;
    private int poopLevel = 0;
    private int peeLevel = 0;

    public PlayerData(Player bindedPlayer) {
        this.bindedPlayer = bindedPlayer;
        this.bindedOfflinePlayer = null;
        this.dataUUID = UUID.randomUUID();
        ErrorHandler.debug("Created PlayerData for player: " + bindedPlayer.getName() + " with UUID: " + bindedPlayer.getUniqueId());
    }

    public PlayerData(OfflinePlayer bindedOfflinePlayer) {
        this.bindedOfflinePlayer = bindedOfflinePlayer;
        this.bindedPlayer = null;
        this.dataUUID = UUID.randomUUID();
        ErrorHandler.debug("Created PlayerData for offline player: " + bindedOfflinePlayer.getName() + " with UUID: " + bindedOfflinePlayer.getUniqueId());
    }

    public PlayerData(String playerUUID){
        if(playerUUID == null) throw new IllegalArgumentException("Player UUID cannot be null!");
        this.playerUUID = playerUUID;
        this.dataUUID = UUID.randomUUID();
        ErrorHandler.debug("Created PlayerData for player UUID: " + playerUUID);
    }

    /**
     * Adds a job to the player's list of jobs.
     * <p>
     * This method does not check if the player is at maximum amount of jobs.
     *
     * @param job The job to be added.
     */
    public void addJob(Job job){
        ErrorHandler.debug("Attempting to add job: " + job.getName() + " to player: " + playerUUID);

        if (job.isPlayerInJob(UUID.fromString(playerUUID))) {
            ErrorHandler.debug("Player is already in job: " + job.getName());
            return;
        }
        if (playerJobs.contains(job)) {
            ErrorHandler.debug("Player already has job: " + job.getName() + " in their job list");
            return;
        }
        if (!job.isJobReady().isEmpty()) {
            List<String> reasons = job.isJobReady();
            ErrorHandler.severe("The job " + job.getName() + " is not ready to be added to the player " + playerUUID + " because it is missing the default requirements: " + String.join(", ", reasons));
            return;
        }

        PlayerAddedToJobEvent event = new PlayerAddedToJobEvent(job, this.bindedPlayer);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ErrorHandler.debug("PlayerAddedToJobEvent was cancelled for job: " + job.getName());
            return;
        }

        playerJobs.add(job);
        job.addPlayerToJob(UUID.fromString(playerUUID));
        ErrorHandler.debug("Successfully added job: " + job.getName() + " to player: " + playerUUID);

        if(selectedPlayerJob == null) {
            selectedPlayerJob = job;
            RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);
            ErrorHandler.debug("Set job: " + job.getName() + " as selected job for player: " + playerUUID);
        }
    }

    /**
     * Removes a job from the player's list of jobs.
     *
     * @param job The job to be removed.
     */
    public void removeJob(Job job){
        ErrorHandler.debug("Attempting to remove job: " + job.getName() + " from player: " + playerUUID);

        PlayerRemovedFromJobEvent event = new PlayerRemovedFromJobEvent(job, this.bindedPlayer);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ErrorHandler.debug("PlayerRemovedFromJobEvent was cancelled for job: " + job.getName());
            return;
        }

        playerJobs.remove(job);
        ErrorHandler.debug("Successfully removed job: " + job.getName() + " from player: " + playerUUID);

        if(selectedPlayerJob == job){
            if(!playerJobs.isEmpty()) {
                selectedPlayerJob = playerJobs.get(0);
                ErrorHandler.debug("Set new selected job: " + selectedPlayerJob.getName() + " for player: " + playerUUID);
            } else {
                selectedPlayerJob = null;
                ErrorHandler.debug("Player: " + playerUUID + " has no more jobs, selected job set to null");
            }

            RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);
        }
    }

    /**
     * Sets the selected player job.
     *
     * @param job The job to be set as the selected player job.
     */
    public void setSelectedPlayerJob(Job job){
        if (job == null) {
            ErrorHandler.debug("Setting selected job to null for player: " + playerUUID);
            this.selectedPlayerJob = null;
            RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);
            return;
        }

        ErrorHandler.debug("Attempting to set selected job to: " + job.getName() + " for player: " + playerUUID);

        if(playerJobs.contains(job)) {
            this.selectedPlayerJob = job;
            ErrorHandler.debug("Successfully set selected job to: " + job.getName() + " for player: " + playerUUID);
            RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);
        } else {
            ErrorHandler.severe("Failed to set selected job: player does not have the job " + job.getName());
            throw new IllegalArgumentException("The player does not have the job " + job.getName());
        }
    }

    /**
     * Retrieves the selected player job.
     *
     * @return the selected player job
     */
    public Job getSelectedPlayerJob(){
        return selectedPlayerJob;
    }

    /**
     * Retrieves the list of jobs for the player data.
     * <p>
     * This method returns a new ArrayList to prevent the original list from being modified.
     *
     * @return the list of jobs for the player data
     */
    public List<Job> getPlayerJobs(){
        return new ArrayList<>(playerJobs);
    }

    /**
     * Sets the player mode.
     *
     * @param playerMode The player mode to be set.
     */
    public void setPlayerMode(PlayerMode playerMode){
        this.playerMode = playerMode;

        RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);

        RPUniverse.getInstance().getHoloAPI().getVisibilityHandler().queue.add(() -> RPUniverse.getInstance().getHoloAPI().getVisibilityHandler().updateHologramsPlayerMode(bindedPlayer, this));
        PlayerMenu menu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(bindedPlayer);
        if(menu.getCurrentMenu() != null) {
            if(menu.getCurrentMenu().getMenuTags().contains(MenuTag.JOB)){
                if(menu.getCurrentMenu().getMenuTags().contains(MenuTag.ADMIN)){
                    if(!hasPermissionForEditingJobs()) menu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                }
            }
        };
    }

    /**
     * Increase time online by 1 second
     */
    public void increaseTimeOnline(){
        timeOnline++;
    }

    /**
     * Retrieves the time online for the player data in seconds.
     *
     * @return the time online of the player data
     */
    public int getTimeOnline(){
        return timeOnline;
    }

    /**
     * Retrieves the player mode for the player data.
     *
     * @return the player mode of the player data
     */
    public PlayerMode getPlayerMode(){
        return playerMode;
    }

    /**
     * Retrieves the tag for the player data.
     *
     * @return the tag of the player data
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag for the player data.
     *
     * @param tag The tag to be set.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Retrieves the visibility of the tag for the player data.
     *
     * @return the visibility of the tag of the player data
     */
    public boolean isTagVisible() {
        return currentTagHologram != null;
    }

    /**
     * Retrieves the hologram for the tag of the player data.
     *
     * @return the hologram of the tag of the player data
     */
    @Nullable
    public famiHologram getCurrentTagHologram() {
        return currentTagHologram;
    }

    /**
     * Sets the hologram for the tag of the player data.
     *
     * @param currentTagHologram The hologram to be set.
     */
    public void setCurrentTagHologram(famiHologram currentTagHologram) {
        this.currentTagHologram = currentTagHologram;
    }

    /**
     * Prepares the player data for saving.
     * <p>
     * If the bindedPlayer field is not null, it retrieves the name of the player and assigns it to playerName.
     * <p>
     * If the bindedOfflinePlayer field is not null, it retrieves the name of the offline player and assigns it to playerName.
     * <p>
     * It retrieves the UUID of the player data as a String and assigns it to playerUUID.
     * <p>
     * If the selectedPlayerJob field is not null, it retrieves the name of the job and assigns it to selectedJobName.
     * <p>
     * Initializes playerJobNames as an empty ArrayList.
     * <p>
     * For each job in playerJobs, it retrieves the name of the job and adds it to playerJobNames.
     */
    public void prepareForSave(){
        ErrorHandler.debug("Preparing PlayerData for save for player: " + playerUUID);

        playerUUID = getPlayerUUID().toString();

        selectedJobUUID = selectedPlayerJob == null ? null : selectedPlayerJob.getJobUUID().toString();
        ErrorHandler.debug("Selected job UUID for save: " + selectedJobUUID);

        playerJobUUIDs = new ArrayList<>();
        for(Job job : playerJobs) {
            playerJobUUIDs.add(job.getJobUUID().toString());
        }
        ErrorHandler.debug("Saved " + playerJobUUIDs.size() + " job UUIDs for player: " + playerUUID);
    }

    /**
     * Loads the player data after it has been saved.
     * <p>
     * If the bindedPlayer field is not null, it retrieves the player object using the playerUUID and assigns it to bindedPlayer.
     * <p>
     * If the bindedOfflinePlayer field is not null, it retrieves the offline player object using the playerUUID and assigns it to bindedOfflinePlayer.
     * <p>
     * If the selectedJobName field is not null, it retrieves the job object using the selectedJobName and assigns it to selectedPlayerJob.
     * <p>
     * Initializes playerJobs as an empty ArrayList.
     * <p>
     * For each jobName in playerJobNames, it retrieves the job object using the jobName and adds it to playerJobs.
     */
    public void loadAfterSave(){
        ErrorHandler.debug("Loading PlayerData after save for player UUID: " + playerUUID);

        if(Bukkit.getPlayer(playerUUID) != null) {
            this.bindedPlayer = Bukkit.getPlayer(playerUUID);
            ErrorHandler.debug("Bound to online player: " + bindedPlayer.getName());
        } else {
            this.bindedOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
            ErrorHandler.debug("Bound to offline player: " + bindedOfflinePlayer.getName());
        }

        if(selectedJobUUID != null) {
            try {
                selectedPlayerJob = Job.getJobByUUID(selectedJobUUID);
                ErrorHandler.debug("Loaded selected job: " + selectedPlayerJob.getName());
            } catch (Exception e) {
                ErrorHandler.severe("Failed to load selected job with UUID: " + selectedJobUUID, e);
            }
        }

        playerJobs = new ArrayList<>();
        if(playerJobUUIDs != null) {
            for (String jobUUID : playerJobUUIDs) {
                try {
                    Job job = Job.getJobByUUID(jobUUID);
                    playerJobs.add(job);
                    ErrorHandler.debug("Loaded job: " + job.getName() + " for player: " + playerUUID);
                } catch (Exception e) {
                    ErrorHandler.severe("Failed to load job with UUID: " + jobUUID, e);
                }
            }
        }
        ErrorHandler.debug("Loaded " + playerJobs.size() + " jobs for player: " + playerUUID);

        this.playerMode = PlayerMode.USER;
        ErrorHandler.debug("Set player mode to USER for player: " + playerUUID);
    }

    /**
     * Retrieves the UUID of the player data.
     *
     * @return the UUID of the player data*/
    public UUID getDataUUID() {
        return dataUUID;
    }

    /**
     * Retrieves the UUID of the player data.
     *
     * @return the UUID of the player data
     */
    public UUID getPlayerUUID() {
        if (bindedPlayer != null) {
            return bindedPlayer.getUniqueId();
        } else if (bindedOfflinePlayer != null) {
            return bindedOfflinePlayer.getUniqueId();
        } else {
            return null;
        }
    }

    /**
     * Determines whether a job should be displayed to a player in a boss menu.
     *
     * @param job       The job to check for display.
     * @return True if the job should be displayed, false otherwise.
     */
    public boolean shouldDisplayJob(Job job){
        if(hasPermissionForEditingJobs()) return true;

        if(!job.isPlayerInJob(UUID.fromString(playerUUID))) return false;
        if(selectedPlayerJob != job) return false;
        if(bindedPlayer != null) return job.getPlayerPosition(bindedPlayer.getUniqueId()).isBoss();
        else if(bindedOfflinePlayer != null) return bindedOfflinePlayer.getUniqueId().equals(job.getPlayerPosition(bindedOfflinePlayer.getUniqueId()).isBoss());

        return false;
    }

    /**
     * Determines whether a working step should be displayed to a player.
     *
     * @param job The job to check for display.
     * @param neededPermissionLevel The minimum permission level needed to display the working step.
     * @return <code>true</code> if the working step should be displayed, <code>false</code> otherwise.
     */
    public boolean shouldDisplayWorkingStep(Job job, int neededPermissionLevel){
        if(hasPermissionForEditingJobs()) return true;

        if(selectedPlayerJob == null) return false;
        if(selectedPlayerJob != job) return false;

        if(bindedPlayer != null) return selectedPlayerJob.getPlayerPosition(bindedPlayer.getUniqueId()).getWorkingStepPermissionLevel() >= neededPermissionLevel;
        else if(bindedOfflinePlayer != null) return selectedPlayerJob.getPlayerPosition(bindedOfflinePlayer.getUniqueId()).getWorkingStepPermissionLevel() >= neededPermissionLevel;

        return false;
    }



    /**
     * Checks if the player has permission to edit jobs.
     *
     * @return true if the player has permission to edit jobs, false otherwise.
     */
    public boolean hasPermissionForEditingJobs(){
        if(RPUniverse.getInstance().getConfiguration().getBoolean("jobs.preferPermissionsOverModeForEdit")) {
            if (bindedPlayer != null) {
                if (bindedPlayer.hasPermission("rpu.jobs"))
                    return true;
            } else if (bindedOfflinePlayer != null) {
                if (bindedOfflinePlayer.getPlayer().hasPermission("rpu.jobs"))
                    return true;
            }
        }

        if(playerMode == PlayerMode.ADMIN) return true;

        PlayerMode neededPlayerMode = PlayerMode.getModeFromString(RPUniverse.getInstance().getConfiguration().getString("jobs.neededModeToEditJobs"));

        return neededPlayerMode == playerMode;
    }

    /**
     * Checks if the player has permission to create jobs.
     *
     * @return true if the player has permission to create jobs, false otherwise.
     */
    public boolean hasPermissionForCreatingJobs(){
        if(RPUniverse.getInstance().getConfiguration().getBoolean("jobs.preferPermissionsOverModeForCreate")) {
            if (bindedPlayer != null) {
                if (bindedPlayer.hasPermission("rpu.createjob"))
                    return true;
            } else if (bindedOfflinePlayer != null) {
                if (bindedOfflinePlayer.getPlayer().hasPermission("rpu.createjob"))
                    return true;
            }
        }

        if(playerMode == PlayerMode.ADMIN) return true;

        PlayerMode neededPlayerMode = PlayerMode.getModeFromString(RPUniverse.getInstance().getConfiguration().getString("jobs.neededModeToCreateJobs"));

        return neededPlayerMode == playerMode;
    }

    /**
     * Determines whether the player can be added to a job.
     *
     * @return true if the player can be added to a job, false otherwise.
     */
    public boolean canBeAddedToJob() {
        if(playerJobs.isEmpty()) return true;

        if(playerJobs.size() >= RPUniverse.getInstance().getConfiguration().getInt("jobs.maxJobsPerPlayer")) return false;

        if(RPUniverse.getInstance().getConfiguration().getBoolean("jobs.needsPermissionToHaveMultipleJobs")){
            boolean hasPermission;
            if(bindedPlayer != null) hasPermission =  bindedPlayer.hasPermission("rpu.multiplejobs");
            else if(bindedOfflinePlayer != null) hasPermission =  bindedOfflinePlayer.getPlayer().hasPermission("rpu.multiplejobs");
            else hasPermission = false;

            return hasPermission;
        }

        return true;
    }

    /**
     * Determines whether the player can be added to the given job.
     *
     * @param job The job to check for add-ability.
     * @return True if the player can be added to the job, false otherwise.
     */
    public boolean canBeAddedToJob(Job job) {
        if(playerJobs.isEmpty()) return true;

        if(playerJobs.contains(job)) return false;

        if(playerJobs.size() >= RPUniverse.getInstance().getConfiguration().getInt("jobs.maxJobsPerPlayer")) return false;

        if(RPUniverse.getInstance().getConfiguration().getBoolean("jobs.needsPermissionToHaveMultipleJobs")){
            boolean hasPermission;
            if(bindedPlayer != null) hasPermission =  bindedPlayer.hasPermission("rpu.multiplejobs");
            else if(bindedOfflinePlayer != null) hasPermission =  bindedOfflinePlayer.getPlayer().hasPermission("rpu.multiplejobs");
            else hasPermission = false;

            return hasPermission;
        }

        return true;
    }

    /**
     * Updates the player bound to this PlayerData instance.
     *
     * @param player The player to be updated.
     */
    public void updatePlayer(Player player){
        this.bindedPlayer = player;
        this.bindedOfflinePlayer = null;
    }

    /**
     * Updates the player bound to this PlayerData instance.
     *
     * @param player The player to be updated.
     */
    public void updatePlayer(OfflinePlayer player){
        this.bindedOfflinePlayer = player;
        this.bindedPlayer = null;
    }

    /**
     * Retrieves the player object that is currently bound to this PlayerData instance.
     *
     * @return the player object that is currently bound, or null if no player is bound.
     */
    @Nullable
    public Player getBindedPlayer() {
        return bindedPlayer;
    }

    /**
     * Returns the OfflinePlayer object that is currently bound to this PlayerData instance.
     *
     * @return the OfflinePlayer object that is currently bound, or null if no OfflinePlayer is bound.
     */
    @Nullable
    public OfflinePlayer getBindedOfflinePlayer() {
        return bindedOfflinePlayer;
    }

    /**
     * Gets the player's food level
     *
     * @return the player's food level (Higher is better)
     */
    public int getFoodLevel() {
        return foodLevel;
    }

    /**
     * Sets the player's food level
     *
     * @param foodLevel the player's food level (Higher is better)
     */
    public void setFoodLevel(int foodLevel) {
        if (foodLevel > 100) foodLevel = 100;
        if (foodLevel < 0) foodLevel = 0;
        this.foodLevel = foodLevel;
    }

    /**
     * Gets the player's water level
     *
     * @return the player's water level (Higher is better)
     */
    public int getWaterLevel() {
        return waterLevel;
    }

    /**
     * Sets the player's water level
     *
     * @param waterLevel the player's water level (Higher is better)
     */
    public void setWaterLevel(int waterLevel) {
        if (waterLevel > 100) waterLevel = 100;
        if (waterLevel < 0) waterLevel = 0;
        this.waterLevel = waterLevel;
    }

    /**
     * Gets the player's poop level
     * @return the player's poop level (Lower is better)
     */
    public int getPoopLevel() {
        return poopLevel;
    }

    /**
     * Sets the player's poop level
     *
     * @param poopLevel the player's poop level (Lower is better)
     */
    public void setPoopLevel(int poopLevel) {
        if (poopLevel > 100) poopLevel = 100;
        if (poopLevel < 0) poopLevel = 0;
        this.poopLevel = poopLevel;
    }

    /**
     * Gets the player's pee level
     *
     * @return the player's pee level (Lower is better)
     */
    public int getPeeLevel() {
        return peeLevel;
    }

    /**
     * Sets the player's pee level
     *
     * @param peeLevel the player's pee level (Lower is better)
     */
    public void setPeeLevel(int peeLevel) {
        if (peeLevel > 100) peeLevel = 100;
        if (peeLevel < 0) peeLevel = 0;
        this.peeLevel = peeLevel;
    }

    /**
     * Determines if the player can open a specified lock.
     * @param lock The lock to check against.
     * @return true if the player can open the lock, false otherwise.
     */
    public boolean canOpenLock(Lock lock) {
        ErrorHandler.debug("Checking if player can open lock for job: " + (lock.getJobName() != null ? lock.getJobName() : "none"));

        UUID playerUUID = getPlayerUUID();
        if (playerUUID == null) {
            ErrorHandler.debug("Player UUID is null, cannot open lock");
            return false;
        }

        if (playerMode == PlayerMode.ADMIN) {
            ErrorHandler.debug("Player is in ADMIN mode, can open lock");
            return true;
        }

        boolean accessibleProperty = false;
        try {
            accessibleProperty = RPUniverse.getInstance().getConfiguration().getBoolean("properties.unlockedByDefault");
            ErrorHandler.debug("Properties unlocked by default: " + accessibleProperty);
        } catch (Exception e) {
            ErrorHandler.severe("Error when checking config for: properties.unlockedByDefault", e);
        }

        if (RPUniverse.getInstance().getPropertyManager().isExplorableByLock(lock)) {
            ErrorHandler.debug("Lock is for an explorable property, access: " + accessibleProperty);
            return accessibleProperty;
        }

        if (lock.getOwners() != null && lock.getOwners().contains(playerUUID.toString())) {
            ErrorHandler.debug("Player is an owner of the lock, access granted");
            return true;
        }

        if (lock.getJobName() != null && selectedPlayerJob != null && selectedPlayerJob.getName().equals(lock.getJobName())) {
            ErrorHandler.debug("Lock is for job: " + lock.getJobName() + ", player's selected job matches");

            Position playerPos = selectedPlayerJob.getPlayerPosition(playerUUID);

            if(playerPos == null) {
                ErrorHandler.debug("Player position in job is null, access denied");
                return false;
            }

            if(playerPos.isBoss()) {
                ErrorHandler.debug("Player is a boss in the job, access granted");
                return true;
            }

            boolean hasAccess = playerPos.getWorkingStepPermissionLevel() >= lock.getMinWorkingLevel();
            ErrorHandler.debug("Player working level: " + playerPos.getWorkingStepPermissionLevel() + 
                              ", required level: " + lock.getMinWorkingLevel() + 
                              ", access: " + hasAccess);
            return hasAccess;
        } else {
            if (lock.getJobName() != null) {
                ErrorHandler.debug("Lock is for job: " + lock.getJobName() + 
                                  ", but player's selected job is " + 
                                  (selectedPlayerJob == null ? "null" : selectedPlayerJob.getName()));
            }
        }

        ErrorHandler.debug("No access conditions met, access denied");
        return false;
    }
}
