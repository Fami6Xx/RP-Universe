package me.fami6xx.rpuniverse.core.holoapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.handlers.FollowHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.handlers.VisibilityHoloHandler;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import me.fami6xx.rpuniverse.core.holoapi.types.lines.UpdatingLine;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Random;

public final class HoloAPI implements Listener {
    // ToDo: Remake from holographic displays to decent holograms
    Random random = new Random();
    FollowHoloHandler followHandler;
    VisibilityHoloHandler VisibilityHandler;

    private RPUniverse getPlugin(){
        return RPUniverse.getInstance();
    }

    public boolean enable() {
        if(!getPlugin().getServer().getPluginManager().isPluginEnabled("HolographicDisplays"))
            return false;
        if(!getPlugin().getServer().getPluginManager().isPluginEnabled("ProtocolLib"))
            return false;
        this.followHandler = new FollowHoloHandler();
        this.VisibilityHandler = new VisibilityHoloHandler();

        this.followHandler.start();
        this.VisibilityHandler.start();

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        return true;

    }

    public boolean disable() {
        this.followHandler.stop();
        this.VisibilityHandler.stop();

        PlayerLoginEvent.getHandlerList().unregister(this);
        EntitySpawnEvent.getHandlerList().unregister(this);

        return true;
    }

    public FollowHoloHandler getFollowHandler(){
        return this.followHandler;
    }
    public VisibilityHoloHandler getVisibilityHandler(){return this.VisibilityHandler;}

    // Methods below are there only for testing purposes
    @EventHandler
    public void onConnect(PlayerLoginEvent event){
        FollowingHologram holo = new FollowingHologram(event.getPlayer(), 5, false, false);
        for(int i = 0; i < random.nextInt(10); i++){
            if(random.nextBoolean()) {
                new UpdatingLine(holo.getHologram().appendTextLine("")) {
                    @Override
                    public String update() {
                        return event.getPlayer().getHealth() + "";
                    }
                };
            }else{
                FollowingHologram boomRandom = new FollowingHologram(event.getPlayer(), 5, false, false);
                boomRandom.getHologram().appendTextLine("Randomly created line");
            }
        }
    }
    @EventHandler
    public void onBob(EntitySpawnEvent event){
        FollowingHologram holo = new FollowingHologram(event.getEntity(), 5, false, false);
        for (int i = 0; i < random.nextInt(4); i++){
            if(random.nextBoolean()) {
                new UpdatingLine(holo.getHologram().appendTextLine(""), 5) {
                    @Override
                    public String update() {
                        try {
                            return ((LivingEntity) event.getEntity()).getHealth() + "";
                        } catch (Exception exc) {
                            return "Not Living Entity";
                        }
                    }
                };
            }else {
                FollowingHologram boomRandom = new FollowingHologram(event.getEntity(), 5, false, false);
                boomRandom.getHologram().appendTextLine("Randomly created line");
            }
        }
    }
}
