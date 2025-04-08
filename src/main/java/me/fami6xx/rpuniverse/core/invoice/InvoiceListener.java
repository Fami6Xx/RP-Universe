package me.fami6xx.rpuniverse.core.invoice;

import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * Listener for invoice-related events.
 * <p>
 * This class handles events related to the invoice system, such as notifying
 * players about pending invoices when they join the server.
 */
public class InvoiceListener implements Listener {

    private final InvoiceManager manager;

    /**
     * Creates a new InvoiceListener.
     * 
     * @param manager The InvoiceManager instance
     */
    public InvoiceListener(InvoiceManager manager) {
        this.manager = manager;
    }

    /**
     * Handles player join events.
     * <p>
     * Notifies players about pending invoices when they join the server.
     * 
     * @param event The PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if join notifications are enabled
        if (!manager.getModule().isJoinNotificationEnabled()) {
            return;
        }

        Player player = event.getPlayer();

        // Get pending invoices for the player
        List<Invoice> pendingInvoices = manager.getPendingInvoicesByTarget(player.getUniqueId());

        // If there are pending invoices, notify the player
        if (!pendingInvoices.isEmpty()) {
            // Use the language system for the message
            String message = InvoiceLanguage.getInstance().pendingInvoicesJoinMessage;

            // Replace placeholders
            message = message.replace("{count}", String.valueOf(pendingInvoices.size()));

            player.sendMessage(FamiUtils.formatWithPrefix(message));

            me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler.debug("Player " + player.getName() + 
                                                                    " notified about " + pendingInvoices.size() + 
                                                                    " pending invoices on join");
        }
    }
}
