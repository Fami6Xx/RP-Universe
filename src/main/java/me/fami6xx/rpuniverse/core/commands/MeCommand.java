package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorMeCommandUsage);
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for(String arg : args){
            builder.append(arg).append(" ");
        }
        String message = builder.toString();
        message = message.substring(0, message.length() - 1);

        HashMap<String, String> replace = new HashMap<>();
        replace.put("{player}", player.getName());
        replace.put("{message}", message);

        int range = 0;
        int timeAlive = 0;
        try {
            range = RPUniverse.getInstance().getConfiguration().getInt("holograms.range");
        }catch (Exception exc){
            replace.put("{value}", "holograms.range");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
            return true;
        }

        try {
            timeAlive = RPUniverse.getInstance().getConfiguration().getInt("holograms.timeAlive");
        }catch (Exception exc){
            replace.put("{value}", "holograms.timeAlive");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
            return true;
        }

        FamiUtils.sendMessageInRange(player, RPUniverse.getLanguageHandler().meCommandMessage, range, replace);
        new FollowingHologram(player, range, false, true, timeAlive * 20)
                .addLine(FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().meCommandHologram, replace));
        return true;
    }
}
