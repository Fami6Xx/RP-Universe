package me.fami6xx.rpuniverse.core.misc.datahandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.basicneeds.BasicNeedsHandler;
import me.fami6xx.rpuniverse.core.basicneeds.ConsumableItem;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.locks.Lock;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.ItemStackAdapter;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONDataHandler implements IDataHandler {
    private Gson gson;
    private final Path playerDataDirectory = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/playerdata/");
    private final Path jobDataDirectory = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/jobs/");
    private final Logger logger =  RPUniverse.getInstance().getLogger();

    @Override
    public boolean startUp() {
        try {
            this.gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(Location.class, new LocationAdapter())
                    .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                    .create();

            File dataDir = new File(playerDataDirectory.toUri());
            if(!dataDir.exists()){
                if(!dataDir.mkdirs()) return false;
            }

            File jobsDir = new File(jobDataDirectory.toUri());
            if(!jobsDir.exists()){
                if(!jobsDir.mkdirs()) return false;
            }

            return true;
        } catch (Exception e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean shutDown() {
        return true;
    }

    @Override
    public String getHandlerName() {
        return "JSONDataHandler";
    }

    @Override
    public PlayerData loadPlayerData(String uuid) {
        Path playerFilePath = playerDataDirectory.resolve(uuid + ".json");

        File playerFile = playerFilePath.toFile();
        if(!playerFile.exists()) {
            return new PlayerData(uuid);
        }

        try (Reader reader = new FileReader(playerFilePath.toFile())) {
            return gson.fromJson(reader, PlayerData.class);
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return null;
        } catch (JsonParseException e) {
            logger.severe("Failed to load data for player: " + uuid);
            return null;
        }
    }

    @Override
    public boolean savePlayerData(PlayerData data) {
        Path playerFilePath = playerDataDirectory.resolve(data.getPlayerUUID().toString() + ".json");

        File playerFilePathFile = playerFilePath.toFile();
        if(!playerFilePathFile.exists()) {
            try {
                if(!playerFilePathFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                logger.severe(e.getMessage());
                return false;
            }
        }

        try (Writer writer = new FileWriter(playerFilePath.toFile())) {
            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public Job getJobData(String name) {
        Path jobFilePath = jobDataDirectory.resolve(name + ".json");

        try (Reader reader = new FileReader(jobFilePath.toFile())) {
            return gson.fromJson(reader, Job.class);
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return null;
        } catch (JsonParseException e) {
            logger.severe("Failed to load data for job: " + name);
            return null;
        }
    }

    @Override
    public boolean renameJobData(String oldName, String newName) {
        Path oldJobFilePath = jobDataDirectory.resolve(oldName + ".json");
        Path newJobFilePath = jobDataDirectory.resolve(newName + ".json");

        File oldJobFile = oldJobFilePath.toFile();
        File newJobFile = newJobFilePath.toFile();

        if(!oldJobFile.exists()) {
            return false;
        }

        if(newJobFile.exists()) {
            return false;
        }

        return oldJobFile.renameTo(newJobFile);
    }

    @Override
    public boolean saveJobData(String name, Job data) {
        Path jobFilePath = jobDataDirectory.resolve(name + ".json");

        File jobFile = jobFilePath.toFile();
        if(!jobFile.exists()) {
            try {
                if(!jobFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                logger.severe(e.getMessage());
                return false;
            }
        }

        data.prepareForSave();

        try (Writer writer = new FileWriter(jobFilePath.toFile())) {
            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    /**
     * Saves the consumables for the BasicNeedsHandler. The data is serialized and deserialized by the selected Data Handler.
     *
     * @param handler The BasicNeedsHandler to save consumables for.
     * @return true if the data was saved successfully, false otherwise.
     */
    @Override
    public boolean saveConsumables(BasicNeedsHandler handler) {
        File consumablesFile = new File(RPUniverse.getInstance().getDataFolder().getPath() + "/consumables.json");
        if(!consumablesFile.exists()) {
            try {
                if(!consumablesFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                logger.severe(e.getMessage());
                return false;
            }
        }

        try (Writer writer = new FileWriter(consumablesFile)) {
            JsonObject consumables = new JsonObject();
            handler.getConsumables().forEach((item, consumable) -> {
                consumables.add(gson.toJson(item, ItemStack.class), gson.toJsonTree(consumable));
            });
            gson.toJson(consumables, writer);
            return true;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    /**
     * Loads the consumables for the BasicNeedsHandler. The data is serialized and deserialized by the selected Data Handler.
     *
     * @return A HashMap containing all consumables.
     */
    @Override
    public HashMap<ItemStack, ConsumableItem> loadConsumables() {
        File consumablesFile = new File(RPUniverse.getInstance().getDataFolder().getPath() + "/consumables.json");
        if(!consumablesFile.exists()) {
            return new HashMap<>();
        }

        try (Reader reader = new FileReader(consumablesFile)) {
            HashMap<ItemStack, ConsumableItem> consumables = new HashMap<>();
            JsonObject consumablesJson = gson.fromJson(reader, JsonObject.class);
            consumablesJson.entrySet().forEach(entry -> {
                ItemStack item = gson.fromJson(entry.getKey(), ItemStack.class);
                ConsumableItem consumable = gson.fromJson(entry.getValue(), ConsumableItem.class);
                consumables.put(item, consumable);
            });
            return consumables;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return new HashMap<>();
        } catch (JsonParseException e) {
            logger.severe("Failed to load consumables");
            return new HashMap<>();
        }
    }

    @Override
    public Job[] getAllJobData() {
        File[] files = jobDataDirectory.toFile().listFiles();
        if(files == null) {
            return new Job[0];
        }
        Job[] jobs = new Job[files.length];
        for(int i = 0; i < files.length; i++) {
            jobs[i] = getJobData(files[i].getName().replace(".json", ""));
        }
        return jobs;
    }

    @Override
    public boolean removeJobData(String name) {
        Path jobFilePath = jobDataDirectory.resolve(name + ".json");
        File jobFile = jobFilePath.toFile();
        if(!jobFile.exists()) {
            return false;
        }
        return jobFile.delete();
    }

    @Override
    public int getQueueSaveTime() {
        return 20 * 60 * 5; // 5 minutes
    }

    @Override
    public boolean saveLockData(Lock lock) {
        Path lockFilePath = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/locks/" + lock.getLocation().hashCode() + ".json");
        File lockFile = lockFilePath.toFile();
        if (!lockFile.exists()) {
            try {
                if (!lockFile.createNewFile()) {
                    return false;
                }
            } catch (IOException e) {
                logger.severe(e.getMessage());
                return false;
            }
        }

        try (Writer writer = new FileWriter(lockFile)) {
            gson.toJson(lock, writer);
            return true;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public Lock[] getAllLockData() {
        File lockDir = new File(RPUniverse.getInstance().getDataFolder().getPath() + "/locks/");
        File[] files = lockDir.listFiles();
        if (files == null) {
            return new Lock[0];
        }

        List<Lock> locks = new ArrayList<>();
        for (File file : files) {
            try (Reader reader = new FileReader(file)) {
                Lock lock = gson.fromJson(reader, Lock.class);
                if (lock != null) {
                    locks.add(lock);
                }
            } catch (IOException | JsonParseException e) {
                logger.severe("Failed to load lock data from file: " + file.getName() + " with error: " + e.getMessage());
            }
        }
        return locks.toArray(new Lock[0]);
    }
}