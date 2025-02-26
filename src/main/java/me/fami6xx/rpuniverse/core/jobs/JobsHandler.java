package me.fami6xx.rpuniverse.core.jobs;

import com.google.gson.JsonObject;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.*;
import me.fami6xx.rpuniverse.core.api.holograms.SellStepHologram;
import me.fami6xx.rpuniverse.core.api.holograms.WorkingStepHologram;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The JobsHandler is the main class for handling the jobs.
 * It is responsible for loading the jobs, saving the jobs, and handling the jobs handler.
 * <p>
 * The JobsHandler is a singleton class, so only one instance of it should be created.
 * <p>
 * The JobsHandler is also a listener for the PlayerJoinEvent.
 * This means that the JobsHandler will also listen for the PlayerJoinEvent and update the jobs accordingly.
 */
public class JobsHandler implements Listener {
    private final List<Job> jobs = new ArrayList<>();
    private final List<JobType> jobTypes = new ArrayList<>();

    private final HashMap<WorkingStep, Location> stepLocationHashMap = new HashMap<>();
    private final HashMap<Location, famiHologram> hologramHashMap = new HashMap<>();

    private final HashMap<SellStep, famiHologram> sellStepHologramHashMap = new HashMap<>();

    private BukkitTask salaryTask;

    /**
     * Constructor for the JobsHandler.
     * Initializes the jobs and job types.
     * Also loads all the jobs.
     */
    public JobsHandler() {
        loadAllJobs();
        startSalaryTask();
    }

    /**
     * Shutdown the JobsHandler.
     * Saves all the jobs.
     */
    public void shutdown() {
        jobs.forEach(job -> {
            if(job.getJobType() != null) {
                job.getJobType().stop();
            }
            RPUniverse.getInstance().getDataSystem().getDataHandler().saveJobData(job.getJobUUID().toString(), job);
        });
        salaryTask.cancel();
        hologramHashMap.values().forEach(famiHologram::destroy);
        sellStepHologramHashMap.values().forEach(famiHologram::destroy);
        hologramHashMap.clear();
        sellStepHologramHashMap.clear();
        stepLocationHashMap.clear();
    }

    /**
     * Load all the jobs from the data handler.
     * Also initializes the jobs and job types.
     */
    private void loadAllJobs(){
        Job[] jobs = RPUniverse.getInstance().getDataSystem().getDataHandler().getAllJobData();

        for(Job job : jobs){
            if(job == null) continue;
            JobType jobType = getJobTypeByName(job.getJobTypeName());
            if(jobType != null){
                JobType newInstance = jobType.getNewInstance(job);
                job.setJobType(newInstance);
            }
            job.initialize();
            this.jobs.add(job);
        }
    }

    /**
     * Get a job by its name
     * @param name The name of the job
     * @return The job
     */
    public Job getJobByName(String name){
        for(Job job : jobs){
            if(job.getName().equals(name)){
                return job;
            }
        }
        return null;
    }

    /**
     * Get a job type by its name
     * @param name The name of the job type
     * @return The job type
     */
    public JobType getJobTypeByName(String name){
        for(JobType jobType : jobTypes){
            if(jobType.getName().equals(name)){
                return jobType;
            }
        }
        return null;
    }

    /**
     * Remove a job
     * @param job The job to remove
     */
    public void removeJob(Job job){
        JobDeletedEvent event = new JobDeletedEvent(job);
        Bukkit.getPluginManager().callEvent(event);

        job.remove();
        jobs.remove(job);
        RPUniverse.getInstance().getDataSystem().getDataHandler().removeJobData(job.getJobUUID().toString());
        RPUniverse.getInstance().getMenuManager().closeAllJobMenus(j -> j == job);
    }

    /**
     * Get the jobs
     * @return The jobs
     */
    public List<Job> getJobs() {
        return jobs;
    }

    /**
     * Get the job types
     * @return The job types
     */
    public List<JobType> getJobTypes() {
        return jobTypes;
    }

    /**
     * Add a job
     * @param job The job to add
     */
    public void addJob(Job job){
        jobs.add(job);
        RPUniverse.getInstance().getDataSystem().getDataHandler().saveJobData(job.getJobUUID().toString(), job);
    }

    /**
     * Adds a JobType to the list of job types.
     * <p>
     * It is crucial that the JobType is added to the list of job types in your onEnable method,
     * because when the server loads then jobs are loaded, and they need their
     * JobTypes to be ready as soon as possible.
     *
     * @param jobType the JobType to be added to the list of job types.
     */
    public void addJobType(JobType jobType){
        jobTypes.add(jobType);

        RPUniverse.getInstance().getLogger().info("Registered job type: " + jobType.getName());

        jobs.forEach(job -> {
            if(job.getJobType() != null) return;
            if(job.getJobTypeName() == null) return;
            if(job.getJobTypeName().equals(jobType.getName())){
                JobType newInstance = jobType.getNewInstance(job);
                JsonObject object = job.getJobTypeData();
                if (object != null) newInstance.fromJsonJobTypeData(object);
                job.setJobType(newInstance);
            }
        });
    }

    /**
     * Get a job by its UUID
     * @param uuid The UUID of the job
     * @return The job
     */
    public Job getJobByUUID(String uuid){
        for(Job job : jobs){
            if(job.getJobUUID().toString().equals(uuid)){
                return job;
            }
        }
        return null;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RPUniverse.getInstance().getBossBarHandler().updateBossBar(event.getPlayer());
    }

    @EventHandler
    public void onWorkingStepLocationAdded(WorkingStepLocationAddedEvent event) {
        stepLocationHashMap.put(event.getWorkingStep(), event.getLocation());

        WorkingStepHologram holo = new WorkingStepHologram(event.getWorkingStep(), event.getLocation(), getJobByUUID(event.getWorkingStep().getJobUUID().toString()));
        hologramHashMap.put(event.getLocation(), holo);
    }

    @EventHandler
    public void onSellStepLocationAdded(SellStepLocationAddedEvent event) {
        sellStepHologramHashMap.put(event.getSellStep(), new SellStepHologram(event.getSellStep(), getJobByUUID(event.getSellStep().getJobUUID().toString())));
    }

    @EventHandler
    public void onWorkingStepLocationRemoved(WorkingStepLocationRemovedEvent event) {
        stepLocationHashMap.remove(event.getWorkingStep());
        if (hologramHashMap.containsKey(event.getLocation()) && hologramHashMap.get(event.getLocation()) != null) {
            hologramHashMap.get(event.getLocation()).destroy();
        }
        hologramHashMap.remove(event.getLocation());
    }

    @EventHandler
    public void onSellStepLocationRemoved(SellStepLocationRemovedEvent event) {
        if (sellStepHologramHashMap.containsKey(event.getSellStep()) && sellStepHologramHashMap.get(event.getSellStep()) != null) {
            sellStepHologramHashMap.get(event.getSellStep()).destroy();
        }
        sellStepHologramHashMap.remove(event.getSellStep());
    }

    private void startSalaryTask() {
        salaryTask = RPUniverse.getInstance().getServer().getScheduler().runTaskTimer(RPUniverse.getInstance(), () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());
                if (data == null) return;
                data.increaseTimeOnline();

                AtomicReference<Double> salary = new AtomicReference<>(0.0);
                AtomicBoolean runThroughJobs = new AtomicBoolean(false);

                data.getPlayerJobs().forEach(job -> {
                    if (job == null) {
                        runThroughJobs.set(true);
                        return;
                    }

                    if (job.isPlayersReceiveSalary()) {
                        int timeOnline = data.getTimeOnline();
                        if (timeOnline != 0 && timeOnline % job.getSalaryInterval() == 0) {
                            if (job.getPlayerPosition(player.getUniqueId()) == null) return;

                            if (job.getCurrentMoneyInJobBank() < job.getPlayerPosition(player.getUniqueId()).getSalary()) {
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("{jobName}", job.getName());
                                FamiUtils.sendMessage(player, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().salaryJobDoesntHaveEnoughMoney, placeholders));
                            }

                            if (job.isSalaryBeingRemovedFromBank()) {
                                job.removeMoneyFromJobBank(job.getPlayerPosition(player.getUniqueId()).getSalary());
                            }
                            salary.updateAndGet(v -> v + job.getPlayerPosition(player.getUniqueId()).getSalary());
                        }
                    }
                });

                if(runThroughJobs.get()) {
                    data.getPlayerJobs().removeIf(Objects::isNull);
                }

                if (salary.get() != 0) {
                    RPUniverse.getInstance().getEconomy().depositPlayer(player, salary.get());
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("{salary}", salary.get().toString());

                    RPUniverse.getInstance().getActionBarHandler().addMessage(player, FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().salaryReceivedMessage, placeholders));
                }
            });
        }, 0, 20);
    }
}
