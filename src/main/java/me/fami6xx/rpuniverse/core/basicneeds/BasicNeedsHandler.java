package me.fami6xx.rpuniverse.core.basicneeds;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.events.FoodTrackerListener;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.basichandlers.ActionBarHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Queue;

public class BasicNeedsHandler {
    private transient RPUniverse plugin;
    private final HashMap<ItemStack, ConsumableItem> consumables = new HashMap<>();
    private transient BasicNeedsConfig config;
    private transient BukkitTask actionBarTask;
    private transient BukkitTask removeNeedsTask;


    public void initialize(RPUniverse plugin) {
        this.plugin = plugin;
        this.config = new BasicNeedsConfig(plugin);

        plugin.getServer().getPluginManager().registerEvents(new FoodTrackerListener(), plugin);

        if(config.isEnabled()) {
            actionBarTask = displayInterval();
            removeNeedsTask = removeNeedsInterval();
        }
    }

    public void shutdown() {
        if(actionBarTask != null) {
            actionBarTask.cancel();
        }
        if(removeNeedsTask != null) {
            removeNeedsTask.cancel();
        }
    }

    private BukkitTask displayInterval() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                    ActionBarHandler actionBarHandler = plugin.getActionBarHandler();
                    PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
                    Queue<String> messages = actionBarHandler.getMessages(player);
                    if (messages != null) {
                        if(messages.size() >= 3) return;
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("{food}", formatNeedForActionBar(playerData.getFoodLevel()));
                        placeholders.put("{water}", formatNeedForActionBar(playerData.getWaterLevel()));
                        placeholders.put("{pee}", formatNeedForActionBar(playerData.getPeeLevel()));
                        placeholders.put("{poop}", formatNeedForActionBar(playerData.getPoopLevel()));
                        actionBarHandler.addMessage(player, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().basicNeedsActionBarMessage, placeholders), false, true);
                    }
                });
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private BukkitTask removeNeedsInterval() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                    PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
                    playerData.setFoodLevel(playerData.getFoodLevel() - config.getRemovedHunger());
                    playerData.setWaterLevel(playerData.getWaterLevel() - config.getRemovedThirst());
                    playerData.setPeeLevel(playerData.getPeeLevel() + config.getAddedPee());
                    playerData.setPoopLevel(playerData.getPoopLevel() + config.getAddedPoop());
                });
            }
        }.runTaskTimer(plugin, 0, config.getInterval() * 60L);
    }

    private String formatNeedForActionBar(int level) {
        if (level < 25) {
            return "&4" + level;
        } else if (level < 75) {
            return "&e" + level;
        } else {
            return "&a" + level;
        }
    }

    /**
     * Get the consumable items.
     *
     * @return the consumable items
     */
    public HashMap<ItemStack, ConsumableItem> getConsumables() {
        return consumables;
    }

    /**
     * Add a consumable item to the handler.
     *
     * @param item the item to add
     * @param consumable the consumable item
     */
    public void addConsumable(ItemStack item, ConsumableItem consumable) {
        consumables.put(item, consumable);
    }

    /**
     * Get the consumable item for the given item.
     *
     * @param item the item to get the consumable item for
     * @return the consumable item
     */
    public ConsumableItem getConsumable(ItemStack item) {
        return consumables.get(item);
    }

    /**
     * Remove a consumable item from the handler.
     *
     * @param item the item to remove
     */
    public void removeConsumable(ItemStack item) {
        consumables.remove(item);
    }

    /**
     * Check if the given item is a consumable.
     *
     * @param item the item to check
     * @return true if the item is a consumable, false otherwise
     */
    public boolean isConsumable(ItemStack item) {
        return consumables.containsKey(item.asOne());
    }

    /**
     * Gets the basic need config.
     *
     * @return the basic needs config
     */
    public BasicNeedsConfig getConfig() {
        return config;
    }
}
