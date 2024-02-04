package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SetTagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            if(args.length < 2){
                return false;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if(target == null){
                return false;
            }

            StringBuilder builder = new StringBuilder();
            for(String arg : args){
                if(arg.equals(args[0]))
                    continue;
                builder.append(arg).append(" ");
            }
            String tag = builder.toString().trim();

            PlayerData data = RPUniverse.getPlayerData(target.getUniqueId().toString());
            data.setTag(tag);
            RPUniverse.getInstance().getLogger().info("Tag set for " + target.getName() + " to " + tag);

            return true;
        }

        Player player = (Player) sender;
        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

        if(!player.hasPermission("rpu.settag") && data.getPlayerMode() != PlayerMode.ADMIN){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage);
            return true;
        }

        if(args.length < 2){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorSetTagCommandUsage);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorSetTagCommandPlayerNotFound);
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for(String arg : args){
            if(arg.equals(args[0]))
                continue;
            builder.append(arg).append(" ");
        }
        String tag = builder.toString().trim();

        if(tag.length() > 16){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorSetTagCommandTagTooLong);
            return true;
        }

        data = RPUniverse.getPlayerData(target.getUniqueId().toString());
        data.setTag(tag);

        HashMap<String, String> replace = new HashMap<>();
        replace.put("{player}", target.getName());
        replace.put("{tag}", tag);

        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().successTagSetMessage, replace);
        return true;
    }
}
