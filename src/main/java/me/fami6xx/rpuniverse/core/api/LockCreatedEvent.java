package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.locks.Lock;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event that is called when a lock is created.
 */
public class LockCreatedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Lock lock;

    /**
     * Constructs a new LockCreatedEvent.
     *
     * @param lock the lock that was created
     */
    public LockCreatedEvent(Lock lock) {
        this.lock = lock;
    }

    /**
     * Gets the lock that was created.
     *
     * @return the created lock
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return the list of handlers
     */
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