package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SwitchJobCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;
        PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());

        if (playerData.getPlayerMode() != PlayerMode.USER) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouAreNotInUserMode);
            return true;
        }

        if (playerData.getPlayerJobs().isEmpty() && playerData.getSelectedPlayerJob() == null) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().errorYouDontHaveAnyJob);
            return true;
        }

        if (args.length == 0) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandInfo);
            int id = 1;

            // List the "No Job" option
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{jobName}", RPUniverse.getLanguageHandler().bossBarPlayerNoJob);
            placeholders.put("{jobId}", String.valueOf(id));
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandJobList, placeholders);
            id++;

            // List available jobs
            for (Job job : playerData.getPlayerJobs()) {
                placeholders = new HashMap<>();
                placeholders.put("{jobName}", job.getName());
                placeholders.put("{jobId}", String.valueOf(id));
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandJobList, placeholders);
                id++;
            }
            return true;
        }

        if (!FamiUtils.isInteger(args[0])) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandError);
            return true;
        }

        int jobId = Integer.parseInt(args[0]);

        if (jobId < 1 || jobId > playerData.getPlayerJobs().size() + 1) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandError);
            return true;
        }

        if (jobId == 1) {
            if (playerData.getSelectedPlayerJob() == null) {
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandErrorAlreadyInJob);
                return true;
            }

            playerData.setSelectedPlayerJob(null);
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("{jobName}", RPUniverse.getLanguageHandler().bossBarPlayerNoJob);
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandSuccess, placeholders);
            return true;
        }

        Job job = playerData.getPlayerJobs().get(jobId - 2);

        if (job.equals(playerData.getSelectedPlayerJob())) {
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandErrorAlreadyInJob);
            return true;
        }

        playerData.setSelectedPlayerJob(job);
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", job.getName());
        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().switchJobCommandSuccess, placeholders);

        return true;
    }
}
