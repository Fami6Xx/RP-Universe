package me.fami6xx.rpuniverse.core.jobs.types.basic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import me.fami6xx.rpuniverse.core.jobs.SellStep;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.jobs.types.JobTypeData;

import java.util.ArrayList;
import java.util.List;

public class BasicJobTypeData implements JobTypeData {
    private transient Gson gson = new GsonBuilder().create();

    @SerializedName("workingSteps")
    private List<JsonObject> workingStepsJSON = new ArrayList<>();
    @SerializedName("sellSteps")
    private List<JsonObject> sellStepsJSON = new ArrayList<>();

    public transient List<WorkingStep> workingSteps = new ArrayList<>();
    public transient List<SellStep> sellSteps = new ArrayList<>();

    @Override
    public JsonObject getDataInJson() {
        workingStepsJSON = new ArrayList<>();
        workingSteps.forEach(step -> workingStepsJSON.add(step.toJsonObject()));
        sellStepsJSON = new ArrayList<>();
        sellSteps.forEach(step -> sellStepsJSON.add(step.toJsonObject()));
        return gson.toJsonTree(this).getAsJsonObject();
    }

    @Override
    public JobTypeData setDataFromJson(JsonObject json) {
        BasicJobTypeData data = gson.fromJson(json, BasicJobTypeData.class);
        data.workingSteps = new ArrayList<>();
        data.workingStepsJSON.forEach(step -> data.workingSteps.add(WorkingStep.fromJsonObject(step)));
        data.sellSteps= new ArrayList<>();
        data.sellStepsJSON.forEach(step -> data.sellSteps.add(SellStep.fromJsonObject(step)));
        return data;
    }
}
