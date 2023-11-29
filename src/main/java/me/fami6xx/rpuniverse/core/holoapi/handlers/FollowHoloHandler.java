package me.fami6xx.rpuniverse.core.holoapi.handlers;

import eu.decentsoftware.holograms.api.DHAPI;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class FollowHoloHandler extends famiHoloHandler {
    public double calculateHeight(UUID uuid){
        Entity entity = Bukkit.getEntity(uuid);
        if(entity == null)
            return 0;

        double height = 0;

        if(entity instanceof Player){
            height = entity.getHeight() + 0.5;
        } else if(entity instanceof LivingEntity){
            height = entity.getHeight() + 0.25;
        }else{
            height = entity.getHeight() + 0.35;
        }

        return height;
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
                        famiHolograms.forEach(famiHologram::destroy);
                        queue.add(() -> clearList(uuid));
                        return;
                    }

                    if(!entity.isValid()){
                        if(!(entity instanceof Player)) {
                            famiHolograms.forEach(famiHologram::destroy);
                            queue.add(() -> clearList(uuid));
                            return;
                        }

                        if(!((Player) entity).isOnline()){
                            famiHolograms.forEach(famiHologram::destroy);
                            queue.add(() -> clearList(uuid));
                            return;
                        }
                    }

                    double height = calculateHeight(uuid);

                    famiHologram[] arr = famiHolograms.toArray(new famiHologram[0]);

                    for(famiHologram holo : arr) {
                        if(holo.getHologram().isDisabled()){
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
                                toTeleport.getX() != holo.getHologram().getLocation().getX() ||
                                toTeleport.getY() != holo.getHologram().getLocation().getY() ||
                                toTeleport.getZ() != holo.getHologram().getLocation().getZ()
                        ) {
                            DHAPI.moveHologram(holo.getHologram(), toTeleport);
                        }
                    }
                }));
            }
        }.runTaskTimerAsynchronously(RPUniverse.getInstance(), 0L, 1L);
    }
}
