package me.fami6xx.rpuniverse.core.api.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.actions.Action;
import eu.decentsoftware.holograms.api.actions.ActionType;
import eu.decentsoftware.holograms.api.actions.ClickType;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.SellStepLocationRemovedEvent; // Ensure this event exists or adjust accordingly
import me.fami6xx.rpuniverse.core.holoapi.HoloAPI;
import me.fami6xx.rpuniverse.core.holoapi.types.holograms.famiHologram;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.SellStep;
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

import java.util.HashMap;
import java.util.UUID;

public class SellStepHologram extends famiHologram implements Listener {

    private final HoloAPI api = RPUniverse.getInstance().getHoloAPI();
    private ProgressBarString progressBar;

    private final SellStep sellStep;
    private final Job job;

    public SellStepHologram(SellStep sellStep, Job job) {
        super(DHAPI.createHologram(UUID.randomUUID().toString(), sellStep.getLocation()));

        this.sellStep = sellStep;
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
    public void onSellStepLocationRemoved(SellStepLocationRemovedEvent event) {
        if (event.getLocation().equals(getBaseLocation()) && event.getSellStep() == sellStep) {
            destroy();
        }
    }

    @Override
    public Location getBaseLocation() {
        return sellStep.getLocation();
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
        DHAPI.addHologramLine(page0, FamiUtils.format("&7" + sellStep.getName()));
        DHAPI.addHologramLine(page0, FamiUtils.format("&7" + sellStep.getDescription()));
        DHAPI.addHologramLine(page0, "");
        DHAPI.addHologramLine(page0, sellStep.getItemToSell());
        DHAPI.addHologramLine(page0, FamiUtils.format(RPUniverse.getLanguageHandler().interactToSell));

        page0.addAction(ClickType.RIGHT, new Action(new ActionType(UUID.randomUUID().toString()) {
            @Override
            public boolean execute(Player player, String... strings) {
                if (!shouldShow(player)) return true;
                if (player.hasMetadata("sellStepAmountToSell")) {
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().alreadySelling));
                    return true;
                }

                // Check if player has items to sell
                ItemStack itemToSell = sellStep.getItemToSell();
                int maxSellAmount = sellStep.getMaxSellAmount();

                int amountPlayerHas = countItems(player, itemToSell);
                if (amountPlayerHas <= 0) {
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().missingNeededItem));
                    return true;
                }

                int amountToSell = Math.min(amountPlayerHas, maxSellAmount);

                // Remove the items from the player's inventory
                removeItems(player, itemToSell, amountToSell);

                // Store the amount being sold for use in setSecondStage
                player.setMetadata("sellStepAmountToSell", new org.bukkit.metadata.FixedMetadataValue(RPUniverse.getJavaPlugin(), amountToSell));

                // Start selling process
                setSecondStage(player);

                return true;
            }
        }, ""));

        DHAPI.updateHologram(getHologram().getName());
        getHologram().getShowPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (shouldShow(player)) {
                    getHologram().showClickableEntities(player);
                }
            }
        });
    }

    private void setSecondStage(Player player) {
        getHologram().hideClickableEntitiesAll();
        DHAPI.removeHologramPage(getHologram(), 0);
        HologramPage page1 = DHAPI.addHologramPage(getHologram());
        DHAPI.addHologramLine(page1, FamiUtils.format("&c&l" + job.getName()));
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, "");
        DHAPI.addHologramLine(page1, FamiUtils.format(RPUniverse.getLanguageHandler().processingSell));
        DHAPI.addHologramLine(page1, "");

        progressBar = new ProgressBarString("", sellStep.getTimeToSell(),
                () -> DHAPI.setHologramLine(page1, 2, FamiUtils.format(progressBar.getString())),
                () -> {
                    int amountSold = player.getMetadata("sellStepAmountToSell").get(0).asInt();

                    // Calculate rewards using itemValue
                    double itemPrice = sellStep.getItemValue();
                    double totalValue = itemPrice * amountSold;
                    double playerShare = totalValue * (sellStep.getPlayerPercentage() / 100.0);
                    double jobShare = totalValue * (sellStep.getJobPercentage() / 100.0);

                    // Add money to player
                    RPUniverse.getInstance().getEconomy().depositPlayer(player, playerShare);

                    // Add money to job
                    Job job = RPUniverse.getInstance().getJobsHandler().getJobByUUID(sellStep.getJobUUID().toString());
                    if (job != null) {
                        job.addMoneyToJobBank(jobShare);
                    }

                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("{amount}", String.valueOf(amountSold));
                    placeholders.put("{price}", String.format("%.2f", playerShare));
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().sellSuccess, placeholders);

                    // Remove metadata
                    player.removeMetadata("sellStepAmountToSell", RPUniverse.getJavaPlugin());

                    recreatePages();
                });
        progressBar.runTaskTimer(RPUniverse.getJavaPlugin(), 0L, 1L);
    }

    private int countItems(Player player, ItemStack itemToCount) {
        Inventory inventory = player.getInventory();
        int count = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.isSimilar(itemToCount)) {
                count += item.getAmount();
            }
        }

        return count;
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
                // Remove the entire stack
                inventory.setItem(i, null);
                amountRemaining -= currentAmount;
            } else {
                // Remove part of the stack
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
                    return false;
                } else if (data.getSelectedPlayerJob().getPlayerPosition(player.getUniqueId()).isBoss()) {
                    shouldShow = true;
                } else {
                    shouldShow = true; // Assuming all job members can see the sell step
                }
            } else if (data.getPlayerMode() == PlayerMode.ADMIN) {
                shouldShow = true;
            }
        }

        return shouldShow;
    }
}
