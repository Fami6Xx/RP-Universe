package me.fami6xx.rpuniverse.core.invoice.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.invoice.Invoice;
import me.fami6xx.rpuniverse.core.invoice.InvoiceModule;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for the /createinvoice command.
 * <p>
 * This command allows players in jobs to create invoices for other players.
 * It validates the target player, amount, and other conditions like distance and visibility.
 */
public class CreateInvoiceCommand implements CommandExecutor {

    private final InvoiceModule module;

    /**
     * Creates a new CreateInvoiceCommand instance.
     * 
     * @param module The InvoiceModule instance
     */
    public CreateInvoiceCommand(InvoiceModule module) {
        this.module = module;
    }

    /**
     * Executes the /createinvoice command.
     * <p>
     * This method handles the creation of invoices, including validation of:
     * - Player permissions
     * - Target player existence
     * - Amount validity
     * - Distance and visibility requirements
     * - Job membership
     *
     * @param sender The command sender
     * @param command The command being executed
     * @param label The command label
     * @param args The command arguments
     * @return true if the command was handled, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix("§cOnly players can use this command."));
            return true;
        }

        // Check if the player has permission
        Player player = (Player) sender;
        if (!player.hasPermission("rpu.invoices.create")) {
            player.sendMessage(FamiUtils.formatWithPrefix("§cYou don't have permission to use this command."));
            return true;
        }

        // Check if the command has the correct number of arguments
        if (args.length != 2) {
            player.sendMessage(FamiUtils.formatWithPrefix("§cUsage: /createinvoice <player> <amount>"));
            return true;
        }

        try {
            // Get the target player
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cPlayer not found."));
                return true;
            }

            // Check if the player is trying to create an invoice for themselves
            if (player.equals(target)) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cYou cannot create an invoice for yourself."));
                return true;
            }

            // Parse the amount
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cInvalid amount. Please enter a valid number."));
                return true;
            }

            // Check if the amount is positive
            if (amount <= 0) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cThe amount must be positive."));
                return true;
            }

            // Check if decimal amounts are allowed
            if (!module.isDecimalAmountAllowed() && amount != Math.floor(amount)) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cDecimal amounts are not allowed."));
                return true;
            }

            // Check if the players are in the same world
            if (!player.getWorld().equals(target.getWorld())) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cYou must be in the same world as the target player."));
                return true;
            }

            // Check if the players are within the configured distance
            if (module.isDistanceCheckEnabled()) {
                double distance = player.getLocation().distance(target.getLocation());
                if (distance > module.getMaxDistance()) {
                    player.sendMessage(FamiUtils.formatWithPrefix("§cYou are too far away from the target player."));
                    return true;
                }
            }

            // Check if the player can see the target player
            if (module.isMustSeePlayerEnabled() && !player.hasLineOfSight(target)) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cYou must be able to see the target player."));
                return true;
            }

            // Get the player's job
            PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
            if (playerData.getSelectedPlayerJob() == null) {
                player.sendMessage(FamiUtils.formatWithPrefix("§cYou must be in a job to create an invoice."));
                return true;
            }
            String jobName = playerData.getSelectedPlayerJob().getName();

            // Create the invoice
            Invoice invoice = module.getManager().createInvoice(jobName, player.getUniqueId(), target.getUniqueId(), amount);

            // Send success messages
            player.sendMessage(FamiUtils.formatWithPrefix("§aInvoice created successfully for " + target.getName() + 
                    " for " + amount + module.getDefaultCurrency() + "."));

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error creating invoice", e);
            player.sendMessage(FamiUtils.formatWithPrefix("§cAn error occurred while creating the invoice."));
            return true;
        }
    }
}
