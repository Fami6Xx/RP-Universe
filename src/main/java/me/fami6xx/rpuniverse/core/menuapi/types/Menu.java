package me.fami6xx.rpuniverse.core.menuapi.types;

import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Menu implements InventoryHolder {
    protected PlayerMenu playerMenu;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = makeColoredGlass((short) 7);

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
        if(inventory != null){
            if(playerMenu.getPlayer().getOpenInventory() == null){
                closeAndCreateInv();
                return;
            }

            if(playerMenu.getPlayer().getOpenInventory().getTopInventory() == null){
                closeAndCreateInv();
                return;
            }

            if(playerMenu.getPlayer().getOpenInventory().getTopInventory() != this.inventory){
                closeAndCreateInv();
                return;
            }

            this.setMenuItems();
        }else{
            closeAndCreateInv();
        }
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

    public ItemStack makeColoredGlass(short color){
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, color);

        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);

        return glass;
    }

    public ItemStack makeItem(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(FamiUtils.format(displayName));
        List<String> loreList = Arrays.asList(lore);
        if(lore.length == 1){
            if(lore[0] != null || !lore[0].isEmpty())
                loreList = Arrays.asList(lore[0].split("~"));
        }
        loreList = loreList.stream().map(FamiUtils::format).collect(Collectors.toList());
        itemMeta.setLore(loreList);
        item.setItemMeta(itemMeta);

        return item;
    }

}

