package me.fami6xx.rpuniverse.core.misc.language.editor;

import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.language.LanguageFieldsManager;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows a paginated list of all LanguageFields.
 * Both core language fields and addon translations are included.
 */
public class LanguageEditorMainMenu extends EasyPaginatedMenu {

    private final List<LanguageField> allFields;

    public LanguageEditorMainMenu(PlayerMenu menu) {
        super(menu);
        // Get the combined list of language fields (core + addon)
        this.allFields = LanguageFieldsManager.getAllLanguageFields();
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&6Language Editor");
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }

    @Override
    public int getCollectionSize() {
        return allFields.size();
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        LanguageField lf = allFields.get(index);

        // We'll show the lines if multiLine, otherwise single line.
        List<String> lines = new ArrayList<>(lf.getSplitLines());
        if (lines.isEmpty()) {
            lines.add("&7(No value)");
        }

        // Append placeholders if any.
        if (!lf.getPlaceholders().isEmpty()) {
            lines.add("&7 ");
            lines.add("&7Placeholders:");
            for (String ph : lf.getPlaceholders()) {
                lines.add("&8- &f" + ph);
            }
        }
        // Add header info.
        lines.add(0, "&7 ");
        lines.add(1, "&7Current text:");
        // Optionally indicate the type of field.
        String type = (lf.getReflectionField() == null) ? "&7[Addon]" : "&7[Core]";

        String name = lf.getFieldName();
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf(".") + 1);
        }

        return FamiUtils.makeItem(
                Material.PAPER,
                "&e" + name + " " + type,
                lines.toArray(new String[0])
        );
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;

        // The slot the user clicked
        int clickedSlot = e.getSlot();

        // Exactly the same border slots used in setMenuItems()
        Integer[] borderSlots = {
                0,1,2,3,4,5,6,7,8,9,
                17,18,
                26,27,
                35,36,
                44,45,46,47,48,49,50,51,52,53
        };
        List<Integer> borderSlotsList = new ArrayList<>(List.of(borderSlots));

        // If they clicked a border slot, ignore.
        if (borderSlotsList.contains(clickedSlot)) {
            return;
        }

        // Build the “non‐border” slot list in the exact order items are placed.
        List<Integer> itemSlots = new ArrayList<>();
        int slotPointer = 10;  // Start from slot 10.
        for (int i = 0; i < getMaxItemsPerPage() && slotPointer < 54; i++) {
            // Skip border slots.
            while (borderSlotsList.contains(slotPointer)) {
                slotPointer++;
                if (slotPointer >= 54) {
                    break;
                }
            }
            if (slotPointer >= 54) {
                break;
            }
            itemSlots.add(slotPointer);
            slotPointer++;
        }

        // Figure out which item index on this page was clicked.
        int relativeIndex = itemSlots.indexOf(clickedSlot);
        if (relativeIndex == -1) {
            return;
        }

        // Final index in the full collection.
        int idx = (page * getMaxItemsPerPage()) + relativeIndex;
        if (idx < 0 || idx >= allFields.size()) {
            return;
        }

        // Open the editor for the selected field.
        LanguageField lf = allFields.get(idx);
        new LanguageFieldEditorMenu(playerMenu, lf, this).open();
    }

    @Override
    public void addAdditionalItems() {}
}
