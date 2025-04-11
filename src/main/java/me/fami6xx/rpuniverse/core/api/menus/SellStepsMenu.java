package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.core.api.SellStepLocationAddedEvent;
import me.fami6xx.rpuniverse.core.jobs.SellStep;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SellStepsMenu extends EasyPaginatedMenu {
    private List<SellStep> sellSteps;
    private UUID jobUUID;

    public SellStepsMenu(PlayerMenu menu, List<SellStep> sellSteps, UUID jobUUID) {
        super(menu);
        this.sellSteps = sellSteps;
        this.jobUUID = jobUUID;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        SellStep sellStep = sellSteps.get(index);
        ItemStack item = new ItemStack(Material.PAPER); // You can choose a different material if you prefer
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + sellStep.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Description: " + sellStep.getDescription());
        lore.add(ChatColor.GRAY + "Item to Sell: " + sellStep.getItemToSell().getType().name());
        lore.add(ChatColor.GRAY + "Item Value: $" + sellStep.getItemValue());
        lore.add(ChatColor.GRAY + "Max Sell Amount: " + sellStep.getMaxSellAmount());
        lore.add(ChatColor.GRAY + "Player Percentage: " + sellStep.getPlayerPercentage() + "%");
        lore.add(ChatColor.GRAY + "Job Percentage: " + sellStep.getJobPercentage() + "%");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Left-click to edit");
        lore.add(ChatColor.RED + "Right-click to remove");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int getCollectionSize() {
        return sellSteps.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int clickedIndex = getSlotIndex(e.getSlot());

        if (clickedIndex != -1) {
            SellStep sellStep = sellSteps.get(clickedIndex);
            if (e.isLeftClick()) {
                // Open the SellStepEditorMenu for editing
                new SellStepEditorMenu(playerMenu, sellStep).open();
            } else if (e.isRightClick()) {
                // Remove the SellStep
                sellSteps.remove(clickedIndex);
                player.sendMessage(FamiUtils.formatWithPrefix("Sell Step '" + sellStep.getName() + "' has been removed."));
                // Refresh the menu
                open();
            }
        } else {
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            if (displayName.equalsIgnoreCase("Create New Sell Step")) {
                // Create a new SellStep with default values
                SellStep newSellStep = new SellStep(
                        jobUUID,
                        player.getLocation().add(0, 2.5, 0),
                        new ItemStack(Material.DIRT), // Default item
                        10.0, // Default item value
                        100, // Default time to sell
                        64, // Default max sell amount
                        80.0, // Default player percentage
                        20.0, // Default job percentage
                        "New Sell Step",
                        "Default description"
                );
                sellSteps.add(newSellStep);
                Bukkit.getPluginManager().callEvent(new SellStepLocationAddedEvent(newSellStep, newSellStep.getLocation()));
                new SellStepEditorMenu(playerMenu, newSellStep).open();
            }
        }
    }

    @Override
    public void addAdditionalItems() {
        ItemStack createNew = new ItemStack(Material.EMERALD);
        ItemMeta meta = createNew.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Create New Sell Step");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Click to create a new Sell Step."));
        createNew.setItemMeta(meta);
        inventory.setItem(49, createNew);
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("Sell Steps Menu");
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
