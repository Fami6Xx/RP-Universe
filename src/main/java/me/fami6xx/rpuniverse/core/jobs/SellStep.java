package me.fami6xx.rpuniverse.core.jobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.ItemStackAdapter;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a sell step where players can sell items at a specific location.
 */
public class SellStep {
    private static final Logger LOGGER = RPUniverse.getInstance().getLogger();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .create();

    private UUID uuid = UUID.randomUUID();
    private UUID jobUUID;
    private Location location;
    private ItemStack itemToSell;
    private double itemValue; // The value of the item
    private int timeToSell; // In ticks
    private int maxSellAmount;
    private double playerPercentage; // Percentage that goes to player (0 to 100)
    private double jobPercentage;    // Percentage that goes to job (0 to 100)

    private String name;
    private String description;

    /**
     * Constructor for creating a new SellStep.
     *
     * @param jobUUID          The UUID of the job this sell step belongs to.
     * @param location         The location where the sell step is performed.
     * @param itemToSell       The item that players can sell.
     * @param itemValue        The value of the item (per unit).
     * @param timeToSell       The time it takes to sell the item (in ticks).
     * @param maxSellAmount    The maximum number of items a player can sell at once.
     * @param playerPercentage The percentage of the sale price that goes to the player.
     * @param jobPercentage    The percentage of the sale price that goes to the job.
     * @param name             The name of the sell step.
     * @param description      The description of the sell step.
     */
    public SellStep(@Nonnull UUID jobUUID, @Nonnull Location location, @Nonnull ItemStack itemToSell,
                    double itemValue, int timeToSell, int maxSellAmount, double playerPercentage, double jobPercentage,
                    @Nonnull String name, @Nonnull String description) {
        this.jobUUID = jobUUID;
        this.location = location;
        this.itemToSell = itemToSell;
        this.itemValue = itemValue;
        this.timeToSell = timeToSell;
        this.maxSellAmount = maxSellAmount;
        setPlayerPercentage(playerPercentage);
        setJobPercentage(jobPercentage);
        this.name = name;
        this.description = description;
    }

    /**
     * Private constructor used for deserialization.
     */
    private SellStep(@Nonnull UUID uuid, @Nonnull UUID jobUUID, @Nonnull Location location, @Nonnull ItemStack itemToSell,
                     double itemValue, int timeToSell, int maxSellAmount, double playerPercentage, double jobPercentage,
                     @Nonnull String name, @Nonnull String description) {
        this.uuid = uuid;
        this.jobUUID = jobUUID;
        this.location = location;
        this.itemToSell = itemToSell;
        this.itemValue = itemValue;
        this.timeToSell = timeToSell;
        this.maxSellAmount = maxSellAmount;
        setPlayerPercentage(playerPercentage);
        setJobPercentage(jobPercentage);
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the UUID of this SellStep.
     *
     * @return The UUID of this SellStep.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the UUID of the job this SellStep belongs to.
     *
     * @return The UUID of the job.
     */
    public UUID getJobUUID() {
        return jobUUID;
    }

    /**
     * Sets the UUID of the job this SellStep belongs to.
     *
     * @param jobUUID The new UUID of the job.
     */
    public void setJobUUID(@Nonnull UUID jobUUID) {
        this.jobUUID = jobUUID;
    }

    /**
     * Gets the location where the sell step is performed.
     *
     * @return The location of the sell step.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location where the sell step is performed.
     *
     * @param location The new location of the sell step.
     */
    public void setLocation(@Nonnull Location location) {
        this.location = location;
    }

    /**
     * Gets the item that players can sell.
     *
     * @return The item to sell.
     */
    public ItemStack getItemToSell() {
        return itemToSell;
    }

    /**
     * Sets the item that players can sell.
     *
     * @param itemToSell The new item to sell.
     */
    public void setItemToSell(@Nonnull ItemStack itemToSell) {
        this.itemToSell = itemToSell;
    }

    /**
     * Gets the value of the item (per unit).
     *
     * @return The value of the item.
     */
    public double getItemValue() {
        return itemValue;
    }

    /**
     * Sets the value of the item (per unit).
     *
     * @param itemValue The new value of the item.
     */
    public void setItemValue(double itemValue) {
        this.itemValue = itemValue;
    }

    /**
     * Gets the time it takes to sell the item (in ticks).
     *
     * @return The time to sell the item.
     */
    public int getTimeToSell() {
        return timeToSell;
    }

    /**
     * Sets the time it takes to sell the item (in ticks).
     *
     * @param timeToSell The new time to sell the item.
     */
    public void setTimeToSell(int timeToSell) {
        this.timeToSell = timeToSell;
    }

    /**
     * Gets the maximum number of items a player can sell at once.
     *
     * @return The maximum sell amount.
     */
    public int getMaxSellAmount() {
        return maxSellAmount;
    }

    /**
     * Sets the maximum number of items a player can sell at once.
     *
     * @param maxSellAmount The new maximum sell amount.
     */
    public void setMaxSellAmount(int maxSellAmount) {
        this.maxSellAmount = maxSellAmount;
    }

    /**
     * Gets the percentage of the sale price that goes to the player.
     *
     * @return The player percentage.
     */
    public double getPlayerPercentage() {
        return playerPercentage;
    }

    /**
     * Sets the percentage of the sale price that goes to the player.
     *
     * @param playerPercentage The new player percentage.
     */
    public void setPlayerPercentage(double playerPercentage) {
        if (playerPercentage < 0.0 || playerPercentage > 100.0) {
            throw new IllegalArgumentException("Player percentage must be between 0 and 100.");
        }
        this.playerPercentage = playerPercentage;
    }

    /**
     * Gets the percentage of the sale price that goes to the job.
     *
     * @return The job percentage.
     */
    public double getJobPercentage() {
        return jobPercentage;
    }

    /**
     * Sets the percentage of the sale price that goes to the job.
     *
     * @param jobPercentage The new job percentage.
     */
    public void setJobPercentage(double jobPercentage) {
        if (jobPercentage < 0.0 || jobPercentage > 100.0) {
            throw new IllegalArgumentException("Job percentage must be between 0 and 100.");
        }
        this.jobPercentage = jobPercentage;
    }

    /**
     * Gets the name of the sell step.
     *
     * @return The name of the sell step.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the sell step.
     *
     * @param name The new name of the sell step.
     */
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Gets the description of the sell step.
     *
     * @return The description of the sell step.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the sell step.
     *
     * @param description The new description of the sell step.
     */
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    /**
     * Converts a string representation of a SellStep object into an actual SellStep object.
     *
     * @param s The string representation of the SellStep object.
     * @return The SellStep object created from the string representation, or null if an error occurs during parsing.
     */
    public static SellStep fromString(String s) {
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(s);

            UUID uuid = UUID.fromString(yaml.getString("uuid"));
            UUID jobUUID = UUID.fromString(yaml.getString("jobUUID"));
            Location location = deserializeLocation(yaml.getConfigurationSection("location"));
            ItemStack itemToSell = yaml.getItemStack("itemToSell");
            double itemValue = yaml.getDouble("itemValue");
            int timeToSell = yaml.getInt("timeToSell");
            int maxSellAmount = yaml.getInt("maxSellAmount");
            double playerPercentage = yaml.getDouble("playerPercentage");
            double jobPercentage = yaml.getDouble("jobPercentage");
            String name = yaml.getString("name");
            String description = yaml.getString("description");

            return new SellStep(uuid, jobUUID, location, itemToSell, itemValue, timeToSell, maxSellAmount,
                    playerPercentage, jobPercentage, name, description);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while parsing SellStep object from string {\n" + s + "\n}, with error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts this SellStep to a YAML string representation.
     *
     * @return A YAML string representing this SellStep.
     */
    @Override
    public String toString() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("uuid", uuid.toString());
        yaml.set("jobUUID", jobUUID.toString());
        yaml.createSection("location", serializeLocation(location));
        yaml.set("itemToSell", itemToSell);
        yaml.set("itemValue", itemValue);
        yaml.set("timeToSell", timeToSell);
        yaml.set("maxSellAmount", maxSellAmount);
        yaml.set("playerPercentage", playerPercentage);
        yaml.set("jobPercentage", jobPercentage);
        yaml.set("name", name);
        yaml.set("description", description);
        return yaml.saveToString();
    }

    /**
     * Converts this SellStep to a JSON object.
     *
     * @return A {@link JsonObject} representing this SellStep.
     */
    public JsonObject toJsonObject() {
        JsonObject root = GSON.toJsonTree(this).getAsJsonObject();

        // Overwrite 'location' field in JSON with the properly adapted location
        if (this.location != null) {
            root.add("location", GSON.toJsonTree(this.location, Location.class));
        }

        // Overwrite 'itemToSell' field in JSON with the properly adapted item
        if (this.itemToSell != null) {
            root.add("itemToSell", GSON.toJsonTree(this.itemToSell, ItemStack.class));
        }

        return root;
    }

    /**
     * Creates a SellStep from its JSON object.
     *
     * @param jsonObject The JSON representation of a SellStep.
     * @return A new {@link SellStep} instance populated from the JSON data.
     */
    public static SellStep fromJsonObject(JsonObject jsonObject) {
        SellStep step = GSON.fromJson(jsonObject, SellStep.class);

        if (jsonObject.has("location")) {
            step.location = GSON.fromJson(jsonObject.get("location"), Location.class);
        }
        if (jsonObject.has("itemToSell")) {
            step.itemToSell = GSON.fromJson(jsonObject.get("itemToSell"), ItemStack.class);
        }

        return step;
    }

    /**
     * Serializes a Location object into a Map for storage.
     *
     * @param loc The Location to serialize.
     * @return A Map containing the serialized location data.
     */
    private static java.util.Map<String, Object> serializeLocation(Location loc) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("world", loc.getWorld().getName());
        map.put("x", loc.getX());
        map.put("y", loc.getY());
        map.put("z", loc.getZ());
        map.put("yaw", loc.getYaw());
        map.put("pitch", loc.getPitch());
        return map;
    }

    /**
     * Deserializes a Location object from a ConfigurationSection.
     *
     * @param section The ConfigurationSection containing location data.
     * @return The deserialized Location object.
     */
    private static Location deserializeLocation(org.bukkit.configuration.ConfigurationSection section) {
        String worldName = section.getString("world");
        org.bukkit.World world = Bukkit.getWorld(worldName);
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Validates that the sum of playerPercentage and jobPercentage is 100%.
     *
     * @throws IllegalArgumentException if the percentages do not sum to 100%.
     */
    public void validatePercentages() {
        double total = playerPercentage + jobPercentage;
        if (Math.abs(total - 100.0) > 0.0001) { // Allowing a tiny margin for floating-point errors
            throw new IllegalArgumentException("The sum of playerPercentage and jobPercentage must be 100.");
        }
    }
}