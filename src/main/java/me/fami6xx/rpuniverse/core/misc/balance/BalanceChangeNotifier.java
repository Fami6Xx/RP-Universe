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
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BalanceChangeNotifier extends BukkitRunnable implements Listener {
    private final RPUniverse plugin;
    private final Economy economy;
    private final Map<UUID, Double> balanceMap = new ConcurrentHashMap<>();
    private final String webhookUrl;

    public BalanceChangeNotifier(RPUniverse plugin) {
        this.plugin = plugin;
        this.economy = RPUniverse.getInstance().getEconomy();
        this.webhookUrl = plugin.getConfig().getString("balance.discordWebhookURL");
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
        JSONObject json = new JSONObject();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        String formattedOldBalance = currencyFormat.format(Double.parseDouble(String.format("%.2f", oldBalance)));
        String formattedNewBalance = currencyFormat.format(Double.parseDouble(String.format("%.2f", newBalance)));

        // Create an embed object
        JSONObject embed = new JSONObject();
        embed.put("title", "Balance Update");
        embed.put("description", String.format(
                "Player **%s**'s balance changed.",
                playerName
        ));
        embed.put("fields", Arrays.asList(
                createField("Old Balance", formattedOldBalance, true),
                createField("New Balance", formattedNewBalance, true)
        ));
        embed.put("timestamp", java.time.Instant.now().toString());

        // Add the embed to the payload
        json.put("embeds", Collections.singletonList(embed));
        json.put("username", "Balance Notifier");

        return json.toJSONString();
    }

    private JSONObject createField(String name, String value, boolean inline) {
        JSONObject field = new JSONObject();
        field.put("name", name);
        field.put("value", value);
        field.put("inline", inline);
        return field;
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

                // Get the response code
                int responseCode = conn.getResponseCode();

                // Read the response body
                InputStream is = (responseCode >= 200 && responseCode < 400) ? conn.getInputStream() : conn.getErrorStream();
                String responseBody = new BufferedReader(new InputStreamReader(is))
                        .lines().collect(Collectors.joining("\n"));

                // Log the response for debugging
                if (!(responseCode >= 200 && responseCode < 300)) {
                    plugin.getLogger().warning("Failed to send webhook: HTTP " + responseCode);
                    plugin.getLogger().warning("Response body: " + responseBody);
                }

                conn.disconnect();
            } catch (Exception e) {
                plugin.getLogger().severe("Error sending webhook: " + e.getMessage());
            }
        });
    }
}
