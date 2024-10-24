package me.fami6xx.rpuniverse.core.basicneeds;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.PlayerDamageByBasicNeedsEvent;
import me.fami6xx.rpuniverse.core.basicneeds.events.FoodTrackerListener;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.basichandlers.ActionBarHandler;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.ItemStackAdapter;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Queue;

/**
 * The BasicNeedsHandler is the main class for handling the basic needs.
 * It is responsible for adding, removing, and updating the basic needs.
 * <p>
 * The BasicNeedsHandler is a singleton class, so only one instance of it should be created.
 * <p>
 * The BasicNeedsHandler is also a listener for the PlayerJoinEvent and PlayerQuitEvent.
 * This means that the BasicNeedsHandler will also listen for these events and update the basic needs accordingly.
 */
public class BasicNeedsHandler {
    private transient RPUniverse plugin;
    private HashMap<ItemStack, ConsumableItem> consumables = new HashMap<>();
    private transient BasicNeedsConfig config;
    private transient BukkitTask actionBarTask;
    private transient BukkitTask removeNeedsTask;


    /**
     * Initialize the BasicNeedsHandler.
     * Loads the consumables and starts the tasks.
     * @param plugin The plugin instance
     */
    public void initialize(RPUniverse plugin) {
        this.plugin = plugin;
        this.config = new BasicNeedsConfig(plugin);

        plugin.getServer().getPluginManager().registerEvents(new FoodTrackerListener(), plugin);

        if(config.isEnabled()) {
            this.consumables = RPUniverse.getInstance().getDataSystem().getDataHandler().loadConsumables();
            actionBarTask = displayInterval();
            removeNeedsTask = removeNeedsInterval();
        }
    }

    /**
     * Shutdown the BasicNeedsHandler.
     * Stops the tasks and removes the consumables.
     */
    public void shutdown() {
        if(actionBarTask != null) {
            actionBarTask.cancel();
        }
        if(removeNeedsTask != null) {
            removeNeedsTask.cancel();
        }
    }

    /**
     * Display the interval.
     * @return The interval
     */
    private BukkitTask displayInterval() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                    player.setFoodLevel(18);
                    ActionBarHandler actionBarHandler = plugin.getActionBarHandler();
                    PlayerData playerData = RPUniverse.getPlayerData(player.getUniqueId().toString());
                    Queue<String> messages = actionBarHandler.getMessages(player);
                    if (messages != null) {
                        if (messages.size() >= 3) return;
                    }
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("{food}", formatNeedForActionBar(playerData.getFoodLevel(), false));
                    placeholders.put("{water}", formatNeedForActionBar(playerData.getWaterLevel(), false));
                    placeholders.put("{pee}", formatNeedForActionBar(playerData.getPeeLevel(), true));
                    placeholders.put("{poop}", formatNeedForActionBar(playerData.getPoopLevel(), true));
                    actionBarHandler.addMessage(player, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().basicNeedsActionBarMessage, placeholders), false);

                    if (playerData.getFoodLevel() == 0 || playerData.getWaterLevel() == 0 || playerData.getPeeLevel() == 100 || playerData.getPoopLevel() == 100) {
                        PlayerDamageByBasicNeedsEvent event = new PlayerDamageByBasicNeedsEvent(player, 1);
                        Bukkit.getPluginManager().callEvent(event);

                        if (event.isCancelled()) return;
                        player.damage(1);
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

                    boolean ignore = RPUniverse.getInstance().getConfiguration().getBoolean("basicNeeds.ignoreInMode");
                    if (ignore && playerData.getPlayerMode() != PlayerMode.USER) return;

                    playerData.setFoodLevel(playerData.getFoodLevel() - config.getRemovedHunger());
                    playerData.setWaterLevel(playerData.getWaterLevel() - config.getRemovedThirst());
                    playerData.setPeeLevel(playerData.getPeeLevel() + config.getAddedPee());
                    playerData.setPoopLevel(playerData.getPoopLevel() + config.getAddedPoop());
                });
            }
        }.runTaskTimer(plugin, 0, config.getInterval() * 60L);
    }

    /**
     * Format the need for the action bar.
     * @param level The level
     * @param isPoopOrPee <code>true</code> for poop or pee, <code>false</code> for food or water
     * @return The formatted need
     */
    public static String formatNeedForActionBar(int level, boolean isPoopOrPee) {
        if (isPoopOrPee){
            if (level < 25) {
                return "&a" + level;
            } else if (level < 70) {
                return "&e" + level;
            } else {
                return "&4" + level;
            }
        }
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
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .create();
        String json = gson.toJson(item.asOne(), ItemStack.class);
        ItemStack item1 = gson.fromJson(json, ItemStack.class);
        return consumables.get(item1);
    }

    /**
     * Remove a consumable item from the handler.
     *
     * @param item the item to remove
     */
    public void removeConsumable(ItemStack item) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .create();
        String json = gson.toJson(item.asOne(), ItemStack.class);
        ItemStack item1 = gson.fromJson(json, ItemStack.class);
        consumables.remove(item1);
    }

    /**
     * Check if the given item is a consumable.
     *
     * @param item the item to check
     * @return true if the item is a consumable, false otherwise
     */
    public boolean isConsumable(ItemStack item) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .create();
        String json = gson.toJson(item.asOne(), ItemStack.class);
        ItemStack item1 = gson.fromJson(json, ItemStack.class);
        return consumables.containsKey(item1);
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
