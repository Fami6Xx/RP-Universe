package me.fami6xx.rpuniverse.core.invoice.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.invoice.Invoice;
import me.fami6xx.rpuniverse.core.invoice.InvoiceManager;
import me.fami6xx.rpuniverse.core.invoice.InvoiceMenu;
import me.fami6xx.rpuniverse.core.invoice.InvoiceModule;
import me.fami6xx.rpuniverse.core.invoice.admin.AdminInvoiceMenu;
import me.fami6xx.rpuniverse.core.invoice.admin.InvoiceManagementMenu;
import me.fami6xx.rpuniverse.core.invoice.admin.MaintenanceToolsMenu;
import me.fami6xx.rpuniverse.core.invoice.admin.SystemSettingsMenu;
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
     * @param sender  The command sender
     * @param command The command being executed
     * @param label   The command label
     * @param args    The command arguments
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

        // Get the player
        Player player = (Player) sender;

        try {
            // Check for admin subcommand
            if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
                return handleAdminCommand(player, args);
            }

            // Check if the player has permission for regular invoice viewing
            if (!player.hasPermission("rpu.invoices.view")) {
                sender.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                ErrorHandler.debug("InvoiceCommand failed: player " + player.getName() + " has no permission");
                return true;
            }

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
            PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
            new InvoiceMenu(playerMenu, module.getManager(), filterMode).open();
            ErrorHandler.debug("Invoice menu opened for player " + player.getName() + " with filter " + filterMode);

            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error opening invoice menu", e);
            player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            ErrorHandler.debug("InvoiceCommand failed: error opening menu for player " + player.getName());
            return true;
        }
    }

    /**
     * Handles the admin subcommand.
     *
     * @param player The player executing the command
     * @param args   The command arguments
     * @return true if the command was handled, false otherwise
     */
    private boolean handleAdminCommand(Player player, String[] args) {
        InvoiceLanguage lang = InvoiceLanguage.getInstance();
        InvoiceManager manager = module.getManager();

        // Check if the player has admin permission
        if (!player.hasPermission("rpu.invoices.admin")) {
            player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
            ErrorHandler.debug("Admin command failed: player " + player.getName() + " has no admin permission");
            return true;
        }

        // If no subcommand is provided, open the admin menu
        if (args.length == 1) {
            try {
                PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                new AdminInvoiceMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin menu opened for player " + player.getName());
                return true;
            } catch (Exception e) {
                ErrorHandler.severe("Error opening admin menu", e);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
                return true;
            }
        }

        // Handle admin subcommands
        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "view":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.view")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                try {
                    PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                    new InvoiceManagementMenu(playerMenu, manager).open();
                    ErrorHandler.debug("Invoice management menu opened for admin " + player.getName());
                    return true;
                } catch (Exception e) {
                    ErrorHandler.severe("Error opening invoice management menu", e);
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
                    return true;
                }

            case "edit":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.edit")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                // Check if invoice ID is provided
                if (args.length < 3) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /invoices admin edit <id> [field]"));
                    return true;
                }

                // Get the invoice
                String invoiceId = args[2];
                Invoice invoice = manager.getInvoice(invoiceId);

                if (invoice == null) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorInvalidInvoiceIdMessage));
                    return true;
                }

                // For now, just open the management menu which will have edit functionality
                try {
                    PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                    new InvoiceManagementMenu(playerMenu, manager).open();
                    ErrorHandler.debug("Invoice management menu opened for admin " + player.getName() + " to edit invoice " + invoiceId);
                    return true;
                } catch (Exception e) {
                    ErrorHandler.severe("Error opening invoice management menu", e);
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
                    return true;
                }

            case "delete":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.delete")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                // Check if invoice ID is provided
                if (args.length < 3) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /invoices admin delete <id>"));
                    return true;
                }

                // Get the invoice
                invoiceId = args[2];
                invoice = manager.getInvoice(invoiceId);

                if (invoice == null) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorInvalidInvoiceIdMessage));
                    return true;
                }

                // Delete the invoice
                if (manager.deleteInvoice(invoice, player)) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminInvoiceDeletedMessage.replace("{id}", invoiceId)));
                    manager.logAdminAction(player, "delete", invoiceId);
                    return true;
                } else {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorDeletingInvoiceMessage));
                    return true;
                }

            case "restore":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.restore")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                // Check if invoice ID is provided
                if (args.length < 3) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /invoices admin restore <id>"));
                    return true;
                }

                // Get the invoice
                invoiceId = args[2];
                invoice = manager.getInvoice(invoiceId);

                if (invoice == null) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorInvalidInvoiceIdMessage));
                    return true;
                }

                // Restore the invoice
                if (manager.restoreInvoice(invoice, player)) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminInvoiceRestoredMessage.replace("{id}", invoiceId)));
                    return true;
                } else {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorCannotRestoreNonDeletedInvoiceMessage));
                    return true;
                }

            case "pay":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.pay")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                // Check if invoice ID is provided
                if (args.length < 3) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /invoices admin pay <id>"));
                    return true;
                }

                // Get the invoice
                invoiceId = args[2];
                invoice = manager.getInvoice(invoiceId);

                if (invoice == null) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorInvalidInvoiceIdMessage));
                    return true;
                }

                // Force pay the invoice
                if (manager.forcePayInvoice(invoice, player)) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminInvoiceForcePaidMessage.replace("{id}", invoiceId)));
                    return true;
                } else {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorCannotForcePayPaidInvoiceMessage));
                    return true;
                }

            case "maintenance":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.maintenance")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                // Handle maintenance subcommands
                if (args.length > 2) {
                    String maintenanceSubCommand = args[2].toLowerCase();

                    switch (maintenanceSubCommand) {
                        case "backup":
                            String backupFileName = manager.createDataBackup(player);
                            if (backupFileName != null) {
                                player.sendMessage(FamiUtils.formatWithPrefix(lang.adminDataBackupCreatedMessage));
                                player.sendMessage(FamiUtils.formatWithPrefix("&7Backup file: &f" + backupFileName));
                            } else {
                                player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorBackupFailedMessage));
                            }
                            return true;

                        case "restore":
                            if (args.length < 4) {
                                player.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /invoices admin maintenance restore <filename>"));
                                return true;
                            }

                            String fileName = args[3];
                            if (manager.restoreFromBackup(fileName, player)) {
                                player.sendMessage(FamiUtils.formatWithPrefix(lang.adminDataRestoredMessage));
                            } else {
                                player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorRestoreFailedMessage));
                            }
                            return true;

                        case "clear":
                            if (args.length < 4) {
                                player.sendMessage(FamiUtils.formatWithPrefix("&cUsage: /invoices admin maintenance clear <days>"));
                                return true;
                            }

                            try {
                                int days = Integer.parseInt(args[3]);
                                int count = manager.clearOldInvoices(days, player);
                                player.sendMessage(FamiUtils.formatWithPrefix(lang.adminOldInvoicesClearedMessage.replace("{count}", String.valueOf(count))));
                            } catch (NumberFormatException e) {
                                player.sendMessage(FamiUtils.formatWithPrefix("&cInvalid number of days"));
                            }
                            return true;

                        default:
                            player.sendMessage(FamiUtils.formatWithPrefix("&cUnknown maintenance subcommand. Available commands: backup, restore, clear"));
                            return true;
                    }
                }

                // Open the maintenance tools menu
                try {
                    PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                    new MaintenanceToolsMenu(playerMenu, manager).open();
                    ErrorHandler.debug("Maintenance tools menu opened for admin " + player.getName());
                    return true;
                } catch (Exception e) {
                    ErrorHandler.severe("Error opening maintenance tools menu", e);
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
                    return true;
                }

            case "settings":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.maintenance")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                try {
                    PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player);
                    new SystemSettingsMenu(playerMenu, manager).open();
                    ErrorHandler.debug("System settings menu opened for admin " + player.getName());
                    return true;
                } catch (Exception e) {
                    ErrorHandler.severe("Error opening system settings menu", e);
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
                    return true;
                }

            case "logs":
                // Check permission
                if (!player.hasPermission("rpu.invoices.admin.logs")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
                    return true;
                }

                // For now, just inform the player that logs can be viewed in the console
                player.sendMessage(FamiUtils.formatWithPrefix("&aAdmin action logs are available in the server console."));
                return true;

            default:
                player.sendMessage(FamiUtils.formatWithPrefix("&cUnknown admin subcommand. Available commands: view, edit, delete, restore, pay, maintenance, settings, logs"));
                return true;
        }
    }
}
