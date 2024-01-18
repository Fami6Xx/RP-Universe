package me.fami6xx.rpuniverse.core.misc.chatapi;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class UniversalChatHandler implements Listener {
    private HashMap<Player, IChatExecuteQueue> chatExecuteQueueHashMap = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(chatExecuteQueueHashMap.containsKey(e.getPlayer())){
            e.setCancelled(true);
            IChatExecuteQueue queue = chatExecuteQueueHashMap.get(e.getPlayer());
            if(queue.execute(e.getPlayer(), e.getMessage())){
                chatExecuteQueueHashMap.remove(e.getPlayer());
            }
        }
    }

    public void addToQueue(Player player, IChatExecuteQueue queue){
        chatExecuteQueueHashMap.put(player, queue);
    }

    public boolean canAddToQueue(Player player){
        return !chatExecuteQueueHashMap.containsKey(player);
    }
}
