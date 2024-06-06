package me.fami6xx.rpuniverse.core.locks;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class LocksHandler {
    private final List<Lock> locks = new ArrayList<>();

    public LocksHandler() {
        loadAllLocks();
    }

    public void shutdown() {
        locks.forEach(lock -> RPUniverse.getInstance().getDataSystem().getDataHandler().saveLockData(lock));
    }

    public void loadAllLocks() {
        Lock[] loadedLocks = RPUniverse.getInstance().getDataSystem().getDataHandler().getAllLockData();

        for (Lock lock : loadedLocks) {
            if (lock == null) continue;
            this.locks.add(lock);
        }
    }

    public Lock getLockByLocation(Location location) {
        for (Lock lock : locks) {
            if (lock.getLocation().equals(location)) {
                return lock;
            }
        }
        return null;
    }

    public List<Lock> getLocksByOwner(String owner) {
        List<Lock> result = new ArrayList<>();
        for (Lock lock : locks) {
            if (lock.getOwner().equals(owner)) {
                result.add(lock);
            }
        }
        return result;
    }

    public List<Lock> getAllLocks() {
        return new ArrayList<>(locks);
    }
}
