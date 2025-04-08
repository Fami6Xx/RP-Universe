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

            return options.stream()
                    .filter(option -> option.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}