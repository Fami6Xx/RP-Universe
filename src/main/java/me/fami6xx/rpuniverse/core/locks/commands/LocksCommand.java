package me.fami6xx.rpuniverse.core.locks.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.menus.AllLocksMenu;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

public class LocksCommand implements CommandExecutor {
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

        new AllLocksMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player)).open();
        return true;
    }
}
