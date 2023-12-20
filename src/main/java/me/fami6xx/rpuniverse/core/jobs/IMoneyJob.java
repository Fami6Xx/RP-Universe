package me.fami6xx.rpuniverse.core.jobs;

import java.util.UUID;

/**
 * The IMoneyJob interface represents a job that involves managing money.
 * It extends the IJob interface and provides additional methods for adding, removing, and retrieving money from the job bank.
 */
public interface IMoneyJob extends IJob {
    /**
     * Adds money to the job bank.
     *
     * @param addingPlayer the UUID of the player adding money to the job bank
     * @param money the amount of money to add to the job bank
     */
    void addMoneyToJobBank(UUID addingPlayer, int money);

    /**
     * Removes money from the job bank.
     *
     * @param player the UUID of the player for whom to remove money from the job bank
     * @param money the amount of money to remove from the job bank
     * @return true if the money was successfully removed, false otherwise
     */
    boolean removeMoneyFromJobBank(UUID player, int money);

    /**
     * Retrieves the current amount of money in the job bank for the specified player.
     *
     * @param player the UUID of the player for whom to retrieve the money
     * @return the current amount of money in the job bank for the player
     */
    int getCurrentMoneyInJobBank(UUID player);
}
