package me.fami6xx.rpuniverse.core.properties.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PropertiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

        if(data.getPlayerMode() != PlayerMode.ADMIN) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouAreNotInCorrectModeToUseThisCommandMessage));
            return true;
        }


        return true;
    }
}
