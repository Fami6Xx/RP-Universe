package me.fami6xx.rpuniverse.core.jobs.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A working step is a step that the player must do to receive an item.
 * It can be set that the player must have a specific item in his inventory to do the step.
 */
public class WorkingStep {
    private Location workingLocation;
    private int timeForStep; // In ticks
    private ItemStack itemNeeded; // Can be null
    private int amountOfItemNeeded; // Can be 0
    private ItemStack itemGiven; // Cannot be null
    private int amountOfItemGiven; // Must be more than zero
    private int neededPermissionLevel; // Must be more than zero

    /**
     * A working step is a step that the player must do to receive an item. The player must have a specific item in his inventory to do the step.
     * @param workingLocation The location where the player must be to do the step.
     * @param timeForStep The time in ticks that the player must be at the location to do the step.
     * @param itemNeeded The item that the player must have in his inventory to do the step.
     * @param amountOfItemNeeded The amount of the item that the player must have in his inventory to do the step.
     * @param itemGiven The item that the player will receive when he does the step.
     * @param amountOfItemGiven The amount of the item that the player will receive when he does the step.
     * @param neededPermissionLevel The permission level that the player must have to do the step.
     */
    public WorkingStep(@Nonnull Location workingLocation, int timeForStep, ItemStack itemNeeded, int amountOfItemNeeded, @Nonnull ItemStack itemGiven, int amountOfItemGiven, int neededPermissionLevel){
        this.workingLocation = workingLocation;
        this.timeForStep = timeForStep;
        this.itemNeeded = itemNeeded;
        this.amountOfItemNeeded = amountOfItemNeeded;
        this.itemGiven = itemGiven;
        this.amountOfItemGiven = amountOfItemGiven;
        this.neededPermissionLevel = neededPermissionLevel;
    }

    /**
     * A working step is a step that the player must do to receive an item. This constructor is used when the player doesn't need an item to do the step.
     * @param workingLocation The location where the player must be to do the step.
     * @param timeForStep The time in ticks that the player must be at the location to do the step.
     * @param itemGiven The item that the player will receive when he does the step.
     * @param amountOfItemGiven The amount of the item that the player will receive when he does the step.
     * @param neededPermissionLevel The permission level that the player must have to do the step.
     */
    public WorkingStep(@Nonnull Location workingLocation, int timeForStep,@Nonnull ItemStack itemGiven, int amountOfItemGiven, int neededPermissionLevel){
        this.workingLocation = workingLocation;
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
    public Location getWorkingLocation() {
        return workingLocation;
    }

    /**
     * Sets the working location for the given working step.
     *
     * @param workingLocation The location where the player must be to perform the step.
     */
    public void setWorkingLocation(@Nonnull Location workingLocation) {
        this.workingLocation = workingLocation;
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
            String[] parts = s.substring(s.indexOf('{') + 1, s.lastIndexOf('}')).split(", ");

            // Parsing Location
            Location workingLocation = null;
            if (!parts[0].equals("null")) {
                String[] locParts = parts[0].split(",");
                workingLocation = new Location(
                        Bukkit.getWorld(locParts[0]),
                        Double.parseDouble(locParts[1]),
                        Double.parseDouble(locParts[2]),
                        Double.parseDouble(locParts[3]),
                        Float.parseFloat(locParts[4]),
                        Float.parseFloat(locParts[5])
                );
            }

            int timeForStep = Integer.parseInt(parts[1]);

            // Parsing ItemStack for itemNeeded
            ItemStack itemNeeded = null;
            if (!parts[2].equals("null")) {
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.loadFromString(parts[2]);
                itemNeeded = yaml.getItemStack("item");
            }

            int amountOfItemNeeded = Integer.parseInt(parts[3]);

            // Parsing ItemStack for itemGiven
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(parts[4]);
            ItemStack itemGiven = yaml.getItemStack("item");

            int amountOfItemGiven = Integer.parseInt(parts[5]);
            int neededPermissionLevel = Integer.parseInt(parts[6]);

            return new WorkingStep(workingLocation, timeForStep, itemNeeded, amountOfItemNeeded, itemGiven, amountOfItemGiven, neededPermissionLevel);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        YamlConfiguration yaml = new YamlConfiguration();

        StringBuilder sb = new StringBuilder("WorkingStep{");

        // Serialize Location
        if (workingLocation != null) {
            sb.append(workingLocation.getWorld().getName()).append(",")
                    .append(workingLocation.getX()).append(",")
                    .append(workingLocation.getY()).append(",")
                    .append(workingLocation.getZ()).append(",")
                    .append(workingLocation.getYaw()).append(",")
                    .append(workingLocation.getPitch()).append(", ");
        } else {
            sb.append("null, ");
        }

        // Serialize timeForStep
        sb.append(timeForStep).append(", ");

        // Serialize itemNeeded
        if (itemNeeded != null) {
            yaml.set("item", itemNeeded);
            sb.append(yaml.saveToString()).append(", ");
        } else {
            sb.append("null, ");
        }

        // Serialize amountOfItemNeeded
        sb.append(amountOfItemNeeded).append(", ");

        // Serialize itemGiven
        yaml = new YamlConfiguration();
        yaml.set("item", itemGiven);
        sb.append(yaml.saveToString()).append(", ");

        // Serialize amountOfItemGiven and neededPermissionLevel
        sb.append(amountOfItemGiven).append(", ")
                .append(neededPermissionLevel);

        sb.append('}');

        return sb.toString();
    }
}