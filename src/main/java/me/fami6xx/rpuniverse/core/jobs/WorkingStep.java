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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private int timeForStep;

    private transient List<NeededItem> neededItems = new ArrayList<>();
    @SerializedName("neededItems")
    private List<JsonObject> neededItemsJson = new ArrayList<>();

    private transient List<PossibleDrop> possibleDrops = new ArrayList<>();
    @SerializedName("possibleDrops")
    private List<JsonObject> possibleDropsJson = new ArrayList<>();

    private int neededPermissionLevel;
    private boolean interactableFirstStage = false;
    private String name;
    private String description;
    private String workingStepBeingDoneMessage;

    /**
     * Creates a WorkingStep.
     */
    public WorkingStep(@Nonnull List<Location> workingLocations,
                       int timeForStep,
                       @Nonnull List<NeededItem> neededItems,
                       int neededPermissionLevel,
                       @Nonnull String name,
                       @Nonnull String description,
                       @Nonnull String workingStepBeingDoneMessage,
                       @Nonnull UUID jobUUID,
                       boolean interactableFirstStage,
                       @Nonnull List<PossibleDrop> possibleDrops) {
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.neededItems = neededItems;
        this.neededPermissionLevel = neededPermissionLevel;
        this.name = name;
        this.description = description;
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
        this.jobUUID = jobUUID;
        this.interactableFirstStage = interactableFirstStage;
        this.possibleDrops = possibleDrops;
    }

    private WorkingStep(@Nonnull List<Location> workingLocations,
                        int timeForStep,
                        @Nonnull List<NeededItem> neededItems,
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
        this.neededItems = neededItems;
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
     * Builds a WorkingStep from a YAML string.
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
            int neededPermissionLevel = yaml.getInt("neededPermissionLevel");
            String name = yaml.getString("name");
            String description = yaml.getString("description");
            String workingStepBeingDoneMessage = yaml.getString("workingStepBeingDoneMessage");
            boolean interactableFirstStage = yaml.getBoolean("interactableFirstStage", false);

            List<NeededItem> neededItems = new ArrayList<>();
            List<?> rawNeededItems = yaml.getList("neededItems");
            if (rawNeededItems != null) {
                for (Object obj : rawNeededItems) {
                    if (obj instanceof Map<?, ?>) {
                        Map<String, Object> niMap = (Map<String, Object>) obj;
                        YamlConfiguration niConfig = new YamlConfiguration();
                        niConfig.createSection("root", niMap);
                        ConfigurationSection section = niConfig.getConfigurationSection("root");
                        ItemStack niItem = section.getItemStack("item");
                        int amount = section.getInt("amount", 0);
                        if (niItem != null) {
                            neededItems.add(new NeededItem(niItem, amount));
                        }
                    }
                }
            }

            List<PossibleDrop> possibleDrops = new ArrayList<>();
            List<?> rawPossibleDrops = yaml.getList("possibleDrops");
            if (rawPossibleDrops != null) {
                for (Object obj : rawPossibleDrops) {
                    if (obj instanceof Map<?, ?>) {
                        Map<String, Object> dropMap = (Map<String, Object>) obj;
                        YamlConfiguration dropConfig = new YamlConfiguration();
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
                    neededItems,
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
            LOGGER.log(Level.SEVERE, "An error occurred while parsing WorkingStep from string: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts this WorkingStep to a YAML string.
     */
    @Override
    public String toString() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("uuid", uuid.toString());
        yaml.set("jobUUID", jobUUID.toString());
        yaml.set("workingLocations", workingLocations);
        yaml.set("timeForStep", timeForStep);
        yaml.set("neededPermissionLevel", neededPermissionLevel);
        yaml.set("name", name);
        yaml.set("description", description);
        yaml.set("workingStepBeingDoneMessage", workingStepBeingDoneMessage);
        yaml.set("interactableFirstStage", interactableFirstStage);

        List<YamlConfiguration> serializedNeededItems = new ArrayList<>();
        for (NeededItem ni : neededItems) {
            YamlConfiguration niConfig = new YamlConfiguration();
            niConfig.set("item", ni.getItem());
            niConfig.set("amount", ni.getAmount());
            serializedNeededItems.add(niConfig);
        }
        yaml.set("neededItems", serializedNeededItems);

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
     * Converts this WorkingStep to a JSON object.
     */
    public JsonObject toJsonObject() {
        workingLocationsJson = new ArrayList<>();
        possibleDropsJson = new ArrayList<>();
        neededItemsJson = new ArrayList<>();

        if (this.workingLocations != null) {
            for (Location location : this.workingLocations) {
                workingLocationsJson.add(GSON.toJsonTree(location, Location.class).getAsJsonObject());
            }
        }

        if (this.possibleDrops != null) {
            for (PossibleDrop drop : this.possibleDrops) {
                JsonObject dropJson = new JsonObject();
                dropJson.addProperty("chance", drop.getChance());
                dropJson.add("item", GSON.toJsonTree(drop.getItem(), ItemStack.class));
                possibleDropsJson.add(dropJson);
            }
        }

        if (this.neededItems != null) {
            for (NeededItem ni : this.neededItems) {
                JsonObject niJson = new JsonObject();
                niJson.addProperty("amount", ni.getAmount());
                niJson.add("item", GSON.toJsonTree(ni.getItem(), ItemStack.class));
                neededItemsJson.add(niJson);
            }
        }

        return GSON.toJsonTree(this).getAsJsonObject();
    }

    /**
     * Converts a JSON object to a WorkingStep.
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

        if (step.possibleDropsJson != null) {
            step.possibleDrops = new ArrayList<>();
            for (JsonObject dropJson : step.possibleDropsJson) {
                double chance = dropJson.get("chance").getAsDouble();
                JsonElement itemElement = dropJson.get("item");
                ItemStack dropItem = GSON.fromJson(itemElement, ItemStack.class);
                step.possibleDrops.add(new PossibleDrop(dropItem, chance));
            }
        } else {
            step.possibleDrops = new ArrayList<>();
        }

        if (step.neededItemsJson != null) {
            step.neededItems = new ArrayList<>();
            for (JsonObject niJson : step.neededItemsJson) {
                int amount = niJson.get("amount").getAsInt();
                JsonElement itemElement = niJson.get("item");
                ItemStack niItem = GSON.fromJson(itemElement, ItemStack.class);
                step.neededItems.add(new NeededItem(niItem, amount));
            }
        } else {
            step.neededItems = new ArrayList<>();
        }

        return step;
    }

    /**
     * Gets job UUID.
     */
    public UUID getJobUUID() {
        return jobUUID;
    }

    /**
     * Gets name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     */
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Gets description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     */
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    /**
     * Gets working step being done message.
     */
    public String getWorkingStepBeingDoneMessage() {
        return workingStepBeingDoneMessage;
    }

    /**
     * Sets working step being done message.
     */
    public void setWorkingStepBeingDoneMessage(@Nonnull String workingStepBeingDoneMessage) {
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
    }

    /**
     * Gets needed permission level.
     */
    public int getNeededPermissionLevel() {
        return neededPermissionLevel;
    }

    /**
     * Sets needed permission level.
     */
    public void setNeededPermissionLevel(int neededPermissionLevel) {
        this.neededPermissionLevel = neededPermissionLevel;
    }

    /**
     * Gets working locations.
     */
    public List<Location> getWorkingLocations() {
        return workingLocations;
    }

    /**
     * Sets working locations.
     */
    public void setWorkingLocations(@Nonnull List<Location> workingLocations) {
        this.workingLocations = workingLocations;
    }

    /**
     * Adds working location.
     */
    public void addWorkingLocation(@Nonnull Location location) {
        this.workingLocations.add(location);
    }

    /**
     * Removes working location.
     */
    public void removeWorkingLocation(@Nonnull Location location) {
        this.workingLocations.remove(location);
    }

    /**
     * Gets time for step.
     */
    public int getTimeForStep() {
        return timeForStep;
    }

    /**
     * Sets time for step.
     */
    public void setTimeForStep(int timeForStep) {
        this.timeForStep = timeForStep;
    }

    /**
     * Checks if first stage is interactable.
     */
    public boolean isInteractableFirstStage() {
        return interactableFirstStage;
    }

    /**
     * Sets first stage interactable.
     */
    public void setInteractableFirstStage(boolean interactableFirstStage) {
        this.interactableFirstStage = interactableFirstStage;
    }

    /**
     * Gets the internal UUID.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets possible drops.
     */
    public List<PossibleDrop> getPossibleDrops() {
        return possibleDrops;
    }

    /**
     * Sets possible drops.
     */
    public void setPossibleDrops(@Nonnull List<PossibleDrop> possibleDrops) {
        this.possibleDrops = possibleDrops;
    }

    /**
     * Gets needed items.
     */
    public List<NeededItem> getNeededItems() {
        return neededItems;
    }

    /**
     * Sets needed items.
     */
    public void setNeededItems(@Nonnull List<NeededItem> neededItems) {
        this.neededItems = neededItems;
    }

    /**
     * Adds needed item.
     */
    public void addNeededItem(@Nonnull NeededItem neededItem) {
        this.neededItems.add(neededItem);
    }

    /**
     * Removes needed item.
     */
    public void removeNeededItem(@Nonnull NeededItem neededItem) {
        this.neededItems.remove(neededItem);
    }

    public static class NeededItem {
        private transient ItemStack item;
        private int amount;
        private JsonElement itemJson;

        public NeededItem(@Nonnull ItemStack item, int amount) {
            this.item = item;
            this.amount = amount;
        }

        /**
         * Gets item.
         */
        public ItemStack getItem() {
            return item;
        }

        /**
         * Sets item.
         */
        public void setItem(ItemStack item) {
            this.item = item;
        }

        /**
         * Gets amount.
         */
        public int getAmount() {
            return amount;
        }

        /**
         * Sets amount.
         */
        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
