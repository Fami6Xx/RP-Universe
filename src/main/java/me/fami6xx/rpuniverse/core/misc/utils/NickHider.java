package me.fami6xx.rpuniverse.core.misc.utils;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NickHider {
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Team team;
    BukkitTask task;

    public void init() {
        team = scoreboard.getTeam("hiddenNames") == null ? scoreboard.registerNewTeam("hiddenNames") : scoreboard.getTeam("hiddenNames");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.getScoreboard().getTeam("hiddenNames") == null) {
                        team = player.getScoreboard().registerNewTeam("hiddenNames");
                        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                        team.addEntry(player.getName());
                        return;
                    }
                    if (!player.getScoreboard().getTeam("hiddenNames").hasEntry(player.getName()))
                        player.getScoreboard().getTeam("hiddenNames").addEntry(player.getName());
                });
            }
        }.runTaskTimer(RPUniverse.getInstance(), 0, 20);
    }

    public void shutdown() {
        task.cancel();
        team.unregister();
    }
}
