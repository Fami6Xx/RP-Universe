package me.fami6xx.rpuniverse.core.locks;

import me.fami6xx.rpuniverse.RPUniverse;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída LocksHandler spravuje zámky v Minecraft světě.
 * Umožňuje načítání, ukládání a získávání zámků na základě různých kritérií.
 */
public class LocksHandler {
    private final List<Lock> locks = new ArrayList<>();

    /**
     * Konstruktor, který načítá všechny zámky při inicializaci.
     */
    public LocksHandler() {
        loadAllLocks();
    }

    /**
     * Uloží všechny zámky do datového systému při vypnutí pluginu.
     */
    public void shutdown() {
        locks.forEach(lock -> RPUniverse.getInstance().getDataSystem().getDataHandler().saveLockData(lock));
    }

    /**
     * Načte všechny zámky z datového systému do interního seznamu.
     */
    private void loadAllLocks() {
        Lock[] loadedLocks = RPUniverse.getInstance().getDataSystem().getDataHandler().getAllLockData();

        for (Lock lock : loadedLocks) {
            if (lock == null) continue;
            this.locks.add(lock);
        }
    }

    /**
     * Vrátí zámek nacházející se na specifikované lokaci.
     * 
     * @param location Lokace, pro kterou se má najít zámek.
     * @return Zámek na dané lokaci nebo null, pokud zámek neexistuje.
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
     * Vrátí seznam zámků, které vlastní specifikovaný majitel.
     * 
     * @param owner Jméno majitele, pro kterého se mají najít zámky.
     * @return Seznam zámků patřících danému majiteli.
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
     * Vrátí seznam zámků, které jsou spojeny s určitou prací.
     * 
     * @param jobName Název práce, pro kterou se mají najít zámky.
     * @return Seznam zámků spojených s danou prací.
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
     * Vrátí seznam všech zámků spravovaných tímto handlerem.
     * 
     * @return Seznam všech zámků.
     */
    public List<Lock> getAllLocks() {
        return new ArrayList<>(locks);
    }
}
