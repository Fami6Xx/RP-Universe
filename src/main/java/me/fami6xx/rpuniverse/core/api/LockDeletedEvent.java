package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.locks.Lock;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is called when a lock is deleted.
 */
public class LockDeletedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Lock lock;

    private boolean cancelled = false;

    /**
     * Constructs a new LockDeletedEvent.
     *
     * @param lock the lock that was deleted
     */
    public LockDeletedEvent(Lock lock) {
        this.lock = lock;
    }

    /**
     * Gets the lock that was deleted.
     *
     * @return the deleted lock
     */
    public Lock getLock() {
        return lock;
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
     * @param b true to cancel the event, false to not cancel the event
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
}