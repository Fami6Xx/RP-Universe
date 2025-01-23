package me.fami6xx.rpuniverse.core.misc.language.editor;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.language.LanguageFieldsManager;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LanguageFieldEditorMenu extends Menu {
    private final LanguageField languageField;
    private final Menu previousMenu;

    public LanguageFieldEditorMenu(PlayerMenu menu, LanguageField languageField, Menu previousMenu) {
        super(menu);
        this.languageField = languageField;
        this.previousMenu = previousMenu;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format("&6Edit Field: &e" + languageField.getFieldName());
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
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

        String displayName = FamiUtils.format(e.getCurrentItem().getItemMeta().getDisplayName());
        Player p = (Player) e.getWhoClicked();

        // If "Close", just close
        if (displayName.equals(FamiUtils.format("&cClose"))) {
            p.closeInventory();
            return;
        }

        if (displayName.equals(FamiUtils.format("&cBack"))){
            previousMenu.open();
            return;
        }

        boolean multiLine = languageField.isMultiLine();
        List<String> lines = languageField.getSplitLines();
        int clickedSlot = e.getSlot();

        // Add New Line (only if multiLine)
        if (displayName.equals(FamiUtils.format("&aAdd New Line"))) {
            if (!multiLine) {
                p.sendMessage(FamiUtils.format("&cThis field is not multi-line!"));
                return;
            }
            p.closeInventory();
            p.sendMessage(FamiUtils.format("&7Type the new line in chat. Use 'cancel' to cancel."));
            playerMenu.setPendingAction(input -> {
                if (input.equalsIgnoreCase("cancel")) {
                    p.sendMessage(FamiUtils.format("&cCancelled adding new line."));
                } else {
                    lines.add(input);
                    languageField.setLines(lines);
                    // Persist
                    LanguageFieldsManager.setLanguageFieldValue(languageField, languageField.getValue());
                    p.sendMessage(FamiUtils.format("&aNew line added!"));
                }
                reopenEditorMenu(p);
            });
            return;
        }

        // If user clicked on one of the lines (they start at slot=0)
        if (clickedSlot < lines.size()) {
            String oldLine = lines.get(clickedSlot);
            p.closeInventory();
            p.sendMessage(FamiUtils.format("&7Editing line: &c" + oldLine));
            p.sendMessage(FamiUtils.format("&7Type the new text in chat, or 'cancel' to cancel."));
            playerMenu.setPendingAction(input -> {
                if (input.equalsIgnoreCase("cancel")) {
                    p.sendMessage(FamiUtils.format("&cCancelled editing line."));
                } else {
                    lines.set(clickedSlot, input);
                    languageField.setLines(lines);
                    LanguageFieldsManager.setLanguageFieldValue(languageField, languageField.getValue());
                    p.sendMessage(FamiUtils.format("&aLine updated!"));
                }
                reopenEditorMenu(p);
            });
        }
    }

    private void reopenEditorMenu(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                new LanguageFieldEditorMenu(playerMenu, languageField, previousMenu).open();
            }
        }.runTaskLater(RPUniverse.getInstance(), 2L);
    }

    @Override
    public void setMenuItems() {
        // Fill entire inventory
        setFillerGlass();
        Inventory inv = getInventory();

        List<String> lines = languageField.getSplitLines();
        boolean multiLine = languageField.isMultiLine();

        // Place each line in the inventory
        for (int i = 0; i < lines.size() && i < inv.getSize(); i++) {
            String line = lines.get(i);
            inv.setItem(i, FamiUtils.makeItem(
                    Material.PAPER,
                    "&e" + line,
                    "&7Click to edit this line."
            ));
        }

        // Show placeholders in bottom row
        List<String> placeholders = languageField.getPlaceholders();
        if (!placeholders.isEmpty()) {
            int startIndex = 45; // or wherever you'd like
            for (int i = 0; i < placeholders.size() && (startIndex + i) < inv.getSize(); i++) {
                String ph = placeholders.get(i);
                inv.setItem(startIndex + i, FamiUtils.makeItem(
                        Material.NAME_TAG,
                        "&bPlaceholder",
                        "&7" + ph
                ));
            }
        }

        // If multiLine, add "Add new line" button
        if (multiLine) {
            inv.setItem(51, FamiUtils.makeItem(Material.EMERALD_BLOCK, "&aAdd New Line", "&7Click to add a new line."));
        }

        // Add "Back" item
        if (previousMenu != null) {
            inv.setItem(52, FamiUtils.makeItem(Material.ARROW, "&cBack", "&7Click to go back."));
        }

        // Add "Close" item
        inv.setItem(53, FamiUtils.makeItem(Material.BARRIER, "&cClose", "&7Click to close this editor."));
    }
}
