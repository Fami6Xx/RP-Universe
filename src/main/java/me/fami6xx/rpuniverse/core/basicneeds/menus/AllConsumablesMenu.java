package me.fami6xx.rpuniverse.core.basicneeds.menus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.basicneeds.ConsumableItem;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.ItemStackAdapter;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AllConsumablesMenu extends EasyPaginatedMenu {
    private final BasicNeedsHandler handler;
    public AllConsumablesMenu(PlayerMenu menu, BasicNeedsHandler basicNeedsHandler) {
        super(menu);
        this.handler = basicNeedsHandler;
        menu.setCurrentMenu(this);
        menu.setEditingJob(null);
    }

    /**
     * Gets ItemStack you created from your collection
     *
     * @param index Index of item you want to get from collection
     * @return ItemStack you create
     */
    @Override
    public ItemStack getItemFromIndex(int index) {
        return (ItemStack) handler.getConsumables().keySet().toArray()[index];
    }

    /**
     * @return Size of collection you use
     */
    @Override
    public int getCollectionSize() {
        return handler.getConsumables().size();
    }

    /**
     * Handles click on your item
     *
     * @param e Previously handled InventoryClickEvent
     */
    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if(e.getSlot() == 45){
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().sendMessage(FamiUtils.format(RPUniverse.getLanguageHandler().allConsumableMenuAddItemMessage));
            UniversalChatHandler universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();
            universalChatHandler.addToQueue((Player) e.getWhoClicked(), (player, message) -> {
                ItemStack item = e.getWhoClicked().getInventory().getItemInMainHand().clone();
                if(item.getType() == Material.AIR){
                    e.getWhoClicked().sendMessage(FamiUtils.format(RPUniverse.getLanguageHandler().allConsumableMenuAddItemMessageError));
                    return true;
                }

                item.setAmount(1);

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                        .create();
                String json = gson.toJson(item);
                handler.addConsumable(gson.fromJson(json, ItemStack.class), new ConsumableItem(0,0,0,0,0));
                e.getWhoClicked().sendMessage(FamiUtils.format(RPUniverse.getLanguageHandler().allConsumableMenuAddItemMessageSuccess));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        RPUniverse.getInstance().getMenuManager().reopenMenus((player1 -> {
                            PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(player1);

                            if(playerMenu == null){
                                return false;
                            }

                            return playerMenu.getCurrentMenu() instanceof AllConsumablesMenu;
                        }));

                        new AllConsumablesMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu((Player) e.getWhoClicked()), handler).open();
                    }
                }.runTaskLater(RPUniverse.getInstance(), 1);
                return true;
            });
            return;
        }
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().allConsumablesMenuName);
    }

    /**
     * A method where you can add your own items to the inventory border for example.
     */
    @Override
    public void addAdditionalItems() {
        inventory.setItem(45, FamiUtils.makeItem(Material.EMERALD_BLOCK, FamiUtils.format(RPUniverse.getLanguageHandler().allConsumableMenuAddItemDisplayName), FamiUtils.format(RPUniverse.getLanguageHandler().allConsumableMenuAddItemLore)));
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        tags.add(MenuTag.ALL_CONSUMABLES);
        return tags;
    }
}
