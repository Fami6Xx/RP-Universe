package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.BeforeCharacterKilledEvent;
import me.fami6xx.rpuniverse.core.api.CharacterKilledEvent;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import net.milkbowl.vault.economy.Economy;
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
                BeforeCharacterKilledEvent event = new BeforeCharacterKilledEvent(target, null, RPUniverse.getInstance().getDataSystem().getPlayerData(target.getUniqueId()));
                RPUniverse.getInstance().getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    sender.sendMessage(FamiUtils.formatWithPrefix("&cCharacter kill cancelled!"));
                    return true;
                }

                // Resetting player data
                resetData(target);

                CharacterKilledEvent killedEvent = new CharacterKilledEvent(target, null, RPUniverse.getInstance().getDataSystem().getPlayerData(target.getUniqueId()));
                RPUniverse.getInstance().getServer().getPluginManager().callEvent(killedEvent);

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

            BeforeCharacterKilledEvent event = new BeforeCharacterKilledEvent(target, player, RPUniverse.getInstance().getDataSystem().getPlayerData(target.getUniqueId()));
            RPUniverse.getInstance().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                FamiUtils.sendMessageWithPrefix(player, "&cCharacter kill cancelled!");
                return true;
            }

            // Resetting player data
            resetData(target);

            CharacterKilledEvent killedEvent = new CharacterKilledEvent(target, player, RPUniverse.getInstance().getDataSystem().getPlayerData(target.getUniqueId()));
            RPUniverse.getInstance().getServer().getPluginManager().callEvent(killedEvent);

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

    private void resetData(Player player) {
        player.teleport(player.getWorld().getSpawnLocation());
        player.setHealth(20);

        PlayerData data = RPUniverse.getInstance().getDataSystem().getPlayerData(player.getUniqueId());

        Job[] array = data.getPlayerJobs().toArray(new Job[0]);
        for (Job job : array) {
            job.removePlayerFromJob(player.getUniqueId());
        }

        data.setFoodLevel(100);
        data.setWaterLevel(100);
        data.setPeeLevel(0);
        data.setPoopLevel(0);
        data.setPlayerMode(PlayerMode.USER);
        data.setSelectedPlayerJob(null);

        player.getInventory().clear();
        player.getEnderChest().clear();
        player.getInventory().getItemInMainHand().setAmount(0);
        player.getInventory().getItemInOffHand().setAmount(0);
        if (player.getInventory().getBoots() != null) player.getInventory().getBoots().setAmount(0);
        if (player.getInventory().getLeggings() != null) player.getInventory().getLeggings().setAmount(0);
        if (player.getInventory().getChestplate() != null) player.getInventory().getChestplate().setAmount(0);
        if (player.getInventory().getHelmet() != null) player.getInventory().getHelmet().setAmount(0);
        Economy econ = RPUniverse.getInstance().getEconomy();
        econ.withdrawPlayer(player, econ.getBalance(player));

        RPUniverse.getInstance().getMenuManager().closeMenu(player);

        RPUniverse.getInstance().getLockHandler().getAllLocks().stream().filter(lock -> {
            if (lock.getOwners() == null) return false;
            return lock.getOwners().contains(player.getUniqueId().toString());
        }).forEach(lock -> RPUniverse.getInstance().getLockHandler().removeLock(lock));
    }
}
