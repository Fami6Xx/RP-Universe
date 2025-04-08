package me.fami6xx.rpuniverse.core.invoice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completer for the /invoices command.
 * <p>
 * This class provides tab completion for the /invoices command, suggesting
 * the available filter modes based on the player's permissions.
 */
public class InvoiceCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("received");
            options.add("created");

            if (player.hasPermission("rpu.invoices.view.job")) {
                options.add("job");
            }

            if (player.hasPermission("rpu.invoices.admin")) {
                options.add("admin");
            }

            return options.stream()
                    .filter(option -> option.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Handle admin subcommands
        if (args.length >= 2 && args[0].equalsIgnoreCase("admin")) {
            if (args.length == 2) {
                List<String> adminOptions = new ArrayList<>();

                if (player.hasPermission("rpu.invoices.admin.view")) {
                    adminOptions.add("view");
                }

                if (player.hasPermission("rpu.invoices.admin.edit")) {
                    adminOptions.add("edit");
                }

                if (player.hasPermission("rpu.invoices.admin.delete")) {
                    adminOptions.add("delete");
                }

                if (player.hasPermission("rpu.invoices.admin.restore")) {
                    adminOptions.add("restore");
                }

                if (player.hasPermission("rpu.invoices.admin.pay")) {
                    adminOptions.add("pay");
                }

                if (player.hasPermission("rpu.invoices.admin.maintenance")) {
                    adminOptions.add("maintenance");
                    adminOptions.add("settings");
                }

                if (player.hasPermission("rpu.invoices.admin.logs")) {
                    adminOptions.add("logs");
                }

                return adminOptions.stream()
                        .filter(option -> option.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }

            // Handle maintenance subcommands
            if (args.length == 3 && args[1].equalsIgnoreCase("maintenance") && 
                player.hasPermission("rpu.invoices.admin.maintenance")) {
                List<String> maintenanceOptions = Arrays.asList("backup", "restore", "clear");

                return maintenanceOptions.stream()
                        .filter(option -> option.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }
}
