package me.fami6xx.rpuniverse.core.invoice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;

import java.util.Date;
import java.util.UUID;

/**
 * Represents an invoice in the system.
 * <p>
 * Each invoice has a unique ID, the job it was created from, the player who created it,
 * the player it was assigned to, the amount to be paid, the creation date, and the status.
 */
public class Invoice {

    /**
     * The status of an invoice.
     */
    public enum Status {
        /**
         * The invoice is pending payment.
         */
        PENDING,

        /**
         * The invoice has been paid.
         */
        PAID,

        /**
         * The invoice has been deleted.
         */
        DELETED
    }

    @Expose
    @SerializedName("id")
    private final String id;

    @Expose
    @SerializedName("job")
    private final String job; // Stores job UUID as a string

    @Expose
    @SerializedName("creator")
    private final UUID creator;

    @Expose
    @SerializedName("target")
    private final UUID target;

    @Expose
    @SerializedName("amount")
    private final double amount;

    @Expose
    @SerializedName("creationDate")
    private final Date creationDate;

    @Expose
    @SerializedName("status")
    private Status status;

    /**
     * Creates a new invoice.
     *
     * @param job     The job the invoice is created from
     * @param creator The UUID of the player who created the invoice
     * @param target  The UUID of the player the invoice is assigned to
     * @param amount  The amount to be paid
     */
    public Invoice(String job, UUID creator, UUID target, double amount) {
        this.id = UUID.randomUUID().toString();
        this.job = job;
        this.creator = creator;
        this.target = target;
        this.amount = amount;
        this.creationDate = new Date();
        this.status = Status.PENDING;
    }

    /**
     * Gets the unique ID of the invoice.
     *
     * @return The invoice ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the job UUID the invoice was created from.
     *
     * @return The job UUID as a string
     */
    public String getJob() {
        return job;
    }

    /**
     * Gets the job name for display purposes.
     *
     * @return The job name
     */
    public String getJobName() {
        // Get the job by UUID and return its name
        Job job = Job.getJobByUUID(this.job);
        return job != null ? job.getName() : "Unknown Job";
    }

    /**
     * Gets the UUID of the player who created the invoice.
     *
     * @return The creator's UUID
     */
    public UUID getCreator() {
        return creator;
    }

    /**
     * Gets the player who created the invoice.
     *
     * @return The creator player, or null if they are offline
     */
    public Player getCreatorPlayer() {
        return Bukkit.getPlayer(creator);
    }

    /**
     * Gets the UUID of the player the invoice is assigned to.
     *
     * @return The target's UUID
     */
    public UUID getTarget() {
        return target;
    }

    /**
     * Gets the player the invoice is assigned to.
     *
     * @return The target player, or null if they are offline
     */
    public Player getTargetPlayer() {
        return Bukkit.getPlayer(target);
    }

    /**
     * Gets the amount to be paid.
     *
     * @return The amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the creation date of the invoice.
     *
     * @return The creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Gets the status of the invoice.
     *
     * @return The status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the invoice.
     *
     * @param status The new status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Checks if the invoice is pending payment.
     *
     * @return true if the invoice is pending, false otherwise
     */
    public boolean isPending() {
        return status == Status.PENDING;
    }

    /**
     * Checks if the invoice has been paid.
     *
     * @return true if the invoice is paid, false otherwise
     */
    public boolean isPaid() {
        return status == Status.PAID;
    }

    /**
     * Checks if the invoice has been deleted.
     *
     * @return true if the invoice is deleted, false otherwise
     */
    public boolean isDeleted() {
        return status == Status.DELETED;
    }

    /**
     * Marks the invoice as paid.
     */
    public void markAsPaid() {
        this.status = Status.PAID;
        ErrorHandler.debug("Invoice marked as paid: ID=" + id);
    }

    /**
     * Marks the invoice as deleted.
     */
    public void markAsDeleted() {
        this.status = Status.DELETED;
        ErrorHandler.debug("Invoice marked as deleted: ID=" + id);
    }
}
