package me.fami6xx.rpuniverse.core.misc;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
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

    private final UUID dataUUID;
    private String playerUUID;
    private String selectedJobName;
    private String tag;
    private List<String> playerJobNames;

    private int foodLevel = 100;
    private int waterLevel = 100;
    private int poopLevel = 100;
    private int peeLevel = 100;

    public PlayerData(Player bindedPlayer) {
        this.bindedPlayer = bindedPlayer;
        this.bindedOfflinePlayer = null;
        this.dataUUID = UUID.randomUUID();
    }

    public PlayerData(OfflinePlayer bindedOfflinePlayer) {
        this.bindedOfflinePlayer = bindedOfflinePlayer;
        this.bindedPlayer = null;
        this.dataUUID = UUID.randomUUID();
    }

    public PlayerData(String playerUUID){
        if(playerUUID == null) throw new IllegalArgumentException("Player UUID cannot be null!");
        this.playerUUID = playerUUID;
        this.dataUUID = UUID.randomUUID();
    }

    /**
     * Adds a job to the player's list of jobs.
     *
     * @param job The job to be added.
     */
    public void addJob(Job job){
        playerJobs.add(job);
        if(selectedPlayerJob == null) {
            selectedPlayerJob = job;
            RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);
        }
    }

    /**
     * Removes a job from the player's list of jobs.
     *
     * @param job The job to be removed.
     */
    public void removeJob(Job job){
        playerJobs.remove(job);
        if(selectedPlayerJob == job){
            if(!playerJobs.isEmpty()) selectedPlayerJob = playerJobs.get(0);
            else selectedPlayerJob = null;

            RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);
        }
    }

    /**
     * Sets the selected player job.
     *
     * @param job The job to be set as the selected player job.
     */
    public void setSelectedPlayerJob(Job job){
        if(playerJobs.contains(job)) {
            this.selectedPlayerJob = job;

            RPUniverse.getInstance().getBossBarHandler().updateBossBar(bindedPlayer);
        }else throw new IllegalArgumentException("The player does not have the job " + job.getName());
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
     *
     * @return the list of jobs for the player data
     */
    public List<Job> getPlayerJobs(){
        return playerJobs;
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
        playerUUID = getPlayerUUID().toString();

        selectedJobName = selectedPlayerJob == null ? null : selectedPlayerJob.getName();

        playerJobNames = new ArrayList<>();
        for(Job job : playerJobs)
            playerJobNames.add(job.getName());
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
        if(Bukkit.getPlayer(playerUUID) != null) this.bindedPlayer = Bukkit.getPlayer(playerUUID);
        else this.bindedOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

        if(selectedJobName != null)
            selectedPlayerJob = Job.getJob(selectedJobName);

        playerJobs = new ArrayList<>();
        if(playerJobNames != null) {
            for (String jobName : playerJobNames) {
                playerJobs.add(Job.getJob(jobName));
            }
        }

        this.playerMode = PlayerMode.USER;
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
        this.waterLevel = waterLevel;
    }

    /**
     * Gets the player's poop level
     * @return the player's poop level (Higher is better)
     */
    public int getPoopLevel() {
        return poopLevel;
    }

    /**
     * Sets the player's poop level
     *
     * @param poopLevel the player's poop level (Higher is better)
     */
    public void setPoopLevel(int poopLevel) {
        if (poopLevel > 100) poopLevel = 100;
        this.poopLevel = poopLevel;
    }

    /**
     * Gets the player's pee level
     *
     * @return the player's pee level (Higher is better)
     */
    public int getPeeLevel() {
        return peeLevel;
    }

    /**
     * Sets the player's pee level
     *
     * @param peeLevel the player's pee level (Higher is better)
     */
    public void setPeeLevel(int peeLevel) {
        if (peeLevel > 100) peeLevel = 100;
        this.peeLevel = peeLevel;
    }
}
