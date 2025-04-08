# RPUniverse Invoice System - User Guide

This guide provides practical information for server administrators and players on how to use the RPUniverse invoice system. It includes command usage examples, configuration options, and permissions details.

## Command Usage Examples

### For Players

#### Viewing Your Invoices

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

#### Creating an Invoice

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

#### Paying an Invoice

1. Open the invoice menu with `/invoices`
2. Click on the invoice you want to pay
3. The payment will be processed automatically if you have enough money

#### Deleting an Invoice

1. Open the invoice menu with `/invoices created` to see invoices you've created
2. Shift-click on the invoice you want to delete
3. The invoice will be marked as deleted

Job bosses can also delete invoices for their job by using `/invoices job` and shift-clicking on invoices.

### For Administrators

#### Checking Player Invoices

Administrators with appropriate permissions can check any player's invoices by using the job filter:

```
/invoices job
```

## Configuration Guide

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

### Configuration Options Explained

#### enabled
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** Enables or disables the entire invoice module. Set to false to turn off all invoice functionality.
- **Example:** `enabled: false` will disable the invoice system completely.

#### maxDistance
- **Type:** Double (decimal number)
- **Default:** 5.0
- **Description:** The maximum distance (in blocks) between players for invoice creation. Players must be within this distance to create invoices for each other.
- **Example:** `maxDistance: 10.0` allows players to create invoices for others up to 10 blocks away.

#### mustSeePlayer
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** If true, players must have a clear line of sight to the target player to create an invoice. This prevents creating invoices through walls or obstacles.
- **Example:** `mustSeePlayer: false` allows creating invoices without seeing the target player.

#### defaultCurrency
- **Type:** String
- **Default:** "$"
- **Description:** The currency symbol to display in invoice amounts.
- **Example:** `defaultCurrency: "€"` will show amounts like "100€" instead of "$100".

#### saveInterval
- **Type:** Integer
- **Default:** 5
- **Description:** How often (in minutes) to automatically save invoice data to disk.
- **Example:** `saveInterval: 10` will save invoice data every 10 minutes.

#### notifyOnJoin
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** If true, players will be notified about pending invoices when they join the server.
- **Example:** `notifyOnJoin: false` will disable join notifications.

#### allowDecimal
- **Type:** Boolean (true/false)
- **Default:** true
- **Description:** If true, allows decimal amounts in invoices (e.g., 10.5). If false, only whole numbers are allowed.
- **Example:** `allowDecimal: false` will only allow whole number amounts like 10, 100, etc.

## Permissions

### Basic Permissions

- `rpu.invoices.use` - Allows using the invoice system (viewing invoices)
- `rpu.invoices.create` - Allows creating invoices for other players
- `rpu.invoices.delete.own` - Allows deleting invoices you've created
- `rpu.invoices.delete.job` - Allows job bosses to delete any invoice for their job
- `rpu.invoices.view.job` - Allows job bosses to view all invoices for their job

### Permission Examples

#### Regular Player
```yaml
permissions:
  - rpu.invoices.use
  - rpu.invoices.create
  - rpu.invoices.delete.own
```

This allows a regular player to view invoices, create invoices, and delete their own invoices.

#### Job Boss
```yaml
permissions:
  - rpu.invoices.use
  - rpu.invoices.create
  - rpu.invoices.delete.own
  - rpu.invoices.delete.job
  - rpu.invoices.view.job
```

This allows a job boss to view all invoices for their job and delete any invoice for their job.

#### Administrator
```yaml
permissions:
  - rpu.invoices.*
```

This gives an administrator all invoice-related permissions.

## Troubleshooting Common Issues

### "You don't have permission to use this command"
- Check that you have the appropriate permission (`rpu.invoices.use` for viewing, `rpu.invoices.create` for creating)
- Verify that the permissions are correctly assigned in your permissions plugin

### "Player is too far away to create an invoice"
- Move closer to the target player
- Check the `maxDistance` configuration setting

### "You must be able to see the player to create an invoice"
- Ensure you have a clear line of sight to the target player
- Check the `mustSeePlayer` configuration setting

### "You must be in a job to create an invoice"
- Join a job before trying to create an invoice
- Use `/job join <jobname>` to join a job

### "Invalid amount. Please enter a valid number"
- Ensure you're entering a valid number for the amount
- If using decimals, check that `allowDecimal` is set to true in the configuration

## Best Practices

1. **Set appropriate permissions:** Carefully assign permissions to control who can create and delete invoices
2. **Configure distance limits:** Set the `maxDistance` based on your server's roleplay style
3. **Regular backups:** The invoice data is stored in `plugins/RPUniverse/invoices.json` - back it up regularly
4. **Monitor usage:** Watch for players abusing the system by creating excessive invoices
5. **Educate players:** Make sure players understand how to use the invoice system properly