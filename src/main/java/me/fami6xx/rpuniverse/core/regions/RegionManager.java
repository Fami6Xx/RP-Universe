package me.fami6xx.rpuniverse.core.regions;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.*;
import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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

    // Which players are currently viewing which regions:
    // Player UUID -> set of Region UUIDs
    private final Map<UUID, Set<UUID>> viewingRegions = new HashMap<>();

    // File where we'll save and load region data
    private final File regionFile;

    private final Gson gson;
    private BukkitTask showTask;

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

            startShowingTask();

            RPUniverse.getInstance().getLogger().info("Loaded " + regions.size() + " region(s) from regions.json.");
        } catch (Exception e) {
            RPUniverse.getInstance().getLogger().log(Level.SEVERE, "Failed to load regions.json!", e);
        }
    }

    /**
     * Saves all regions and cancels the repeating particle task.
     */
    public void shutdown() {
        saveAllRegions();
        if (showTask != null) {
            showTask.cancel();
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
     * Shows region boundaries to a player (repeats via the scheduled task).
     */
    public void showRegion(Player player, Region region) {
        if (player == null || region == null) return;
        viewingRegions.putIfAbsent(player.getUniqueId(), new HashSet<>());
        viewingRegions.get(player.getUniqueId()).add(region.getRegionId());
    }

    /**
     * Hides region boundaries (particles) for a player.
     */
    public void hideRegion(Player player, Region region) {
        if (player == null || region == null) return;
        if (viewingRegions.containsKey(player.getUniqueId())) {
            viewingRegions.get(player.getUniqueId()).remove(region.getRegionId());
        }
    }

    /**
     * Checks if the player is currently showing a given region.
     */
    public boolean isShowingRegion(Player player, Region region) {
        if (player == null || region == null) return false;
        Set<UUID> set = viewingRegions.get(player.getUniqueId());
        if (set == null) return false;
        return set.contains(region.getRegionId());
    }

    /**
     * Starts a repeating task that draws all "shown" regions for each player.
     * You can change the period to 5, 10, or 20 ticks, depending on desired frequency.
     */
    private void startShowingTask() {
        showTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Set<UUID> regionIds = viewingRegions.get(player.getUniqueId());
                    if (regionIds == null || regionIds.isEmpty()) {
                        continue;
                    }
                    // For each region the player is viewing, draw its bounding box
                    for (UUID regionId : regionIds) {
                        Region region = regions.get(regionId);
                        if (region != null && region.getCorner1() != null && region.getCorner2() != null) {
                            drawRegionBoundingBox(player, region);
                        }
                    }
                }
            }
        }.runTaskTimer(RPUniverse.getInstance(), 0L, 10L); // draws every 10 ticks
    }

    /**
     * Draws a bounding box for the specified region using REDSTONE particles.
     * You can adapt color, increments, etc.
     */
    private void drawRegionBoundingBox(Player player, Region region) {
        World world = region.getCorner1().getWorld();
        if (world == null || !world.equals(player.getWorld())) {
            return; // Only show if player is in same world, or else it won't be visible
        }

        Location min = region.getMinCorner();
        Location max = region.getMaxCorner();

        double minX = Math.min(min.getBlockX(), max.getBlockX());
        double minY = Math.min(min.getBlockY(), max.getBlockY());
        double minZ = Math.min(min.getBlockZ(), max.getBlockZ());
        double maxX = Math.max(min.getBlockX(), max.getBlockX()) + 1;
        double maxY = Math.max(min.getBlockY(), max.getBlockY()) + 1;
        double maxZ = Math.max(max.getBlockZ(), max.getBlockZ()) + 1;

        double step = 0.25;
        for (double x = minX; x <= maxX; x += step) {
            for (double y = minY; y <= maxY; y += step) {
                for (double z = minZ; z <= maxZ; z += step) {
                    boolean edge = (
                            (x == minX || x == maxX) && (y == minY || y == maxY)
                    ) || (
                            (x == minX || x == maxX) && (z == minZ || z == maxZ)
                    ) || (
                            (y == minY || y == maxY) && (z == minZ || z == maxZ)
                    );

                    if (edge) {
                        Location newLoc = new Location(world, x, y, z);
                        new ParticleBuilder(Particle.REDSTONE)
                                .color(Color.BLACK)
                                .count(0)
                                .receivers(player)
                                .location(newLoc)
                                .spawn();
                    }
                }
            }
        }
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
