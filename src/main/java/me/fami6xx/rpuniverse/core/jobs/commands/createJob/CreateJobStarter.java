package me.fami6xx.rpuniverse.core.jobs.commands.createJob;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.scheduler.BukkitTask;

public class CreateJobStarter {
    RPUniverse plugin;
    CreateJobCommand createJobCommand;
    BukkitTask showTitleRunnable;

    public CreateJobStarter(RPUniverse plugin) {
        this.plugin = plugin;
    }

    public void start() {
        createJobCommand = new CreateJobCommand();
        plugin.getCommand("createjob").setExecutor(createJobCommand);
        plugin.getServer().getPluginManager().registerEvents(createJobCommand, plugin);
        showTitleRunnable = createJobCommand.showTitleRunnable();
    }

    public void stop() {
        createJobCommand = null;
        plugin.getCommand("createjob").setExecutor(null);
        showTitleRunnable.cancel();
        showTitleRunnable = null;
    }
}
