package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.regions.Region;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Event that is called when a player tries to break a block in a region.
 */
public class RegionBlockBreakEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final List<Region> regions;
    private final BlockBreakEvent event;

    /**
     * Constructs a new <code>RegionBlockBreakEvent</code>.
     *
     * @param regions the regions where the block break attempt occurred
     * @param event the original BlockBreakEvent
     */
    public RegionBlockBreakEvent(List<Region> regions, BlockBreakEvent event) {
        this.regions = regions;
        this.event = event;
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
     * Gets the region where the block break attempt occurred.
     *
     * @return the region
     */
    public List<Region> getRegions() {
        return new ArrayList<>(regions);
    }

    /**
     * Gets the original BlockBreakEvent.
     *
     * @return the original BlockBreakEvent
     */
    public BlockBreakEvent getEvent() {
        return event;
    }
}