package me.fami6xx.rpuniverse.core.invoice.admin;

import me.fami6xx.rpuniverse.core.invoice.Invoice;
import me.fami6xx.rpuniverse.core.invoice.InvoiceManager;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Menu for editing an invoice.
 * <p>
 * This menu allows administrators to edit the details of an invoice,
 * such as the amount.
 */
public class EditInvoiceMenu extends Menu {

    private final InvoiceManager manager;
    private final InvoiceLanguage lang;
    private final Invoice invoice;

    /**
     * Creates a new EditInvoiceMenu.
     *
     * @param menu    The PlayerMenu instance
     * @param manager The InvoiceManager instance
     * @param invoice The invoice to edit
     */
    public EditInvoiceMenu(PlayerMenu menu, InvoiceManager manager, Invoice invoice) {
        super(menu);
        this.manager = manager;
        this.lang = InvoiceLanguage.getInstance();
        this.invoice = invoice;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(lang.adminInvoiceEditTitle.replace("{id}", invoice.getId()));
    }

    @Override
    public int getSlots() {
        return 54; // Same size as the original menu
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Get the display name of the clicked item
        String displayName = clickedItem.getItemMeta().getDisplayName();

        if (displayName.contains(FamiUtils.format(lang.adminInvoiceEditAmountButtonName))) {
            // Close the inventory and start the amount edit conversation
            player.closeInventory();
            player.sendMessage(FamiUtils.formatWithPrefix(lang.adminInvoiceEditAmountPrompt));

            // Start the edit process
            manager.startInvoiceAmountEdit(player, invoice);
        } else if (displayName.contains(FamiUtils.format(lang.adminBackButtonName))) {
            // Return to invoice management menu
            try {
                new InvoiceManagementMenu(this.playerMenu, manager).open();
                ErrorHandler.debug("Admin returned to invoice management menu");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening invoice management menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass(); // Fill the menu with glass panes
        // Format the creation date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = dateFormat.format(invoice.getCreationDate());

        // Add invoice information item
        ItemStack infoItem = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName(FamiUtils.format("&6Invoice #" + invoice.getId()));

        List<String> infoLore = new ArrayList<>();
        infoLore.add(FamiUtils.format("&7Job: &f" + invoice.getJobName()));
        infoLore.add(FamiUtils.format("&7From: &f" + (invoice.getCreatorPlayer() != null ? invoice.getCreatorPlayer().getName() : "Unknown")));
        infoLore.add(FamiUtils.format("&7To: &f" + (invoice.getTargetPlayer() != null ? invoice.getTargetPlayer().getName() : "Unknown")));
        infoLore.add(FamiUtils.format("&7Amount: &6" + invoice.getAmount() + manager.getModule().getDefaultCurrency()));
        infoLore.add(FamiUtils.format("&7Date: &f" + formattedDate));
        infoLore.add(FamiUtils.format("&7Status: &e" + invoice.getStatus().name()));

        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        inventory.setItem(13, infoItem);

        // Add edit amount button
        ItemStack editAmountButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta editAmountMeta = editAmountButton.getItemMeta();
        editAmountMeta.setDisplayName(FamiUtils.format(lang.adminInvoiceEditAmountButtonName));

        List<String> editAmountLore = new ArrayList<>();
        editAmountLore.add(FamiUtils.format(lang.adminInvoiceEditAmountButtonDescription));
        editAmountLore.add(FamiUtils.format("&7Current: &6" + invoice.getAmount() + manager.getModule().getDefaultCurrency()));
        editAmountLore.add(FamiUtils.format("&eClick to edit"));

        editAmountMeta.setLore(editAmountLore);
        editAmountButton.setItemMeta(editAmountMeta);
        inventory.setItem(22, editAmountButton);

        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(FamiUtils.format(lang.adminBackButtonName));
        backMeta.setLore(Arrays.asList(FamiUtils.format("&7Return to the invoice management menu")));
        backButton.setItemMeta(backMeta);
        inventory.setItem(31, backButton);
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return Arrays.asList(MenuTag.ADMIN);
    }
}
