package me.fami6xx.rpuniverse.core.misc.basichandlers;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActionBarHandler {
    private final HashMap<Player, BlockingQueue<String>> playerMessages = new HashMap<>();

    public ActionBarHandler() {
        cycleMessages();
    }

    /**
     * Add a player to the playerMessages map and start cycling messages for that player.
     *
     * @param player   The player to add.
     * @param messages The list of messages for the player.
     */
    public void addPlayer(Player player, List<String> messages) {
        BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>(messages);
        playerMessages.put(player, messageQueue);
    }

    /**
     * Remove a player from the playerMessages map.
     *
     * @param player The player to be removed.
     */
    public void removePlayer(Player player) {
        playerMessages.remove(player);
    }

    /**
     * Add a message to the player's action bar.
     * This method will add the given message to the action bar of the specified player.
     * If the player is not currently displaying any messages, the action bar will start cycling through the messages periodically.
     *
     * @param player The player to add the message for.
     * @param message The message to add.
     * @param force Whether to immediately display the message on the action bar.
     */
    public void addMessage(Player player, String message, boolean force) {
        BlockingQueue<String> messages = playerMessages.get(player);
        if (messages != null) {
            if (force) {
                player.sendActionBar(FamiUtils.format(message));
            } else {
                messages.add(message);
            }
        }else{
            List<String> messagesList = new ArrayList<>();
            messagesList.add(message);
            this.addPlayer(player, messagesList);
        }
    }

    /**
     * Add a message to the player's action bar.
     * This method will add the given message to the action bar of the specified player.
     * If the player is not currently displaying any messages, the action bar will start cycling through the messages periodically.
     *
     * @param player The player to add the message for.
     * @param message The message to add.
     */
    public void addMessage(Player player, String message) {
        this.addMessage(player, message, false);
    }

    /**
     * Get queued messages for the player.
     *
     * @param player The player to get the messages for.
     * @return The messages for the player.
     */
    public Queue<String> getMessages(Player player){
        return playerMessages.get(player);
    }

    /**
     * Remove a message from the player's action bar.
     *
     * @param player The player to remove the message from.
     * @param message The message to remove.
     */
    public void removeMessage(Player player, String message){
        BlockingQueue<String> messages = playerMessages.get(player);
        if (messages != null) {
            messages.remove(message);
        }
    }

    /**
     * Cycle through the messages for online players and display them on the action bar periodically.
     */
    private void cycleMessages() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                    BlockingQueue<String> messages = playerMessages.get(player);
                    if (messages != null && !messages.isEmpty()) {
                        String message = messages.poll();
                        player.sendActionBar(FamiUtils.format(message));
                    }else{
                        playerMessages.remove(player);
                    }
                });
            }
        }.runTaskTimer(RPUniverse.getInstance(), 0L, 20L);
    }
}
