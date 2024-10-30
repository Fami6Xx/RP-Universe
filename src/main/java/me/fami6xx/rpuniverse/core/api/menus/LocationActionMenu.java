package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.WorkingStepLocationAddedEvent;
import me.fami6xx.rpuniverse.core.api.WorkingStepLocationRemovedEvent;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationActionMenu extends Menu {

    private WorkingStep workingStep;
    private org.bukkit.Location location;

    public LocationActionMenu(PlayerMenu menu, WorkingStep workingStep, org.bukkit.Location location) {
        super(menu);
        this.workingStep = workingStep;
        this.location = location;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix(ChatColor.AQUA + "Location Actions");
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) e.getWhoClicked();
        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        switch (displayName) {
            case "Reposition to Player's Location":
                org.bukkit.Location playerLoc = player.getLocation();
                workingStep.getWorkingLocations().remove(location);
                workingStep.getWorkingLocations().add(playerLoc.add(0, 1.5, 0));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(new WorkingStepLocationRemovedEvent(workingStep, location));
                        Bukkit.getPluginManager().callEvent(new WorkingStepLocationAddedEvent(workingStep, playerLoc));
                    }
                }.runTask(RPUniverse.getJavaPlugin());

                player.sendMessage(FamiUtils.formatWithPrefix("&7Location has been repositioned to your current location."));
                this.open();
                break;
            case "Remove Location":
                workingStep.removeWorkingLocation(location);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.getPluginManager().callEvent(new WorkingStepLocationRemovedEvent(workingStep, location));
                    }
                }.runTask(RPUniverse.getJavaPlugin());
                player.sendMessage(FamiUtils.formatWithPrefix("&7Location has been removed."));
                player.closeInventory();
                break;
            case "Port to Location":
                player.teleport(location);
                player.sendMessage(FamiUtils.formatWithPrefix("&7Teleported to the location."));
                player.closeInventory();
                break;
            case "Back":
                WorkingLocationsMenu locationsMenu = new WorkingLocationsMenu(playerMenu, workingStep);
                locationsMenu.open();
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // Clear existing items
        inventory.clear();

        // Define action items
        inventory.setItem(10, createMenuItem(Material.PLAYER_HEAD, "Reposition to Player's Location", "Set this location to your current position."));
        inventory.setItem(13, createMenuItem(Material.BARRIER, "Remove Location", "Remove this location from the working step."));
        inventory.setItem(16, createMenuItem(Material.ENDER_PEARL, "Port to Location", "Teleport to this location."));
        inventory.setItem(22, createMenuItem(Material.ARROW, "Back", "Go back to the previous menu."));

        // Fill the rest with filler glass
        setFillerGlass();
    }

    private ItemStack createMenuItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        meta.setLore(Arrays.asList(ChatColor.GRAY + lore));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }
}
