package me.fami6xx.rpuniverse.core.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageByBasicNeedsEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private double damage;
    private Player player;
    private boolean cancelled = false;

    public PlayerDamageByBasicNeedsEvent(Player player, double damage) {
        this.player = player;
        this.damage = damage;
    }

    public Player getPlayer() {
        return player;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
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
