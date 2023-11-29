package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class famiHologram {
    private final Hologram hologram;
    private final UUID uuid = UUID.randomUUID();
    private final List<Player> currentlyVisiblePlayers = new ArrayList<>();

    private double distance;
    private boolean seeThroughBlocks;

    public famiHologram(Hologram holo){
        hologram = holo;
    }

    public HologramLine addLine(String line){
        return DHAPI.addHologramLine(hologram, line);
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

    /**
     * Destroys hologram and hides it from all players
     */
    public void destroy() {
        HoloAPI api = RPUniverse.getInstance().getHoloAPI();
        if(this instanceof FollowingHologram) {
            api.getFollowHandler().queue.add(() -> api.getFollowHandler().removeFromList(((FollowingHologram) this).getFollowing().getUniqueId(), this));
        }

        if(!hologram.isDisabled() && !hologram.isDefaultVisibleState()){
            hologram.getPages().forEach(page -> page.getLines().forEach(line -> line.hide(currentlyVisiblePlayers.toArray(new Player[0]))));
        }

        api.getVisibilityHandler().queue.add(() -> api.getVisibilityHandler().removeFromList(getUUID(), this));

        getHologram().delete();
    };

    /**
     * Shows hologram to player
     * @param player Player to show hologram to
     */
    public void show(Player player){
        if(hologram.isDisabled())
            return;

        if(currentlyVisiblePlayers.contains(player))
            return;

        hologram.getPages().forEach(page -> page.getLines().forEach(line -> line.show(player)));
        currentlyVisiblePlayers.add(player);
    }

    /**
     * Hides hologram from player
     * @param player Player to hide hologram from
     */
    public void hide(Player player){
        if(hologram.isDisabled())
            return;

        if(!currentlyVisiblePlayers.contains(player))
            return;

        hologram.getPages().forEach(page -> page.getLines().forEach(line -> line.hide(player)));
        currentlyVisiblePlayers.remove(player);
    }

    /**
     * Checks if hologram is visible to player
     * @param player Player to check visibility for
     * @return true if hologram is visible to player, false otherwise
     */
    public boolean isVisible(Player player){
        return currentlyVisiblePlayers.contains(player);
    }
}
