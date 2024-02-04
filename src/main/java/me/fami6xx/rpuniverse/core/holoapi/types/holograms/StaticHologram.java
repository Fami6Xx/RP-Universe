package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaticHologram extends famiHologram {
    HoloAPI api = RPUniverse.getInstance().getHoloAPI();

    public StaticHologram(Location loc){
        super(
                DHAPI.createHologram(UUID.randomUUID().toString(), loc)
        );

        getHologram().setDefaultVisibleState(true);
        updateVisibility(-1, false);

        api.getVisibilityHandler().queue.add(
                () -> api.getVisibilityHandler().addToList(getUUID(), this)
        );
    }

    public StaticHologram(Location loc, boolean isVisibleByDefault, double visibleDistance, boolean seeThroughBlocks){
        super(
                DHAPI.createHologram(UUID.randomUUID().toString(), loc)
        );

        getHologram().setDefaultVisibleState(isVisibleByDefault);
        updateVisibility(visibleDistance, seeThroughBlocks);

        api.getVisibilityHandler().queue.add(
                () -> api.getVisibilityHandler().addToList(getUUID(), this)
        );
    }

    @Override
    public int getPageToDisplay(Player player){
        return 0;
    }

    @Override
    public boolean shouldShow(Player player){
        return true;
    }

    @Override
    public Location getBaseLocation(){
        return getHologram().getLocation().clone();
    }
}
