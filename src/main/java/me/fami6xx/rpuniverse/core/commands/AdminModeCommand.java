package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminModeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            if(args.length == 0){
                return false;
            }

            Player player = Bukkit.getPlayer(args[0]);
            if(player == null){
                return false;
            }

            PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());
            if(data.getPlayerMode() == PlayerMode.ADMIN) {
                data.setPlayerMode(PlayerMode.USER);
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().adminModeDisabledMessage);
                ErrorHandler.info("Admin mode disabled for " + player.getName());
            }

            else if(data.getPlayerMode() == PlayerMode.USER || data.getPlayerMode() == PlayerMode.MODERATOR) {
                data.setPlayerMode(PlayerMode.ADMIN);
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().adminModeEnabledMessage);
                ErrorHandler.info("Admin mode enabled for " + player.getName());
            }

            return true;
        }

        Player player = (Player) sender;
        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

        if(args.length == 0){
            if(data.getPlayerMode() == PlayerMode.ADMIN) {
                data.setPlayerMode(PlayerMode.USER);
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().adminModeDisabledMessage);
                ErrorHandler.info("Admin mode disabled for " + player.getName());
            }

            else if(data.getPlayerMode() == PlayerMode.USER || data.getPlayerMode() == PlayerMode.MODERATOR) {
                if(!player.hasPermission("rpu.adminmode")){
                    ErrorHandler.info("Player " + player.getName() + " tried to enable admin mode without permission");
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage);
                    return true;
                }

                data.setPlayerMode(PlayerMode.ADMIN);
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().adminModeEnabledMessage);
                ErrorHandler.info("Admin mode enabled for " + player.getName());
            }

            return true;
        }

        if(!player.hasPermission("rpu.adminmode")){
            ErrorHandler.info("Player " + player.getName() + " tried to enable admin mode without permission");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().adminModeErrorPlayerNotFoundMessage);
            return true;
        }

        PlayerData targetData = RPUniverse.getPlayerData(target.getUniqueId().toString());

        if(targetData.getPlayerMode() == PlayerMode.ADMIN) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().modesErrorCannotChangeModeMessage);
            ErrorHandler.info("Player " + player.getName() + " tried to change admin mode of " + target.getName());
        }

        else if(targetData.getPlayerMode() == PlayerMode.USER || targetData.getPlayerMode() == PlayerMode.MODERATOR) {
            targetData.setPlayerMode(PlayerMode.ADMIN);
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().adminModeEnabledMessage);
            FamiUtils.sendMessageWithPrefix(target, RPUniverse.getLanguageHandler().adminModeEnabledMessage);
            ErrorHandler.info("Admin mode enabled for " + target.getName());
        }

        return true;
    }
}
