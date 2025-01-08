package me.fami6xx.rpuniverse.core.misc.gsonadapters;

import com.google.gson.*;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack itemStack, Type typeOfSrc, JsonSerializationContext context) {
        if (itemStack == null) {
            return JsonNull.INSTANCE;
        }

        try {
            return new JsonPrimitive(itemStackToBase64(itemStack));
        } catch (IOException e) {
            e.printStackTrace();
            return JsonNull.INSTANCE;
        }
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        }

        try {
            return itemStackFromBase64(json.getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert an ItemStack to a Base64 string.
     */
    private static String itemStackToBase64(ItemStack item) throws IOException {
        YamlConfiguration config = new YamlConfiguration();
        config.set("item", item);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(config.saveToString().getBytes());
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * Convert a Base64 string back into an ItemStack.
     */
    private static ItemStack itemStackFromBase64(String data) throws IOException, InvalidConfigurationException {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(new String(inputStream.readAllBytes()));
        return config.getItemStack("item");
    }
}