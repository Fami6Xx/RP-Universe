package me.fami6xx.rpuniverse.core.invoice.admin;

import me.fami6xx.rpuniverse.core.invoice.InvoiceManager;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menu for configuring invoice system settings.
 * <p>
 * This menu allows administrators to configure which jobs can create invoices
 * and other system-wide settings.
 */
public class SystemSettingsMenu extends EasyPaginatedMenu {

    private final InvoiceManager manager;
    private final InvoiceLanguage lang;

    /**
     * Creates a new SystemSettingsMenu.
     *
     * @param menu    The PlayerMenu instance
     * @param manager The InvoiceManager instance
     */
    public SystemSettingsMenu(PlayerMenu menu, InvoiceManager manager) {
        super(menu);
        this.manager = manager;
        this.lang = InvoiceLanguage.getInstance();
    }

    /**
     * Gets the item stack for a specific index in the paginated menu.
     * This menu doesn't use pagination for items.
     *
     * @param index The index of the item in the collection
     * @return Always returns null as this menu doesn't use pagination
     */
    @Override
    public ItemStack getItemFromIndex(int index) {
        return null;
    }

    /**
     * Gets the total number of items in the collection.
     * This menu doesn't use pagination for items.
     *
     * @return Always returns 0 as this menu doesn't use pagination
     */
    @Override
    public int getCollectionSize() {
        return 0;
    }

    /**
     * Handles clicks in the paginated menu.
     *
     * @param e The inventory click event
     */
    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        // Handle back button
        if (slot == 49) {
            // Return to admin menu
            try {
                new AdminInvoiceMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin returned to main admin menu from system settings");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening admin menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
            return;
        }

        // Handle job integration settings button
        if (slot == 22) {
            player.sendMessage(FamiUtils.formatWithPrefix("&aJob integration settings are not yet implemented."));
            player.sendMessage(FamiUtils.formatWithPrefix("&7This would allow configuring which jobs can create invoices."));
        }
    }

    /**
     * Gets the name of the menu.
     *
     * @return The menu name
     */
    @Override
    public String getMenuName() {
        return FamiUtils.format(lang.adminSystemSettingsTitle);
    }

    /**
     * Adds additional items to the menu.
     */
    @Override
    public void addAdditionalItems() {
        // Add job integration settings button
        ItemStack jobIntegrationButton = new ItemStack(Material.NAME_TAG);
        ItemMeta jobIntegrationMeta = jobIntegrationButton.getItemMeta();
        jobIntegrationMeta.setDisplayName(FamiUtils.format(lang.adminJobIntegrationSettingsButtonName));
        List<String> jobIntegrationLore = new ArrayList<>();
        jobIntegrationLore.add(FamiUtils.format(lang.adminJobIntegrationSettingsButtonDescription));
        jobIntegrationLore.add("");
        jobIntegrationLore.add(FamiUtils.format("&7Click to configure which jobs can create invoices"));
        jobIntegrationLore.add(FamiUtils.format("&7(Not yet implemented)"));
        jobIntegrationMeta.setLore(jobIntegrationLore);
        jobIntegrationButton.setItemMeta(jobIntegrationMeta);
        inventory.setItem(22, jobIntegrationButton);

        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(FamiUtils.format("&aBack to Admin Menu"));
        backMeta.setLore(Arrays.asList(FamiUtils.format("&7Return to the main admin menu")));
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
    }

    /**
     * Gets the menu tags for this menu.
     *
     * @return A list of menu tags
     */
    @Override
    public List<MenuTag> getMenuTags() {
        return Arrays.asList(MenuTag.ADMIN, MenuTag.JOB);
    }
}
