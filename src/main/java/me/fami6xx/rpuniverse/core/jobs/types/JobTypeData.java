package me.fami6xx.rpuniverse.core.jobs.types;

import com.google.gson.JsonObject;

public interface  JobTypeData {
    /**
     * Retrieves the data of the job type in JSON format.
     * <p>
     *  I highly suggest using GSON to convert the data to JSON.
     * @return The data of the job type in JSON format.
     */
    JsonObject getDataInJson();

    /**
     * Sets the data of the job type from JSON format.
     * @param json The data of the job type in JSON format.
     * @return The data of the job type in Object format that you can use.
     */
    JobTypeData setDataFromJson(JsonObject json);
}
