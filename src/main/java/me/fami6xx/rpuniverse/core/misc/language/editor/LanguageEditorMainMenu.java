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
 * Only some will be multi-line (the ones whose default contained '~').
 */
public class LanguageEditorMainMenu extends EasyPaginatedMenu {

    private final List<LanguageField> allFields;

    public LanguageEditorMainMenu(PlayerMenu menu) {
        super(menu);
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

        // We'll show the lines if multiLine, otherwise single line
        List<String> lines = new ArrayList<>(lf.getSplitLines());; // If multiLine==false, this is always 1 line
        if (lines.isEmpty()) {
            lines.add("&7(No value)");
        }

        // Show placeholders if any
        if (!lf.getPlaceholders().isEmpty()) {
            lines.add("&7 ");
            lines.add("&7Placeholders:");
            for (String ph : lf.getPlaceholders()) {
                lines.add("&8- &f" + ph);
            }
        }

        return FamiUtils.makeItem(
                Material.PAPER,
                "&e" + lf.getFieldName(),
                lines.toArray(new String[0])
        );
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;
        int idx = getSlotIndex(e.getSlot());
        idx = idx + (page * 28); // Adjust for page
        if (idx < 0 || idx >= allFields.size()) return;

        LanguageField lf = allFields.get(idx);
        new LanguageFieldEditorMenu(playerMenu, lf, this).open();
    }

    @Override
    public void addAdditionalItems() {}
}
