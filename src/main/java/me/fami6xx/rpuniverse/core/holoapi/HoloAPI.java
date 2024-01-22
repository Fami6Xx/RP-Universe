package me.fami6xx.rpuniverse.core.holoapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.handlers.FollowHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.handlers.VisibilityHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HoloAPI {
    private final Map<UUID, List<famiHologram>> playerHolograms = new HashMap<>();

    FollowHoloHandler followHandler;
    VisibilityHoloHandler VisibilityHandler;

    private RPUniverse getPlugin(){
        return RPUniverse.getInstance();
    }

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

    public Map<UUID, List<famiHologram>> getPlayerHolograms() {
        return playerHolograms;
    }

    public FollowHoloHandler getFollowHandler(){
        return this.followHandler;
    }
    public VisibilityHoloHandler getVisibilityHandler(){return this.VisibilityHandler;}
}
