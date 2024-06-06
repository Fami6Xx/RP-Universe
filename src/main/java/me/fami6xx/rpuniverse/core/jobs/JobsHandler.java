package me.fami6xx.rpuniverse.core.jobs;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Constructor for the JobsHandler.
     * Initializes the jobs and job types.
     * Also loads all the jobs.
     */
    public JobsHandler() {
        loadAllJobs();
    }

    /**
     * Shutdown the JobsHandler.
     * Saves all the jobs.
     */
    public void shutdown() {
        jobs.forEach(job -> RPUniverse.getInstance().getDataSystem().getDataHandler().saveJobData(job.getName(), job));
    }

    /**
     * Load all the jobs from the data handler.
     * Also initializes the jobs and job types.
     */
    public void loadAllJobs(){
        Job[] jobs = RPUniverse.getInstance().getDataSystem().getDataHandler().getAllJobData();

        for(Job job : jobs){
            if(job == null) continue;
            JobType jobType = getJobTypeByName(job.getJobTypeName());
            if(jobType != null){
                JobType newInstance = jobType.getNewInstance();
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
        job.remove();
        jobs.remove(job);
        RPUniverse.getInstance().getDataSystem().getDataHandler().removeJobData(job.getName());
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
        RPUniverse.getInstance().getDataSystem().getDataHandler().saveJobData(job.getName(), job);
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
            if(job.getJobTypeName().equals(jobType.getName())){
                JobType newInstance = jobType.getNewInstance();
                job.setJobType(newInstance);
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RPUniverse.getInstance().getBossBarHandler().updateBossBar(event.getPlayer());
    }
}
