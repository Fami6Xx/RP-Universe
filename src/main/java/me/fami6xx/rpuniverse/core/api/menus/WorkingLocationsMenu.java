package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WorkingLocationsMenu extends EasyPaginatedMenu {

    private WorkingStep workingStep;

    public WorkingLocationsMenu(PlayerMenu menu, WorkingStep workingStep) {
        super(menu);
        this.workingStep = workingStep;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix(ChatColor.BLUE + "Working Locations");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        List<org.bukkit.Location> locations = workingStep.getWorkingLocations();
        if (index >= locations.size()) {
            return new ItemStack(Material.AIR);
        }

        org.bukkit.Location loc = locations.get(index);
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Location " + (index + 1));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "World: " + loc.getWorld().getName());
        lore.add(ChatColor.GRAY + "X: " + loc.getX());
        lore.add(ChatColor.GRAY + "Y: " + loc.getY());
        lore.add(ChatColor.GRAY + "Z: " + loc.getZ());
        lore.add(ChatColor.GRAY + "Yaw: " + loc.getYaw());
        lore.add(ChatColor.GRAY + "Pitch: " + loc.getPitch());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int getCollectionSize() {
        return workingStep.getWorkingLocations().size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        int index = getMaxItemsPerPage() * page + getSlotIndex(e.getSlot());
        if (index >= getCollectionSize()) return;

        org.bukkit.Location loc = workingStep.getWorkingLocations().get(index);
        // Open LocationActionMenu for the selected location
        LocationActionMenu actionMenu = new LocationActionMenu(playerMenu, workingStep, loc);
        actionMenu.open();
    }

    @Override
    public void addAdditionalItems() {
        // Add a button to add a new location
        inventory.setItem(49, createMenuItem(Material.LIME_WOOL, "Add New Location", "Click to add a new working location."));
    }

    private ItemStack createMenuItem(Material material, String name, String lore) {
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        meta.setLore(java.util.Collections.singletonList(ChatColor.GRAY + lore));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        super.handleMenu(e);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("Add New Location")) {
            Player player = (Player) e.getWhoClicked();
            player.sendMessage(FamiUtils.formatWithPrefix("Please stand at the desired location and enter 'here' to add it."));
            playerMenu.setPendingAction((input) -> {
                if (input.equalsIgnoreCase("here")) {
                    workingStep.addWorkingLocation(player.getLocation());
                    player.sendMessage(FamiUtils.formatWithPrefix("Location added."));
                    open();
                } else {
                    player.sendMessage(FamiUtils.formatWithPrefix("Invalid input. Please try again."));
                }
            });
            player.closeInventory();
        }
    }
}
