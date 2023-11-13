package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import org.bukkit.Location;

public class StaticHologram extends famiHologram {
    HoloAPI api = RPUniverse.getInstance().getHoloAPI();

    public StaticHologram(Location loc, boolean isVisibleByDefault, double visibleDistance, boolean seeThroughBlocks){
        super(
                HologramsAPI.createHologram(RPUniverse.getInstance(), loc)
        );

        getHologram().getVisibilityManager().setVisibleByDefault(isVisibleByDefault);
        updateVisibility(visibleDistance, seeThroughBlocks);

        StaticHologram staticHologram = this;

        api.getVisibilityHandler().queue.add(
                () -> api.getVisibilityHandler().addToList(getUUID(), staticHologram)
        );
    }

    @Override
    public Location getBaseLocation(){
        return getHologram().getLocation().clone();
    }

    @Override
    public void destroy() {
        StaticHologram staticHologram = this;

        api.getVisibilityHandler().queue.add(
                () -> api.getVisibilityHandler().removeFromList(getUUID(), staticHologram)
        );
    }
}
