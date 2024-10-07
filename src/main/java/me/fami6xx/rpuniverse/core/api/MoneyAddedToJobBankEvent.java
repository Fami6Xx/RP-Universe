package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.jobs.Job;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MoneyAddedToJobBankEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final double amount;
    private final double finalAmount;
    private final Job job;

    public MoneyAddedToJobBankEvent(double amount, double finalAmount, Job job) {
        this.amount = amount;
        this.job = job;
        this.finalAmount = finalAmount;
    }

    public double getAmount() {
        return amount;
    }

    public Job getJob() {
        return job;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
