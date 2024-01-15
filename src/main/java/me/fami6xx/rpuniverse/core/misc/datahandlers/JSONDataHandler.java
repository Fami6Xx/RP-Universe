package me.fami6xx.rpuniverse.core.misc.datahandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.misc.PlayerData;

import java.util.logging.Logger;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONDataHandler implements IDataHandler {
    private Gson gson;
    private final Path playerDataDirectory = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/playerdata/");
    private final Path jobDataDirectory = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/jobs/");
    private final Logger logger =  Logger.getLogger(JSONDataHandler.class.getName());

    @Override
    public boolean startUp() {
        try {
            this.gson = new GsonBuilder().setPrettyPrinting().create();
            File dataDir = new File(playerDataDirectory.toUri());
            if(!dataDir.exists()){
                return dataDir.mkdirs();
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
        }
    }

    @Override
    public boolean savePlayerData(PlayerData data) {
        Path playerFilePath = playerDataDirectory.resolve(data.getUuid() + ".json");
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
        }
    }

    @Override
    public boolean saveJobData(String name, String data) {
        Path jobFilePath = jobDataDirectory.resolve(name + ".json");
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
            try (Reader reader = new FileReader(files[i])) {
                jobs[i] = gson.fromJson(reader, Job.class);
            } catch (IOException e) {
                logger.severe(e.getMessage());
                return null;
            }
        }
        return jobs;
    }

    @Override
    public int getQueueSaveTime() {
        return 20 * 60 * 5; // 5 minutes
    }
}