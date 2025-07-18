package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep.NeededItem;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        String itemName; // Get the name of the item, if it has one
        if (meta.hasDisplayName()) {
            itemName = meta.getDisplayName();
        } else {
            itemName = display.getType().name().replace("_", " ").toLowerCase();
        }
        meta.setDisplayName(ChatColor.GREEN + itemName);
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

        if (slot == 46) {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().getInventory().addItem(workingStep.getNeededItems().stream()
                .map(NeededItem::getItem)
                .toArray(ItemStack[]::new));
            e.getWhoClicked().sendMessage(FamiUtils.formatWithPrefix("&aYou have been given all needed items for this step."));
            return;
        }

        if (slot == 45) {
            Player player = playerMenu.getPlayer();
            player.closeInventory();

            player.sendMessage(FamiUtils.formatWithPrefix("&7Hold the item you want to add in your hand"));
            player.sendMessage(FamiUtils.formatWithPrefix("&7and type how many should the player need in chat."));
            playerMenu.setPendingAction(input -> {
                if (!FamiUtils.isInteger(input)) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cPlease enter a valid number."));
                    super.open();
                    return;
                }

                int amount = Integer.parseInt(input);
                if (amount <= 0) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cAmount must be greater than 0."));
                    super.open();
                    return;
                }

                ItemStack itemInHand = player.getInventory().getItemInMainHand().clone().asOne();
                if (itemInHand.getType() == Material.AIR) {
                    player.sendMessage(FamiUtils.formatWithPrefix("&cYou must hold an item in your hand."));
                    super.open();
                    return;
                }

                workingStep.addNeededItem(new NeededItem(itemInHand, amount));
                String itemName;
                if (itemInHand.getItemMeta() != null && itemInHand.getItemMeta().hasDisplayName()) {
                    itemName = itemInHand.getItemMeta().getDisplayName();
                } else {
                    itemName = itemInHand.getType().name().replace("_", " ").toLowerCase();
                }

                player.sendMessage(FamiUtils.formatWithPrefix("&aAdded " + amount + "x " + itemName + " to needed items."));
                super.open();
            });
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
        inventory.setItem(46, FamiUtils.makeItem(Material.COMMAND_BLOCK, "&c&lClone All Items", "&7Give yourself all items.") );
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
