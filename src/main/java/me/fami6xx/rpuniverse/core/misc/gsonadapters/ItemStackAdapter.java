package me.fami6xx.rpuniverse.core.misc.gsonadapters;

import com.google.gson.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", src);
        String yaml = config.saveToString();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("item", yaml);

        return jsonObject;
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String yaml = json.getAsJsonObject().get("item").getAsString();

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(yaml);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }

        return config.getItemStack("item");
    }
}