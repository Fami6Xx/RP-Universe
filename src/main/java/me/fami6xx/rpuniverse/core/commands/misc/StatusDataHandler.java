package me.fami6xx.rpuniverse.core.commands.misc;

import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;

import java.util.HashMap;
import java.util.UUID;

public class StatusDataHandler {
    private static HashMap<UUID, famiHologram> statusData = new HashMap<>();

    public static void addStatusData(UUID uuid, famiHologram hologram){
        statusData.put(uuid, hologram);
    }

    public static famiHologram getStatusData(UUID uuid){
        return statusData.get(uuid);
    }

    public static void removeStatusData(UUID uuid){
        statusData.remove(uuid);
    }

    public static boolean hasStatusData(UUID uuid){
        return statusData.containsKey(uuid);
    }
}
