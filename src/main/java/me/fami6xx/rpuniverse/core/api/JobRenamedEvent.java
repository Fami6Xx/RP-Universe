package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobRenamedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Job job;
    private final String oldName;
    private final String newName;

    public JobRenamedEvent(Job job, String oldName, String newName) {
        this.job = job;
        this.oldName = oldName;
        this.newName = newName;
    }

    public Job getJob() {
        return job;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
