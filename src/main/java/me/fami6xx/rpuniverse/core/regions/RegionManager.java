package me.fami6xx.rpuniverse.core.regions;

import com.google.gson.*;
import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages all regions in memory and provides save/load functionality via manual JSON
 * (including corner1 and corner2). Also provides a method to get all regions containing a location.
 */
public class RegionManager {

    private static RegionManager instance;

    // Where we store all regions in memory
    private final Map<UUID, Region> regions = new HashMap<>();

    // File where we'll save and load region data
    private final File regionFile;

    // We'll still use Gson, but do corners manually since they're transient
    private final Gson gson;

    private RegionManager() {
        File dataFolder = RPUniverse.getInstance().getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        // We'll store all regions in this JSON file
        regionFile = new File(dataFolder, "regions.json");

        // Prepare a basic Gson (no LocationAdapter needed, corners are handled manually)
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Get the singleton instance of RegionManager.
     */
    public static RegionManager getInstance() {
        if (instance == null) {
            instance = new RegionManager();
        }
        return instance;
    }

    /**
     * Load all regions from file (if any), manually parsing corner1 and corner2.
     */
    public void init() {
        if (!regionFile.exists()) {
            RPUniverse.getInstance().getLogger().info("No regions.json found; starting with an empty region list.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(regionFile))) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                RPUniverse.getInstance().getLogger().warning("regions.json is not a JSON array! Skipping load.");
                return;
            }

            JsonArray array = root.getAsJsonArray();
            for (JsonElement element : array) {
                if (!element.isJsonObject()) continue;
                JsonObject obj = element.getAsJsonObject();

                // Extract basic fields
                UUID regionId = UUID.fromString(obj.get("regionId").getAsString());
                String name = obj.get("name").getAsString();

                // Extract corner1
                JsonObject c1 = obj.getAsJsonObject("corner1");
                Location corner1 = jsonToLocation(c1);

                // Extract corner2
                JsonObject c2 = obj.getAsJsonObject("corner2");
                Location corner2 = jsonToLocation(c2);

                // Create and store the region
                Region region = new Region(regionId, name, corner1, corner2);
                regions.put(region.getRegionId(), region);
            }

            RPUniverse.getInstance().getLogger().info("Loaded " + regions.size() + " region(s) from regions.json.");
        } catch (Exception e) {
            RPUniverse.getInstance().getLogger().log(Level.SEVERE, "Failed to load regions.json!", e);
        }
    }

    /**
     * Save all regions to file, manually writing out corner1 and corner2 in JSON.
     */
    public void saveAllRegions() {
        JsonArray array = new JsonArray();

        for (Region region : regions.values()) {
            JsonObject obj = new JsonObject();

            // Basic fields
            obj.addProperty("regionId", region.getRegionId().toString());
            obj.addProperty("name", region.getName());

            // corner1
            JsonObject c1 = locationToJson(region.getCorner1());
            obj.add("corner1", c1);

            // corner2
            JsonObject c2 = locationToJson(region.getCorner2());
            obj.add("corner2", c2);

            array.add(obj);
        }

        try (Writer writer = new FileWriter(regionFile)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            RPUniverse.getInstance().getLogger().log(Level.SEVERE, "Failed to save regions.json!", e);
        }
    }

    /**
     * Create and store a region in memory. Does NOT auto-save to file.
     */
    public Region createRegion(String name, Location corner1, Location corner2) {
        Region region = new Region(name, corner1, corner2);
        regions.put(region.getRegionId(), region);
        return region;
    }

    /**
     * Deletes a region from memory by its UUID. Does NOT auto-save to file.
     *
     * @return true if the region was removed, false if it didn't exist
     */
    public boolean deleteRegion(UUID regionId) {
        return (regions.remove(regionId) != null);
    }

    /**
     * Finds a region by name. Returns the first match or null if none found.
     */
    public Region getRegionByName(String name) {
        for (Region region : regions.values()) {
            if (region.getName().equalsIgnoreCase(name)) {
                return region;
            }
        }
        return null;
    }

    /**
     * Get a region by its UUID.
     */
    public Region getRegionById(UUID regionId) {
        return regions.get(regionId);
    }

    /**
     * Returns all current regions.
     */
    public Collection<Region> getAllRegions() {
        return regions.values();
    }

    /**
     * Returns all regions that contain the given location (same world, x/y/z in range).
     */
    public List<Region> getRegionsContainingLocation(Location loc) {
        List<Region> inRegions = new ArrayList<>();
        if (loc == null) {
            return inRegions; // or throw an exception if desired
        }

        for (Region region : regions.values()) {
            // Must be same world
            if (!loc.getWorld().equals(region.getCorner1().getWorld())) {
                continue;
            }

            Location min = region.getMinCorner();
            Location max = region.getMaxCorner();

            double lx = loc.getX();
            double ly = loc.getY();
            double lz = loc.getZ();

            if (lx >= min.getX() && lx <= max.getX() &&
                    ly >= min.getY() && ly <= max.getY() &&
                    lz >= min.getZ() && lz <= max.getZ()) {
                inRegions.add(region);
            }
        }
        return inRegions;
    }

    /**
     * Helper to turn a Location into a JsonObject manually.
     */
    private JsonObject locationToJson(Location loc) {
        JsonObject obj = new JsonObject();
        if (loc != null && loc.getWorld() != null) {
            obj.addProperty("world", loc.getWorld().getName());
            obj.addProperty("x", loc.getX());
            obj.addProperty("y", loc.getY());
            obj.addProperty("z", loc.getZ());
            obj.addProperty("yaw", loc.getYaw());
            obj.addProperty("pitch", loc.getPitch());
        } else {
            // If no location or world is invalid, store defaults
            obj.addProperty("world", "world");
            obj.addProperty("x", 0.0);
            obj.addProperty("y", 0.0);
            obj.addProperty("z", 0.0);
            obj.addProperty("yaw", 0.0f);
            obj.addProperty("pitch", 0.0f);
        }
        return obj;
    }

    /**
     * Helper to turn a JsonObject into a Location manually.
     */
    private Location jsonToLocation(JsonObject obj) {
        if (obj == null) return null;

        String worldName = obj.has("world") ? obj.get("world").getAsString() : "world";
        World w = Bukkit.getWorld(worldName);
        if (w == null) {
            // If world does not exist, default to "world" if that exists, or null
            w = Bukkit.getWorld("world");
        }

        double x = obj.has("x") ? obj.get("x").getAsDouble() : 0.0;
        double y = obj.has("y") ? obj.get("y").getAsDouble() : 0.0;
        double z = obj.has("z") ? obj.get("z").getAsDouble() : 0.0;
        float yaw = obj.has("yaw") ? obj.get("yaw").getAsFloat() : 0.0f;
        float pitch = obj.has("pitch") ? obj.get("pitch").getAsFloat() : 0.0f;

        return new Location(w, x, y, z, yaw, pitch);
    }
}
