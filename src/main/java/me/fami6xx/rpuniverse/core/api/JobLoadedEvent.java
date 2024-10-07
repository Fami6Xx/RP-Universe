package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobLoadedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Job job;

    public JobLoadedEvent(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
