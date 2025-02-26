package me.fami6xx.rpuniverse.core.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerLocalChatEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private String massage;
    private Player player;
    private boolean cancelled = false;

    public PlayerLocalChatEvent(Player player, String message) {
        this.player = player;
        this.massage = message;
    }

    public Player getPlayer() {
        return player;
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

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
