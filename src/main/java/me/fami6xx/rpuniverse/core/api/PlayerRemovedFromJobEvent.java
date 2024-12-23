package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is called when a player is removed from a job.
 */
public class PlayerRemovedFromJobEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Job job;
    private final Player player;
    private boolean cancelled = false;

    /**
     * Constructs a new PlayerRemovedFromJobEvent.
     *
     * @param job the job from which the player was removed
     * @param player the player who was removed from the job
     */
    public PlayerRemovedFromJobEvent(Job job, Player player) {
        this.job = job;
        this.player = player;
    }

    /**
     * Checks if the event is cancelled.
     *
     * @return true if the event is cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param b true to cancel the event, false to uncancel
     */
    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return the list of handlers
     */
    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the static handler list for this event.
     *
     * @return the static handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Gets the job from which the player was removed.
     *
     * @return the job
     */
    public Job getJob() {
        return job;
    }

    /**
     * Gets the player who was removed from the job.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
}