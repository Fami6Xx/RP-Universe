package me.fami6xx.rpuniverse.core.misc.utils;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

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
}
