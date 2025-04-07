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
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONDataHandler implements IDataHandler {
    private Gson gson;
    private final Path playerDataDirectory = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/playerdata/");
    private final Path jobDataDirectory = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/jobs/");

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
                return jobsDir.mkdirs();
            }

            ErrorHandler.debug("JSONDataHandler initialized successfully");
            return true;
        } catch (Exception e) {
            ErrorHandler.severe("Failed to initialize JSONDataHandler", e);
            return false;
        }
    }

    @Override
    public boolean shutDown() {
        ErrorHandler.debug("JSONDataHandler shutting down");
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
            ErrorHandler.debug("Creating new player data for: " + uuid);
            return new PlayerData(uuid);
        }

        try (Reader reader = new FileReader(playerFilePath.toFile())) {
            PlayerData data = gson.fromJson(reader, PlayerData.class);
            ErrorHandler.debug("Loaded player data for: " + uuid);
            return data;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to load player data file for: " + uuid, e);
            return null;
        } catch (JsonParseException e) {
            ErrorHandler.severe("Failed to parse player data for: " + uuid, e);
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
                    ErrorHandler.warning("Failed to create new player data file for: " + data.getPlayerUUID());
                    return false;
                }
            } catch (IOException e) {
                ErrorHandler.severe("Error creating player data file for: " + data.getPlayerUUID(), e);
                return false;
            }
        }

        try (Writer writer = new FileWriter(playerFilePath.toFile())) {
            gson.toJson(data, writer);
            ErrorHandler.debug("Saved player data for: " + data.getPlayerUUID());
            return true;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save player data for: " + data.getPlayerUUID(), e);
            return false;
        }
    }

    @Override
    public Job getJobData(String uuid) {
        Path jobFilePath = jobDataDirectory.resolve(uuid + ".json");

        try (Reader reader = new FileReader(jobFilePath.toFile())) {
            Job job = gson.fromJson(reader, Job.class);
            ErrorHandler.debug("Loaded job data for: " + uuid);
            return job;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to load job data file for: " + uuid, e);
            return null;
        } catch (JsonParseException e) {
            ErrorHandler.severe("Failed to parse job data for: " + uuid, e);
            return null;
        }
    }

    @Override
    public boolean saveJobData(String uuid, Job data) {
        Path jobFilePath = jobDataDirectory.resolve(uuid + ".json");

        File jobFile = jobFilePath.toFile();
        if(!jobFile.exists()) {
            try {
                if(!jobFile.createNewFile()) {
                    ErrorHandler.warning("Failed to create new job data file for: " + uuid);
                    return false;
                }
            } catch (IOException e) {
                ErrorHandler.severe("Error creating job data file for: " + uuid, e);
                return false;
            }
        }

        data.prepareForSave();

        try (Writer writer = new FileWriter(jobFilePath.toFile())) {
            gson.toJson(data, writer);
            ErrorHandler.debug("Saved job data for: " + uuid);
            return true;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save job data for: " + uuid, e);
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
                    ErrorHandler.warning("Failed to create consumables file");
                    return false;
                }
            } catch (IOException e) {
                ErrorHandler.severe("Error creating consumables file", e);
                return false;
            }
        }

        try (Writer writer = new FileWriter(consumablesFile)) {
            JsonObject consumables = new JsonObject();
            handler.getConsumables().forEach((item, consumable) -> {
                consumables.add(gson.toJson(item, ItemStack.class), gson.toJsonTree(consumable));
            });
            gson.toJson(consumables, writer);
            ErrorHandler.debug("Saved consumables data");
            return true;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save consumables data", e);
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
            ErrorHandler.debug("Consumables file does not exist, returning empty map");
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
            ErrorHandler.debug("Loaded consumables data: " + consumables.size() + " items");
            return consumables;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to load consumables file", e);
            return new HashMap<>();
        } catch (JsonParseException e) {
            ErrorHandler.severe("Failed to parse consumables data", e);
            return new HashMap<>();
        }
    }

    @Override
    public Job[] getAllJobData() {
        File[] files = jobDataDirectory.toFile().listFiles();
        if(files == null) {
            ErrorHandler.warning("No job files found or job directory does not exist");
            return new Job[0];
        }
        Job[] jobs = new Job[files.length];
        for(int i = 0; i < files.length; i++) {
            jobs[i] = getJobData(files[i].getName().replace(".json", ""));
        }
        ErrorHandler.debug("Loaded " + jobs.length + " jobs");
        return jobs;
    }

    @Override
    public boolean removeJobData(String uuid) {
        Path jobFilePath = jobDataDirectory.resolve(uuid + ".json");
        File jobFile = jobFilePath.toFile();
        if(!jobFile.exists()) {
            ErrorHandler.debug("Job file does not exist for: " + uuid);
            return false;
        }
        boolean result = jobFile.delete();
        if (result) {
            ErrorHandler.debug("Removed job data for: " + uuid);
        } else {
            ErrorHandler.warning("Failed to remove job data for: " + uuid);
        }
        return result;
    }

    @Override
    public int getQueueSaveTime() {
        return 20 * 60 * 5; // 5 minutes
    }

    @Override
    public boolean saveLockData(Lock lock) {
        Path lockFilePath = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/locks/" + lock.getUUID().toString() + ".json");
        File lockFile = lockFilePath.toFile();
        if (!lockFile.exists()) {
            try {
                if(!lockFile.getParentFile().exists()) {
                    if(!lockFile.getParentFile().mkdirs()) {
                        ErrorHandler.warning("Failed to create locks directory");
                        return false;
                    }
                }
                if (!lockFile.createNewFile()) {
                    ErrorHandler.warning("Failed to create lock file for: " + lock.getUUID());
                    return false;
                }
            } catch (IOException e) {
                ErrorHandler.severe("Error creating lock file for: " + lock.getUUID(), e);
                return false;
            }
        }

        try (Writer writer = new FileWriter(lockFile)) {
            gson.toJson(lock, writer);
            ErrorHandler.debug("Saved lock data for: " + lock.getUUID());
            return true;
        } catch (IOException e) {
            ErrorHandler.severe("Failed to save lock data for: " + lock.getUUID(), e);
            return false;
        }
    }

    @Override
    public Lock[] getAllLockData() {
        File lockDir = new File(RPUniverse.getInstance().getDataFolder().getPath() + "/locks/");
        if(!lockDir.exists()) {
            if(!lockDir.mkdirs()) {
                ErrorHandler.warning("Failed to create locks directory");
                return new Lock[0];
            }
            return new Lock[0];
        }
        File[] files = lockDir.listFiles();
        if (files == null) {
            ErrorHandler.warning("No lock files found or locks directory does not exist");
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
                ErrorHandler.severe("Failed to load lock data from file: " + file.getName(), e);
            }
        }
        ErrorHandler.debug("Loaded " + locks.size() + " locks");
        return locks.toArray(new Lock[0]);
    }

    @Override
    public void removeLockData(Lock lock) {
        Path lockFilePath = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/locks/" + lock.getUUID().toString() + ".json");
        File lockFile = lockFilePath.toFile();
        if(lockFile.exists()) {
            boolean result = lockFile.delete();
            if (result) {
                ErrorHandler.debug("Removed lock data for: " + lock.getUUID());
            } else {
                ErrorHandler.warning("Failed to remove lock data for: " + lock.getUUID());
            }
        } else {
            ErrorHandler.debug("Lock file does not exist for: " + lock.getUUID());
        }
    }
}