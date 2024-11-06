package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ShowBasicNeedsActionBarEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private boolean cancelled = false;

    public ShowBasicNeedsActionBarEvent(Player player) {
        this.player = player;
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
}
