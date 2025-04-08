# RPUniverse Invoice System

This document provides detailed information about the RPUniverse invoice system, including core components, workflow, configuration, and implementation details.

## Overview

The RPUniverse invoice system is a comprehensive framework for creating, managing, and tracking invoices between players. It allows players in specific jobs to create invoices for other players, which can then be viewed, paid, or deleted through an intuitive interface.

Key features of the invoice system include:

- **Invoice Creation**: Players in jobs can create invoices for other players
- **Invoice Management**: View, pay, and delete invoices through a paginated menu
- **Job Integration**: Verification of job membership when creating invoices
- **Distance Checking**: Configurable distance requirement between players for invoice creation
- **Visibility Checking**: Requirement that players must see each other to create invoices
- **Filtering Options**: View invoices you've received, created, or all invoices for your job (for job bosses)
- **Data Persistence**: GSON-based storage for invoice data
- **Localization**: Full support for the RPUniverse language system

## Core Components

### InvoiceModule

`InvoiceModule` is the main class that initializes and manages the invoice system. It extends `AbstractModule` and provides:

- Registration of commands
- Initialization of the invoice manager
- Configuration loading and saving
- Integration with other RPUniverse systems

### InvoiceManager

`InvoiceManager` is responsible for managing all invoices in the system. Key features include:

- Creating, retrieving, and deleting invoices
- Loading and saving invoice data using GSON
- Filtering invoices by player, job, or status
- Handling invoice payment logic
- Notifying players about invoice events

### Invoice

`Invoice` represents an individual invoice in the system. Each invoice has:

- A unique ID
- The job it was created from
- The player who created it
- The player it was assigned to
- The amount to be paid
- The creation date
- The status (pending, paid, deleted)

### InvoiceMenu

`InvoiceMenu` extends `EasyPaginatedMenu` and provides the user interface for viewing and managing invoices. It includes:

- Pagination for viewing multiple invoices
- Filtering options for different views (received, created, job)
- Action buttons for paying or deleting invoices
- Status indicators for invoice state

### InvoiceCommand

`InvoiceCommand` handles the `/invoices` command, which opens the invoice menu. It supports:

- Opening the menu with different filters
- Permission checking
- Player-only execution

### CreateInvoiceCommand

`CreateInvoiceCommand` handles the `/createinvoice` command, which creates a new invoice. It supports:

- Job membership verification
- Distance and visibility checking
- Amount validation
- Player targeting

## Workflow

The typical workflow in the invoice system follows these steps:

1. **Invoice Creation**: A player in a job uses `/createinvoice <JOB> <PLAYER> <AMOUNT>` to create an invoice
2. **Distance Check**: The system verifies that the target player is within the configured distance
3. **Visibility Check**: The system verifies that the creator can see the target player
4. **Job Verification**: The system verifies that the creator is in the specified job
5. **Invoice Storage**: The invoice is created and stored in the system
6. **Notification**: The target player is notified about the new invoice
7. **Invoice Viewing**: Players can view their invoices using the `/invoices` command
8. **Invoice Management**: Players can pay or delete invoices through the menu
9. **Data Persistence**: All changes are saved to the data file

## Configuration Options

The invoice system can be configured through the `config.yml` file in the `modules.Invoices` section:

```yaml
modules:
  Invoices:
    # enabled: If true, the module is enabled
    enabled: true
    # maxDistance: Maximum distance between players for invoice creation
    maxDistance: 5.0
    # mustSeePlayer: If true, the creator must be able to see the target player
    mustSeePlayer: true
    # defaultCurrency: The default currency symbol to use
    defaultCurrency: "$"
    # saveInterval: How often to save invoice data (in minutes)
    saveInterval: 5
    # notifyOnJoin: If true, notify players about pending invoices when they join
    notifyOnJoin: true
    # allowDecimal: If true, allow decimal amounts in invoices
    allowDecimal: true
```

Configuration options include:

- **enabled**: Whether the invoice module is enabled
- **maxDistance**: Maximum distance between players for invoice creation
- **mustSeePlayer**: Whether the creator must be able to see the target player
- **defaultCurrency**: The default currency symbol to use
- **saveInterval**: How often to save invoice data (in minutes)
- **notifyOnJoin**: Whether to notify players about pending invoices when they join
- **allowDecimal**: Whether to allow decimal amounts in invoices

## Commands and Permissions

### Commands

- `/invoices [received|created|job]` - Open the invoice menu with optional filter
- `/createinvoice <JOB> <PLAYER> <AMOUNT>` - Create a new invoice

### Permissions

- `rpu.invoices.use` - Allows using the invoice system
- `rpu.invoices.create` - Allows creating invoices
- `rpu.invoices.delete.own` - Allows deleting own invoices
- `rpu.invoices.delete.job` - Allows job bosses to delete job invoices
- `rpu.invoices.view.job` - Allows job bosses to view all job invoices

## Integration with Other Systems

The invoice system integrates with several other systems in RPUniverse:

### Job System

- Verification of job membership when creating invoices
- Special privileges for job bosses
- Job-specific invoice filtering

### Menu API

- Use of `EasyPaginatedMenu` for the invoice interface
- Menu navigation and action handling
- Consistent UI experience

### Language System

- All messages are localizable through the language system
- Support for placeholders in messages
- Integration with the `AbstractAddonLanguage` system

### Economy System

- Integration with the server economy for invoice payments
- Transaction logging

## Data Structure and Storage

The invoice system uses GSON for data persistence. Invoices are stored in a JSON file.

## Implementation Details

### Invoice Creation

When a player creates an invoice:

1. The system verifies that the player is in the specified job
2. The system checks that the target player is within the configured distance
3. The system verifies that the creator can see the target player (if configured)
4. The system validates the amount (checking for decimals if configured)
5. A new `Invoice` object is created with a unique ID
6. The invoice is added to the `InvoiceManager`
7. The target player is notified about the new invoice
8. The invoice data is saved to the data file

### Invoice Menu

The invoice menu is implemented as an `EasyPaginatedMenu` with the following features:

- Items representing invoices with information about:
  - The job it was created from
  - The player who created it
  - The amount to be paid
  - The creation date
  - The status
- Navigation buttons for pagination
- Filter buttons for different views:
  - Received invoices (default)
  - Created invoices
  - Job invoices (for job bosses)
- Action buttons for:
  - Paying invoices
  - Deleting invoices (if permitted)

### Invoice Payment

When a player pays an invoice:

1. The system verifies that the player has enough money
2. The money is transferred from the player to the job bank
3. The invoice status is updated to "PAID"
4. The creator is notified that the invoice has been paid
5. The invoice data is saved to the data file

### Invoice Deletion

When a player deletes an invoice:

1. The system verifies that the player has permission to delete the invoice:
   - The player created the invoice
   - The player is a boss in the job that created the invoice
2. The invoice status is updated to "DELETED"
3. The target player is notified that the invoice has been deleted
4. The invoice data is saved to the data file

### Localization

The invoice system uses the language system for all messages. A sample language class might look like:

```java
public class InvoiceLanguage extends AbstractAddonLanguage {
    // Invoice creation messages
    public String invoiceCreatedMessage = "&aInvoice created successfully for {player} for {amount}{currency}";
    public String invoiceReceivedMessage = "&aYou have received an invoice from {job} for {amount}{currency}";
    public String errorPlayerTooFarMessage = "&cPlayer is too far away to create an invoice";
    public String errorPlayerNotVisibleMessage = "&cYou must be able to see the player to create an invoice";
    public String errorNotInJobMessage = "&cYou are not in the specified job";
    public String errorInvalidAmountMessage = "&cInvalid amount. Please enter a valid number";
    
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
    
    // Notification messages
    public String pendingInvoicesJoinMessage = "&aYou have {count} pending invoices. Use /invoices to view them";
    
    // Create a singleton instance
    private static InvoiceLanguage instance;
    
    public static InvoiceLanguage getInstance() {
        if (instance == null) {
            instance = AbstractAddonLanguage.create(InvoiceLanguage.class);
        }
        return instance;
    }
    
    // Constructor
    public InvoiceLanguage() {
        // Call initLanguage() to register translations
        initLanguage();
    }
}
```

## Best Practices

Here are some best practices for working with the RPUniverse invoice system:

1. **Use job verification**: Always verify that players are in the appropriate job before allowing invoice creation
2. **Set appropriate distance limits**: Configure the maximum distance based on your server's needs
3. **Use descriptive invoice messages**: Provide clear information about what the invoice is for
4. **Monitor invoice usage**: Regularly check for abuse or spam of the invoice system
5. **Configure appropriate permissions**: Carefully assign permissions to control who can create and delete invoices
6. **Use the language system**: Customize messages to match your server's theme and language
7. **Backup invoice data**: Regularly backup the invoice data file to prevent data loss

## Troubleshooting

If you encounter issues with the invoice system:

1. **Invoices not saving**: Check that the data file is writable and that the save interval is configured correctly
2. **Players can't create invoices**: Verify that they are in the correct job and have the necessary permissions
3. **Distance check failing**: Check that the maximum distance is configured appropriately for your server
4. **Menu not displaying correctly**: Ensure that the menu API is functioning properly
5. **Language messages not appearing**: Verify that the language system is initialized correctly
6. **Job verification failing**: Check that the job system is properly integrated with the invoice system
7. **Console errors**: Look for error messages related to the invoice system in the console

## Conclusion

The RPUniverse invoice system provides a comprehensive framework for creating and managing invoices between players. By understanding its components and workflow, server administrators can create an engaging economic ecosystem that enhances the roleplay experience.