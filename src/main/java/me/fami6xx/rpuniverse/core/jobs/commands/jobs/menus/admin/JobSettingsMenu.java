package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus.admin;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class JobSettingsMenu extends Menu implements Listener {
    private final Menu previousMenu;

    public JobSettingsMenu(PlayerMenu menu, Menu previousMenu) {
        super(menu);
        this.previousMenu = previousMenu;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format("&c&lRPU &8» &7Job Settings");
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        switch (e.getSlot()) {
            case 2:
                playerMenu.getEditingJob().setSalaryBeingRemovedFromBank(!playerMenu.getEditingJob().isSalaryBeingRemovedFromBank());
                this.open();
                break;
            case 3:
                playerMenu.getEditingJob().setBossCanEditPositions(!playerMenu.getEditingJob().isBossCanEditPositions());
                this.open();
                break;
            case 4:
                playerMenu.getEditingJob().setPlayersReceiveSalary(!playerMenu.getEditingJob().isPlayersReceiveSalary());
                this.open();
                break;
            case 5:
                playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), "&aType the new salary interval in seconds.");
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), "&7Type &6cancel &7to cancel.");
                waitForSalaryIntervalInput(playerMenu.getPlayer(), this);
                break;
            case 6:
                playerMenu.getEditingJob().setBossCanRemoveMoneyFromBank(!playerMenu.getEditingJob().isBossCanRemoveMoneyFromBank());
                this.open();
                break;
            case 8:
                previousMenu.open();
                break;
            default:
                break;
        }

    }

    @Override
    public void setMenuItems() {
        inventory.setItem(2, FamiUtils.makeItem(Material.DIAMOND, "&cSalary being removed from bank", "&7Current: " + (playerMenu.getEditingJob().isSalaryBeingRemovedFromBank() ? "&aYes" : "&cNo"), "&7Click to toggle"));
        inventory.setItem(3, FamiUtils.makeItem(Material.ANVIL, "&cBoss can edit positions", "&7Current: " + (playerMenu.getEditingJob().isBossCanEditPositions() ? "&aYes" : "&cNo"), "&7Click to toggle"));
        inventory.setItem(4, FamiUtils.makeItem(Material.ENDER_PEARL, "&cPlayers receive salary", "&7Current: " + (playerMenu.getEditingJob().isPlayersReceiveSalary() ? "&aYes" : "&cNo"), "&7Click to toggle"));
        inventory.setItem(5, FamiUtils.makeItem(Material.DIAMOND, "&cSalary time in seconds", "&7Current: &c" + playerMenu.getEditingJob().getSalaryInterval() + "&7 seconds", "", "&7Determines at what interval do players receive their salary in seconds", "&7Click to change"));
        inventory.setItem(6, FamiUtils.makeItem(Material.GOLD_INGOT, "&cBoss can remove money from bank", "&7Current: " + (playerMenu.getEditingJob().isBossCanRemoveMoneyFromBank() ? "&aYes" : "&cNo"), "&7Click to toggle"));

        inventory.setItem(8, FamiUtils.makeItem(Material.BARRIER, "&cBack", "&7Go back to the previous menu"));
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        tags.add(MenuTag.JOB);
        return tags;
    }

    private void waitForSalaryIntervalInput(Player player, Menu currentMenu) {
        Listener listener = new Listener() {
            @EventHandler
            public void onPlayerChat(AsyncPlayerChatEvent event) {
                if (event.getPlayer().equals(player)) {
                    event.setCancelled(true);
                    if (event.getMessage().equalsIgnoreCase("cancel")) {
                        FamiUtils.sendMessageWithPrefix(player, "&cCancelled.");
                        HandlerList.unregisterAll(this);
                        return;
                    }
                    try {
                        int interval = Integer.parseInt(event.getMessage());
                        playerMenu.getEditingJob().setSalaryInterval(interval);
                        FamiUtils.sendMessageWithPrefix(player, "&aSalary interval set to &6" + interval + " &aseconds.");
                        currentMenu.open();
                        HandlerList.unregisterAll(this);
                    } catch (NumberFormatException e) {
                        FamiUtils.sendMessageWithPrefix(player, "&cInvalid number. Please try again.");
                    }
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, RPUniverse.getInstance());
    }
}
