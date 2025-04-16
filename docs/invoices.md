# RPUniverse Invoice System Documentation

This document provides comprehensive information about the RPUniverse invoice system, including general overview, user guide, administrator guide, and implementation details.

## Table of Contents

1. [General Overview](#general-overview)
2. [User Guide](#user-guide)
3. [Administrator Guide](#administrator-guide)
4. [Implementation Tasks](#implementation-tasks)

## General Overview

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

### Core Components

#### InvoiceModule

`InvoiceModule` is the main class that initializes and manages the invoice system. It extends `AbstractModule` and provides:

- Registration of commands
- Initialization of the invoice manager
- Configuration loading and saving
- Integration with other RPUniverse systems

#### InvoiceManager

`InvoiceManager` is responsible for managing all invoices in the system. Key features include:

- Creating, retrieving, and deleting invoices
- Loading and saving invoice data using GSON
- Filtering invoices by player, job, or status
- Handling invoice payment logic
- Notifying players about invoice events

#### Invoice

`Invoice` represents an individual invoice in the system. Each invoice has:

- A unique ID
- The job it was created from
- The player who created it
- The player it was assigned to
- The amount to be paid
- The creation date
- The status (pending, paid, deleted)

#### InvoiceMenu

`InvoiceMenu` extends `EasyPaginatedMenu` and provides the user interface for viewing and managing invoices. It includes:

- Pagination for viewing multiple invoices
- Filtering options for different views (received, created, job)
- Action buttons for paying or deleting invoices
- Status indicators for invoice state

#### InvoiceCommand

`InvoiceCommand` handles the `/invoices` command, which opens the invoice menu. It supports:

- Opening the menu with different filters
- Permission checking
- Player-only execution

#### CreateInvoiceCommand

`CreateInvoiceCommand` handles the `/createinvoice` command, which creates a new invoice. It supports:

- Job membership verification
- Distance and visibility checking
- Amount validation
- Player targeting

### Workflow

The typical workflow in the invoice system follows these steps:

1. **Invoice Creation**: A player in a job uses `/createinvoice <PLAYER> <AMOUNT>` to create an invoice using their currently selected job
2. **Argument Validation**: The system verifies that the correct number of arguments is provided
3. **Target Verification**: The system verifies that the target player exists and is not the creator themselves
4. **Amount Validation**: The system validates that the amount is positive and checks for decimals (if configured)
5. **World Check**: The system verifies that the players are in the same world
6. **Distance Check**: The system verifies that the target player is within the configured distance
7. **Visibility Check**: The system verifies that the creator can see the target player (if configured)
8. **Job Verification**: The system verifies that the creator is in a job (using their currently selected job)
9. **Invoice Storage**: The invoice is created and stored in the system
10. **Notification**: The target player is notified about the new invoice
11. **Invoice Viewing**: Players can view their invoices using the `/invoices` command
12. **Invoice Management**: Players can pay or delete invoices through the menu
13. **Data Persistence**: All changes are saved to the data file

### Configuration Options

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

### Commands and Permissions

#### Commands

- `/invoices [received|created|job]` - Open the invoice menu with optional filter
- `/createinvoice <PLAYER> <AMOUNT>` - Create a new invoice using your currently selected job

#### Permissions

- `rpu.invoices.view` - Allows using the invoice menu via the `/invoices` command
- `rpu.invoices.create` - Allows creating invoices via the `/createinvoice` command
- `rpu.invoices.delete.job` - Allows job bosses to delete any invoice from their job
- `rpu.invoices.view.job` - Allows job bosses to view all invoices for their job

Note: Players can always delete invoices they created themselves without needing a special permission.

### Integration with Other Systems

The invoice system integrates with several other systems in RPUniverse:

#### Job System

- Verification of job membership when creating invoices
- Special privileges for job bosses
- Job-specific invoice filtering

#### Menu API

- Use of `EasyPaginatedMenu` for the invoice interface
- Menu navigation and action handling
- Consistent UI experience

#### Language System

- All messages are localizable through the language system
- Support for placeholders in messages
- Integration with the `AbstractAddonLanguage` system

#### Economy System

- Integration with the server economy for invoice payments
- Transaction logging

### Data Structure and Storage

The invoice system uses GSON for data persistence. Invoices are stored in a JSON file.

### Implementation Details

#### Invoice Creation

When a player creates an invoice:

1. The system verifies that the player is in a job (using their currently selected job)
2. The system verifies that the correct number of arguments is provided
3. The system verifies that the target player exists and is not the creator themselves
4. The system validates that the amount is positive and checks for decimals (if configured)
5. The system verifies that the players are in the same world
6. The system checks that the target player is within the configured distance
7. The system verifies that the creator can see the target player (if configured)
8. A new `Invoice` object is created with a unique ID
9. The invoice is added to the `InvoiceManager`
10. The target player is notified about the new invoice
11. The invoice data is saved to the data file

#### Invoice Menu

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

#### Invoice Payment

When a player pays an invoice:

1. The system verifies that the player has enough money
2. The money is transferred from the player to the job bank
3. The invoice status is updated to "PAID"
4. The creator is notified that the invoice has been paid
5. The invoice data is saved to the data file

#### Invoice Deletion

When a player deletes an invoice:

1. The system verifies that the player has permission to delete the invoice:
   - The player created the invoice
   - The player is a boss in the job that created the invoice
2. The invoice status is updated to "DELETED"
3. The target player is notified that the invoice has been deleted
4. The invoice data is saved to the data file

#### Localization

The invoice system uses the language system for all messages. A sample language class might look like:

```java
public class InvoiceLanguage extends AbstractAddonLanguage {
    // Invoice creation messages
    public String invoiceCreatedMessage = "&aInvoice created successfully for {player} for {amount}{currency}";
    public String invoiceReceivedMessage = "&aYou have received an invoice from {job} for {amount}{currency}";
    public String errorPlayerTooFarMessage = "&cPlayer is too far away to create an invoice";
    public String errorPlayerNotVisibleMessage = "&cYou must be able to see the player to create an invoice";
    public String errorNotInJobMessage = "&cYou must be in a job to create an invoice";
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

### Best Practices

Here are some best practices for working with the RPUniverse invoice system:

1. **Use job verification**: Always verify that players are in the appropriate job before allowing invoice creation
2. **Set appropriate distance limits**: Configure the maximum distance based on your server's needs
3. **Use descriptive invoice messages**: Provide clear information about what the invoice is for
4. **Monitor invoice usage**: Regularly check for abuse or spam of the invoice system
5. **Configure appropriate permissions**: Carefully assign permissions to control who can create and delete invoices
6. **Use the language system**: Customize messages to match your server's theme and language
7. **Backup invoice data**: Regularly backup the invoice data file to prevent data loss

### Troubleshooting

If you encounter issues with the invoice system:

1. **Invoices not saving**: Check that the data file is writable and that the save interval is configured correctly
2. **Players can't create invoices**: Verify that they are in the correct job and have the necessary permissions
3. **Distance check failing**: Check that the maximum distance is configured appropriately for your server
4. **Menu not displaying correctly**: Ensure that the menu API is functioning properly
5. **Language messages not appearing**: Verify that the language system is initialized correctly
6. **Job verification failing**: Check that the job system is properly integrated with the invoice system
7. **Console errors**: Look for error messages related to the invoice system in the console

## User Guide

This section provides practical information for server administrators and players on how to use the RPUniverse invoice system. It includes command usage examples, configuration options, and permissions details.

### Command Usage Examples

#### For Players

##### Viewing Your Invoices

To view all invoices that have been sent to you:

```
/invoices
```

This opens the invoice menu showing all invoices you've received. You can navigate through pages using the arrow buttons if you have multiple invoices.

To view invoices you've created:

```
/invoices created
```

If you're a job boss, you can view all invoices for your job:

```
/invoices job
```

##### Creating an Invoice

To create an invoice for another player:

```
/createinvoice <player> <amount>
```

Example:
```
/createinvoice Steve 100
```

This creates an invoice for 100 currency units to player Steve from your current job.

**Requirements:**
- You must be in a job
- The target player must be within the configured distance (default: 5 blocks)
- You must be able to see the target player (if enabled in config)
- You must have the `rpu.invoices.create` permission

##### Paying an Invoice

1. Open the invoice menu with `/invoices`
2. Click on the invoice you want to pay
3. The payment will be processed automatically if you have enough money

##### Deleting an Invoice

1. Open the invoice menu with `/invoices created` to see invoices you've created
2. Shift-click on the invoice you want to delete
3. The invoice will be marked as deleted

Job bosses can also delete invoices for their job by using `/invoices job` and shift-clicking on invoices.

#### For Administrators

##### Checking Player Invoices

Administrators with appropriate permissions can check any player's invoices by using the job filter:

```
/invoices job
```

### Configuration Guide

The invoice system is configured in the `config.yml` file under the `modules.Invoices` section:

```yaml
modules:
  Invoices:
    enabled: true
    maxDistance: 5.0
    mustSeePlayer: true
    defaultCurrency: "$"
    saveInterval: 5
    notifyOnJoin: true
    allowDecimal: true
```

#### Configuration Options Explained

##### enabled
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** Enables or disables the entire invoice module. Set to false to turn off all invoice functionality.
- **Example:** `enabled: false` will disable the invoice system completely.

##### maxDistance
- **Type:** Double (decimal number)
- **Default:** 5.0
- **Description:** The maximum distance (in blocks) between players for invoice creation. Players must be within this distance to create invoices for each other.
- **Example:** `maxDistance: 10.0` allows players to create invoices for others up to 10 blocks away.

##### mustSeePlayer
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** If true, players must have a clear line of sight to the target player to create an invoice. This prevents creating invoices through walls or obstacles.
- **Example:** `mustSeePlayer: false` allows creating invoices without seeing the target player.

##### defaultCurrency
- **Type:** String
- **Default:** "$"
- **Description:** The currency symbol to display in invoice amounts.
- **Example:** `defaultCurrency: "€"` will show amounts like "100€" instead of "$100".

##### saveInterval
- **Type:** Integer
- **Default:** 5
- **Description:** How often (in minutes) to automatically save invoice data to disk.
- **Example:** `saveInterval: 10` will save invoice data every 10 minutes.

##### notifyOnJoin
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** If true, players will be notified about pending invoices when they join the server.
- **Example:** `notifyOnJoin: false` will disable join notifications.

##### allowDecimal
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** If true, allows decimal amounts in invoices (e.g., 10.5). If false, only whole numbers are allowed.
- **Example:** `allowDecimal: false` will only allow whole number amounts like 10, 100, etc.

### Permissions

#### Basic Permissions

- `rpu.invoices.use` - Allows using the invoice system (viewing invoices)
- `rpu.invoices.create` - Allows creating invoices for other players
- `rpu.invoices.delete.own` - Allows deleting invoices you've created
- `rpu.invoices.delete.job` - Allows job bosses to delete any invoice for their job
- `rpu.invoices.view.job` - Allows job bosses to view all invoices for their job

#### Permission Examples

##### Regular Player
```yaml
permissions:
  - rpu.invoices.use
  - rpu.invoices.create
  - rpu.invoices.delete.own
```

This allows a regular player to view invoices, create invoices, and delete their own invoices.

##### Job Boss
```yaml
permissions:
  - rpu.invoices.use
  - rpu.invoices.create
  - rpu.invoices.delete.own
  - rpu.invoices.delete.job
  - rpu.invoices.view.job
```

This allows a job boss to view all invoices for their job and delete any invoice for their job.

##### Administrator
```yaml
permissions:
  - rpu.invoices.*
```

This gives an administrator all invoice-related permissions.

### Troubleshooting Common Issues

#### "You don't have permission to use this command"
- Check that you have the appropriate permission (`rpu.invoices.use` for viewing, `rpu.invoices.create` for creating)
- Verify that the permissions are correctly assigned in your permissions plugin

#### "Player is too far away to create an invoice"
- Move closer to the target player
- Check the `maxDistance` configuration setting

#### "You must be able to see the player to create an invoice"
- Ensure you have a clear line of sight to the target player
- Check the `mustSeePlayer` configuration setting

#### "You must be in a job to create an invoice"
- Join a job before trying to create an invoice
- Use `/job join <jobname>` to join a job

#### "Invalid amount. Please enter a valid number"
- Ensure you're entering a valid number for the amount
- If using decimals, check that `allowDecimal` is set to true in the configuration

### Best Practices

1. **Set appropriate permissions:** Carefully assign permissions to control who can create and delete invoices
2. **Configure distance limits:** Set the `maxDistance` based on your server's roleplay style
3. **Regular backups:** The invoice data is stored in `plugins/RPUniverse/invoices.json` - back it up regularly
4. **Monitor usage:** Watch for players abusing the system by creating excessive invoices
5. **Educate players:** Make sure players understand how to use the invoice system properly

## Administrator Guide

This section provides detailed technical information for server administrators on managing, troubleshooting, and extending the RPUniverse invoice system.

### System Architecture

The invoice system consists of several key components:

1. **InvoiceModule**: The main module class that initializes the system
2. **InvoiceManager**: Handles invoice CRUD operations and data persistence
3. **Invoice**: Represents individual invoices with their properties
4. **InvoiceMenu**: Provides the user interface for viewing and managing invoices
5. **Commands**: Handles user interactions through commands
6. **Language System**: Provides localization for all messages

### Data Storage

Invoices are stored in `plugins/RPUniverse/invoices.json` using GSON serialization. The file structure is:

```json
[
  {
    "id": "unique-uuid",
    "job": "JobName",
    "creator": "creator-uuid",
    "target": "target-uuid",
    "amount": 100.0,
    "creationDate": "2023-04-08T12:34:56.789Z",
    "status": "PENDING"
  }
]
```

Note: The actual file would contain multiple invoice objects in the array.

#### Backup Strategy

It's recommended to:

1. Set up automatic backups of the `invoices.json` file
2. Configure the `saveInterval` option to an appropriate value (5-15 minutes)
3. Implement a rotation system for backups (daily, weekly, monthly)

Example backup script:
```bash
#!/bin/bash
DATE=$(date +%Y-%m-%d)
cp plugins/RPUniverse/invoices.json backups/invoices-$DATE.json
# Keep only the last 30 backups
ls -t backups/invoices-*.json | tail -n +31 | xargs rm -f
```

### Integration Points

#### Job System Integration

The invoice system integrates with the job system through:

1. **Job Membership Verification**: Checks if players are in jobs when creating invoices
2. **Job Boss Privileges**: Provides special permissions for job bosses
3. **Job-Specific Filtering**: Allows filtering invoices by job

Integration points in code:
- `InvoiceManager.isPlayerJobBoss()`: Checks if a player is a boss in a job
- `InvoiceMenu.getFilteredInvoices()`: Filters invoices by job
- `CreateInvoiceCommand.onCommand()`: Verifies job membership

#### Economy System Integration

The invoice system integrates with the economy system (Vault) through:

1. **Balance Checking**: Verifies players have enough money to pay invoices
2. **Money Transfer**: Handles the transfer of funds between players
3. **Transaction Logging**: Records all financial transactions

Integration points in code:
- `InvoiceManager.payInvoice()`: Handles the payment process
- `RPUniverse.getEconomy()`: Gets the economy provider

#### Menu API Integration

The invoice system integrates with the menu API through:

1. **EasyPaginatedMenu**: Uses the pagination system for displaying invoices
2. **Menu Navigation**: Implements navigation between menu pages
3. **Action Handling**: Processes user actions like paying or deleting invoices

Integration points in code:
- `InvoiceMenu` class: Extends `EasyPaginatedMenu`
- `InvoiceMenu.handlePaginatedMenu()`: Processes menu actions

#### Language System Integration

The invoice system integrates with the language system through:

1. **Message Localization**: All user-facing messages are localized
2. **Placeholder Support**: Messages support placeholders for dynamic content
3. **Language Initialization**: The system initializes language resources on startup

Integration points in code:
- `InvoiceLanguage` class: Extends `AbstractAddonLanguage`
- `InvoiceManager` methods: Use language strings for messages

### Advanced Configuration

#### Custom Configuration

You can extend the default configuration with custom options by modifying the `InvoiceModule` class:

1. Add new configuration fields to the class
2. Add getter methods for the new fields
3. Update the configuration loading in the `enable()` method

Example for adding a new configuration option:
```java
// Add this field to the InvoiceModule class
private boolean requireConfirmation;

// Add this getter method to the InvoiceModule class
public boolean isConfirmationRequired() {
    return requireConfirmation;
}

// Add this line in the enable() method of InvoiceModule class
@Override
public boolean enable() {
    try {
        // Existing code...

        // Load the new configuration option
        this.requireConfirmation = getConfigBoolean("requireConfirmation", false);

        // Existing code...
        return true;
    } catch (Exception e) {
        ErrorHandler.severe("Failed to enable InvoiceModule", e);
        return false;
    }
}
```

#### Performance Tuning

For servers with high invoice volume:

1. **Increase Save Interval**: Set `saveInterval` to a higher value (10-15 minutes)
2. **Implement Caching**: Consider adding a caching layer for frequently accessed invoices
3. **Optimize Queries**: Use indexed collections for faster invoice lookups
4. **Batch Processing**: Process multiple invoices in batch operations

### Troubleshooting Guide

#### Common Issues and Solutions

##### Invoice Creation Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Players can't create invoices | Permission issues, Job system problems | Check permissions, Verify job membership |
| Distance check failing | Configuration issues, World issues | Check `maxDistance` setting, Verify players are in same world |
| Visibility check failing | Line of sight issues | Check `mustSeePlayer` setting, Verify no blocks between players |

##### Invoice Payment Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Payment failing | Economy issues, Insufficient funds | Check economy plugin, Verify player balance |
| Money not transferring | Economy plugin compatibility | Test economy plugin separately, Check transaction logs |
| Payment not saving | Data persistence issues | Check file permissions, Verify save process |

##### Menu Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Menu not displaying | Menu API issues | Check menu API functionality, Verify menu initialization |
| Pagination not working | Collection issues | Check invoice collection size, Verify pagination logic |
| Actions not processing | Event handling issues | Check event registration, Verify action handlers |

##### Data Persistence Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Invoices not saving | File permission issues, I/O errors | Check file permissions, Verify save process |
| Invoices not loading | File format issues, Missing file | Check file format, Verify file exists |
| Data corruption | Concurrent access, Server crashes | Implement transaction system, Regular backups |

#### Diagnostic Tools

##### Log Analysis

Look for these patterns in logs:

- `[RPUniverse] [ERROR] Failed to save invoice data`: Indicates data persistence issues
- `[RPUniverse] [ERROR] Failed to initialize InvoiceModule`: Indicates module initialization issues
- `[RPUniverse] [DEBUG] Invoice payment: ...`: Shows payment transaction details

##### Performance Monitoring

Monitor these metrics:

- Invoice creation rate: Should be reasonable for your server size
- Payment processing time: Should be under 100ms
- Menu opening time: Should be under 200ms
- File save time: Should be under 500ms

### Best Practices

#### Security

1. **Permission Management**: Carefully assign permissions to prevent abuse
2. **Input Validation**: All user input is validated, but monitor for exploitation attempts
3. **Transaction Verification**: Double-check all financial transactions
4. **Access Control**: Restrict sensitive operations to authorized users

#### Performance

1. **Optimize Save Interval**: Balance between data safety and performance
2. **Limit Active Invoices**: Consider implementing a cap on active invoices per player
3. **Batch Processing**: Process multiple operations in batches when possible
4. **Asynchronous Operations**: Use async tasks for I/O operations

#### Maintenance

1. **Regular Backups**: Implement automated backups of invoice data
2. **Data Cleanup**: Periodically archive or remove old invoices
3. **Version Updates**: Keep the plugin updated to the latest version
4. **Monitoring**: Set up alerts for unusual activity or errors

#### Integration

1. **Plugin Dependencies**: Ensure all required plugins are properly installed
2. **Version Compatibility**: Check compatibility when updating other plugins
3. **API Usage**: Use the provided APIs rather than direct access to data
4. **Event Handling**: Listen for relevant events from other systems

### Extending the System

#### Adding Custom Features

To add custom features to the invoice system:

1. Create a new class that extends or uses the existing classes
2. Register your class with the appropriate systems
3. Add configuration options for your feature
4. Document your feature for users

Example: Adding a notification system for expired invoices:
```java
public class InvoiceExpiryNotifier implements Listener {
    private final InvoiceManager manager;
    private final long expiryTime;

    public InvoiceExpiryNotifier(InvoiceManager manager, long expiryTime) {
        this.manager = manager;
        this.expiryTime = expiryTime;
        Bukkit.getPluginManager().registerEvents(this, manager.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<Invoice> expiredInvoices = manager.getInvoicesByCreator(player.getUniqueId()).stream()
            .filter(invoice -> invoice.isPending() && 
                    System.currentTimeMillis() - invoice.getCreationDate().getTime() > expiryTime)
            .collect(Collectors.toList());

        if (!expiredInvoices.isEmpty()) {
            player.sendMessage("You have " + expiredInvoices.size() + " expired invoices!");
        }
    }
}
```

#### API Documentation

The invoice system provides these key APIs for developers:

##### InvoiceManager API

```java
// Create an invoice
Invoice createInvoice(String job, UUID creator, UUID target, double amount);

// Get an invoice by ID
Invoice getInvoice(String id);

// Get invoices by various criteria
List<Invoice> getInvoicesByCreator(UUID creator);
List<Invoice> getInvoicesByTarget(UUID target);
List<Invoice> getInvoicesByJob(String job);
List<Invoice> getInvoicesByStatus(Invoice.Status status);

// Process invoice actions
boolean payInvoice(Invoice invoice, Player player);
boolean deleteInvoice(Invoice invoice, Player player);
```

##### Invoice API

```java
// Get invoice properties
String getId();
String getJob();
UUID getCreator();
UUID getTarget();
double getAmount();
Date getCreationDate();
Status getStatus();

// Check invoice status
boolean isPending();
boolean isPaid();
boolean isDeleted();

// Update invoice status
void markAsPaid();
void markAsDeleted();
```

## Implementation Tasks

This section contains a comprehensive list of tasks required to implement the RPUniverse invoice system.

### Core Components Implementation

[x] Create the `InvoiceModule` class
   - Extend `AbstractModule`
   - Implement module initialization and shutdown
   - Register commands
   - Set up configuration loading and saving
   - Integrate with other RPUniverse systems

[x] Implement the `InvoiceManager` class
   - Create methods for invoice CRUD operations
   - Implement data persistence using GSON
   - Add filtering capabilities (by player, job, status)
   - Implement invoice payment logic
   - Add notification system for invoice events

[x] Create the `Invoice` class
   - Define properties (ID, job, creator, target, amount, date, status)
   - Implement getters and setters
   - Add serialization/deserialization support for GSON
   - Implement status management (pending, paid, deleted)

[x] Develop the `InvoiceMenu` class
   - Extend `EasyPaginatedMenu`
   - Implement pagination for viewing multiple invoices
   - Add filtering options (received, created, job)
   - Create action buttons (pay, delete)
   - Implement status indicators

[x] Create command classes
   - Implement `InvoiceCommand` for opening the menu
   - Implement `CreateInvoiceCommand` for creating invoices
   - Create an auto complete for the commands
   - Add permission checks
   - Implement command argument validation

### Integration Tasks

[x] Get all the permissions
   - Check all the files in `me.fami6xx.rpuniverse.core.invoice`
   - Add permissions to the `plugin.yml` file

[x] Integrate with the Job System
   - Implement job membership verification
   - Add special privileges for job bosses
   - Create job-specific invoice filtering

[x] Integrate with the Menu API
   - Ensure proper use of `EasyPaginatedMenu`
   - Implement menu navigation
   - Set up action handling

[x] Integrate with the Language System
   - Create `InvoiceLanguage` class extending `AbstractAddonLanguage`
   - Define all necessary message strings
   - Implement placeholder support
   - Set up language initialization

[x] Integrate with the Economy System
   - Implement balance checking
   - Set up money transfer functionality
   - Add transaction logging

### Configuration Implementation

[x] Create default configuration
   - Add module enabled/disabled toggle
   - Implement distance configuration
   - Add visibility checking option
   - Set up currency symbol configuration
   - Configure save interval
   - Add join notification option
   - Implement decimal amount toggle

[x] Add configuration validation
   - Validate numeric values
   - Check for required settings
   - Provide sensible defaults
   - Create a new migration 

### Data Persistence

[x] Implement data storage
   - Create JSON file structure
   - Implement GSON serialization/deserialization
   - Add data loading on startup
   - Implement periodic data saving
   - Add data backup functionality

### Feature Implementation

[x] Implement invoice creation workflow
   - Add job verification
   - Implement distance checking
   - Add visibility verification
   - Create amount validation
   - Set up invoice object creation
   - Implement notification system

[x] Develop invoice payment system
   - Add balance verification
   - Implement money transfer
   - Create status updating
   - Set up notification for payment

[x] Implement invoice deletion
   - Add permission checking
   - Implement status updating
   - Create notification for deletion

[x] Add notification system
   - Implement join notifications
   - Create invoice status change notifications
   - Add action result notifications

### Documentation

[x] Update JavaDoc
   - Document all classes and methods
   - Add examples where appropriate
   - Document public APIs thoroughly

[x] Create user documentation
   - Add command usage examples
   - Create configuration guide
   - Document permissions

[x] Develop admin documentation
   - Add troubleshooting section
   - Create best practices guide
   - Document integration points

### Deployment

[x] Prepare for release
   - Final code review
   - Performance testing
   - Ensure all features are complete

[x] Release management
   - Version tagging
   - Update changelog
   - Prepare release notes
