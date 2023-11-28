package me.fami6xx.rpuniverse.core.holoapi.types.lines;

import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.scheduler.BukkitRunnable;


public abstract class UpdatingLine {
    private final HologramLine line;

    private final long period;

    public UpdatingLine(HologramLine textLine){
        this.line = textLine;
        this.period = 20;
        start();
    }

    public UpdatingLine(HologramLine textLine, long period){
        this.line = textLine;
        this.period = period;
        start();
    }

    public abstract String update();

    private void start(){
        new BukkitRunnable(){
            @Override
            public void run(){
                if(!line.isDisabled()) {
                    String update = update();
                    if (!line.getText().equals(update)) {
                        line.setText(update);
                    }
                }else {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(RPUniverse.getInstance(), 1, period);
    }
}
