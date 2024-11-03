package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RPUCoreAutoComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("ck", "addjob", "removejob");
        }
        if (args[0].equalsIgnoreCase("addjob") || args[0].equalsIgnoreCase("removejob")) {
            if (args.length == 2) {
                return Bukkit.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).toList();
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("addjob")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        return new ArrayList<>();
                    }
                    PlayerData data = RPUniverse.getPlayerData(target.getUniqueId().toString());
                    return RPUniverse.getInstance().getJobsHandler().getJobs().stream()
                            .filter(job -> !data.getPlayerJobs().contains(job))
                            .map(Job::getName)
                            .toList();
                }

                if (args[0].equalsIgnoreCase("removejob")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        return new ArrayList<>();
                    }
                    PlayerData data = RPUniverse.getPlayerData(target.getUniqueId().toString());
                    return RPUniverse.getInstance().getJobsHandler().getJobs().stream()
                            .filter(job -> data.getPlayerJobs().contains(job))
                            .map(Job::getName)
                            .toList();
                }
            }
        }
        return null;
    }
}
