package me.fami6xx.rpuniverse.core.locks;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.fami6xx.rpuniverse.core.jobs.Job;

/**
 * Lock class
 */
public class Lock {
    private Location location;
    private Material shownMaterial;
    private String owner;
    private String jobName;
    private int minWorkingLevel = 0;

    public Lock(Location location, String owner, String jobName, int minWorkingLevel, Material shownMaterial) {
        this.location = location;
        this.owner = owner;
        this.jobName = jobName;
        this.minWorkingLevel = minWorkingLevel;
        this.shownMaterial = shownMaterial;
    }

    public Lock(Location location, Material shownMaterial, Player owner) {
        this.location = location;
        this.shownMaterial = shownMaterial;
        this.owner = owner.getName();
    }

    public Lock(Location location, Material shownMaterial, Job job, int minWorkingLevel) {
        this.location = location;
        this.shownMaterial = shownMaterial;
        this.jobName = job.getName();
        this.minWorkingLevel = minWorkingLevel;
    }
    
    public Lock(Location location, Material shownMaterial, String jobName, int minWorkingLevel) {
        this.location = location;
        this.shownMaterial = shownMaterial;
        this.jobName = jobName;
        this.minWorkingLevel = minWorkingLevel;
    }

    /**
     * Get the location of the lock
     * @return The location of the lock
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the owner of the lock
     * @return The owner of the lock
     */
    public @Nullable String getOwner() {
        return owner;
    }

    /**
     * Get the job name of the lock
     * @return The job name of the lock
     */
    public @Nullable String getJobName() {
        return jobName;
    }

    /**
     * Get the minimum working level of the lock
     * @return The minimum working level of the lock
     */
    public int getMinWorkingLevel() {
        return minWorkingLevel;
    }

    /**
     * Get the material shown in the All Locks menu
     * @return The material shown in the All Locks menu
     */
    public Material getShownMaterial() {
        return shownMaterial;
    }
}

