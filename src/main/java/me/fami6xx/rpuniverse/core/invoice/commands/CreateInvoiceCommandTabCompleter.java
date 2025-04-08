package me.fami6xx.rpuniverse.core.invoice.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tab completer for the /createinvoice command.
 * <p>
 * This class provides tab completion for the /createinvoice command, suggesting
 * online players for the first argument and common amounts for the second argument.
 */
public class CreateInvoiceCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        
        if (!player.hasPermission("rpu.invoices.create")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            // Suggest online players except the sender
            return Bukkit.getServer().getOnlinePlayers().stream()
                    .filter(p -> !p.equals(player)) // Exclude the sender
                    .map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            // Suggest common amounts
            List<String> amounts = Arrays.asList("10", "50", "100", "500", "1000");
            return amounts.stream()
                    .filter(amount -> amount.startsWith(args[1]))
                    .collect(Collectors.toList());
        }
        
        return new ArrayList<>();
    }
}