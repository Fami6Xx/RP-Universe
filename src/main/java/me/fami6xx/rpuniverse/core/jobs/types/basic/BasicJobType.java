package me.fami6xx.rpuniverse.core.jobs.types.basic;

import com.google.gson.JsonObject;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.SellStepLocationAddedEvent;
import me.fami6xx.rpuniverse.core.api.WorkingStepLocationAddedEvent;
import me.fami6xx.rpuniverse.core.api.WorkingStepLocationRemovedEvent;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.SellStep;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BasicJobType implements JobType {
    private final Job job;
    private transient BasicJobTypeData data;

    public BasicJobType(Job job) {
        this.job = job;
        this.data = new BasicJobTypeData();
    }

    public BasicJobType() {
        this.job = null;
    }

    @Override
    public String getName() {
        return "Basic";
    }

    @Override
    public String getDescription() {
        return "Create WorkingSteps and SellSteps!";
    }

    @Override
    public boolean hasAdminMenu() {
        return true;
    }

    @Override
    public void initialize() {
        if (!data.workingSteps.isEmpty()) {
            for (WorkingStep step : data.workingSteps) {
                if(step.getWorkingLocations().isEmpty()) continue;

                step.getWorkingLocations().forEach(location -> Bukkit.getPluginManager().callEvent(new WorkingStepLocationAddedEvent(step, location)));
            }
        }

        if (!data.sellSteps.isEmpty()) {
            for (SellStep step : data.sellSteps) {
                Bukkit.getPluginManager().callEvent(new SellStepLocationAddedEvent(step, step.getLocation()));
            }
        }
    }

    @Override
    public void stop() {
        if (!data.workingSteps.isEmpty()) {
            for (WorkingStep step : data.workingSteps) {
                if(step.getWorkingLocations().isEmpty()) continue;

                step.getWorkingLocations().forEach(location -> Bukkit.getPluginManager().callEvent(new WorkingStepLocationRemovedEvent(step, location)));
            }
        }

        if (!data.sellSteps.isEmpty()) {
            for (SellStep step : data.sellSteps) {
                Bukkit.getPluginManager().callEvent(new SellStepLocationAddedEvent(step, step.getLocation()));
            }
        }
    }

    @Override
    public void openAdminMenu(Player player) {
        new BasicAdminMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), job, this.data).open();
    }

    @Override
    public boolean hasBossMenu() {
        return false;
    }

    @Override
    public void openBossMenu(Player player) {}

    @Override
    public @Nullable ItemStack getIcon() {
        return FamiUtils.makeItem(Material.STICK, "&7Basic", "&7Create/Test WorkingSteps and SellSteps!");
    }

    @Override
    public JobType getNewInstance(Job job) {
        return new BasicJobType(job);
    }

    @Override
    public JsonObject getJsonJobTypeData() {
        return data != null ? data.getDataInJson() : new BasicJobTypeData().getDataInJson();
    }

    @Override
    public void fromJsonJobTypeData(JsonObject json) {
        data = (BasicJobTypeData) new BasicJobTypeData().setDataFromJson(json);
    }
}
