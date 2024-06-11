package me.fami6xx.rpuniverse.core.locks.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.fami6xx.rpuniverse.core.locks.Lock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

public class AllLocksMenu extends EasyPaginatedMenu {
    private List<Lock> locks;
    private static HashMap<Player, Boolean> creatingLockMap = new HashMap<>();

    public AllLocksMenu(PlayerMenu menu) {
        super(menu);
        this.locks = RPUniverse.getInstance().getLockHandler().getAllLocks();
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        Lock lock = locks.get(index);
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{lockOwner}", lock.getOwners() == null ? "None" : lock.getOwners().toString());
        placeholders.put("{lockJobName}", lock.getJobName() == null ? "None" : lock.getJobName());
        placeholders.put("{lockMinWorkingLevel}", lock.getMinWorkingLevel() == 0 ? "None" : String.valueOf(lock.getMinWorkingLevel()));
        
        ItemStack item = FamiUtils.makeItem(lock.getShownMaterial(), FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().lockItemDisplayName, placeholders), FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().lockItemLore, placeholders));

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(RPUniverse.getInstance(), "lock"), PersistentDataType.INTEGER, index);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public int getCollectionSize() {
        return locks.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
    
        if (e.getSlot() == 45 && !creatingLockMap.getOrDefault(player, false)) {
            creatingLockMap.put(player, true);
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createLockPrompt);
            FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().cancelActivityMessage);
            player.closeInventory();

            waitForBlockClick(player);
            return;
        }
    }

    private void waitForBlockClick(Player player) {
        Listener listener = new Listener() {
            @EventHandler
            public void onBlockClick(PlayerInteractEvent event) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().equals(player)) {
                    Material blockType = event.getClickedBlock().getType();

                    if (isLockable(blockType)) {
                        createLock(event.getClickedBlock());
                    } else {
                        player.sendMessage(FamiUtils.format(RPUniverse.getLanguageHandler().invalidLockItem));
                    }

                    event.setCancelled(true);
                    HandlerList.unregisterAll(this);
                    creatingLockMap.put(player, false);
                }
            }

            @EventHandler
            public void onPlayerDisconnect(PlayerQuitEvent event) {
                if (event.getPlayer().equals(player)) {
                    HandlerList.unregisterAll(this);
                    creatingLockMap.put(player, false);
                }
            }

            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                if (event.getPlayer().equals(player)) {
                    if (event.getMessage().equalsIgnoreCase("cancel")) {
                        event.setCancelled(true);
                        HandlerList.unregisterAll(this);
                        creatingLockMap.put(player, false);
                    }
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, RPUniverse.getInstance());
    }

    private boolean isLockable(Material material) {
        return material.isBlock() &&
         material != Material.AIR &&
         (material.toString().contains("DOOR") || material.toString().contains("CHEST") || material.toString().contains("BARREL"));
    }

    private void createLock(Block block) {
        new CreateLockSelectTypeMenu(playerMenu, block).open();
    }

    @Override
    public void addAdditionalItems() {
        // 45 - Create Lock
        // 52 - Search
        // 53 - Filter
        inventory.setItem(45, FamiUtils.makeItem(Material.EMERALD_BLOCK, RPUniverse.getLanguageHandler().allLocksMenuCreateLockDisplayName, RPUniverse.getLanguageHandler().allLocksMenuCreateLockLore));
        inventory.setItem(52, FamiUtils.makeItem(Material.BARREL, RPUniverse.getLanguageHandler().allLocksMenuSearchDisplayName, RPUniverse.getLanguageHandler().allLocksMenuSearchLore));
        inventory.setItem(53, FamiUtils.makeItem(Material.BOOK, RPUniverse.getLanguageHandler().allLocksMenuFilterDisplayName, RPUniverse.getLanguageHandler().allLocksMenuFilterLore));
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().allLocksMenuName);
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> menuTags = new ArrayList<>();
        menuTags.add(MenuTag.ADMIN);
        menuTags.add(MenuTag.ALL_LOCKS);
        return menuTags;
    }

}
