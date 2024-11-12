package me.fami6xx.rpuniverse.core.misc.gsonadapters;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", src.getWorld().getName());
        jsonObject.addProperty("x", src.getX());
        jsonObject.addProperty("y", src.getY());
        jsonObject.addProperty("z", src.getZ());
        jsonObject.addProperty("yaw", src.getYaw());
        jsonObject.addProperty("pitch", src.getPitch());
        return jsonObject;
    }

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String world = jsonObject.get("world").getAsString();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.has("yaw") ? jsonObject.get("yaw").getAsFloat() : 0;
        float pitch = jsonObject.has("pitch") ? jsonObject.get("pitch").getAsFloat() : 0;
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
