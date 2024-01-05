package me.fami6xx.rpuniverse.core.jobs.commands.createJob.utils;

import org.bukkit.Location;

import java.util.UUID;

public class CreateJobStorage {
    private final UUID playerUUID;
    private String jobName;
    private Location bossMenuLocation;

    public CreateJobStorage(UUID playerUUID){
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setBossMenuLocation(Location bossMenuLocation) {
        this.bossMenuLocation = bossMenuLocation;
    }

    public Location getBossMenuLocation() {
        return bossMenuLocation;
    }
}