package me.fami6xx.rpuniverse.core.jobs.commands.jobs.menus;

import me.fami6xx.rpuniverse.RPUniverse;
import me.fami6xx.rpuniverse.core.jobs.Job;
import me.fami6xx.rpuniverse.core.jobs.Position;
import me.fami6xx.rpuniverse.core.menuapi.types.EasyPaginatedMenu;
import me.fami6xx.rpuniverse.core.menuapi.utils.MenuTag;
import me.fami6xx.rpuniverse.core.menuapi.utils.PlayerMenu;
import me.fami6xx.rpuniverse.core.misc.chatapi.UniversalChatHandler;
import me.fami6xx.rpuniverse.core.misc.utils.FamiUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JobAllPositionsMenu extends EasyPaginatedMenu {
    private final Job job;
    private final boolean adminMenu;
    private final List<Position> positionList;
    public JobAllPositionsMenu(PlayerMenu menu, Job job, boolean adminMenu) {
        super(menu);
        this.job = job;
        this.adminMenu = adminMenu;
        this.positionList = job.getPositions();
    }

    private HashMap<String, String> getPlaceHolders(Position position){
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("{positionName}", position.getName());
        placeholders.put("{positionSalary}", String.valueOf(position.getSalary()));
        placeholders.put("{positionWorkingPermissionLevel}", String.valueOf(position.getWorkingStepPermissionLevel()));
        placeholders.put("{jobName}", job.getName());
        placeholders.put("{positionIsBoss}", position.isBoss() ? "Yes" : "No");
        return placeholders;
    }

    @Override
    public ItemStack getItemFromIndex(int index) {
        Position position = positionList.get(index);

        HashMap<String, String> placeholders = getPlaceHolders(position);
        String displayName = FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAllPositionsMenuPositionItemDisplayName, placeholders);
        String lore = FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAllPositionsMenuPositionItemLore, placeholders);

        return FamiUtils.makeItem(Material.PAPER, displayName, lore);
    }

    @Override
    public int getCollectionSize() {
        return positionList.size();
    }

    @Override
    public void handlePaginatedMenu(InventoryClickEvent e) {
        if(e.getSlot() == 45){
            UniversalChatHandler universalChatHandler = RPUniverse.getInstance().getUniversalChatHandler();
            if(!universalChatHandler.canAddToQueue(playerMenu.getPlayer())){
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorYouAlreadyHaveSomethingToType);
                return;
            }

            playerMenu.getPlayer().closeInventory(InventoryCloseEvent.Reason.PLUGIN);

            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().addPositionTypePositionNameMessage);
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().cancelActivityMessage);

            universalChatHandler.addToQueue(playerMenu.getPlayer(), (player, message) -> {
                if(message.equalsIgnoreCase("cancel")){
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().cancelPositionCreationMessage));
                    return true;
                }

                if(message.length() > 16){
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPositionNameTooLongMessage));
                    return false;
                }

                if(job.getPositionByName(message) != null){
                    player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPositionNameAlreadyExistsMessage));
                    return false;
                }

                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().addPositionTypePositionSalaryMessage);
                universalChatHandler.addToQueue(playerMenu.getPlayer(), (player1, message1) -> {
                    if(message1.equalsIgnoreCase("cancel")){
                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().cancelPositionCreationMessage));
                        return true;
                    }

                    try{
                        int salary = Integer.parseInt(message1);
                        if(salary < 0){
                            player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPositionSalaryTooLowMessage));
                            return false;
                        }

                        FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().addPositionShouldBeBossMessage);
                        universalChatHandler.addToQueue(playerMenu.getPlayer(), (player2, message2) -> {
                            if(message2.equalsIgnoreCase("cancel")){
                                player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().cancelPositionCreationMessage));
                                return true;
                            }

                            if(message2.equalsIgnoreCase("yes") || message2.equalsIgnoreCase("true")){
                                job.addPosition(new Position(message, salary, 0, true, false));
                                player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().addPositionSuccessMessage));
                                this.open();
                                return true;
                            }else if(message2.equalsIgnoreCase("no") || message2.equalsIgnoreCase("false")){
                                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().addPositionShouldBeWorkingPermissionLevelMessage);
                                universalChatHandler.addToQueue(playerMenu.getPlayer(), (player3, message3) -> {
                                    if(message3.equalsIgnoreCase("cancel")){
                                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().cancelPositionCreationMessage));
                                        return true;
                                    }

                                    try{
                                        int workingPermissionLevel = Integer.parseInt(message3);
                                        if(workingPermissionLevel < 0){
                                            player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPositionWorkingPermissionLevelTooLowMessage));
                                            return false;
                                        }

                                        job.addPosition(new Position(message, salary, workingPermissionLevel, false, false));
                                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().addPositionSuccessMessage));
                                        this.open();

                                        return true;
                                    }catch (NumberFormatException exception){
                                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPositionWorkingPermissionLevelNotANumberMessage));
                                        return false;
                                    }
                                });

                                return false;
                            }

                            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().errorYesOrNoMessage);
                            return false;
                        });

                        return false;
                    }catch (NumberFormatException exception){
                        player.sendMessage(FamiUtils.formatWithPrefix(RPUniverse.getLanguageHandler().errorPositionSalaryNotANumberMessage));
                        return false;
                    }
                });

                return false;
            });

            return;
        }

        Position position = positionList.stream()
                .filter(position1 -> {
                    HashMap<String, String> placeholders = getPlaceHolders(position1);
                    String displayName = FamiUtils.replaceAndFormat(RPUniverse.getLanguageHandler().jobAllPositionsMenuPositionItemDisplayName, placeholders);
                    return e.getCurrentItem().getItemMeta().getDisplayName().equals(displayName);
                })
                .findFirst()
                .orElse(null);

        if(position == null){
            return;
        }

        if(e.isShiftClick()){
            if(positionList.indexOf(position) < positionList.indexOf(job.getPlayerPosition(playerMenu.getPlayer().getUniqueId()))){
                FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobAllPositionsMenuCannotMovePositionMessage);
                return;
            }
            if(e.isRightClick()){
                job.movePositionUpAndDown(position.getName(), false);
            }else if(e.isLeftClick()){
                if(positionList.indexOf(position) - 1 < positionList.indexOf(job.getPlayerPosition(playerMenu.getPlayer().getUniqueId()))){
                    FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobAllPositionsMenuCannotMovePositionMessage);
                    return;
                }
                job.movePositionUpAndDown(position.getName(), true);
            }
            return;
        }

        if(positionList.indexOf(position) < positionList.indexOf(job.getPlayerPosition(playerMenu.getPlayer().getUniqueId()))){
            FamiUtils.sendMessageWithPrefix(playerMenu.getPlayer(), RPUniverse.getLanguageHandler().jobAllPositionsMenuCannotEditPositionMessage);
            return;
        }
        new JobPositionMenu(playerMenu, job, position, adminMenu, this).open();
    }

    @Override
    public void addAdditionalItems() {
        super.inventory.setItem(45, FamiUtils.makeItem(Material.EMERALD_BLOCK, RPUniverse.getLanguageHandler().jobAllPositionsMenuAddPositionItemDisplayName, RPUniverse.getLanguageHandler().jobAllPositionsMenuAddPositionItemLore));
        super.inventory.setItem(53, FamiUtils.makeItem(Material.BOOKSHELF, RPUniverse.getLanguageHandler().jobAllPositionsMenuMovePositionsItemDisplayName, RPUniverse.getLanguageHandler().jobAllPositionsMenuMovePositionsItemLore));
    }

    @Override
    public List<MenuTag> getMenuTags() {
        List<MenuTag> tags = new ArrayList<>();
        tags.add(MenuTag.JOB);
        if(adminMenu)
            tags.add(MenuTag.ADMIN);
        else
            tags.add(MenuTag.BOSS);
        tags.add(MenuTag.JOB_POSITION);
        tags.add(MenuTag.JOB_ALL_POSITIONS);
        return tags;
    }

    @Override
    public String getMenuName() {
        return FamiUtils.format(RPUniverse.getLanguageHandler().jobAllPositionsMenuName);
    }
}
