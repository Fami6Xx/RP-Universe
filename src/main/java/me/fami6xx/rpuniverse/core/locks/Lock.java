package me.fami6xx.rpuniverse.core.locks;

import java.util.Arrays;
import java.util.List;

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
    private List<String> owners;
    private String jobName;
    private int minWorkingLevel = 0;

    public Lock(Location location, List<String> owners, String jobName, int minWorkingLevel, Material shownMaterial) {
        this.location = location;
        this.owners = owners;
        this.jobName = jobName;
        this.minWorkingLevel = minWorkingLevel;
        this.shownMaterial = shownMaterial;
    }

    public Lock(Location location, Material shownMaterial, Player owner) {
        this.location = location;
        this.shownMaterial = shownMaterial;
        this.owners = Arrays.asList(owner.getUniqueId().toString());
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
     * Get the owners of the lock
     * @return The list of owners of the lock
     */
    public @Nullable List<String> getOwners() {
        return owners;
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

