package me.fami6xx.rpuniverse.core.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.actions.Action;
import eu.decentsoftware.holograms.api.actions.ActionType;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.StaticHologram;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.locks.LockHandler;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.menus.AdminPropertyEditMenu;
import me.fami6xx.rpuniverse.core.properties.menus.BuyPropertyMenu;
import me.fami6xx.rpuniverse.core.properties.menus.PlayerManagePropertyMenu;
import me.fami6xx.rpuniverse.core.properties.menus.RentPropertyMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a property in the RP Universe.
 */
public class Property {
    private final UUID propertyId;
    private final transient List<Lock> locks = new ArrayList<>();
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

    private transient famiHologram hologram;

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

        List<Lock> allLocks = RPUniverse.getInstance().getLockHandler().getAllLocks();
        allLocks.stream().filter(lock -> locksUUID.contains(lock.getUUID())).forEach(this.locks::add);
        long count = this.locks.size();
        if (count != locksUUID.size()) {
            RPUniverse.getInstance().getLogger().severe("Property " + propertyId + " has invalid locks.");
        }

        if (hologramLocation != null) {
            if (hologram != null)
                hologram.destroy();
            hologram = startHologram();
        }
    }

    /**
     * Deactivates the property, deleting the hologram.
     */
    protected void deactivate() {
        if (hologram != null) {
            hologram.destroy();
        }
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

        // Remove the hologram
        if (hologram != null) {
            hologram.destroy();
        }
    }

    /**
     * Starts the hologram for the property.
     *
     * @return the hologram
     */
    private famiHologram startHologram() {
        Property property = this;
        StaticHologram hologram = new StaticHologram(hologramLocation, false, RPUniverse.getInstance().getConfiguration().getDouble("jobs.menuRange"), false) {
            @Override
            public boolean shouldShow(Player player) {
                if (player == null) return true;
                PlayerData playerData = RPUniverse.getInstance().getPlayerData(player.getUniqueId().toString());
                if (playerData.getPlayerMode() == PlayerMode.ADMIN) {
                    if (getHologram() == null) return true;
                    if (getHologram().getPage(3) == null) return true;
                    String[] adminHologramLines = getAdminHologramLines();
                    for (int i = 0; i < getHologram().getPage(3).getLines().size(); i++) {
                        String content = getHologram().getPage(3).getLines().get(i).getContent();
                        String adminContent = FamiUtils.format(adminHologramLines[i]);
                        if (!content.equals(adminContent)) {
                            getHologram().getPage(3).getLines().get(i).setContent(FamiUtils.format(adminHologramLines[i]));
                        }
                    }
                    return true;
                }

                if (owner == null) return true;
                if (player.getUniqueId().toString().equals(owner.toString())) return true;

                return true;
            }

            @Override
            public int getPageToDisplay(Player player) {
                if (player == null) return 0;

                PlayerData playerData = RPUniverse.getInstance().getPlayerData(player.getUniqueId().toString());
                if (playerData.getPlayerMode() == PlayerMode.ADMIN) return 3;

                if (owner != null && player.getUniqueId().toString().equals(owner.toString())) return 2;

                if (rentable) return 1;
                return 0;
            }
        };

        Hologram holo = hologram.getHologram();
        String[] buyableHologramLines = RPUniverse.getLanguageHandler().propertyBuyableHologram.split("~");
        String[] rentableHologramLines = RPUniverse.getLanguageHandler().propertyRentableHologram.split("~");
        String[] ownerHologramLines = RPUniverse.getLanguageHandler().propertyOwnerHologram.split("~");

        for (String buyableHologramLine : buyableHologramLines) {
            hologram.addLine(FamiUtils.format(buyableHologramLine));
        }
        DHAPI.addHologramPage(holo, Arrays.stream(rentableHologramLines).map(FamiUtils::format).toList());
        DHAPI.addHologramPage(holo, Arrays.stream(ownerHologramLines).map(FamiUtils::format).toList());
        DHAPI.addHologramPage(holo, Arrays.stream(getAdminHologramLines()).map(FamiUtils::format).toList());

        holo.getPages().forEach(page -> page.addAction(ClickType.RIGHT,  new Action(new ActionType(UUID.randomUUID().toString()) {
            @Override
            public boolean execute(Player player, String... strings) {
                if (player == null) return true;
                PlayerData playerData = RPUniverse.getInstance().getPlayerData(player.getUniqueId().toString());
                if (playerData.getPlayerMode() == PlayerMode.ADMIN) {
                    new AdminPropertyEditMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), property).open();
                } else if (owner != null && player.getUniqueId().toString().equals(owner.toString())) {
                    new PlayerManagePropertyMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), property).open();
                } else if (rentable) {
                    new RentPropertyMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), property).open();
                } else {
                    new BuyPropertyMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), property).open();
                }
                return true;
            }
        }, "")));

        return hologram;
    }

    private String[] getAdminHologramLines() {
        return new String[] {
                "&7&k|",
                "&c&lProperty",
                "&7&k|",
                "&6Owner: &e" + (owner == null ? "None" : Bukkit.getServer().getOfflinePlayer(owner).getName()),
                "&6Price: &e" + price + "&7$",
                "&6Rentable: &e" + (rentable ? "Yes" : "No"),
                "&7&k|",
                "&cClick to edit"
        };
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
        this.locks.clear();
        RPUniverse.getInstance().getLockHandler().getAllLocks().stream()
                .filter(lock -> locksUUID.contains(lock.getUUID()))
                .forEach(locks::add);
    }

    /**
     * Adds a lock to the property.
     * <p>
     * Assuming that the lock is not already associated with the property.
     * <p>
     * Assuming you've already added the lock to LockHandler.
     *
     * @param lock the lock to add
     */
    public void addLock(Lock lock) {
        locks.add(lock);
        locksUUID.add(lock.getUUID());

        lock.addOwner(owner);
        trustedPlayers.forEach(lock::addOwner);
    }

    /**
     * Removes a lock from the property.
     * <p>
     * Also removes the lock from the LockHandler. Completely deleting it from the server.
     *
     * @param lock the lock to remove
     */
    public void removeLock(Lock lock) {
        locks.remove(lock);
        locksUUID.remove(lock.getUUID());

        lock.removeOwner(owner);
        trustedPlayers.forEach(lock::removeOwner);

        RPUniverse.getInstance().getLockHandler().removeLock(lock);
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
     * <p>
     * Destroys the current hologram and creates a new one.
     *
     * @param hologramLocation the new hologram location
     */
    public void setHologramLocation(Location hologramLocation) {
        this.hologramLocation = hologramLocation;

        if (hologram != null) {
            hologram.destroy();
        }

        hologram = startHologram();
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
        if (owner != null) {
            LockHandler lockHandler = RPUniverse.getInstance().getLockHandler();
            for (UUID lockUUID : locksUUID) {
                Lock lock = lockHandler.getLockByUUID(lockUUID);
                if (lock != null) {
                    lock.removeOwner(owner);
                }
            }
        }
        this.owner = owner;
        updateLastActive();
        if (owner != null) {
            LockHandler lockHandler = RPUniverse.getInstance().getLockHandler();
            for (UUID lockUUID : locksUUID) {
                Lock lock = lockHandler.getLockByUUID(lockUUID);
                if (lock != null) {
                    lock.addOwner(owner);
                }
            }
        }
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
        this.trustedPlayers.forEach(uuid -> {
            for (Lock lock : locks) {
                if (lock == null) continue;
                lock.removeOwner(uuid);
            }
        });
        this.trustedPlayers = trustedPlayers;
        this.trustedPlayers.forEach(uuid -> {
            for (Lock lock : locks) {
                if (lock == null) continue;
                lock.addOwner(uuid);
            }
        });
    }

    /**
     * Adds a player to the list of trusted players.
     *
     * @param playerUUID the UUID of the player to add
     */
    public void addTrustedPlayer(UUID playerUUID) {
        if (!trustedPlayers.contains(playerUUID)) {
            trustedPlayers.add(playerUUID);
            for(Lock lock : locks) {
                if (lock == null) continue;
                lock.addOwner(playerUUID);
            }
        }
    }

    /**
     * Removes a player from the list of trusted players.
     *
     * @param playerUUID the UUID of the player to remove
     */
    public void removeTrustedPlayer(UUID playerUUID) {
        trustedPlayers.remove(playerUUID);
        for(Lock lock : locks) {
            if (lock == null) continue;
            lock.removeOwner(playerUUID);
        }
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

    /**
     * Gets the associated locks of the property.
     * @return the associated locks
     */
    public List<Lock> getAssociatedLocks() {
        return new ArrayList<>(locks);
    }
}