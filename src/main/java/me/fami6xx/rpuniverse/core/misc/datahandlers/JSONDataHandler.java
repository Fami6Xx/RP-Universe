package me.fami6xx.rpuniverse.core.misc.datahandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.misc.PlayerData;

import java.util.logging.Logger;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONDataHandler implements IDataHandler {
    private Gson gson;
    private final Path dataDirectory = Paths.get(RPUniverse.getInstance().getDataFolder().getPath() + "/playerdata/");
    private final Logger logger =  Logger.getLogger(JSONDataHandler.class.getName());

    @Override
    public boolean startUp() {
        try {
            this.gson = new GsonBuilder().setPrettyPrinting().create();
            File dataDir = new File(dataDirectory.toUri());
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
        Path playerFilePath = dataDirectory.resolve(uuid + ".json");
        try (Reader reader = new FileReader(playerFilePath.toFile())) {
            return gson.fromJson(reader, PlayerData.class);
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean savePlayerData(PlayerData data) {
        Path playerFilePath = dataDirectory.resolve(data.getUuid() + ".json");
        try (Writer writer = new FileWriter(playerFilePath.toFile())) {
            gson.toJson(data, writer);
            return true;
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public int getQueueSaveTime() {
        return 20 * 60 * 5; // 5 minutes
    }
}