package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.menuapi.types.Menu;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.language.LanguageHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class JobBankActionsMenu extends Menu {
    private final LanguageHandler languageHandler;
    private final UniversalChatHandler universalChatHandler;
    private final Menu previousMenu;
    private final Job job;

    public JobBankActionsMenu(PlayerMenu menu, Menu previousMenu, Job job) {
        super(menu);
        this.languageHandler = RPUniverse.getLanguageHandler();
        this.previousMenu = previousMenu;
        this.universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();
        this.job = job;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(languageHandler.jobBankActionsMenuName);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getSlot() == 3){
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), languageHandler.jobBankActionsMenuAddMoneyMessage);
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), languageHandler.cancelActivityMessage);
            playerMenu.getPlayer().closeInventory();

            universalChatHandler.addToQueue(playerMenu.getPlayer(), (player, message) -> {
                if(message.equalsIgnoreCase("cancel")){
                    this.open();
                    return true;
                }

                try{
                    int amount = Integer.parseInt(message);
                    Economy econ = RPUniverse.getInstance().getEconomy();
                    if(amount > 0 && econ.has(player, amount)){
                        job.addMoneyToJobBank(amount);
                        econ.withdrawPlayer(player, amount);
                        player.sendMessage(FamiUtils.formatWithPrefix(languageHandler.jobBankActionsMenuAddMoneySuccessMessage));
                        this.open();
                        return true;
                    }else{
                        player.sendMessage(FamiUtils.formatWithPrefix(languageHandler.jobBankActionsMenuAddMoneyErrorMessage));
                        this.open();
                        return true;
                    }
                }catch (NumberFormatException exception){
                    player.sendMessage(FamiUtils.formatWithPrefix(languageHandler.jobBankActionsMenuAddMoneyErrorMessage));
                    return false;
                }
            });
        }
        if(e.getSlot() == 5){
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), languageHandler.jobBankActionsMenuRemoveMoneyMessage);
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), languageHandler.cancelActivityMessage);
            playerMenu.getPlayer().closeInventory();

            universalChatHandler.addToQueue(playerMenu.getPlayer(), (player, message) -> {
                if(message.equalsIgnoreCase("cancel")){
                    this.open();
                    return true;
                }

                try{
                    int amount = Integer.parseInt(message);
                    Economy econ = RPUniverse.getInstance().getEconomy();
                    if(amount > 0 && job.getCurrentMoneyInJobBank() >= amount){
                        job.removeMoneyFromJobBank(amount);
                        econ.depositPlayer(player, amount);
                        player.sendMessage(FamiUtils.formatWithPrefix(languageHandler.jobBankActionsMenuRemoveMoneySuccessMessage));
                        this.open();
                        return true;
                    }else{
                        player.sendMessage(FamiUtils.formatWithPrefix(languageHandler.jobBankActionsMenuRemoveMoneyErrorMessage));
                        this.open();
                        return true;
                    }
                }catch (NumberFormatException exception){
                    player.sendMessage(FamiUtils.formatWithPrefix(languageHandler.jobBankActionsMenuRemoveMoneyErrorMessage));
                    return false;
                }
            });
        }
        if(e.getSlot() == 8){
            previousMenu.open();
        }
    }

    @Override
    public void setMenuItems() {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{jobName}", job.getName());
        placeholders.put("{jobMoney}", String.valueOf(job.getCurrentMoneyInJobBank()));

        inventory.setItem(3, makeItem(Material.EMERALD_BLOCK, FamiUtils.format(languageHandler.jobBankActionsMenuAddMoneyItemDisplayName), FamiUtils.format(languageHandler.jobBankActionsMenuAddMoneyItemLore)));
        inventory.setItem(4, makeItem(Material.GOLD_BLOCK, FamiUtils.replaceAndFormat(languageHandler.jobBankActionsMenuCurrentMoneyItemDisplayName, placeholders), FamiUtils.replaceAndFormat(languageHandler.jobBankActionsMenuCurrentMoneyItemLore, placeholders)));
        inventory.setItem(5, makeItem(Material.REDSTONE_BLOCK, FamiUtils.format(languageHandler.jobBankActionsMenuRemoveMoneyItemDisplayName), FamiUtils.format(languageHandler.jobBankActionsMenuRemoveMoneyItemLore)));
        inventory.setItem(8, makeItem(Material.BARRIER, FamiUtils.format(languageHandler.generalMenuBackItemDisplayName), FamiUtils.format(languageHandler.generalMenuBackItemLore)));
        setFillerGlass();
    }
}
