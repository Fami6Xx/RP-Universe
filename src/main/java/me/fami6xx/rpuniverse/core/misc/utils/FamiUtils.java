package me.fami6xx.rpuniverse.core.misc.utils;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FamiUtils {
    /**
     * Format the message
     * @param message The message to format
     * @return The formatted message
     */
    public static String format(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Format the message with the prefix
     * @param message The message to format
     * @return The formatted message
     */
    public static String formatWithPrefix(String message){
        return format(RPUniverse.getPrefix() + " " + message);
    }

    /**
     * Replace the message
     * @param message The message to replace
     * @param replace The replace map
     * @return The replaced message
     */
    public static String replace(String message, HashMap<String, String> replace){
        for(String key : replace.keySet()){
            message = message.replace(key, replace.get(key));
        }
        return message;
    }

    /**
     * Replace the message and format it
     * @param message The message to replace
     * @param replace The replace map
     * @return The formatted message
     */
    public static String replaceAndFormat(String message, HashMap<String, String> replace){
        return format(replace(message, replace));
    }

    /**
     * Send a message to the player with the prefix
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendMessageWithPrefix(Player player, String message){
        player.sendMessage(formatWithPrefix(message));
    }

    /**
     * Send a message to the player with the prefix
     * @param player The player to send the message to
     * @param message The message to send
     * @param replace The replace map
     */
    public static void sendMessageWithPrefix(Player player, String message, HashMap<String, String> replace){
        player.sendMessage(formatWithPrefix(replace(message, replace)));
    }

    /**
     * Send a message to the player in a range
     * @param player The player to send the message to
     * @param message The message to send
     * @param range The range to send the message to
     */
    public static void sendMessageInRange(Player player, String message, int range){
        String finalMessage = format(message);
        sendMessageInRangeSynchronized(player, range, finalMessage);
    }

    /**
     * Send a message to the player in a range
     * @param player The player to send the message to
     * @param message The message to send
     * @param range The range to send the message to
     * @param replace The replace map
     */
    public static void sendMessageInRange(Player player, String message, int range, HashMap<String, String> replace){
        String finalMessage = format(replace(message, replace));

        sendMessageInRangeSynchronized(player, range, finalMessage);
    }

    /**
     * Check if the string is an integer
     * @param s The string to check
     * @return True if the string is an integer, false otherwise
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Send a message to the player in a range
     * @param player The player to send the message to
     * @param range The range to send the message to
     * @param finalMessage The message to send
     */
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

    /**
     * Send a message to the player
     * @param player The player to send the message to
     * @param message The message to send
     */
    public static void sendMessage(Player player, String message){
        player.sendMessage(format(message));
    }

    /**
     * Create an item with the given material, display name and lore
     * @param material The material of the item
     * @param displayName The display name of the item
     * @param lore The lore of the item
     * @return The created item
     */
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

    /**
     * Check if the block is a double chest
     * @param block The block to check
     * @return True if the block is a double chest, false otherwise
     */
    public static boolean isDoubleChest(Block block) {
        if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();
            InventoryHolder holder = chest.getInventory().getHolder();
            return holder instanceof DoubleChestInventory;
        }
        return false;
    }

    /**
     * Check if the block is a double door
     * @param block The block to check
     * @return True if the block is a double door, false otherwise
     */
    public static boolean isDoubleDoor(Block block) {
        if (!(block.getBlockData() instanceof Door)) {
            return false;
        }
        
        Door door = (Door) block.getBlockData();
        BlockFace facing = door.getFacing();
        BlockFace hinge = door.getHinge() == Door.Hinge.RIGHT ? facing.getOppositeFace() : facing;

        Block adjacentBlock = block.getRelative(hinge);
        if (adjacentBlock.getType() == block.getType()) {
            Door adjacentDoor = (Door) adjacentBlock.getBlockData();
            return adjacentDoor.getFacing() == facing && adjacentDoor.getHinge() != door.getHinge();
        }
    
        return false;
    }
}
