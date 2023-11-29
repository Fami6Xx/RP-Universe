package me.fami6xx.rpuniverse.core.holoapi.handlers;

import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.misc.IExecuteQueue;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class famiHoloHandler {
    // This queue handles modification requests for anything from other threads
    public BlockingQueue<IExecuteQueue> queue = new LinkedBlockingQueue<>();

    public void handleQueue(){
        if(queue.size() > 0){
            try {
                while(queue.size() > 0) {
                    queue.take().execute();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private final HashMap<UUID, ArrayList<famiHologram>> map = new HashMap<>();

    public ArrayList<famiHologram> getList(UUID uuid) {
        return map.get(uuid) == null ? new ArrayList<>() : map.get(uuid);
    }

    public void addToList(UUID uuid, famiHologram holo){
        ArrayList<famiHologram> l = getList(uuid);
        l.add(holo);
        if(map.containsKey(uuid))
            map.replace(uuid, l);
        else
            map.put(uuid, l);
    }

    public void removeFromList(UUID uuid, famiHologram holo){
        ArrayList<famiHologram> l = getList(uuid);
        if(l.contains(holo) || !l.isEmpty())
            l.remove(holo);
        if(!l.isEmpty())
            map.replace(uuid, l);
        else
            map.remove(uuid);
    }

    public void clearList(UUID uuid){
        ArrayList<famiHologram> list = getList(uuid);

        famiHologram[] arr = list.toArray(new famiHologram[0]);

        for(famiHologram famiHolo : arr){
            famiHolo.destroy();
        }
    }

    public HashMap<UUID, ArrayList<famiHologram>> getMap(){
        return map;
    }

    private BukkitTask task;

    public void start(){
        task = startTask();
    }

    public void stop(){
        task.cancel();
        ((HashMap<UUID, ArrayList<famiHologram>>) getMap().clone()).forEach((uuid, holograms) -> holograms.forEach(famiHologram -> famiHologram.getHologram().delete()));
        map.clear();
    }
    
    public abstract BukkitTask startTask();
}
