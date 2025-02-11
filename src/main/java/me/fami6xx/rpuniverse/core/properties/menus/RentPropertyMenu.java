package me.fami6xx.rpuniverse.core.properties.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import me.fami6xx.rpuniverse.core.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RentPropertyMenu extends Menu {

    private final Property property;
    private int selectedDuration; // in days
    private double totalPrice;

    private List<DurationOption> durationOptions = new ArrayList<>();

    private static class DurationOption {
        String displayName;
        int days;

        DurationOption(String displayName, int days) {
            this.displayName = displayName;
            this.days = days;
        }
    }

    public RentPropertyMenu(PlayerMenu playerMenu, Property property) {
        super(playerMenu);
        this.property = property;
        loadDurationOptions();
        if (!durationOptions.isEmpty()) {
            this.selectedDuration = durationOptions.get(0).days;
        } else {
            this.selectedDuration = 1; // Default to 1 day if no durations are configured
        }
        this.totalPrice = property.getPrice() * selectedDuration;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().rentPropertyMenuName);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    private void loadDurationOptions() {
        String durationsConfig = RPUniverse.getLanguageHandler().rentPropertyMenuDurations;
        String[] durationEntries = durationsConfig.split("~");
        durationOptions.clear();

        for (String entry : durationEntries) {
            String[] parts = entry.split(":");
            if (parts.length != 2) continue;
            String displayName = parts[0];
            int days;
            try {
                days = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                continue;
            }
            durationOptions.add(new DurationOption(displayName, days));
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = playerMenu.getPlayer();

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        Material clickedMaterial = e.getCurrentItem().getType();

        switch (clickedMaterial) {
            case CLOCK:
                // Handle rent duration selection
                String itemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                for (DurationOption option : durationOptions) {
                    String optionDisplayName = RPUniverse.getLanguageHandler().rentPropertyMenuDurationItemDisplayName.replace("{duration}", option.displayName);
                    if (itemName.equals(ChatColor.stripColor(FamiUtils.format(optionDisplayName)))) {
                        selectedDuration = option.days;
                        totalPrice = property.getPrice() * selectedDuration;
                        open();
                        break;
                    }
                }
                break;
            case EMERALD_BLOCK:
                // Handle rent confirmation with maximum duration enforcement
                long requestedDurationMs = selectedDuration * 24 * 60 * 60 * 1000L;
                long maxDurationMs = property.getRentMaximumDuration();
                if (maxDurationMs > 0 && requestedDurationMs > maxDurationMs) {
                    long maxDays = maxDurationMs / (24 * 60 * 60 * 1000L);
                    player.sendMessage(FamiUtils.formatWithPrefix("You cannot rent for more than " + maxDays + " days."));
                    return;
                }

                double price = totalPrice;
                if (RPUniverse.getInstance().getEconomy().has(player, price)) {
                    RPUniverse.getInstance().getEconomy().withdrawPlayer(player, price);
                    property.setOwner(player.getUniqueId());
                    property.setRentStart(System.currentTimeMillis());
                    property.setRentDuration(selectedDuration * 24 * 60 * 60 * 1000L); // convert days to milliseconds
                    property.updateLastActive();
                    RPUniverse.getInstance().getPropertyManager().saveProperty(property);
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().successfullyRentedPropertyMessage));
                    player.closeInventory();
                } else {
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorYouDoNotHaveEnoughMoneyToRentPropertyMessage));
                }
                break;
            case BARRIER:
                player.closeInventory();
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        loadDurationOptions();

        String[] durationLore = RPUniverse.getLanguageHandler().rentPropertyMenuDurationItemLore.split("~");
        HashMap<String, String> placeholders = new HashMap<>();

        int[] slots = {10, 13, 16}; // Adjust slots based on the number of durations

        for (int i = 0; i < durationOptions.size(); i++) {
            if (i >= slots.length) break; // Avoid IndexOutOfBoundsException
            DurationOption option = durationOptions.get(i);
            long requestedDurationMs = option.days * 24 * 60 * 60 * 1000L;
            long maxDurationMs = property.getRentMaximumDuration();
            if (maxDurationMs > 0 && requestedDurationMs > maxDurationMs) {
                continue; // Skip this duration option
            }
            placeholders.put("{price}", String.valueOf(property.getPrice() * option.days));
            placeholders.put("{duration}", option.displayName);
            List<String> loreList = Arrays.stream(durationLore).map(s -> FamiUtils.replace(s, placeholders)).toList();
            ItemStack item = FamiUtils.makeItem(Material.CLOCK, RPUniverse.getLanguageHandler().rentPropertyMenuDurationItemDisplayName.replace("{duration}", option.displayName),
                    loreList.toArray(new String[0]));
            if (selectedDuration == option.days) {
                item = FamiUtils.addGlow(item);
            }
            inventory.setItem(slots[i], item);
        }

        // Confirm Item
        String[] confirmLore = RPUniverse.getLanguageHandler().rentPropertyMenuConfirmRentItemLore.split("~");
        placeholders.put("{totalPrice}", String.valueOf(totalPrice));
        List<String> confirmLoreList = Arrays.stream(confirmLore).map(s -> FamiUtils.replace(s, placeholders)).toList();
        ItemStack confirmItem = FamiUtils.makeItem(Material.EMERALD_BLOCK, RPUniverse.getLanguageHandler().rentPropertyMenuConfirmRentItemDisplayName,
                confirmLoreList.toArray(new String[0]));

        // Cancel Item
        ItemStack cancelItem = FamiUtils.makeItem(Material.BARRIER, RPUniverse.getLanguageHandler().rentPropertyMenuCancelItemDisplayName,
                RPUniverse.getLanguageHandler().rentPropertyMenuCancelItemLore.split("~"));

        inventory.setItem(22, confirmItem);
        inventory.setItem(26, cancelItem);
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        return new ArrayList<>();
    }
}
