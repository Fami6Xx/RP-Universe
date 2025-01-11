package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.api.WorkingStepLocationRemovedEvent;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.WorkingStep;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AllWorkingStepsMenu extends EasyPaginatedMenu {
    private final Job job;
    private final List<WorkingStep> workingSteps;

    public AllWorkingStepsMenu(PlayerMenu menu, List<WorkingStep> workingSteps, Job job) {
        super(menu);
        this.job = job;
        this.workingSteps = workingSteps;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        WorkingStep step = workingSteps.get(index);
        List<String> lore = new ArrayList<>();
        lore.add(FamiUtils.format("&7Basic information:"));
        lore.add(FamiUtils.format("&7- &fName: &7" + step.getName()));
        lore.add(FamiUtils.format("&7- &fDescription: &7" + step.getDescription()));
        lore.add("");
        lore.add(FamiUtils.format("&7Needed items:"));
        if (step.getNeededItems().isEmpty()) {
            lore.add(FamiUtils.format("&7- &fNone"));
        }
        for (WorkingStep.NeededItem neededItem : step.getNeededItems()) {
            lore.add(FamiUtils.format("&7- &f" + neededItem.getAmount() + "x &e" + (neededItem.getItem().hasItemMeta() && neededItem.getItem().getItemMeta().hasDisplayName() ? neededItem.getItem().getItemMeta().getDisplayName() : neededItem.getItem().getType().name())));
        }
        ItemStack item = FamiUtils.makeItem(Material.BOOK, FamiUtils.format("&cWorking Step"), lore.toArray(new String[0]));
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(RPUniverse.getJavaPlugin(), "workingStep");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, index);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int getCollectionSize() {
        return workingSteps.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if (e.getSlot() == 45) {
            workingSteps.add(new WorkingStep(
                    new ArrayList<>(),
                    200,
                    new ArrayList<>(),
                    0,
                    "Edit this",
                    "Edit this",
                    "Edit this",
                    job.getJobUUID(),
                    false,
                    new ArrayList<>()
            ));
            new WorkingStepEditorMenu(playerMenu, workingSteps.get(workingSteps.size() - 1)).open();
        }

        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        NamespacedKey key = new NamespacedKey(RPUniverse.getJavaPlugin(), "workingStep");
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            int index = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
            if (e.isShiftClick()) {
                WorkingStep step = workingSteps.get(index);
                step.getWorkingLocations().forEach(location -> Bukkit.getPluginManager().callEvent(new WorkingStepLocationRemovedEvent(step, location)));
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), "&cRemoved working step.");
                workingSteps.remove(index);
                open();
                return;
            }

            new WorkingStepEditorMenu(playerMenu, workingSteps.get(index)).open();
        }
    }

    @Override
    public void addAdditionalItems() {
        ItemStack item = FamiUtils.makeItem(Material.EMERALD_BLOCK, "&6Add new working step");
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(RPUniverse.getJavaPlugin(), "addWorkingStep");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        inventory.setItem(45, item);

        ItemStack item2 = FamiUtils.makeItem(Material.BOOKSHELF, "&cRemove working step", "&7Shift click on a working step to remove it.");
        ItemMeta meta2 = item2.getItemMeta();
        NamespacedKey key2 = new NamespacedKey(RPUniverse.getJavaPlugin(), "removeWorkingStep");
        meta2.getPersistentDataContainer().set(key2, PersistentDataType.INTEGER, 1);
        item2.setItemMeta(meta2);
        inventory.setItem(53, item2);
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix("&6Working Steps");
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
