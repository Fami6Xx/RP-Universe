package me.fami6xx.rpuniverse.core.holoapi.handlers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class FollowHoloHandler extends famiHoloHandler {
    public double calculateHeight(UUID uuid){
        if(Bukkit.getEntity(uuid) == null)
            return 0;
        double[] height = {Bukkit.getEntity(uuid).getHeight() + 0.5};

        getMap().get(uuid).forEach(holo -> height[0] += holo.getHologram().size() * 0.25);

        return height[0];
    }

    @Override
    public BukkitTask startTask(){
        return new BukkitRunnable(){
            @Override
            public void run() {
                // Checking queue and if there is something then executing it safely in this thread
                handleQueue();

                // Cloning HashMap because we are modifying it inside the forEach loop
                getMap().forEach(((uuid, famiHolograms) -> {
                    Entity entity = Bukkit.getEntity(uuid);

                    // Check entity
                    if(entity == null){
                        // Has to be handled outside for loop otherwise it would throw ConcurrentModificationExc
                        queue.add(

                                () -> clearList(uuid)
                        );
                        return;
                    }
                    if(!entity.isValid()){
                        if(entity instanceof Player) {
                            if(!((Player) entity).isOnline()){
                                queue.add(
                                        () -> clearList(uuid)
                                );
                                return;
                            }
                        }else{
                            queue.add(
                                    () -> clearList(uuid)
                            );
                            return;
                        }
                    }

                    double height = entity.getHeight() + 0.5;

                    famiHologram[] arr = famiHolograms.toArray(new famiHologram[0]);

                    for(famiHologram holo : arr) {
                        if(holo.getHologram().isDeleted()){
                            queue.add(
                                    () -> removeFromList(uuid, holo)
                            );
                            continue;
                        }

                        // Move Hologram

                        Location toTeleport = entity.getLocation();
                        height += holo.getHologram().size() * 0.25;
                        toTeleport.setY(toTeleport.getY() + height);

                        if(
                                toTeleport.getX() != holo.getHologram().getX() ||
                                toTeleport.getY() != holo.getHologram().getY() ||
                                toTeleport.getZ() != holo.getHologram().getZ()
                        ) {
                            holo.getHologram().teleport(toTeleport);
                        }
                    }
                }));
            }
        }.runTaskTimerAsynchronously(RPUniverse.getInstance(), 0L, 1L);
    }
}
