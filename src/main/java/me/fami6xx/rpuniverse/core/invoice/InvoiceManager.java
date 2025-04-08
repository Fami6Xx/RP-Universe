package me.fami6xx.rpuniverse.core.invoice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
    private final File dataFile;
    private final Gson gson;
    
    /**
     * Creates a new InvoiceManager.
     * 
     * @param module The InvoiceModule instance
     */
    public InvoiceManager(InvoiceModule module) {
        this.module = module;
        this.dataFile = new File(module.getPlugin().getDataFolder(), "invoices.json");
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
        // TODO: Implement InvoiceListener class for join notifications
        // plugin.getServer().getPluginManager().registerEvents(new InvoiceListener(this), plugin);
        
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
            Type type = new TypeToken<List<Invoice>>(){}.getType();
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
    }
    
    /**
     * Creates a new invoice.
     *
     * @param job     The job the invoice is created from
     * @param creator The UUID of the player who created the invoice
     * @param target  The UUID of the player the invoice is assigned to
     * @param amount  The amount to be paid
     * @return The created invoice
     */
    public Invoice createInvoice(String job, UUID creator, UUID target, double amount) {
        Invoice invoice = new Invoice(job, creator, target, amount);
        invoices.put(invoice.getId(), invoice);
        
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
     * @param job The job name
     * @return A list of invoices for the job
     */
    public List<Invoice> getInvoicesByJob(String job) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getJob().equals(job))
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
        
        // TODO: Integrate with economy system to check balance and transfer money
        // For now, just mark as paid
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
            return false;
        }
        
        // Check if the player is the creator of the invoice or has permission to delete job invoices
        boolean isCreator = invoice.getCreator().equals(player.getUniqueId());
        boolean hasJobPermission = player.hasPermission("rpu.invoices.delete.job") && 
                                   isPlayerJobBoss(player, invoice.getJob());
        
        if (!isCreator && !hasJobPermission) {
            return false;
        }
        
        invoice.markAsDeleted();
        
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
     * @param player The player to check
     * @param job    The job name
     * @return true if the player is a boss in the job, false otherwise
     */
    private boolean isPlayerJobBoss(Player player, String job) {
        // TODO: Integrate with job system to check if player is a boss
        // For now, just check the permission
        return player.hasPermission("rpu.invoices.view.job");
    }
    
    /**
     * Notifies a player that they have received an invoice.
     *
     * @param player  The player to notify
     * @param invoice The invoice they received
     */
    private void notifyInvoiceReceived(Player player, Invoice invoice) {
        // TODO: Implement language system integration for messages
        player.sendMessage("§aYou have received an invoice from " + invoice.getJob() + 
                " for " + invoice.getAmount() + module.getDefaultCurrency());
    }
    
    /**
     * Notifies a player that their invoice has been paid.
     *
     * @param player  The player to notify
     * @param invoice The invoice that was paid
     */
    private void notifyInvoicePaid(Player player, Invoice invoice) {
        // TODO: Implement language system integration for messages
        player.sendMessage("§aYour invoice for " + invoice.getAmount() + module.getDefaultCurrency() + 
                " has been paid by " + Bukkit.getOfflinePlayer(invoice.getTarget()).getName());
    }
    
    /**
     * Notifies a player that their invoice has been deleted.
     *
     * @param player  The player to notify
     * @param invoice The invoice that was deleted
     */
    private void notifyInvoiceDeleted(Player player, Invoice invoice) {
        // TODO: Implement language system integration for messages
        player.sendMessage("§cAn invoice from " + invoice.getJob() + 
                " for " + invoice.getAmount() + module.getDefaultCurrency() + " has been deleted");
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
}