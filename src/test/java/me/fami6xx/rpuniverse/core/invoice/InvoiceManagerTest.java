package me.fami6xx.rpuniverse.core.invoice;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.Position;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import net.milkbowl.vault.economy.Economy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the InvoiceManager class.
 */
public class InvoiceManagerTest {

    private static final String TEST_JOB = "TestJob";
    private static final UUID TEST_CREATOR = UUID.randomUUID();
    private static final UUID TEST_TARGET = UUID.randomUUID();
    private static final double TEST_AMOUNT = 100.0;

    @Mock
    private InvoiceModule mockModule;

    @Mock
    private RPUniverse mockPlugin;

    @Mock
    private Player mockCreatorPlayer;

    @Mock
    private Player mockTargetPlayer;

    @Mock
    private Economy mockEconomy;

    @Mock
    private ServicesManager mockServicesManager;

    @Mock
    private RegisteredServiceProvider<Economy> mockEconomyProvider;

    private InvoiceManager invoiceManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock behavior
        when(mockCreatorPlayer.getUniqueId()).thenReturn(TEST_CREATOR);
        when(mockCreatorPlayer.getName()).thenReturn("TestCreator");
        when(mockTargetPlayer.getUniqueId()).thenReturn(TEST_TARGET);
        when(mockTargetPlayer.getName()).thenReturn("TestTarget");

        // Mock Bukkit.getPlayer
        mockStatic(Bukkit.class);
        when(Bukkit.getPlayer(TEST_CREATOR)).thenReturn(mockCreatorPlayer);
        when(Bukkit.getPlayer(TEST_TARGET)).thenReturn(mockTargetPlayer);

        // Mock plugin and module
        when(mockModule.getPlugin()).thenReturn(mockPlugin);
        when(mockPlugin.getServer()).thenReturn(mock(org.bukkit.Server.class));
        when(mockPlugin.getServer().getServicesManager()).thenReturn(mockServicesManager);

        // Mock economy
        when(mockServicesManager.getRegistration(Economy.class)).thenReturn(mockEconomyProvider);
        when(mockEconomyProvider.getProvider()).thenReturn(mockEconomy);

        // Create the invoice manager
        invoiceManager = new InvoiceManager(mockModule);
    }

    @Test
    public void testCreateInvoice() {
        // Test invoice creation
        Invoice invoice = invoiceManager.createInvoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);

        // Verify the invoice was created with the correct properties
        assertNotNull(invoice, "Invoice should not be null");
        assertEquals(TEST_JOB, invoice.getJob(), "Job should match the provided value");
        assertEquals(TEST_CREATOR, invoice.getCreator(), "Creator UUID should match the provided value");
        assertEquals(TEST_TARGET, invoice.getTarget(), "Target UUID should match the provided value");
        assertEquals(TEST_AMOUNT, invoice.getAmount(), "Amount should match the provided value");
        assertEquals(Invoice.Status.PENDING, invoice.getStatus(), "Initial status should be PENDING");
    }

    @Test
    public void testPayInvoice() {
        // Create a test invoice
        Invoice invoice = new Invoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);

        // Mock economy checks
        when(mockEconomy.has(mockTargetPlayer, TEST_AMOUNT)).thenReturn(true);
        when(mockEconomy.withdrawPlayer(mockTargetPlayer, TEST_AMOUNT)).thenReturn(null);
        when(mockEconomy.depositPlayer(mockCreatorPlayer, TEST_AMOUNT)).thenReturn(null);

        // Add the invoice to the manager's collection
        invoiceManager.getAllInvoices().add(invoice);

        // Test paying the invoice
        boolean result = invoiceManager.payInvoice(invoice, mockTargetPlayer);

        // Verify the result and invoice status
        assertTrue(result, "Payment should be successful");
        assertEquals(Invoice.Status.PAID, invoice.getStatus(), "Invoice status should be PAID");

        // Verify economy interactions
        verify(mockEconomy).has(mockTargetPlayer, TEST_AMOUNT);
        verify(mockEconomy).withdrawPlayer(mockTargetPlayer, TEST_AMOUNT);
        verify(mockEconomy).depositPlayer(mockCreatorPlayer, TEST_AMOUNT);
    }

    @Test
    public void testPayInvoiceInsufficientFunds() {
        // Create a test invoice
        Invoice invoice = new Invoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);

        // Mock economy checks - insufficient funds
        when(mockEconomy.has(mockTargetPlayer, TEST_AMOUNT)).thenReturn(false);

        // Add the invoice to the manager's collection
        invoiceManager.getAllInvoices().add(invoice);

        // Test paying the invoice with insufficient funds
        boolean result = invoiceManager.payInvoice(invoice, mockTargetPlayer);

        // Verify the result and invoice status
        assertFalse(result, "Payment should fail due to insufficient funds");
        assertEquals(Invoice.Status.PENDING, invoice.getStatus(), "Invoice status should remain PENDING");

        // Verify economy interactions
        verify(mockEconomy).has(mockTargetPlayer, TEST_AMOUNT);
        verify(mockEconomy, never()).withdrawPlayer(any(Player.class), anyDouble());
        verify(mockEconomy, never()).depositPlayer(any(Player.class), anyDouble());
    }

    @Test
    public void testDeleteInvoice() {
        // Create a test invoice
        Invoice invoice = new Invoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);

        // Add the invoice to the manager's collection
        invoiceManager.getAllInvoices().add(invoice);

        // Test deleting the invoice
        boolean result = invoiceManager.deleteInvoice(invoice, mockCreatorPlayer);

        // Verify the result and invoice status
        assertTrue(result, "Deletion should be successful");
        assertEquals(Invoice.Status.DELETED, invoice.getStatus(), "Invoice status should be DELETED");
    }

    @Test
    public void testGetInvoicesByCreator() {
        // Create test invoices
        Invoice invoice1 = new Invoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);
        Invoice invoice2 = new Invoice(TEST_JOB, TEST_CREATOR, UUID.randomUUID(), TEST_AMOUNT * 2);
        Invoice invoice3 = new Invoice(TEST_JOB, UUID.randomUUID(), TEST_TARGET, TEST_AMOUNT * 3);

        // Add the invoices to the manager's collection
        List<Invoice> allInvoices = new ArrayList<>();
        allInvoices.add(invoice1);
        allInvoices.add(invoice2);
        allInvoices.add(invoice3);

        // Mock getAllInvoices to return our test invoices
        InvoiceManager spyManager = spy(invoiceManager);
        doReturn(allInvoices).when(spyManager).getAllInvoices();

        // Test filtering by creator
        List<Invoice> creatorInvoices = spyManager.getInvoicesByCreator(TEST_CREATOR);

        // Verify the filtered invoices
        assertEquals(2, creatorInvoices.size(), "Should return 2 invoices created by TEST_CREATOR");
        assertTrue(creatorInvoices.contains(invoice1), "Should contain invoice1");
        assertTrue(creatorInvoices.contains(invoice2), "Should contain invoice2");
        assertFalse(creatorInvoices.contains(invoice3), "Should not contain invoice3");
    }

    @Test
    public void testGetInvoicesByTarget() {
        // Create test invoices
        Invoice invoice1 = new Invoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);
        Invoice invoice2 = new Invoice(TEST_JOB, UUID.randomUUID(), TEST_TARGET, TEST_AMOUNT * 2);
        Invoice invoice3 = new Invoice(TEST_JOB, TEST_CREATOR, UUID.randomUUID(), TEST_AMOUNT * 3);

        // Add the invoices to the manager's collection
        List<Invoice> allInvoices = new ArrayList<>();
        allInvoices.add(invoice1);
        allInvoices.add(invoice2);
        allInvoices.add(invoice3);

        // Mock getAllInvoices to return our test invoices
        InvoiceManager spyManager = spy(invoiceManager);
        doReturn(allInvoices).when(spyManager).getAllInvoices();

        // Test filtering by target
        List<Invoice> targetInvoices = spyManager.getInvoicesByTarget(TEST_TARGET);

        // Verify the filtered invoices
        assertEquals(2, targetInvoices.size(), "Should return 2 invoices targeting TEST_TARGET");
        assertTrue(targetInvoices.contains(invoice1), "Should contain invoice1");
        assertTrue(targetInvoices.contains(invoice2), "Should contain invoice2");
        assertFalse(targetInvoices.contains(invoice3), "Should not contain invoice3");
    }

    // Note: We can't directly test isPlayerJobBoss because it's a private method
    // Instead, we would test methods that use it, like createInvoice or deleteInvoice
    // which would indirectly test the permission checking functionality
}
