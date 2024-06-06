package me.fami6xx.rpuniverse.core.holoapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.handlers.FollowHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.handlers.VisibilityHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The HoloAPI is the main class for handling the holograms.
 * It is responsible for loading the holograms, saving the holograms, and handling the hologram handler.
 * <p>
 * The HoloAPI is a singleton class, so only one instance of it should be created.
 * <p>
 * The HoloAPI is also a listener for the PlayerJoinEvent and PlayerQuitEvent.
 * This means that the HoloAPI will also listen for these events and update the holograms accordingly.
 */
public final class HoloAPI {
    private final Map<UUID, List<famiHologram>> playerHolograms = new HashMap<>();

    FollowHoloHandler followHandler;
    VisibilityHoloHandler VisibilityHandler;

    private RPUniverse getPlugin(){
        return RPUniverse.getInstance();
    }

    /**
     * Enable the HoloAPI
     * @return If the HoloAPI was enabled
     */
    public boolean enable() {
        if(!getPlugin().getServer().getPluginManager().isPluginEnabled("DecentHolograms"))
            return false;

        this.followHandler = new FollowHoloHandler();
        this.VisibilityHandler = new VisibilityHoloHandler();

        this.followHandler.start();
        this.VisibilityHandler.start();

        return true;

    }

    public boolean disable() {
        this.followHandler.stop();
        this.VisibilityHandler.stop();

        return true;
    }

    /**
     * Get the player holograms
     * @return The player holograms
     */
    public Map<UUID, List<famiHologram>> getPlayerHolograms() {
        return playerHolograms;
    }

    /**
     * Get the follow handler
     * @return The follow handler
     */
    public FollowHoloHandler getFollowHandler(){
        return this.followHandler;
    }

    /**
     * Get the visibility handler
     * @return The visibility handler
     */
    public VisibilityHoloHandler getVisibilityHandler(){return this.VisibilityHandler;}
}
