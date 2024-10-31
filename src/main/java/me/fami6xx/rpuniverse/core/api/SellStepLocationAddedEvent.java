package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.jobs.SellStep;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SellStepLocationAddedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final SellStep step;
    private final Location location;

    public SellStepLocationAddedEvent(SellStep step, Location location) {
        this.step = step;
        this.location = location;
    }

    public SellStep getSellStep() {
        return step;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
