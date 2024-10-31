package me.fami6xx.rpuniverse.core.jobs;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A working step is a step that the player must do to receive an item.
 * It can be set that the player must have a specific item in his inventory to do the step.
 */
public class WorkingStep {
    private static final Logger LOGGER = RPUniverse.getInstance().getLogger();

    private UUID uuid = UUID.randomUUID();
    private UUID jobUUID;
    private List<Location> workingLocations;
    private int timeForStep; // In ticks
    private ItemStack itemNeeded; // Can be null
    private int amountOfItemNeeded; // Can be 0
    private ItemStack itemGiven; // Cannot be null
    private int amountOfItemGiven; // Must be more than zero
    private int neededPermissionLevel; // Must be more than zero

    private boolean interactableFirstStage = false;

    private boolean dropRareItem = false;
    private double percentage = 0.0;
    private ItemStack rareItem; // Can be null

    private String name;
    private String description;
    private String workingStepBeingDoneMessage;

    /**
     * A working step is a step that the player must do to receive an item. The player must have a specific item in his inventory to do the step.
     *
     * @param workingLocations            The location where the player must be to do the step.
     * @param timeForStep                 The time in ticks that the player must be at the location to do the step.
     * @param itemNeeded                  The item that the player must have in his inventory to do the step.
     * @param amountOfItemNeeded          The amount of the item that the player must have in his inventory to do the step.
     * @param itemGiven                   The item that the player will receive when he does the step.
     * @param amountOfItemGiven           The amount of the item that the player will receive when he does the step.
     * @param neededPermissionLevel       The permission level that the player must have to do the step.
     * @param name                        The name of the working step.
     * @param description                 The description of the working step.
     * @param workingStepBeingDoneMessage The message displayed when the working step is being done.
     * @param jobUUID                     The UUID of the job that this working step belongs to.
     * @param interactableFirstStage      Whether the first stage is interactable.
     * @param dropRareItem                Whether a rare item can drop.
     * @param percentage                  The percentage chance of dropping the rare item.
     * @param rareItem                    The rare item that can be dropped.
     */
    public WorkingStep(@Nonnull List<Location> workingLocations, int timeForStep, @Nullable ItemStack itemNeeded, int amountOfItemNeeded,
                       @Nonnull ItemStack itemGiven, int amountOfItemGiven, int neededPermissionLevel,
                       @Nonnull String name, @Nonnull String description, @Nonnull String workingStepBeingDoneMessage, @Nonnull UUID jobUUID,
                       boolean interactableFirstStage, boolean dropRareItem, double percentage, @Nullable ItemStack rareItem) {
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.itemNeeded = itemNeeded;
        this.amountOfItemNeeded = amountOfItemNeeded;
        this.itemGiven = itemGiven;
        this.amountOfItemGiven = amountOfItemGiven;
        this.neededPermissionLevel = neededPermissionLevel;
        this.name = name;
        this.description = description;
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
        this.jobUUID = jobUUID;
        this.interactableFirstStage = interactableFirstStage;
        this.dropRareItem = dropRareItem;
        setPercentage(percentage);
        this.rareItem = rareItem;
    }

    /**
     * Private constructor used for deserialization.
     */
    private WorkingStep(@Nonnull List<Location> workingLocations, int timeForStep, @Nullable ItemStack itemNeeded, int amountOfItemNeeded,
                        @Nonnull ItemStack itemGiven, int amountOfItemGiven, int neededPermissionLevel,
                        @Nonnull UUID uuid, @Nonnull String name, @Nonnull String description,
                        @Nonnull String workingStepBeingDoneMessage, @Nonnull UUID jobUUID, boolean interactableFirstStage,
                        boolean dropRareItem, double percentage, @Nullable ItemStack rareItem) {
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.itemNeeded = itemNeeded;
        this.amountOfItemNeeded = amountOfItemNeeded;
        this.itemGiven = itemGiven;
        this.amountOfItemGiven = amountOfItemGiven;
        this.neededPermissionLevel = neededPermissionLevel;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
        this.jobUUID = jobUUID;
        this.interactableFirstStage = interactableFirstStage;
        this.dropRareItem = dropRareItem;
        setPercentage(percentage);
        this.rareItem = rareItem;
    }

    /**
     * A working step is a step that the player must do to receive an item. This constructor is used when the player doesn't need an item to do the step.
     *
     * @param workingLocations            The location where the player must be to do the step.
     * @param timeForStep                 The time in ticks that the player must be at the location to do the step.
     * @param itemGiven                   The item that the player will receive when he does the step.
     * @param amountOfItemGiven           The amount of the item that the player will receive when he does the step.
     * @param neededPermissionLevel       The permission level that the player must have to do the step.
     * @param name                        The name of the working step.
     * @param description                 The description of the working step.
     * @param workingStepBeingDoneMessage The message displayed when the working step is being done.
     * @param jobUUID                     The UUID of the job that this working step belongs to.
     * @param interactableFirstStage      Whether the first stage is interactable.
     * @param dropRareItem                Whether a rare item can drop.
     * @param percentage                  The percentage chance of dropping the rare item.
     * @param rareItem                    The rare item that can be dropped.
     */
    public WorkingStep(@Nonnull List<Location> workingLocations, int timeForStep, @Nonnull ItemStack itemGiven, int amountOfItemGiven,
                       int neededPermissionLevel, @Nonnull String name, @Nonnull String description,
                       @Nonnull String workingStepBeingDoneMessage, @Nonnull UUID jobUUID, boolean interactableFirstStage,
                       boolean dropRareItem, double percentage, @Nullable ItemStack rareItem) {
        this(workingLocations, timeForStep, null, 0, itemGiven, amountOfItemGiven, neededPermissionLevel, name,
                description, workingStepBeingDoneMessage, jobUUID, interactableFirstStage, dropRareItem, percentage, rareItem);
    }

    /**
     * Checks if a rare item can drop.
     *
     * @return True if a rare item can drop, false otherwise.
     */
    public boolean isDropRareItem() {
        return dropRareItem;
    }

    /**
     * Sets whether a rare item can drop.
     *
     * @param dropRareItem True if a rare item can drop, false otherwise.
     */
    public void setDropRareItem(boolean dropRareItem) {
        this.dropRareItem = dropRareItem;
    }

    /**
     * Gets the percentage chance of dropping the rare item.
     *
     * @return The percentage chance (0 to 100).
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Sets the percentage chance of dropping the rare item.
     *
     * @param percentage The percentage chance (0 to 100).
     */
    public void setPercentage(double percentage) {
        if (percentage < 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100.");
        }
        this.percentage = percentage;
    }

    /**
     * Gets the rare item that can be dropped.
     *
     * @return The rare item.
     */
    @Nullable
    public ItemStack getRareItem() {
        return rareItem;
    }

    /**
     * Sets the rare item that can be dropped.
     *
     * @param rareItem The rare item.
     */
    public void setRareItem(@Nullable ItemStack rareItem) {
        this.rareItem = rareItem;
    }

    /**
     * Gets the UUID of the job that this working step belongs to.
     *
     * @return The job UUID.
     */
    public UUID getJobUUID() {
        return jobUUID;
    }

    /**
     * Gets the name of the working step.
     *
     * @return The name of the working step.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the working step.
     *
     * @param name The name of the working step.
     */
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Gets the description of the working step.
     *
     * @return The description of the working step.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the working step.
     *
     * @param description The description of the working step.
     */
    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    /**
     * Gets the message displayed when the working step is being done.
     *
     * @return The working step being done message.
     */
    public String getWorkingStepBeingDoneMessage() {
        return workingStepBeingDoneMessage;
    }

    /**
     * Sets the message displayed when the working step is being done.
     *
     * @param workingStepBeingDoneMessage The working step being done message.
     */
    public void setWorkingStepBeingDoneMessage(@Nonnull String workingStepBeingDoneMessage) {
        this.workingStepBeingDoneMessage = workingStepBeingDoneMessage;
    }

    /**
     * Retrieves the needed permission level for the working step.
     *
     * @return The needed permission level.
     */
    public int getNeededPermissionLevel() {
        return neededPermissionLevel;
    }

    /**
     * Sets the needed permission level for the working step.
     *
     * @param neededPermissionLevel The needed permission level for the working step.
     */
    public void setNeededPermissionLevel(int neededPermissionLevel) {
        this.neededPermissionLevel = neededPermissionLevel;
    }

    /**
     * Returns the working location for the given working step.
     *
     * @return The working locations.
     */
    public List<Location> getWorkingLocations() {
        return workingLocations;
    }

    /**
     * Sets the working locations for the given working step.
     *
     * @param workingLocations The locations where the player must be to perform the step.
     */
    public void setWorkingLocations(@Nonnull List<Location> workingLocations) {
        this.workingLocations = workingLocations;
    }

    /**
     * Adds a working location to the list of working locations for this working step.
     *
     * @param location The location to add. (Cannot be null)
     */
    public void addWorkingLocation(@Nonnull Location location) {
        this.workingLocations.add(location);
    }

    /**
     * Removes a working location from the list of working locations for this working step.
     *
     * @param location The location to remove. (Cannot be null)
     */
    public void removeWorkingLocation(@Nonnull Location location) {
        this.workingLocations.remove(location);
    }

    /**
     * Retrieves the time required for the step.
     *
     * @return The time required for the step.
     */
    public int getTimeForStep() {
        return timeForStep;
    }

    /**
     * Sets the time required for the step.
     *
     * @param timeForStep The time in ticks that the player must be at the location to do the step.
     */
    public void setTimeForStep(int timeForStep) {
        this.timeForStep = timeForStep;
    }

    /**
     * Get the item that the player must have in his inventory to do the step.
     *
     * @return The item that the player must have in his inventory to do the step.
     */
    @Nullable
    public ItemStack getItemNeeded() {
        return itemNeeded;
    }

    /**
     * Set the item that the player must have in their inventory to perform the working step.
     *
     * @param itemNeeded The item that the player must have in their inventory.
     */
    public void setItemNeeded(@Nullable ItemStack itemNeeded) {
        this.itemNeeded = itemNeeded;
    }

    /**
     * Get the amount of the item that the player must have in their inventory to perform the working step.
     *
     * @return The amount of the item that the player must have in their inventory.
     */
    public int getAmountOfItemNeeded() {
        return amountOfItemNeeded;
    }

    /**
     * Set the amount of the item that the player must have in their inventory to perform the working step.
     *
     * @param amountOfItemNeeded The amount of the item that the player must have in their inventory.
     */
    public void setAmountOfItemNeeded(int amountOfItemNeeded) {
        this.amountOfItemNeeded = amountOfItemNeeded;
    }

    /**
     * Get the item that the player will receive when they complete the working step.
     *
     * @return The item that the player will receive.
     */
    public ItemStack getItemGiven() {
        return itemGiven;
    }

    /**
     * Set the item that the player will receive when they complete the working step.
     *
     * @param itemGiven The item that the player will receive. (Cannot be null)
     */
    public void setItemGiven(@Nonnull ItemStack itemGiven) {
        this.itemGiven = itemGiven;
    }

    /**
     * Get the amount of the item that the player will receive when they complete the working step.
     *
     * @return The amount of the item that the player will receive.
     */
    public int getAmountOfItemGiven() {
        return amountOfItemGiven;
    }

    /**
     * Set the amount of the item that the player will receive when they complete the working step.
     *
     * @param amountOfItemGiven The amount of the item that the player will receive.
     */
    public void setAmountOfItemGiven(int amountOfItemGiven) {
        this.amountOfItemGiven = amountOfItemGiven;
    }

    /**
     * Gets whether the first stage is interactable.
     *
     * @return True if the first stage is interactable, false otherwise.
     */
    public boolean isInteractableFirstStage() {
        return interactableFirstStage;
    }

    /**
     * Sets whether the first stage is interactable.
     *
     * @param interactableFirstStage True if the first stage should be interactable, false otherwise.
     */
    public void setInteractableFirstStage(boolean interactableFirstStage) {
        this.interactableFirstStage = interactableFirstStage;
    }

    /**
     * Converts a string representation of a WorkingStep object into an actual WorkingStep object.
     *
     * @param s The string representation of the WorkingStep object.
     * @return The WorkingStep object created from the string representation, or null if an error occurs during parsing.
     */
    public static WorkingStep fromString(String s) {
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(s);

            UUID uuid = UUID.fromString(yaml.getString("uuid"));
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
            int amountOfItemNeeded = yaml.getInt("amountOfItemNeeded");
            ItemStack itemGiven = yaml.getItemStack("itemGiven");
            int amountOfItemGiven = yaml.getInt("amountOfItemGiven");
            int neededPermissionLevel = yaml.getInt("neededPermissionLevel");

            String name = yaml.getString("name");
            String description = yaml.getString("description");
            String workingStepBeingDoneMessage = yaml.getString("workingStepBeingDoneMessage");
            UUID jobUUID = UUID.fromString(yaml.getString("jobUUID"));
            boolean interactableFirstStage = yaml.getBoolean("interactableFirstStage", false);

            boolean dropRareItem = yaml.getBoolean("dropRareItem", false);
            double percentage = yaml.getDouble("percentage", 0.0);
            ItemStack rareItem = yaml.getItemStack("rareItem");

            return new WorkingStep(workingLocations, timeForStep, itemNeeded, amountOfItemNeeded,
                    itemGiven, amountOfItemGiven, neededPermissionLevel, uuid, name, description,
                    workingStepBeingDoneMessage, jobUUID, interactableFirstStage, dropRareItem, percentage, rareItem);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while parsing WorkingStep object from string {\n" + s + "\n}, with error: " + e.getMessage());
            return null;
        }
    }

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
        yaml.set("itemGiven", itemGiven);
        yaml.set("amountOfItemGiven", amountOfItemGiven);
        yaml.set("neededPermissionLevel", neededPermissionLevel);
        yaml.set("name", name);
        yaml.set("description", description);
        yaml.set("workingStepBeingDoneMessage", workingStepBeingDoneMessage);
        yaml.set("interactableFirstStage", interactableFirstStage);
        yaml.set("dropRareItem", dropRareItem);
        yaml.set("percentage", percentage);
        if (rareItem != null) {
            yaml.set("rareItem", rareItem);
        }
        return yaml.saveToString();
    }
}
