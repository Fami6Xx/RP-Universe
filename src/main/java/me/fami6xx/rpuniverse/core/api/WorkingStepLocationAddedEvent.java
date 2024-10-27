package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WorkingStepLocationAddedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final WorkingStep step;
    private final Location location;

    public WorkingStepLocationAddedEvent(WorkingStep step, Location location) {
        this.step = step;
        this.location = location;
    }

    public WorkingStep getWorkingStep() {
        return step;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
