package me.fami6xx.rpuniverse.core.misc;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
    private transient boolean isTagVisible = true;

    private final UUID dataUUID;
    private String playerUUID;
    private String selectedJobName;
    private String tag;
    private List<String> playerJobNames;

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
        if(selectedPlayerJob == null) selectedPlayerJob = job;
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
        }
    }

    /**
     * Sets the player mode.
     *
     * @param playerMode The player mode to be set.
     */
    public void setPlayerMode(PlayerMode playerMode){
        this.playerMode = playerMode;

        RPUniverse.getInstance().getHoloAPI().getVisibilityHandler().queue.add(() -> RPUniverse.getInstance().getHoloAPI().getVisibilityHandler().updateHologramsPlayerMode(bindedPlayer, this));
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
        return isTagVisible;
    }

    /**
     * Sets the visibility of the tag for the player data.
     *
     * @param tagVisible The visibility of the tag to be set.
     */
    public void setTagVisible(boolean tagVisible) {
        isTagVisible = tagVisible;
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

    public void updatePlayer(Player player){
        this.bindedPlayer = player;
        this.bindedOfflinePlayer = null;
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
}
