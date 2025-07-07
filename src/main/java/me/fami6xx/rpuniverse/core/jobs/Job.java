package me.fami6xx.rpuniverse.core.jobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.decentsoftware.holograms.api.actions.Action;
import eu.decentsoftware.holograms.api.actions.ActionType;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.JobLoadedEvent;
import me.fami6xx.rpuniverse.core.api.JobRenamedEvent;
import me.fami6xx.rpuniverse.core.api.MoneyAddedToJobBankEvent;
import me.fami6xx.rpuniverse.core.api.MoneyRemovedFromJobBankEvent;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.StaticHologram;
import me.fami6xx.rpuniverse.core.invoice.InvoiceModule;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin.JobAdminMenu;
import me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.user.JobBossMenu;
import me.fami6xx.rpuniverse.core.jobs.types.JobType;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.gsonadapters.LocationAdapter;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a job in the system.
 */
public class Job {
    private static final Logger LOGGER = RPUniverse.getInstance().getLogger(); // Kept for backward compatibility
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();
    private transient JobType jobType;
    private transient Hologram bossMenuHologram;
    private UUID jobUUID = UUID.randomUUID();
    private String jobTypeName = null;
    private JsonObject JSONJobTypeData = null;
    private HashMap<UUID, String> playerPositionsSave = new HashMap<>();

    private transient Map<UUID, Position> playerPositions;
    private List<Position> jobPositions;
    private String jobName;
    private double jobBank = 0;
    private Location bossMenuLocation;
    private boolean bossCanEditPositions = false;
    private boolean playersReceiveSalary = true;
    private int salaryInterval = 360;
    private boolean salaryBeingRemovedFromBank = true;
    private boolean bossCanRemoveMoneyFromBank = true;

    /**
     * Creates a new AJob instance that is empty.
     */
    public Job() {
        playerPositions = new HashMap<>();
        jobPositions = new ArrayList<>();
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     */
    public Job(String jobName, int jobBank, Location bossMenuLocation) {
        this();
        this.jobName = jobName;
        this.jobBank = jobBank;
        this.bossMenuLocation = bossMenuLocation;
        createBossMenuHologram();
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     * @param jobPositions     The list of positions associated with the job.
     */
    public Job(String jobName, int jobBank, Location bossMenuLocation, List<Position> jobPositions) {
        this(jobName, jobBank, bossMenuLocation);
        this.jobPositions = jobPositions;
        createBossMenuHologram();
    }

    /**
     * Creates a new AJob instance with the given parameters.
     *
     * @param jobName          The name of the job.
     * @param jobBank          The initial amount of money in the job bank. Must be a positive integer.
     * @param bossMenuLocation The location of the boss menu.
     * @param jobPositions     The list of positions associated with the job.
     * @param playerPositions  The map of player UUID to their assigned position.
     */
    public Job(String jobName, int jobBank, Location bossMenuLocation, List<Position> jobPositions, Map<UUID, Position> playerPositions) {
        this(jobName, jobBank, bossMenuLocation, jobPositions);
        this.playerPositions = playerPositions;
        createBossMenuHologram();
    }

    public UUID getJobUUID() {
        return jobUUID;
    }

    /**
     * Initializes the object by creating the boss menu hologram.
     */
    protected void initialize(){
        ErrorHandler.debug("Initializing job: " + jobName + " (UUID: " + jobUUID + ")");
        createBossMenuHologram();

        if(playerPositionsSave == null) {
            ErrorHandler.debug("playerPositionsSave was null, creating new HashMap");
            playerPositionsSave = new HashMap<>();
        }

        if(!playerPositionsSave.isEmpty()){
            ErrorHandler.debug("Loading " + playerPositionsSave.size() + " player positions from save data");
            for(UUID playerUUID : playerPositionsSave.keySet()){
                Position position = getPositionByName(playerPositionsSave.get(playerUUID));
                if(position != null){
                    playerPositions.put(playerUUID, position);
                    ErrorHandler.debug("Loaded position " + position.getName() + " for player " + playerUUID);
                } else {
                    ErrorHandler.warning("Could not find position " + playerPositionsSave.get(playerUUID) + " for player " + playerUUID);
                }
            }
        }

        if(jobType != null){
            ErrorHandler.debug("Initializing job type: " + jobTypeName);
            jobType.fromJsonJobTypeData(JSONJobTypeData);
            jobType.initialize();
        } else if (jobTypeName != null) {
            ErrorHandler.warning("Job type name is set to " + jobTypeName + " but jobType is null");
        }

        JobLoadedEvent event = new JobLoadedEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        ErrorHandler.debug("Job initialized: " + jobName);
    }

    /**
     * Removes the job by deleting the boss menu hologram if it exists.
     */
    protected void remove(){
        ErrorHandler.debug("Removing job: " + jobName + " (UUID: " + jobUUID + ")");

        if(bossMenuHologram != null){
            ErrorHandler.debug("Deleting boss menu hologram for job: " + jobName);
            bossMenuHologram.delete();
        } else {
            ErrorHandler.debug("No boss menu hologram to delete for job: " + jobName);
        }

        if (jobType != null) {
            ErrorHandler.debug("Stopping job type: " + jobTypeName);
            jobType.stop();
        } else {
            ErrorHandler.debug("No job type to stop for job: " + jobName);
        }

        ErrorHandler.debug("Removing " + playerPositions.size() + " players from job: " + jobName);
        for(UUID playerUUID : playerPositions.keySet()){
            if(playerPositions.get(playerUUID).isBoss()){
                ErrorHandler.debug("Closing boss menus for player: " + playerUUID);
                RPUniverse.getInstance().getMenuManager().closeAllMenusUUIDPredicate(p -> p.equals(playerUUID), MenuTag.BOSS);
            }

            removePlayerFromJob(playerUUID);
        }

        ErrorHandler.debug("Removing all invoices made for this job");
        InvoiceModule module = (InvoiceModule) RPUniverse.getInstance().getModuleManager().getModule("Invoices");
        if (module != null) {
            if (module.isEnabled()) {
                module.getManager().forceDeleteInvoicesByJob(jobUUID.toString());
            }
        }

        ErrorHandler.debug("Job removed: " + jobName);
    }

    /**
     * Creates a hologram for the boss menu.
     * If bossMenuLocation is not null, creates a static hologram with the specified range.
     * The hologram displays information about the job, such as job name, job bank, and job type.
     * The hologram has two pages - one for regular players and one for admins.
     * The regular player page allows opening the JobAdminMenu.
     * The admin page allows opening the JobAdminMenu as well.
     */
    protected void createBossMenuHologram(){
        ErrorHandler.debug("Creating boss menu hologram for job: " + jobName);

        if(bossMenuLocation != null){
            if(bossMenuHologram != null){
                ErrorHandler.debug("Deleting existing boss menu hologram for job: " + jobName);
                bossMenuHologram.delete();
            }

            Job job = this;

            int range;

            HashMap<String, String> replace = new HashMap<>();
            try {
                range = RPUniverse.getInstance().getConfiguration().getInt("jobs.menuRange");
                ErrorHandler.debug("Using menu range: " + range + " for job: " + jobName);
            } catch (Exception exc){
                replace.put("{value}", "jobs.menuRange");
                String errorMessage = FamiUtils.formatWithPrefix(FamiUtils.replace(RPUniverse.getLanguageHandler().invalidValueInConfigMessage, replace));
                ErrorHandler.severe("Failed to get menu range from config: " + errorMessage, exc);
                return;
            }

            Location toCreate = bossMenuLocation.clone();
            toCreate.add(0, 1.5, 0);
            ErrorHandler.debug("Creating hologram at location: " + toCreate.getWorld().getName() + 
                " X:" + toCreate.getX() + " Y:" + toCreate.getY() + " Z:" + toCreate.getZ());

            StaticHologram staticHologram = new StaticHologram(toCreate, false, range, false) {
                @Override
                public int getPageToDisplay(Player player) {
                    return job.getPageToDisplay(player);
                }

                @Override
                public boolean shouldShow(Player player) {
                    return RPUniverse.getPlayerData(player.getUniqueId().toString()).shouldDisplayJob(job);
                }
            };

            Hologram holo = staticHologram.getHologram();
            this.bossMenuHologram = holo;

            replace.put("{jobName}", jobName);
            replace.put("{jobBank}", String.valueOf(jobBank));
            if(jobTypeName != null)
                replace.put("{jobType}", jobTypeName);
            else
                replace.put("{jobType}", "None");

            if(holo.size() == 0){
                ErrorHandler.debug("Adding two pages to hologram for job: " + jobName);
                holo.addPage();
                holo.addPage();
            } else if(holo.size() == 1){
                ErrorHandler.debug("Adding one page to hologram for job: " + jobName);
                holo.addPage();
            }

            String[] hologramLines = RPUniverse.getLanguageHandler().jobBossMenuHologram.split("~");
            HologramPage bossPage = holo.getPage(0);
            ErrorHandler.debug("Creating boss page with " + hologramLines.length + " lines for job: " + jobName);
            for(String line : hologramLines){
                line = FamiUtils.replaceAndFormat(line, replace);
                bossPage.addLine(new HologramLine(bossPage, bossPage.getNextLineLocation(), line));
            }

            bossPage.addAction(ClickType.RIGHT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    ErrorHandler.debug("Player " + player.getName() + " clicked on boss menu hologram for job: " + jobName);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new JobBossMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), job).open();
                        }
                    }.runTask(RPUniverse.getInstance());
                    return true;
                }
            }, ""));

            String[] adminHologramLines = RPUniverse.getLanguageHandler().jobBossMenuAdminHologram.split("~");
            HologramPage adminPage = holo.getPage(1);
            ErrorHandler.debug("Creating admin page with " + adminHologramLines.length + " lines for job: " + jobName);
            for(String line : adminHologramLines){
                line = FamiUtils.replaceAndFormat(line, replace);
                adminPage.addLine(new HologramLine(adminPage, adminPage.getNextLineLocation(), line));
            }

            adminPage.addAction(ClickType.RIGHT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    ErrorHandler.debug("Admin " + player.getName() + " clicked on boss menu hologram for job: " + jobName);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new JobAdminMenu(RPUniverse.getInstance().getMenuManager().getPlayerMenu(player), job).open();
                        }
                    }.runTask(RPUniverse.getInstance());
                    return true;
                }
            }, ""));

            ErrorHandler.debug("Boss menu hologram created successfully for job: " + jobName);
        } else {
            ErrorHandler.warning("Cannot create boss menu hologram for job: " + jobName + " - bossMenuLocation is null");
        }
    }

    /**
     * Retrieves the page to display for a given player.
     *
     * @param player the player whose page to retrieve
     * @return the page number to display, either 1 if the player has permission for editing jobs, or 0 otherwise
     */
    private int getPageToDisplay(Player player){
        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());

        return data.hasPermissionForEditingJobs() ? 1 : 0;
    }

    /**
     * Retrieves the name of the job.
     *
     * @return The name of the job as a String.
     */
    public String getName() {
        return jobName;
    }

    /**
     * Retrieves the bossCanEditPositions field.
     * @return The bossCanEditPositions field as a boolean.
     */
    public boolean isBossCanEditPositions() {
        return bossCanEditPositions;
    }

    /**
     * Sets the bossCanEditPositions field.
     * @param bossCanEditPositions The value to set the bossCanEditPositions field to.
     */
    public void setBossCanEditPositions(boolean bossCanEditPositions) {
        this.bossCanEditPositions = bossCanEditPositions;
    }

    /**
     * Retrieves the playersReceiveSalary field.
     * @return The playersReceiveSalary field as a boolean.
     */
    public boolean isPlayersReceiveSalary() {
        return playersReceiveSalary;
    }

    /**
     * Sets the playersReceiveSalary field.
     * @param playersReceiveSalary The value to set the playersReceiveSalary field to.
     */
    public void setPlayersReceiveSalary(boolean playersReceiveSalary) {
        this.playersReceiveSalary = playersReceiveSalary;
    }

    /**
     * Retrieves the salaryInterval field.
     * This field is in seconds.
     * @return The salaryInterval field as an integer.
     */
    public int getSalaryInterval() {
        return salaryInterval;
    }

    /**
     * Sets the salaryInterval field.
     * This field is in seconds.
     * @param salaryInterval The value to set the salaryInterval field to.
     */
    public void setSalaryInterval(int salaryInterval) {
        this.salaryInterval = salaryInterval;
    }

    /**
     * Renames the job with the given new name.
     *
     * @param newName The new name for the job. Must not be null.
     */
    public void renameJob(String newName) {
        if(newName == null || newName.isEmpty()) {
            ErrorHandler.warning("Cannot rename job to an empty name.");
            return;
        }
        ErrorHandler.debug("Renaming job from " + jobName + " to " + newName + " (UUID: " + jobUUID + ")");
        if(newName.equals(jobName)) {
            ErrorHandler.debug("Job name is already " + newName + ", no changes made.");
            return;
        }

        // Check if the new name already exists
        if(RPUniverse.getInstance().getJobsHandler().getJobByName(newName) != null) {
            ErrorHandler.warning("A job with the name " + newName + " already exists.");
            return;
        }

        // Call the JobRenamedEvent to notify listeners about the job name change
        new BukkitRunnable() {
            @Override
            public void run() {
                JobRenamedEvent event = new JobRenamedEvent(Job.this, getName(), newName);
                Bukkit.getPluginManager().callEvent(event);
                ErrorHandler.debug("Called the JobRenamedEvent for job: " + jobName);
            }
        }.runTask(RPUniverse.getInstance());
        ErrorHandler.debug("Job name changed successfully from " + jobName + " to " + newName);

        this.jobName = newName;
        createBossMenuHologram();
        RPUniverse.getInstance().getMenuManager().closeAllJobMenus(j -> j == this);
    }

    /**
     * Retrieves the location of the boss menu associated with the job.
     *
     * @return The location of the boss menu as a Location object.
     */
    public Location getBossMenuLocation() {
        return bossMenuLocation;
    }

    /**
     * Sets the location of the boss menu associated with the job.
     *
     * @param location The location of the boss menu. Must not be null.
     */
    public void setBossMenuLocation(@Nonnull Location location){
        this.bossMenuLocation = location;
        createBossMenuHologram();
    }

    /**
     * Retrieves the job type of the job.
     *
     * @return The job type of the job as a JobType object.
     */
    @Nullable
    public JobType getJobType() {
        return jobType;
    }

    /**
     * Retrieves the name of the job type.
     *
     * @return The name of the job type as a String.
     */
    public String getJobTypeName() {
        return jobTypeName;
    }

    /**
     * Sets the job type of the job. Requires that the Job Type has been new instanced.
     * <code>JobType#getNewInstance(Job)</code>
     *
     * @param jobType The job type to be set. Must not be null.
     */
    public void setJobType(@NotNull JobType jobType) {
        ErrorHandler.debug("Setting job type for job: " + jobName + " (UUID: " + jobUUID + ")");
        ErrorHandler.debug("New job type: " + jobType.getName());

        this.jobType = jobType;
        this.jobTypeName = jobType.getName();
        this.JSONJobTypeData = null;

        ErrorHandler.debug("Initializing job type: " + jobTypeName + " for job: " + jobName);
        this.jobType.initialize();

        ErrorHandler.debug("Closing all job menus for job: " + jobName);
        RPUniverse.getInstance().getMenuManager().closeAllJobMenus(j -> j == this);

        ErrorHandler.debug("Job type set successfully for job: " + jobName);
    }

    /**
     * Retrieves the job type data of the job.
     *
     * @return The job type data of the job as a String.
     */
    public JsonObject getJobTypeData() {
        return JSONJobTypeData;
    }

    /**
     * Moves the position up or down in the job positions list.
     *
     * @param positionName the name of the position to move
     * @param moveUp       true if the position should be moved up, false otherwise
     */
    public void movePositionUpAndDown(String positionName, boolean moveUp){
        for(int i = 0; i < jobPositions.size(); i++){
            if(jobPositions.get(i).getName().equals(positionName)){
                if(moveUp){
                    if(i == 0) return;
                    Collections.swap(jobPositions, i, i - 1);
                }else{
                    if(i == jobPositions.size() - 1) return;
                    Collections.swap(jobPositions, i, i + 1);
                }
                RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this, MenuTag.JOB_ALL_POSITIONS);
                return;
            }
        }
    }

    /**
     * Adds a position to the jobPositions list.
     *
     * @param position The position to be added.
     */
    public void addPosition(Position position) {
        jobPositions.add(position);
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
    }

    /**
     * Edits the details of a position in the job.
     *
     * @param positionName   The name of the position to be edited.
     * @param updatedPosition The updated details of the position.
     */
    public void editPosition(String positionName, Position updatedPosition) {
        for(Position position : jobPositions) {
            if(position.getName().equals(positionName)) {
                if(position.isBoss() && !updatedPosition.isBoss()){
                    for(UUID playerUUID : playerPositions.keySet()) {
                        if (playerPositions.get(playerUUID).equals(position)) {
                            RPUniverse.getInstance().getMenuManager().closeAllMenusUUIDPredicate(p -> p.equals(playerUUID), MenuTag.BOSS);
                        }
                    }
                }
                position.setName(updatedPosition.getName());
                position.setSalary(updatedPosition.getSalary());
                position.setWorkingStepPermissionLevel(updatedPosition.getWorkingStepPermissionLevel());
                position.setBoss(updatedPosition.isBoss());
                position.setDefault(updatedPosition.isDefault());
            }
        }
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
    }

    /**
     * Removes a position from the jobPositions list based on the position name.
     *
     * @param positionName The name of the position to be removed.
     */
    public void removePosition(String positionName) {
        int index = -1;
        for(int i = 0; i < jobPositions.size(); i++) {
            if(jobPositions.get(i).getName().equals(positionName)) {
                index = i;
                break;
            }
        }

        if(index == -1) {
            return;
        }

        Position removedPosition = jobPositions.get(index);
        jobPositions.remove(index);

        if(playerPositions.containsValue(removedPosition)){
            for(UUID playerUUID : playerPositions.keySet()){
                if(playerPositions.get(playerUUID).equals(removedPosition)){
                    if(removedPosition.isBoss()){
                        RPUniverse.getInstance().getMenuManager().closeAllMenusUUIDPredicate(p -> p.equals(playerUUID), MenuTag.BOSS);
                    }
                    playerPositions.remove(playerUUID);
                    if(jobPositions.isEmpty()){
                        RPUniverse.getPlayerData(playerUUID.toString()).removeJob(this);
                    }else addPlayerToJob(playerUUID);
                }
            }
        }

        RPUniverse.getInstance().getMenuManager().closeAllJobMenus(j -> j == this, MenuTag.JOB_POSITION_INTERNAL);
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
    }

    /**
     * Retrieves the list of positions associated with the job.
     *
     * @return A List of Position objects representing the job positions.
     */
    public List<Position> getPositions(){
        return jobPositions;
    }

    /**
     * Retrieves a Position object from the jobPositions list based on the provided position name.
     *
     * @param name The name of the position to retrieve. Must not be null.
     * @return The Position object with the specified name, or null if no position with that name exists.
     */
    public Position getPositionByName(String name){
        for(Position position : jobPositions){
            if(position.getName().equalsIgnoreCase(name)){
                return position;
            }
        }

        return null;
    }

    /**
     * Checks if a list of positions contains at least one default position and at least one boss position.
     *
     * @param positions The list of positions to check.
     * @return {@code true} if the list contains at least one default position and at least one boss position, {@code false} otherwise.
     */
    public boolean checkPositions(List<Position> positions){
        boolean hasDefault = false;
        boolean hasBoss = false;

        for(Position position : positions){
            if(position.isDefault()){
                if(!hasDefault) hasDefault = true;
                else return false;
            }
            if(position.isBoss()){
                if(!hasBoss) hasBoss = true;
                else return false;
            }
        }

        return hasDefault && hasBoss;
    }

    /**
     * Checks if the job is ready to be executed by validating various conditions.
     *
     * @return A list of error messages indicating the reasons why the job is not ready. An empty list signifies that the job is ready.
     */
    public List<String> isJobReady(){
        List<String> errors = new ArrayList<>();
        if(jobPositions.isEmpty()) errors.add("No positions have been added to the job.");
        if(!checkPositions(jobPositions)) errors.add("The job must have at least one default position and at least one boss position.");

        if (jobTypeName != null)
            if(jobType == null) errors.add("The job type '" + jobTypeName + "' does not exist or has not been loaded.");

        return errors;
    }

    /**
     * Changes the position of a player by updating the player's UUID and the new position in the playerPositions map.
     *
     * @param playerUUID    The UUID of the player to change the position for.
     * @param newPosition   The new position to assign to the player.
     */
    public void changePlayerPosition(UUID playerUUID, Position newPosition) {
        ErrorHandler.debug("Changing position for player " + playerUUID + " in job " + jobName);

        if(playerPositions.containsKey(playerUUID)) {
            Position oldPosition = playerPositions.get(playerUUID);
            ErrorHandler.debug("Player " + playerUUID + " current position: " + oldPosition.getName() + " in job " + jobName);
            ErrorHandler.debug("Player " + playerUUID + " new position: " + newPosition.getName() + " in job " + jobName);

            if(oldPosition.isBoss() && !newPosition.isBoss()){
                ErrorHandler.debug("Player " + playerUUID + " is being demoted from boss position, closing boss menus");
                RPUniverse.getInstance().getMenuManager().closeAllMenusUUIDPredicate(p -> p.equals(playerUUID), MenuTag.BOSS);
            }
        } else {
            ErrorHandler.debug("Player " + playerUUID + " did not have a position in job " + jobName + ", assigning new position: " + newPosition.getName());
        }

        playerPositions.put(playerUUID, newPosition);
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);

        Player player = Bukkit.getPlayer(playerUUID);
        if(player != null) {
            ErrorHandler.debug("Updating boss bar for player " + player.getName() + " in job " + jobName);
            RPUniverse.getInstance().getBossBarHandler().updateBossBar(player);
        } else {
            ErrorHandler.debug("Player " + playerUUID + " is offline, boss bar will be updated when they log in");
        }

        ErrorHandler.debug("Position changed successfully for player " + playerUUID + " in job " + jobName);
    }

    /**
     * Adds the specified amount of money to the job bank.
     * Calls the MoneyAddedToJobBankEvent.
     *
     * @param money The amount of money to add to the job bank. Must be a positive integer.
     */
    public void addMoneyToJobBank(double money) {
        ErrorHandler.debug("Adding " + money + " to job bank for job " + jobName + " (current balance: " + jobBank + ")");
        BigDecimal bd = new BigDecimal(jobBank + money);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        jobBank = bd.doubleValue();
        Job job = this;
        new BukkitRunnable(){
            @Override
            public void run() {
                MoneyAddedToJobBankEvent event = new MoneyAddedToJobBankEvent(money, jobBank, job);
                Bukkit.getPluginManager().callEvent(event);
                ErrorHandler.debug("MoneyAddedToJobBankEvent called for job " + jobName + " (amount: " + money + ", new balance: " + jobBank + ")");
            }
        }.runTask(RPUniverse.getInstance());
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
        ErrorHandler.debug("Money added to job bank successfully. New balance: " + jobBank + " for job " + jobName);
    }

    /**
     * Removes the specified amount of money from the job bank.
     * Calls the MoneyRemovedFromJobBankEvent.
     *
     * @param money The amount of money to remove from the job bank. Must be a positive integer.
     * @return {@code true} if the money was successfully removed from the job bank, {@code false} otherwise.
     */
    public boolean removeMoneyFromJobBank(double money) {
        ErrorHandler.debug("Attempting to remove " + money + " from job bank for job " + jobName + " (current balance: " + jobBank + ")");
        if(jobBank >= money) {
            BigDecimal bd = new BigDecimal(jobBank - money);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            jobBank = bd.doubleValue();
            Job job = this;
            new BukkitRunnable(){
                @Override
                public void run() {
                    MoneyRemovedFromJobBankEvent event = new MoneyRemovedFromJobBankEvent(money, jobBank, job);
                    Bukkit.getPluginManager().callEvent(event);
                    ErrorHandler.debug("MoneyRemovedFromJobBankEvent called for job " + jobName + " (amount: " + money + ", new balance: " + jobBank + ")");
                }
            }.runTask(RPUniverse.getInstance());
            RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
            ErrorHandler.debug("Money removed from job bank successfully. New balance: " + jobBank + " for job " + jobName);
            return true;
        }
        ErrorHandler.warning("Failed to remove " + money + " from job bank for job " + jobName + " - insufficient funds (current balance: " + jobBank + ")");
        return false;
    }

    /**
     * Retrieves the current amount of money available in the job bank.
     *
     * @return An integer representing the current amount of money in the job bank.
     */
    public double getCurrentMoneyInJobBank() {
        BigDecimal bd = new BigDecimal(jobBank);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Retrieves the current amount of money available in the job bank.
     * It doesn't round the value.
     * It doesn't call any event.
     *
     * @param jobBank The new amount of money in the job bank.
     */
    public void setJobBank(double jobBank) {
        this.jobBank = jobBank;
    }

    /**
     * Adds a player to a job by associating their UUID with a position.
     *
     * @param playerUUID The UUID of the player to add.
     * @param position The position to associate the player with.
     */
    public void addPlayerToJob(UUID playerUUID, Position position) {
        ErrorHandler.debug("Adding player " + playerUUID + " to job " + jobName + " with position " + position.getName());
        playerPositions.put(playerUUID, position);
        RPUniverse.getPlayerData(playerUUID.toString()).addJob(this);
        RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
        ErrorHandler.debug("Player " + playerUUID + " added to job " + jobName + " successfully");
    }

    /**
     * Adds a player to a job by associating their UUID with the default position.
     * @param playerUUID The UUID of the player to add.
     */
    public void addPlayerToJob(UUID playerUUID){
        ErrorHandler.debug("Adding player " + playerUUID + " to job " + jobName + " with default position");
        for(Position position : jobPositions) {
            if(position.isDefault()) {
                RPUniverse.getPlayerData(playerUUID.toString()).addJob(this);
                playerPositions.put(playerUUID, position);
                RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
                if (!RPUniverse.getPlayerData(playerUUID.toString()).getPlayerJobs().contains(this)) {
                    ErrorHandler.debug("Player " + playerUUID + " wasn't added to job " + jobName + " successfully");
                }
                ErrorHandler.debug("Player " + playerUUID + " added to job " + jobName + " with default position " + position.getName());
                return;
            }
        }

        ErrorHandler.severe("No default position found for job " + jobName + " even though player " + playerUUID + " was added to the job!");
    }

    /**
     * Removes a player from the job by removing their UUID from the playerPositions map.
     *
     * @param playerUUID The UUID of the player to remove from the job.
     */
    public void removePlayerFromJob(UUID playerUUID) {
        ErrorHandler.debug("Removing player " + playerUUID + " from job " + jobName);

        if(playerPositions.containsKey(playerUUID)) {
            Position position = playerPositions.get(playerUUID);
            ErrorHandler.debug("Player " + playerUUID + " had position " + position.getName() + " in job " + jobName);

            if(position.isBoss()){
                ErrorHandler.debug("Closing boss menus for player " + playerUUID + " as they were a boss in job " + jobName);
                RPUniverse.getInstance().getMenuManager().closeAllMenusUUIDPredicate(p -> p.equals(playerUUID), MenuTag.BOSS);
            }

            playerPositions.remove(playerUUID);
            RPUniverse.getPlayerData(playerUUID.toString()).removeJob(this);
            RPUniverse.getInstance().getMenuManager().reopenJobMenus(j -> j == this);
            ErrorHandler.debug("Player " + playerUUID + " removed from job " + jobName + " successfully");
        } else {
            ErrorHandler.warning("Attempted to remove player " + playerUUID + " from job " + jobName + " but they were not in the job");
        }
    }

    /**
     * Checks whether a player is in a job by checking if their UUID is associated with a position.
     *
     * @param playerUUID The UUID of the player to check.
     * @return {@code true} if the player is in a job, {@code false} otherwise.
     */
    public boolean isPlayerInJob(UUID playerUUID) {
        return playerPositions.containsKey(playerUUID);
    }

    /**
     * Retrieves the position of a player identified by their UUID.
     *
     * @param playerUUID The UUID of the player.
     * @return The position of the player, or null if the player's position is not found.
     */
    @Nullable
    public Position getPlayerPosition(UUID playerUUID){
        return playerPositions.getOrDefault(playerUUID, null);
    }

    /**
     * Retrieves a list of all players in a job.
     *
     * @return a {@link List} of {@link UUID} representing the player IDs in the job.
     */
    public List<UUID> getAllPlayersInJob(){
        return new ArrayList<>(playerPositions.keySet());
    }

    /**
     * Determines if a player can kick another player based on their positions.
     *
     * @param playerUUID        the UUID of the player initiating the kick
     * @param playerToKickUUID  the UUID of the player to be kicked
     * @return true if the player can kick the other player, false otherwise
     */
    public boolean canPlayerKickPlayer(UUID playerUUID, UUID playerToKickUUID){
        if(playerPositions.containsKey(playerUUID) && playerPositions.containsKey(playerToKickUUID)){
            Position playerPosition = playerPositions.get(playerUUID);
            Position playerToKickPosition = playerPositions.get(playerToKickUUID);

            return jobPositions.indexOf(playerPosition) < jobPositions.indexOf(playerToKickPosition);
        }
        return false;
    }

    /**
     * Returns a list of all positions that a player can assign to.
     *
     * @param playerUUID the UUID of the player
     * @return a list of Position objects that the player can assign to, empty if none found
     */
    public List<Position> getAllPositionsPlayerCanAssign(UUID playerUUID){
        List<Position> positions = new ArrayList<>();
        if(playerPositions.containsKey(playerUUID)){
            Position playerPosition = playerPositions.get(playerUUID);
            for(Position position : jobPositions){
                if(jobPositions.indexOf(playerPosition) < jobPositions.indexOf(position)){
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    /**
     * Prepares the Job object for saving by serializing the jobType to a JSON string.
     * <p>
     * This method checks if the jobType field is not null. If it is not null, it serializes the jobType
     * to a JSON string using the {@link JobType#getJsonJobTypeData()} method. The JSON string is then stored
     * in the JSONJobTypeData field of the Job object.
     * <p>
     * Note that it is crucial to use the GSON library to serialize the jobType to a JSON string.
     *
     * @see JobType#getJsonJobTypeData()
     */
    public void prepareForSave(){
        ErrorHandler.debug("Preparing job for save: " + jobName + " (UUID: " + jobUUID + ")");

        if(jobType != null) {
            ErrorHandler.debug("Serializing job type data for job: " + jobName);
            JSONJobTypeData = jobType.getJsonJobTypeData();
        } else {
            ErrorHandler.debug("No job type to serialize for job: " + jobName);
        }

        if(!playerPositions.isEmpty()){
            ErrorHandler.debug("Saving " + playerPositions.size() + " player positions for job: " + jobName);
            playerPositionsSave.clear();
            for(UUID playerUUID : playerPositions.keySet()){
                Position position = playerPositions.get(playerUUID);
                playerPositionsSave.put(playerUUID, position.getName());
                ErrorHandler.debug("Saved position " + position.getName() + " for player " + playerUUID + " in job " + jobName);
            }
        } else {
            ErrorHandler.debug("No player positions to save for job: " + jobName);
        }

        ErrorHandler.debug("Job prepared for save successfully: " + jobName);
    }

    /**
     * Retrieves a Job object with the specified job name.
     *
     * @param jobName The name of the job to retrieve. Must not be null.
     * @return The Job object with the specified job name, or null if no job with that name exists.
     */
    @Nullable
    public static Job getJob(String jobName){
        for(Job job : RPUniverse.getInstance().getJobsHandler().getJobs()){
            if(job.getName().equalsIgnoreCase(jobName)){
                return job;
            }
        }

        return null;
    }

    /**
     * Retrieves a Job object with the specified job UUID.
     *
     * @param jobUUID The UUID of the job to retrieve. Must not be null.
     * @return The Job object with the specified job UUID, or null if no job with that UUID exists.
     */
    @Nullable
    public static Job getJobByUUID(String jobUUID){
        for(Job job : RPUniverse.getInstance().getJobsHandler().getJobs()){
            if(job.getJobUUID().toString().equals(jobUUID)){
                return job;
            }
        }

        return null;
    }

    public static Job fromString(String s) {
        ErrorHandler.debug("Attempting to convert string to Job instance");
        try {
            Job job = GSON.fromJson(s, Job.class);
            if (job != null) {
                ErrorHandler.debug("Successfully converted string to Job instance: " + job.getName() + " (UUID: " + job.getJobUUID() + ")");
            } else {
                ErrorHandler.warning("Converted string to null Job instance");
            }
            return job;
        } catch (Exception e) {
            ErrorHandler.severe("Error converting string to Job instance: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String toString() {
        ErrorHandler.debug("Converting job to string: " + jobName + " (UUID: " + jobUUID + ")");

        if(jobType != null) {
            ErrorHandler.debug("Serializing job type data for job: " + jobName);
            JSONJobTypeData = jobType.getJsonJobTypeData();
        }

        String json = GSON.toJson(this);
        ErrorHandler.debug("Job serialized successfully: " + jobName);
        return json;
    }

    /**
     * Retrieves the bossCanRemoveMoneyFromBank field.
     * @return The bossCanRemoveMoneyFromBank field as a boolean.
     */
    public boolean isSalaryBeingRemovedFromBank() {
        return salaryBeingRemovedFromBank;
    }

    /**
     * Sets the salaryBeingRemovedFromBank field.
     * @param salaryBeingRemovedFromBank The value to set the salaryBeingRemovedFromBank field to.
     */
    public void setSalaryBeingRemovedFromBank(boolean salaryBeingRemovedFromBank) {
        this.salaryBeingRemovedFromBank = salaryBeingRemovedFromBank;
    }

    /**
     * Retrieves the bossCanRemoveMoneyFromBank field.
     * @return The bossCanRemoveMoneyFromBank field as a boolean.
     */
    public boolean isBossCanRemoveMoneyFromBank() {
        return bossCanRemoveMoneyFromBank;
    }

    /**
     * Sets the bossCanRemoveMoneyFromBank field.
     * @param bossCanRemoveMoneyFromBank The value to set the bossCanRemoveMoneyFromBank field to.
     */
    public void setBossCanRemoveMoneyFromBank(boolean bossCanRemoveMoneyFromBank) {
        this.bossCanRemoveMoneyFromBank = bossCanRemoveMoneyFromBank;
    }
}
