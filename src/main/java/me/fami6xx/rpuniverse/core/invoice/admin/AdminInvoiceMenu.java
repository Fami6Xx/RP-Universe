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
 * The main admin menu for the invoice system.
 * <p>
 * This menu provides access to the various admin tools for managing invoices,
 * configuring system settings, and performing maintenance tasks.
 */
public class AdminInvoiceMenu extends EasyPaginatedMenu {

    private final InvoiceManager manager;
    private final InvoiceLanguage lang;

    /**
     * Creates a new AdminInvoiceMenu.
     *
     * @param menu    The PlayerMenu instance
     * @param manager The InvoiceManager instance
     */
    public AdminInvoiceMenu(PlayerMenu menu, InvoiceManager manager) {
        super(menu);
        this.manager = manager;
        this.lang = InvoiceLanguage.getInstance();
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        // This menu doesn't use pagination for items
        return null;
    }

    @Override
    public int getCollectionSize() {
        // This menu doesn't use pagination for items
        return 0;
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Get the display name of the clicked item
        String displayName = clickedItem.getItemMeta().getDisplayName();

        // Handle menu navigation based on the clicked item
        if (displayName.contains(FamiUtils.format(lang.adminInvoiceManagementButtonName))) {
            // Open the invoice management menu
            try {
                new InvoiceManagementMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin opened invoice management menu");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening invoice management menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
        } else if (displayName.contains(FamiUtils.format(lang.adminSystemSettingsButtonName))) {
            // Open the system settings menu
            try {
                new SystemSettingsMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin opened system settings menu");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening system settings menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
        } else if (displayName.contains(FamiUtils.format(lang.adminMaintenanceToolsButtonName))) {
            // Open the maintenance tools menu
            try {
                new MaintenanceToolsMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin opened maintenance tools menu");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening maintenance tools menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
        }
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(lang.adminMenuTitle);
    }

    @Override
    public void addAdditionalItems() {
        // Add the invoice management button
        ItemStack invoiceManagementButton = new ItemStack(Material.BOOK);
        ItemMeta invoiceManagementMeta = invoiceManagementButton.getItemMeta();
        invoiceManagementMeta.setDisplayName(FamiUtils.format(lang.adminInvoiceManagementButtonName));
        List<String> invoiceManagementLore = new ArrayList<>();
        invoiceManagementLore.add(FamiUtils.format(lang.adminInvoiceManagementButtonDescription));
        invoiceManagementMeta.setLore(invoiceManagementLore);
        invoiceManagementButton.setItemMeta(invoiceManagementMeta);
        inventory.setItem(20, invoiceManagementButton);

        // Add the system settings button
        ItemStack systemSettingsButton = new ItemStack(Material.COMPARATOR);
        ItemMeta systemSettingsMeta = systemSettingsButton.getItemMeta();
        systemSettingsMeta.setDisplayName(FamiUtils.format(lang.adminSystemSettingsButtonName));
        List<String> systemSettingsLore = new ArrayList<>();
        systemSettingsLore.add(FamiUtils.format(lang.adminSystemSettingsButtonDescription));
        systemSettingsMeta.setLore(systemSettingsLore);
        systemSettingsButton.setItemMeta(systemSettingsMeta);
        inventory.setItem(22, systemSettingsButton);

        // Add the maintenance tools button
        ItemStack maintenanceToolsButton = new ItemStack(Material.ANVIL);
        ItemMeta maintenanceToolsMeta = maintenanceToolsButton.getItemMeta();
        maintenanceToolsMeta.setDisplayName(FamiUtils.format(lang.adminMaintenanceToolsButtonName));
        List<String> maintenanceToolsLore = new ArrayList<>();
        maintenanceToolsLore.add(FamiUtils.format(lang.adminMaintenanceToolsButtonDescription));
        maintenanceToolsMeta.setLore(maintenanceToolsLore);
        maintenanceToolsButton.setItemMeta(maintenanceToolsMeta);
        inventory.setItem(24, maintenanceToolsButton);
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return Arrays.asList(MenuTag.ADMIN);
    }
}
