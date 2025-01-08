package me.fami6xx.rpuniverse.core.jobs;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.ItemStackAdapter;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A working step is a step that the player must do to (optionally) consume certain items
 * and then have a chance to receive one or more drops defined in {@link PossibleDrop}.
 */
public class WorkingStep {
    private static final Logger LOGGER = RPUniverse.getInstance().getLogger();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .create();

    private UUID uuid = UUID.randomUUID();
    private UUID jobUUID;
    private transient List<Location> workingLocations = new ArrayList<>();
    @SerializedName("workingLocations")
    private List<JsonObject> workingLocationsJson = new ArrayList<>();
    private int timeForStep; // In ticks

    // Optional item requirement
    private transient ItemStack itemNeeded; // Can be null
    @SerializedName("itemNeeded")
    private JsonElement itemNeededJson;
    private int amountOfItemNeeded; // Can be 0

    private transient List<PossibleDrop> possibleDrops = new ArrayList<>();
    @SerializedName("possibleDrops")
    private List<JsonObject> possibleDropsJson = new ArrayList<>();

    private int neededPermissionLevel; // Must be more than zero
    private boolean interactableFirstStage = false;

    private String name;
    private String description;
    private String workingStepBeingDoneMessage;

    /**
     * A working step is a step that the player must do to receive items. The player may optionally
     * need a specific item in his inventory to do the step, and the resulting drops and drop chances
     * are defined in {@link #possibleDrops}.
     *
     * @param workingLocations            The location(s) where the player must be to do the step.
     * @param timeForStep                 The time in ticks that the player must remain at the location.
     * @param itemNeeded                  The item that the player must have in their inventory (may be null).
     * @param amountOfItemNeeded          The amount of the above item that the player must have (may be 0).
     * @param neededPermissionLevel       The permission level required to do the step.
     * @param name                        The name of this working step.
     * @param description                 A short description of the working step.
     * @param workingStepBeingDoneMessage The message displayed while the working step is being done.
     * @param jobUUID                     The UUID of the job that this working step belongs to.
     * @param interactableFirstStage      Whether the first stage is interactable.
     * @param possibleDrops               A list of possible drops (each with an item and a chance).
     */
    public WorkingStep(@Nonnull List<Location> workingLocations,
                       int timeForStep,
                       @Nullable ItemStack itemNeeded,
                       int amountOfItemNeeded,
                       int neededPermissionLevel,
                       @Nonnull String name,
                       @Nonnull String description,
                       @Nonnull String workingStepBeingDoneMessage,
                       @Nonnull UUID jobUUID,
                       boolean interactableFirstStage,
                       @Nonnull List<PossibleDrop> possibleDrops) {
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.itemNeeded = itemNeeded;
        this.amountOfItemNeeded = amountOfItemNeeded;
        this.neededPermissionLevel = neededPermissionLevel;
        this.name = name;
        this.description = description;
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
        this.jobUUID = jobUUID;
        this.interactableFirstStage = interactableFirstStage;
        this.possibleDrops = possibleDrops;
    }

    /**
     * Private constructor used for deserialization.
     */
    private WorkingStep(@Nonnull List<Location> workingLocations,
                        int timeForStep,
                        @Nullable ItemStack itemNeeded,
                        int amountOfItemNeeded,
                        int neededPermissionLevel,
                        @Nonnull UUID uuid,
                        @Nonnull String name,
                        @Nonnull String description,
                        @Nonnull String workingStepBeingDoneMessage,
                        @Nonnull UUID jobUUID,
                        boolean interactableFirstStage,
                        @Nonnull List<PossibleDrop> possibleDrops) {
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.itemNeeded = itemNeeded;
        this.amountOfItemNeeded = amountOfItemNeeded;
        this.neededPermissionLevel = neededPermissionLevel;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
        this.jobUUID = jobUUID;
        this.interactableFirstStage = interactableFirstStage;
        this.possibleDrops = possibleDrops;
    }

    /**
     * A working step that requires no item to begin (itemNeeded = null).
     *
     * @param workingLocations            The location(s) where the player must be to do the step.
     * @param timeForStep                 The time in ticks that the player must remain at the location.
     * @param neededPermissionLevel       The permission level required to do the step.
     * @param name                        The name of this working step.
     * @param description                 A short description of the working step.
     * @param workingStepBeingDoneMessage The message displayed while the working step is being done.
     * @param jobUUID                     The UUID of the job that this working step belongs to.
     * @param interactableFirstStage      Whether the first stage is interactable.
     * @param possibleDrops               A list of possible drops (each with an item and a chance).
     */
    public WorkingStep(@Nonnull List<Location> workingLocations,
                       int timeForStep,
                       int neededPermissionLevel,
                       @Nonnull String name,
                       @Nonnull String description,
                       @Nonnull String workingStepBeingDoneMessage,
                       @Nonnull UUID jobUUID,
                       boolean interactableFirstStage,
                       @Nonnull List<PossibleDrop> possibleDrops) {
        this(workingLocations,
                timeForStep,
                null,
                0,
                neededPermissionLevel,
                name,
                description,
                workingStepBeingDoneMessage,
                jobUUID,
                interactableFirstStage,
                possibleDrops);
    }

    /**
     * Converts a string representation of a WorkingStep object into an actual WorkingStep object.
     *
     * @param s The string representation of the WorkingStep object (YAML).
     * @return The WorkingStep object created from the string representation, or null if an error occurs.
     */
    public static WorkingStep fromString(String s) {
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(s);

            UUID uuid = UUID.fromString(yaml.getString("uuid"));
            UUID jobUUID = UUID.fromString(yaml.getString("jobUUID"));

            List<?> rawLocations = yaml.getList("workingLocations");
            List<Location> workingLocations = new ArrayList<>();
            if (rawLocations != null) {
                for (Object obj : rawLocations) {
                    if (obj instanceof Location) {
                        workingLocations.add((Location) obj);
                    }
                }
            }

            int timeForStep = yaml.getInt("timeForStep");
            ItemStack itemNeeded = yaml.getItemStack("itemNeeded");
            int amountOfItemNeeded = yaml.getInt("amountOfItemNeeded", 0);

            int neededPermissionLevel = yaml.getInt("neededPermissionLevel");
            String name = yaml.getString("name");
            String description = yaml.getString("description");
            String workingStepBeingDoneMessage = yaml.getString("workingStepBeingDoneMessage");
            boolean interactableFirstStage = yaml.getBoolean("interactableFirstStage", false);

            // Reconstruct possibleDrops
            List<PossibleDrop> possibleDrops = new ArrayList<>();
            List<?> rawPossibleDrops = yaml.getList("possibleDrops");
            if (rawPossibleDrops != null) {
                for (Object obj : rawPossibleDrops) {
                    if (obj instanceof Map<?, ?>) {
                        Map<String, Object> dropMap = (Map<String, Object>) obj;

                        // Create a YamlConfiguration in memory
                        YamlConfiguration dropConfig = new YamlConfiguration();
                        // Put the map under a subsection, e.g. "root"
                        dropConfig.createSection("root", dropMap);
                        ConfigurationSection section = dropConfig.getConfigurationSection("root");

                        ItemStack dropItem = section.getItemStack("item");
                        double chance = section.getDouble("chance", 0.0);

                        if (dropItem != null) {
                            possibleDrops.add(new PossibleDrop(dropItem, chance));
                        }
                    }
                }
            }

            return new WorkingStep(
                    workingLocations,
                    timeForStep,
                    itemNeeded,
                    amountOfItemNeeded,
                    neededPermissionLevel,
                    uuid,
                    name,
                    description,
                    workingStepBeingDoneMessage,
                    jobUUID,
                    interactableFirstStage,
                    possibleDrops
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while parsing WorkingStep object from string {\n"
                    + s + "\n}, with error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts this WorkingStep to a YAML string representation.
     *
     * @return A YAML string representing this WorkingStep.
     */
    @Override
    public String toString() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("uuid", uuid.toString());
        yaml.set("jobUUID", jobUUID.toString());
        yaml.set("workingLocations", workingLocations);
        yaml.set("timeForStep", timeForStep);
        if (itemNeeded != null) {
            yaml.set("itemNeeded", itemNeeded);
        }
        yaml.set("amountOfItemNeeded", amountOfItemNeeded);
        yaml.set("neededPermissionLevel", neededPermissionLevel);
        yaml.set("name", name);
        yaml.set("description", description);
        yaml.set("workingStepBeingDoneMessage", workingStepBeingDoneMessage);
        yaml.set("interactableFirstStage", interactableFirstStage);

        // Serialize possibleDrops
        List<YamlConfiguration> serializedDrops = new ArrayList<>();
        for (PossibleDrop drop : possibleDrops) {
            YamlConfiguration dropConfig = new YamlConfiguration();
            dropConfig.set("item", drop.getItem());
            dropConfig.set("chance", drop.getChance());
            serializedDrops.add(dropConfig);
        }
        yaml.set("possibleDrops", serializedDrops);

        return yaml.saveToString();
    }

    /**
     * Converts this WorkingStep to a JSON object representation.
     *
     * @return A JSON object representing this WorkingStep.
     */
    public JsonObject toJsonObject() {
        workingLocationsJson = new ArrayList<>();
        possibleDropsJson = new ArrayList<>();
        itemNeededJson = null;

        if (this.workingLocations != null) {
            for (Location location : this.workingLocations) {
                workingLocationsJson.add(GSON.toJsonTree(location, Location.class).getAsJsonObject());
            }
        }

        if (this.itemNeeded != null) {
            itemNeededJson = GSON.toJsonTree(this.itemNeeded, ItemStack.class);
        }

        if (this.possibleDrops != null) {
            for (PossibleDrop drop : this.possibleDrops) {
                JsonObject dropJson = new JsonObject();
                dropJson.addProperty("chance", drop.getChance());
                dropJson.add("item", GSON.toJsonTree(drop.getItem(), ItemStack.class));
                possibleDropsJson.add(dropJson);
            }
        }
        return GSON.toJsonTree(this).getAsJsonObject();
    }

    /**
     * Converts a JSON object representation of a WorkingStep into an actual WorkingStep object.
     *
     * @param jsonObject The JSON object representation of the WorkingStep.
     * @return The WorkingStep object created from the JSON object.
     */
    public static WorkingStep fromJsonObject(JsonObject jsonObject) {
        WorkingStep step = GSON.fromJson(jsonObject, WorkingStep.class);

        if (step.workingLocationsJson != null) {
            step.workingLocations = new ArrayList<>();
            for (JsonObject locJson : step.workingLocationsJson) {
                Location location = GSON.fromJson(locJson, Location.class);
                step.workingLocations.add(location);
            }
        } else {
            step.workingLocations = new ArrayList<>();
        }

        if (step.itemNeededJson != null) {
            step.itemNeeded = GSON.fromJson(step.itemNeededJson, ItemStack.class);
        } else {
            step.itemNeeded = null;
        }

        if (step.possibleDropsJson != null) {
            step.possibleDrops = new ArrayList<>();
            for (JsonObject dropJson : step.possibleDropsJson) {
                double chance = dropJson.get("chance").getAsDouble();
                JsonPrimitive itemJson = dropJson.getAsJsonPrimitive("item");

                ItemStack dropItem = GSON.fromJson(itemJson, ItemStack.class);
                step.possibleDrops.add(new PossibleDrop(dropItem, chance));
            }
        } else {
            step.possibleDrops = new ArrayList<>();
        }

        return step;
    }


    /**
     * Gets the UUID of the job that this working step belongs to.
     *
     * @return The UUID of the job.
     */
    public UUID getJobUUID() {
        return jobUUID;
    }

    /**
     * Gets the name of this working step.
     *
     * @return The name of the working step.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this working step.
     *
     * @param name The new name of the working step.
     */
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Gets the description of this working step.
     *
     * @return The description of the working step.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this working step.
     *
     * @param description The new description of the working step.
     */
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    /**
     * Gets the message displayed while the working step is being done.
     *
     * @return The message displayed while the working step is being done.
     */
    public String getWorkingStepBeingDoneMessage() {
        return workingStepBeingDoneMessage;
    }

    /**
     * Sets the message displayed while the working step is being done.
     *
     * @param workingStepBeingDoneMessage The new message displayed while the working step is being done.
     */
    public void setWorkingStepBeingDoneMessage(@Nonnull String workingStepBeingDoneMessage) {
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
    }

    /**
     * Gets the permission level required to do the step.
     *
     * @return The permission level required to do the step.
     */
    public int getNeededPermissionLevel() {
        return neededPermissionLevel;
    }

    /**
     * Sets the permission level required to do the step.
     *
     * @param neededPermissionLevel The new permission level required to do the step.
     */
    public void setNeededPermissionLevel(int neededPermissionLevel) {
        this.neededPermissionLevel = neededPermissionLevel;
    }

    /**
     * Gets the location(s) where the player must be to do the step.
     *
     * @return The location(s) where the player must be to do the step.
     */
    public List<Location> getWorkingLocations() {
        return workingLocations;
    }

    /**
     * Sets the location(s) where the player must be to do the step.
     *
     * @param workingLocations The new location(s) where the player must be to do the step.
     */
    public void setWorkingLocations(@Nonnull List<Location> workingLocations) {
        this.workingLocations = workingLocations;
    }

    /**
     * Adds a location where the player must be to do the step.
     *
     * @param location The new location where the player must be to do the step.
     */
    public void addWorkingLocation(@Nonnull Location location) {
        this.workingLocations.add(location);
    }

    /**
     * Removes a location where the player must be to do the step.
     *
     * @param location The location to remove.
     */
    public void removeWorkingLocation(@Nonnull Location location) {
        this.workingLocations.remove(location);
    }

    /**
     * Gets the time in ticks that the player must remain at the location.
     *
     * @return The time in ticks that the player must remain at the location.
     */
    public int getTimeForStep() {
        return timeForStep;
    }

    /**
     * Sets the time in ticks that the player must remain at the location.
     *
     * @param timeForStep The new time in ticks that the player must remain at the location.
     */
    public void setTimeForStep(int timeForStep) {
        this.timeForStep = timeForStep;
    }

    /**
     * Gets the item that the player must have in their inventory.
     *
     * @return The item that the player must have in their inventory, or null if no item is needed.
     */
    @Nullable
    public ItemStack getItemNeeded() {
        return itemNeeded;
    }

    /**
     * Sets the item that the player must have in their inventory.
     *
     * @param itemNeeded The new item that the player must have in their inventory.
     */
    public void setItemNeeded(@Nullable ItemStack itemNeeded) {
        this.itemNeeded = itemNeeded;
    }

    /**
     * Gets the amount of the item that the player must have.
     *
     * @return The amount of the item that the player must have.
     */
    public int getAmountOfItemNeeded() {
        return amountOfItemNeeded;
    }

    /**
     * Sets the amount of the item that the player must have.
     *
     * @param amountOfItemNeeded The new amount of the item that the player must have.
     */
    public void setAmountOfItemNeeded(int amountOfItemNeeded) {
        this.amountOfItemNeeded = amountOfItemNeeded;
    }

    /**
     * Checks if the first stage is interactable.
     *
     * @return true if the first stage is interactable, false otherwise.
     */
    public boolean isInteractableFirstStage() {
        return interactableFirstStage;
    }

    /**
     * Sets whether the first stage is interactable.
     *
     * @param interactableFirstStage true if the first stage is interactable, false otherwise.
     */
    public void setInteractableFirstStage(boolean interactableFirstStage) {
        this.interactableFirstStage = interactableFirstStage;
    }

    /**
     * Gets the internal UUID of this WorkingStep.
     *
     * @return The internal UUID of this WorkingStep.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the list of all possible drops for this WorkingStep.
     *
     * @return The list of all possible drops for this WorkingStep.
     */
    @Nonnull
    public List<PossibleDrop> getPossibleDrops() {
        return possibleDrops;
    }

    /**
     * Sets the list of all possible drops for this WorkingStep.
     *
     * @param possibleDrops A list of {@link PossibleDrop} objects, each representing an item and its drop chance.
     */
    public void setPossibleDrops(@Nonnull List<PossibleDrop> possibleDrops) {
        this.possibleDrops = possibleDrops;
    }
}