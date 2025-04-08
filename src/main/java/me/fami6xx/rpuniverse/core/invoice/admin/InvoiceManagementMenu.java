package me.fami6xx.rpuniverse.core.invoice.admin;

import me.fami6xx.rpuniverse.core.invoice.Invoice;
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

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Menu for managing all invoices in the system.
 * <p>
 * This menu allows administrators to view, edit, delete, restore, and force payment
 * for any invoice in the system. It includes filtering and sorting options.
 */
public class InvoiceManagementMenu extends EasyPaginatedMenu {

    private final InvoiceManager manager;
    private final InvoiceLanguage lang;
    private List<Invoice> invoices;
    private SortType sortType = SortType.DATE_DESC;
    private FilterType filterType = FilterType.ALL;
    private String filterValue = null;

    /**
     * Sort types for invoices.
     */
    public enum SortType {
        DATE_ASC,
        DATE_DESC,
        AMOUNT_ASC,
        AMOUNT_DESC,
        STATUS,
        CREATOR,
        RECIPIENT
    }

    /**
     * Filter types for invoices.
     */
    public enum FilterType {
        ALL,
        STATUS,
        JOB,
        PLAYER,
        DATE_RANGE,
        AMOUNT_RANGE
    }

    /**
     * Creates a new InvoiceManagementMenu.
     *
     * @param menu    The PlayerMenu instance
     * @param manager The InvoiceManager instance
     */
    public InvoiceManagementMenu(PlayerMenu menu, InvoiceManager manager) {
        super(menu);
        this.manager = manager;
        this.lang = InvoiceLanguage.getInstance();
        this.invoices = getFilteredInvoices();
    }

    /**
     * Gets the filtered and sorted list of invoices.
     *
     * @return The filtered and sorted list of invoices
     */
    private List<Invoice> getFilteredInvoices() {
        List<Invoice> result = new ArrayList<>(manager.getAllInvoices());

        // Apply filters
        if (filterType != FilterType.ALL && filterValue != null) {
            switch (filterType) {
                case STATUS:
                    try {
                        Invoice.Status status = Invoice.Status.valueOf(filterValue);
                        result = manager.getInvoicesByStatus(status);
                    } catch (IllegalArgumentException e) {
                        ErrorHandler.debug("Invalid status filter value: " + filterValue);
                    }
                    break;
                case JOB:
                    result = manager.getInvoicesByJob(filterValue);
                    break;
                case PLAYER:
                    try {
                        UUID playerUUID = UUID.fromString(filterValue);
                        List<Invoice> created = manager.getInvoicesByCreator(playerUUID);
                        List<Invoice> received = manager.getInvoicesByTarget(playerUUID);
                        result = new ArrayList<>();
                        result.addAll(created);
                        result.addAll(received);
                    } catch (IllegalArgumentException e) {
                        ErrorHandler.debug("Invalid player UUID filter value: " + filterValue);
                    }
                    break;
                // Date range and amount range filters would require more complex logic
                // and are not implemented in this basic version
            }
        }

        // Apply sorting
        switch (sortType) {
            case DATE_ASC:
                result.sort(Comparator.comparing(Invoice::getCreationDate));
                break;
            case DATE_DESC:
                result.sort(Comparator.comparing(Invoice::getCreationDate).reversed());
                break;
            case AMOUNT_ASC:
                result.sort(Comparator.comparing(Invoice::getAmount));
                break;
            case AMOUNT_DESC:
                result.sort(Comparator.comparing(Invoice::getAmount).reversed());
                break;
            case STATUS:
                result.sort(Comparator.comparing(Invoice::getStatus));
                break;
            case CREATOR:
                result.sort(Comparator.comparing(invoice -> invoice.getCreator().toString()));
                break;
            case RECIPIENT:
                result.sort(Comparator.comparing(invoice -> invoice.getTarget().toString()));
                break;
        }

        return result;
    }

    /**
     * Gets the item stack for an invoice.
     *
     * @param invoice The invoice
     * @return The item stack representing the invoice
     */
    private ItemStack getInvoiceItem(Invoice invoice) {
        Material material;
        String statusColor;

        // Determine the material and status color based on the invoice status
        if (invoice.isPending()) {
            material = Material.PAPER;
            statusColor = "&e";
        } else if (invoice.isPaid()) {
            material = Material.EMERALD;
            statusColor = "&a";
        } else if (invoice.isDeleted()) {
            material = Material.BARRIER;
            statusColor = "&c";
        } else {
            material = Material.BARRIER;
            statusColor = "&7";
        }

        // Create the item
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(FamiUtils.format("&6Invoice #" + invoice.getId()));

        // Format the creation date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = dateFormat.format(invoice.getCreationDate());

        // Create the lore with invoice details
        List<String> lore = new ArrayList<>();
        lore.add(FamiUtils.format("&7Job: &f" + invoice.getJobName()));
        lore.add(FamiUtils.format("&7From: &f" + (invoice.getCreatorPlayer() != null ? invoice.getCreatorPlayer().getName() : "Unknown")));
        lore.add(FamiUtils.format("&7To: &f" + (invoice.getTargetPlayer() != null ? invoice.getTargetPlayer().getName() : "Unknown")));
        lore.add(FamiUtils.format("&7Amount: &6" + invoice.getAmount() + manager.getModule().getDefaultCurrency()));
        lore.add(FamiUtils.format("&7Date: &f" + formattedDate));
        lore.add(FamiUtils.format("&7Status: " + statusColor + invoice.getStatus().name()));
        lore.add("");

        // Add action hints based on the invoice status
        if (invoice.isPending()) {
            lore.add(FamiUtils.format("&aClick to force pay"));
            lore.add(FamiUtils.format("&cShift-click to delete"));
        } else if (invoice.isPaid()) {
            lore.add(FamiUtils.format("&7This invoice has been paid"));
        } else if (invoice.isDeleted()) {
            lore.add(FamiUtils.format("&aClick to restore"));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Gets the item stack for a specific index in the paginated menu.
     *
     * @param index The index of the item in the collection
     * @return The item stack for the invoice at the specified index
     */
    @Override
    public ItemStack getItemFromIndex(int index) {
        if (index >= invoices.size()) {
            return null;
        }

        Invoice invoice = invoices.get(index);
        return getInvoiceItem(invoice);
    }

    /**
     * Gets the total number of invoices in the filtered collection.
     *
     * @return The number of invoices in the collection
     */
    @Override
    public int getCollectionSize() {
        return invoices.size();
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
        int index = getSlotIndex(slot);

        // Handle sort and filter buttons
        if (slot == 46) {
            // Sort by date
            if (sortType == SortType.DATE_DESC) {
                sortType = SortType.DATE_ASC;
            } else {
                sortType = SortType.DATE_DESC;
            }
            invoices = getFilteredInvoices();
            super.open();
            return;
        } else if (slot == 47) {
            // Sort by amount
            if (sortType == SortType.AMOUNT_DESC) {
                sortType = SortType.AMOUNT_ASC;
            } else {
                sortType = SortType.AMOUNT_DESC;
            }
            invoices = getFilteredInvoices();
            super.open();
            return;
        } else if (slot == 48) {
            // Sort by status
            sortType = SortType.STATUS;
            invoices = getFilteredInvoices();
            super.open();
            return;
        } else if (slot == 49) {
            // Sort by creator
            sortType = SortType.CREATOR;
            invoices = getFilteredInvoices();
            super.open();
            return;
        } else if (slot == 50) {
            // Sort by recipient
            sortType = SortType.RECIPIENT;
            invoices = getFilteredInvoices();
            super.open();
            return;
        } else if (slot == 51) {
            // Filter by status (cycle through statuses)
            if (filterType == FilterType.STATUS) {
                if (filterValue.equals(Invoice.Status.PENDING.name())) {
                    filterValue = Invoice.Status.PAID.name();
                } else if (filterValue.equals(Invoice.Status.PAID.name())) {
                    filterValue = Invoice.Status.DELETED.name();
                } else {
                    filterType = FilterType.ALL;
                    filterValue = null;
                }
            } else {
                filterType = FilterType.STATUS;
                filterValue = Invoice.Status.PENDING.name();
            }
            invoices = getFilteredInvoices();
            super.open();
            return;
        } else if (slot == 52) {
            // Return to admin menu
            try {
                new AdminInvoiceMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin returned to main admin menu");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening admin menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
            return;
        }

        // Handle invoice actions
        if (index >= 0 && index < invoices.size()) {
            Invoice invoice = invoices.get(index);

            if (e.isShiftClick()) {
                // Delete invoice
                if (invoice.isPending() && player.hasPermission("rpu.invoices.admin.delete")) {
                    if (manager.deleteInvoice(invoice, player)) {
                        player.sendMessage(FamiUtils.formatWithPrefix(lang.adminInvoiceDeletedMessage.replace("{id}", invoice.getId())));
                        manager.logAdminAction(player, "delete", invoice.getId());
                        invoices = getFilteredInvoices();
                        super.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix(lang.errorDeletingInvoiceMessage));
                    }
                }
            } else {
                // Force pay or restore invoice
                if (invoice.isPending() && player.hasPermission("rpu.invoices.admin.pay")) {
                    // Force pay
                    if (manager.forcePayInvoice(invoice, player)) {
                        player.sendMessage(FamiUtils.formatWithPrefix(lang.adminInvoiceForcePaidMessage.replace("{id}", invoice.getId())));
                        invoices = getFilteredInvoices();
                        super.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorCannotForcePayPaidInvoiceMessage));
                    }
                } else if (invoice.isDeleted() && player.hasPermission("rpu.invoices.admin.restore")) {
                    // Restore
                    if (manager.restoreInvoice(invoice, player)) {
                        player.sendMessage(FamiUtils.formatWithPrefix(lang.adminInvoiceRestoredMessage.replace("{id}", invoice.getId())));
                        invoices = getFilteredInvoices();
                        super.open();
                    } else {
                        player.sendMessage(FamiUtils.formatWithPrefix(lang.adminErrorCannotRestoreNonDeletedInvoiceMessage));
                    }
                }
            }
        }
    }

    /**
     * Gets the name of the menu.
     *
     * @return The menu name
     */
    @Override
    public String getMenuName() {
        return FamiUtils.format(lang.adminInvoiceManagementTitle);
    }

    /**
     * Adds additional items to the menu.
     */
    @Override
    public void addAdditionalItems() {
        // Add sort buttons
        ItemStack sortByDateButton = new ItemStack(Material.CLOCK);
        ItemMeta sortByDateMeta = sortByDateButton.getItemMeta();
        sortByDateMeta.setDisplayName(FamiUtils.format(lang.adminSortByDateButtonName));
        List<String> sortByDateLore = new ArrayList<>();
        sortByDateLore.add(FamiUtils.format("&7Current: " + (sortType == SortType.DATE_ASC ? "Oldest first" : "Newest first")));
        sortByDateLore.add(FamiUtils.format("&eClick to toggle"));
        sortByDateMeta.setLore(sortByDateLore);
        sortByDateButton.setItemMeta(sortByDateMeta);
        inventory.setItem(46, sortByDateButton);

        ItemStack sortByAmountButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta sortByAmountMeta = sortByAmountButton.getItemMeta();
        sortByAmountMeta.setDisplayName(FamiUtils.format(lang.adminSortByAmountButtonName));
        List<String> sortByAmountLore = new ArrayList<>();
        sortByAmountLore.add(FamiUtils.format("&7Current: " + (sortType == SortType.AMOUNT_ASC ? "Lowest first" : "Highest first")));
        sortByAmountLore.add(FamiUtils.format("&eClick to toggle"));
        sortByAmountMeta.setLore(sortByAmountLore);
        sortByAmountButton.setItemMeta(sortByAmountMeta);
        inventory.setItem(47, sortByAmountButton);

        ItemStack sortByStatusButton = new ItemStack(Material.PAPER);
        ItemMeta sortByStatusMeta = sortByStatusButton.getItemMeta();
        sortByStatusMeta.setDisplayName(FamiUtils.format(lang.adminSortByStatusButtonName));
        List<String> sortByStatusLore = new ArrayList<>();
        sortByStatusLore.add(FamiUtils.format("&eClick to sort by status"));
        sortByStatusMeta.setLore(sortByStatusLore);
        sortByStatusButton.setItemMeta(sortByStatusMeta);
        inventory.setItem(48, sortByStatusButton);

        ItemStack sortByCreatorButton = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta sortByCreatorMeta = sortByCreatorButton.getItemMeta();
        sortByCreatorMeta.setDisplayName(FamiUtils.format(lang.adminSortByCreatorButtonName));
        List<String> sortByCreatorLore = new ArrayList<>();
        sortByCreatorLore.add(FamiUtils.format("&eClick to sort by creator"));
        sortByCreatorMeta.setLore(sortByCreatorLore);
        sortByCreatorButton.setItemMeta(sortByCreatorMeta);
        inventory.setItem(49, sortByCreatorButton);

        ItemStack sortByRecipientButton = new ItemStack(Material.CHEST);
        ItemMeta sortByRecipientMeta = sortByRecipientButton.getItemMeta();
        sortByRecipientMeta.setDisplayName(FamiUtils.format(lang.adminSortByRecipientButtonName));
        List<String> sortByRecipientLore = new ArrayList<>();
        sortByRecipientLore.add(FamiUtils.format("&eClick to sort by recipient"));
        sortByRecipientMeta.setLore(sortByRecipientLore);
        sortByRecipientButton.setItemMeta(sortByRecipientMeta);
        inventory.setItem(50, sortByRecipientButton);

        // Add filter button
        ItemStack filterByStatusButton = new ItemStack(Material.HOPPER);
        ItemMeta filterByStatusMeta = filterByStatusButton.getItemMeta();
        filterByStatusMeta.setDisplayName(FamiUtils.format(lang.adminFilterByStatusButtonName));
        List<String> filterByStatusLore = new ArrayList<>();
        if (filterType == FilterType.STATUS) {
            filterByStatusLore.add(FamiUtils.format("&7Current: " + filterValue));
        } else {
            filterByStatusLore.add(FamiUtils.format("&7Current: All"));
        }
        filterByStatusLore.add(FamiUtils.format("&eClick to cycle through statuses"));
        filterByStatusMeta.setLore(filterByStatusLore);
        filterByStatusButton.setItemMeta(filterByStatusMeta);
        inventory.setItem(51, filterByStatusButton);

        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(FamiUtils.format("&aBack to Admin Menu"));
        backMeta.setLore(Arrays.asList(FamiUtils.format("&7Return to the main admin menu")));
        backButton.setItemMeta(backMeta);
        inventory.setItem(52, backButton);
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