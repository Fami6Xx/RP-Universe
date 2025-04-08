package me.fami6xx.rpuniverse.core.invoice.language;

import me.fami6xx.rpuniverse.core.misc.language.AbstractAddonLanguage;

/**
 * Language class for the invoice system.
 * <p>
 * This class provides localized messages for the invoice system, including
 * invoice creation, menu, action, notification, and admin messages.
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

    public String receivedFilterButtonName = "&aReceived Invoices";
    public String createdFilterButtonName = "&aCreated Invoices";
    public String jobFilterButtonName = "&aJob Invoices";
    public String unknownFilterButtonName = "&cUnknown Filter";
    public String currentlySelectedText = "&aCurrently selected";
    public String clickToSelectText = "&eClick to select";
    public String receivedFilterDescription = "&7View invoices sent to you";
    public String createdFilterDescription = "&7View invoices you've created";
    public String jobFilterDescription = "&7View all invoices for your job";

    // Invoice menu filter names
    public String receivedFilterName = "Received";
    public String createdFilterName = "Created";
    public String jobFilterName = "Job";
    public String allFilterName = "All";
    public String invoicesMenuTitle = "&6Invoices - {filterName}";

    // Invoice item display
    public String invoiceItemTitle = "&6Invoice #{id}";
    public String invoiceItemJobLine = "&7Job: &f{job}";
    public String invoiceItemFromLine = "&7From: &f{from}";
    public String invoiceItemToLine = "&7To: &f{to}";
    public String invoiceItemAmountLine = "&7Amount: &6{amount}{currency}";
    public String invoiceItemDateLine = "&7Date: &f{date}";
    public String invoiceItemStatusLine = "&7Status: {statusColor}{status}";
    public String invoiceItemClickToPay = "&aClick to pay";
    public String invoiceItemShiftClickToDelete = "&cShift-click to delete";
    public String unknownPlayerName = "Unknown";

    // Invoice info button
    public String invoiceInfoButtonTitle = "&bInvoice Information";
    public String invoiceInfoButtonDescription = "&7View and manage your invoices";
    public String invoiceInfoButtonPayHint = "&eClick on a pending invoice to pay it";
    public String invoiceInfoButtonDeleteHint = "&eShift-click on your invoice to delete it";
    public String invoiceInfoButtonFilterHint = "&7Use the filter buttons to change view";

    // Invoice action messages
    public String invoicePaidMessage = "&aInvoice paid successfully";
    public String invoiceDeletedMessage = "&aInvoice deleted successfully";
    public String errorNotEnoughMoneyMessage = "&cYou don't have enough money to pay this invoice";
    public String errorNoPermissionToDeleteMessage = "&cYou don't have permission to delete this invoice";
    public String errorCanOnlyPayOwnInvoicesMessage = "&cYou can only pay invoices assigned to you";
    public String errorDeletingInvoiceMessage = "&cFailed to delete invoice";
    public String errorPayingInvoiceMessage = "&cFailed to pay invoice. Do you have enough money?";

    // Notification messages
    public String pendingInvoicesJoinMessage = "&aYou have {count} pending invoices. Use /invoices to view them";

    // Command messages
    public String errorOnlyPlayersMessage = "&cOnly players can use this command";
    public String errorNoPermissionMessage = "&cYou don't have permission to use this command";
    public String errorCommandUsageMessage = "&cUsage: /createinvoice <player> <amount>";
    public String errorPlayerNotFoundMessage = "&cPlayer not found";
    public String errorOpeningMenuMessage = "&cAn error occurred while opening the invoice menu";
    public String errorCreatingInvoiceMessage = "&cAn error occurred while creating the invoice";

    // Admin menu messages
    public String adminMenuTitle = "&6Invoice Admin Menu";
    public String adminInvoiceManagementTitle = "&6Invoice Management";
    public String adminSystemSettingsTitle = "&6System Settings";
    public String adminMaintenanceToolsTitle = "&6Maintenance Tools";

    // Admin menu buttons
    public String adminInvoiceManagementButtonName = "&aInvoice Management";
    public String adminInvoiceManagementButtonDescription = "&7View and manage all invoices";
    public String adminSystemSettingsButtonName = "&aSystem Settings";
    public String adminSystemSettingsButtonDescription = "&7Configure invoice system settings";
    public String adminMaintenanceToolsButtonName = "&aMaintenanceTools";
    public String adminMaintenanceToolsButtonDescription = "&7Access maintenance and troubleshooting tools";

    // Admin invoice management messages
    public String adminViewAllInvoicesButtonName = "&aView All Invoices";
    public String adminViewAllInvoicesButtonDescription = "&7View all invoices in the system";
    public String adminSortByDateButtonName = "&aSort by Date";
    public String adminSortByAmountButtonName = "&aSort by Amount";
    public String adminSortByStatusButtonName = "&aSort by Status";
    public String adminSortByCreatorButtonName = "&aSort by Creator";
    public String adminSortByRecipientButtonName = "&aSort by Recipient";
    public String adminFilterByStatusButtonName = "&aFilter by Status";
    public String adminFilterByJobButtonName = "&aFilter by Job";
    public String adminFilterByPlayerButtonName = "&aFilter by Player";
    public String adminFilterByDateRangeButtonName = "&aFilter by Date Range";
    public String adminFilterByAmountRangeButtonName = "&aFilter by Amount Range";

    // Admin invoice action messages
    public String adminInvoiceEditButtonName = "&aEdit Invoice";
    public String adminInvoiceEditButtonDescription = "&7Edit this invoice's details";
    public String adminInvoiceDeleteButtonName = "&cDelete Invoice";
    public String adminInvoiceDeleteButtonDescription = "&7Delete this invoice";
    public String adminInvoiceRestoreButtonName = "&aRestore Invoice";
    public String adminInvoiceRestoreButtonDescription = "&7Restore this deleted invoice";
    public String adminInvoiceForcePayButtonName = "&aForce Payment";
    public String adminInvoiceForcePayButtonDescription = "&7Force payment for this invoice";

    // Admin system settings messages
    public String adminJobIntegrationSettingsButtonName = "&aJob Integration Settings";
    public String adminJobIntegrationSettingsButtonDescription = "&7Configure which jobs can create invoices";

    // Admin maintenance tools messages
    public String adminDataBackupButtonName = "&aManual Data Backup";
    public String adminDataBackupButtonDescription = "&7Create a manual backup of invoice data";
    public String adminDataRestoreButtonName = "&aRestore from Backup";
    public String adminDataRestoreButtonDescription = "&7Restore invoice data from a backup";
    public String adminClearOldInvoicesButtonName = "&cClear Old Invoices";
    public String adminClearOldInvoicesButtonDescription = "&7Delete old or invalid invoices";
    public String adminNotificationSystemButtonName = "&aNotification System";
    public String adminNotificationSystemButtonDescription = "&7Configure notification settings";

    // Admin action result messages
    public String adminInvoiceEditedMessage = "&aInvoice #{id} has been edited successfully";
    public String adminInvoiceDeletedMessage = "&aInvoice #{id} has been deleted successfully";
    public String adminInvoiceRestoredMessage = "&aInvoice #{id} has been restored successfully";
    public String adminInvoiceForcePaidMessage = "&aInvoice #{id} has been force paid successfully";
    public String adminDataBackupCreatedMessage = "&aData backup created successfully";
    public String adminDataRestoredMessage = "&aData restored successfully from backup";
    public String adminOldInvoicesClearedMessage = "&a{count} old invoices have been cleared";

    // Admin error messages
    public String adminErrorInvalidInvoiceIdMessage = "&cInvalid invoice ID";
    public String adminErrorCannotEditPaidInvoiceMessage = "&cCannot edit a paid invoice";
    public String adminErrorCannotRestoreNonDeletedInvoiceMessage = "&cCannot restore a non-deleted invoice";
    public String adminErrorCannotForcePayPaidInvoiceMessage = "&cCannot force payment for an already paid invoice";
    public String adminErrorNoBackupsFoundMessage = "&cNo backups found";
    public String adminErrorBackupFailedMessage = "&cBackup creation failed";
    public String adminErrorRestoreFailedMessage = "&cRestore operation failed";
    public String adminErrorClearFailedMessage = "&cFailed to clear old invoices";

    // Admin notification messages
    public String adminActionLoggedMessage = "&aAdmin action logged: {action} by {admin}";
    public String adminInvoiceEditedNotificationMessage = "&aYour invoice #{id} has been edited by an administrator";
    public String adminInvoiceDeletedNotificationMessage = "&cYour invoice #{id} has been deleted by an administrator";
    public String adminInvoiceRestoredNotificationMessage = "&aYour invoice #{id} has been restored by an administrator";
    public String adminInvoiceForcePaidNotificationMessage = "&aYour invoice #{id} has been force paid by an administrator";

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
