package me.fami6xx.rpuniverse.core.jobs.commands.createJob;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.commands.createJob.utils.CreateJobStorage;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class CreateJobStarter {
    private final RPUniverse plugin;
    private CreateJobCommand createJobCommand;
    private BukkitTask showTitleRunnable;

    private final HashMap<UUID, CreateJobStorage> createJobStorage = new HashMap<>();

    public CreateJobStarter(RPUniverse plugin) {
        this.plugin = plugin;
    }

    public void start() {
        createJobCommand = new CreateJobCommand(this);
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

    public void addToCreateJobStorage(UUID playerUUID, CreateJobStorage createJobStorage){
        this.createJobStorage.put(playerUUID, createJobStorage);
    }

    public void removeFromCreateJobStorage(UUID playerUUID){
        this.createJobStorage.remove(playerUUID);
    }

    public CreateJobStorage getCreateJobStorage(UUID playerUUID){
        return this.createJobStorage.get(playerUUID);
    }
}
