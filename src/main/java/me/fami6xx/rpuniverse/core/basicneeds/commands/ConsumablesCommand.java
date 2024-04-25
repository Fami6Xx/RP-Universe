package me.fami6xx.rpuniverse.core.basicneeds.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsConfig;
import me.fami6xx.rpuniverse.core.basicneeds.menus.AllConsumablesMenu;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConsumablesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BasicNeedsConfig config = RPUniverse.getInstance().getBasicNeedsHandler().getConfig();
        if(!config.isEnabled()){
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorBasicNeedsDisabledMessage));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;
        if (config.isPreferPermissionsOverModeForEdit()) {
            if (!player.hasPermission("rpu.basicneeds.edit")){
                player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage));
                return true;
            }
        }else{
            if(config.getNeededModeToEdit() == PlayerMode.ADMIN) {
                if (RPUniverse.getPlayerData(((Player) sender).getUniqueId().toString()).getPlayerMode() == config.getNeededModeToEdit()) {
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouAreNotInCorrectModeToUseThisCommandMessage));
                    return true;
                }
            }else if (config.getNeededModeToEdit() == PlayerMode.MODERATOR) {
                if (RPUniverse.getPlayerData(((Player) sender).getUniqueId().toString()).getPlayerMode() == config.getNeededModeToEdit() ||
                        RPUniverse.getPlayerData(((Player) sender).getUniqueId().toString()).getPlayerMode() == PlayerMode.ADMIN) {
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouAreNotInCorrectModeToUseThisCommandMessage));
                    return true;
                }
            }else{
                player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage));
                return true;
            }
        }

        new AllConsumablesMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), RPUniverse.getInstance().getBasicNeedsHandler());
        return true;
    }
}
