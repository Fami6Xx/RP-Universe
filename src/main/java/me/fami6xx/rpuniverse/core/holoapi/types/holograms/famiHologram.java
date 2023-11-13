package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Location;

import java.util.UUID;

public abstract class famiHologram {
    private final Hologram hologram;
    private final UUID uuid = UUID.randomUUID();

    private double distance;
    private boolean seeThroughBlocks;

    public famiHologram(Hologram holo){
        hologram = holo;
    }

    public void updateVisibility(double distance, boolean seeThroughBlocks){
        this.distance = distance;
        this.seeThroughBlocks = seeThroughBlocks;
    }

    public double getDistance() {
        return distance;
    }

    public int getIntDistance(){
        return (int) Math.ceil(distance);
    }

    public boolean canSeeThroughBlocks() {
        return seeThroughBlocks;
    }

    public Hologram getHologram(){
        return hologram;
    }

    public UUID getUUID() {
        return uuid;
    }

    /**
     * This function is only for Visibility calculations as it needs to get from where to send RayTrace
     */
    public abstract Location getBaseLocation();

    public abstract void destroy();
}
