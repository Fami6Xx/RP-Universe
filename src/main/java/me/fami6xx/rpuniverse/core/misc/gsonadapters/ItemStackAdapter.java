package me.fami6xx.rpuniverse.core.misc.gsonadapters;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getType().name());
        jsonObject.addProperty("amount", src.getAmount());
        jsonObject.addProperty("durability", src.getDurability());


        if (src.hasItemMeta()) {
            ItemMeta meta = src.getItemMeta();
            // Possible solution for before 1.13 is to look into the meta class and search for the method that returns the custom model data
            // and then use reflection to get the value of that method
            if (meta.hasCustomModelData()) {
                jsonObject.addProperty("customModelData", meta.getCustomModelData());
            }
            if (meta.hasDisplayName()) {
                jsonObject.addProperty("displayName", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                JsonArray lore = new JsonArray();
                meta.getLore().forEach(lore::add);
                jsonObject.add("lore", lore);
            }
            if (meta.hasEnchants()) {
                JsonObject enchants = new JsonObject();
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchants.addProperty(entry.getKey().getName(), entry.getValue());
                }
                jsonObject.add("enchants", enchants);
            }
            if(meta.isUnbreakable()){
                jsonObject.addProperty("unbreakable", true);
            }
        }

        return jsonObject;
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Material type = Material.valueOf(jsonObject.get("type").getAsString());
        int amount = jsonObject.get("amount").getAsInt();
        short durability = jsonObject.get("durability").getAsShort();

        ItemStack itemStack = new ItemStack(type, amount, durability);
        ItemMeta meta = itemStack.getItemMeta();

        if (jsonObject.has("displayName")) {
            meta.setDisplayName(jsonObject.get("displayName").getAsString());
        }
        if (jsonObject.has("lore")) {
            JsonArray lore = jsonObject.getAsJsonArray("lore");
            meta.setLore(new ArrayList<>());
            lore.forEach(element -> meta.getLore().add(element.getAsString()));
        }
        if (jsonObject.has("enchants")) {
            JsonObject enchants = jsonObject.getAsJsonObject("enchants");
            for (Map.Entry<String, JsonElement> entry : enchants.entrySet()) {
                meta.addEnchant(Enchantment.getByName(entry.getKey()), entry.getValue().getAsInt(), true);
            }
        }
        if(jsonObject.has("unbreakable")){
            meta.setUnbreakable(true);
        }
        if(jsonObject.has("customModelData")){
            meta.setCustomModelData(jsonObject.get("customModelData").getAsInt());
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
