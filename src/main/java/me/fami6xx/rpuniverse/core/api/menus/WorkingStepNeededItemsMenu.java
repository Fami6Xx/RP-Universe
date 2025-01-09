package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep.NeededItem;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WorkingStepNeededItemsMenu extends EasyPaginatedMenu {

    private final WorkingStep workingStep;

    public WorkingStepNeededItemsMenu(PlayerMenu menu, WorkingStep workingStep) {
        super(menu);
        this.workingStep = workingStep;
    }

    /**
     * Gets menu name.
     */
    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&aNeeded Items");
    }

    /**
     * Gets collection size.
     */
    @Override
    public int getCollectionSize() {
        return workingStep.getNeededItems().size();
    }

    /**
     * Gets item from index.
     */
    @Override
    public ItemStack getItemFromIndex(int index) {
        NeededItem neededItem = workingStep.getNeededItems().get(index);
        ItemStack display = neededItem.getItem().clone();
        ItemMeta meta = display.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + neededItem.getItem().getType().name());
        meta.setLore(List.of(
                ChatColor.GRAY + "Amount needed: " + neededItem.getAmount(),
                ChatColor.YELLOW + "Shift+Click to increase amount by 1.",
                ChatColor.RED + "Click to remove this item."
        ));
        display.setItemMeta(meta);
        return display;
    }

    /**
     * Handles click on items.
     */
    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (e.getCurrentItem().getType() == Material.BARRIER) return;
        if (e.getCurrentItem().getType() == Material.STONE_BUTTON) return;

        int slot = e.getSlot();

        if (slot == 45) {
            ItemStack handItem = e.getWhoClicked().getInventory().getItemInMainHand();
            if (handItem == null || handItem.getType() == Material.AIR) {
                e.getWhoClicked().sendMessage(FamiUtils.formatWithPrefix("&cYou must hold an item in your hand to add it."));
                return;
            }
            NeededItem newNeededItem = new NeededItem(handItem.clone().asOne(), 1);
            workingStep.addNeededItem(newNeededItem);
            super.open();
            return;
        }

        int indexClicked = getClickedItemIndex(slot);
        if (indexClicked < 0 || indexClicked >= workingStep.getNeededItems().size()) return;

        NeededItem clickedNeededItem = workingStep.getNeededItems().get(indexClicked);

        if (e.isShiftClick()) {
            if (e.isLeftClick()) {
                clickedNeededItem.setAmount(clickedNeededItem.getAmount() + 1);
            } else if (e.isRightClick()) {
                if (clickedNeededItem.getAmount() > 1) {
                    clickedNeededItem.setAmount(clickedNeededItem.getAmount() - 1);
                }
            }
        } else {
            workingStep.removeNeededItem(clickedNeededItem);
        }
        super.open();
    }

    /**
     * Adds additional items (like "Add new item" button).
     */
    @Override
    public void addAdditionalItems() {
        inventory.setItem(45, makeAddItemButton());
    }

    /**
     * Makes the "Add new item" button.
     */
    private ItemStack makeAddItemButton() {
        ItemStack addButton = new ItemStack(Material.EMERALD);
        ItemMeta meta = addButton.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Add a New Needed Item");
        meta.setLore(List.of(
                ChatColor.GRAY + "Put the item you want to add in your hand,",
                ChatColor.GRAY + "then click this button to add it."
        ));
        addButton.setItemMeta(meta);
        return addButton;
    }

    /**
     * Handles slot index mapping.
     */
    private int getClickedItemIndex(int slot) {
        return getSlotIndex(slot);
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
