package me.fami6xx.rpuniverse.core.misc.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class RPUExpansion extends PlaceholderExpansion {
    private final RPUniverse plugin;

    public RPUExpansion(RPUniverse plugin){
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rpu";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Fami6Xx";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

    }
}
