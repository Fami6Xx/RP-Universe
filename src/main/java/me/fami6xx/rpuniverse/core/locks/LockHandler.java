package me.fami6xx.rpuniverse.core.locks;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.locks.commands.LocksCommand;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * The lock handler class
 * 
 * @author Fami6xx
 * @version 1.0
 */
public class LockHandler {
    private final List<Lock> locks = new ArrayList<>();

    /**
     * Constructor, which loads all locks when the plugin is initialized.
     */
    public LockHandler() {
        loadAllLocks();
        RPUniverse.getInstance().getCommand("locks").setExecutor(new LocksCommand());
    }

    /**
     * Save all locks to the data system when the plugin is shut down.
     */
    public void shutdown() {
        locks.forEach(lock -> RPUniverse.getInstance().getDataSystem().getDataHandler().saveLockData(lock));
    }

    /**
     * Load all locks from the data system to the internal list.
     */
    private void loadAllLocks() {
        Lock[] loadedLocks = RPUniverse.getInstance().getDataSystem().getDataHandler().getAllLockData();

        for (Lock lock : loadedLocks) {
            if (lock == null) continue;
            this.locks.add(lock);
        }
    }

    /**
     * Returns the lock located at the specified location.
     * 
     * @param location The location for which to find the lock.
     * @return The lock at the given location or null if no lock exists.
     */
    public Lock getLockByLocation(Location location) {
        for (Lock lock : locks) {
            if (lock.getLocation().equals(location)) {
                return lock;
            }
        }
        return null;
    }

    /**
     * Returns a list of locks owned by the specified owner.
     * 
     * @param owner The name of the owner for whom to find locks.
     * @return List of locks belonging to the specified owner.
     */
    public List<Lock> getLocksByOwner(String owner) {
        List<Lock> result = new ArrayList<>();
        for (Lock lock : locks) {
            if (lock.getOwner() != null ? lock.getOwner().equals(owner) : false) {
                result.add(lock);
            }
        }
        return result;
    }

    /**
     * Returns a list of locks associated with a specific job.
     * 
     * @param jobName The name of the job for which to find locks.
     * @return List of locks associated with the specified job.
     */
    public List<Lock> getLocksByJob(String jobName) {
        List<Lock> result = new ArrayList<>();
        for (Lock lock : locks) {
            if (lock.getJobName() != null && lock.getJobName().equals(jobName)) {
                result.add(lock);
            }
        }
        return result;
    }

    /**
     * Returns a list of all locks managed by this handler.
     * 
     * @return List of all locks.
     */
    public List<Lock> getAllLocks() {
        return new ArrayList<>(locks);
    }
}
