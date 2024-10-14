package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;

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
                    return RPUniverse.getInstance().getJobsHandler().getJobs().stream()
                            .map(Job::getName)
                            .toList();
                }

                if (args[0].equalsIgnoreCase("removejob")) {
                    return RPUniverse.getInstance().getJobsHandler().getJobs().stream()
                            .map(Job::getName)
                            .toList();
                }
            }
        }
        return null;
    }
}
