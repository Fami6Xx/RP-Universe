package me.fami6xx.rpuniverse.core.invoice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manager for the invoice system.
 * <p>
 * This class is responsible for managing all invoices in the system, including:
 * - Creating, retrieving, and deleting invoices
 * - Loading and saving invoice data using GSON
 * - Filtering invoices by player, job, or status
 * - Handling invoice payment logic
 * - Notifying players about invoice events
 */
public class InvoiceManager {

    private final InvoiceModule module;
    private RPUniverse plugin;
    private final Map<String, Invoice> invoices = new ConcurrentHashMap<>();
    private final Map<UUID, String> playerEditingInvoice = new HashMap<>(); // Maps player UUID to invoice ID being edited
    private final Set<String> jobsAllowedToCreateInvoices = new HashSet<>(); // Set of job UUIDs allowed to create invoices
    private final File dataFile;
    private final File jobConfigFile; // File to store job configuration
    private final Gson gson;

    /**
     * Creates a new InvoiceManager.
     *
     * @param module The InvoiceModule instance
     */
    public InvoiceManager(InvoiceModule module) {
        this.module = module;
        File invoiceDataFolder = new File(module.getPlugin().getDataFolder(), "invoices");
        if (!invoiceDataFolder.exists()) {
            try{
                invoiceDataFolder.mkdirs();
            } catch (SecurityException e) {
                ErrorHandler.severe("Failed to create invoice data folder", e);
            }
        }
        this.dataFile = new File(invoiceDataFolder, "invoices.json");
        this.jobConfigFile = new File(invoiceDataFolder, "job_config.json");
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Initializes the InvoiceManager.
     *
     * @param plugin The plugin instance
     */
    public void initialize(RPUniverse plugin) {
        this.plugin = plugin;

        // Create data directory if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Load data from file
        loadData();

        // Register player join listener for notifications
        plugin.getServer().getPluginManager().registerEvents(new InvoiceListener(this), plugin);

        ErrorHandler.debug("InvoiceManager initialized");
    }

    /**
     * Shuts down the InvoiceManager.
     */
    public void shutdown() {
        // Save data before shutting down
        saveData();

        ErrorHandler.debug("InvoiceManager shut down");
    }

    /**
     * Loads invoice data from file.
     */
    public void loadData() {
        if (!dataFile.exists()) {
            ErrorHandler.debug("Invoice data file does not exist, creating empty one");
            saveData();
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<List<Invoice>>() {
            }.getType();
            List<Invoice> loadedInvoices = gson.fromJson(reader, type);

            if (loadedInvoices != null) {
                invoices.clear();
                for (Invoice invoice : loadedInvoices) {
                    invoices.put(invoice.getId(), invoice);
                }
                ErrorHandler.debug("Loaded " + invoices.size() + " invoices from file");
            } else {
                ErrorHandler.debug("No invoices found in data file");
            }
        } catch (IOException e) {
            ErrorHandler.severe("Failed to load invoice data", e);
        } catch (com.google.gson.JsonSyntaxException e) {
            ErrorHandler.severe("Failed to parse invoice data file (invalid JSON format)", e);
            // Create a backup of the corrupted file
            try {
                File backupFile = new File(dataFile.getParentFile(), "invoices_backup_" + System.currentTimeMillis() + ".json");
                java.nio.file.Files.copy(dataFile.toPath(), backupFile.toPath());
                ErrorHandler.info("Created backup of corrupted invoice data file: " + backupFile.getName());
                // Create a new empty file
                saveData();
            } catch (IOException backupException) {
                ErrorHandler.severe("Failed to create backup of corrupted invoice data file", backupException);
            }
        }

        // Load job configuration
        loadJobConfiguration();
    }

    /**
     * Loads job configuration from file.
     */
    private void loadJobConfiguration() {
        if (!jobConfigFile.exists()) {
            ErrorHandler.debug("Job configuration file does not exist, creating empty one");
            saveJobConfiguration();
            return;
        }

        try (FileReader reader = new FileReader(jobConfigFile)) {
            Type type = new TypeToken<Set<String>>() {
            }.getType();
            Set<String> loadedJobUUIDs = gson.fromJson(reader, type);

            if (loadedJobUUIDs != null) {
                jobsAllowedToCreateInvoices.clear();
                jobsAllowedToCreateInvoices.addAll(loadedJobUUIDs);
                ErrorHandler.debug("Loaded " + jobsAllowedToCreateInvoices.size() + " jobs allowed to create invoices");
            } else {
                ErrorHandler.debug("No job configuration found in data file");
            }
        } catch (IOException e) {
            ErrorHandler.severe("Failed to load job configuration data", e);
        } catch (com.google.gson.JsonSyntaxException e) {
            ErrorHandler.severe("Failed to parse job configuration file (invalid JSON format)", e);
            // Create a backup of the corrupted file
            try {
                File backupFile = new File(jobConfigFile.getParentFile(), "invoice_job_config_backup_" + System.currentTimeMillis() + ".json");
                java.nio.file.Files.copy(jobConfigFile.toPath(), backupFile.toPath());
                ErrorHandler.info("Created backup of corrupted job configuration file: " + backupFile.getName());
                // Create a new empty file
                saveJobConfiguration();
            } catch (IOException backupException) {
                ErrorHandler.severe("Failed to create backup of corrupted job configuration file", backupException);
            }
        }
    }

    /**
     * Saves invoice data to file.
     */
    public void saveData() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            List<Invoice> invoiceList = new ArrayList<>(invoices.values());
            gson.toJson(invoiceList, writer);
            ErrorHandler.debug("Saved " + invoices.size() + " invoices to file");
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save invoice data", e);
        }

        // Save job configuration
        saveJobConfiguration();
    }

    /**
     * Saves job configuration to file.
     */
    private void saveJobConfiguration() {
        try (FileWriter writer = new FileWriter(jobConfigFile)) {
            gson.toJson(jobsAllowedToCreateInvoices, writer);
            ErrorHandler.debug("Saved " + jobsAllowedToCreateInvoices.size() + " jobs allowed to create invoices");
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save job configuration data", e);
        }
    }

    /**
     * Checks if a job is allowed to create invoices.
     *
     * @param jobUUID The UUID of the job to check
     * @return true if the job is allowed to create invoices, false otherwise
     */
    public boolean isJobAllowedToCreateInvoices(String jobUUID) {
        // If no jobs are configured, allow all jobs to create invoices
        if (jobsAllowedToCreateInvoices.isEmpty()) {
            return true;
        }
        return jobsAllowedToCreateInvoices.contains(jobUUID);
    }

    /**
     * Adds a job to the list of jobs allowed to create invoices.
     *
     * @param jobUUID The UUID of the job to add
     * @return true if the job was added, false if it was already in the list
     */
    public boolean addJobAllowedToCreateInvoices(String jobUUID) {
        boolean added = jobsAllowedToCreateInvoices.add(jobUUID);
        if (added) {
            saveJobConfiguration();
            ErrorHandler.debug("Added job " + jobUUID + " to list of jobs allowed to create invoices");
        }
        return added;
    }

    /**
     * Removes a job from the list of jobs allowed to create invoices.
     *
     * @param jobUUID The UUID of the job to remove
     * @return true if the job was removed, false if it wasn't in the list
     */
    public boolean removeJobAllowedToCreateInvoices(String jobUUID) {
        boolean removed = jobsAllowedToCreateInvoices.remove(jobUUID);
        if (removed) {
            saveJobConfiguration();
            ErrorHandler.debug("Removed job " + jobUUID + " from list of jobs allowed to create invoices");
        }
        return removed;
    }

    /**
     * Gets all jobs allowed to create invoices.
     *
     * @return A set of job UUIDs allowed to create invoices
     */
    public Set<String> getJobsAllowedToCreateInvoices() {
        return Collections.unmodifiableSet(jobsAllowedToCreateInvoices);
    }

    /**
     * Creates a new invoice.
     *
     * @param job     The job UUID the invoice is created from
     * @param creator The UUID of the player who created the invoice
     * @param target  The UUID of the player the invoice is assigned to
     * @param amount  The amount to be paid
     * @return The created invoice
     */
    public Invoice createInvoice(String job, UUID creator, UUID target, double amount) {
        Invoice invoice = new Invoice(job, creator, target, amount);
        invoices.put(invoice.getId(), invoice);

        ErrorHandler.debug("Invoice created: ID=" + invoice.getId() + ", Job UUID=" + job +
                ", Creator=" + creator + ", Target=" + target + ", Amount=" + amount);

        // Schedule async save
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskAsynchronously(plugin);

        // Notify the target player if they're online
        Player targetPlayer = Bukkit.getPlayer(target);
        if (targetPlayer != null && targetPlayer.isOnline()) {
            notifyInvoiceReceived(targetPlayer, invoice);
        }

        return invoice;
    }

    /**
     * Gets an invoice by its ID.
     *
     * @param id The invoice ID
     * @return The invoice, or null if not found
     */
    public Invoice getInvoice(String id) {
        return invoices.get(id);
    }

    /**
     * Gets all invoices in the system.
     *
     * @return A collection of all invoices
     */
    public Collection<Invoice> getAllInvoices() {
        return Collections.unmodifiableCollection(invoices.values());
    }

    /**
     * Gets all invoices created by a specific player.
     *
     * @param creator The UUID of the creator
     * @return A list of invoices created by the player
     */
    public List<Invoice> getInvoicesByCreator(UUID creator) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getCreator().equals(creator))
                .collect(Collectors.toList());
    }

    /**
     * Gets all invoices assigned to a specific player.
     *
     * @param target The UUID of the target
     * @return A list of invoices assigned to the player
     */
    public List<Invoice> getInvoicesByTarget(UUID target) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getTarget().equals(target))
                .collect(Collectors.toList());
    }

    /**
     * Gets all invoices for a specific job.
     *
     * @param jobUUID The job UUID
     * @return A list of invoices for the job
     */
    public List<Invoice> getInvoicesByJob(String jobUUID) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getJob().equals(jobUUID))
                .collect(Collectors.toList());
    }

    /**
     * Gets all invoices with a specific status.
     *
     * @param status The invoice status
     * @return A list of invoices with the status
     */
    public List<Invoice> getInvoicesByStatus(Invoice.Status status) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Gets all pending invoices assigned to a specific player.
     *
     * @param target The UUID of the target
     * @return A list of pending invoices assigned to the player
     */
    public List<Invoice> getPendingInvoicesByTarget(UUID target) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getTarget().equals(target) && invoice.isPending())
                .collect(Collectors.toList());
    }

    /**
     * Processes payment for an invoice.
     *
     * @param invoice The invoice to pay
     * @param player  The player making the payment
     * @return true if the payment was successful, false otherwise
     */
    public boolean payInvoice(Invoice invoice, Player player) {
        if (invoice == null || !invoice.isPending()) {
            return false;
        }

        // Check if the player is the target of the invoice
        if (!invoice.getTarget().equals(player.getUniqueId())) {
            return false;
        }

        // Get the economy instance
        net.milkbowl.vault.economy.Economy economy = plugin.getEconomy();

        // Check if the player has enough money
        double amount = invoice.getAmount();
        if (!economy.has(player, amount)) {
            // Notify the player that they don't have enough money
            String message = InvoiceLanguage.getInstance().errorNotEnoughMoneyMessage;
            message = message.replace("{amount}", String.valueOf(amount))
                    .replace("{currency}", module.getDefaultCurrency());
            player.sendMessage(FamiUtils.formatWithPrefix(message));
            return false;
        }

        // Get the job
        Job job = Job.getJobByUUID(invoice.getJob());
        if (job == null) {
            ErrorHandler.debug("Pay invoice failed: job not found for UUID " + invoice.getJob());
            // Notify the player that the job doesn't exist
            player.sendMessage(FamiUtils.formatWithPrefix(InvoiceLanguage.getInstance().errorPayingInvoiceMessage));
            return false;
        }

        // Transfer the money
        economy.withdrawPlayer(player, amount);
        job.addMoneyToJobBank(amount);

        // Log the transaction
        ErrorHandler.debug("Invoice payment: " + player.getName() + " paid " + amount + " to job bank " +
                job.getName() + " for invoice " + invoice.getId());

        // Mark the invoice as paid
        invoice.markAsPaid();

        // Schedule async save
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskAsynchronously(plugin);

        // Notify the creator if they're online
        Player creatorPlayer = Bukkit.getPlayer(invoice.getCreator());
        if (creatorPlayer != null && creatorPlayer.isOnline()) {
            notifyInvoicePaid(creatorPlayer, invoice);
        }

        // Notify the player who paid the invoice
        player.sendMessage(FamiUtils.formatWithPrefix(InvoiceLanguage.getInstance().invoicePaidMessage
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(amount))
                .replace("{currency}", module.getDefaultCurrency())));

        return true;
    }

    /**
     * Deletes an invoice.
     *
     * @param invoice The invoice to delete
     * @param player  The player deleting the invoice
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteInvoice(Invoice invoice, Player player) {
        if (invoice == null || invoice.isDeleted()) {
            ErrorHandler.debug("Delete invoice failed: invoice is null or already deleted");
            return false;
        }

        // Check if the player is the creator of the invoice or has permission to delete job invoices
        boolean isCreator = invoice.getCreator().equals(player.getUniqueId());
        boolean hasJobPermission = isPlayerJobBoss(player, invoice.getJob());

        if (!isCreator && !hasJobPermission) {
            ErrorHandler.debug("Delete invoice failed: player " + player.getName() +
                    " is not creator and doesn't have job permission for invoice " + invoice.getId());
            return false;
        }

        invoice.markAsDeleted();
        ErrorHandler.debug("Invoice deleted: ID=" + invoice.getId() + ", by player=" + player.getName());

        // Schedule async save
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskAsynchronously(plugin);

        // Notify the target if they're online
        Player targetPlayer = Bukkit.getPlayer(invoice.getTarget());
        if (targetPlayer != null && targetPlayer.isOnline()) {
            notifyInvoiceDeleted(targetPlayer, invoice);
        }

        return true;
    }

    /**
     * Checks if a player is a boss in a specific job.
     *
     * @param player  The player to check
     * @param jobUUID The job UUID
     * @return true if the player is a boss in the job, false otherwise
     */
    private boolean isPlayerJobBoss(Player player, String jobUUID) {
        // Get the job by UUID
        Job job = Job.getJobByUUID(jobUUID);
        if (job != null) {
            // Check if the player is in the job
            if (job.isPlayerInJob(player.getUniqueId())) {
                // Get the player's position in the job
                Position position = job.getPlayerPosition(player.getUniqueId());
                if (position != null) {
                    // Check if the position is a boss position
                    return position.isBoss();
                }
            }
        }
        return false;
    }

    /**
     * Notifies a player that they have received an invoice.
     *
     * @param player  The player to notify
     * @param invoice The invoice they received
     */
    private void notifyInvoiceReceived(Player player, Invoice invoice) {
        // Use the language system for the message
        String message = InvoiceLanguage.getInstance().invoiceReceivedMessage;

        // Replace placeholders
        message = message.replace("{job}", invoice.getJobName())
                .replace("{amount}", String.valueOf(invoice.getAmount()))
                .replace("{currency}", module.getDefaultCurrency());

        player.sendMessage(FamiUtils.formatWithPrefix(message));

        ErrorHandler.debug("Player " + player.getName() + " notified about received invoice: ID=" + invoice.getId());
    }

    /**
     * Notifies a player that their invoice has been paid.
     *
     * @param player  The player to notify
     * @param invoice The invoice that was paid
     */
    private void notifyInvoicePaid(Player player, Invoice invoice) {
        // Use the language system for the message
        String message = InvoiceLanguage.getInstance().invoicePaidMessage;

        // Replace placeholders
        String targetName = Bukkit.getOfflinePlayer(invoice.getTarget()).getName();
        message = message.replace("{player}", targetName != null ? targetName : "Unknown")
                .replace("{amount}", String.valueOf(invoice.getAmount()))
                .replace("{currency}", module.getDefaultCurrency());

        player.sendMessage(FamiUtils.formatWithPrefix(message));

        ErrorHandler.debug("Player " + player.getName() + " notified about paid invoice: ID=" + invoice.getId());
    }

    /**
     * Notifies a player that their invoice has been deleted.
     *
     * @param player  The player to notify
     * @param invoice The invoice that was deleted
     */
    private void notifyInvoiceDeleted(Player player, Invoice invoice) {
        // Use the language system for the message
        String message = InvoiceLanguage.getInstance().invoiceDeletedMessage;

        // Replace placeholders
        message = message.replace("{job}", invoice.getJobName())
                .replace("{amount}", String.valueOf(invoice.getAmount()))
                .replace("{currency}", module.getDefaultCurrency());

        player.sendMessage(FamiUtils.formatWithPrefix(message));

        ErrorHandler.debug("Player " + player.getName() + " notified about deleted invoice: ID=" + invoice.getId());
    }

    /**
     * Gets the InvoiceModule instance.
     *
     * @return The InvoiceModule instance
     */
    public InvoiceModule getModule() {
        return module;
    }

    /**
     * Gets the plugin instance.
     *
     * @return The plugin instance
     */
    public RPUniverse getPlugin() {
        return plugin;
    }

    /**
     * Force deletes all invoices created for a specific job.
     * This completely removes them from memory and data storage.
     *
     * @param jobUUID The UUID of the job
     * @return The number of invoices deleted
     */
    public int forceDeleteInvoicesByJob(String jobUUID) {
        if (jobUUID == null || jobUUID.isEmpty()) {
            ErrorHandler.debug("Force delete invoices by job failed: job UUID is null or empty");
            return 0;
        }

        // Get all invoices for the job
        List<String> invoiceIdsToRemove = invoices.values().stream()
                .filter(invoice -> invoice.getJob().equals(jobUUID))
                .map(Invoice::getId)
                .collect(Collectors.toList());

        // Remove the invoices from the map
        int count = 0;
        for (String id : invoiceIdsToRemove) {
            invoices.remove(id);
            count++;
        }

        if (count > 0) {
            ErrorHandler.debug("Force deleted " + count + " invoices for job UUID: " + jobUUID);

            // Schedule async save
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveData();
                }
            }.runTaskAsynchronously(plugin);
        }

        return count;
    }

    /**
     * Force deletes all invoices created by or assigned to a specific player.
     * This completely removes them from memory and data storage.
     *
     * @param playerUUID The UUID of the player
     * @return The number of invoices deleted
     */
    public int forceDeleteInvoicesByPlayer(UUID playerUUID) {
        if (playerUUID == null) {
            ErrorHandler.debug("Force delete invoices by player failed: player UUID is null");
            return 0;
        }

        // Get all invoices created by or assigned to the player
        List<String> invoiceIdsToRemove = invoices.values().stream()
                .filter(invoice -> invoice.getCreator().equals(playerUUID) || invoice.getTarget().equals(playerUUID))
                .map(Invoice::getId)
                .collect(Collectors.toList());

        // Remove the invoices from the map
        int count = 0;
        for (String id : invoiceIdsToRemove) {
            invoices.remove(id);
            count++;
        }

        if (count > 0) {
            ErrorHandler.debug("Force deleted " + count + " invoices for player UUID: " + playerUUID);

            // Schedule async save
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveData();
                }
            }.runTaskAsynchronously(plugin);
        }

        return count;
    }

    /**
     * Force pays an invoice as an administrator.
     * This marks the invoice as paid without requiring the target player to have the funds.
     *
     * @param invoice The invoice to force pay
     * @param admin   The administrator forcing the payment
     * @return true if the payment was successful, false otherwise
     */
    public boolean forcePayInvoice(Invoice invoice, Player admin) {
        if (invoice == null || !invoice.isPending()) {
            ErrorHandler.debug("Force pay invoice failed: invoice is null or not pending");
            return false;
        }

        // Mark the invoice as paid
        invoice.markAsPaid();
        ErrorHandler.debug("Invoice force paid: ID=" + invoice.getId() + ", by admin=" + admin.getName());

        // Log the admin action
        logAdminAction(admin, "force_pay", invoice.getId());

        // Schedule async save
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskAsynchronously(plugin);

        // Notify the creator if they're online
        Player creatorPlayer = Bukkit.getPlayer(invoice.getCreator());
        if (creatorPlayer != null && creatorPlayer.isOnline()) {
            notifyInvoicePaid(creatorPlayer, invoice);
        }

        // Notify the target if they're online
        Player targetPlayer = Bukkit.getPlayer(invoice.getTarget());
        if (targetPlayer != null && targetPlayer.isOnline()) {
            String message = InvoiceLanguage.getInstance().adminInvoiceForcePaidNotificationMessage;
            message = message.replace("{id}", invoice.getId());
            targetPlayer.sendMessage(FamiUtils.formatWithPrefix(message));
        }

        return true;
    }

    /**
     * Restores a deleted invoice.
     *
     * @param invoice The invoice to restore
     * @param admin   The administrator restoring the invoice
     * @return true if the restoration was successful, false otherwise
     */
    public boolean restoreInvoice(Invoice invoice, Player admin) {
        if (invoice == null || !invoice.isDeleted()) {
            ErrorHandler.debug("Restore invoice failed: invoice is null or not deleted");
            return false;
        }

        // Set the status back to pending
        invoice.setStatus(Invoice.Status.PENDING);
        ErrorHandler.debug("Invoice restored: ID=" + invoice.getId() + ", by admin=" + admin.getName());

        // Log the admin action
        logAdminAction(admin, "restore", invoice.getId());

        // Schedule async save
        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskAsynchronously(plugin);

        // Notify the target if they're online
        Player targetPlayer = Bukkit.getPlayer(invoice.getTarget());
        if (targetPlayer != null && targetPlayer.isOnline()) {
            String message = InvoiceLanguage.getInstance().adminInvoiceRestoredNotificationMessage;
            message = message.replace("{id}", invoice.getId());
            targetPlayer.sendMessage(FamiUtils.formatWithPrefix(message));
        }

        return true;
    }

    /**
     * Deletes multiple invoices at once.
     *
     * @param invoiceIds The IDs of the invoices to delete
     * @param admin      The administrator deleting the invoices
     * @return The number of invoices successfully deleted
     */
    public int bulkDeleteInvoices(List<String> invoiceIds, Player admin) {
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            ErrorHandler.debug("Bulk delete invoices failed: invoice IDs list is null or empty");
            return 0;
        }

        int count = 0;
        for (String id : invoiceIds) {
            Invoice invoice = getInvoice(id);
            if (invoice != null && !invoice.isDeleted()) {
                invoice.markAsDeleted();
                logAdminAction(admin, "delete", id);
                count++;

                // Notify the target if they're online
                Player targetPlayer = Bukkit.getPlayer(invoice.getTarget());
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    String message = InvoiceLanguage.getInstance().adminInvoiceDeletedNotificationMessage;
                    message = message.replace("{id}", invoice.getId());
                    targetPlayer.sendMessage(FamiUtils.formatWithPrefix(message));
                }
            }
        }

        if (count > 0) {
            ErrorHandler.debug("Bulk deleted " + count + " invoices by admin=" + admin.getName());

            // Schedule async save
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveData();
                }
            }.runTaskAsynchronously(plugin);
        }

        return count;
    }

    /**
     * Force pays multiple invoices at once.
     *
     * @param invoiceIds The IDs of the invoices to force pay
     * @param admin      The administrator forcing the payments
     * @return The number of invoices successfully paid
     */
    public int bulkForcePayInvoices(List<String> invoiceIds, Player admin) {
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            ErrorHandler.debug("Bulk force pay invoices failed: invoice IDs list is null or empty");
            return 0;
        }

        int count = 0;
        for (String id : invoiceIds) {
            Invoice invoice = getInvoice(id);
            if (invoice != null && invoice.isPending()) {
                invoice.markAsPaid();
                logAdminAction(admin, "force_pay", id);
                count++;

                // Notify the creator if they're online
                Player creatorPlayer = Bukkit.getPlayer(invoice.getCreator());
                if (creatorPlayer != null && creatorPlayer.isOnline()) {
                    notifyInvoicePaid(creatorPlayer, invoice);
                }

                // Notify the target if they're online
                Player targetPlayer = Bukkit.getPlayer(invoice.getTarget());
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    String message = InvoiceLanguage.getInstance().adminInvoiceForcePaidNotificationMessage;
                    message = message.replace("{id}", invoice.getId());
                    targetPlayer.sendMessage(FamiUtils.formatWithPrefix(message));
                }
            }
        }

        if (count > 0) {
            ErrorHandler.debug("Bulk force paid " + count + " invoices by admin=" + admin.getName());

            // Schedule async save
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveData();
                }
            }.runTaskAsynchronously(plugin);
        }

        return count;
    }

    /**
     * Starts the process of editing an invoice's amount.
     * <p>
     * This method registers the player as editing the specified invoice and
     * sets up a pending action to capture the new amount.
     *
     * @param admin   The administrator editing the invoice
     * @param invoice The invoice to edit
     * @return true if the edit process was started successfully, false otherwise
     */
    public boolean startInvoiceAmountEdit(Player admin, Invoice invoice) {
        if (invoice == null) {
            ErrorHandler.debug("Start invoice amount edit failed: invoice is null");
            return false;
        }

        if (invoice.isPaid()) {
            ErrorHandler.debug("Start invoice amount edit failed: invoice is already paid");
            return false;
        }

        if (invoice.isDeleted()) {
            ErrorHandler.debug("Start invoice amount edit failed: invoice is deleted");
            return false;
        }

        // Register the player as editing this invoice
        playerEditingInvoice.put(admin.getUniqueId(), invoice.getId());

        // Set up a pending action for the next message from this player
        PlayerMenu playerMenu = RPUniverse.getInstance().getMenuManager().getPlayerMenu(admin);
        playerMenu.setPendingAction(new Consumer<String>() {
            @Override
            public void accept(String input) {
                try {
                    double newAmount = Double.parseDouble(input);
                    if (newAmount <= 0) {
                        admin.sendMessage(FamiUtils.formatWithPrefix(InvoiceLanguage.getInstance().errorAmountMustBePositiveMessage));
                        playerEditingInvoice.remove(admin.getUniqueId());
                        return;
                    }

                    // Edit the invoice with the new amount
                    if (editInvoiceAmount(invoice, newAmount, admin)) {
                        admin.sendMessage(FamiUtils.formatWithPrefix(InvoiceLanguage.getInstance().adminInvoiceEditedMessage.replace("{id}", invoice.getId())));
                    } else {
                        admin.sendMessage(FamiUtils.formatWithPrefix(InvoiceLanguage.getInstance().adminErrorEditingInvoiceMessage));
                    }
                } catch (NumberFormatException e) {
                    admin.sendMessage(FamiUtils.formatWithPrefix(InvoiceLanguage.getInstance().errorInvalidAmountMessage));
                } finally {
                    playerEditingInvoice.remove(admin.getUniqueId());
                }
            }
        });

        return true;
    }

    /**
     * Edits an invoice's amount.
     * <p>
     * Since the amount field is final, this method creates a new invoice with the updated amount
     * and replaces the old invoice in the invoices map.
     *
     * @param invoice   The invoice to edit
     * @param newAmount The new amount for the invoice
     * @param admin     The administrator editing the invoice
     * @return true if the edit was successful, false otherwise
     */
    public boolean editInvoiceAmount(Invoice invoice, double newAmount, Player admin) {
        if (invoice == null) {
            ErrorHandler.debug("Edit invoice amount failed: invoice is null");
            return false;
        }

        if (invoice.isPaid()) {
            ErrorHandler.debug("Edit invoice amount failed: invoice is already paid");
            return false;
        }

        if (invoice.isDeleted()) {
            ErrorHandler.debug("Edit invoice amount failed: invoice is deleted");
            return false;
        }

        try {
            // Create a new invoice with the updated amount
            Invoice newInvoice = new Invoice(invoice.getJob(), invoice.getCreator(), invoice.getTarget(), newAmount);

            // Copy the ID and status from the old invoice
            // Note: This is a hack since we can't modify the ID directly
            java.lang.reflect.Field idField = Invoice.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(newInvoice, invoice.getId());

            // Set the status to match the original invoice
            newInvoice.setStatus(invoice.getStatus());

            // Replace the old invoice in the map
            invoices.put(invoice.getId(), newInvoice);

            // Log the admin action
            logAdminAction(admin, "edit_amount", invoice.getId());

            // Schedule async save
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveData();
                }
            }.runTaskAsynchronously(plugin);

            // Notify the target if they're online
            Player targetPlayer = Bukkit.getPlayer(invoice.getTarget());
            if (targetPlayer != null && targetPlayer.isOnline()) {
                String message = InvoiceLanguage.getInstance().adminInvoiceEditedNotificationMessage;
                message = message.replace("{id}", invoice.getId());
                targetPlayer.sendMessage(FamiUtils.formatWithPrefix(message));
            }

            ErrorHandler.debug("Invoice amount edited: ID=" + invoice.getId() + ", new amount=" + newAmount + ", by admin=" + admin.getName());
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Error editing invoice amount", e);
            return false;
        }
    }

    /**
     * Logs an administrative action.
     *
     * @param admin     The administrator who performed the action
     * @param action    The action performed (e.g., "edit", "delete", "restore", "force_pay")
     * @param invoiceId The ID of the invoice the action was performed on
     */
    public void logAdminAction(Player admin, String action, String invoiceId) {
        ErrorHandler.info("Admin action: " + admin.getName() + " performed " + action + " on invoice " + invoiceId);

        // Notify online admins with the appropriate permission
        String message = InvoiceLanguage.getInstance().adminActionLoggedMessage;
        message = message.replace("{action}", action)
                .replace("{admin}", admin.getName())
                .replace("{id}", invoiceId);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("rpu.invoices.admin.logs")) {
                player.sendMessage(FamiUtils.formatWithPrefix(message));
            }
        }
    }

    /**
     * Creates a manual backup of the invoice data.
     *
     * @param admin The administrator creating the backup
     * @return The name of the backup file, or null if the backup failed
     */
    public String createDataBackup(Player admin) {
        try {
            String backupFileName = "invoices_backup_" + System.currentTimeMillis() + ".json";
            File backupFile = new File(plugin.getDataFolder(), backupFileName);

            // Save current data to the backup file
            try (FileWriter writer = new FileWriter(backupFile)) {
                List<Invoice> invoiceList = new ArrayList<>(invoices.values());
                gson.toJson(invoiceList, writer);
            }

            ErrorHandler.info("Manual backup created by " + admin.getName() + ": " + backupFileName);
            logAdminAction(admin, "create_backup", "N/A");

            return backupFileName;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to create manual backup", e);
            return null;
        }
    }

    /**
     * Restores invoice data from a backup file.
     *
     * @param backupFileName The name of the backup file to restore from
     * @param admin          The administrator performing the restoration
     * @return true if the restoration was successful, false otherwise
     */
    public boolean restoreFromBackup(String backupFileName, Player admin) {
        File backupFile = new File(plugin.getDataFolder(), backupFileName);
        if (!backupFile.exists()) {
            ErrorHandler.debug("Restore from backup failed: backup file does not exist: " + backupFileName);
            return false;
        }

        try (FileReader reader = new FileReader(backupFile)) {
            Type type = new TypeToken<List<Invoice>>() {
            }.getType();
            List<Invoice> loadedInvoices = gson.fromJson(reader, type);

            if (loadedInvoices != null) {
                // Create a backup of the current data before restoring
                createDataBackup(admin);

                // Clear current invoices and load from backup
                invoices.clear();
                for (Invoice invoice : loadedInvoices) {
                    invoices.put(invoice.getId(), invoice);
                }

                // Save the restored data
                saveData();

                ErrorHandler.info("Data restored from backup by " + admin.getName() + ": " + backupFileName);
                logAdminAction(admin, "restore_from_backup", backupFileName);

                return true;
            } else {
                ErrorHandler.debug("Restore from backup failed: no invoices found in backup file: " + backupFileName);
                return false;
            }
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            ErrorHandler.severe("Failed to restore from backup: " + backupFileName, e);
            return false;
        }
    }

    /**
     * Clears old or invalid invoices from the system.
     *
     * @param daysOld The minimum age in days for invoices to be considered old
     * @param admin   The administrator performing the cleanup
     * @return The number of invoices cleared
     */
    public int clearOldInvoices(int daysOld, Player admin) {
        if (daysOld <= 0) {
            ErrorHandler.debug("Clear old invoices failed: daysOld must be positive");
            return 0;
        }

        // Calculate the cutoff date
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L);
        Date cutoffDate = new Date(cutoffTime);

        // Get all old invoices
        List<String> invoiceIdsToRemove = invoices.values().stream()
                .filter(invoice -> invoice.getCreationDate().before(cutoffDate))
                .map(Invoice::getId)
                .collect(Collectors.toList());

        // Remove the invoices from the map
        int count = 0;
        for (String id : invoiceIdsToRemove) {
            invoices.remove(id);
            count++;
        }

        if (count > 0) {
            ErrorHandler.info("Cleared " + count + " old invoices (older than " + daysOld + " days) by admin=" + admin.getName());
            logAdminAction(admin, "clear_old_invoices", "count=" + count);

            // Schedule async save
            new BukkitRunnable() {
                @Override
                public void run() {
                    saveData();
                }
            }.runTaskAsynchronously(plugin);
        }

        return count;
    }
}
