package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.regions.Region;
import me.fami6xx.rpuniverse.core.regions.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RPUCoreAutoComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("ck", "addjob", "removejob", "region");
        }

        if (args.length >= 2) {
            String mainArg = args[0].toLowerCase();
            switch (mainArg) {
                case "addjob":
                case "removejob":
                    if (args.length == 2) {
                        return Bukkit.getServer().getOnlinePlayers().stream()
                                .map(HumanEntity::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                    if (args.length == 3) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            return new ArrayList<>();
                        }
                        PlayerData data = RPUniverse.getPlayerData(target.getUniqueId().toString());
                        List<Job> jobs = RPUniverse.getInstance().getJobsHandler().getJobs();
                        if (mainArg.equals("addjob")) {
                            return jobs.stream()
                                    .filter(job -> !data.getPlayerJobs().contains(job))
                                    .filter(job -> job.isJobReady().isEmpty())
                                    .map(Job::getName)
                                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                    .collect(Collectors.toList());
                        } else { // removejob
                            return jobs.stream()
                                    .filter(job -> data.getPlayerJobs().contains(job))
                                    .map(Job::getName)
                                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                    .collect(Collectors.toList());
                        }
                    }
                    break;

                case "region":
                    return handleRegionAutoComplete(args);
                default:
                    return null;
            }
        }

        return null;
    }

    private List<String> handleRegionAutoComplete(String[] args) {
        List<String> regionSubCommands = Arrays.asList("pos1", "pos2", "create", "list", "delete", "show", "hide", "tp");
        if (args.length == 2) {
            return filterStartingWith(args[1], regionSubCommands);
        }
        if (args.length == 3) {
            String subCommand = args[1].toLowerCase();
            switch (subCommand) {
                case "create":
                case "tp":
                case "hide":
                case "show":
                case "delete":
                    // Suggest region names based on context
                    if (subCommand.equals("delete") || subCommand.equals("tp") || subCommand.equals("hide") || subCommand.equals("show")) {
                        return RegionManager.getInstance().getAllRegions().stream()
                                .map(Region::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                    // For create, suggest nothing or possibly existing names to prevent duplicates
                    return new ArrayList<>();
                default:
                    return new ArrayList<>();
            }
        }
        return null;
    }

    private List<String> filterStartingWith(String input, List<String> options) {
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }
}
