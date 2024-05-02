package me.fami6xx.rpuniverse.core.basicneeds.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsConfig;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static me.fami6xx.rpuniverse.core.commands.DocCommand.addDoc;

public class PeeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BasicNeedsConfig config = RPUniverse.getInstance().getBasicNeedsHandler().getConfig();
        if (!config.isEnabled()) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorBasicNeedsDisabledMessage));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;
        PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
        if (playerData.getPeeLevel() <= 25) {
            player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouDontNeedToPeeMessage));
            return true;
        }

        playerData.setPeeLevel(0);
        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().successPeeMessage));
        HashMap<String, String> replace = new HashMap<>();
        replace.put("{message}", RPUniverse.getLanguageHandler().peeDocHologramMessage);
        addDoc(player, 5, FamiUtils.replace(RPUniverse.getLanguageHandler().docCommandHologram, replace), true);
        return true;
    }
}
