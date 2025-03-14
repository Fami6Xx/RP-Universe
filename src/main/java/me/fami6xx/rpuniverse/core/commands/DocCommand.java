package me.fami6xx.rpuniverse.core.commands;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import me.fami6xx.rpuniverse.core.holoapi.types.lines.UpdatingHologramLine;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DocCommand implements CommandExecutor, Listener {
    private static HashMap<Player, Boolean> blockedMovementPlayers = new HashMap<>();
    private static ConcurrentHashMap<Player, Long> activeDocCommands = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorDocCommandUsage);
            return true;
        }

        // Check if the player has an active /doc command
        if (activeDocCommands.containsKey(player)) {
            long expireTime = activeDocCommands.get(player);
            if (System.currentTimeMillis() < expireTime) {
                // Command is still active
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorDocCommandAlreadyActive);
                return true;
            } else {
                // Command has expired, remove from map
                activeDocCommands.remove(player);
            }
        }

        if(!FamiUtils.isInteger(args[0])){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorDocCommandUsage);
            return true;
        }

        int docSeconds = Integer.parseInt(args[0]);
        if(docSeconds < 1){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorDocCommandUsage);
            return true;
        }

        HashMap<String, String> replace = new HashMap<>();
        replace.put("{player}", player.getName());
        replace.put("{seconds}", String.valueOf(docSeconds));

        if(args.length > 1) {
            String[] args2 = new String[args.length - 1];
            System.arraycopy(args, 1, args2, 0, args.length - 1);
            StringBuilder builder = new StringBuilder();
            for (String arg : args2) {
                builder.append(arg).append(" ");
            }
            String message = builder.toString();
            message = message.substring(0, message.length() - 1);
            replace.put("{message}", message);
        }else{
            replace.put("{message}", "");
        }

        addDoc(player, docSeconds, replace.get("{message}"), false);
        return true;
    }

    public static void addDoc(Player player, int docSeconds, String message, boolean blockPlayerMovement){
        long expireTime = System.currentTimeMillis() + docSeconds * 1000L;
        activeDocCommands.put(player, expireTime);

        final int[] range = {0};
        try {
            range[0] = RPUniverse.getInstance().getConfiguration().getInt("holograms.range");
        }catch (Exception exc){
            HashMap<String, String> replace = new HashMap<>();
            replace.put("{value}", "holograms.range");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
            return;
        }

        HashMap<String, String> replace = new HashMap<>();
        replace.put("{player}", player.getName());
        replace.put("{seconds}", 1 + "/" + docSeconds);
        replace.put("{message}", message);

        FollowingHologram holo = new FollowingHologram(player, range[0], false, false);
        HologramLine line = holo.addLine(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().docCommandHologram, replace));
        if(blockPlayerMovement){
            blockedMovementPlayers.put(player, true);
        }
        new UpdatingHologramLine(line) {
            int seconds = 1;
            final int maxSeconds = docSeconds;
            final Player docPlayer = player;
            final String docMessage = message;
            final int docRange = range[0];
            @Override
            public String update() {
                HashMap<String, String> replaceHashMap = new HashMap<>();
                replaceHashMap.put("{player}", docPlayer.getName());
                replaceHashMap.put("{seconds}", seconds + "/" + maxSeconds);
                replaceHashMap.put("{message}", docMessage);
                String newLine = FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().docCommandHologram, replaceHashMap);
                if(seconds <= maxSeconds){
                    seconds++;
                    FamiUtils.sendMessageInRange(docPlayer, RPUniverse.getLanguageHandler().docCommandMessage, docRange, replaceHashMap);
                    return newLine;
                }
                holo.destroy();
                blockedMovementPlayers.remove(docPlayer);
                activeDocCommands.remove(docPlayer);
                return newLine;
            }

            @Override
            public void onDisable() {
                holo.destroy();
                blockedMovementPlayers.remove(docPlayer);
                activeDocCommands.remove(docPlayer);
            }
        };
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        if(blockedMovementPlayers.containsKey(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}
