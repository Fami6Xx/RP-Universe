package me.fami6xx.rpuniverse.core.jobs.commands.createJob;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class CreateJobCommand implements CommandExecutor, Listener {
    List<Player> currentlyCreating = new ArrayList<>();
    List<Player> showTitle = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;

        if(currentlyCreating.contains(player)){
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouAreAlreadyCreatingAJobMessage);
            return true;
        }

        currentlyCreating.add(player);
        showTitle.add(player);
        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createJobCommandTypeNameMessage);

        return true;
    }

    public BukkitTask showTitleRunnable(){
        return (new BukkitRunnable() {
            @Override
            public void run() {

            }
        }).runTaskTimerAsynchronously(RPUniverse.getInstance(), 0, 20);
    }
}
