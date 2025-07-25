package me.fami6xx.rpuniverse.core.api.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.actions.Action;
import eu.decentsoftware.holograms.api.actions.ActionType;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import eu.decentsoftware.holograms.api.utils.items.HologramItem;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.WorkingStepLocationRemovedEvent;
import me.fami6xx.rpuniverse.core.api.menus.WorkingStepEditorMenu;
import me.fami6xx.rpuniverse.core.api.menus.WorkingStepInteractableMenu;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.PossibleDrop;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep.NeededItem;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.misc.utils.ProgressBarString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class WorkingStepHologram extends famiHologram implements Listener {

    private final HoloAPI api = RPUniverse.getInstance().getHoloAPI();
    private ProgressBarString progressBar;
    private static final Random random = new Random();

    /**
     * If you’re using a fixed depletionAmount, each location can be used “X” times.
     * We'll track usage left in this static map.
     * Key = location of the hologram, Value = how many uses remain.
     */
    private static final Map<Location,Integer> depletionUsageMap = new HashMap<>();

    private final WorkingStep step;
    private final Job job;

    public WorkingStepHologram(WorkingStep step, Location location, Job job) {
        super(DHAPI.createHologram(UUID.randomUUID().toString(), location));

        this.step = step;
        this.job = job;

        getHologram().setDefaultVisibleState(false);
        updateVisibility(RPUniverse.getInstance().getConfiguration().getDouble("jobs.menuRange"), false);

        recreatePages();

        api.getVisibilityHandler().queue.add(() -> api.getVisibilityHandler().addToList(getUUID(), this));
        Bukkit.getPluginManager().registerEvents(this, RPUniverse.getJavaPlugin());

        // Initialize usage map if needed
        if (step.isAllowDepletion() && step.getDepletionAmount() >= 0) {
            depletionUsageMap.putIfAbsent(location, step.getDepletionAmount());
        }
    }

    @Override
    public void destroy() {
        HandlerList.unregisterAll(this);
        super.destroy();
    }

    @EventHandler
    public void WorkingStepLocationRemovedEvent(WorkingStepLocationRemovedEvent event) {
        if (event.getLocation().equals(getBaseLocation()) && event.getWorkingStep() == step) {
            // Clean up from the depletion map when the location is removed entirely
            depletionUsageMap.remove(getBaseLocation());
            destroy();
        }
    }

    @Override
    public Location getBaseLocation() {
        return getHologram().getLocation();
    }

    @Override
    public int getPageToDisplay(Player player) {
        return 0;
    }

    public void recreatePages() {
        // Cancel any running progress bar if we are re-displaying pages
        if (progressBar != null) {
            progressBar.cancel();
        }

        // If location is currently depleted, don't reset to normal pages
        if (isLocationDepleted()) {
            setDepletedStage();
            return;
        }

        // Otherwise, go to the normal (first) stage
        setFirstStage();
    }

    /**
     * The initial stage displayed on the hologram.
     */
    private void setFirstStage() {
        removePages(); // remove old pages, if any

        HologramPage page0 = DHAPI.addHologramPage(getHologram());
        DHAPI.addHologramLine(page0, FamiUtils.format("&c&l" + job.getName()));
        DHAPI.addHologramLine(page0, "");
        DHAPI.addHologramLine(page0, FamiUtils.format("&7" + step.getName()));
        DHAPI.addHologramLine(page0, FamiUtils.format("&7" + step.getDescription()));
        DHAPI.addHologramLine(page0, "");
        DHAPI.addHologramLine(page0, FamiUtils.format(RPUniverse.getLanguageHandler().interactToWork));

        if (!step.isInteractableFirstStage()) {
            // Normal usage, no separate menu
            page0.addAction(ClickType.RIGHT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    if (!shouldShow(player)) return true;

                    // Check if player is in AMODE, if so, don't check items
                    PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());
                    if (data.getPlayerMode() == PlayerMode.ADMIN) {
                        // Admins can skip item checks
                        player.sendMessage(FamiUtils.formatWithPrefix("&aSkipping item checks due to admin mode."));
                        ErrorHandler.info(player.getName() + " is in admin mode, skipping item checks for working step: " + step.getName());
                        setSecondStage();
                        return true;
                    }

                    List<NeededItem> missingItems = findMissingItems(player);
                    if (!missingItems.isEmpty()) {
                        sendMissingItemsMessage(player, missingItems);
                        return true;
                    }
                    // Remove needed items from player
                    for (NeededItem neededItem : step.getNeededItems()) {
                        removeItems(player, neededItem.getItem(), neededItem.getAmount());
                    }

                    setSecondStage();
                    return true;
                }
            }, ""));
            addAdminOpenAction(ClickType.LEFT, page0);
        } else {
            // Interactable menu stage
            page0.addAction(ClickType.RIGHT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    if (!shouldShow(player)) return true;

                    // Check if player is in AMODE, if so, don't check items
                    PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());
                    if (data.getPlayerMode() == PlayerMode.ADMIN) {
                        // Admins can skip item checks
                        // Open the interactable menu
                        player.sendMessage(FamiUtils.formatWithPrefix("&aSkipping item checks due to admin mode."));
                        ErrorHandler.info(player.getName() + " is in admin mode, skipping item checks for working step: " + step.getName());
                        new WorkingStepInteractableMenu(
                                RPUniverse.getInstance().getMenuManager().getPlayerMenu(player),
                                () -> setSecondStage()
                        ).open();
                        return true;
                    }

                    List<NeededItem> missingItems = findMissingItems(player);
                    if (!missingItems.isEmpty()) {
                        sendMissingItemsMessage(player, missingItems);
                        return true;
                    }
                    // Remove needed items
                    for (NeededItem neededItem : step.getNeededItems()) {
                        removeItems(player, neededItem.getItem(), neededItem.getAmount());
                    }
                    // Open the interactable menu
                    new WorkingStepInteractableMenu(
                            RPUniverse.getInstance().getMenuManager().getPlayerMenu(player),
                            () -> setSecondStage()
                    ).open();
                    return true;
                }
            }, ""));
            addAdminOpenAction(ClickType.LEFT, page0);
        }

        DHAPI.updateHologram(getHologram().getName());
        showHologramToEligiblePlayers();
    }

    /**
     * The second stage is the "working step in progress" stage.
     */
    private void setSecondStage() {
        removePages();

        HologramPage page1 = DHAPI.addHologramPage(getHologram());
        DHAPI.addHologramLine(page1, FamiUtils.format("&c&l" + job.getName()));
        DHAPI.addHologramLine(page1, "");
        // lines 2,3 intentionally empty so we can update line 2 with progress bar
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, FamiUtils.format("&7" + step.getWorkingStepBeingDoneMessage()));
        DHAPI.addHologramLine(page1, "");

        progressBar = new ProgressBarString(
                "",
                step.getTimeForStep(),
                // Called each tick to update the progress bar
                () -> DHAPI.setHologramLine(page1, 2, FamiUtils.format(progressBar.getString())),
                // Called when the progress completes
                () -> {
                    // 1) Drop logic
                    Location dropLocation = getBaseLocation().clone().add(0, -1, 0);
                    performPossibleDrop(dropLocation);

                    // 2) Now handle depletion
                    boolean justDepleted = checkAndHandleDepletion();
                    if (justDepleted) {
                        // If we depleted the location, show the "depleted" stage
                        return;
                    }

                    // 3) If not depleted, reset to first stage
                    recreatePages();
                }
        );
        progressBar.runTaskTimer(RPUniverse.getJavaPlugin(), 0L, 1L);
    }

    /**
     * If the location is depleted, show a "depleted" hologram page.
     */
    private void setDepletedStage() {
        removePages();

        HologramPage depletedPage = DHAPI.addHologramPage(getHologram());
        DHAPI.addHologramLine(depletedPage, FamiUtils.format("&c&l" + job.getName()));
        DHAPI.addHologramLine(depletedPage, "");
        String depletedMessage = RPUniverse.getLanguageHandler().workingStepDepletedMessage;
        String[] depletedMessageSplit = depletedMessage.split("~");
        for (String line : depletedMessageSplit) {
            DHAPI.addHologramLine(depletedPage, FamiUtils.format(line));
        }

        addAdminOpenAction(ClickType.LEFT, depletedPage);

        DHAPI.updateHologram(getHologram().getName());
    }

    /**
     * Removes all pages from this hologram.
     */
    private void removePages() {
        getHologram().hideClickableEntitiesAll();
        int size = getHologram().getPages().size();
        for (int i = 0; i < size; i++) {
            DHAPI.removeHologramPage(getHologram(), i);
        }
        getHologram().updateAll();
    }

    /**
     * Called at the end of the second stage to see if we should deplete the location,
     * and if so, schedule a replenish.
     *
     * @return true if the location was just depleted, false otherwise.
     */
    private boolean checkAndHandleDepletion() {
        // If depletion is not enabled, do nothing
        if (!step.isAllowDepletion()) {
            return false;
        }

        // CHANCE-BASED Depletion
        if (step.getDepletionChance() >= 0) {
            double rng = random.nextDouble(); // 0.0 to 1.0
            if (rng <= step.getDepletionChance()) {
                // Deplete the location
                setDepletedStage();
                scheduleReplenish();
                return true;
            }
        }
        // AMOUNT-BASED Depletion
        else if (step.getDepletionAmount() >= 0) {
            int usageLeft = depletionUsageMap.getOrDefault(getBaseLocation(), step.getDepletionAmount());
            usageLeft--;
            depletionUsageMap.put(getBaseLocation(), usageLeft);

            if (usageLeft <= 0) {
                setDepletedStage();
                scheduleReplenish();
                return true;
            }
        }
        return false;
    }

    /**
     * Schedules the location to replenish after 'replenishTimeMilliseconds'.
     */
    private void scheduleReplenish() {
        long ticks = step.getReplenishTimeTicks();
        Bukkit.getScheduler().runTaskLater(RPUniverse.getJavaPlugin(), () -> {
            // Clear usage for this location so it's restored
            depletionUsageMap.remove(getBaseLocation());
            recreatePages();
        }, ticks);
    }

    /**
     * Checks if this location is currently depleted:
     *  - For chance-based depletion, we only track it as "depleted" if it’s within the replenish window.
     *  - For amount-based depletion, we see if usage is at 0 or below.
     */
    private boolean isLocationDepleted() {
        // If not allowing depletion, never "depleted"
        if (!step.isAllowDepletion()) {
            return false;
        }

        // If chance-based (>=0), we do not store usage. If it was depleted, we'd see no usage in the map,
        // but we do forcibly set a "depleted" stage until scheduleReplenish() is done.
        // We'll detect that by checking if usage was forced to some special value, or we can see if
        // the location is still in depletionUsageMap with a negative usage.
        // Easiest approach: For chance-based, the moment we get depleted, we put usageLeft=0:
        if (step.getDepletionChance() >= 0) {
            // If it's not in the map, or not "0", it's not depleted yet
            // We'll store usage=0 for "depleted" while we wait.
            Integer usage = depletionUsageMap.get(getBaseLocation());
            return usage != null && usage <= 0;
        }

        // If amount-based
        if (step.getDepletionAmount() >= 0) {
            Integer usage = depletionUsageMap.get(getBaseLocation());
            // If usage left is 0 or below => it’s depleted
            return usage != null && usage <= 0;
        }

        return false;
    }

    /**
     * Does the random drop logic at the end of the second stage.
     */
    private void performPossibleDrop(Location dropLocation) {
        // If no possible drops, just return
        if (step.getPossibleDrops().isEmpty()) {
            return;
        }
        // For safety, if the first drop chance is <= 0, do nothing
        if (step.getPossibleDrops().get(0).getChance() <= 0) {
            return;
        }
        // Sort them from the highest chance to the lowest chance
        List<PossibleDrop> dropsSorted = new ArrayList<>(step.getPossibleDrops());
        dropsSorted.sort((d1, d2) -> Double.compare(d2.getChance(), d1.getChance()));

        double randomVal = Math.random() * 100; // 0 - 100
        double cumulativeChance = 0;
        for (PossibleDrop drop : dropsSorted) {
            if (drop.getItem().getAmount() <= 0) {
                ErrorHandler.warning("PossibleDrop with non-positive amount: " + drop.getItem().getAmount() + " for step: " + step.getName());
                continue; // Skip invalid drops
            }

            if (drop.getChance() == 100) {
                // Drop the item at the specified location
                dropItemAtLocation(dropLocation, drop.getItem());
                continue;
            }
            cumulativeChance += drop.getChance();
            if (randomVal <= cumulativeChance) {
                dropItemAtLocation(dropLocation, drop.getItem());
                break;
            }
        }
    }

    /**
     * Drops the item at the specified location.
     * If the item amount is greater than 1, it drops multiple items one by one.
     *
     * @param location The location where the item should be dropped.
     * @param item The item to drop.
     */
    private void dropItemAtLocation(Location location, ItemStack item) {
        if (item.getAmount() <= 0) {
            ErrorHandler.warning("Attempted to drop an item with non-positive amount: " + item.getAmount());
            return; // Skip invalid items
        }
        if (item.getAmount() > 1) {
            int amountToDrop = item.getAmount();
            ItemStack dropItem = item.clone();
            dropItem.setAmount(1);
            for (int i = 0; i < amountToDrop; i++) {
                location.getWorld().dropItem(location, dropItem);
            }
        } else {
            location.getWorld().dropItem(location, item);
        }
    }

    /**
     * Finds if the player lacks any required items for this step.
     */
    private List<NeededItem> findMissingItems(Player player) {
        List<NeededItem> missing = new ArrayList<>();
        for (NeededItem neededItem : step.getNeededItems()) {
            if (!player.getInventory().containsAtLeast(neededItem.getItem(), neededItem.getAmount())) {
                missing.add(neededItem);
            }
        }
        return missing;
    }

    private void sendMissingItemsMessage(Player player, List<NeededItem> missingItems) {
        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().missingNeededItem));
        player.sendMessage(FamiUtils.format("&7" +
                missingItems.stream()
                        .map(neededItem -> " - " + neededItem.getAmount() + "x " +
                                (neededItem.getItem().hasItemMeta() && neededItem.getItem().getItemMeta().hasDisplayName()
                                        ? neededItem.getItem().getItemMeta().getDisplayName()
                                        : neededItem.getItem().getType().name()))
                        .collect(Collectors.joining("\n"))));
    }

    private void addAdminOpenAction(ClickType clickType, HologramPage page) {
        page.addAction(clickType, new Action(
                new ActionType(UUID.randomUUID().toString()) {
                    @Override
                    public boolean execute(Player player, String... strings) {
                        PlayerData data = RPUniverse.getPlayerData(player.getUniqueId().toString());
                        if (data != null && data.getPlayerMode() == PlayerMode.ADMIN) {
                            new WorkingStepEditorMenu(
                                    RPUniverse.getInstance().getMenuManager().getPlayerMenu(player),
                                    step
                            ).open();
                        }
                        return true;
                    }
                }, ""
        ));
    }

    /**
     * Removes the specified number of matching items from the player's inventory.
     */
    public boolean removeItems(Player player, ItemStack itemToRemove, int amountToRemove) {
        Inventory inventory = player.getInventory();
        int amountRemaining = amountToRemove;

        ItemStack itemClone = itemToRemove.clone();
        itemClone.setAmount(1);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);
            if (currentItem == null || !currentItem.isSimilar(itemClone)) {
                continue;
            }
            int currentAmount = currentItem.getAmount();

            if (currentAmount <= amountRemaining) {
                inventory.setItem(i, null);
                amountRemaining -= currentAmount;
            } else {
                currentItem.setAmount(currentAmount - amountRemaining);
                inventory.setItem(i, currentItem);
                amountRemaining = 0;
            }
            if (amountRemaining <= 0) {
                break;
            }
        }
        return amountRemaining <= 0;
    }

    @Override
    public boolean shouldShow(Player player) {
        boolean shouldShow = false;
        PlayerData data = RPUniverse.getInstance().getPlayerData(player.getUniqueId().toString());

        if (data != null) {
            if (data.getSelectedPlayerJob() != null && data.getSelectedPlayerJob().equals(job)) {
                if (data.getSelectedPlayerJob().getPlayerPosition(player.getUniqueId()) == null) {
                    data.setSelectedPlayerJob(null);
                } else if (data.getSelectedPlayerJob().getPlayerPosition(player.getUniqueId()).isBoss()) {
                    shouldShow = true;
                } else if (data.getSelectedPlayerJob().getPlayerPosition(player.getUniqueId())
                        .getWorkingStepPermissionLevel() >= step.getNeededPermissionLevel()) {
                    shouldShow = true;
                }
            } else if (data.getPlayerMode() == PlayerMode.ADMIN) {
                shouldShow = true;
            }
        }
        return shouldShow;
    }

    /**
     * Shows clickable holograms only to players who should see them.
     */
    private void showHologramToEligiblePlayers() {
        getHologram().getShowPlayers().forEach(playerUuid -> {
            Player p = Bukkit.getPlayer(playerUuid);
            if (p != null && shouldShow(p)) {
                getHologram().showClickableEntities(p);
            }
        });
    }
}
