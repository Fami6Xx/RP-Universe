package me.fami6xx.rpuniverse.core.api.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.actions.Action;
import eu.decentsoftware.holograms.api.actions.ActionType;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.WorkingStepLocationRemovedEvent;
import me.fami6xx.rpuniverse.core.api.menus.WorkingStepInteractableMenu;
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.misc.PlayerData;
import me.fami6xx.rpuniverse.core.misc.PlayerMode;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.misc.utils.ProgressBarString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class WorkingStepHologram extends famiHologram implements Listener {
    HoloAPI api = RPUniverse.getInstance().getHoloAPI();
    private ProgressBarString progressBar;

    WorkingStep step;
    Job job;

    public WorkingStepHologram(WorkingStep step, Location location, Job job) {
        super(
                DHAPI.createHologram(UUID.randomUUID().toString(), location)
        );

        this.step = step;
        this.job = job;

        getHologram().setDefaultVisibleState(false);
        updateVisibility(RPUniverse.getInstance().getConfiguration().getDouble("jobs.menuRange"), false);

        recreatePages();

        api.getVisibilityHandler().queue.add(
                () -> api.getVisibilityHandler().addToList(getUUID(), this)
        );
        Bukkit.getPluginManager().registerEvents(this, RPUniverse.getJavaPlugin());
    }

    @Override
    public void destroy() {
        HandlerList.unregisterAll(this);
        super.destroy();
    }

    @EventHandler
    public void WorkingStepLocationRemovedEvent(WorkingStepLocationRemovedEvent event) {
        if (event.getLocation().equals(getBaseLocation()) && event.getWorkingStep() == step) {
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
        if (progressBar != null) {
            progressBar.cancel();
        }
        setFirstStage();
    }

    private void setFirstStage() {
        getHologram().updateAll();
        int size = getHologram().getPages().size();
        for (int i = 0; i < size; i++) {
            DHAPI.removeHologramPage(getHologram(), i);
        }
        getHologram().updateAll();
        HologramPage page0 = DHAPI.addHologramPage(getHologram());
        DHAPI.addHologramLine(page0, FamiUtils.format("&c&l" + job.getName()));
        DHAPI.addHologramLine(page0, "");
        DHAPI.addHologramLine(page0, FamiUtils.format("&7" + step.getName()));
        DHAPI.addHologramLine(page0, FamiUtils.format("&7" + step.getDescription()));
        DHAPI.addHologramLine(page0, "");
        DHAPI.addHologramLine(page0, FamiUtils.format(RPUniverse.getLanguageHandler().interactToWork));

        if (!step.isInteractableFirstStage()) {
            page0.addAction(ClickType.LEFT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    // Check working step conditions
                    if(!shouldShow(player)) return true;
                    if (step.getItemNeeded() != null && !player.getInventory().containsAtLeast(step.getItemNeeded(), step.getAmountOfItemNeeded())) {
                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().missingNeededItem));
                        return true;
                    }

                    // Remove the needed amount of item
                    if (step.getItemNeeded() != null)
                        removeItems(player, step.getItemNeeded(), step.getAmountOfItemNeeded());

                    // Start working
                    setSecondStage();

                    return true;
                }
            }, ""));
        } else {
            page0.addAction(ClickType.LEFT, new Action(new ActionType(UUID.randomUUID().toString()) {
                @Override
                public boolean execute(Player player, String... strings) {
                    // Check working step conditions
                    if(!shouldShow(player)) return true;
                    if (step.getItemNeeded() != null && !player.getInventory().containsAtLeast(step.getItemNeeded(), step.getAmountOfItemNeeded())) {
                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().missingNeededItem));
                        return true;
                    }

                    // Remove the needed amount of item
                    if (step.getItemNeeded() != null)
                        removeItems(player, step.getItemNeeded(), step.getAmountOfItemNeeded());

                    // Start working
                    new WorkingStepInteractableMenu(
                            RPUniverse.getInstance().getMenuManager().getPlayerMenu(player),
                            () -> setSecondStage()
                    ).open();

                    return true;
                }
            }, ""));
        }

        DHAPI.updateHologram(getHologram().getName());
        getHologram().getShowPlayers().forEach(player -> {
            Player player1 = Bukkit.getPlayer(player);
            if (player1 != null) {
                if (shouldShow(player1)) {
                    getHologram().showClickableEntities(player1);
                }
            }
        });
    }

    private void setSecondStage() {
        getHologram().hideClickableEntitiesAll();
        DHAPI.removeHologramPage(getHologram(), 0);
        HologramPage page1 = DHAPI.addHologramPage(getHologram());
        DHAPI.addHologramLine(page1, FamiUtils.format("&c&l" + job.getName()));
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, FamiUtils.format("&7" + step.getWorkingStepBeingDoneMessage()));
        DHAPI.addHologramLine(page1, "");
        progressBar = new ProgressBarString("", step.getTimeForStep(), () -> DHAPI.setHologramLine(page1, 2, FamiUtils.format(progressBar.getString())),
                () -> {
                    for (int i = 0; i < step.getAmountOfItemGiven(); i++) {
                        getBaseLocation().getWorld().dropItem(getBaseLocation().add(0, getBaseLocation().getY() / 2 * -1, 0), step.getItemGiven().clone().asOne());
                    }
                    recreatePages();
                });
        progressBar.runTaskTimer(RPUniverse.getJavaPlugin(), 0L, 1L);
    }

    public boolean removeItems(Player player, ItemStack itemToRemove, int amountToRemove) {
        Inventory inventory = player.getInventory();
        int amountRemaining = amountToRemove;

        // Create a clone of the item to remove to avoid modifying the original ItemStack
        ItemStack itemClone = itemToRemove.clone();
        itemClone.setAmount(1); // Set amount to 1 for comparison purposes

        // Iterate over the inventory contents
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);

            if (currentItem == null || !currentItem.isSimilar(itemClone)) {
                continue;
            }

            int currentAmount = currentItem.getAmount();

            if (currentAmount <= amountRemaining) {
                // Remove the entire stack
                inventory.setItem(i, null);
                amountRemaining -= currentAmount;
            } else {
                // Remove part of the stack
                currentItem.setAmount(currentAmount - amountRemaining);
                inventory.setItem(i, currentItem);
                amountRemaining = 0;
            }

            // Break out of the loop if we've removed enough items
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
                    return false;
                }else if (data.getSelectedPlayerJob().getPlayerPosition(player.getUniqueId()).isBoss()) {
                    shouldShow = true;
                }else if (data.getSelectedPlayerJob().getPlayerPosition(player.getUniqueId()).getWorkingStepPermissionLevel() >= step.getNeededPermissionLevel()) {
                    shouldShow = true;
                }
            }else if (data.getPlayerMode() == PlayerMode.ADMIN) {
                shouldShow = true;
            }
        }

        return shouldShow;
    }
}
