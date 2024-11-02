package me.fami6xx.rpuniverse.core.commands;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.FollowingHologram;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class TryCommand implements CommandExecutor {

    // Cooldown time in milliseconds (5 seconds)
    private static final long COOLDOWN_TIME = 5000;
    private static HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorOnlyPlayersCanUseThisCommandMessage));
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Check for cooldown
        if (cooldowns.containsKey(playerUUID)) {
            long lastUsed = cooldowns.get(playerUUID);
            long timeElapsed = currentTime - lastUsed;

            if (timeElapsed < COOLDOWN_TIME) {
                long timeLeft = (COOLDOWN_TIME - timeElapsed) / 1000;
                HashMap<String, String> replace = new HashMap<>();
                replace.put("{time}", String.valueOf(timeLeft));
                FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().tryCommandCooldownMessage, replace);
                return true;
            }
        }

        // Update the cooldown
        cooldowns.put(playerUUID, currentTime);

        String message = "";
        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String arg : args) {
                builder.append(arg).append(" ");
            }
            message = builder.toString().trim();
        }

        HashMap<String, String> replace = new HashMap<>();
        replace.put("{player}", player.getName());
        replace.put("{message}", message);

        // Get range from configuration
        int range = 0;
        int timeAlive = 0;

        try {
            range = RPUniverse.getInstance().getConfiguration().getInt("holograms.range");
        } catch (Exception exc) {
            replace.put("{value}", "holograms.range");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
            return true;
        }

        try {
            timeAlive = RPUniverse.getInstance().getConfiguration().getInt("holograms.timeAlive");
        }catch (Exception exc){
            replace.put("{value}", "holograms.timeAlive");
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace);
            return true;
        }

        // Generate random yes/no with 50% chance
        boolean result = Math.random() < 0.5;

        // Get the appropriate message from the language handler
        String outputMessage;
        if (result) {
            outputMessage = RPUniverse.getLanguageHandler().tryCommandYesMessage;
        } else {
            outputMessage = RPUniverse.getLanguageHandler().tryCommandNoMessage;
        }

        // Send message in range
        FamiUtils.sendMessageInRange(player, FamiUtils.formatWithPrefix(outputMessage), range, replace);

        // Create hologram
        new FollowingHologram(player, range, false, false, timeAlive * 20)
                .addLine(FamiUtils.replaceAndFormat((result ? RPUniverse.getLanguageHandler().tryCommandHologramYes : RPUniverse.getLanguageHandler().tryCommandHologramNo), replace));

        return true;
    }
}
