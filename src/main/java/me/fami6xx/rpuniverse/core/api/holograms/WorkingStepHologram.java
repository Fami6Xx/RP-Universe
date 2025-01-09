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
            page0.addAction(ClickType.RIGHT, new Action(
                    new ActionType(UUID.randomUUID().toString()) {
                        @Override
                        public boolean execute(Player player, String... strings) {
                            if (!shouldShow(player)) return true;

                            List<NeededItem> missingItems = new ArrayList<>();
                            // Check if player has all needed items
                            for (NeededItem neededItem : step.getNeededItems()) {
                                if (!player.getInventory().containsAtLeast(neededItem.getItem(), neededItem.getAmount())) {
                                    missingItems.add(neededItem);
                                }
                            }

                            if (!missingItems.isEmpty()) {
                                player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().missingNeededItem));
                                player.sendMessage(FamiUtils.format("&7" + missingItems.stream()
                                        .map(neededItem -> " - " + neededItem.getAmount() + "x " + (neededItem.getItem().hasItemMeta() && neededItem.getItem().getItemMeta().hasDisplayName() ? neededItem.getItem().getItemMeta().getDisplayName() : neededItem.getItem().getType().name()))
                                        .collect(Collectors.joining("\n"))));
                                return true;
                            }

                            // Remove needed items
                            for (NeededItem neededItem : step.getNeededItems()) {
                                removeItems(player, neededItem.getItem(), neededItem.getAmount());
                            }
                            setSecondStage();
                            return true;
                        }
                    }, ""
            ));

            addAdminOpenAction(ClickType.LEFT, page0);
        } else {
            page0.addAction(ClickType.RIGHT, new Action(
                    new ActionType(UUID.randomUUID().toString()) {
                        @Override
                        public boolean execute(Player player, String... strings) {
                            if (!shouldShow(player)) return true;

                            List<NeededItem> missingItems = new ArrayList<>();
                            // Check if player has all needed items
                            for (NeededItem neededItem : step.getNeededItems()) {
                                if (!player.getInventory().containsAtLeast(neededItem.getItem(), neededItem.getAmount())) {
                                    missingItems.add(neededItem);
                                }
                            }

                            if (!missingItems.isEmpty()) {
                                player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().missingNeededItem));
                                player.sendMessage(FamiUtils.format("&7" + missingItems.stream()
                                        .map(neededItem -> " - " + neededItem.getAmount() + "x " + (neededItem.getItem().hasItemMeta() && neededItem.getItem().getItemMeta().hasDisplayName() ? neededItem.getItem().getItemMeta().getDisplayName() : neededItem.getItem().getType().name()))
                                        .collect(Collectors.joining("\n"))));
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
                    }, ""
            ));

            addAdminOpenAction(ClickType.LEFT, page0);
        }

        DHAPI.updateHologram(getHologram().getName());
        getHologram().getShowPlayers().forEach(playerUuid -> {
            Player p = Bukkit.getPlayer(playerUuid);
            if (p != null) {
                if (shouldShow(p)) {
                    getHologram().showClickableEntities(p);
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

        progressBar = new ProgressBarString(
                "",
                step.getTimeForStep(),
                () -> DHAPI.setHologramLine(page1, 2, FamiUtils.format(progressBar.getString())),
                () -> {
                    Location dropLocation = getBaseLocation().clone().add(0, -1, 0);

                    if (step.getPossibleDrops().isEmpty()) {
                        recreatePages();
                        return;
                    }
                    if (step.getPossibleDrops().get(0).getChance() <= 0) {
                        recreatePages();
                        return;
                    }

                    List<PossibleDrop> dropsSorted = new ArrayList<>(
                            step.getPossibleDrops()
                                    .stream()
                                    .sorted((d1, d2) -> Double.compare(d2.getChance(), d1.getChance()))
                                    .toList()
                    );
                    Collections.reverse(dropsSorted);

                    double randomVal = Math.random() * 100;
                    double cumulativeChance = 0;
                    for (PossibleDrop drop : dropsSorted) {
                        cumulativeChance += drop.getChance();
                        if (randomVal <= cumulativeChance) {
                            dropLocation.getWorld().dropItem(dropLocation, drop.getItem());
                            break;
                        }
                    }
                    recreatePages();
                }
        );
        progressBar.runTaskTimer(RPUniverse.getJavaPlugin(), 0L, 1L);
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
}
