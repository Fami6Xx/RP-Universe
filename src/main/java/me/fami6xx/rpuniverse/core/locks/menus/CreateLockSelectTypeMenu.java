package me.fami6xx.rpuniverse.core.locks.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.IChatExecuteQueue;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;

public class CreateLockSelectTypeMenu extends Menu {
    private Block block;

    public CreateLockSelectTypeMenu(PlayerMenu menu, Block block) {
        super(menu);
        this.block = block;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().createLockSelectTypeMenuName);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        int slot = e.getSlot();

        if(slot == 3) {
            CreateLockSelectJobMenu createLockSelectJobMenu = new CreateLockSelectJobMenu(playerMenu, block);
            createLockSelectJobMenu.open();
        }

        if(slot == 5) {
            FamiUtils.sendMessageWithPrefix(((Player) e.getWhoClicked()), RPUniverse.getLanguageHandler().createLockTypePlayerNames);
            FamiUtils.sendMessageWithPrefix(((Player) e.getWhoClicked()), RPUniverse.getLanguageHandler().cancelActivityMessage);

            e.getWhoClicked().closeInventory();

            RPUniverse.getInstance().getUniversalChatHandler().addToQueue(((Player) e.getWhoClicked()), new IChatExecuteQueue() {

                @Override
                public boolean execute(Player player, String message) {
                    if (message.equalsIgnoreCase("cancel")) {
                        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createLockCanceled);
                        return true;
                    }

                    List<String> playerNames = Arrays.asList(message.split(","));
                    List<String> playerUUIDs = playerNames.stream()
                        .map(name -> Bukkit.getOfflinePlayer(name.trim()).getUniqueId().toString())
                        .collect(Collectors.toList());
    
                    if (playerUUIDs.isEmpty()) {
                        FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().createLockNoPlayersFound);
                        return true;
                    }
    
                    RPUniverse.getInstance().getLockHandler().createLock(block.getLocation(), block.getType(), playerUUIDs, null, 0);
                    FamiUtils.sendMessageWithPrefix(player, RPUniverse.getLanguageHandler().lockCreationSuccess);
                    return true;
                }

            });
        }
        
        if(slot == 7) {
            RPUniverse.getInstance().getLockHandler().createLock(block.getLocation(), block.getType(), null, null, 0);
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(3, FamiUtils.makeItem(Material.DIAMOND_SWORD, 
            RPUniverse.getLanguageHandler().createLockSelectTypeMenuJobLockDisplayName, 
            RPUniverse.getLanguageHandler().createLockSelectTypeMenuJobLockLore));
        inventory.setItem(5, FamiUtils.makeItem(Material.PLAYER_HEAD, 
            RPUniverse.getLanguageHandler().createLockSelectTypeMenuPlayerLockDisplayName, 
            RPUniverse.getLanguageHandler().createLockSelectTypeMenuPlayerLockLore));
        inventory.setItem(7, FamiUtils.makeItem(Material.BARRIER, 
            RPUniverse.getLanguageHandler().createLockSelectTypeMenuUnopenableDisplayName, 
            RPUniverse.getLanguageHandler().createLockSelectTypeMenuUnopenableLore));
    
        setFillerGlass();
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.ADMIN);
        return tags;
    }
    
}
