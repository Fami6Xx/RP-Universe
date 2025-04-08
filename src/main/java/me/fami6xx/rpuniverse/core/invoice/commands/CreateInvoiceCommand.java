package me.fami6xx.rpuniverse.core.invoice.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.invoice.Invoice;
import me.fami6xx.rpuniverse.core.invoice.InvoiceModule;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
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
        InvoiceLanguage lang = InvoiceLanguage.getInstance();

        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(lang.errorOnlyPlayersMessage));
            return true;
        }

        // Check if the player has permission
        Player player = (Player) sender;
        if (!player.hasPermission("rpu.invoices.create")) {
            player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
            return true;
        }

        // Check if the command has the correct number of arguments
        if (args.length != 2) {
            player.sendMessage(FamiUtils.formatWithPrefix(lang.errorCommandUsageMessage));
            return true;
        }

        try {
            // Get the target player
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorPlayerNotFoundMessage));
                return true;
            }

            // Check if the player is trying to create an invoice for themselves
            if (player.equals(target)) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorCannotInvoiceSelfMessage));
                return true;
            }

            // Parse the amount
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorInvalidAmountMessage));
                return true;
            }

            // Check if the amount is positive
            if (amount <= 0) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorAmountMustBePositiveMessage));
                return true;
            }

            // Check if decimal amounts are allowed
            if (!module.isDecimalAmountAllowed() && amount != Math.floor(amount)) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorDecimalNotAllowedMessage));
                return true;
            }

            // Check if the players are in the same world
            if (!player.getWorld().equals(target.getWorld())) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorSameWorldMessage));
                return true;
            }

            // Check if the players are within the configured distance
            if (module.isDistanceCheckEnabled()) {
                double distance = player.getLocation().distance(target.getLocation());
                if (distance > module.getMaxDistance()) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorPlayerTooFarMessage));
                    return true;
                }
            }

            // Check if the player can see the target player
            if (module.isMustSeePlayerEnabled() && !player.hasLineOfSight(target)) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorPlayerNotVisibleMessage));
                return true;
            }

            // Get the player's job
            PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
            if (playerData.getSelectedPlayerJob() == null) {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNotInJobMessage));
                return true;
            }
            String jobName = playerData.getSelectedPlayerJob().getName();

            // Create the invoice
            Invoice invoice = module.getManager().createInvoice(jobName, player.getUniqueId(), target.getUniqueId(), amount);

            // Send success messages
            String successMessage = lang.invoiceCreatedMessage
                    .replace("{player}", target.getName())
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{currency}", module.getDefaultCurrency());
            player.sendMessage(FamiUtils.formatWithPrefix(successMessage));

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error creating invoice", e);
            player.sendMessage(FamiUtils.formatWithPrefix(lang.errorCreatingInvoiceMessage));
            return true;
        }
    }
}
