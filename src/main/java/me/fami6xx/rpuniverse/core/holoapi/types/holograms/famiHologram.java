package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class famiHologram {
    private final Hologram hologram;
    private final UUID uuid = UUID.randomUUID();

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

    public void updatedPlayerMode(PlayerData data){
        if(data.getBindedPlayer() == null) return;

        if(hologram.getPlayerPage(data.getBindedPlayer()) != getPageToDisplay(data.getBindedPlayer())){
            hologram.show(data.getBindedPlayer(), getPageToDisplay(data.getBindedPlayer()));
        }
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

        api.getVisibilityHandler().queue.add(() -> api.getVisibilityHandler().removeFromList(getUUID(), this));

        if(!hologram.isDisabled() && !hologram.isDefaultVisibleState()){
            hologram.hideAll();
        }


        getHologram().delete();

        Entity owner = this instanceof FollowingHologram ? ((FollowingHologram) this).getFollowing() : null;
        if (owner != null) {
            List<famiHologram> playerHolograms = RPUniverse.getInstance().getHoloAPI().getPlayerHolograms().get(owner.getUniqueId());
            if (playerHolograms != null) {
                playerHolograms.remove(this);
            }
        }
    };

    /**
     * Shows hologram to player
     * @param player Player to show hologram to
     */
    public void show(Player player){
        System.out.println(" ");
        System.out.println("Showing hologram to player " + player.getName());
        if(hologram.isDisabled())
            return;

        System.out.println("Hologram is not disabled");
        if(hologram.isShowState(player))
            return;

        System.out.println("Hologram is not in show state for player");
        if(hologram.isVisible(player)){
            hologram.removeShowPlayer(player);
            System.out.println("Hologram is visible for player");
            return;
        }

        System.out.println("Hologram is not visible for player.. showing holo");
        hologram.setShowPlayer(player);
        hologram.show(player, getPageToDisplay(player));
    }

    /**
     * This method is used to retrieve the page number to display for a given player.
     *
     * @param player The player for whom to retrieve the page number.
     * @return The page number to display for the given player.
     */
    public abstract int getPageToDisplay(Player player);

    /**
     * Hides hologram from player
     * @param player Player to hide hologram from
     */
    public void hide(Player player){
        System.out.println(" ");
        System.out.println("Hiding hologram from player " + player.getName());
        if(hologram.isDisabled())
            return;

        System.out.println("Hologram is not disabled");
        if(!hologram.isShowState(player))
            return;

        System.out.println("Hologram is in show state for player");
        if(!hologram.isVisible(player))
            return;

        System.out.println("Hologram is visible for player.. hiding holo");
        hologram.removeShowPlayer(player);
    }

    /**
     * Checks if hologram is visible to player
     * @param player Player to check visibility for
     * @return true if hologram is visible to player, false otherwise
     */
    public boolean isVisible(Player player){
        return hologram.isVisible(player) && hologram.isShowState(player);
    }
}
