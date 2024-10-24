package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkingStepEditorMenu extends Menu {

    private WorkingStep workingStep;

    public WorkingStepEditorMenu(PlayerMenu menu, WorkingStep workingStep) {
        super(menu);
        this.workingStep = workingStep;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix(ChatColor.GREEN + "Edit Working Step");
    }

    @Override
    public int getSlots() {
        return 54; // Full double chest size for more space
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true); // Prevent taking items

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) e.getWhoClicked();
        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        switch (displayName) {
            case "Edit Working Locations":
                // Open WorkingLocationsMenu
                WorkingLocationsMenu locationsMenu = new WorkingLocationsMenu(playerMenu, workingStep);
                locationsMenu.open();
                break;
            case "Edit Time for Step":
                player.sendMessage(FamiUtils.formatWithPrefix("Please enter the new time for the step in ticks. (Seconds * 20)"));

                playerMenu.setPendingAction((input) -> {
                    try {
                        int newTime = Integer.parseInt(input);
                        workingStep.setTimeForStep(newTime);
                        player.sendMessage(FamiUtils.formatWithPrefix("Time for step updated to " + newTime + " ticks."));
                        this.open();
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Item Needed":
                player.sendMessage(FamiUtils.formatWithPrefix("Please select an item in your hand to set as the needed item, then type 'complete'."));
                playerMenu.setPendingAction((input) -> {
                    if (input.equalsIgnoreCase("complete")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() == Material.AIR) {
                            workingStep.setItemNeeded(null);
                            player.sendMessage(FamiUtils.formatWithPrefix("Item needed updated to nothing."));
                            return;
                        }
                        workingStep.setItemNeeded(item);
                        player.sendMessage(FamiUtils.formatWithPrefix("Item needed updated to " + workingStep.getItemNeeded().getType().name() + "."));
                        this.open();
                        return;
                    }
                    Material material = Material.matchMaterial(input.toUpperCase());
                    if (material != null) {
                        workingStep.setItemNeeded(new ItemStack(material));
                        player.sendMessage(FamiUtils.formatWithPrefix("Item needed updated to " + material.name() + "."));
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix("Invalid material."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Amount of Item Needed":
                player.sendMessage(FamiUtils.formatWithPrefix("Please enter the new amount of the item needed."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newAmount = Integer.parseInt(input);
                        workingStep.setAmountOfItemNeeded(newAmount);
                        player.sendMessage(FamiUtils.formatWithPrefix("Amount of item needed updated to " + newAmount + "."));
                        this.open();
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Item Given":
                player.sendMessage(FamiUtils.formatWithPrefix("Please select an item in your hand to set as the item given and type 'complete'."));
                playerMenu.setPendingAction((input) -> {
                    if (input.equalsIgnoreCase("complete")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() == Material.AIR) {
                            player.sendMessage(FamiUtils.formatWithPrefix("Item given cannot be air."));
                            return;
                        }
                        workingStep.setItemNeeded(item);
                        player.sendMessage(FamiUtils.formatWithPrefix("Item needed updated to " + workingStep.getItemNeeded().getType().name() + "."));
                        this.open();
                        return;
                    }

                    Material material = Material.matchMaterial(input.toUpperCase());
                    if (material != null) {
                        workingStep.setItemGiven(new ItemStack(material));
                        player.sendMessage(FamiUtils.formatWithPrefix("Item given updated to " + material.name() + "."));
                        this.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix("Invalid material."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Amount of Item Given":
                player.sendMessage(FamiUtils.formatWithPrefix("Please enter the new amount of the item given."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newAmount = Integer.parseInt(input);
                        if (newAmount > 0) {
                            workingStep.setAmountOfItemGiven(newAmount);
                            player.sendMessage(FamiUtils.formatWithPrefix("Amount of item given updated to " + newAmount + "."));
                            this.open();
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("Amount must be greater than zero."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Needed Permission Level":
                player.sendMessage(FamiUtils.formatWithPrefix("Please enter the new permission level required."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newLevel = Integer.parseInt(input);
                        if (newLevel > 0) {
                            workingStep.setNeededPermissionLevel(newLevel);
                            player.sendMessage(FamiUtils.formatWithPrefix("Permission level updated to " + newLevel + "."));
                            this.open();
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("Permission level must be greater than zero."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Save and Close":
                player.sendMessage(FamiUtils.formatWithPrefix("Working step saved successfully."));
                player.closeInventory();
                break;
            case "Cancel":
                player.sendMessage(FamiUtils.formatWithPrefix("Edits canceled."));
                player.closeInventory();
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        inventory.setItem(11, createMenuItem(Material.COMMAND_BLOCK, "Edit Working Locations", "Click to manage working locations."));
        inventory.setItem(13, createMenuItem(Material.CLOCK, "Edit Time for Step", "Click to set the time required for this step."));
        inventory.setItem(15, createMenuItem(Material.EMERALD, "Edit Item Needed", "Click to set the required item."));
        inventory.setItem(19, createMenuItem(Material.NETHER_STAR, "Edit Amount of Item Needed", "Click to set the amount of the required item."));
        inventory.setItem(21, createMenuItem(Material.GOLD_INGOT, "Edit Item Given", "Click to set the item given upon completion."));
        inventory.setItem(23, createMenuItem(Material.DIAMOND, "Edit Amount of Item Given", "Click to set the amount of the item given."));
        inventory.setItem(29, createMenuItem(Material.EXPERIENCE_BOTTLE, "Edit Needed Permission Level", "Click to set the required permission level."));
        inventory.setItem(49, createMenuItem(Material.LIME_WOOL, "Save and Close", "Click to save changes and close the menu."));
        inventory.setItem(51, createMenuItem(Material.RED_WOOL, "Cancel", "Click to cancel edits and close the menu."));

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
