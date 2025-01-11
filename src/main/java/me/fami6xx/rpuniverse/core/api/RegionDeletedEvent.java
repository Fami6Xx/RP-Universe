package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.regions.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is called when a region is deleted.
 */
public class RegionDeletedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Region region;

    /**
     * Constructs a new <code>RegionDeletedEvent</code>.
     *
     * @param region the region that was deleted
     */
    public RegionDeletedEvent(Region region) {
        this.region = region;
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
     * Gets the region that was deleted.
     *
     * @return the region
     */
    public Region getRegion() {
        return region;
    }
}