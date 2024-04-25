package me.fami6xx.rpuniverse.core.basicneeds.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.ConsumableItem;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EditConsumableMenu extends Menu {
    private final ConsumableItem consumable;
    private final ItemStack item;
    public EditConsumableMenu(PlayerMenu menu, ItemStack item, ConsumableItem consumable) {
        super(menu);
        this.consumable = consumable;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().editConsumableMenuName);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getSlot() == 10){
            if(e.isRightClick()) {
                if(consumable.getPoop() + 1 > 100) return;
                consumable.setPoop(consumable.getPoop() + 1);
            }else{
                if(consumable.getPoop() - 1 < -100) return;
                consumable.setPoop(consumable.getPoop() - 1);
            }

            setMenuItems();
            return;
        }
        if(e.getSlot() == 16){
            if(e.isRightClick()) {
                if(consumable.getPee() + 1 > 100) return;
                consumable.setPee(consumable.getPee() + 1);
            }else{
                if(consumable.getPee() - 1 < -100) return;
                consumable.setPee(consumable.getPee() - 1);
            }

            setMenuItems();
            return;
        }
        if(e.getSlot() == 12){
            if(e.isRightClick()) {
                if(consumable.getFood() + 1 > 100) return;
                consumable.setFood(consumable.getFood() + 1);
            }else{
                if(consumable.getFood() - 1 < -100) return;
                consumable.setFood(consumable.getFood() - 1);
            }

            setMenuItems();
            return;
        }
        if(e.getSlot() == 13){
            if(e.isRightClick()) {
                if(consumable.getHealth() + 1 > 20) return;
                consumable.setHealth(consumable.getHealth() + 1);
            }else{
                if(consumable.getHealth() - 1 < -20) return;
                consumable.setHealth(consumable.getHealth() - 1);
            }

            setMenuItems();
            return;
        }
        if(e.getSlot() == 14){
            if(e.isRightClick()) {
                consumable.setWater(consumable.getWater() + 1);
            }else{
                consumable.setWater(consumable.getWater() - 1);
            }

            setMenuItems();
            return;
        }
        if(e.getSlot() == 26){
            RPUniverse.getInstance().getBasicNeedsHandler().removeConsumable(item);
            playerMenu.getPlayer().sendMessage(FamiUtils.format(RPUniverse.getLanguageHandler().editConsumableMenuRemoveItemMessage));
            playerMenu.getPlayer().closeInventory();
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

                    new AllConsumablesMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(playerMenu.getPlayer()), RPUniverse.getInstance().getBasicNeedsHandler()).open();
                }
            }.runTaskLater(RPUniverse.getInstance(), 1);
            return;
        }

        if(e.getSlot() == 18){
            new AllConsumablesMenu(playerMenu, RPUniverse.getInstance().getBasicNeedsHandler()).open();
        }
    }

    @Override
    public void setMenuItems() {
        // 4 - The itemstack
        // 10 - Poop
        // 16 - Pee
        // 12 - Food
        // 13 - Health
        // 14 - Drink
        // 26 - Remove button
        // 18 - Back button

        inventory.setItem(4, item);
        inventory.setItem(10, FamiUtils.makeItem(Material.BROWN_WOOL, RPUniverse.getLanguageHandler().editConsumableMenuPoopItemDisplayName, FamiUtils.replace(RPUniverse.getLanguageHandler().editConsumableMenuPoopItemLore, new HashMap<String, String>(){{
            put("{value}", String.valueOf(consumable.getPoop()));
        }})));
        inventory.setItem(16, FamiUtils.makeItem(Material.YELLOW_WOOL, RPUniverse.getLanguageHandler().editConsumableMenuPeeItemDisplayName, FamiUtils.replace(RPUniverse.getLanguageHandler().editConsumableMenuPeeItemLore, new HashMap<String, String>(){{
            put("{value}", String.valueOf(consumable.getPee()));
        }})));
        inventory.setItem(12, FamiUtils.makeItem(Material.COOKED_BEEF, RPUniverse.getLanguageHandler().editConsumableMenuFoodItemDisplayName, FamiUtils.replace(RPUniverse.getLanguageHandler().editConsumableMenuFoodItemLore, new HashMap<String, String>(){{
            put("{value}", String.valueOf(consumable.getFood()));
        }})));
        inventory.setItem(13, FamiUtils.makeItem(Material.GOLDEN_APPLE, RPUniverse.getLanguageHandler().editConsumableMenuHealthItemDisplayName, FamiUtils.replace(RPUniverse.getLanguageHandler().editConsumableMenuHealthItemLore, new HashMap<String, String>(){{
            put("{value}", String.valueOf(consumable.getHealth()));
        }})));
        inventory.setItem(14, FamiUtils.makeItem(Material.POTION, RPUniverse.getLanguageHandler().editConsumableMenuDrinkItemDisplayName, FamiUtils.replace(RPUniverse.getLanguageHandler().editConsumableMenuDrinkItemLore, new HashMap<String, String>(){{
            put("{value}", String.valueOf(consumable.getWater()));
        }})));
        inventory.setItem(26, FamiUtils.makeItem(Material.RED_WOOL, RPUniverse.getLanguageHandler().editConsumableMenuRemoveItemDisplayName, RPUniverse.getLanguageHandler().editConsumableMenuRemoveItemLore));
        inventory.setItem(18, FamiUtils.makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().generalMenuBackItemDisplayName, RPUniverse.getLanguageHandler().generalMenuBackItemLore));
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return Collections.emptyList();
    }
}
