package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FollowingHologram extends famiHologram {

    FollowingHologram followingHologram = this;

    HoloAPI api = RPUniverse.getInstance().getHoloAPI();

    Entity following;
    UUID followingUUID;

    /**
     * Creates a new FollowingHologram that will be visible by default and can be seen through blocks with no max visible distance
     * @param toFollow Entity to follow
     */
    public FollowingHologram(Entity toFollow){
        super(
                DHAPI.createHologram(UUID.randomUUID().toString(), toFollow.getLocation().clone().add(0, toFollow.getHeight(), 0))
        );
        following = toFollow;
        updateVisibility(-1, false);
        getHologram().setDefaultVisibleState(true);

        api.getFollowHandler().queue.add(() -> api.getFollowHandler().addToList(toFollow.getUniqueId(), followingHologram));
        followingUUID = toFollow.getUniqueId();

        if(toFollow instanceof Player){
            List<famiHologram> playerHolograms = api.getPlayerHolograms().computeIfAbsent(toFollow.getUniqueId(), k -> new ArrayList<>());
            playerHolograms.add(this);
        }
    }

    /**
     * Creates a new FollowingHologram that will stay on the entity until it is destroyed
     * @param toFollow Entity to follow
     * @param visibleDistance Distance in blocks in which the hologram is visible
     * @param isVisibleByDefault If the hologram is visible by default
     * @param seeThroughBlocks If the hologram can be seen through blocks
     */
    public FollowingHologram(Entity toFollow, double visibleDistance, boolean isVisibleByDefault, boolean seeThroughBlocks){
        super(
                DHAPI.createHologram(UUID.randomUUID().toString(), toFollow.getLocation().clone().add(0, toFollow.getHeight(), 0))
        );
        following = toFollow;
        updateVisibility(visibleDistance, seeThroughBlocks);
        if(!isVisibleByDefault)
            updateVisibility(visibleDistance, seeThroughBlocks);
        else
            updateVisibility(-1, seeThroughBlocks);

        getHologram().setDefaultVisibleState(isVisibleByDefault);

        api.getFollowHandler().queue.add(() -> api.getFollowHandler().addToList(toFollow.getUniqueId(), followingHologram));
        if(!isVisibleByDefault) {
            api.getVisibilityHandler().queue.add(
                    () -> api.getVisibilityHandler().addToList(getUUID(), followingHologram)
            );
        }

        followingUUID = toFollow.getUniqueId();

        if(toFollow instanceof Player){
            List<famiHologram> playerHolograms = api.getPlayerHolograms().computeIfAbsent(toFollow.getUniqueId(), k -> new ArrayList<>());
            playerHolograms.add(this);
        }
    }

    /**
     * Creates a new FollowingHologram that will stay on the entity for a certain amount of time
     * @param toFollow Entity to follow
     * @param visibleDistance Distance in blocks in which the hologram is visible
     * @param isVisibleByDefault If the hologram is visible by default
     * @param seeThroughBlocks If the hologram can be seen through blocks
     * @param timeAlive Time in ticks after which the hologram will be destroyed
     */
    public FollowingHologram(Entity toFollow, double visibleDistance, boolean isVisibleByDefault, boolean seeThroughBlocks, int timeAlive){
        super(
                DHAPI.createHologram(UUID.randomUUID().toString(), toFollow.getLocation().clone().add(0, toFollow.getHeight(), 0))
        );
        following = toFollow;
        updateVisibility(visibleDistance, seeThroughBlocks);
        if(!isVisibleByDefault)
            updateVisibility(visibleDistance, seeThroughBlocks);
        else
            updateVisibility(-1, seeThroughBlocks);

        getHologram().setDefaultVisibleState(isVisibleByDefault);

        api.getFollowHandler().queue.add(() -> api.getFollowHandler().addToList(toFollow.getUniqueId(), followingHologram));
        if(!isVisibleByDefault) {
            api.getVisibilityHandler().queue.add(
                    () -> api.getVisibilityHandler().addToList(getUUID(), followingHologram)
            );
        }

        followingUUID = toFollow.getUniqueId();

        if(toFollow instanceof Player){
            List<famiHologram> playerHolograms = api.getPlayerHolograms().computeIfAbsent(toFollow.getUniqueId(), k -> new ArrayList<>());
            playerHolograms.add(this);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                destroy();
            }
        }.runTaskLater(RPUniverse.getInstance(), timeAlive);
    }

    public Entity getFollowing(){
        return following;
    }

    @Override
    public Location getBaseLocation() {
        return following.getLocation().clone().add(0, following.getHeight(), 0);
    }

    @Override
    public int getPageToDisplay(Player player) {
        return 0;
    }

    @Override
    public String toString(){
        return "FollowingHologram [uuid: " + getUUID() + ", canSeeThrough:" + canSeeThroughBlocks() + ", maxVisibleDistance:" + getDistance() + ", " + getHologram().toString() + "]";
    }
}
