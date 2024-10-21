package me.fami6xx.rpuniverse.core.jobs;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.JobDeletedEvent;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
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
        jobs.forEach(job -> RPUniverse.getInstance().getDataSystem().getDataHandler().saveJobData(job.getJobUUID().toString(), job));
        salaryTask.cancel();
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

        jobs.forEach(job -> {
            if(job.getJobType() != null) return;
            if(job.getJobTypeName() == null) return;
            if(job.getJobTypeName().equals(jobType.getName())){
                JobType newInstance = jobType.getNewInstance(job);
                String string = job.getJobTypeData();
                job.setJobType(newInstance);
                if (string != null) newInstance.fromJsonJobTypeData(string);
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RPUniverse.getInstance().getBossBarHandler().updateBossBar(event.getPlayer());
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
