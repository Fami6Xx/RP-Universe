package me.fami6xx.rpuniverse.core.menuapi;

import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

/**
 * Represents a player's menu state and interactions.
 * <p>
 * This class serves as a wrapper around a Player object that adds menu-related functionality and state.
 * It tracks which menu a player currently has open, any job they're currently editing, and any pending
 * actions that require input from the player.
 * <p>
 * PlayerMenu instances are managed by the MenuManager, which creates them on-demand when a player
 * needs to interact with a menu.
 */
public class PlayerMenu {
    /**
     * The player associated with this menu state.
     */
    private final Player player;

    /**
     * The menu currently being displayed to the player, or null if no menu is open.
     */
    private Menu currentMenu;

    /**
     * The job the player is currently editing, or null if they're not editing a job.
     */
    private Job editingJob;

    /**
     * A pending action that will be executed when the player provides input,
     * or null if there's no pending action.
     */
    private Consumer<String> pendingAction;

    /**
     * Creates a new PlayerMenu for the specified player.
     *
     * @param p the player to create a menu for
     */
    protected PlayerMenu(Player p) {
        this.player = p;
    }

    /**
     * Gets the player associated with this menu state.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the menu currently being displayed to the player.
     *
     * @param menu the menu to set as current, or null to indicate no menu is open
     */
    public void setCurrentMenu(Menu menu) {
        this.currentMenu = menu;
    }

    /**
     * Gets the menu currently being displayed to the player.
     *
     * @return the current menu, or null if no menu is open
     */
    public Menu getCurrentMenu() {
        return currentMenu;
    }

    /**
     * Gets the job the player is currently editing.
     *
     * @return the job being edited, or null if the player is not editing a job
     */
    public Job getEditingJob() {
        return editingJob;
    }

    /**
     * Sets the job the player is currently editing.
     *
     * @param job the job to set as being edited, or null to indicate no job is being edited
     */
    public void setEditingJob(Job job) {
        this.editingJob = job;
    }

    /**
     * Sets a pending action that will be executed when the player provides input.
     * <p>
     * This is typically used for actions that require text input from the player,
     * such as naming an item or entering a value.
     *
     * @param action the action to execute when the player provides input,
     *               or null to clear the pending action
     */
    public void setPendingAction(Consumer<String> action) {
        this.pendingAction = action;
    }

    /**
     * Gets the pending action that will be executed when the player provides input.
     *
     * @return the pending action, or null if there's no pending action
     */
    public Consumer<String> getPendingAction() {
        return pendingAction;
    }

    /**
     * Clears the pending action, indicating that no action is waiting for player input.
     */
    public void clearPendingAction() {
        this.pendingAction = null;
    }
}
