package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

        if(!player.hasPermission("rpu.tag") && data.getPlayerMode() != PlayerMode.ADMIN && data.getPlayerMode() != PlayerMode.MODERATOR){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouDontHavePermissionToUseThisCommandMessage);
            return true;
        }

        if(data.isTagVisible()){
            data.getCurrentTagHologram().destroy();
            data.setCurrentTagHologram(null);
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().tagDisabledMessage);
            return true;
        }

        HashMap<String, String> replace = new HashMap<>();
        int range = 0;
        try {
            range = RPUniverse.getInstance().getConfiguration().getInt("holograms.range");
        }catch (Exception exc){
            replace.put("{value}", "holograms.range");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
            return true;
        }

        String tag = "";
        if(data.getTag() == null || data.getTag().trim().isEmpty()) {
            if(data.getPlayerMode() == PlayerMode.MODERATOR){
                tag = RPUniverse.getLanguageHandler().moderatorTag;
            }

            else if(data.getPlayerMode() == PlayerMode.ADMIN){
                tag = RPUniverse.getLanguageHandler().adminTag;
            }

            else {
                if(player.hasPermission("rpu.adminmode")){
                    tag = RPUniverse.getLanguageHandler().adminTag;
                }

                else {
                    tag = RPUniverse.getLanguageHandler().moderatorTag;
                }
            }
        }else tag = data.getTag();

        FollowingHologram hologram = new FollowingHologram(player, range, false, false);
        hologram.addLine(FamiUtils.format(tag));
        data.setCurrentTagHologram(hologram);
        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().tagEnabledMessage);

        return true;
    }
}
