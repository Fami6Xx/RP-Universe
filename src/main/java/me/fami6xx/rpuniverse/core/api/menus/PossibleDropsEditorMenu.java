package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.PossibleDrop;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A paginated menu to show and edit possible drops from a WorkingStep.
 */
public class PossibleDropsEditorMenu extends EasyPaginatedMenu {

    private final WorkingStep workingStep;

    public PossibleDropsEditorMenu(PlayerMenu menu, WorkingStep workingStep) {
        super(menu);
        this.workingStep = workingStep;
    }

    @Override
    public String getMenuName() {
        return ChatColor.DARK_GREEN + "Possible Drops Editor";
    }

    @Override
    public int getCollectionSize() {
        return workingStep.getPossibleDrops().size();
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        PossibleDrop drop = workingStep.getPossibleDrops().get(index);
        ItemStack item = drop.getItem().clone(); // Clone to keep the original intact
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Drop: " + ChatColor.YELLOW + item.getType().toString());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Chance: " + ChatColor.WHITE + drop.getChance() + "%");
            lore.add(ChatColor.GRAY + " ");
            lore.add(ChatColor.YELLOW + "Click" + ChatColor.GRAY + " to remove this drop.");
            lore.add(ChatColor.YELLOW + "Shift-Left-Click" + ChatColor.GRAY + " to add +0.5% chance.");
            lore.add(ChatColor.YELLOW + "Shift-Right-Click" + ChatColor.GRAY + " to subtract -0.5% chance.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        // This method is called AFTER the normal page/close check in EasyPaginatedMenu
        Player player = (Player) e.getWhoClicked();
        ItemStack currentItem = e.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }

        // If the user clicked on an actual drop slot (not border).
        int clickedSlot = e.getSlot();
        int clickedIndex = getSlotIndex(clickedSlot);
        if (clickedIndex >= 0) {
            // We have a valid index in the possibleDrops list
            PossibleDrop drop = workingStep.getPossibleDrops().get(clickedIndex);

            // SHIFT-CLICK checks
            if (e.getClick() == ClickType.SHIFT_LEFT) {
                // Increase chance by 0.5
                double newChance = Math.min(drop.getChance() + 0.5, 100.0);
                setDropChance(clickedIndex, newChance);
                player.sendMessage(FamiUtils.formatWithPrefix("&7Increased chance to &a" + newChance + "%"));
                super.open();
                return;
            }
            if (e.getClick() == ClickType.SHIFT_RIGHT) {
                // Decrease chance by 0.5
                double newChance = Math.max(drop.getChance() - 0.5, 0.0);
                setDropChance(clickedIndex, newChance);
                player.sendMessage(FamiUtils.formatWithPrefix("&7Decreased chance to &a" + newChance + "%"));
                super.open();
                return;
            }

            // Normal (non-shift) click => Remove the drop
            workingStep.getPossibleDrops().remove(clickedIndex);
            player.sendMessage(FamiUtils.formatWithPrefix("&cRemoved drop &7(" + drop.getItem().getType() + " - " + drop.getChance() + "%)."));
            super.open();
        }
    }

    private void setDropChance(int index, double newChance) {
        PossibleDrop originalDrop = workingStep.getPossibleDrops().get(index);
        PossibleDrop modifiedDrop = new PossibleDrop(originalDrop.getItem(), newChance);
        workingStep.getPossibleDrops().set(index, modifiedDrop);
    }

    @Override
    public void addAdditionalItems() {
        // Here you can add items to the border, e.g., an “Add New Drop” button
        ItemStack addNewDrop = new ItemStack(Material.EMERALD);
        ItemMeta meta = addNewDrop.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Add New Drop");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to add a new PossibleDrop");
        meta.setLore(lore);
        addNewDrop.setItemMeta(meta);

        // We'll place it in slot 52 (which is typically part of the border).
        super.inventory.setItem(52, addNewDrop);
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        super.handleMenu(e);
        // The parent handleMenu will call handlePaginatedMenu(e) afterwards
        // for non-border items. But we can still check border items here:

        int slot = e.getSlot();
        Player player = (Player) e.getWhoClicked();

        // If user clicked on the "Add New Drop" in slot 52:
        if (slot == 52) {
            e.setCancelled(true);
            addNewDropFlow(player);
        }
    }

    private void addNewDropFlow(Player player) {
        // Prompt the user to hold an item + type a chance
        player.sendMessage(FamiUtils.formatWithPrefix("&7Type the chance (0-100) for the drop in chat."));
        playerMenu.setPendingAction(input -> {
            try {
                double chance = Double.parseDouble(input);
                if (chance < 0.0 || chance > 100.0) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cChance must be between 0 and 100!"));
                    return;
                }

                // Item in player's main hand is the new drop
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (heldItem == null || heldItem.getType() == Material.AIR) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cYou must hold a valid item in your hand!"));
                    return;
                }

                PossibleDrop newDrop = new PossibleDrop(heldItem.clone(), chance);
                workingStep.getPossibleDrops().add(newDrop);
                player.sendMessage(FamiUtils.formatWithPrefix("&aNew drop added: &f" + heldItem.getType() + " &7(" + chance + "%)."));
                // Re-open the menu
                super.open();
            } catch (NumberFormatException ex) {
                player.sendMessage(FamiUtils.formatWithPrefix("&cInvalid number format."));
            }
        });
        player.closeInventory();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }
}
