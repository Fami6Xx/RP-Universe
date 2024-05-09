package me.fami6xx.rpuniverse.core.menuapi.types;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public abstract class Menu implements InventoryHolder {
    protected PlayerMenu playerMenu;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = makeColoredGlass(DyeColor.GRAY);

    public Menu(PlayerMenu menu) {
        this.playerMenu = menu;
    }
    public abstract String getMenuName();
    public abstract int getSlots();
    public abstract void handleMenu(InventoryClickEvent e);
    public abstract void setMenuItems();
    public abstract List<MenuTag> getMenuTags();

    private void closeAndCreateInv(){
        playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        playerMenu.getPlayer().openInventory(inventory);
    }

    public void open() {
        Menu menu = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(inventory != null){
                    if(playerMenu.getPlayer().getOpenInventory() == null){
                        closeAndCreateInv();
                        return;
                    }

                    if(playerMenu.getPlayer().getOpenInventory().getTopInventory() == null){
                        closeAndCreateInv();
                        return;
                    }

                    if(playerMenu.getPlayer().getOpenInventory().getTopInventory() != menu.inventory){
                        closeAndCreateInv();
                        return;
                    }

                    if(!playerMenu.getPlayer().getOpenInventory().getTitle().equals(menu.getMenuName())){
                        closeAndCreateInv();
                        return;
                    }

                    menu.setMenuItems();
                }else{
                    closeAndCreateInv();
                }
            }
        }.runTaskLater(RPUniverse.getInstance(), 1L);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setFillerGlass(){
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    public ItemStack makeColoredGlass(DyeColor color){
        Material coloredGlassPane = Material.matchMaterial(color.name() + "_STAINED_GLASS_PANE");
        if (coloredGlassPane == null) {
            throw new IllegalArgumentException("Invalid color: " + color);
        }

        ItemStack glass = new ItemStack(coloredGlassPane, 1);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        return glass;
    }
}

