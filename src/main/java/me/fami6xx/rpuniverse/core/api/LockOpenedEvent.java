package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.locks.Lock;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is called when a lock is opened.
 */
public class LockOpenedEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Lock lock;
    private final Player player;
    private boolean cancelled = false;

    /**
     * Constructs a new LockOpenedEvent.
     *
     * @param lock the lock that was opened
     * @param player the player who opened the lock
     */
    public LockOpenedEvent(Lock lock, Player player) {
        this.lock = lock;
        this.player = player;
    }

    /**
     * Gets the lock that was opened.
     *
     * @return the opened lock
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * Gets the player who opened the lock.
     *
     * @return the player who opened the lock
     */
    public Player getPlayer() {
        return player;
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
     * @param cancel true to cancel the event, false to uncancel
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}