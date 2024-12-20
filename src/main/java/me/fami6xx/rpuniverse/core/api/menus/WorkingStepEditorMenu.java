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
            case "Edit Name":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new name for the working step."));
                playerMenu.setPendingAction((input) -> {
                    workingStep.setName(input);
                    player.sendMessage(FamiUtils.formatWithPrefix("&7Name updated to \"" + input + "\"."));
                    this.open();
                });
                player.closeInventory();
                break;
            case "Edit Description":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new description for the working step."));
                playerMenu.setPendingAction((input) -> {
                    workingStep.setDescription(input);
                    player.sendMessage(FamiUtils.formatWithPrefix("&7Description updated."));
                    this.open();
                });
                player.closeInventory();
                break;
            case "Edit Working Step Being Done Message":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new message displayed during the working step."));
                playerMenu.setPendingAction((input) -> {
                    workingStep.setWorkingStepBeingDoneMessage(input);
                    player.sendMessage(FamiUtils.formatWithPrefix("&7Working step message updated."));
                    this.open();
                });
                player.closeInventory();
                break;
            case "Edit Working Locations":
                WorkingLocationsMenu locationsMenu = new WorkingLocationsMenu(playerMenu, workingStep);
                locationsMenu.open();
                break;
            case "Edit Time for Step":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new time for the step in ticks. (Seconds * 20)"));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newTime = Integer.parseInt(input);
                        workingStep.setTimeForStep(newTime);
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Time for step updated to " + newTime + " ticks."));
                        this.open();
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Item Needed":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please select an item in your hand to set as the needed item, then type 'complete'."));
                playerMenu.setPendingAction((input) -> {
                    if (input.equalsIgnoreCase("complete")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() == Material.AIR) {
                            workingStep.setItemNeeded(null);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Item needed updated to nothing."));
                        } else {
                            workingStep.setItemNeeded(item);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Item needed updated to " + item.getType().name() + "."));
                        }
                        this.open();
                        return;
                    }
                    Material material = Material.matchMaterial(input.toUpperCase());
                    if (material != null) {
                        workingStep.setItemNeeded(new ItemStack(material));
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Item needed updated to " + material.name() + "."));
                        this.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid material."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Amount of Item Needed":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new amount of the item needed."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newAmount = Integer.parseInt(input);
                        workingStep.setAmountOfItemNeeded(newAmount);
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Amount of item needed updated to " + newAmount + "."));
                        this.open();
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Item Given":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please select an item in your hand to set as the item given and type 'complete'."));
                playerMenu.setPendingAction((input) -> {
                    if (input.equalsIgnoreCase("complete")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() == Material.AIR) {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Item given cannot be air."));
                            return;
                        }
                        workingStep.setItemGiven(item);
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Item given updated to " + item.getType().name() + "."));
                        this.open();
                        return;
                    }

                    Material material = Material.matchMaterial(input.toUpperCase());
                    if (material != null) {
                        workingStep.setItemGiven(new ItemStack(material));
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Item given updated to " + material.name() + "."));
                        this.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid material."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Amount of Item Given":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new amount of the item given."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newAmount = Integer.parseInt(input);
                        if (newAmount > 0) {
                            workingStep.setAmountOfItemGiven(newAmount);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Amount of item given updated to " + newAmount + "."));
                            this.open();
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Amount must be greater than zero."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Needed Permission Level":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new permission level required."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newLevel = Integer.parseInt(input);
                        if (newLevel > 0) {
                            workingStep.setNeededPermissionLevel(newLevel);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Permission level updated to " + newLevel + "."));
                            this.open();
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Permission level must be greater than zero."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Toggle Interactable First Stage":
                boolean currentValue = workingStep.isInteractableFirstStage();
                workingStep.setInteractableFirstStage(!currentValue);
                player.sendMessage(FamiUtils.formatWithPrefix("&7Interactable First Stage set to " + !currentValue + "."));
                this.open();
                break;
            case "Toggle Drop Rare Item":
                boolean currentDropRareItem = workingStep.isDropRareItem();
                workingStep.setDropRareItem(!currentDropRareItem);
                player.sendMessage(FamiUtils.formatWithPrefix("&7Drop Rare Item set to " + !currentDropRareItem + "."));
                this.open();
                break;
            case "Edit Rare Item Drop Percentage":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new drop percentage (0-100)."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        double newPercentage = Double.parseDouble(input);
                        if (newPercentage >= 0.0 && newPercentage <= 100.0) {
                            workingStep.setPercentage(newPercentage);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Drop percentage updated to " + newPercentage + "%."));

                            this.open();
                        } else {
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Percentage must be between 0 and 100."));
                        }
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
                    }
                });
                player.closeInventory();
                break;
            case "Edit Rare Item":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please select an item in your hand to set as the rare item, then type 'complete'."));
                playerMenu.setPendingAction((input) -> {
                    if (input.equalsIgnoreCase("complete")) {
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() == Material.AIR) {
                            workingStep.setRareItem(null);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Rare item removed."));
                        } else {
                            workingStep.setRareItem(item);
                            player.sendMessage(FamiUtils.formatWithPrefix("&7Rare item updated to " + item.getType().name() + "."));
                        }
                        this.open();
                        return;
                    }
                    Material material = Material.matchMaterial(input.toUpperCase());
                    if (material != null) {
                        workingStep.setRareItem(new ItemStack(material));
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Rare item updated to " + material.name() + "."));
                        this.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid material."));
                    }
                });
                player.closeInventory();
                break;
            case "Save and Close":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Working step saved successfully."));
                player.closeInventory();
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
        inventory.setItem(10, createMenuItem(Material.NAME_TAG, "Edit Name", "Current: " + workingStep.getName(), "Click to edit the name."));
        inventory.setItem(12, createMenuItem(Material.BOOK, "Edit Description", "Current: " + workingStep.getDescription(), "Click to edit the description."));
        inventory.setItem(14, createMenuItem(Material.PAPER, "Edit Working Step Being Done Message", "Current: " + workingStep.getWorkingStepBeingDoneMessage(), "Click to edit the message."));
        inventory.setItem(16, createMenuItem(Material.COMMAND_BLOCK, "Edit Working Locations", "Click to manage working locations."));

        inventory.setItem(20, createMenuItem(Material.CLOCK, "Edit Time for Step", "Current: " + workingStep.getTimeForStep() + " ticks", "Click to set the time required for this step."));
        inventory.setItem(22, createMenuItem(Material.EMERALD, "Edit Item Needed", "Current: " + (workingStep.getItemNeeded() != null ? workingStep.getItemNeeded().getType().name() : "None"), "Click to set the required item."));
        inventory.setItem(24, createMenuItem(Material.NETHER_STAR, "Edit Amount of Item Needed", "Current: " + workingStep.getAmountOfItemNeeded(), "Click to set the amount of the required item."));

        inventory.setItem(28, createMenuItem(Material.LEVER, "Toggle Interactable First Stage", "Current: " + workingStep.isInteractableFirstStage(), "Click to toggle interactable first stage."));
        inventory.setItem(30, createMenuItem(Material.GOLD_INGOT, "Edit Item Given", "Current: " + workingStep.getItemGiven().getType().name(), "Click to set the item given upon completion."));
        inventory.setItem(32, createMenuItem(Material.DIAMOND, "Edit Amount of Item Given", "Current: " + workingStep.getAmountOfItemGiven(), "Click to set the amount of the item given."));
        inventory.setItem(34, createMenuItem(Material.EXPERIENCE_BOTTLE, "Edit Needed Permission Level", "Current: " + workingStep.getNeededPermissionLevel(), "Click to set the required permission level."));

        inventory.setItem(38, createMenuItem(Material.CHEST, "Toggle Drop Rare Item", "Current: " + workingStep.isDropRareItem(), "Click to toggle dropping a rare item."));
        inventory.setItem(40, createMenuItem(Material.PAPER, "Edit Rare Item Drop Percentage", "Current: " + workingStep.getPercentage() + "%", "Click to set the drop percentage."));
        inventory.setItem(42, createMenuItem(Material.DIAMOND, "Edit Rare Item", "Current: " + (workingStep.getRareItem() != null ? workingStep.getRareItem().getType().name() : "None"), "Click to set the rare item."));

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
        return new ArrayList<>();
    }
}
