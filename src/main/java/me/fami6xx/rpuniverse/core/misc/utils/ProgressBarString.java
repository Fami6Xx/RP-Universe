package me.fami6xx.rpuniverse.core.misc.utils;

import me.fami6xx.rpuniverse.core.misc.IExecuteQueue;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class ProgressBarString extends BukkitRunnable {
    private String string;
    private final int totalTicks;
    private int ticksElapsed;
    private final IExecuteQueue updateCallback;
    private final IExecuteQueue endCallback;

    private final String symbol = "|";

    /**
     * Constructs a ProgressBar instance.
     *
     * @param string        The string to display in the progress bar.
     * @param durationTicks The total duration of the progress bar in ticks.
     */
    public ProgressBarString(String string, int durationTicks, IExecuteQueue updateCallback, IExecuteQueue endCallback) {
        this.string = string;
        this.totalTicks = durationTicks;
        this.ticksElapsed = 0;
        this.updateCallback = updateCallback;
        this.endCallback = endCallback;
    }

    @Override
    public void run() {
        if (ticksElapsed >= totalTicks) {
            endCallback.execute();
            cancel();
            return;
        }

        // Calculate progress percentage
        double progress = (double) ticksElapsed / totalTicks;

        // Create progress bar string
        int totalBars = 20; // Length of the progress bar
        int filledBars = (int) (progress * totalBars);

        StringBuilder progressBar = new StringBuilder();
        progressBar.append(ChatColor.GREEN);

        // Add filled bars
        progressBar.append(symbol.repeat(Math.max(0, filledBars)));

        progressBar.append(ChatColor.RED);

        // Add unfilled bars
        progressBar.append(symbol.repeat(Math.max(0, totalBars - filledBars)));

        // Send the progress bar to the player's action bar
        string = progressBar.toString();

        updateCallback.execute();

        ticksElapsed++;
    }

    /**
     * Get the string representation of the progress bar.
     * @return The string representation of the progress bar.
     */
    public String getString() {
        return string;
    }
}
