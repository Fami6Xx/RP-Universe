package me.fami6xx.rpuniverse.core.api;

import me.fami6xx.rpuniverse.core.misc.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CharacterKilledEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private Player player;
    private Player commandExecutor;
    private PlayerData data;

    public CharacterKilledEvent(Player player, Player commandExecutor, PlayerData data) {
        this.player = player;
        this.commandExecutor = commandExecutor;
        this.data = data;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getCommandExecutor() {
        return commandExecutor;
    }

    public PlayerData getPlayerData() {
        return data;
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
