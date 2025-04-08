package me.fami6xx.rpuniverse.core.invoice.language;

import me.fami6xx.rpuniverse.core.misc.language.AbstractAddonLanguage;

/**
 * Language class for the invoice system.
 * <p>
 * This class provides localized messages for the invoice system, including
 * invoice creation, menu, action, and notification messages.
 */
public class InvoiceLanguage extends AbstractAddonLanguage {
    
    // Invoice creation messages
    public String invoiceCreatedMessage = "&aInvoice created successfully for {player} for {amount}{currency}";
    public String invoiceReceivedMessage = "&aYou have received an invoice from {job} for {amount}{currency}";
    public String errorPlayerTooFarMessage = "&cPlayer is too far away to create an invoice";
    public String errorPlayerNotVisibleMessage = "&cYou must be able to see the player to create an invoice";
    public String errorNotInJobMessage = "&cYou must be in a job to create an invoice";
    public String errorInvalidAmountMessage = "&cInvalid amount. Please enter a valid number";
    public String errorAmountMustBePositiveMessage = "&cThe amount must be positive";
    public String errorDecimalNotAllowedMessage = "&cDecimal amounts are not allowed";
    public String errorSameWorldMessage = "&cYou must be in the same world as the target player";
    public String errorCannotInvoiceSelfMessage = "&cYou cannot create an invoice for yourself";
    
    // Invoice menu messages
    public String invoiceMenuTitle = "Invoices";
    public String noInvoicesMessage = "&cYou have no invoices";
    public String receivedFilterButtonName = "&aReceived Invoices";
    public String createdFilterButtonName = "&aCreated Invoices";
    public String jobFilterButtonName = "&aJob Invoices";
    
    // Invoice action messages
    public String invoicePaidMessage = "&aInvoice paid successfully";
    public String invoiceDeletedMessage = "&aInvoice deleted successfully";
    public String errorNotEnoughMoneyMessage = "&cYou don't have enough money to pay this invoice";
    public String errorNoPermissionToDeleteMessage = "&cYou don't have permission to delete this invoice";
    public String errorCanOnlyPayOwnInvoicesMessage = "&cYou can only pay invoices assigned to you";
    
    // Notification messages
    public String pendingInvoicesJoinMessage = "&aYou have {count} pending invoices. Use /invoices to view them";
    
    // Command messages
    public String errorOnlyPlayersMessage = "&cOnly players can use this command";
    public String errorNoPermissionMessage = "&cYou don't have permission to use this command";
    public String errorCommandUsageMessage = "&cUsage: /createinvoice <player> <amount>";
    public String errorPlayerNotFoundMessage = "&cPlayer not found";
    public String errorOpeningMenuMessage = "&cAn error occurred while opening the invoice menu";
    public String errorCreatingInvoiceMessage = "&cAn error occurred while creating the invoice";
    
    // Create a singleton instance
    private static InvoiceLanguage instance;
    
    /**
     * Gets the singleton instance of the InvoiceLanguage class.
     * 
     * @return The InvoiceLanguage instance
     */
    public static InvoiceLanguage getInstance() {
        if (instance == null) {
            instance = AbstractAddonLanguage.create(InvoiceLanguage.class);
        }
        return instance;
    }
    
    /**
     * Creates a new InvoiceLanguage instance.
     * <p>
     * This constructor is called by the AbstractAddonLanguage.create() method.
     * It initializes the language values by calling initLanguage().
     */
    public InvoiceLanguage() {
        // Call initLanguage() to register translations
        initLanguage();
    }
}