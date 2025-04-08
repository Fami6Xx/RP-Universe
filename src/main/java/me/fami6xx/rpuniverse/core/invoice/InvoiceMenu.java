package me.fami6xx.rpuniverse.core.invoice;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Menu for viewing and managing invoices.
 * <p>
 * This menu allows players to view their invoices, filter them by different criteria,
 * and perform actions like paying or deleting invoices.
 */
public class InvoiceMenu extends EasyPaginatedMenu {

    /**
     * The filter mode for the invoice menu.
     */
    public enum FilterMode {
        /**
         * Show invoices received by the player.
         */
        RECEIVED,

        /**
         * Show invoices created by the player.
         */
        CREATED,

        /**
         * Show all invoices for a specific job (for job bosses).
         */
        JOB
    }

    private final InvoiceManager manager;
    private FilterMode filterMode;
    private List<Invoice> invoices;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Creates a new invoice menu.
     *
     * @param menu The player menu
     * @param manager The invoice manager
     * @param filterMode The initial filter mode
     */
    public InvoiceMenu(PlayerMenu menu, InvoiceManager manager, FilterMode filterMode) {
        super(menu);
        this.manager = manager;
        this.filterMode = filterMode;
        this.invoices = getFilteredInvoices();
        menu.setCurrentMenu(this);
    }

    /**
     * Gets the filtered list of invoices based on the current filter mode.
     *
     * @return The filtered list of invoices
     */
    private List<Invoice> getFilteredInvoices() {
        Player player = playerMenu.getPlayer();
        UUID playerId = player.getUniqueId();

        switch (filterMode) {
            case RECEIVED:
                return manager.getInvoicesByTarget(playerId);
            case CREATED:
                return manager.getInvoicesByCreator(playerId);
            case JOB:
                // Check if player is a job boss
                if (player.hasPermission("rpu.invoices.view.job") && playerMenu.getEditingJob() != null) {
                    return manager.getInvoicesByJob(playerMenu.getEditingJob().getName());
                } else {
                    // Fallback to RECEIVED if player doesn't have permission
                    filterMode = FilterMode.RECEIVED;
                    return manager.getInvoicesByTarget(playerId);
                }
            default:
                return new ArrayList<>();
        }
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
        InvoiceLanguage lang = InvoiceLanguage.getInstance();

        // Set material and color based on status
        if (invoice.isPending()) {
            material = Material.PAPER;
            statusColor = ChatColor.YELLOW.toString();
        } else if (invoice.isPaid()) {
            material = Material.EMERALD;
            statusColor = ChatColor.GREEN.toString();
        } else {
            material = Material.BARRIER;
            statusColor = ChatColor.RED.toString();
        }

        // Create the item
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // Set display name
        String displayName = FamiUtils.format(lang.invoiceItemTitle.replace("{id}", invoice.getId().substring(0, 8)));
        meta.setDisplayName(displayName);

        // Set lore with invoice details
        List<String> lore = new ArrayList<>();
        lore.add(FamiUtils.format(lang.invoiceItemJobLine.replace("{job}", invoice.getJob())));

        String creatorName = Bukkit.getOfflinePlayer(invoice.getCreator()).getName();
        lore.add(FamiUtils.format(lang.invoiceItemFromLine.replace("{from}", (creatorName != null ? creatorName : lang.unknownPlayerName))));

        String targetName = Bukkit.getOfflinePlayer(invoice.getTarget()).getName();
        lore.add(FamiUtils.format(lang.invoiceItemToLine.replace("{to}", (targetName != null ? targetName : lang.unknownPlayerName))));

        lore.add(FamiUtils.format(lang.invoiceItemAmountLine
                .replace("{amount}", String.valueOf(invoice.getAmount()))
                .replace("{currency}", manager.getModule().getDefaultCurrency())));

        lore.add(FamiUtils.format(lang.invoiceItemDateLine.replace("{date}", dateFormat.format(invoice.getCreationDate()))));
        lore.add(FamiUtils.format(lang.invoiceItemStatusLine
                .replace("{statusColor}", statusColor)
                .replace("{status}", invoice.getStatus().toString())));

        // Add action hints
        if (invoice.isPending()) {
            if (invoice.getTarget().equals(playerMenu.getPlayer().getUniqueId())) {
                lore.add("");
                lore.add(FamiUtils.format(lang.invoiceItemClickToPay));
            }

            if (invoice.getCreator().equals(playerMenu.getPlayer().getUniqueId()) || 
                    playerMenu.getPlayer().hasPermission("rpu.invoices.delete.job")) {
                lore.add("");
                lore.add(FamiUtils.format(lang.invoiceItemShiftClickToDelete));
            }
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Gets the item stack for the filter button.
     *
     * @param mode The filter mode
     * @param isActive Whether this mode is currently active
     * @return The item stack for the filter button
     */
    private ItemStack getFilterButton(FilterMode mode, boolean isActive) {
        Material material;
        String name;
        List<String> lore = new ArrayList<>();
        InvoiceLanguage lang = InvoiceLanguage.getInstance();

        switch (mode) {
            case RECEIVED:
                material = Material.CHEST;
                name = isActive ? lang.receivedFilterButtonName : lang.receivedFilterButtonName.replace("&a", "&7");
                lore.add(FamiUtils.format(lang.receivedFilterDescription));
                break;
            case CREATED:
                material = Material.WRITABLE_BOOK;
                name = isActive ? lang.createdFilterButtonName : lang.createdFilterButtonName.replace("&a", "&7");
                lore.add(FamiUtils.format(lang.createdFilterDescription));
                break;
            case JOB:
                material = Material.GOLD_INGOT;
                name = isActive ? lang.jobFilterButtonName : lang.jobFilterButtonName.replace("&a", "&7");
                lore.add(FamiUtils.format(lang.jobFilterDescription));
                break;
            default:
                material = Material.BARRIER;
                name = lang.unknownFilterButtonName;
                break;
        }

        if (isActive) {
            lore.add(FamiUtils.format(lang.currentlySelectedText));
        } else {
            lore.add(FamiUtils.format(lang.clickToSelectText));
        }

        return FamiUtils.makeItem(material, FamiUtils.format(name), lore.toArray(new String[0]));
    }

    /**
     * Gets the item stack for a specific index in the paginated menu.
     * <p>
     * This method retrieves the invoice at the specified index and returns
     * its corresponding item stack representation.
     *
     * @param index The index of the item in the collection
     * @return The item stack for the invoice at the specified index, or null if the index is out of bounds
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
     * Gets the total number of invoices in the current filtered collection.
     * <p>
     * This method is used by the paginated menu to determine how many pages are needed.
     *
     * @return The number of invoices in the collection
     */
    @Override
    public int getCollectionSize() {
        return invoices.size();
    }

    /**
     * Handles clicks in the paginated menu.
     * <p>
     * This method processes filter button clicks and invoice actions like payment and deletion.
     *
     * @param e The inventory click event
     */
    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        int slot = e.getSlot();

        // Handle filter buttons
        if (slot == 46) {
            // Received filter
            if (filterMode != FilterMode.RECEIVED) {
                filterMode = FilterMode.RECEIVED;
                invoices = getFilteredInvoices();
                page = 0;
                super.open();
                me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                        " changed invoice filter to RECEIVED");
            }
            return;
        } else if (slot == 47) {
            // Created filter
            if (filterMode != FilterMode.CREATED) {
                filterMode = FilterMode.CREATED;
                invoices = getFilteredInvoices();
                page = 0;
                super.open();
                me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                        " changed invoice filter to CREATED");
            }
            return;
        } else if (slot == 51 && playerMenu.getPlayer().hasPermission("rpu.invoices.view.job")) {
            // Job filter (only if player has permission)
            if (filterMode != FilterMode.JOB) {
                filterMode = FilterMode.JOB;
                invoices = getFilteredInvoices();
                page = 0;
                super.open();
                me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                        " changed invoice filter to JOB");
            }
            return;
        }

        // Handle invoice actions
        int index = getSlotIndex(slot);
        if (index != -1 && index < invoices.size()) {
            Invoice invoice = invoices.get(index);

            if (invoice.isPending()) {
                if (e.isShiftClick()) {
                    // Delete invoice
                    if (invoice.getCreator().equals(playerMenu.getPlayer().getUniqueId()) || 
                            playerMenu.getPlayer().hasPermission("rpu.invoices.delete.job")) {

                        me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                " attempting to delete invoice: ID=" + invoice.getId());
                        boolean success = manager.deleteInvoice(invoice, playerMenu.getPlayer());
                        if (success) {
                            playerMenu.getPlayer().sendMessage(FamiUtils.format(InvoiceLanguage.getInstance().invoiceDeletedMessage));
                            invoices = getFilteredInvoices();
                            super.open();
                            me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                    " successfully deleted invoice: ID=" + invoice.getId());
                        } else {
                            playerMenu.getPlayer().sendMessage(FamiUtils.format(InvoiceLanguage.getInstance().errorDeletingInvoiceMessage));
                            me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                    " failed to delete invoice: ID=" + invoice.getId());
                        }
                    } else {
                        playerMenu.getPlayer().sendMessage(FamiUtils.format(InvoiceLanguage.getInstance().errorNoPermissionToDeleteMessage));
                        me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                " has no permission to delete invoice: ID=" + invoice.getId());
                    }
                } else {
                    // Pay invoice
                    if (invoice.getTarget().equals(playerMenu.getPlayer().getUniqueId())) {
                        me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                " attempting to pay invoice: ID=" + invoice.getId() + 
                                                                                ", Amount=" + invoice.getAmount());
                        boolean success = manager.payInvoice(invoice, playerMenu.getPlayer());
                        if (success) {
                            playerMenu.getPlayer().sendMessage(FamiUtils.format(InvoiceLanguage.getInstance().invoicePaidMessage));
                            invoices = getFilteredInvoices();
                            super.open();
                            me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                    " successfully paid invoice: ID=" + invoice.getId());
                        } else {
                            playerMenu.getPlayer().sendMessage(FamiUtils.format(InvoiceLanguage.getInstance().errorPayingInvoiceMessage));
                            me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                    " failed to pay invoice: ID=" + invoice.getId());
                        }
                    } else {
                        playerMenu.getPlayer().sendMessage(FamiUtils.format(InvoiceLanguage.getInstance().errorCanOnlyPayOwnInvoicesMessage));
                        me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + playerMenu.getPlayer().getName() + 
                                                                                " attempted to pay someone else's invoice: ID=" + invoice.getId());
                    }
                }
            }
        }
    }

    /**
     * Gets the name of the menu.
     * <p>
     * This method returns the title of the menu, which includes the current filter mode.
     *
     * @return The menu name
     */
    @Override
    public String getMenuName() {
        InvoiceLanguage lang = InvoiceLanguage.getInstance();
        String filterName;

        switch (filterMode) {
            case RECEIVED:
                filterName = lang.receivedFilterName;
                break;
            case CREATED:
                filterName = lang.createdFilterName;
                break;
            case JOB:
                filterName = lang.jobFilterName;
                break;
            default:
                filterName = lang.allFilterName;
                break;
        }

        return FamiUtils.format(lang.invoicesMenuTitle.replace("{filterName}", filterName));
    }

    /**
     * Adds additional items to the menu.
     * <p>
     * This method adds filter buttons and an information button to the menu.
     */
    @Override
    public void addAdditionalItems() {
        InvoiceLanguage lang = InvoiceLanguage.getInstance();

        // Add filter buttons
        inventory.setItem(46, getFilterButton(FilterMode.RECEIVED, filterMode == FilterMode.RECEIVED));
        inventory.setItem(47, getFilterButton(FilterMode.CREATED, filterMode == FilterMode.CREATED));

        // Only show job filter if player has permission
        if (playerMenu.getPlayer().hasPermission("rpu.invoices.view.job")) {
            inventory.setItem(51, getFilterButton(FilterMode.JOB, filterMode == FilterMode.JOB));
        }

        // Add info button
        List<String> infoLore = new ArrayList<>();
        infoLore.add(FamiUtils.format(lang.invoiceInfoButtonDescription));
        infoLore.add("");
        infoLore.add(FamiUtils.format(lang.invoiceInfoButtonPayHint));
        infoLore.add(FamiUtils.format(lang.invoiceInfoButtonDeleteHint));
        infoLore.add("");
        infoLore.add(FamiUtils.format(lang.invoiceInfoButtonFilterHint));

        inventory.setItem(52, FamiUtils.makeItem(Material.BOOK, FamiUtils.format(lang.invoiceInfoButtonTitle), infoLore.toArray(new String[0])));
    }

    /**
     * Gets the menu tags for this menu.
     * <p>
     * This method returns a list of tags that indicate what data this menu requires.
     * The PLAYER tag is always included, and the JOB tag is included when the filter mode is JOB.
     *
     * @return A list of menu tags
     */
    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.PLAYER);

        if (filterMode == FilterMode.JOB) {
            tags.add(MenuTag.JOB);
        }

        return tags;
    }
}
