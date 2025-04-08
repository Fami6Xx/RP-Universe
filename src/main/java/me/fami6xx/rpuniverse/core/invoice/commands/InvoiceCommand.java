package me.fami6xx.rpuniverse.core.invoice.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.invoice.InvoiceMenu;
import me.fami6xx.rpuniverse.core.invoice.InvoiceModule;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for the /invoices command.
 * <p>
 * This command opens the invoice menu, allowing players to view and manage their invoices.
 */
public class InvoiceCommand implements CommandExecutor {

    private final InvoiceModule module;

    /**
     * Creates a new InvoiceCommand instance.
     * 
     * @param module The InvoiceModule instance
     */
    public InvoiceCommand(InvoiceModule module) {
        this.module = module;
    }

    /**
     * Executes the /invoices command.
     * <p>
     * This method handles opening the invoice menu with different filter modes:
     * - Default: Shows invoices received by the player
     * - "created": Shows invoices created by the player
     * - "job": Shows all invoices for a specific job (requires permission)
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

        ErrorHandler.debug("InvoiceCommand executed by " + sender.getName() + " with args: " + 
                          (args.length > 0 ? String.join(", ", args) : "none"));

        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(lang.errorOnlyPlayersMessage));
            ErrorHandler.debug("InvoiceCommand failed: sender is not a player");
            return true;
        }

        // Check if the player has permission
        Player player = (Player) sender;
        if (!player.hasPermission("rpu.invoices.view")) {
            sender.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
            ErrorHandler.debug("InvoiceCommand failed: player " + player.getName() + " has no permission");
            return true;
        }

        try {
            // Determine the filter mode based on arguments
            InvoiceMenu.FilterMode filterMode = InvoiceMenu.FilterMode.RECEIVED;

            if (args.length > 0) {
                String filterArg = args[0].toLowerCase();
                if (filterArg.equals("created")) {
                    filterMode = InvoiceMenu.FilterMode.CREATED;
                    ErrorHandler.debug("InvoiceCommand: player " + player.getName() + " using CREATED filter");
                } else if (filterArg.equals("job") && player.hasPermission("rpu.invoices.view.job")) {
                    filterMode = InvoiceMenu.FilterMode.JOB;
                    ErrorHandler.debug("InvoiceCommand: player " + player.getName() + " using JOB filter");
                } else {
                    ErrorHandler.debug("InvoiceCommand: player " + player.getName() + " using default RECEIVED filter (invalid arg: " + filterArg + ")");
                }
            } else {
                ErrorHandler.debug("InvoiceCommand: player " + player.getName() + " using default RECEIVED filter");
            }

            // Open the invoice menu
            PlayerMenu playerMenu = new PlayerMenu(player);
            new InvoiceMenu(playerMenu, module.getManager(), filterMode);
            ErrorHandler.debug("Invoice menu opened for player " + player.getName() + " with filter " + filterMode);

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error opening invoice menu", e);
            player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            ErrorHandler.debug("InvoiceCommand failed: error opening menu for player " + player.getName());
            return true;
        }
    }
}
