package me.fami6xx.rpuniverse.core.holoapi.types.lines;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class UpdatingHologramLine {
    private final HologramLine line;

    private final long period;

    public UpdatingHologramLine(HologramLine textLine){
        this.line = textLine;
        this.period = 20;
        start();
    }

    public UpdatingHologramLine(HologramLine textLine, long period){
        this.line = textLine;
        this.period = period;
        start();
    }

    public abstract String update();

    private void start(){
        new BukkitRunnable(){
            @Override
            public void run(){
                if (line == null) {
                    cancel();
                    return;
                }

                if (line.isDisabled()) {
                    cancel();
                    return;
                }

                if (line.getParent() == null) {
                    cancel();
                    return;
                }

                if (line.getParent().getParent().isDisabled()) {
                    cancel();
                    return;
                }

                String update = update();
                if (!line.getText().equals(update)) {
                    line.setText(update);
                }
            }
        }.runTaskTimerAsynchronously(RPUniverse.getInstance(), 1, period);
    }
}
