package me.fami6xx.rpuniverse.core.jobs;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class JobsHandler implements Listener {
    private final List<Job> jobs = new ArrayList<>();
    private final List<JobType> jobTypes = new ArrayList<>();

    public JobsHandler() {
        loadAllJobs();
    }

    public void shutdown() {
        jobs.forEach(job -> RPUniverse.getInstance().getDataSystem().getDataHandler().saveJobData(job.getName(), job));
    }

    public void loadAllJobs(){
        Job[] jobs = RPUniverse.getInstance().getDataSystem().getDataHandler().getAllJobData();

        for(Job job : jobs){
            if(job == null) continue;
            JobType jobType = getJobTypeByName(job.getJobTypeName());
            if(jobType != null){
                job.setJobType(jobType.fromString(job.getJobTypeData()));
            }
            job.initialize();
            this.jobs.add(job);
        }
    }

    public Job getJobByName(String name){
        for(Job job : jobs){
            if(job.getName().equals(name)){
                return job;
            }
        }
        return null;
    }

    public JobType getJobTypeByName(String name){
        for(JobType jobType : jobTypes){
            if(jobType.getName().equals(name)){
                return jobType;
            }
        }
        return null;
    }

    public void removeJob(Job job){
        job.remove();
        jobs.remove(job);
        RPUniverse.getInstance().getDataSystem().getDataHandler().removeJobData(job.getName());
        RPUniverse.getInstance().getMenuManager().closeAllJobMenus(j -> j == job);
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public List<JobType> getJobTypes() {
        return jobTypes;
    }

    public void addJob(Job job){
        jobs.add(job);
        RPUniverse.getInstance().getDataSystem().getDataHandler().saveJobData(job.getName(), job);
    }

    /**
     * Adds a JobType to the list of job types.
     * <p>
     * It is crucial that the JobType is added to the list of job types in your onEnable method, because when the server loads then jobs are loaded, and they need their JobTypes to be ready.
     *
     * @param jobType the JobType to be added to the list of job types.
     */
    public void addJobType(JobType jobType){
        jobTypes.add(jobType);

        jobs.forEach(job -> {
            if(job.getJobTypeName().equals(jobType.getName())){
                job.setJobType(jobType.fromString(job.getJobTypeData()));
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RPUniverse.getInstance().getBossBarHandler().updateBossBar(event.getPlayer());
    }
}
