package me.fami6xx.rpuniverse.core.menuapi.utils;

import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import org.bukkit.entity.Player;

public class PlayerMenu {
    private final Player player;
    private Menu currentMenu;
    private Job editingJob;

    public PlayerMenu(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }

    public void setCurrentMenu(Menu menu) {
        this.currentMenu = menu;
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public Job getEditingJob() {
        return editingJob;
    }

    public void setEditingJob(Job job) {
        this.editingJob = job;
    }
}
