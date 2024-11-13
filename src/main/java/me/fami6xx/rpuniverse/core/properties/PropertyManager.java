package me.fami6xx.rpuniverse.core.properties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages properties in the RP Universe.
 */
public class PropertyManager implements Listener {
    private final Map<UUID, Property> properties;
    private final File dataFolder;
    private final Gson gson;
    private final RPUniverse plugin;
    private final BukkitTask inactivityCheckerTask;
    private final BukkitTask savingTask;

    /**
     * Constructs a new PropertyManager.
     *
     * @param plugin the RPUniverse plugin instance
     */
    public PropertyManager(RPUniverse plugin) {
        this.plugin = plugin;
        properties = new HashMap<>();
        dataFolder = new File(plugin.getDataFolder(), "properties");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).setPrettyPrinting().create();

        loadProperties();
        inactivityCheckerTask = startInactivityChecker();
        savingTask = startSavingTask();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Disables the PropertyManager.
     */
    public void disable() {
        savingTask.cancel();
        inactivityCheckerTask.cancel();
        properties.values().forEach(Property::deactivate);
        saveProperties();
    }

    /**
     * Loads properties from JSON files in the data folder.
     */
    private void loadProperties() {
        File[] files = dataFolder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    Property property = gson.fromJson(content, Property.class);
                    properties.put(property.getPropertyId(), property);
                    property.afterLoad();
                } catch (IOException e) {
                    RPUniverse.getInstance().getLogger().severe("Failed to load property from file: " + file.getName());
                }
            }
        }
    }

    /**
     * Saves all properties to JSON files.
     */
    public void saveProperties() {
        for (Property property : properties.values()) {
            property.beforeSave();
            saveProperty(property);
        }
    }

    /**
     * Saves a single property to a JSON file.
     *
     * @param property the property to save
     */
    public void saveProperty(Property property) {
        property.beforeSave();
        File file = new File(dataFolder, property.getPropertyId().toString() + ".json");
        try {
            String json = gson.toJson(property);
            Files.write(file.toPath(), json.getBytes());
        } catch (IOException e) {
            RPUniverse.getInstance().getLogger().severe("Failed to save property to file: " + file.getName());
        }
    }

    /**
     * Creates a new property and saves it.
     *
     * @param property the property to create
     */
    public void createProperty(Property property) {
        properties.put(property.getPropertyId(), property);
        property.beforeSave();
        saveProperty(property);
    }

    /**
     * Removes a property by its ID.
     *
     * @param propertyId the ID of the property to remove
     */
    public void removeProperty(UUID propertyId) {
        Property property = properties.remove(propertyId);
        if (property == null) return;

        File file = new File(dataFolder, propertyId.toString() + ".json");
        if (file.exists()) {
            file.delete();
        }

        property.remove();
    }

    /**
     * Gets a property by its hologram location.
     *
     * @param loc the location of the hologram
     * @return the property with the specified hologram location, or null if not found
     */
    public Property getPropertyByHologram(Location loc) {
        for (Property property : properties.values()) {
            if (property.getHologramLocation().equals(loc)) {
                return property;
            }
        }
        return null;
    }

    /**
     * Gets all properties.
     *
     * @return a collection of all properties
     */
    public Collection<Property> getAllProperties() {
        return properties.values();
    }

    /**
     * Starts a task to check for inactive properties and remove ownership if necessary.
     * <p>
     * Also checks for expired rent.
     */
    private BukkitTask startInactivityChecker() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long inactivityThreshold = 30L * 24 * 60 * 60 * 1000; // 30 days in milliseconds
                for (Property property : properties.values()) {
                    if (!property.isAvailable()) {
                        if (property.isRentable()) {
                            // Check if the rent has expired
                            long rentEndTime = property.getRentStart() + property.getRentDuration();
                            if (now >= rentEndTime) {
                                // Rent has expired, remove owner and trusted players
                                property.setOwner(null);
                                property.setTrustedPlayers(new ArrayList<>());
                                saveProperty(property);
                            }
                        } else {
                            // For non-rentable properties, check for inactivity
                            if (now - property.getLastActive() > inactivityThreshold) {
                                // Remove ownership due to inactivity
                                property.setOwner(null);
                                property.setTrustedPlayers(new ArrayList<>());
                                saveProperty(property);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 60 * 20L); // Run every minute
    }

    /**
     * Starts a task to save properties every 5 minutes.
     */
    private BukkitTask startSavingTask() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                saveProperties();
            }
        }.runTaskTimer(plugin, 0L, 5 * 60 * 20L); // Run every 5 minutes
    }

    /**
     * Gets a property by its ID.
     *
     * @param propertyId the ID of the property
     * @return the property with the specified ID, or null if not found
     */
    public Property getPropertyById(UUID propertyId) {
        return properties.get(propertyId);
    }

    /**
     * Gets a property by its owner's UUID.
     *
     * @param ownerUUID the UUID of the owner
     * @return the property owned by the specified UUID, or null if not found
     */
    private Property getPropertyByOwner(UUID ownerUUID) {
        for (Property property : getAllProperties()) {
            if (ownerUUID.equals(property.getOwner())) {
                return property;
            }
        }
        return null;
    }

    /**
     * Event handler for player login events.
     *
     * @param event the player join event
     */
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Property property = getPropertyByOwner(player.getUniqueId());
        if (property != null) {
            property.updateLastActive();
            saveProperty(property);
        }
    }
}