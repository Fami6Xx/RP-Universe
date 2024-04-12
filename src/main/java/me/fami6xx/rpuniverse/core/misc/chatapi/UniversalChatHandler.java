package me.fami6xx.rpuniverse.core.misc.chatapi;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class UniversalChatHandler implements Listener, CommandExecutor {
    private HashMap<Player, IChatExecuteQueue> chatExecuteQueueHashMap = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        if(chatExecuteQueueHashMap.containsKey(e.getPlayer())){
            e.setCancelled(true);
            IChatExecuteQueue queue = chatExecuteQueueHashMap.get(e.getPlayer());
            if(queue.execute(e.getPlayer(), e.getMessage())){
                chatExecuteQueueHashMap.remove(e.getPlayer());
            }
        }else{
            if (e.getMessage().startsWith("/")) return;
            if (!RPUniverse.getInstance().getConfig().getBoolean("general.localOOC")) return;
            e.setCancelled(true);

            HashMap<String, String> replace = new HashMap<>();
            replace.put("{player}", e.getPlayer().getName());
            replace.put("{message}", e.getMessage());

            String localOOCMessage = "";
            int range = 0;
            try {
                localOOCMessage = RPUniverse.getInstance().getConfiguration().getString("general.localOOCFormat");
            }catch (Exception exc){
                replace.put("{value}", "general.localOOCFormat");
                FamiUtils.sendMessageWithPrefix(e.getPlayer(), RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
                return;
            }
            try{
                range = RPUniverse.getInstance().getConfiguration().getInt("general.localOOCRange");
            }catch (Exception exc){
                replace.put("{value}", "general.localOOCRange");
                FamiUtils.sendMessageWithPrefix(e.getPlayer(), RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
                return;
            }

            String formattedMessage = FamiUtils.replaceAndFormat(localOOCMessage, replace);

            FamiUtils.sendMessageInRange(e.getPlayer(), formattedMessage, RPUniverse.getInstance().getConfiguration().getInt("general.localOOCRange"));
        }
    }

    public void addToQueue(Player player, IChatExecuteQueue queue){
        chatExecuteQueueHashMap.put(player, queue);
    }

    public boolean canAddToQueue(Player player){
        return !chatExecuteQueueHashMap.containsKey(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        if(args.length == 0){
            FamiUtils.sendMessageWithPrefix((Player) sender, RPUniverse.getLanguageHandler().errorGlobalOOCUsage);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("rpu.globalooc")){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage);
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for(String arg : args){
            builder.append(arg).append(" ");
        }
        String message = builder.toString();

        HashMap<String, String> replace = new HashMap<>();
        replace.put("{player}", player.getName());
        replace.put("{message}", message);

        String globalOOCMessage = "";
        try {
            globalOOCMessage = RPUniverse.getInstance().getConfiguration().getString("general.globalOOCFormat");
        }catch (Exception exc){
            replace.put("{value}", "general.globalOOCFormat");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
            return true;
        }

        String formattedMessage = FamiUtils.replaceAndFormat(globalOOCMessage, replace);

        for (Player sendPlayer : Bukkit.getServer().getOnlinePlayers()) {
            sendPlayer.sendMessage(formattedMessage);
        }
        return true;
    }
}
