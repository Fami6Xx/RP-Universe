package me.fami6xx.rpuniverse.core.payment.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.payment.PaymentModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Command handler for the /pay command.
 * <p>
 * This command allows players to pay other players, but only if they are within
 * a configurable distance and can see each other (if enabled in the configuration).
 */
public class PayCommand implements CommandExecutor {

    private final PaymentModule module;

    /**
     * Creates a new PayCommand instance.
     * 
     * @param module The PaymentModule instance
     */
    public PayCommand(PaymentModule module) {
        this.module = module;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command is enabled in the configuration
        if (!module.isCommandEnabled()) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandDisabledMessage));
            return true;
        }

        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        // Check if the command has the correct number of arguments
        if (args.length != 2) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandUsageMessage));
            return true;
        }

        Player player = (Player) sender;

        // Get the target player
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandPlayerNotFoundMessage));
            return true;
        }

        // Check if the player is trying to pay themselves
        if (player.equals(target)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandCannotPayYourselfMessage));
            return true;
        }

        // Parse the amount
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandInvalidAmountMessage));
            return true;
        }

        // Check if the amount is positive
        if (amount <= 0) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandAmountMustBePositiveMessage));
            return true;
        }

        // Check if the player has enough money
        if (!RPUniverse.getInstance().getEconomy().has(player, amount)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandNotEnoughMoneyMessage));
            return true;
        }

        // Check if the players are in the same world
        if (!player.getWorld().equals(target.getWorld())) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandMustBeInSameWorldMessage));
            return true;
        }

        // Check if the players are within the configured distance
        if (module.isDistanceCheckEnabled()) {
            double distance = player.getLocation().distance(target.getLocation());
            if (distance > module.getMaxDistance()) {
                sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandTooFarAwayMessage));
                return true;
            }
        }

        // Check if the player can see the target player
        if (module.isLineOfSightCheckEnabled() && !player.hasLineOfSight(target)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPayCommandMustBeAbleToSeeTargetMessage));
            return true;
        }

        // Transfer the money
        RPUniverse.getInstance().getEconomy().withdrawPlayer(player, amount);
        RPUniverse.getInstance().getEconomy().depositPlayer(target, amount);

        // Send success messages
        HashMap<String, String> senderReplace = new HashMap<>();
        senderReplace.put("{targetName}", target.getName());
        senderReplace.put("{amount}", String.valueOf(amount));
        sender.sendMessage(FamiUtils.formatWithPrefix(FamiUtils.replace(RPUniverse.getLanguageHandler().successPayCommandSenderMessage, senderReplace)));

        HashMap<String, String> targetReplace = new HashMap<>();
        targetReplace.put("{amount}", String.valueOf(amount));
        targetReplace.put("{senderName}", player.getName());
        target.sendMessage(FamiUtils.formatWithPrefix(FamiUtils.replace(RPUniverse.getLanguageHandler().successPayCommandTargetMessage, targetReplace)));

        // Log the transaction
        ErrorHandler.info(player.getName() + " paid " + target.getName() + " " + amount + " money.");

        return true;
    }
}
