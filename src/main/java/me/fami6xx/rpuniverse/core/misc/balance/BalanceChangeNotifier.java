package me.fami6xx.rpuniverse.core.misc.balance;

import me.fami6xx.rpuniverse.RPUniverse;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceChangeNotifier extends BukkitRunnable implements Listener {
    private final RPUniverse plugin;
    private final Economy economy;
    private final Map<UUID, Double> balanceMap = new ConcurrentHashMap<>();
    private final String webhookUrl;

    public BalanceChangeNotifier(RPUniverse plugin) {
        this.plugin = plugin;
        this.economy = RPUniverse.getInstance().getEconomy();
        this.webhookUrl = plugin.getConfig().getString("webhook-url");
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            double currentBalance = economy.getBalance(player);
            double previousBalance = balanceMap.getOrDefault(uuid, currentBalance);

            if (currentBalance != previousBalance) {
                balanceMap.put(uuid, currentBalance);

                String payload = preparePayload(player.getName(), previousBalance, currentBalance);

                sendToWebhookAsync(payload);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        double currentBalance = economy.getBalance(player);
        balanceMap.put(uuid, currentBalance);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        balanceMap.remove(uuid);
    }

    private String preparePayload(String playerName, double oldBalance, double newBalance) {
        return String.format(
                "{\"player\":\"%s\",\"old_balance\":%.2f,\"new_balance\":%.2f,\"timestamp\":%d}",
                playerName, oldBalance, newBalance, System.currentTimeMillis()
        );
    }

    private void sendToWebhookAsync(String payload) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(webhookUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    plugin.getLogger().warning("Failed to send webhook: HTTP " + responseCode);
                }

                conn.disconnect();
            } catch (Exception e) {
                plugin.getLogger().severe("Error sending webhook: " + e.getMessage());
            }
        });
    }
}
