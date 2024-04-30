package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class StatusCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("status")){
            if(args.length == 0){
                FamiUtils.sendMessageWithPrefix((Player) sender, RPUniverse.getLanguageHandler().errorStatusCommandUsage);
                return true;
            }

            if(StatusDataHandler.hasStatusData(player.getUniqueId())){
                FamiUtils.sendMessageWithPrefix((Player) sender, RPUniverse.getLanguageHandler().errorStatusAlreadySet);
                return true;
            }

            StringBuilder builder = new StringBuilder();
            for(String arg : args){
                builder.append(arg).append(" ");
            }
            String message = builder.toString();
            message = message.substring(0, message.length() - 1);

            if(message.length() >= 32){
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorStatusCommandUsage);
                return true;
            }

            HashMap<String, String> replace = new HashMap<>();
            replace.put("{player}", player.getName());
            replace.put("{message}", message);

            int range = 0;
            try {
                range = RPUniverse.getInstance().getConfiguration().getInt("holograms.range");
            }catch (Exception exc){
                replace.put("{value}", "holograms.range");
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
                return true;
            }

            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().statusCommandMessage, replace);
            FollowingHologram holo = new FollowingHologram(player, range, false, false);
            holo.addLine(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().statusCommandHologram, replace));
            StatusDataHandler.addStatusData(player.getUniqueId(), holo);

            return true;
        }

        if(command.getName().equalsIgnoreCase("stopstatus")){
            if(!StatusDataHandler.hasStatusData(player.getUniqueId())){
                FamiUtils.sendMessageWithPrefix((Player) sender, RPUniverse.getLanguageHandler().errorNoStatusSet);
                return true;
            }

            FollowingHologram holo = (FollowingHologram) StatusDataHandler.getStatusData(player.getUniqueId());
            StatusDataHandler.removeStatusData(player.getUniqueId());
            holo.destroy();
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().stopStatusCommandMessage);

            return true;
        }

        System.out.println("Unknown command: " + command.getName());
        return true;
    }
}
