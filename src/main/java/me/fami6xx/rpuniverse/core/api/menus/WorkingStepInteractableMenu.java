package me.fami6xx.rpuniverse.core.api.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.IExecuteQueue;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WorkingStepInteractableMenu extends Menu {
    private final IExecuteQueue afterExecute;
    private final Map<Integer, Boolean> paneSlots = new HashMap<>();
    private final static Random random = new Random();

    // After being done, call afterExecute#execute()
    // It has no parameters and returns void

    public WorkingStepInteractableMenu(PlayerMenu menu, IExecuteQueue afterExecute) {
        super(menu);
        this.afterExecute = afterExecute;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().clickGreenItemsToContinue);
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true); // Prevent taking items

        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        int slot = e.getSlot();

        // Check if the clicked slot is one of the green panes that haven't been clicked yet
        if (paneSlots.containsKey(slot) && !paneSlots.get(slot)) {
            // Change the green pane to red
            ItemStack redPane = FamiUtils.makeItem(Material.RED_STAINED_GLASS_PANE, RPUniverse.getLanguageHandler().clickedItemDisplayName);
            inventory.setItem(slot, redPane);
            paneSlots.put(slot, true); // Mark as clicked

            // Check if all green panes have been clicked
            boolean allClicked = paneSlots.values().stream().allMatch(clicked -> clicked);

            if (allClicked) {
                afterExecute.execute();
                player.closeInventory();
            }
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        paneSlots.clear();

        int numberOfPanes = random.nextInt(11) + 5; // 5-15 panes

        while (paneSlots.size() < numberOfPanes) {
            int slot = random.nextInt(getSlots());

            if (!paneSlots.containsKey(slot) && inventory.getItem(slot) == null) {
                ItemStack greenPane = FamiUtils.makeItem(Material.GREEN_STAINED_GLASS_PANE, RPUniverse.getLanguageHandler().clickMeItemDisplayName);
                inventory.setItem(slot, greenPane);
                paneSlots.put(slot, false);
            }
        }

        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return List.of(MenuTag.ADMIN);
    }
}
