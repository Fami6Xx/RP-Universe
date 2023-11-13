package me.fami6xx.rpuniverse.core.holoapi.types.holograms;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class FollowingHologram extends famiHologram {

    FollowingHologram followingHologram = this;

    HoloAPI api = (HoloAPI) RPUniverse.getInstance().getHoloAPI();

    Entity following;
    UUID followingUUID;


    public FollowingHologram(Entity toFollow, double visibleDistance, boolean isVisibleByDefault, boolean seeThroughBlocks){
        super(
                HologramsAPI.createHologram(
                        RPUniverse.getInstance(),
                        toFollow.getLocation().add(
                                0,
                                (RPUniverse.getInstance().getHoloAPI().getFollowHandler().calculateHeight(toFollow.getUniqueId())),
                                0
                        )
                )
        );
        following = toFollow;
        updateVisibility(visibleDistance, seeThroughBlocks);
        if(!isVisibleByDefault)
            updateVisibility(visibleDistance, seeThroughBlocks);
        else
            updateVisibility(-1, seeThroughBlocks);

        getHologram().getVisibilityManager().setVisibleByDefault(isVisibleByDefault);

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
