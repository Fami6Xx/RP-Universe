package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.core.api.SellStepLocationAddedEvent;
import me.fami6xx.rpuniverse.core.api.SellStepLocationRemovedEvent;
import me.fami6xx.rpuniverse.core.jobs.SellStep;
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

import java.util.ArrayList;
import java.util.List;

public class SellStepEditorMenu extends Menu {

    private SellStep sellStep;

    public SellStepEditorMenu(PlayerMenu menu, SellStep sellStep) {
        super(menu);
        this.sellStep = sellStep;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix(ChatColor.GREEN + "Edit Sell Step");
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
            case "Edit Name":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new name for the sell step."));
                playerMenu.setPendingAction((input) -> {
                    sellStep.setName(input);
                    player.sendMessage(FamiUtils.formatWithPrefix("&7Name updated to \"" + input + "\"."));
                    this.open();
                });
                player.closeInventory();
                break;

            case "Edit Description":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new description for the sell step."));
                playerMenu.setPendingAction((input) -> {
                    sellStep.setDescription(input);
                    player.sendMessage(FamiUtils.formatWithPrefix("&7Description updated."));
                    this.open();
                });
                player.closeInventory();
                break;

            case "Edit Location":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please stand at the new location and type 'confirm'."));
                playerMenu.setPendingAction((input) -> {
                    if (input.equalsIgnoreCase("confirm")) {
                        Bukkit.getPluginManager().callEvent(new SellStepLocationRemovedEvent(sellStep, sellStep.getLocation()));
                        sellStep.setLocation(player.getLocation().add(0, 2.5, 0));
                        Bukkit.getPluginManager().callEvent(new SellStepLocationAddedEvent(sellStep, sellStep.getLocation()));
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Location updated to your current position."));
                        this.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Location update cancelled."));
                        this.open();
                    }
                });
                player.closeInventory();
                break;

            case "Edit Item to Sell":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please hold the item to sell in your hand and type 'confirm'."));
                playerMenu.setPendingAction((input) -> {
                    if (input.equalsIgnoreCase("confirm")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item == null || item.getType() == Material.AIR) {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7You must hold an item in your hand."));
                        } else {
                            sellStep.setItemToSell(item);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Item to sell updated to " + item.getType().name() + "."));
                        }
                        this.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Item to sell update cancelled."));
                        this.open();
                    }
                });
                player.closeInventory();
                break;

            case "Edit Time to Sell":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new time to sell in ticks (1 second = 20 ticks)."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int time = Integer.parseInt(input);
                        sellStep.setTimeToSell(time);
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Time to sell updated to " + time + " ticks."));
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                    this.open();
                });
                player.closeInventory();
                break;

            case "Edit Max Sell Amount":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the maximum number of items a player can sell at once."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int amount = Integer.parseInt(input);
                        if (amount > 0) {
                            sellStep.setMaxSellAmount(amount);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Max sell amount updated to " + amount + "."));
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Amount must be greater than zero."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                    this.open();
                });
                player.closeInventory();
                break;

            case "Edit Player Percentage":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the percentage that goes to the player (0-100)."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        double percentage = Double.parseDouble(input);
                        if (percentage >= 0 && percentage <= 100) {
                            sellStep.setPlayerPercentage(percentage);
                            sellStep.setJobPercentage(100 - percentage); // Ensure total is 100%
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Player percentage updated to " + percentage + "%."));
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Job percentage automatically set to " + (100 - percentage) + "%."));

                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Percentage must be between 0 and 100."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                    this.open();
                });
                player.closeInventory();
                break;

            case "Edit Job Percentage":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the percentage that goes to the job (0-100)."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        double percentage = Double.parseDouble(input);
                        if (percentage >= 0 && percentage <= 100) {
                            sellStep.setJobPercentage(percentage);
                            sellStep.setPlayerPercentage(100 - percentage); // Ensure total is 100%
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Job percentage updated to " + percentage + "%."));
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Player percentage automatically set to " + (100 - percentage) + "%."));

                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Percentage must be between 0 and 100."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                    this.open();
                });
                player.closeInventory();
                break;

            case "Edit Item Value":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the value of the item (per unit)."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        double value = Double.parseDouble(input);
                        if (value >= 0) {
                            sellStep.setItemValue(value);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Item value updated to $" + value));
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Value must be zero or positive."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                    this.open();
                });
                player.closeInventory();
                break;

            case "Save and Close":
                try {
                    sellStep.validatePercentages();
                    player.sendMessage(FamiUtils.formatWithPrefix("&7Sell step saved successfully."));
                    player.closeInventory();
                } catch (IllegalArgumentException ex) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&c" + ex.getMessage()));
                    this.open();
                }
                break;

            case "Cancel":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Edits canceled."));
                player.closeInventory();
                break;

            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        // Display current values in lore
        inventory.setItem(10, createMenuItem(Material.NAME_TAG, "Edit Name", "Current: " + sellStep.getName(), "Click to edit the name."));
        inventory.setItem(12, createMenuItem(Material.BOOK, "Edit Description", "Current: " + sellStep.getDescription(), "Click to edit the description."));
        inventory.setItem(14, createMenuItem(Material.COMPASS, "Edit Location", "Click to set the sell location to your current position."));
        inventory.setItem(16, createMenuItem(Material.GOLD_NUGGET, "Edit Item Value", "Current: $" + sellStep.getItemValue(), "Click to set the item's value."));

        inventory.setItem(20, createMenuItem(Material.GOLD_INGOT, "Edit Item to Sell", "Current: " + sellStep.getItemToSell().getType().name(), "Click to set the item to sell."));
        inventory.setItem(22, createMenuItem(Material.CLOCK, "Edit Time to Sell", "Current: " + sellStep.getTimeToSell() + " ticks", "Click to set the time required to sell."));
        inventory.setItem(24, createMenuItem(Material.CHEST, "Edit Max Sell Amount", "Current: " + sellStep.getMaxSellAmount(), "Click to set the maximum sell amount."));

        inventory.setItem(30, createMenuItem(Material.EMERALD, "Edit Player Percentage", "Current: " + sellStep.getPlayerPercentage() + "%", "Click to set the percentage for player."));
        inventory.setItem(32, createMenuItem(Material.EXPERIENCE_BOTTLE, "Edit Job Percentage", "Current: " + sellStep.getJobPercentage() + "%", "Click to set the percentage for job."));

        inventory.setItem(48, createMenuItem(Material.LIME_WOOL, "Save and Close", "Click to save changes and close the menu."));
        inventory.setItem(50, createMenuItem(Material.RED_WOOL, "Cancel", "Click to cancel edits and close the menu."));

        setFillerGlass();
    }

    private ItemStack createMenuItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);

        List<String> lore = new ArrayList<>();
        for (String line : loreLines) {
            lore.add(ChatColor.GRAY + line);
        }
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
