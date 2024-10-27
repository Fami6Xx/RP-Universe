package me.fami6xx.rpuniverse.core.jobs;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.Bukkit;
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
import java.util.stream.Collectors;

/**
 * A working step is a step that the player must do to receive an item.
 * It can be set that the player must have a specific item in his inventory to do the step.
 */
public class WorkingStep {
    private static final Logger LOGGER = RPUniverse.getInstance().getLogger();

    private UUID uuid = UUID.randomUUID();
    private List<Location> workingLocations;
    private int timeForStep; // In ticks
    private ItemStack itemNeeded; // Can be null
    private int amountOfItemNeeded; // Can be 0
    private ItemStack itemGiven; // Cannot be null
    private int amountOfItemGiven; // Must be more than zero
    private int neededPermissionLevel; // Must be more than zero

    /**
     * A working step is a step that the player must do to receive an item. The player must have a specific item in his inventory to do the step.
     * @param workingLocations The location where the player must be to do the step.
     * @param timeForStep The time in ticks that the player must be at the location to do the step.
     * @param itemNeeded The item that the player must have in his inventory to do the step.
     * @param amountOfItemNeeded The amount of the item that the player must have in his inventory to do the step.
     * @param itemGiven The item that the player will receive when he does the step.
     * @param amountOfItemGiven The amount of the item that the player will receive when he does the step.
     * @param neededPermissionLevel The permission level that the player must have to do the step.
     */
    public WorkingStep(@Nonnull List<Location> workingLocations, int timeForStep, ItemStack itemNeeded, int amountOfItemNeeded, @Nonnull ItemStack itemGiven, int amountOfItemGiven, int neededPermissionLevel){
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.itemNeeded = itemNeeded;
        this.amountOfItemNeeded = amountOfItemNeeded;
        this.itemGiven = itemGiven;
        this.amountOfItemGiven = amountOfItemGiven;
        this.neededPermissionLevel = neededPermissionLevel;
    }

    /**
     * A working step is a step that the player must do to receive an item. The player must have a specific item in his inventory to do the step.
     * @param workingLocations The location where the player must be to do the step.
     * @param timeForStep The time in ticks that the player must be at the location to do the step.
     * @param itemNeeded The item that the player must have in his inventory to do the step.
     * @param amountOfItemNeeded The amount of the item that the player must have in his inventory to do the step.
     * @param itemGiven The item that the player will receive when he does the step.
     * @param amountOfItemGiven The amount of the item that the player will receive when he does the step.
     * @param neededPermissionLevel The permission level that the player must have to do the step.
     * @param uuid The unique identifier of the working step.
     */
    private WorkingStep(@Nonnull List<Location> workingLocations, int timeForStep, ItemStack itemNeeded, int amountOfItemNeeded, @Nonnull ItemStack itemGiven, int amountOfItemGiven, int neededPermissionLevel, UUID uuid){
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.itemNeeded = itemNeeded;
        this.amountOfItemNeeded = amountOfItemNeeded;
        this.itemGiven = itemGiven;
        this.amountOfItemGiven = amountOfItemGiven;
        this.neededPermissionLevel = neededPermissionLevel;
        this.uuid = uuid;
    }

    /**
     * A working step is a step that the player must do to receive an item. This constructor is used when the player doesn't need an item to do the step.
     * @param workingLocations The location where the player must be to do the step.
     * @param timeForStep The time in ticks that the player must be at the location to do the step.
     * @param itemGiven The item that the player will receive when he does the step.
     * @param amountOfItemGiven The amount of the item that the player will receive when he does the step.
     * @param neededPermissionLevel The permission level that the player must have to do the step.
     */
    public WorkingStep(@Nonnull List<Location> workingLocations, int timeForStep,@Nonnull ItemStack itemGiven, int amountOfItemGiven, int neededPermissionLevel){
        this.workingLocations = workingLocations;
        this.timeForStep = timeForStep;
        this.itemNeeded = null;
        this.amountOfItemNeeded = 0;
        this.itemGiven = itemGiven;
        this.amountOfItemGiven = amountOfItemGiven;
        this.neededPermissionLevel = neededPermissionLevel;
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
     * @return The working location.
     */
    public List<Location> getWorkingLocations() {
        return workingLocations;
    }

    /**
     * Sets the working location for the given working step.
     *
     * @param workingLocations The location where the player must be to perform the step.
     */
    public void setWorkingLocations(@Nonnull List<Location> workingLocations) {
        this.workingLocations = workingLocations;
    }

    /**
     * Adds a working location to the list of working locations for this working step.
     *
     * @param location The location to add. (Cannot be null)
     */
    public void addWorkingLocation(@Nonnull Location location){
        this.workingLocations.add(location);
    }

    /**
     * Removes a working location from the list of working locations for this working step.
     *
     * @param location The location to remove. (Cannot be null)
     */
    public void removeWorkingLocation(@Nonnull Location location){
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
    public void setItemNeeded(ItemStack itemNeeded) {
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
            List<Location> workingLocations = (List<Location>) yaml.getList("workingLocations");
            int timeForStep = yaml.getInt("timeForStep");
            ItemStack itemNeeded = yaml.getItemStack("itemNeeded");
            int amountOfItemNeeded = yaml.getInt("amountOfItemNeeded");
            ItemStack itemGiven = yaml.getItemStack("itemGiven");
            int amountOfItemGiven = yaml.getInt("amountOfItemGiven");
            int neededPermissionLevel = yaml.getInt("neededPermissionLevel");

            return new WorkingStep(workingLocations, timeForStep, itemNeeded, amountOfItemNeeded, itemGiven, amountOfItemGiven, neededPermissionLevel, uuid);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while parsing WorkingStep object from string {\n" + s + "\n}, with error: " + e.getMessage());
            return null;
        }
    }


    @Override
    public String toString() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("uuid", uuid.toString());
        yaml.set("workingLocations", workingLocations);
        yaml.set("timeForStep", timeForStep);
        if (itemNeeded != null) {
            yaml.set("itemNeeded", itemNeeded);
        }
        yaml.set("amountOfItemNeeded", amountOfItemNeeded);
        yaml.set("itemGiven", itemGiven);
        yaml.set("amountOfItemGiven", amountOfItemGiven);
        yaml.set("neededPermissionLevel", neededPermissionLevel);
        return yaml.saveToString();
    }

}