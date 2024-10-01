package me.fami6xx.rpuniverse.core.locks;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.fami6xx.rpuniverse.core.jobs.Job;

/**
 * Lock class
 */
public class Lock {
    private Location location;
    private Material shownMaterial;
    private List<String> owners;
    private String jobName;
    private int minWorkingLevel = 0;
    private final UUID uuid = UUID.randomUUID();

    public Lock(Location location, List<String> owners, String jobName, int minWorkingLevel, Material shownMaterial) {
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.owners = owners;
        this.jobName = jobName;
        this.minWorkingLevel = minWorkingLevel;
        this.shownMaterial = shownMaterial;
    }

    public Lock(Location location, Material shownMaterial, Player owner) {
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.shownMaterial = shownMaterial;
        this.owners = Arrays.asList(owner.getUniqueId().toString());
    }

    public Lock(Location location, Material shownMaterial, Job job, int minWorkingLevel) {
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.shownMaterial = shownMaterial;
        this.jobName = job.getName();
        this.minWorkingLevel = minWorkingLevel;
    }
    
    public Lock(Location location, Material shownMaterial, String jobName, int minWorkingLevel) {
        this.location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        this.shownMaterial = shownMaterial;
        this.jobName = jobName;
        this.minWorkingLevel = minWorkingLevel;
    }

    /**
     * Get the location of the lock
     * @return The location of the lock
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Get the owners of the lock
     * @return The list of owners of the lock
     */
    public @Nullable List<String> getOwners() {
        return owners;
    }

    /**
     * Add an owner to the lock
     * @param player The player to add as an owner
     */
    public void addOwner(Player player) {
        this.owners.add(player.getUniqueId().toString());
    }

    /**
     * Remove an owner from the lock
     * @param player The player to remove as an owner
     */
    public void removeOwner(Player player) {
        this.owners.remove(player.getUniqueId().toString());
    }

    /**
     * Set owners of the lock
     * @param owners The list of owners to set
     */
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    /**
     * Get the job name of the lock
     * @return The job name of the lock
     */
    public @Nullable String getJobName() {
        return jobName;
    }

    /**
     * Set the job name of the lock
     * @param jobName The job name to set
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }


    /**
     * Get the minimum working level of the lock
     * @return The minimum working level of the lock
     */
    public int getMinWorkingLevel() {
        return minWorkingLevel;
    }

    /**
     * Set the minimum working level of the lock
     * @param minWorkingLevel The minimum working level to set
     */
    public void setMinWorkingLevel(int minWorkingLevel) {
        this.minWorkingLevel = minWorkingLevel;
    }

    /**
     * Get the material shown in the All Locks menu
     * @return The material shown in the All Locks menu
     */
    public Material getShownMaterial() {
        return shownMaterial;
    }

    /**
     * Set the material shown in the All Locks menu
     * @param shownMaterial The material shown in the All Locks menu
     */
    public void setShownMaterial(Material shownMaterial) {
        this.shownMaterial = shownMaterial;
    }

    /**
     * Get the UUID of the lock
     * @return The UUID of the lock
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Get the owners of the lock as a string
     * @return The owners of the lock as a string
     */
    public String getOwnersAsString() {
        StringBuilder owners = new StringBuilder();
        if (this.getOwners() == null || this.getOwners().isEmpty()) {
            owners = new StringBuilder("None");
        } else if (this.getOwners().size() > 1){
            for (int i = 0; i < this.getOwners().size(); i++) {
                String uuid = this.getOwners().get(i);
                OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                owners.append(offlineOwner.getName());
                if (i < this.getOwners().size() - 1) {
                    owners.append(", ");
                }
            }
        } else {
            OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(UUID.fromString(this.getOwners().get(0)));
            owners = new StringBuilder(Objects.requireNonNull(offlineOwner.getName()));
        }

        return owners.toString();
    }
}

