package me.fami6xx.rpuniverse.core.misc.chatapi;

import org.bukkit.entity.Player;

public interface IChatExecuteQueue {
    boolean execute(Player player, String message);
}
