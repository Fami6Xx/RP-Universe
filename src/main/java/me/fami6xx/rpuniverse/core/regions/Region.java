package me.fami6xx.rpuniverse.core.regions;

import org.bukkit.Location;

import java.util.UUID;

/**
 * Represents a region (cuboid) with two corners.
 */
public class Region {
    private final UUID regionId;        // Unique ID for the region
    private String name;                // Region name (user-defined)
    private transient Location corner1;           // One corner of the cuboid
    private transient Location corner2;           // Opposite corner of the cuboid

    /**
     * Create a new Region.
     *
     * @param name    Region's name
     * @param corner1 First corner (Location)
     * @param corner2 Second corner (Location)
     */
    public Region(String name, Location corner1, Location corner2) {
        this.regionId = UUID.randomUUID();
        this.name = name;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    /**
     * Used by Gson or internal re-creation when loading from disk.
     *
     * @param regionId Unique ID for the region
     * @param name     Region's name
     * @param corner1  First corner (Location)
     * @param corner2  Second corner (Location)
     */
    public Region(UUID regionId, String name, Location corner1, Location corner2) {
        this.regionId = regionId;
        this.name = name;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    /**
     * Gets the unique ID of the region.
     *
     * @return the unique ID of the region
     */
    public UUID getRegionId() {
        return regionId;
    }

    /**
     * Gets the name of the region.
     *
     * @return the name of the region
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the region.
     *
     * @param name the new name of the region
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the first corner of the region.
     *
     * @return the first corner of the region
     */
    public Location getCorner1() {
        return corner1;
    }

    /**
     * Sets the first corner of the region.
     *
     * @param corner1 the new first corner of the region
     */
    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    /**
     * Gets the second corner of the region.
     *
     * @return the second corner of the region
     */
    public Location getCorner2() {
        return corner2;
    }

    /**
     * Sets the second corner of the region.
     *
     * @param corner2 the new second corner of the region
     */
    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    /**
     * Helper to get the minimum corner for x, y, z.
     *
     * @return the minimum corner for x, y, z
     */
    public Location getMinCorner() {
        return new Location(
                corner1.getWorld(),
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
    }

    /**
     * Helper to get the maximum corner for x, y, z.
     *
     * @return the maximum corner for x, y, z
     */
    public Location getMaxCorner() {
        return new Location(
                corner1.getWorld(),
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
    }
}