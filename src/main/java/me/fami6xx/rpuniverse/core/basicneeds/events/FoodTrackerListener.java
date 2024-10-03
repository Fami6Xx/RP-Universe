package me.fami6xx.rpuniverse.core.basicneeds.events;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.basicneeds.ConsumableItem;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;


public class FoodTrackerListener implements Listener {
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        if (RPUniverse.getInstance().getBasicNeedsHandler().getConfig().isEnabled()) {
            Player player = event.getPlayer();
            PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

            if (data == null) {
                return;
            }

            BasicNeedsHandler handler = RPUniverse.getInstance().getBasicNeedsHandler();

            if (handler.isConsumable(event.getItem())) {
                ConsumableItem consumable = handler.getConsumable(event.getItem());
                data.setFoodLevel(data.getFoodLevel() + consumable.getFood());
                data.setWaterLevel(data.getWaterLevel() + consumable.getWater());
                data.setPeeLevel(data.getPeeLevel() + consumable.getPee());
                data.setPoopLevel(data.getPoopLevel() + consumable.getPoop());

                if (consumable.getHealth() > 0 || consumable.getHealth() < -20) {
                    double health = player.getHealth() + consumable.getHealth();
                    if(health > 20) {
                        health = 20;
                    }else if(health < 0) {
                        health = 0;
                    }

                    player.setHealth(health);
                }

                if (handler.isConsumable(player.getInventory().getItemInMainHand()) && handler.getConsumable(player.getInventory().getItemInMainHand()) == handler.getConsumable(event.getItem())) {
                    player.getInventory().getItemInMainHand().subtract();
                }else if (handler.isConsumable(player.getInventory().getItemInOffHand()) && handler.getConsumable(player.getInventory().getItemInOffHand()) == handler.getConsumable(event.getItem())){
                    player.getInventory().getItemInOffHand().subtract();
                }else{
                    player.getInventory().remove(event.getItem());
                }
            }

            event.setCancelled(true);
            event.getPlayer().setFoodLevel(18);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (RPUniverse.getInstance().getBasicNeedsHandler().getConfig().isEnabled()) {
            event.setKeepInventory(true);
            PlayerData data = RPUniverse.getPlayerData(event.getEntity().getUniqueId().toString());
            data.setFoodLevel(50);
            data.setWaterLevel(50);
            data.setPeeLevel(0);
            data.setPoopLevel(0);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (RPUniverse.getInstance().getBasicNeedsHandler().getConfig().isEnabled()) {
            event.setCancelled(true);
            Player player = (Player) event.getEntity();
            player.setFoodLevel(18);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (RPUniverse.getInstance().getBasicNeedsHandler().getConfig().isEnabled()) {
            event.getPlayer().setFoodLevel(18);
        }
    }
}
