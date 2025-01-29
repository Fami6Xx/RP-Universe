package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
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

public class WorkingStepEditorMenu extends Menu {

    private final WorkingStep workingStep;

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
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);

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

            case "Manage Items Needed":
                WorkingStepNeededItemsMenu neededItemsMenu = new WorkingStepNeededItemsMenu(playerMenu, workingStep);
                neededItemsMenu.open();
                break;

            case "Manage Possible Drops":
                PossibleDropsEditorMenu dropsEditor = new PossibleDropsEditorMenu(playerMenu, workingStep);
                dropsEditor.open();
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

            case "Toggle Depletion":
                boolean allowDep = workingStep.isAllowDepletion();
                workingStep.setAllowDepletion(!allowDep);
                if(!allowDep) {
                    workingStep.setDepletionAmount(-1);
                    workingStep.setDepletionChance(-1);
                }
                player.sendMessage(FamiUtils.formatWithPrefix("&7Allow Depletion set to " + !allowDep + "."));
                this.open();
                break;

            case "Edit Depletion Chance":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the depletion chance (e.g., 50 for 50%)."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        double newChance = Double.parseDouble(input);
                        // If we set a chance, we set amount to -1 to enforce the "only one can be active" rule.
                        newChance = newChance / 100;
                        workingStep.setDepletionChance(newChance);
                        workingStep.setDepletionAmount(-1);
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Depletion chance set to " + newChance * 100 +
                                "%."));
                        this.open();
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format for chance."));
                    }
                });
                player.closeInventory();
                break;

            case "Edit Depletion Amount":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the depletion amount (integer)."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newAmount = Integer.parseInt(input);
                        // If we set an amount, we set chance to -1 to enforce the "only one can be active" rule.
                        workingStep.setDepletionAmount(newAmount);
                        workingStep.setDepletionChance(-1);
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Depletion amount set to " + newAmount +
                                "."));
                        this.open();
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format for amount."));
                    }
                });
                player.closeInventory();
                break;

            case "Edit Replenish Time":
                player.sendMessage(FamiUtils.formatWithPrefix("&7Please enter the new replenish time in seconds."));
                playerMenu.setPendingAction((input) -> {
                    try {
                        int newTime = Integer.parseInt(input);
                        workingStep.setReplenishTimeTicks(newTime*20);
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Replenish time set to " + newTime + " s."));
                        this.open();
                    } catch (NumberFormatException ex) {
                        player.sendMessage(FamiUtils.formatWithPrefix("&7Invalid number format."));
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
                // Do nothing
                break;
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        inventory.setItem(10, createMenuItem(Material.NAME_TAG,
                "Edit Name",
                "Current: " + workingStep.getName(),
                "Click to edit the name."));

        inventory.setItem(12, createMenuItem(Material.BOOK,
                "Edit Description",
                "Current: " + workingStep.getDescription(),
                "Click to edit the description."));

        inventory.setItem(14, createMenuItem(Material.PAPER,
                "Edit Working Step Being Done Message",
                "Current: " + workingStep.getWorkingStepBeingDoneMessage(),
                "Click to edit the message."));

        inventory.setItem(16, createMenuItem(Material.COMMAND_BLOCK,
                "Edit Working Locations",
                "Click to manage working locations."));

        inventory.setItem(20, createMenuItem(Material.CLOCK,
                "Edit Time for Step",
                "Current: " + workingStep.getTimeForStep() + " ticks",
                "Click to set the time required for this step."));

        inventory.setItem(22, createMenuItem(Material.CHEST,
                "Manage Items Needed",
                "Click to view or add needed items."));

        inventory.setItem(24, createMenuItem(Material.ENDER_CHEST,
                "Manage Possible Drops",
                "Click to edit the possible drops."));

        inventory.setItem(28, createMenuItem(Material.LEVER,
                "Toggle Interactable First Stage",
                "Current: " + workingStep.isInteractableFirstStage(),
                "Click to toggle interactable first stage."));

        inventory.setItem(34, createMenuItem(Material.EXPERIENCE_BOTTLE,
                "Edit Needed Permission Level",
                "Current: " + workingStep.getNeededPermissionLevel(),
                "Click to set the required permission level."));

        inventory.setItem(38, createMenuItem(Material.IRON_ORE,
                "Toggle Depletion",
                "Current: " + workingStep.isAllowDepletion(),
                "Click to enable/disable depletion."));

        inventory.setItem(39, createMenuItem(Material.POTION,
                "Edit Depletion Chance",
                "Current: " + workingStep.getDepletionChance() * 100 + "%",
                "If set, amount is forced to -1. (Disabled)"));

        inventory.setItem(41, createMenuItem(Material.STONE,
                "Edit Depletion Amount",
                "Current: " + workingStep.getDepletionAmount(),
                "If set, chance is forced to -1. (Disabled)"));

        inventory.setItem(42, createMenuItem(Material.CLOCK,
                "Edit Replenish Time",
                "Current: " + workingStep.getReplenishTimeTicks()/20 + " seconds",
                "Time until location is restored."));

        inventory.setItem(48, createMenuItem(Material.LIME_WOOL,
                "Save and Close",
                "Click to save changes and close the menu."));

        inventory.setItem(50, createMenuItem(Material.RED_WOOL,
                "Cancel",
                "Click to cancel edits and close the menu."));

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
