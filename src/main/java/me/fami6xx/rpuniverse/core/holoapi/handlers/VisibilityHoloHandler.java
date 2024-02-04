package me.fami6xx.rpuniverse.core.holoapi.handlers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.raycast.RayCast;
import me.fami6xx.rpuniverse.core.misc.raycast.RayCastResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class VisibilityHoloHandler extends famiHoloHandler {
    public void updateHologramsPlayerMode(Player player, PlayerData data){
        getMap().forEach(((uuid, famiHolograms) -> {
            famiHologram[] arr = famiHolograms.toArray(new famiHologram[0]);

            for(famiHologram holo : arr){
                if(holo.isVisible(player)){
                    holo.updatedPlayerMode(data);
                }
            }
        }));
    }

    @Override
    public BukkitTask startTask() {
        return new BukkitRunnable(){
            private final HashMap<UUID, List<Player>> hashMap = new HashMap<>();
            private List<Player> createList(UUID uuid){
                List<Player> l = new ArrayList<>();
                hashMap.put(uuid, l);
                return l;
            }

            public List<Player> getList(UUID uuid) {
                return hashMap.get(uuid) == null ? createList(uuid) : hashMap.get(uuid);
            }
            public void removeList(UUID uuid){
                hashMap.remove(uuid);
            }
            public void updateList(UUID uuid, List<Player> list){
                hashMap.replace(uuid, list);
            }

            public boolean checkConditions(Player player, famiHologram holo){
                boolean returnValue = true;

                if(isFollowingOwner(player, holo))
                    return true;

                if(!holo.shouldShow(player))
                    return false;

                if(!holo.canSeeThroughBlocks()){
                    Vector playerVector = player.getEyeLocation().toVector();
                    Vector entityVector = holo.getBaseLocation().toVector();
                    Vector vector = playerVector.clone().subtract(entityVector.clone());

                    Location startLoc = holo.getBaseLocation();

                    RayCastResult result = new RayCast(vector, startLoc.getWorld(), startLoc, player.getEyeLocation(), holo.getDistance(), 0.1)
                            .enableIgnoreSeeThroughMaterials()
                            .shoot();

                    if(result.hasHit())
                        returnValue = false;
                }

                return returnValue;
            }

            public boolean isFollowingOwner(Player player, famiHologram holo){
                if(!(holo instanceof FollowingHologram))
                    return false;

                FollowingHologram followingHologram = (FollowingHologram) holo;

                return followingHologram.getFollowing().equals(player);
            }

            @Override
            public void run() {
                // Checking queue and if there is something then executing it safely in this thread
                handleQueue();

                getMap().forEach(((uuid, famiHolograms) -> {
                    famiHologram[] arr = famiHolograms.toArray(new famiHologram[0]);

                    for(famiHologram holo : arr) {
                        if (holo.getHologram().isDisabled()) {
                            // Has to be handled outside for loop otherwise it would throw ConcurrentModificationExc
                            holo.destroy();
                            continue;
                        }
                        if(holo.getHologram().getLocation() == null){
                            holo.destroy();
                            continue;
                        }

                        if (!holo.getHologram().isDefaultVisibleState()) {
                            List<Player> prevVisible = getList(holo.getUUID());
                            Collection<Player> nowVisible = Bukkit.getOnlinePlayers().stream()
                                    .filter(player -> player.getWorld().equals(holo.getHologram().getLocation().getWorld()))
                                    .filter(player -> player.getLocation().distance(holo.getHologram().getLocation()) <= holo.getDistance())
                                    .collect(Collectors.toList());

                            // Hide to everyone who left visible distance
                            prevVisible.forEach(player -> {
                                if (!nowVisible.contains(player)) {
                                    holo.hide(player);
                                }
                            });

                            // Collect everyone who passed conditions
                            List<Player> passedConditions = nowVisible.stream()
                                    .filter(player -> checkConditions(player, holo))
                                    .collect(Collectors.toList());

                            // Hide everyone who didn't pass conditions
                            nowVisible.stream()
                                    .filter(player -> !passedConditions.contains(player))
                                    .filter(holo::isVisible)
                                    .forEach(holo::hide);

                            // Show everyone who passed conditions and didn't see the hologram
                            passedConditions.stream()
                                    .filter(player -> !holo.isVisible(player))
                                    .forEach(holo::show);

                            // Filter everyone who doesn't see the hologram and collect them
                            List<Player> finalList =
                                    nowVisible.stream()
                                            .filter(holo::isVisible)
                                            .collect(Collectors.toList());

                            updateList(holo.getUUID(), finalList);
                        }
                    }
                }));
            }
        }.runTaskTimerAsynchronously(RPUniverse.getInstance(), 0L, 1L);
    }
}
