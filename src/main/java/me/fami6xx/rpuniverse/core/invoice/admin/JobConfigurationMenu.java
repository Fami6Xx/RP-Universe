package me.fami6xx.rpuniverse.core.invoice.admin;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.invoice.InvoiceManager;
import me.fami6xx.rpuniverse.core.invoice.language.InvoiceLanguage;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.ErrorHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Menu for configuring which jobs can create invoices.
 * <p>
 * This menu allows administrators to select which jobs can create invoices.
 */
public class JobConfigurationMenu extends EasyPaginatedMenu {

    private final InvoiceManager manager;
    private final InvoiceLanguage lang;
    private final List<Job> allJobs;

    /**
     * Creates a new JobConfigurationMenu.
     *
     * @param menu    The PlayerMenu instance
     * @param manager The InvoiceManager instance
     */
    public JobConfigurationMenu(PlayerMenu menu, InvoiceManager manager) {
        super(menu);
        this.manager = manager;
        this.lang = InvoiceLanguage.getInstance();

        // Get all jobs
        this.allJobs = new ArrayList<>(RPUniverse.getInstance().getJobsHandler().getJobs());

        // Sort jobs by name
        this.allJobs.sort(Comparator.comparing(Job::getName));
    }

    /**
     * Gets the name of the menu.
     *
     * @return The menu name
     */
    @Override
    public String getMenuName() {
        return FamiUtils.format("&6Job Invoice Configuration");
    }

    /**
     * Gets ItemStack you created from your collection
     * @param index Index of item you want to get from collection
     * @return ItemStack you create
     */
    @Override
    public ItemStack getItemFromIndex(int index) {
        if (index >= allJobs.size()) {
            return null;
        }

        Job job = allJobs.get(index);
        String jobUUID = job.getJobUUID().toString();
        boolean isAllowed = manager.isJobAllowedToCreateInvoices(jobUUID);

        ItemStack jobButton = new ItemStack(isAllowed ? Material.LIME_WOOL : Material.RED_WOOL);
        ItemMeta jobMeta = jobButton.getItemMeta();
        jobMeta.setDisplayName(FamiUtils.format("&f" + job.getName()));

        List<String> jobLore = new ArrayList<>();
        jobLore.add(FamiUtils.format(isAllowed ? "&aAllowed to create invoices" : "&cNot allowed to create invoices"));
        jobLore.add("");
        jobLore.add(FamiUtils.format("&7Click to " + (isAllowed ? "disable" : "enable") + " invoice creation"));

        jobMeta.setLore(jobLore);
        jobButton.setItemMeta(jobMeta);

        return jobButton;
    }

    /**
     * @return Size of collection you use
     */
    @Override
    public int getCollectionSize() {
        return allJobs.size();
    }

    /**
     * Handles click on your item
     * @param e Previously handled InventoryClickEvent
     */
    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        // Convert slot to index in the collection
        int index = getMaxItemsPerPage() * page + getSlotIndex(slot);

        if (index >= 0 && index < allJobs.size()) {
            Job job = allJobs.get(index);
            String jobUUID = job.getJobUUID().toString();

            // Toggle job's ability to create invoices
            if (manager.isJobAllowedToCreateInvoices(jobUUID)) {
                manager.removeJobAllowedToCreateInvoices(jobUUID);
                player.sendMessage(FamiUtils.formatWithPrefix("&cJob &f" + job.getName() + " &ccan no longer create invoices"));
            } else {
                manager.addJobAllowedToCreateInvoices(jobUUID);
                player.sendMessage(FamiUtils.formatWithPrefix("&aJob &f" + job.getName() + " &acan now create invoices"));
            }

            // Refresh menu
            setMenuItems();
        }
    }

    /**
     * A method where you can add your own items to the inventory border for example.
     */
    @Override
    public void addAdditionalItems() {
        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(FamiUtils.format("&aBack to System Settings"));
        backMeta.setLore(Arrays.asList(FamiUtils.format("&7Return to the system settings menu")));
        backButton.setItemMeta(backMeta);
        inventory.setItem(49, backButton);
    }

    /**
     * Handles clicks in the menu.
     *
     * @param e The inventory click event
     */
    @Override
    public void handleMenu(InventoryClickEvent e) {
        // Handle back button
        if (e.getSlot() == 49) {
            Player player = (Player) e.getWhoClicked();
            // Return to system settings menu
            try {
                new SystemSettingsMenu(playerMenu, manager).open();
                ErrorHandler.debug("Admin returned to system settings menu from job configuration");
            } catch (Exception ex) {
                ErrorHandler.severe("Error opening system settings menu", ex);
                player.sendMessage(FamiUtils.formatWithPrefix(lang.errorOpeningMenuMessage));
            }
            return;
        }

        // Let the parent class handle other clicks
        super.handleMenu(e);
    }

    /**
     * Gets the menu tags for this menu.
     *
     * @return A list of menu tags
     */
    @Override
    public List<MenuTag> getMenuTags() {
        return Arrays.asList(MenuTag.ADMIN, MenuTag.JOB);
    }
}
