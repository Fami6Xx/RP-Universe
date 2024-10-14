package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RPUCoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                showHelp(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("ck")) {
                if (args.length < 2) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /rpu ck <Player>"));
                    return true;
                }
                Player target = sender.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer not found!"));
                    return true;
                }
                target.setHealth(0);
                sender.sendMessage(FamiUtils.formatWithPrefix("&aCharacter killed!"));
                return true;
            }
            if (args[0].equalsIgnoreCase("addjob")) {
                if (args.length < 3) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /rpu addjob <Player> <Job name>"));
                    return true;
                }
                Player target = sender.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer not found!"));
                    return true;
                }
                Job job = RPUniverse.getInstance().getJobsHandler().getJobByName(args[2]);
                if (job == null) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cJob not found!"));
                    return true;
                }
                if (job.isPlayerInJob(target.getUniqueId())) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer is already in this job!"));
                    return true;
                }
                job.addPlayerToJob(target.getUniqueId());
                sender.sendMessage(FamiUtils.formatWithPrefix("&aJob added!"));
                return true;
            }
            if (args[0].equalsIgnoreCase("removejob")) {
                if (args.length < 3) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /rpu removejob <Player> <Job name>"));
                    return true;
                }
                Player target = sender.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer not found!"));
                    return true;
                }
                Job job = RPUniverse.getInstance().getJobsHandler().getJobByName(args[2]);
                if (job == null) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cJob not found!"));
                    return true;
                }
                if (!job.isPlayerInJob(target.getUniqueId())) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cPlayer is not in this job!"));
                    return true;
                }
                job.removePlayerFromJob(target.getUniqueId());
                sender.sendMessage(FamiUtils.formatWithPrefix("&aJob removed!"));
                return true;
            }
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("rpu.core.command")) {
            FamiUtils.sendMessageWithPrefix(player, "&cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("ck")) {
            if (args.length < 2) {
                FamiUtils.sendMessageWithPrefix(player, "&cUsage: /rpu ck <Player>");
                return true;
            }
            Player target = player.getServer().getPlayer(args[1]);
            if (target == null) {
                FamiUtils.sendMessageWithPrefix(player, "&cPlayer not found!");
                return true;
            }
            target.setHealth(0);
            FamiUtils.sendMessageWithPrefix(player, "&aCharacter killed!");
            return true;
        }
        if (args[0].equalsIgnoreCase("addjob")) {
            if (args.length < 3) {
                FamiUtils.sendMessageWithPrefix(player, "&cUsage: /rpu addjob <Player> <Job name>");
                return true;
            }
            Player target = player.getServer().getPlayer(args[1]);
            if (target == null) {
                FamiUtils.sendMessageWithPrefix(player, "&cPlayer not found!");
                return true;
            }
            Job job = RPUniverse.getInstance().getJobsHandler().getJobByName(args[2]);
            if (job == null) {
                FamiUtils.sendMessageWithPrefix(player, "&cJob not found!");
                return true;
            }
            if (job.isPlayerInJob(target.getUniqueId())) {
                FamiUtils.sendMessageWithPrefix(player, "&cPlayer is already in this job!");
                return true;
            }
            job.addPlayerToJob(target.getUniqueId());
            FamiUtils.sendMessageWithPrefix(player, "&aJob added!");
            return true;
        }
        if (args[0].equalsIgnoreCase("removejob")) {
            if (args.length < 3) {
                FamiUtils.sendMessageWithPrefix(player, "&cUsage: /rpu removejob <Player> <Job name>");
                return true;
            }
            Player target = player.getServer().getPlayer(args[1]);
            if (target == null) {
                FamiUtils.sendMessageWithPrefix(player, "&cPlayer not found!");
                return true;
            }
            Job job = RPUniverse.getInstance().getJobsHandler().getJobByName(args[2]);
            if (job == null) {
                FamiUtils.sendMessageWithPrefix(player, "&cJob not found!");
                return true;
            }
            if (!job.isPlayerInJob(target.getUniqueId())) {
                FamiUtils.sendMessageWithPrefix(player, "&cPlayer is not in this job!");
                return true;
            }
            job.removePlayerFromJob(target.getUniqueId());
            FamiUtils.sendMessageWithPrefix(player, "&aJob removed!");
            return true;
        }
        return true;
    }

    public void showHelp(CommandSender sender) {
        sender.sendMessage(FamiUtils.formatWithPrefix("&7&m--------------------------------"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6&lRPUniverse &7- &fHelp"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&7&m--------------------------------"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu &7- &fShow this help"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu ck <Player> &7- &f Character kill a player"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu addjob <Player> <Job name> &7- &fAdd a job to a player"));
        sender.sendMessage(FamiUtils.formatWithPrefix("&6/rpu removejob <Player> <Job name> &7- &fRemove a job from a player"));
    }
}
