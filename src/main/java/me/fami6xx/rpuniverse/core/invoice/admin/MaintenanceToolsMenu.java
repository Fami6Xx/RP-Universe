package me.fami6xx.rpuniverse.core.invoice.admin;

import me.fami6xx.rpuniverse.core.invoice.InvoiceManager;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menu for maintenance tools.
 * <p>
 * This menu provides administrators with tools for system maintenance,
 * including data backup, restoration, and cleanup.
 */
public class MaintenanceToolsMenu extends Menu {

    private final InvoiceManager manager;
    private final InvoiceLanguage lang;

    /**
     * Creates a new MaintenanceToolsMenu.
     *
     * @param menu    The PlayerMenu instance
     * @param manager The InvoiceManager instance
     */
    public MaintenanceToolsMenu(PlayerMenu menu, InvoiceManager manager) {
        super(menu);
        this.manager = manager;
        this.lang = InvoiceLanguage.getInstance();
    }

    @Override
    public int getSlots() {
        return 54; // Standard inventory size
    }

    /**
     * Handles clicks in the menu.
     *
     * @param e The inventory click event
     */
    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        // Handle back button
        if (slot == 49) {
            // Return to admin menu
            try {
                new AdminInvoiceMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin returned to main admin menu from maintenance tools");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening admin menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
            return;
        }

        // Handle data backup button
        if (slot == 20) {
            if (player.hasPermission("rpu.invoices.admin.maintenance")) {
                String backupFileName = manager.createDataBackup(player);
                if (backupFileName != null) {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminDataBackupCreatedMessage));
                    player.sendMessage(FamiUtils.formatWithPrefix("&7Backup file: &f" + backupFileName));
                } else {
                    player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorBackupFailedMessage));
                }
            } else {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
            }
            return;
        }

        // Handle data restore button
        if (slot == 22) {
            if (player.hasPermission("rpu.invoices.admin.maintenance")) {
                // In a real implementation, we would show a list of backup files to choose from
                // For now, we'll just show a message
                player.sendMessage(FamiUtils.formatWithPrefix("&aTo restore from a backup, use the command:"));
                player.sendMessage(FamiUtils.formatWithPrefix("&7/invoices admin maintenance restore <filename>"));
                player.closeInventory();
            } else {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
            }
            return;
        }

        // Handle clear old invoices button
        if (slot == 24) {
            if (player.hasPermission("rpu.invoices.admin.maintenance")) {
                // In a real implementation, we would show a confirmation dialog
                // For now, we'll just show a message
                player.sendMessage(FamiUtils.formatWithPrefix("&aTo clear old invoices, use the command:"));
                player.sendMessage(FamiUtils.formatWithPrefix("&7/invoices admin maintenance clear <days>"));
                player.closeInventory();
            } else {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
            }
            return;
        }

        // Handle notification system button
        if (slot == 31) {
            if (player.hasPermission("rpu.invoices.admin.maintenance")) {
                player.sendMessage(FamiUtils.formatWithPrefix("&aNotification system settings are not yet implemented."));
            } else {
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorNoPermissionMessage));
            }
            return;
        }
    }

    /**
     * Gets the name of the menu.
     *
     * @return The menu name
     */
    @Override
    public String getMenuName() {
        return FamiUtils.format(lang.adminMaintenanceToolsTitle);
    }

    /**
     * Sets up the items in the menu.
     */
    @Override
    public void setMenuItems() {
        setFillerGlass(); // Fill the menu with glass panes
        // Add data backup button
        ItemStack dataBackupButton = new ItemStack(Material.BOOK);
        ItemMeta dataBackupMeta = dataBackupButton.getItemMeta();
        dataBackupMeta.setDisplayName(FamiUtils.format(lang.adminDataBackupButtonName));
        List<String> dataBackupLore = new ArrayList<>();
        dataBackupLore.add(FamiUtils.format(lang.adminDataBackupButtonDescription));
        dataBackupLore.add("");
        dataBackupLore.add(FamiUtils.format("&eClick to create a backup"));
        dataBackupMeta.setLore(dataBackupLore);
        dataBackupButton.setItemMeta(dataBackupMeta);
        inventory.setItem(20, dataBackupButton);

        // Add data restore button
        ItemStack dataRestoreButton = new ItemStack(Material.CHEST);
        ItemMeta dataRestoreMeta = dataRestoreButton.getItemMeta();
        dataRestoreMeta.setDisplayName(FamiUtils.format(lang.adminDataRestoreButtonName));
        List<String> dataRestoreLore = new ArrayList<>();
        dataRestoreLore.add(FamiUtils.format(lang.adminDataRestoreButtonDescription));
        dataRestoreLore.add("");
        dataRestoreLore.add(FamiUtils.format("&eClick to view restore options"));
        dataRestoreMeta.setLore(dataRestoreLore);
        dataRestoreButton.setItemMeta(dataRestoreMeta);
        inventory.setItem(22, dataRestoreButton);

        // Add clear old invoices button
        ItemStack clearOldInvoicesButton = new ItemStack(Material.BARRIER);
        ItemMeta clearOldInvoicesMeta = clearOldInvoicesButton.getItemMeta();
        clearOldInvoicesMeta.setDisplayName(FamiUtils.format(lang.adminClearOldInvoicesButtonName));
        List<String> clearOldInvoicesLore = new ArrayList<>();
        clearOldInvoicesLore.add(FamiUtils.format(lang.adminClearOldInvoicesButtonDescription));
        clearOldInvoicesLore.add("");
        clearOldInvoicesLore.add(FamiUtils.format("&eClick to view cleanup options"));
        clearOldInvoicesMeta.setLore(clearOldInvoicesLore);
        clearOldInvoicesButton.setItemMeta(clearOldInvoicesMeta);
        inventory.setItem(24, clearOldInvoicesButton);

        // Add notification system button
        ItemStack notificationSystemButton = new ItemStack(Material.BELL);
        ItemMeta notificationSystemMeta = notificationSystemButton.getItemMeta();
        notificationSystemMeta.setDisplayName(FamiUtils.format(lang.adminNotificationSystemButtonName));
        List<String> notificationSystemLore = new ArrayList<>();
        notificationSystemLore.add(FamiUtils.format(lang.adminNotificationSystemButtonDescription));
        notificationSystemLore.add("");
        notificationSystemLore.add(FamiUtils.format("&eClick to configure notifications"));
        notificationSystemLore.add(FamiUtils.format("&7(Not yet implemented)"));
        notificationSystemMeta.setLore(notificationSystemLore);
        notificationSystemButton.setItemMeta(notificationSystemMeta);
        inventory.setItem(31, notificationSystemButton);

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
        return Arrays.asList(MenuTag.ADMIN);
    }
}
