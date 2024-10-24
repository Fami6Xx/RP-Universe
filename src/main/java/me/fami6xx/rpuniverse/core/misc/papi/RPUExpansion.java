package me.fami6xx.rpuniverse.core.misc.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
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
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("mode")){
            return RPUniverse.getPlayerData(player.getUniqueId().toString()).getPlayerMode().toString();
        }

        if(params.equalsIgnoreCase("job")){
            return RPUniverse.getPlayerData(player.getUniqueId().toString()).getSelectedPlayerJob() != null ?
             RPUniverse.getPlayerData(player.getUniqueId().toString()).getSelectedPlayerJob().getName() : RPUniverse.getLanguageHandler().bossBarPlayerNoJob;
        }

        if(params.equalsIgnoreCase("jobRank")){
            return RPUniverse.getPlayerData(player.getUniqueId().toString()).getSelectedPlayerJob() != null ?
            RPUniverse.getPlayerData(player.getUniqueId().toString()).getSelectedPlayerJob().getPlayerPosition(player.getUniqueId()).getName() : RPUniverse.getLanguageHandler().bossBarPlayerNoJob;
        }

        if(params.equalsIgnoreCase("poopLevel")){
            return String.valueOf(RPUniverse.getPlayerData(player.getUniqueId().toString()).getPoopLevel());
        }

        if(params.equalsIgnoreCase("peeLevel")){
            return String.valueOf(RPUniverse.getPlayerData(player.getUniqueId().toString()).getPeeLevel());
        }

        if(params.equalsIgnoreCase("foodLevel")){
            return String.valueOf(RPUniverse.getPlayerData(player.getUniqueId().toString()).getFoodLevel());
        }

        if(params.equalsIgnoreCase("waterLevel")){
            return String.valueOf(RPUniverse.getPlayerData(player.getUniqueId().toString()).getWaterLevel());
        }

        if(params.equalsIgnoreCase("poopLevelFormated")){
            return FamiUtils.format(BasicNeedsHandler.formatNeedForActionBar(RPUniverse.getPlayerData(player.getUniqueId().toString()).getPoopLevel(), true));
        }

        if(params.equalsIgnoreCase("peeLevelFormated")){
            return FamiUtils.format(BasicNeedsHandler.formatNeedForActionBar(RPUniverse.getPlayerData(player.getUniqueId().toString()).getPeeLevel(), true));
        }

        if(params.equalsIgnoreCase("foodLevelFormated")){
            return FamiUtils.format(BasicNeedsHandler.formatNeedForActionBar(RPUniverse.getPlayerData(player.getUniqueId().toString()).getFoodLevel(), false));
        }

        if(params.equalsIgnoreCase("waterLevelFormated")){
            return FamiUtils.format(BasicNeedsHandler.formatNeedForActionBar(RPUniverse.getPlayerData(player.getUniqueId().toString()).getWaterLevel(), false));
        }

        return null;
    }
}
