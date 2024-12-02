package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.locks.Lock;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is called when a player tries to open a locked block but is denied.
 */
public class LockDeniedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Lock lock;
    private final Player player;

    /**
     * Constructs a new <code>LockDeniedEvent</code>.
     *
     * @param lock the lock that was denied access to
     * @param player the player who was denied access
     */
    public LockDeniedEvent(Lock lock, Player player) {
        this.lock = lock;
        this.player = player;
    }

    /**
     * Gets the lock that was interacted with.
     *
     * @return the lock that was denied access to
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * Gets the player who was denied access.
     *
     * @return the player who was denied access
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
}