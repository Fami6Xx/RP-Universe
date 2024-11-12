package me.fami6xx.rpuniverse.core.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import org.bukkit.Location;

/**
 * Represents a property in the RP Universe.
 */
public class Property {
    private final UUID propertyId;
    private List<UUID> locksUUID;
    private JsonObject hologramLocationData;
    private transient Location hologramLocation;
    private boolean rentable;
    private double price;
    private long rentStart; //timestamp in milliseconds
    private long rentDuration; //timestamp in milliseconds
    private long rentMaximumDuration; //timestamp in milliseconds
    private UUID owner;
    private List<UUID> trustedPlayers;
    private long lastActive; // timestamp in milliseconds

    /**
     * Constructs a new Property with the specified property ID.
     *
     * @param propertyId the unique identifier for the property
     */
    public Property(UUID propertyId) {
        this.propertyId = propertyId;
        this.locksUUID = new ArrayList<>();
        this.trustedPlayers = new ArrayList<>();
        this.lastActive = System.currentTimeMillis();
    }

    /**
     * Called before saving the property to the file.
     * <p>
     * Makes sure that all the data that needs to be saved is savable.
     */
    protected void beforeSave() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).create();
        hologramLocationData = gson.toJsonTree(hologramLocation, Location.class).getAsJsonObject();
    }

    /**
     * Called after loading the property from the file.
     * <p>
     * Makes sure that all the data that needs to be loaded is loaded.
     */
    protected void afterLoad() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).create();
        hologramLocation = gson.fromJson(hologramLocationData, Location.class);

        List<Lock> locks = RPUniverse.getInstance().getLockHandler().getAllLocks();
        long count = locks.stream().filter(lock -> locksUUID.contains(lock.getUUID())).count();
        if (count != locksUUID.size()) {
            RPUniverse.getInstance().getLogger().severe("Property " + propertyId + " has invalid locks.");
        }
    }

    /**
     * Deactivates the property, deleting the hologram.
     */
    protected void deactivate() {

    }

    /**
     * Removes the property from the server. Doesn't remove the property from file system.
     * <p>
     * Includes removal of all associated locks and the hologram.
     */
    protected void remove() {
        // Remove all the locks
        RPUniverse.getInstance().getLockHandler().getAllLocks().stream()
                .filter(lock -> locksUUID.contains(lock.getUUID()))
                .forEach(lock -> RPUniverse.getInstance().getLockHandler().removeLock(lock));
    }

    /**
     * Gets the unique identifier for the property.
     *
     * @return the property ID
     */
    public UUID getPropertyId() {
        return propertyId;
    }

    /**
     * Gets the list of locked blocks associated with the property.
     *
     * @return the list of locked blocks
     */
    public List<UUID> getLocksUUID() {
        return locksUUID;
    }

    /**
     * Sets the list of locked blocks associated with the property.
     *
     * @param locksUUID the new list of locked blocks
     */
    public void setLockedBlocks(List<UUID> locksUUID) {
        this.locksUUID = locksUUID;
    }

    /**
     * Gets the location of the hologram associated with the property.
     *
     * @return the hologram location
     */
    public Location getHologramLocation() {
        return hologramLocation;
    }

    /**
     * Sets the location of the hologram associated with the property.
     *
     * @param hologramLocation the new hologram location
     */
    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;
    }

    /**
     * Checks if the property is rentable.
     *
     * @return true if the property is rentable, false otherwise
     */
    public boolean isRentable() {
        return rentable;
    }

    /**
     * Sets whether the property is rentable.
     *
     * @param rentable true if the property should be rentable, false otherwise
     */
    public void setRentable(boolean rentable) {
        this.rentable = rentable;
    }

    /**
     * Gets the price of the property.
     *
     * @return the price of the property
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the property.
     *
     * @param price the new price of the property
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the rent duration of the property in milliseconds.
     *
     * @return the rent duration in milliseconds
     */
    public long getRentDuration() {
        return rentDuration;
    }

    /**
     * Sets the rent duration of the property in milliseconds.
     *
     * @param rentDuration the new rent duration in milliseconds
     */
    public void setRentDuration(long rentDuration) {
        this.rentDuration = rentDuration;
    }

    /**
     * Gets the owner of the property.
     *
     * @return the owner UUID
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the property and updates the last active timestamp.
     *
     * @param owner the new owner UUID
     */
    public void setOwner(UUID owner) {
        this.owner = owner;
        updateLastActive();
    }

    /**
     * Gets the list of trusted players for the property.
     *
     * @return the list of trusted players
     */
    public List<UUID> getTrustedPlayers() {
        return trustedPlayers;
    }

    /**
     * Sets the list of trusted players for the property.
     *
     * @param trustedPlayers the new list of trusted players
     */
    public void setTrustedPlayers(List<UUID> trustedPlayers) {
        this.trustedPlayers = trustedPlayers;
    }

    /**
     * Adds a player to the list of trusted players.
     *
     * @param playerUUID the UUID of the player to add
     */
    public void addTrustedPlayer(UUID playerUUID) {
        if (!trustedPlayers.contains(playerUUID)) {
            trustedPlayers.add(playerUUID);
        }
    }

    /**
     * Removes a player from the list of trusted players.
     *
     * @param playerUUID the UUID of the player to remove
     */
    public void removeTrustedPlayer(UUID playerUUID) {
        trustedPlayers.remove(playerUUID);
    }

    /**
     * Gets the last active timestamp of the property.
     *
     * @return the last active timestamp in milliseconds
     */
    public long getLastActive() {
        return lastActive;
    }

    /**
     * Updates the last active timestamp to the current time.
     */
    public void updateLastActive() {
        this.lastActive = System.currentTimeMillis();
    }

    /**
     * Checks if the property is available (i.e., has no owner).
     *
     * @return true if the property is available, false otherwise
     */
    public boolean isAvailable() {
        return owner == null;
    }

    /**
     * Gets the start timestamp of the rent.
     *
     * @return the start timestamp of the rent in milliseconds
     */
    public long getRentStart() {
        return rentStart;
    }

    /**
     * Sets the start timestamp of the rent.
     *
     * @param rentStart the new start timestamp of the rent in milliseconds
     */
    public void setRentStart(long rentStart) {
        this.rentStart = rentStart;
    }

    /**
     * Gets the maximum duration of the rent.
     * @return the maximum duration of the rent in milliseconds
     */
    public long getRentMaximumDuration() {
        return rentMaximumDuration;
    }

    /**
     * Sets the maximum duration of the rent.
     * @param rentMaximumDuration the new maximum duration of the rent in milliseconds
     */
    public void setRentMaximumDuration(long rentMaximumDuration) {
        this.rentMaximumDuration = rentMaximumDuration;
    }
}