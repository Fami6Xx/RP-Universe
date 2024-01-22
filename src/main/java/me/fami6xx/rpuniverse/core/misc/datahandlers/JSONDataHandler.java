package me.fami6xx.rpuniverse.core.misc.datahandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.ItemStackAdapter;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

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
        Path playerFilePath = playerDataDirectory.resolve(data.getUuid() + ".json");

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
}