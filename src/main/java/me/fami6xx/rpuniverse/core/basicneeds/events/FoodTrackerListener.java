package me.fami6xx.rpuniverse.core.basicneeds.events;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.basicneeds.ConsumableItem;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
                    player.setHealth(player.getHealth() + consumable.getHealth());
                }

                if (event.getItem().asOne() == player.getInventory().getItemInMainHand().asOne()) {
                    player.getInventory().getItemInMainHand().subtract();
                }else if (event.getItem().asOne() == player.getInventory().getItemInOffHand().asOne()){
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
