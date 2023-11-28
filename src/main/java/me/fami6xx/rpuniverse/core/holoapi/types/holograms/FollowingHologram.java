package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class FollowingHologram extends famiHologram {

    FollowingHologram followingHologram = this;

    HoloAPI api = RPUniverse.getInstance().getHoloAPI();

    Entity following;
    UUID followingUUID;

    public FollowingHologram(Entity toFollow){
        super(
                DHAPI.createHologram(UUID.randomUUID().toString(), toFollow.getLocation().clone().add(0, toFollow.getHeight(), 0))
        );
        following = toFollow;
        updateVisibility(-1, false);
        getHologram().setDefaultVisibleState(true);

        api.getFollowHandler().queue.add(() -> api.getFollowHandler().addToList(toFollow.getUniqueId(), followingHologram));
        followingUUID = toFollow.getUniqueId();
    }

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
    }

    public Entity getFollowing(){
        return following;
    }

    @Override
    public Location getBaseLocation() {
        return following.getLocation().clone().add(0, following.getHeight(), 0);
    }

    @Override
    public void destroy(){
        api.getFollowHandler().queue.add(() -> api.getFollowHandler().removeFromList(followingUUID, followingHologram));
        api.getVisibilityHandler().queue.add(() -> api.getVisibilityHandler().removeFromList(followingUUID, followingHologram));
        getHologram().delete();
    }

    @Override
    public String toString(){
        return "FollowingHologram [uuid: " + getUUID() + ", canSeeThrough:" + canSeeThroughBlocks() + ", maxVisibleDistance:" + getDistance() + ", " + getHologram().toString() + "]";
    }
}
