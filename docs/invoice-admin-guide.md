# RPUniverse Invoice System - Administrator Guide

This guide provides detailed technical information for server administrators on managing, troubleshooting, and extending the RPUniverse invoice system.

## System Architecture

The invoice system consists of several key components:

1. **InvoiceModule**: The main module class that initializes the system
2. **InvoiceManager**: Handles invoice CRUD operations and data persistence
3. **Invoice**: Represents individual invoices with their properties
4. **InvoiceMenu**: Provides the user interface for viewing and managing invoices
5. **Commands**: Handles user interactions through commands
6. **Language System**: Provides localization for all messages

## Data Storage

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

### Backup Strategy

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

## Integration Points

### Job System Integration

The invoice system integrates with the job system through:

1. **Job Membership Verification**: Checks if players are in jobs when creating invoices
2. **Job Boss Privileges**: Provides special permissions for job bosses
3. **Job-Specific Filtering**: Allows filtering invoices by job

Integration points in code:
- `InvoiceManager.isPlayerJobBoss()`: Checks if a player is a boss in a job
- `InvoiceMenu.getFilteredInvoices()`: Filters invoices by job
- `CreateInvoiceCommand.onCommand()`: Verifies job membership

### Economy System Integration

The invoice system integrates with the economy system (Vault) through:

1. **Balance Checking**: Verifies players have enough money to pay invoices
2. **Money Transfer**: Handles the transfer of funds between players
3. **Transaction Logging**: Records all financial transactions

Integration points in code:
- `InvoiceManager.payInvoice()`: Handles the payment process
- `RPUniverse.getEconomy()`: Gets the economy provider

### Menu API Integration

The invoice system integrates with the menu API through:

1. **EasyPaginatedMenu**: Uses the pagination system for displaying invoices
2. **Menu Navigation**: Implements navigation between menu pages
3. **Action Handling**: Processes user actions like paying or deleting invoices

Integration points in code:
- `InvoiceMenu` class: Extends `EasyPaginatedMenu`
- `InvoiceMenu.handlePaginatedMenu()`: Processes menu actions

### Language System Integration

The invoice system integrates with the language system through:

1. **Message Localization**: All user-facing messages are localized
2. **Placeholder Support**: Messages support placeholders for dynamic content
3. **Language Initialization**: The system initializes language resources on startup

Integration points in code:
- `InvoiceLanguage` class: Extends `AbstractAddonLanguage`
- `InvoiceManager` methods: Use language strings for messages

## Advanced Configuration

### Custom Configuration

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

### Performance Tuning

For servers with high invoice volume:

1. **Increase Save Interval**: Set `saveInterval` to a higher value (10-15 minutes)
2. **Implement Caching**: Consider adding a caching layer for frequently accessed invoices
3. **Optimize Queries**: Use indexed collections for faster invoice lookups
4. **Batch Processing**: Process multiple invoices in batch operations

## Troubleshooting Guide

### Common Issues and Solutions

#### Invoice Creation Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Players can't create invoices | Permission issues, Job system problems | Check permissions, Verify job membership |
| Distance check failing | Configuration issues, World issues | Check `maxDistance` setting, Verify players are in same world |
| Visibility check failing | Line of sight issues | Check `mustSeePlayer` setting, Verify no blocks between players |

#### Invoice Payment Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Payment failing | Economy issues, Insufficient funds | Check economy plugin, Verify player balance |
| Money not transferring | Economy plugin compatibility | Test economy plugin separately, Check transaction logs |
| Payment not saving | Data persistence issues | Check file permissions, Verify save process |

#### Menu Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Menu not displaying | Menu API issues | Check menu API functionality, Verify menu initialization |
| Pagination not working | Collection issues | Check invoice collection size, Verify pagination logic |
| Actions not processing | Event handling issues | Check event registration, Verify action handlers |

#### Data Persistence Issues

| Issue | Possible Causes | Solutions |
|-------|----------------|-----------|
| Invoices not saving | File permission issues, I/O errors | Check file permissions, Verify save process |
| Invoices not loading | File format issues, Missing file | Check file format, Verify file exists |
| Data corruption | Concurrent access, Server crashes | Implement transaction system, Regular backups |

### Diagnostic Tools

#### Console Commands

Use these commands to diagnose issues:

```
/invoices debug
```
Enables debug mode for the invoice system, providing detailed logs.

#### Log Analysis

Look for these patterns in logs:

- `[RPUniverse] [ERROR] Failed to save invoice data`: Indicates data persistence issues
- `[RPUniverse] [ERROR] Failed to initialize InvoiceModule`: Indicates module initialization issues
- `[RPUniverse] [DEBUG] Invoice payment: ...`: Shows payment transaction details

#### Performance Monitoring

Monitor these metrics:

- Invoice creation rate: Should be reasonable for your server size
- Payment processing time: Should be under 100ms
- Menu opening time: Should be under 200ms
- File save time: Should be under 500ms

## Best Practices

### Security

1. **Permission Management**: Carefully assign permissions to prevent abuse
2. **Input Validation**: All user input is validated, but monitor for exploitation attempts
3. **Transaction Verification**: Double-check all financial transactions
4. **Access Control**: Restrict sensitive operations to authorized users

### Performance

1. **Optimize Save Interval**: Balance between data safety and performance
2. **Limit Active Invoices**: Consider implementing a cap on active invoices per player
3. **Batch Processing**: Process multiple operations in batches when possible
4. **Asynchronous Operations**: Use async tasks for I/O operations

### Maintenance

1. **Regular Backups**: Implement automated backups of invoice data
2. **Data Cleanup**: Periodically archive or remove old invoices
3. **Version Updates**: Keep the plugin updated to the latest version
4. **Monitoring**: Set up alerts for unusual activity or errors

### Integration

1. **Plugin Dependencies**: Ensure all required plugins are properly installed
2. **Version Compatibility**: Check compatibility when updating other plugins
3. **API Usage**: Use the provided APIs rather than direct access to data
4. **Event Handling**: Listen for relevant events from other systems

## Extending the System

### Adding Custom Features

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

### API Documentation

The invoice system provides these key APIs for developers:

#### InvoiceManager API

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

#### Invoice API

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

## Conclusion

The RPUniverse invoice system is a powerful tool for managing player-to-player transactions. By understanding its architecture, integration points, and best practices, administrators can ensure a smooth and reliable experience for their players.

For further assistance, contact the plugin developers or refer to the source code documentation.
