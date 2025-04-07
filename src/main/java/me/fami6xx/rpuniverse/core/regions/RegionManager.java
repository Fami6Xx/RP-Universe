package me.fami6xx.rpuniverse.core.regions;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.gson.*;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.RegionBlockBreakEvent;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;

/**
 * Manages all regions in memory and provides save/load functionality via manual JSON
 * (including corner1 and corner2). Also provides a method to get all regions containing a location.
 */
public class RegionManager implements Listener {

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

    // Region visualization settings from config
    private double visualizationStep = 0.5;
    private int maxRenderDistance = 50;
    private boolean edgeOnly = true;
    private Color particleColor = Color.BLACK;

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
        // Load visualization settings from config
        loadVisualizationSettings();

        if (!regionFile.exists()) {
            ErrorHandler.info("No regions.json found; starting with an empty region list.");
            startShowingTask(); // Still start the task even with no regions
            Bukkit.getPluginManager().registerEvents(this, RPUniverse.getInstance());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(regionFile))) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                ErrorHandler.warning("regions.json is not a JSON array! Skipping load.");
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

            Bukkit.getPluginManager().registerEvents(this, RPUniverse.getInstance());

            ErrorHandler.info("Loaded " + regions.size() + " region(s) from regions.json.");
        } catch (Exception e) {
            ErrorHandler.severe("Failed to load regions.json!", e);
        }
    }

    /**
     * Loads region visualization settings from the configuration.
     */
    private void loadVisualizationSettings() {
        FileConfiguration config = RPUniverse.getInstance().getConfig();

        if (config.contains("regionVisualization")) {
            visualizationStep = config.getDouble("regionVisualization.step", 0.5);
            maxRenderDistance = config.getInt("regionVisualization.maxRenderDistance", 50);
            edgeOnly = config.getBoolean("regionVisualization.edgeOnly", true);

            String colorName = config.getString("regionVisualization.particleColor", "BLACK");
            try {
                // Try to parse the color name to a Color object
                java.lang.reflect.Field field = Color.class.getField(colorName);
                particleColor = (Color) field.get(null);
            } catch (Exception e) {
                ErrorHandler.warning("Invalid particle color: " + colorName + ". Using BLACK instead.");
                particleColor = Color.BLACK;
            }

            ErrorHandler.debug("Loaded region visualization settings: step=" + visualizationStep +
                             ", maxRenderDistance=" + maxRenderDistance + 
                             ", edgeOnly=" + edgeOnly + 
                             ", particleColor=" + colorName);
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
            ErrorHandler.severe("Failed to save regions.json!", e);
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
     * Draws a bounding box for the specified region using particles.
     * Optimized with distance-based rendering and configuration options.
     */
    private void drawRegionBoundingBox(Player player, Region region) {
        World world = region.getCorner1().getWorld();
        if (world == null || !world.equals(player.getWorld())) {
            return; // Only show if player is in same world, or else it won't be visible
        }

        // Get the center of the region for distance calculation
        Location min = region.getMinCorner();
        Location max = region.getMaxCorner();
        Location center = new Location(
            world,
            (min.getX() + max.getX()) / 2,
            (min.getY() + max.getY()) / 2,
            (min.getZ() + max.getZ()) / 2
        );

        // Check if player is within render distance (if maxRenderDistance > 0)
        if (maxRenderDistance > 0) {
            double distance = player.getLocation().distance(center);
            if (distance > maxRenderDistance) {
                return; // Skip rendering if player is too far away
            }
        }

        double minX = Math.min(min.getBlockX(), max.getBlockX());
        double minY = Math.min(min.getBlockY(), max.getBlockY());
        double minZ = Math.min(min.getBlockZ(), max.getBlockZ());
        double maxX = Math.max(min.getBlockX(), max.getBlockX()) + 1;
        double maxY = Math.max(min.getBlockY(), max.getBlockY()) + 1;
        double maxZ = Math.max(min.getBlockZ(), max.getBlockZ()) + 1;

        if (edgeOnly) {
            // Draw only the 12 edges of the bounding box using the drawLine helper method
            // Bottom face edges (4)
            drawLine(player, world, minX, minY, minZ, maxX, minY, minZ);
            drawLine(player, world, maxX, minY, minZ, maxX, minY, maxZ);
            drawLine(player, world, maxX, minY, maxZ, minX, minY, maxZ);
            drawLine(player, world, minX, minY, maxZ, minX, minY, minZ);

            // Top face edges (4)
            drawLine(player, world, minX, maxY, minZ, maxX, maxY, minZ);
            drawLine(player, world, maxX, maxY, minZ, maxX, maxY, maxZ);
            drawLine(player, world, maxX, maxY, maxZ, minX, maxY, maxZ);
            drawLine(player, world, minX, maxY, maxZ, minX, maxY, minZ);

            // Vertical edges (4)
            drawLine(player, world, minX, minY, minZ, minX, maxY, minZ);
            drawLine(player, world, maxX, minY, minZ, maxX, maxY, minZ);
            drawLine(player, world, maxX, minY, maxZ, maxX, maxY, maxZ);
            drawLine(player, world, minX, minY, maxZ, minX, maxY, maxZ);
        } else {
            // Draw the entire bounding box (including faces)
            // Use a step size based on the visualization step setting
            for (double x = minX; x <= maxX; x += visualizationStep) {
                for (double y = minY; y <= maxY; y += visualizationStep) {
                    for (double z = minZ; z <= maxZ; z += visualizationStep) {
                        // Only draw particles on the surface of the box
                        boolean onSurface = (
                            Math.abs(x - minX) < 0.01 || Math.abs(x - maxX) < 0.01 ||
                            Math.abs(y - minY) < 0.01 || Math.abs(y - maxY) < 0.01 ||
                            Math.abs(z - minZ) < 0.01 || Math.abs(z - maxZ) < 0.01
                        );

                        if (onSurface) {
                            Location loc = new Location(world, x, y, z);
                            new ParticleBuilder(Particle.REDSTONE)
                                .color(particleColor)
                                .count(0)
                                .receivers(player)
                                .location(loc)
                                .spawn();
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method to draw a line of particles between two points.
     * Used for optimized edge rendering.
     */
    private void drawLine(Player player, World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
        int points = Math.max(2, (int) (distance / visualizationStep));

        for (int i = 0; i < points; i++) {
            double ratio = (double) i / (points - 1);
            double x = x1 + (x2 - x1) * ratio;
            double y = y1 + (y2 - y1) * ratio;
            double z = z1 + (z2 - z1) * ratio;

            Location loc = new Location(world, x, y, z);
            new ParticleBuilder(Particle.REDSTONE)
                    .color(particleColor)
                    .count(0)
                    .receivers(player)
                    .location(loc)
                    .spawn();
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        UUID playerId = e.getPlayer().getUniqueId();
        viewingRegions.remove(playerId);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        List<Region> inRegions = getRegionsContainingLocation(loc);
        if (inRegions.isEmpty()) {
            return;
        }

        Bukkit.getPluginManager().callEvent(new RegionBlockBreakEvent(inRegions, e));
    }
}
