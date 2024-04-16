package me.fami6xx.rpuniverse.core.misc.utils;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FamiUtils {
    public static String format(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String formatWithPrefix(String message){
        return format(RPUniverse.getPrefix() + " " + message);
    }

    public static String replace(String message, HashMap<String, String> replace){
        for(String key : replace.keySet()){
            message = message.replace(key, replace.get(key));
        }
        return message;
    }

    public static String replaceAndFormat(String message, HashMap<String, String> replace){
        return format(replace(message, replace));
    }

    public static void sendMessageWithPrefix(Player player, String message){
        player.sendMessage(formatWithPrefix(message));
    }

    public static void sendMessageWithPrefix(Player player, String message, HashMap<String, String> replace){
        player.sendMessage(formatWithPrefix(replace(message, replace)));
    }

    public static void sendMessageInRange(Player player, String message, int range){
        String finalMessage = format(message);
        sendMessageInRangeSynchronized(player, range, finalMessage);
    }

    public static void sendMessageInRange(Player player, String message, int range, HashMap<String, String> replace){
        String finalMessage = format(replace(message, replace));

        sendMessageInRangeSynchronized(player, range, finalMessage);
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    private static void sendMessageInRangeSynchronized(Player player, int range, String finalMessage) {
        player.sendMessage(finalMessage);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.getNearbyEntities(range, range, range).stream()
                        .filter(entity -> entity instanceof Player)
                        .map(entity -> (Player) entity)
                        .forEach(player1 -> player1.sendMessage(finalMessage));
            }
        }.runTask(RPUniverse.getInstance());
    }

    public static void sendMessage(Player player, String message){
        player.sendMessage(format(message));
    }

    public static ItemStack makeItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(FamiUtils.format(displayName));
        List<String> loreList = Arrays.asList(lore);
        if(lore.length == 1){
            if(lore[0] != null || !lore[0].isEmpty())
                loreList = Arrays.asList(lore[0].split("~"));
        }
        loreList = loreList.stream().map(FamiUtils::format).collect(Collectors.toList());
        itemMeta.setLore(loreList);
        item.setItemMeta(itemMeta);

        return item;
    }
}
