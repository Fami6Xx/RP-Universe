package me.fami6xx.rpuniverse.core.basicneeds.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoodTrackerListener implements Listener {
    private final Map<UUID, Integer> lastFoodLevels = new HashMap<>();
    private final Map<UUID, ItemStack> lastFoodItems = new HashMap<>();

    // ToDo: Think about if it wouldn't be better to have the server admins have an menu where they can add items that
    //  are eatable and how much they add to the food level, this could be even edited so that some foods add water

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        lastFoodLevels.put(player.getUniqueId(), player.getFoodLevel());
        lastFoodItems.put(player.getUniqueId(), event.getItem());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID playerId = player.getUniqueId();

        if (lastFoodLevels.containsKey(playerId) && lastFoodItems.containsKey(playerId)) {
            int foodValue = event.getFoodLevel() - lastFoodLevels.get(playerId);
            ItemStack consumedItem = lastFoodItems.get(playerId);

            // Check if the food level change was caused by consuming food and not saturation
            if (foodValue <= 0) {
                return;
            }

            // Check if the food item consumed is a food item
            if (consumedItem == null || !consumedItem.getType().isEdible()) {
                return;
            }

            // ToDo: Implement adding a food level to the player's food level (Into the PlayerData class

            // Cleanup
            lastFoodLevels.remove(playerId);
            lastFoodItems.remove(playerId);
            event.setCancelled(true);
        }
    }
}
