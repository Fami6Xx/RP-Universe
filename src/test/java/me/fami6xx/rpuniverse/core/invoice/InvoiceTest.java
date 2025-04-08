package me.fami6xx.rpuniverse.core.invoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Invoice class.
 */
public class InvoiceTest {

    private static final String TEST_JOB = "TestJob";
    private static final UUID TEST_CREATOR = UUID.randomUUID();
    private static final UUID TEST_TARGET = UUID.randomUUID();
    private static final double TEST_AMOUNT = 100.0;

    @Mock
    private Player mockCreatorPlayer;

    @Mock
    private Player mockTargetPlayer;

    private Invoice invoice;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up mock behavior
        when(mockCreatorPlayer.getUniqueId()).thenReturn(TEST_CREATOR);
        when(mockTargetPlayer.getUniqueId()).thenReturn(TEST_TARGET);
        
        // Create a test invoice
        invoice = new Invoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);
    }

    @Test
    public void testInvoiceCreation() {
        // Verify the invoice was created with the correct properties
        assertNotNull(invoice.getId(), "Invoice ID should not be null");
        assertEquals(TEST_JOB, invoice.getJob(), "Job should match the provided value");
        assertEquals(TEST_CREATOR, invoice.getCreator(), "Creator UUID should match the provided value");
        assertEquals(TEST_TARGET, invoice.getTarget(), "Target UUID should match the provided value");
        assertEquals(TEST_AMOUNT, invoice.getAmount(), "Amount should match the provided value");
        assertNotNull(invoice.getCreationDate(), "Creation date should not be null");
        assertEquals(Invoice.Status.PENDING, invoice.getStatus(), "Initial status should be PENDING");
    }

    @Test
    public void testStatusChecks() {
        // Test initial status
        assertTrue(invoice.isPending(), "New invoice should be pending");
        assertFalse(invoice.isPaid(), "New invoice should not be paid");
        assertFalse(invoice.isDeleted(), "New invoice should not be deleted");
        
        // Test marking as paid
        invoice.markAsPaid();
        assertFalse(invoice.isPending(), "Paid invoice should not be pending");
        assertTrue(invoice.isPaid(), "Invoice should be marked as paid");
        assertFalse(invoice.isDeleted(), "Paid invoice should not be deleted");
        
        // Create a new invoice for deletion test
        Invoice invoiceToDelete = new Invoice(TEST_JOB, TEST_CREATOR, TEST_TARGET, TEST_AMOUNT);
        
        // Test marking as deleted
        invoiceToDelete.markAsDeleted();
        assertFalse(invoiceToDelete.isPending(), "Deleted invoice should not be pending");
        assertFalse(invoiceToDelete.isPaid(), "Deleted invoice should not be paid");
        assertTrue(invoiceToDelete.isDeleted(), "Invoice should be marked as deleted");
    }

    @Test
    public void testSetStatus() {
        // Test setting status to PAID
        invoice.setStatus(Invoice.Status.PAID);
        assertEquals(Invoice.Status.PAID, invoice.getStatus(), "Status should be updated to PAID");
        assertTrue(invoice.isPaid(), "Invoice should be marked as paid");
        
        // Test setting status to DELETED
        invoice.setStatus(Invoice.Status.DELETED);
        assertEquals(Invoice.Status.DELETED, invoice.getStatus(), "Status should be updated to DELETED");
        assertTrue(invoice.isDeleted(), "Invoice should be marked as deleted");
        
        // Test setting status back to PENDING
        invoice.setStatus(Invoice.Status.PENDING);
        assertEquals(Invoice.Status.PENDING, invoice.getStatus(), "Status should be updated to PENDING");
        assertTrue(invoice.isPending(), "Invoice should be marked as pending");
    }
}